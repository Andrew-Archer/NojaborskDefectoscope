/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.devices.USKUdp;

import java.io.Serializable;

/**
 *
 * @author SmorkalovAV
 */
public class DeviceUSKUdpParams implements Serializable {
    public static final long serialVersionUID = 4806640107661539436L;
    public final DeviceUSKUdpParam prms[] = new DeviceUSKUdpParam[8];
    public String name;
    /**величина ослабления попересных каналов дефектоскопа*/
    public float deltaGainDir;
    /**величина ослабления каналов толщинометрии*/
    public float deltaGainThick;
    /**величина ослабления продольных каналов дефектоскопа*/
    public float deltaGainAx;
    /**
     * Создается с инициализацией параметров
     * @param key ключ типа трубы
     * @param nm имя устройства, при инициализации каналов
     * используется "{@code nm} - Канал {@code ch}".
     */
    public DeviceUSKUdpParams(String nm) {
        name = nm;
        for(int i = 0; i < prms.length; i++){
            prms[i] = new DeviceUSKUdpParam();
            prms[i].setName(name + " - Канал " + (i + 1));
            prms[i].setId(i);
        }
    }
   
    public String getName(int i) {
        return prms[i].getName();
    }

}
