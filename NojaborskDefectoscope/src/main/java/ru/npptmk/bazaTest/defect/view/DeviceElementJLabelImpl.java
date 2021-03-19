/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.view;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

public class DeviceElementJLabelImpl<ElementLabelPosition> implements DeviceElement<ElementLabelPosition> {

    private List<ElementLabelPosition> positions;
    private int defaultPositionIndex;
    private int positionIndex;
    private JLabel view;

    //<editor-fold defaultstate="collapsed" desc="Список конструкторов">
    /**
     * Создает элемент с заданным списком положений и использует заданое по
     * индексу положение в качестве положения по умолчанию. Текущее положение
     * задется равным положению по умолчанию.
     *
     * @param positions Список позвожных положений элемента.
     * @param defaultPositionIndex Индекс положения в списке которое будет
     * установлено как положение поумолчанию.
     */
    public DeviceElementJLabelImpl(
            List<ElementLabelPosition> positions,
            int defaultPositionIndex) {
        this.positions = positions;
        this.defaultPositionIndex = defaultPositionIndex;
        this.positionIndex = this.defaultPositionIndex;
    }

    /**
     * Создает элемент с заданным списком положений и использует 0 положение в
     * качестве положения по умолчанию.
     *
     * @param positions
     */
    public DeviceElementJLabelImpl(List<ElementLabelPosition> positions) {
        this(positions, 0);
    }

    /**
     * Создает элемент с пустым списко положений. Не рекомендуется, так элемент
     * устройства должен иметь хотя бы одно положение.
     */
    public DeviceElementJLabelImpl() {
        this(new ArrayList<ElementLabelPosition>());
    }
    //</editor-fold>

    @Override
    public ElementLabelPosition getPosition(int positionNumber) throws IndexOutOfBoundsException {
        return positions.get(positionNumber);
    }

    @Override
    public void setPosition(ElementLabelPosition newPosition, int positionNumber) throws IndexOutOfBoundsException {
        this.positions.set(positionNumber, newPosition);
    }

    @Override
    public ElementLabelPosition getDefaultPosition() throws Exception {
        return positions.get(defaultPositionIndex);
    }

    @Override
    public void setDefaultPosition(ElementLabelPosition newDefaultPosition) {
        positions.add(newDefaultPosition);
    }

    @Override
    public void setDefaultPosition(int newDefaultPositionIndex) throws IndexOutOfBoundsException {
        isIndexValid(newDefaultPositionIndex);
        this.defaultPositionIndex = newDefaultPositionIndex;

    }

    @Override
    public List<ElementLabelPosition> getAllPositions() {
        return this.positions;
    }

    @Override
    public void setAllPositions(List<ElementLabelPosition> newPositionsList) {
        this.positions = newPositionsList;
    }

    @Override
    public int addPosition(ElementLabelPosition positionToAdd) {
        this.positions.add(positionToAdd);
        return positions.size();
    }

    @Override
    public int addAllPositions(List<ElementLabelPosition> positionsToAdd) {
        this.getAllPositions().addAll(positionsToAdd);
        return this.positionIndex;
    }

    @Override
    public int removePosition(int positionToRemoveIndex) throws IndexOutOfBoundsException {
        isIndexValid(positionToRemoveIndex);
        this.positions.remove(positionToRemoveIndex);
        return this.positions.size();
    }

    @Override
    public int removePosition(ElementLabelPosition positionToRemove) throws Exception {
        this.positions.remove(positionToRemove);
        return this.positions.size();
    }

    @Override
    public int removeAllPositions(List<ElementLabelPosition> positionsToRemove) throws Exception {
        this.positions.removeAll(positionsToRemove);
        return this.positions.size();
    }

    /**
     * Проверяем находится ли указанный индекс в диапазоне 0 - (длина списка -
     * 1).
     *
     * @param indexInPosition Индекс для проверки.
     * @return <tt>false</tt> если индекс недопустимый и <tt>true</tt> в
     * противном случае.
     * @throws IndexOutOfBoundsException
     */
    private boolean isIndexValid(int indexInPosition) throws IndexOutOfBoundsException {
        if (indexInPosition >= 0 && indexInPosition < (positions.size() - 1)) {
            return true;
        } else {
            throw new IndexOutOfBoundsException(String.format(
                    "Возможное значение индекса 0 - %d а, устанавливаемое значение %d",
                    positions.size() - 1, indexInPosition));
        }
    }

}
