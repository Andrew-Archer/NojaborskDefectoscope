/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ru.npptmk.devices.USKUdp.DeviceUSKUdpParam;
import ru.npptmk.devices.USKUdp.DeviceUSKUdpParams;
import ru.npptmk.devices.md8Udp.ParamsMD8Udp;
import ru.npptmk.guiObjects.SamopPnlDfParams;
import ru.npptmk.guiObjects.SamopPnlTLSParams;
import ru.npptmk.guiObjects.TubeGrphParams;
import ru.npptmk.sortoscope.model.Diameter;

/**
 * Данные, которые необходимо сохранить в базе
 *
 * @author SmorkalovAV
 */
public class UEParams implements Serializable {

    /**
     * @return the conTr
     */
    public String getConTr() {
        return conTr;
    }

    /**
     * @param conTr the conTr to set
     */
    public void setConTr(String conTr) {
        this.conTr = conTr;
    }
    public final static long serialVersionUID = 8825850776638315854L;

    /**
     * Параметры отображения панелей для магнитки
     */
    public HashMap<String, List<Serializable>> prGrfMd = new HashMap<>();
    
    /**
     * Минимальная длина годного участка мм
     */
    public int minGoodLength = 6000;
    /**
     * Проверка минимальной длины участка отключена
     */
    public boolean disableMinGoodLengthCheck = false;
    /**
     * Имя набора графиков для вкладки труба.
     */
    public String currAllGrSet;
    /**
     * Параметры отображения панелей для вкладки трубы
     */
    public HashMap<String, List<Serializable>> prGrfTube = new HashMap<>();
    /**
     * Имя набора графиков для вкладки МД.
     */
    public String currMDGrSet;
    /**
     * Параметры отображения панелей для УЗК
     */
    public HashMap<String, List<Serializable>> prGrfUSK = new HashMap<>();
    /**
     * Имя набора графиков для вкладкт УКЗ
     */
    public String currUSGrSet;
    /*
     * Длина калибровочного участка.
     */
    public short calibrLen = 1130;     // 
    /**
     * Координата начала магнитной дефектоскопии.
     */
    public short MDStart;       // 
    /**
     * Координата начала опускания второго лира.
     */
    public short secLirStart = 2200;   // 
    /**
     * Координата начала УЗД
     */
    public short USDStart = 3500;      // 
    /**
     * Координата опускания третьего лира.
     */
    public short thdLirStart = 4400;   // 
    /**
     * Расстояние окончания магнитной дефектоскопии
     */
    public short MDEnd = 700;         // 
    /**
     * Расстояние окончания УЗД
     */
    public short USDEnd = 2730;        // 
    /**
     * Расстояние от датчика подъема 1 лира до начала ск.
     */
    public short tailLen = 1640;       // 

    /**
     * Время заполнения каретки водой в сек.
     */
    public short fillTime = 8;
    /**
     * Интервал сохранения результатов исследования сечения, мм.
     */
    public short intSave = 10;
    /**
     * Режим работы линии:
     * <ul>
     * <li> 0 - Дефектоскопия труб.
     * <li> 1 - Дефектоскопия заготовок.
     * <li> 2 - Подача заготовок.
     * </ul>
     */
    public short mode;
    /**
     * Флаг использования поста инструментального контроля.
     */
    public boolean instrCheck;
    /**
     * Параметры подключения магнитки
     */
    public InetSocketAddress connMDL;       // Локальный сокет
    public InetSocketAddress connMDR;       // Удаленнный сокет
    /**
     * Параметры подключения УЗК 1
     */
    public InetSocketAddress connUSK1L;       // 
    public InetSocketAddress connUSK1R;       // 
    /**
     * Параметры подключения УЗК 2
     */
    public InetSocketAddress connUSK2L;       // 
    public InetSocketAddress connUSK2R;       // 
    /**
     * Параметры подключения транспорта
     */
    private String conTr = "192.168.0.240";       // 
    /**
     * Тип трубы с которым в данный момент работает система
     */
    public TubeType currentTubeType;

    /**
     * Адресс пк на котором JAVA программа. Если не установлен, то становится
     * 127.0.0.1.
     */
    public String conTrLoc = "192.168.0.239";
    public short onWatter = 2500;
    public short offWatter = 2500;
    public short offDry = 2500;
    public short onDry = 2500;

