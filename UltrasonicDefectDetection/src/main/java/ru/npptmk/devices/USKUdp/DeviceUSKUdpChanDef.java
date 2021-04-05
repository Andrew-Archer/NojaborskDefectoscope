/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.devices.USKUdp;

/**
 * Класс описателя канала.<br>
 * Используется для построения списка доступных каналов и привязки их к
 * драйверам. Это необходимо, так как панельнастройки может использоваться с
 * несколькими драйверами одновременно. Например, если установка содержит
 * несколько модклей УЗК.
 *
 * @author MalginAS
 */
public class DeviceUSKUdpChanDef {

    /**
     * Обозначение канала для вывода в списке каналов на панели.
     */
    public String chanDef;
    /**
     * Драйвер, которому принадлежит канал.
     */
    public DeviceUSKUdp driver;
    /**
     * Индекс канала в драйвере.
     */
    public int chanIndex;
    /**
     * Создает описатель канала для панели настройки параметров УЗК.
     * @param chanDef Обозначение канала для вывода в списке каналов на панели.
     * @param driver Драйвер, которому принадлежит канал.
     * @param chanIndex Индекс канала в драйвере.
     */
    public DeviceUSKUdpChanDef(String chanDef, DeviceUSKUdp driver, int chanIndex) {
        this.chanDef = chanDef;
        this.driver = driver;
        this.chanIndex = chanIndex;
    }

    @Override
    public String toString() {
        return chanDef; 
    }

}
