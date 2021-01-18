/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.view;

import java.util.List;

/**
 * Позволяет управлять визуальным отображением элементов устройства.
 *
 * @author RazumnovAA
 * @param <T> Класс или интерфейс - модель положения.
 */
public interface DeviceElement<T> {

    /**
     * Врзваращет одно из возможных положений элемента.
     *
     * @param positionNumber номер положения элемента, который необходимо
     * вернуть.
     * @return Положение элемента соответствующее заданому номеру. Если по
     * номеру не найдено положений, то возвращает <tt>null</tt>.
     */
    public T getPosition(int positionNumber) throws IndexOutOfBoundsException;

    /**
     * Заменяет существующее положение элемента. Если номер положения элемента
     * который необходимо заменить отсутствует, то метод выбрасывает исключение.
     *
     * @param newPosition Новое положение элемента.
     * @param positionNumber Номер положения элемента, которое необходимо
     * заменить на новое.
     * @throws java.lang.Exception Соедржит информацию о том скаким индексом
     * положения не существует у данного элемента.
     */
    public void setPosition(T newPosition, int positionNumber) throws Exception;

    /**
     * Пытается вернуть положение механизма поумолчанию. Если положение по
     * умолчаниб null, то выбрасывает исключение.
     *
     * @return значение механизма по умолчанию.
     * @throws java.lang.Exception Предоставляет информацию о том существует ли
     * список положений из которого можно выбрать положение заданное по
     * умолчанию.
     */
    public T getDefaultPosition() throws Exception;

    /**
     * Устанавливает положение элемента по умолчанию. Если такого положения нет
     * в списке положений элемента, то добавляет новое положение в списко
     * положений элемента.
     *
     * @param newDefaultPosition Положение которое будет установлено как
     * положение по умолчанию.
     */
    public void setDefaultPosition(T newDefaultPosition);

    /**
     * Устанавливает положением по умолчанию положение из существующего списка
     * положений. Если в существующем списке положений такого индекса нет, то
     * выбрасывает исключение.
     *
     * @param newDefaultPositionIndex Индекс в текущем списке положений для
     * задания положением по умолчанию.
     * @throws java.lang.Exception Содержить список возможныих индексов
     * положений.
     */
    public void setDefaultPosition(int newDefaultPositionIndex) throws Exception;

    /**
     * Возвращает список возможных положений элемента.
     *
     * @return Список возможных положений, может быть пустым.
     */
    public List<T> getAllPositions();

    /**
     * Заменяет список существующих положений на новый.
     *
     * @param newPositionsList новый список положений.
     */
    public void setAllPositions(List<T> newPositionsList);

    /**
     * Добавляет новое положение в конец списка существующих положений. Нет
     * проверк на уникальность положений.
     *
     * @param positionToAdd Положение для добавления.
     * @return Длина текущего списка положений.
     */
    public int addPosition(T positionToAdd);

    /**
     * Добавляет новые положения в конец существующего спска.
     *
     * @param positionsToAdd Положения для добавления в конец текущего списка
     * положений.
     * @return Новавя длина текущего списка положений.
     */
    public int addAllPositions(List<T> positionsToAdd);

    /**
     * Удаляет положение из текущего списка положений по заданному индексу.
     *
     * @param positionToRemoveIndex Индекс положения для удаления из текущего
     * списка положений.
     * @return Новая длина списка положений.
     * @throws java.lang.Exception Текущая длина списка положений.
     */
    public int removePosition(int positionToRemoveIndex) throws Exception;

    /**
     * Ищет положение в текущем списке положений и удалет найденое. Если
     * соответствующее положение не найдено, выбрасывает исключение.
     *
     * @param positionToRemove Положение для удаления из списка положений.
     * @return Новая длина списка положений.
     * @throws Exception Возвращает списко положений которые можно удалить.
     */
    public int removePosition(T positionToRemove) throws Exception;

    /**
     * Ищет положения в текущем списке положений и удалет найденый. Если
     * соответствующее положение для удаления не найдено, выбрасывает
     * исключение.
     *
     * @param positionToRemoveIndex
     * @return Новая длина списка положений.
     * @throws Exception Список положений которые можно удалить и списко
     * положений кторые не были найдены в текущем спике положений.
     */
    public int removeAllPositions(List<T> positionsToRemove) throws Exception;

}