    public UEParams() {
        List<Serializable> grList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            grList.add((Serializable) new SamopPnlDfParams("Канал " + (i + 1), Devicess.ID_MD, i));
        }
        prGrfMd.put("Все", grList);
        currMDGrSet = "Все";
        grList = new ArrayList<>();
        Color[] threeCol = {Color.GREEN, Color.RED, Color.BLUE};
        String[] names = {"Магнитный", "УЗК", "Толщина"};
        long[] dev = {Devicess.ID_R4, Devicess.ID_R4, Devicess.ID_R4};
        int[] chans = {0, 1, 2};
        grList.add((Serializable) new TubeGrphParams(threeCol, names, dev, chans));
        prGrfTube.put("Основной", grList);
        currAllGrSet = "Основной";
        grList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            grList.add((Serializable) new SamopPnlDfParams(MainFrame.getChanName(Devicess.ID_USK1.intValue(), i),
                    Devicess.ID_USK1, i));
        }
        int i = 0;
        for (; i < 4; i++) {
            grList.add((Serializable) new SamopPnlDfParams(MainFrame.getChanName(Devicess.ID_USK2.intValue(), i),
                    Devicess.ID_USK2, i));
        }
        for (; i < 8; i++) {
            grList.add((Serializable) new SamopPnlTLSParams(MainFrame.getChanName(Devicess.ID_USK2.intValue(), i),
                    Devicess.ID_USK2, i));
        }
        prGrfUSK.put("По умолчанию", grList);
        currUSGrSet = "По умолчанию";
        connMDR = new InetSocketAddress("192.168.0.242", 0x80CD);
        connMDL = new InetSocketAddress("192.168.0.239", 2201);
//        connMDL = new InetSocketAddress("192.168.0.104", 2201);
//        connMDL = new InetSocketAddress("192.168.0.124", 2201);
        connUSK1R = new InetSocketAddress("192.168.0.200", 0x60c1);
        connUSK1L = new InetSocketAddress("192.168.0.239", 2202);
//        connUSK1L = new InetSocketAddress("192.168.0.104", 2202);
//        connUSK1L = new InetSocketAddress("192.168.0.124", 2202);
        connUSK2R = new InetSocketAddress("192.168.0.201", 0x60c1);
        connUSK2L = new InetSocketAddress("192.168.0.239", 2203);
//        connUSK2L = new InetSocketAddress("192.168.0.104", 2203);
//        connUSK2L = new InetSocketAddress("192.168.0.124", 2203);
        conTr = "192.168.0.240";
        currentTubeType = new TubeType(1l, "НКТ", 73, 5.5f);

        DeviceUSKUdpParams prs = new DeviceUSKUdpParams("УЗК 1");
        for (DeviceUSKUdpParam p : prs.prms) {
            if (p.getId() <= 5) {
                p.setDefect_type(0);
            } else {
                p.setDefect_type(2);
            }
        }
        currentTubeType.setParamsUSK1(prs);

        prs = new DeviceUSKUdpParams("УЗК 2");
        for (DeviceUSKUdpParam p : prs.prms) {
            if (p.getId() <= 3) {
                p.setDefect_type(2);
            } else {
                p.setHardware_type(DeviceUSKUdpParam.TIME_CONTROL);
                p.resetToDefault();
            }
        }
        currentTubeType.setParamsUSK2(prs);
        currentTubeType.setParamsMD(new ParamsMD8Udp());
        currentTubeType.setParamsSort(new Diameter());
        tubeTypes = new ArrayList<>();
        tubeTypes.add(currentTubeType);
    }

    /**
     * Параметры первого блока УЗК
     *
     * @return
     */
    public DeviceUSKUdpParams getParamUSK1() {
        return currentTubeType.getParamsUSK1();
    }

    /**
     * Параметры первого блока УЗК
     *
     * @return
     */
    public DeviceUSKUdpParams getParamUSK2() {
        return currentTubeType.getParamsUSK2();
    }

    /**
     * Параметры второго блока УЗК
     *
     * @return
     */
    public ParamsMD8Udp getParamMD() {
        return currentTubeType.getParamsMD();
    }

    public UEParams deepClone() {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (UEParams) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }

    }

    /**
     * Параметры электромагнитной дефектоскопии
     *
     * @return
     */
    public Diameter getParamsSort() {
        return currentTubeType.getParamsSort();
    }
    /**
     * Список типов труб.
     */
    public List<TubeType> tubeTypes;
}
