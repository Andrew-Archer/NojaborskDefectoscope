/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.transpmanager;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.JOptionPane;

/**
 * @author SerpokrylovDV
 */
/**
 * Задачей транспортного менеджера является отражение текущего расположения и
 * перемещения деталей между установками.<br>
 * Всю работу с транзакциями БД с которой работает трансп. менеджер
 * необходибоо синхронизировать, не должно быть более одной в одно и тоже время 
 * открытой транзакции.<br>
 * При создании новой детали необходибо после создания детали закомитить транзакцию,
 * после выполнения методов класса открыть транзакцию, обновить данные детали и
 * подтвердить изменения.<br>
 * Для анализа причин ошибки использовать функцию
 * {@code TranspManager.startManager()}.
 */
public class TranspManager {
    /**
     * Ошибка, нет установки.
     */
    public final static int NO_DEV = 1;
    /**
     * Ошибка сохранения в базу данных.
     */
    public final static int DATA_BASE_EX = 2;
    /**
     * Ошибка создания класса из данных из базы
     */
    public final static int CLASS_EX = 3;
    /**
     * Ошибка при обновлении данных установки
     */
    public final static int REFRECH_DEV_EX = 4;
    /**
     * Ошибка, деталь на установке не найдена.
     */
    public final static int NO_DET = 5;
    /**
     * Ошибка, установка не допускает работу с деталями.
     */
    public final static int DEVICE_BASY = 6;
    /**
     * Ошибка, установке переполнена.
     */
    public final static int DEVICE_FILL = 7;
    /**
     * Смещение для кодов ошибок установок.
     */
    public final static int SPEC_ERRS = 10;

    private EntityManager em = null;

    private final HashMap<Long, Device> hmD = new HashMap<>();

    private EntityTransaction transaction = null;
    private EventEnt evt = null;

