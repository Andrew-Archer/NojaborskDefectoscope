/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.Serializable;
import java.util.Date;
import ru.npptmk.devices.USKUdp.DeviceUSKUdp;
import ru.npptmk.devices.md8Udp.DeviceMD8Udp;

/**
 *
 * @author MalginAS
 */
public class BazaDefectResults implements Serializable {

    
    public BazaTubeResult tbRes;
    public BazaMDResult mdRes;
    public BazaUSDResult usk1Res;
    public BazaUSDResult usk2Res;
    public Date dateRes = new Date();
   // public float minThick;

    public BazaDefectResults(MainFrame mf, DeviceMD8Udp md, DeviceUSKUdp usk1, DeviceUSKUdp usk2) {
        tbRes = new BazaTubeResult(mf, 3);
        /*//Ищем дефеты толщины и возвращаем наименьшее значение
        float[] defects = tbRes.getDefects(0);
        float minThick = -100;//-100 чтобы знать, что дефектов нет.
        for (int i = 0; i < defects.length; i++) {
            if (minThick > defects[i]) {
                minThick = defects[i];
            }
        }
        //Если дефектов нет, то
        //Проверяем все каналы толщинометрии на наименьшую толщину
        for (int i = 0; i < usk1.getParams().prms.length; i++){
            //Если это канал дефектоскопии
            if (usk1.getParams().prms[i].getHardware_type() == FLAW_CONTROL){
                //Для хранения значений графика
                float y [] = new float[usk1.getGraficLength(i)];
                //Для хранения координат длины графика
                float x [] = new float[usk1.getGraficLength(i)];
                usk1.getGrafic(i, x, y);
                for (int j = 0; j < y.length; j++){
                    //Если минимальная толщина меньше текущей,
                    //то записваем текущую как минимальную.
                    if (minThick < y[j]){
                        minThick = y[j];
                    }
                }
                usk2.getGrafic(i, x, y);
                for (int j = 0; j < y.length; j++){
                    //Если минимальная толщина меньше текущей,
                    //то записваем текущую как минимальную.
                    if (minThick < y[j]){
                        minThick = y[j];
                    }
                }
            }          
        }
        //Если есть значение минимальной толщины,
        //записывваем его в результат.
        if (minThick != -100){
            this.minThick = minThick;
        }*/
        mdRes = new BazaMDResult(md);
        usk1Res = new BazaUSDResult(usk1);
        usk2Res = new BazaUSDResult(usk2);
    }

    @Override
    public String toString() {
        return "Результаты дефектоскопии от " + String.format("%1$td.%1$tm.%1$ty %1$tH:%1$tM:%1$tS", dateRes);
    }

}
