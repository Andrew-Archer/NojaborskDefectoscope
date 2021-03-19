/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;



/**
 * Этот интерфейс регламентирует взаимодействие различных компонент
 * отображения результатов с контейнером этих компоненнт {@code PanelForGraphics}.<br>
 * Каждый компонент, отображаемый в этом контейнере должен поддерживать
 * этот интерфейс.
 * @author SmorkalovAV
 */
public interface IDrvsDataReader {
    /**
     * Метод вызывается контейнером в момент поступления новых данных от
     * источника данных. Как правило, таким источником является драйвер
     * устройства.<br>
     * В рамках данного метода компонент должен выяснить есть ли у 
     * источника новые данные, требуемые ему для отображения. В случае 
     * наличия таковых получить их из источника и отобразить на себе.
     * @param driver Источник, в котором появились новые данные.
     */
    public void dataForGrphs(IScanDataProvider driver);
}
