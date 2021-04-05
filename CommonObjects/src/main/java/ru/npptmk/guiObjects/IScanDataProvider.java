/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

/**
 * Базовый интерфейс поставщика данных сканирования.<br>
 * Этот интерфейс является базовым для семейства интерфейсов получения
 * текущих результатов сканирования от драйвера. Все драйверы сканирующих
 * устройств должны реализовать как минимум этот интерфейс, чтобы обеспечить
 * доступ сторонних объектов к результатам сканирования.<br>
 * Данный интерфейс не предоставляет ни каких данных, а только идентификатор
 * устройства.
 * @author MalginAS
 */
public interface IScanDataProvider {
    /**
     * Возвращает идентификатор устройства, по которому вызывающее приложение
     * сможет определить с каким устройством оно имеет дело.
     * @return 
     */
    public long getDeviceId();
}
