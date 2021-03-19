/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.devices.md8Udp;

import java.io.IOException;
import java.io.Serializable;
import ru.npptmk.commonObjects.RevDataOutputStream;

/**
 *
 * @author SmorkalovAV
 */
public class ParamsMD8Udp implements Serializable {

    /**
     * усиление по каналам 
     */
    public int[] gain = new int[8];
    /**
     * значение порогов по каналам
     */
    public int[] porog = new int[8];
    /**
     * сдвиг координаты канала, мм
     */
    public int[] offset = new int[8];
    /**
     * значение фильтра
     */
    public short filtr;
    /**
     * Создаются параметры с усилением во всех каналах 20,
     * порогом 50, и смещением каналов по оси трубы на четных каналах - 230 мм,
     * а на нечетных - 290. Фильтр 0;
     * @param paramKey инициализированные параметры.
     */
    public ParamsMD8Udp() {
        for(int i=0;i<8;i++){
            gain[i] = 20;
            porog[i] = 50;
        }
    }

    /**масштаб перевода значений графика к значениям порога,
     цифра определена визуально по блоку Сапфир*/
    public static final float msY = 0.048828125f;
    /**
     * преобразует класс в набор байтов для работы с контроллером магнитки 
     * @return массив байт для отправки контроллеру
     * @throws java.io.IOException
     */
    public byte [] GetValue() throws IOException{
        RevDataOutputStream os = new RevDataOutputStream();
        for(int po = 0;po < gain.length;po++)
            os.writeRevShort((short) gain[po]);
        os.writeRevShort(filtr); 
        return os.getBuff();
    }
    
    public boolean isEnabled(int ch) {
        return (true);
    }

}