    /**
     * Запуск менеджера в работу.
     *
     * @param em подключение к базе данных.
     * @return {@code true} если менеджер успешно запущен. {@code false} если
     * менеджер неудалось запустить, возвращаем код ошибки и описание.
     */
    public boolean startManager(EntityManager em) {
        this.em = em;
        transaction = em.getTransaction();
        evt = new EventEnt(new Date(System.currentTimeMillis()), "Запуск транспортного менеджера.", 0);
        try {
            transaction.begin();
            int re = em.createNamedQuery("remInvalidLocations").executeUpdate();
            transaction.commit();
            transaction.begin();
            em.persist(evt);
            List<DeviceEnt> ld = em.createNamedQuery("getAllDevices").getResultList();
            for (DeviceEnt de : ld) {
                if (!refreshDev(de)) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                    hmD.clear();
                    return false;
                }
            }
            transaction.commit();
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            setErr(TranspManager.DATA_BASE_EX, "Ошибка базы данных при запуске менеджера: "
                    + ex.toString() + " " + ex.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /**
     * Обновление данных установки и создание установки из базы данных, метод
     * используется при откате транзакции.
     *
     * @param id идентификатор установки.
     * @return <li>{@code true} при удачном завершении.
     * <li>{@code false} при неудачном завершении, создается объект ошибки.
     */
    private boolean refreshDev(DeviceEnt dv) {
        if (dv != null) {
            Device dvf = createDev(dv);
            if (dvf != null) {
                if (!dvf.refreshDev(dv)) {
                    return false;
                }
                hmD.put(dv.getIdDev(), dvf);
                return true;
            }
        } else {
            setErr(TranspManager.NO_DEV, "Не переданы данные установки при создании установки №" + dv.getIdDev());
        }
        return false;
    }

    /**
     * Создает установку из класса, переданного в параметрах установки класса
     * {@code DeviceEnt.devCl}.
     *
     * @param pr данные установки.
     * @return <li> При удачном завершении создается объект функционала
     * установки.
     * <li> При неудачном завершении, создается объект ошибки.
     */
    private Device createDev(DeviceEnt pr) {
        try {
            try {
                return (Device) pr.getDevCl().getConstructor(TranspManager.class).newInstance(this);
            } catch (NoSuchMethodException | SecurityException ex) {
                setErr(TranspManager.CLASS_EX, "Ошибка вызова контруктора объекта установки: " + ex.toString()
                        + " " + ex.getLocalizedMessage());
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            setErr(TranspManager.CLASS_EX, "Ошибка загрузки класса объекта установки: " + ex.toString()
                    + " " + ex.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Добавление новой установки.<br>
     *
     * @param idDev идентификатор установки.
     * @param param параметры установки.
     * @param name наименование установки.
     * @param devCl класс установки.
     * @param maxDet максимальное кол-во деталей на установке.
     * @return {@code true} если транзакция прошла успешно. {@code false} если
     * транзакция прошла неудачно, создается объект ошибки с её номером и
     * описанием.
     */
    public boolean addNewDevice(long idDev, Serializable param, String name, Class devCl, int maxDet) {
// Создание объекта установки
        DeviceEnt device = new DeviceEnt(idDev, param, name, devCl, maxDet);
        Device dv = createDev(device);
        if (dv == null) {
            return false;
        }
// try проверяется возможность выполнить запись в бд.
        try {
            transaction.begin();
            em.persist(device);
// put записывает в хешмап ключ и объект установки.
            if (!dv.refreshDev(device)) {
                transaction.rollback();
                return false;
            }
            transaction.commit();
            hmD.put(idDev, dv);
        } //Откат транзакции.
        catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            setErr(TranspManager.DATA_BASE_EX, "При создании установки c id=" + idDev + " ошибка " +
                    ex.toString() + " " + ex.getLocalizedMessage());

            return false;
        }
        return true;
    }

    /**
     * Получение параметров установки.<br>
     *
     * @param idDev идентификатор установки.
     * @return Параметры установки. {@code null} Если параметры установки не
     * получены.
     */
    public Serializable getParam(long idDev) {
        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(TranspManager.NO_DEV, "Запрос параметров установки. Установка id=" + idDev + " не найдена.");
            return null;
        }
        return dev.getParam();
    }

    /**
     * Получение названия установки.<br>
     *
     * @param idDev идентификатор установки.
     * @return Возвращает название установки. Возвращает {@code null} если
     * название установки не получено.
     */
    public String getName(long idDev) {

        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(TranspManager.NO_DEV, "Запрос названия установки. Установка id=" + idDev + " не найдена.");
            return null;
        }
        return dev.getName();
    }
    /**
     * Получение количества деталей на установке.<br>
     *
     * @param idDev идентификатор установки.
     * @return Возвращает количество деталей при удачном завершениии,
     * иначе возвращает -1.
     */
    public int getDetailCont(long idDev) {

        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(TranspManager.NO_DEV, "Запрос количества деталей. Установка id=" + idDev + " не найдена.");
            return -1;
        }
        return dev.getCountDetails();
    }
    /**
     * Получение состояния установки.<br>
     * 
     * @param idDev идентификатор установки.
     * @return {@code true} Установка недоступна, удаление 
     * деталей с установки невозможно.
     */
    public boolean isDeviceBasy(long idDev) {

        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(TranspManager.NO_DEV, "Запрос количества деталей. Установка id=" + idDev + " не найдена.");
            return true;
        }
        return dev.isBusy();
    }

    /**
     * Метод изменения параметров установки.<br>
     *
     * @param idDev идентификатор установки.
     * @param param новые параметры установки.
     * @return null, при удачном завершении,
     * иначе событие в поле {@code EventEnt.codeErr} код ошибки, 
     * а в {@code EventEnt.comment} - ее описание.
     */
    synchronized public EventEnt setParam(long idDev, Serializable param) {
        evt = null;
//Проверяется наличие установки, если установки нет, возвращается false.
        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(TranspManager.NO_DEV, "Изменение параметров установки. Установка №" + idDev + " не найдена.");
            return evt;
        }
//Создаем переменную первоначальных параметров.
        Serializable oldpar;
//Присваеваем новой переменной параметры установки. 
        oldpar = dev.getParam();
//Устанавливаем параметры установке.
        
//Попытка записать данные в бд. 
        try {
//Начало транзакции.
            dev.setParam(param);
            transaction.begin();
//Изменение записи в бд.
            em.merge(dev.ent);
//Завершение текущей транзакции.
            transaction.commit();
        } //Откат транзакции, если выпало исключение. Возвращаем false
        catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
// Восстанавливаем первоначальное значение параметров сущности в бд. Установке передается
// значение старых параметров.
            dev.setParam(oldpar);
            setErr(TranspManager.DATA_BASE_EX, "Изменение параметров установки № " + idDev + ". Исключение:"
                    + ex.toString() + " " + ex.getLocalizedMessage());

        }
        return evt;
    }

    /**
     * Меняем название установки.<br>
     *
     * @param idDev идентификатор установки.
     * @param name имя установки.
     * @return {@code null} при удачном завершении, иначе в 
     * {@code EventEnt.codeErr} - код ошибки, а в {@code EventEnt.comment} - ее описание.
     */
    synchronized public EventEnt setName(long idDev, String name) {
        evt = null;
        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(TranspManager.NO_DEV, "Изменение названия установки. Установка №" + idDev + " не найдена.");
            return evt;
        }
