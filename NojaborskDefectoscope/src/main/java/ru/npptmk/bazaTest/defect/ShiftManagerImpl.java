/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static ru.npptmk.bazaTest.defect.ShiftManager.ShiftState.SHIFT_IS_RUNNING;

public class ShiftManagerImpl implements ShiftManager {

    private Shift shift;
    private EntityManagerFactory emf;
    private final List<ShiftManagerListener> shiftManagerListeners;

    public ShiftManagerImpl(EntityManagerFactory emf) {
        this.shiftManagerListeners = new ArrayList<>();
        this.emf = emf;

        //Получаем количество незавершенных смен.
        List<Shift> shifts = getAllUnfinishedShifts();
        switch (shifts.size()) {
            case 0: {
                shift = new Shift();
                saveCurrentShift();
                break;
            }
            case 1: {
                shift = shifts.get(0);
                break;
            }
            default: {
                JOptionPane.showMessageDialog(
                        null,
                        "В базе найдено более одной незакрытой смены. "
                        + "Первая незакрытая смена будет установлена как текущая.",
                        "Предупреждение",
                        JOptionPane.WARNING_MESSAGE
                );
                shift = shifts.get(0);
            }
        }
    }

    @Override
    public void addListeners(List<ShiftManagerListener> listeners) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Уведомляет всех слушателей об изменении состоянии контроллируемой
     * менеджером смены.
     */
    public final void notifyAllListenersAboutShiftState() {
        if (getState() == SHIFT_IS_RUNNING) {
            shiftManagerListeners.forEach((listener) -> {
                listener.doThingsOnShiftChaned(shift);
            });
        }
    }

    private void loadShiftsFromDB() {

    }

    /**
     * Позволяет получить все незавершенные смены.
     *
     * @return Список незавершенных смен, если незавершенных смен не найдено, то
     * возвращаем пустой список.
     */
    private List<Shift> getAllUnfinishedShifts() {
        List<Shift> shifts = new ArrayList<>();
        EntityManager em = emf.createEntityManager();
        //Выбираем все незавершенные смены
        try {
            shifts = em.createQuery("SELECT a FROM Shift a WHERE ((a.beginning IS NULL)"
                    + "OR (a.finish IS NULL))")
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return shifts;
    }

    /**
     * Добавляет слушателя изменений состояния смены.
     *
     * @param listener слашатель для добавления в спимок слушателей.
     */
    @Override
    public void addListener(ShiftManagerListener listener) {
        shiftManagerListeners.add(listener);
        if (shift != null) {
            notifyAllListenersAboutShiftState();
        }
    }

    @Override
    public ShiftState getState() {
        if (shift.getBeginning() == null && shift.getFinish() == null) {
            return ShiftManager.ShiftState.SHIFT_IS_NOT_STARTED;
        }

        if (shift.getBeginning() != null && shift.getFinish() == null) {
            return ShiftManager.ShiftState.SHIFT_IS_RUNNING;
        }

        if (shift.getBeginning() != null && shift.getFinish() != null) {
            return ShiftManager.ShiftState.SHIFT_IS_FINISHED;
        }

        return ShiftManager.ShiftState.SHIFT_IS_IN_A_WRONG_STATE;

    }

    @Override
    public void removeListener(ShiftManagerListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeListeners(List<ShiftManagerListener> listeners) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void saveCurrentShift() {
        if (shift != null) {
            EntityManager em;
            em = emf.createEntityManager();
            try {
                EntityTransaction trans = em.getTransaction();
                try {
                    trans.begin();
                    shift = em.merge(shift);
                    trans.commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (trans.isActive()) {
                        trans.rollback();
                    }
                }
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE
                );
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Невозможно сохранить смену, так как в менеджере нет ни одной смены.",
                    "Ошибка",
                    ERROR_MESSAGE
            );
        }

    }

    @Override
    public void endShift() throws Exception {
        //Если текущая смена идет.
        switch (getState()) {
            case SHIFT_IS_RUNNING:
                shift.setFinish(new Date());
                saveCurrentShift();
                JOptionPane.showMessageDialog(
                        null,
                        "Смена завершилась.",
                        "Уведомление",
                        JOptionPane.INFORMATION_MESSAGE
                );
                notifyAllListenersAboutShiftState();
                System.out.println(shift.getId());
                break;
            default:
                throw new Exception("Не возможно завершить смену.");
        }
    }

    @Override
    public Shift getShift() {
        return shift;
    }

    private void setShift(Shift shift) {
        this.shift = shift;
    }

    @Override
    public void startShift() throws Exception {
        switch (getState()) {
            case SHIFT_IS_NOT_STARTED:
                //Если оператор смены не установлен
                if (shift.getOperator() == null) {
                    //Устанавлиаем оператора смены
                    DialogOperatorSelection operatorSelectionDialog = new DialogOperatorSelection(emf, null, true);
                    operatorSelectionDialog.setVisible(true);
                    shift.setOperator(operatorSelectionDialog.getOperator());
                }
                //Начинаем смену
                shift.setBeginning(new Date());
                saveCurrentShift();
                JOptionPane.showMessageDialog(
                        null,
                        "Смена началась.",
                        "Уведомление",
                        JOptionPane.INFORMATION_MESSAGE
                );
                notifyAllListenersAboutShiftState();
                break;
            case SHIFT_IS_FINISHED:
                //Сохраняем завершенную смену.
                saveCurrentShift();
                //Создаем новую смену.
                shift = new Shift();
                //Устанавлиаем оператора смены
                DialogOperatorSelection operatorSelectionDialog = new DialogOperatorSelection(emf, null, true);
                operatorSelectionDialog.setVisible(true);
                shift.setOperator(operatorSelectionDialog.getOperator());
                //Начинаем смену.
                shift.setBeginning(new Date());
                saveCurrentShift();
                JOptionPane.showMessageDialog(
                        null,
                        "Смена началась.",
                        "Уведомление",
                        JOptionPane.INFORMATION_MESSAGE
                );
                notifyAllListenersAboutShiftState();
                break;
            default:
                throw new Exception("Не удалось начать смену.");
        }
    }

}