//Создаем переменную первоначального имени.
        String oldname;
//Присваеваем новой переменной параметры установки. 
        oldname = dev.getName();
//Устанавливаем параметры установке.
        try {
            dev.setName(name);
            transaction.begin();
//Изменение записи в бд.
            em.merge(dev.ent);
            transaction.commit();
        } //Откат транзакции.
        catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
//Восстанавление первоначального названия установки сущности в бд.
            dev.setName(oldname);
            setErr(TranspManager.DATA_BASE_EX, "Мзменение названия установки №" + idDev + ". Исключение: "
                    + ex.toString() + " " + ex.getLocalizedMessage());

            return evt;
        }
        return evt;
    }

    /**
     * Устанавливается максимальное число деталей на установке.<br>
     *
     * @param idDev идентификатор установки.
     * @param cou максимальное колическтво деталей.
     * @return {@code null} при удачном завершении, иначе в 
     * {@code EventEnt.codeErr} - код ошибки, а в {@code EventEnt.comment} - ее описание.
     */
    synchronized public EventEnt setMaxDetails(long idDev, int cou) {
        evt = null;
        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(TranspManager.NO_DEV, "Установка максимального числа деталей. Установка №" + idDev + " не найдена.");
            return evt;
        }
        int oldCou = dev.getMaxDet();
        try {
            dev.setMaxDet(cou);
            transaction.begin();
            em.merge(dev.ent);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
//Восстанавление первоначального названия установки сущности в бд.
            dev.setMaxDet(oldCou);
            setErr(TranspManager.DATA_BASE_EX, "Установка максимального числа деталей, сущность №" + idDev + ". Исключение: "
                    + ex.toString() + " " + ex.getLocalizedMessage());

            return evt;
        }
        return evt;
    }

    /**
     * Проверка наличия установки в транспортном менеджере.
     *
     * @param idDev Идентификатор установки.
     * @return идентификатор установки.
     */
    public boolean isDevPresent(long idDev) {

        return hmD.containsKey(idDev);
    }

    /**
     * Метод getError вызывается прикладной программой.
     *
     * @return возвращает объект ошибки (номер и строковое описание).
     */
    public EventEnt getError() {
        return evt;
    }

    /**
     * Устанавливает ошибку.<br>
     * Метод установит ошибку только если прежде она не была установлена.
     * Это сделано, чтобы из серии ошибок сохранилась первая.
     * @param cod Код ошибки.
     * @param desc Описание ошибки.
     */
    public void setErr(int cod, String desc) {
        if(evt == null){
            evt = new EventEnt(cod, desc);
        } else if(evt.codeErr == 0){
            evt.codeErr = cod;
            evt.setComment(desc);
        }
    }

    /**
     * Метод добавления новой детали на установку.<br>
     * Предлагается следующий алгоритм использования этого метода:<ul>
     * <li> Начать транзакцию.
     * <li> Создать деталь.
     * <li> Завершить транзакцию.
     * <li> Вызвать метод добалвения детали на установку.
     * <li> В случае ошибки открыть транзакцию, удалить созданную деталь
     * и закрыть транзакцию.
     * <li> При удачном завершении открыть транзакцию, внести изменения и
     * закрыть транзакцию.</ul>
     * @param idDet идентификатор детали, созданной клиентской программой.
     * @param position позиция в которую добавляем новую деталь.
     * @param idDev установка в которую добавляем деталь.
     * @param commt описание события.
     * @return событие, при удачном завершении {@code EventEnt.codeErr} равен 0, иначе в 
     * {@code EventEnt.codeErr} - код ошибки, а в {@code EventEnt.comment} - ее описание.
     */
    synchronized public EventEnt addDet(long idDet, int position, long idDev, String commt) {
        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(TranspManager.NO_DEV, "Добавление детали. Установка №" + idDev + " не найдена.");
            return evt;
        }
        if(dev.getCountDetails() + 1 > dev.getMaxDet()){
            setErr(DEVICE_FILL, "Добавление детали. Установка №" + idDev + " переполнена.");
            return evt;
        }
        evt = new EventEnt(new Date(), commt, idDet);
        LocationEnt posit = new LocationEnt(idDet);
        try {
            transaction.begin();
            em.persist(evt);
            if (!dev.addLocation(posit, position)) {
                transaction.rollback();
                refreshDev(em.find(DeviceEnt.class, idDev));
                return evt;
            }
            transaction.commit();
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            refreshDev(em.find(DeviceEnt.class, idDev));
            setErr(TranspManager.DATA_BASE_EX, "Добавление детали  №" + idDet + " на установку №" + idDev + ". Ошибка: " + ex.toString()
                    + " " + ex.getLocalizedMessage());
        }
        return evt;
    }

    /**
     * Удаление детали с установки.
     *
     * @param position позиция детали.
     * @param idDev идентификатор установки.
     * @param commt комментарий удаления.
     * @return событие, при удачном завершении {@code EventEnt.codeErr} равен 0, иначе в 
     * {@code EventEnt.codeErr} - код ошибки, а в {@code EventEnt.comment} - ее описание.
     */
    synchronized public EventEnt remDet(int position, long idDev, String commt) {
        evt = null;
        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(NO_DEV, "Удаление детали. Установка №" + idDev + " не найдена.");
            return evt;
        }
        if(dev.isBusy()){
            setErr(DEVICE_BASY, "Удаление детали. Установка №" + idDev + " недоступна.");
            return evt;
        }
        long idDet = dev.getIdDet(position);
        if (idDet == 0) {
            return new EventEnt(TranspManager.NO_DET, "Удаление детали. Деталь №" + idDet + " на установке №"
                    + idDev + " в позиции " + position + " не найдена.");
        }
        evt = new EventEnt(new Date(), commt, idDet);
        try {

            transaction.begin();
            em.persist(evt);
            RelocationEnt relo = new RelocationEnt(evt.getId(), idDet, position, 0, idDev, 0);
            em.persist(relo);
            LocationEnt lo = dev.remLocation(position);
            if (lo == null) {
                transaction.rollback();
                refreshDev(em.find(DeviceEnt.class, idDev));
                return evt;
            }
            em.remove(lo);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            refreshDev(em.find(DeviceEnt.class, idDev));
            return new EventEnt(TranspManager.DATA_BASE_EX, "Удаление детали №" + idDet + " c установки №" +
                    idDev + ". Ошибка: " + ex.toString() + " " + ex.getLocalizedMessage());
        }
        return evt;
    }

    /**
     * Метод перемещения детали на новую позицию.
     *
     * @param oldPos позиция установки с которой перемещается деталь.
     * @param newPos позиция установки на которую перемещается деталь.
     * @param oldDev установка с которой перемещается деталь.
     * @param newDev установка на которую перемещается деталь.
     * @param commt Описание события.
     * @return событие, при удачном завершении {@code EventEnt.codeErr} равен 0, иначе в 
     * {@code EventEnt.codeErr} - код ошибки, а в {@code EventEnt.comment} - ее описание.
     */
    synchronized public EventEnt relocateDet(int oldPos, int newPos, long oldDev, long newDev, String commt) {
        evt = null;
        Device devOld = hmD.get(oldDev);
        if (devOld == null) {
            setErr(TranspManager.NO_DEV, "Перемещения детали. Установка №" + oldDev + " не найдена.");
            return evt;
        }
        Device devNew = hmD.get(newDev);
        if (devNew == null) {
            setErr(TranspManager.NO_DEV, "Перемещение детали. Установка №" + newDev + " не найдена.");
            return evt;
        }
        long idDet = devOld.getIdDet(oldPos);
        if (idDet == 0) {
            setErr(TranspManager.NO_DET, "Перемещение детали. Деталь №" + idDet + " не найдена на установке №"
                    + oldDev + " в позиции " + oldPos + ".");
            return evt;
        }
        evt = new EventEnt(new Date(), commt, idDet);
        if(devOld.isBusy()) {
            setErr(TranspManager.DEVICE_BASY, "Перемещения детали. Установка №" + oldDev + " недоступна.");
            return evt;
        }

        if(devNew.getCountDetails() + 1 > devNew.getMaxDet()) {

            setErr(TranspManager.DEVICE_FILL, "Перемещения детали. Установка№ " + newDev + " переполнена.");
            return evt;
        }
        try {
            transaction.begin();
            em.persist(evt);
            LocationEnt lo = devOld.remLocation(oldPos);
            if (lo == null) {
                transaction.rollback();
                refreshDev(em.find(DeviceEnt.class, oldDev));
                return evt;
            }
            if (!devNew.addLocation(lo, newPos)) {
                transaction.rollback();
                refreshDev(em.find(DeviceEnt.class, newDev));
                refreshDev(em.find(DeviceEnt.class, oldDev));
                return evt;
            }
            transaction.commit();
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            refreshDev(em.find(DeviceEnt.class, newDev));
            refreshDev(em.find(DeviceEnt.class, oldDev));
            setErr(TranspManager.DATA_BASE_EX, "Перемещение детали №" + idDet + " c установки №"
                    + oldDev + " на установку " + newDev + ". Ошибка: " + ex.toString()
                    + " " + ex.getLocalizedMessage());
        }
        return evt;
    }

    /**
     * Обновление размещения детали в базе данных. <br>
     * В процессе работы функция заносит в переданное расположение новые
     * значения позиции и идентификаторы установки, при этом в таблицу
     * {@code RelocatinEnt} заносится факт изменения. Таблица
     * {@code LocationEnt} будет обновлена в соответствии с новым расположением.
     * Если поле {@code oldLoc.idDev} содержит ноль, то функция создает новое
     * расположение.<br>
     * Функция предназначена для вызова из классов наследующих класс
     * {@code Device} в процессе реализации абстрактных методов.<br>
     * Перед вызовом функции поле {@code TranspManager.evt} должно содержать
     * адрес корректного объекта класса {@code EventEnt} и должна быть открыта
     * транзакция. В случае неудачного завершения транзакция должна быть
     * отменена.
     *
     * @param oldLoc старое расположение
     * @param newPos новая позиция
     * @param newDev новая установка
     * @return false Если возникла ошибка, {@code TranspManager.getError()} 
     * возвращает код ошибки с описанием.
     */
    public boolean updateLocation(LocationEnt oldLoc, int newPos, long newDev) {
        RelocationEnt re = new RelocationEnt(evt.getId(), oldLoc.getIdDet(), oldLoc.getPosition(),
                newPos, oldLoc.getIdDev(), newDev);
        long oldDv = oldLoc.getIdDev();
        oldLoc.setIdDev(newDev);
        oldLoc.setPosition(newPos);
        try {
            if (oldDv == 0) {
                em.persist(oldLoc);
            } else {
                em.merge(oldLoc);
            }
            em.persist(re);
        } catch (Exception ex) {
            setErr(TranspManager.DATA_BASE_EX, "Обновление положения: прежняя позиция " + oldLoc + ", новая позция "
                    + newPos + ", установка №" + newDev + ". Ощибка: "
                    + ex.toString() + " " + ex.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /**
     * Возвращает расположения в базе данных на заданных установках.<br>
     * В качестве параметра передается список идентификаторов установок, если
     * передан null, то метод вернет размещения на всех установках.<br>
     * Возвращаемый список будет отсортирован по двум ключам: идентификатор
     * установки и позиция. Указанный порядок изменить нельзя. Направление
     * сортировки задается в параметрах.
     *
     * @param ls список идентификаторов установок, может быть null.
     * @param unitOrder сортировка по идентификатору установок: true -
     * возрастание, false - убывание.
     * @param posOrder сортировка по позиции на установке: true - возрастание,
     * false - убывание.
     * @return список размещений на указанных установках.
     */
    public List<LocationEnt> getLocations(List<Long> ls, boolean unitOrder, boolean posOrder) {
        if (ls == null || ls.isEmpty()) {
            if (unitOrder && posOrder) {
                return em.createNamedQuery("getAllLocationsAscAsc").getResultList();
            }
            if (!unitOrder && posOrder) {
                return em.createNamedQuery("getAllLocationsDescAsc").getResultList();
            }
            if (unitOrder && !posOrder) {
                return em.createNamedQuery("getAllLocationsAscDesc").getResultList();
            }
            return em.createNamedQuery("getAllLocationsDevDescDesc").getResultList();
        }
        if (unitOrder && posOrder) {
            return em.createNamedQuery("getLocationsDevAscAsc").setParameter("dev", ls).getResultList();
        }
        if (!unitOrder && posOrder) {
            return em.createNamedQuery("getLocationsDevDescAsc").setParameter("dev", ls).getResultList();
        }
        if (unitOrder && !posOrder) {
            return em.createNamedQuery("getLocationsDevAscDesc").setParameter("dev", ls).getResultList();
        }
        return em.createNamedQuery("getLocationsDevDescDesc").setParameter("dev", ls).getResultList();
    }

    /**
     * Возвращает коллекцию в которой в качестве ключа выступает идентификатор
     * установки, а в качестве значения - наименование установки.
     *
     * @return коллекцию имен установок.
     */
    public HashMap<Long, String> getDevNames() {
        HashMap<Long, String> hm = null;
        if (hmD != null && !hmD.isEmpty()) {
            hm = new HashMap<>();
            for (Device d : hmD.values()) {
                hm.put(d.getIdDev(), d.getName());
            }
        }
        return hm;
    }

    /**
     * Удаление всех деталей с установки.
     *
     * @param idDev идентификатор установки.
     * @param commt комментарий удаления.
     * @return событие, при удачном завершении {@code EventEnt.codeErr} равен 0, иначе в 
     * {@code EventEnt.codeErr} - код ошибки, а в {@code EventEnt.comment} - ее описание.
     */
    synchronized public EventEnt clearAllLoc(long idDev, String commt) {
        // Создаем новое событие.
        LocationEnt locE;
        // Получаем коллекцию установок, проверяем есть ли в ней установка, если нет - возвращаем ошибку.
        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(TranspManager.NO_DEV, "Удаление всех деталей. Установка №" + idDev + " не найдена.");
            return null;
        }
        if (dev.isBusy()) {
            setErr(TranspManager.DEVICE_BASY, "Удаление всех деталей. Установка №" + idDev + " недоступна.");
            return null;
        }
        try {
            // Открываем транзакцию.
            transaction.begin();
            // Метод Удаления деталей с установки.
            List<LocationEnt> loc = dev.clearAllLoc();
            // Откатываем транзакцию.
            if (loc == null) {
                transaction.rollback();
                refreshDev(em.find(DeviceEnt.class, idDev));
                return null;
            }
            evt = new EventEnt(new Date(), commt, 0);
            // Записывам событие в базу.
            em.persist(evt);
            for (int i = 0; i < loc.size(); i++) {
                locE = loc.get(i);
                RelocationEnt re = new RelocationEnt(evt.getId(), locE.getIdDet(), locE.getPosition(), 0, idDev, 0);
                em.remove(locE);
                em.persist(re);
                evt.getRelocations().add(re);//Для передачи вызвающей функции
            }
            transaction.commit();
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            refreshDev(em.find(DeviceEnt.class, idDev));
            setErr(TranspManager.DATA_BASE_EX, "Удалении всех деталей. Установка №" + idDev + ". Ошибка: " + ex.toString()
                    + " " + ex.getLocalizedMessage());
        }
        return evt;
    }
    /**
     * Метод показывает пользователю блокирующий диалог со списком
     * установок из который позователь может выбрать необходимую.
     * @param dv Список идентификаторов допустимых установок.
     * @return null - если польователь отменил действие,
     * иначе идентификатор выбранной установки.
     */
    public Long getSelectDeviceID(Set<Long> dv) {
        Object[] arr;
        if(dv == null){
            arr = hmD.values().toArray();
        } else {
            arr = new Object[dv.size()];
            Iterator<Long> it = dv.iterator();
            int ind = 0; 
            Device dev;
            while(it.hasNext()){
                dev = hmD.get(it.next());
                if(dev != null){
                    arr[ind++] = dev;
                }
            }
        }
        Device ret = (Device) JOptionPane.showInputDialog(null, "Выберете установку", 
                "Запрос", JOptionPane.QUESTION_MESSAGE, null, arr, arr[0]);
        if(ret == null){
            return null;
        } else {
            return ret.getIdDev();
        }
    }
    /**
     * Возвращает идентификатор детали на указанной установке в
     * указаной позиции, если в качестве позиции указан {@code Devise.DEFAULT_POSITION}.
     * @param idDev идентификатор установки.
     * @param position позиция детали.
     * @return 0 если не удалось найти, причину можно узнать методом {@code TranspManagetr.getError},
     * иначе идентификатор детали.
     */
    public long getDetail(long idDev, int position) {
        Device dev = hmD.get(idDev);
        if (dev == null) {
            setErr(NO_DEV, "Удаление детали. Установка №" + idDev + " не найдена.");
            return 0;
        }
        if(dev.isBusy()){
            setErr(DEVICE_BASY, "Удаление детали. Установка №" + idDev + " недоступна.");
            return 0;
        }
        return dev.getIdDet(position);
    }

    public List<LocationEnt> getLocations(long idDev) {
        return em.createNamedQuery("getDeviceLocations").setParameter("dev", idDev).getResultList();
    }

}
