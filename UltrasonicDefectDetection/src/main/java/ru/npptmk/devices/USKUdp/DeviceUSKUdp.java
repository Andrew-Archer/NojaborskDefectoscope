/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.devices.USKUdp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.npptmk.commonObjects.DeltaIsmerenMinMax;
import ru.npptmk.commonObjects.GeneralUDPDevice;
import ru.npptmk.commonObjects.RevDataInputStream;
import ru.npptmk.commonObjects.RevDataOutputStream;
import ru.npptmk.guiObjects.IDefectScanDataProvider;
import ru.npptmk.guiObjects.IThickScanDataProvider;

/**
 * Класс предназначен для взаимодействия с восьмиканальным ультразвуковым
 * дефектоскопом производство КРОПУс по протоколу УДП.<br>
 * Класс наследует {@code GeneralUDPDevice} и основной функционал взаимодействия
 * размещен в нем. В этом классе реализована отправка команд о установке
 * параметров и номера канала для передачи аСкана, а также совмещение полученных
 * данных с координатой сечения присланной драйвером транспортной системы.<br>
 * Опавещение о получении новых данных реализовано в родительсокм классе, для
 * его включения необходимо вызвать {@code addListenerNewPoint}.
 *
 * @author SmorkalovAV
 */
public class DeviceUSKUdp extends GeneralUDPDevice
        implements IDefectScanDataProvider, IThickScanDataProvider {

    private static final Logger log = LoggerFactory.getLogger(DeviceUSKUdp.class);

    /**
     * установка номера канала из которого будет читаться А-скан
     */
    public final static long CMDASCANCHANNEL = 0xA1600AC0l;
    /**
     * установка параметров работы заданного канала
     */
    public final static long CMDSETCHANNELPARAMS = 0xA1600ACDl;
    /**
     * запрет работы каналов
     */
    public final static long CMDDISABLEWORK = 0xA160DD00l;
    /**
     * разрешение работы каналов
     */
    public final static long CMDENABLEWORK = 0xA160EE00l;
    /**
     * Команнда поддержки потока данных. Не требуеет ответа.
     */
    public final static long CMDACTIVE = 0xA16000AAl;

    private final DeltaIsmerenMinMax buf1[] = new DeltaIsmerenMinMax[8];  // Буферы для накопления результатов
    private final DeltaIsmerenMinMax buf2[] = new DeltaIsmerenMinMax[8];
    private final int bfAScan1Min[] = new int[250];         // Буферы для хранения А-Скана
    private final int bfAScan2Min[] = new int[250];
    private final int bfAScan1Max[] = new int[250];
    private final int bfAScan2Max[] = new int[250];
    private byte[] serCmdBuf = new byte[1024];              //Команда поддержания серии.
    private final byte[] aScanBuf = new byte[100];          //Буфер для приема данных А-скана

    private DeltaIsmerenMinMax toSave[];                    // Текущмй буфер для накопления результатов
    private DeltaIsmerenMinMax toProc[];                    // Текущмй буфер для обработки результатов
    private DeviceUSKUdpParams params;          //Параметры всех каналов.
    private int activeChan;                         //Номер текущего канала.
    private long prevPackNo;
    private long pkgNo;
    private final SimpleDateFormat dateFormater = new SimpleDateFormat("DD.MM.YYYY HH:mm:ss.SSS");
    private long prgCmdNo;
    private long cmdId;
    private short cmdStat;
    private short[] resCount = new short[8];        // Количество результатов в каждом канале
    private short[][] resDat = new short[8][50];    // Буфер с результатами по каналам.
    private short setParamStatus;
    private long curCommand;
    private int currentCoord;               // текущая координата трубы.
    private int prevCoord;                  // Последняя обработанная координата.
    private short[] chanStatus = new short[8];
    private short[] ch_freq = new short[8];
    private int[] a_dataSvMin;
    private int[] a_dataPrcMin;
    private int[] a_dataSvMax;
    private int[] a_dataPrcMax;
    private short a_scan_pos = 0;
    /**
     * Количество пропущенных пакетов.
     */
    private long packageRecieved;
    /**
     * Количество первых пакетов которое необходимо пропустить.
     */
    private int packageNumberToSkeep = -1;
    private long drvId;
    private boolean isChanged;
    private float[][] xCoords = new float[8][1500];     // X координаты точек графиков.
    private float[][] yCoords = new float[8][1500];     // Y координаты точек графиков.
    private int nGrPoints;                              // Количество точек на графиках.
    private int dataPackNo = 0;                         // Количество пакетов данных
    // с момента начала накопления.
    private boolean dataForPointReady = false;          // Данные для одной точки готовы.
    public boolean isNewPointReady = false;             // Флаг готовности новой точки к обработке
    /**
     * канал, по которому пришел а-скан
     */
    private short a_scan_channel;
//    private final Object newPndProcc = new Object(); //Синхронизация создания и рисования графиков
    private boolean blockToProc; //фдаг блокировки коллекции toProc
    /**
     * Объект синхронизации, блокирует чтение данных из коллекции {@code toProc}
     * пока не будет вызван метод {@code calculate()} для каждогоо элемента
     * коллекции
     */
    private final Object newPntProc = new Object(); // 
    private boolean newCoord;

    /**
     * Конструктор драйвера.
     *
     * @param local локальный сокет.
     * @param remote сокет компьютера дефектоскопа.
     * @param prm параметры каналов дефектоскопа.
     * @param drvId идентификатор драйвера.
     */
    public DeviceUSKUdp(InetSocketAddress local, InetSocketAddress remote, DeviceUSKUdpParams prm, long drvId) {
        super(1000, 750, local, remote, 1024);
        this.packageNumberToSkeep = 5;
        driverName = "USK_UDP " + drvId;
        this.drvId = drvId;
        for (int i = 0; i < 8; i++) {
            buf1[i] = new DeltaIsmerenMinMax(0f, DeltaIsmerenMinMax.DOVVER0_95, 500);
            buf2[i] = new DeltaIsmerenMinMax(0f, DeltaIsmerenMinMax.DOVVER0_95, 500);
        }
        toSave = buf1;
        toProc = null;
        a_dataSvMin = bfAScan1Min;
        a_dataSvMax = bfAScan1Max;
        a_dataPrcMin = null;
        a_dataPrcMax = null;
        this.params = prm;
        RevDataOutputStream stcm = new RevDataOutputStream(serCmdBuf);
        try {
            stcm.write(getLocal().getAddress().getAddress());
            stcm.writeShort((short) getLocal().getPort());
            stcm.writeRevInt(0);
            stcm.writeRevInt(CMDACTIVE);
            stcm.writeRevShort(activeChan);
        } catch (IOException ex) {
            log.error("Error of working with data stream in constructor.", ex);
        }

    }

    /**
     * Обработка поступившего пакета данных.
     *
     * @param ib Поступивший буфер данных.
     */
    @Override
    public void newDataAvailable(byte[] ib) {
        RevDataInputStream ris = new RevDataInputStream(ib);
        try {
            // Чтение пакета данных.
            pkgNo = ris.readRevInt();
            //Если потеряны пакеты
            if (pkgNo > (prevPackNo + 1)) {
                log.warn(String.format("{} packages are lost {}", (pkgNo - prevPackNo - 1), driverName));
            }
            dataPackNo += (pkgNo - prevPackNo);
            prevPackNo = pkgNo;
            //println("Package number:" + pkgNo);
            packageRecieved++;
            prgCmdNo = ris.readRevInt();
            cmdId = ris.readRevInt();
            ris.readRevShort();
            cmdStat = (short) ris.readRevShort();
            for (int i = 0; i < 8; i++) {
                chanStatus[i] = (short) ris.readRevShort();
            }
            int i;
            for (i = 0; i < 8; i++) {
                ch_freq[i] = (short) ris.readRevShort();
            }
            for (i = 0; i < 8; i++) {
                resCount[i] = (short) ris.readRevShort();
            }
            for (i = 0; i < 8; i++) {
                if (params.prms[i].getHardware_type() == DeviceUSKUdpParam.FLAW_CONTROL) {
                    // Результаты дефектоскопии. Используем только младший байт
                    for (int j = 0; j < 50; j++) {
                        resDat[i][j] = (short) (ris.readRevShort() & 0xff);
                        resDat[i][j] = (short) ((float) resDat[i][j] * 0.7874);
                        if (j < resCount[i]) {
                            toSave[i].addMinMax(resDat[i][j]);
                            if (resDat[i][j] > 70) {
                                /*System.out.println(dateFormater.format(new Date())
                                        + " Update min or max for Block:"
                                        + getDeviceId()
                                        + " channel:"
                                        + i
                                        + " value is:"
                                        + String.valueOf(resDat[i][j])
                                );*/
                            }
                            if (nGrPoints <= 1) {

                            }
                        }
                    }
                } else {
                    for (int j = 0; j < 50; j++) {
                        resDat[i][j] = (short) ris.readRevShort();
                    }
                    toSave[i].addItem(resCount[i], resDat[i]);
                }
            }
            // а-скан читается в виде массива из 100 байт, по паре байт (мин,
            // макс) для каждой точки, высота а-скана 200 .
            ris.read(aScanBuf);//позиция 864 0х360 начало аСкана, оконч 3C4
            a_scan_pos = (short) ris.readRevShort();
            if (a_scan_pos > 200 || a_scan_pos < 0) {
                a_scan_pos = 0;
                Arrays.fill(a_dataSvMax, 0);
                Arrays.fill(a_dataSvMin, 0);
            }
            for (i = 0; i < 50; i++) {
                if (params.prms[activeChan].getDetector() == 0) {
                    a_dataSvMin[i + a_scan_pos] = (int) (aScanBuf[i * 2] / 2.56f) + 50;
                    a_dataSvMax[i + a_scan_pos] = (int) (aScanBuf[i * 2 + 1] / 2.56f) + 50;
                } else {
                    a_dataSvMin[i + a_scan_pos] = (int) (aScanBuf[i * 2] / 1.28f);
                    a_dataSvMax[i + a_scan_pos] = (int) (aScanBuf[i * 2 + 1] / 1.28f);
                }
            }
            a_scan_channel = (short) ris.readRevShort();
            ris.readRevShort();
        } catch (IOException ex) {
            log.error("Error reading data stream", ex);
        }
        // разбока с результатами
        if (cmdId == curCommand) {
            // пришел ответ на команду
            setParamStatus = cmdStat;
            setCurCmdStatus(RC_OK);
            curCommand = -1;
        }

        // Если накоплено нужное количество результатов, обрабатываем их.
        if (a_scan_pos == 200) {
            // Для каждых 5 пакетов будет обновлен А-скан
            if (a_dataSvMax == bfAScan1Max) {
                a_dataPrcMin = bfAScan1Min;
                a_dataPrcMax = bfAScan1Max;
                a_dataSvMin = bfAScan2Min;
                a_dataSvMax = bfAScan2Max;
            } else {
                a_dataPrcMin = bfAScan2Min;
                a_dataPrcMax = bfAScan2Max;
                a_dataSvMin = bfAScan1Min;
                a_dataSvMax = bfAScan1Max;
            }
            // Точка результатов формируется для каждых 20 пакетов.
            if (dataPackNo >= 20) {
                blockToProc = true;
                if (toSave == buf1) {
                    toProc = buf1;
                    toSave = buf2;
                } else {
                    toProc = buf2;
                    toSave = buf1;
                }
                for (DeltaIsmerenMinMax d : toSave) {
                    d.resetCont();
                }
                dataPackNo = 0;
                dataForPointReady = true;
            }
            if (packageNumberToSkeep < packageRecieved) {
                newPointReady();
            } else {
                resetBufs();
            }

        }
    }

    @Override
    public void noResponce() {
        log.warn("Connection to {} block is lost" + drvId);
    }

    /**
     * Обработка результатов для очередной точки.<br>
     * Для каждой точки вычисляются значения толщин.<br>
     * Кроме того, формируется новая точка на графиках.
     */
    @Override
    public void newPointAvailable() {
        if (!dataForPointReady) {
            return;
        }
        if (!isSerCmdOn()) {
            return;
        }
        dataForPointReady = false;
        // Для каналов толщинометрии производим статистическую обработку и вычисляем 
        // значение толщины в условных единицах.

        if (toProc == null) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            if (params.prms[i].getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
                toProc[i].calculate();
            }
        }
        synchronized (newPntProc) {
            blockToProc = false;
            newPntProc.notifyAll();
        }
        // Если появилась новая координата (был вызван метод setCurrentKoord), то
        // формируется новая точка на графиках.
        if (newCoord || nGrPoints == 0) {
            //Если новая точка не выдет за пределы массива
            if (nGrPoints < xCoords[0].length) {
                nGrPoints++;
            }
            //Созадем новую точку
            for (int i = 0; i < 8; i++) {
                //Считаем x координату текущей точки
                xCoords[i][nGrPoints - 1] = currentCoord - params.prms[i].getMech_pos();
                //Корректируем x координату предыдущей точки если есть
                if (nGrPoints > 1) {
                    xCoords[i][nGrPoints - 2] = (xCoords[i][nGrPoints - 2] + xCoords[i][nGrPoints - 1]) / 2.0F;
                }

                //Определяем y координату новой точки
                yCoords[i][nGrPoints - 1] = 0;
                //Если есть вторая точки
                if (nGrPoints == 2) {
                    if (params.prms[i].getHardware_type() == DeviceUSKUdpParam.FLAW_CONTROL) {
                        //Записываем текущее значения для координаты y
                        yCoords[i][0] = toProc[i].max;
                    }
                }
            }
        }

        //Обновляем данные текущей точки
        for (int i = 0; i < 8; i++) {

            //Если канал включен
            if (params.prms[i].getEnabled()) {
                //Если канал толщинометрии
                if (params.prms[i].getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
                    //Если значение толщиномера в мм
                    if (params.prms[i].getKalibrEnable()) {
                        yCoords[i][nGrPoints - 1] = params.prms[i].getThick(toProc[i].srSnach);
                    } else {
                        yCoords[i][nGrPoints - 1] = 0;
                    }
                } else {//Если канал дефектоскопии

                    yCoords[i][nGrPoints - 1] = Math.max(
                            yCoords[i][nGrPoints - 1],
                            toProc[i].max
                    );
                    //System.out.println("Point number:" + nGrPoints + "Value y is: " + yCoords[i][nGrPoints - 1]);
                }
            } else {
                yCoords[i][nGrPoints - 1] = 0;
            }
        }
        newCoord = false;
        prevCoord = currentCoord;
        isNewPointReady = true;
    }

    /**
     * Начало сканирования с заполнением координат отсканированных сечений. <br>
     * Результаты сканирования появляются в реальном времени и при их готовности
     * генерируется событие {@code GeneralUDPDeviceEvent}.
     *
     * @param coord стартовая координата сечения исследования, больше или равна
     * 0.
     */
    public void startScan(int coord) {
        packageRecieved = 0;
        setPrevCoord(coord);
        currentCoord = 0;
        resetBufs();
        setSerCommand(serCmdBuf);
        log.debug("Scanning has been started");
    }

    /**
     * Останавливает работу дефектоскопа.
     */
    public void stopScan() {
        setSerCommand(null);
    }

    public void setPrevCoord(int prevCoord) {
        this.prevCoord = prevCoord;
    }

    @Override
    public void start() {
        super.start();
        setParams(params);
    }

    /**
     * Добавление координаты сечения Метод вызывается когда от драйвера
     * транспортной системы приходит координата
     *
     * @param crd координата
     */
    public void setCurrenCoord(int crd) {
        currentCoord = crd;
        newCoord = true;
    }

    /**
     * Возвращает параметры всех каналов устройства
     *
     * @return параметры всех каналов
     */
    public DeviceUSKUdpParams getParams() {
        return params;
    }

    /**
     * Возвращает параметры активного канала
     *
     * @return парамерты
     */
    public DeviceUSKUdpParam getActiveChanParam() {
        return params.prms[activeChan];
    }

    /**
     * Возвращает канал, который настраиваем и по которому пересылается аСкан
     *
     * @return идентификатор активного канала
     */
    public int getActiveChan() {
        return activeChan;
    }

    /**
     * Устанавливает активный канал
     *
     * @param i ид активного канала
     * @return результат работы команды:<ul>
     * <li>GeneralUDPDevice.CS_NORESPONCE - нет ответа таймаут закончился
     * <li>GeneralUDPDevice.CS_INPROGRESS - пока нет ответа от драйвера
     * <li>GeneralUDPDevice.CS_OK - ответ получен</ul>
     */
    public short setActiveChan(int i) {
        curCommand = CMDASCANCHANNEL;
        RevDataOutputStream stcm = new RevDataOutputStream(sendSize);
        try {
            stcm.write(getLocal().getAddress().getAddress());
            stcm.writeShort((short) getLocal().getPort());
            stcm.writeRevInt(getSendedCmd() + 1);
            stcm.writeRevInt(curCommand);
            stcm.writeRevShort(i);
        } catch (IOException ex) {
        }
        setCommand(stcm.getBuff());
        if (waitForCommandResponce() == CS_OK) {
            log.debug("Channel {} set as active.", i);
            activeChan = i;
            return setParamStatus;
        }
        return CS_NORESPONCE;
    }

    /**
     * Канал, по которому пришел а-скан
     *
     * @return
     */
    public short getA_scan_channel() {
        return a_scan_channel;
    }

    /**
     * Фактическая базовая частота отправки пакетов канала
     *
     * @return
     */
    public int getTimer_base_freq() {
        return ch_freq[activeChan];
    }

    /**
     * Устанавливает режим работы: 1 - работает, 0 - не работает.
     *
     * @param i режим
     * @return результат работы команды:<ul>
     * <li>GeneralUDPDevice.CS_NORESPONCE - нет ответа таймаут закончился
     * <li>GeneralUDPDevice.CS_INPROGRESS - пока нет ответа от драйвера
     * <li>GeneralUDPDevice.CS_OK - ответ получен</ul>
     */
    public short setModeWork(int i) {
        if (i == 0) {
            curCommand = CMDDISABLEWORK;
        } else {
            curCommand = CMDENABLEWORK;
        }
        RevDataOutputStream stcm = new RevDataOutputStream();
        try {
            stcm.write(getLocal().getAddress().getAddress());
            stcm.writeShort((short) getLocal().getPort());
            stcm.writeRevInt(getSendedCmd() + 1);
            stcm.writeRevInt(curCommand);
        } catch (IOException ex) {
            log.error("", ex);
        }
        setCommand(stcm.getBuff());
        if (waitForCommandResponce() == CS_OK) {
            return setParamStatus;
        }
        return CS_NORESPONCE;
    }

    /**
     * Устанавливает параметры всех каналов устройства
     *
     * @param prms параметры всех каналов устройства
     */
    public void setParams(DeviceUSKUdpParams prms) {
        params = prms;
        for (DeviceUSKUdpParam p : params.prms) {
            if (p.getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
                setChnlParam(p, prms.deltaGainThick);
            } else if (p.getDefect_type() == 1 || p.getDefect_type() == 0) {
                setChnlParam(p, prms.deltaGainAx);
            } else if (p.getDefect_type() == 2 || p.getDefect_type() == 3) {
                setChnlParam(p, prms.deltaGainDir);
            }
        }
    }

    /**
     * Копирует в текущий набор параметры из другого типоразмера.
     *
     * @param prms Копируемые параметры.
     */
    public void copyParams(DeviceUSKUdpParams prms) {
        for (int i = 0; i < params.prms.length; i++) {
            params.prms[i].copyFrom(prms.prms[i]);
        }
        setParams(params);
    }

    /**
     * Устанавливает параметры заданного канала, и сохраняет переданный канал в
     * массив каналов
     *
     * @param prm параметры заданного канала
     * @return результат работы команды:<ul>
     * <li>GeneralUDPDevice.CS_NORESPONCE - нет ответа таймаут закончился
     * <li>0 - параметры установлены. (Из-за ошибки в программе кропуса нулевой
     * код завершения будет даже при возникновении ошибки.
     * </ul>
     */
    public short setChnlParam(DeviceUSKUdpParam prm) {
        params.prms[prm.getId()] = prm;
        return setChnlParam(prm, prm.fail);
    }

    /**
     * Устанавливает параметры заданного канала, увеличив значение усиления на
     * значение val, в массив каналов не сохраняет.
     *
     * @param pr параметры заданного канала
     * @param val значение ослабления усиления канала (это значение вычитается
     * из параметра усиления канала, при этом значение усиления заданное в
     * параметрах {@code pr} сохраняется).
     * @return результат работы команды:<ul>
     * <li>GeneralUDPDevice.CS_NORESPONCE - нет ответа таймаут закончился
     * <li>0 - параметры установлены. (Из-за ошибки в программе кропуса нулевой
     * код завершения будет даже при возникновении ошибки.
     * </ul>
     */
    synchronized public short setChnlParam(DeviceUSKUdpParam pr, float val) {
        params.prms[pr.getId()] = pr;
        pr.fail = val;
        if (val != 0) {
            pr.setGain(pr.getGain() - val);
        }
        pr.checkParam();
        curCommand = CMDSETCHANNELPARAMS;
        RevDataOutputStream stcm = new RevDataOutputStream();
        Arrays.fill(stcm.getBuff(), (byte) 0);
        try {
            stcm.write(getLocal().getAddress().getAddress());
            stcm.writeShort((short) getLocal().getPort());
            stcm.writeRevInt(getSendedCmd() + 1);
            stcm.writeRevInt(curCommand);
            stcm.writeRevShort(pr.getId());
            stcm.write(pr.getValue().getBuff());
        } catch (IOException ex) {
        }
        if (val != 0) {
            pr.setGain(pr.getGain() + val);
        }
        setCommand(stcm.getBuff());
        if (waitForCommandResponce() == CS_OK) {
            isChanged = true;
            return setParamStatus;
        }
        return CS_NORESPONCE;
    }

    /**
     * Сбрасывает признак наличия измененных параметров.<br>
     * Нужно вызывать каждый раз после сохранения измененных параметров.
     */
    public void resetChangeFlag() {
        isChanged = false;
    }

    /**
     * Возвращает признак наличия несохраненных изменений в параметрах
     * установки.
     *
     * @return {@code true} если изменения есть, {@code false} - в противном
     * случае.
     */
    public boolean getChangeFlag() {
        return isChanged;
    }

    @Override
    public int getMinValue(int nc) {
        if (toProc != null) {
            while (blockToProc) {
                synchronized (newPntProc) {
                    try {
                        newPntProc.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
            if (params.prms[nc].getEnabled()) {
                if (params.prms[nc].getHardware_type() == DeviceUSKUdpParam.FLAW_CONTROL) {
//                    synchronized (newPndProcc) {
                    return toProc[nc].min;
//                    }
                }
            }
        }
        return -1;
    }

    @Override
    public int getMaxValue(int nc) {
        if (toProc != null) {
            while (blockToProc) {
                synchronized (newPntProc) {
                    try {
                        newPntProc.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
            if (params.prms[nc].getEnabled()) {
                if (params.prms[nc].getHardware_type() == DeviceUSKUdpParam.FLAW_CONTROL) {
//                    synchronized (newPndProcc) {
                    return toProc[nc].max;
//                    }
                }
            }
        }
        return -1;
    }

    @Override
    public int getThreshold(int nc) {
        return params.prms[nc].getA_thresh();
    }

    @Override
    public long getDeviceId() {
        return drvId;
    }

    public int[] getAScanMin() {
        return a_dataPrcMin;
    }

    public int[] getAScanMax() {
        return a_dataPrcMax;
    }

    @Override
    public int getGraficLength(int nc) {
//        synchronized (newPndProcc) {
        return nGrPoints;
//        }
    }

    @Override
    public int getGrafic(int nc, float[] x, float[] y) {
//        synchronized(newPndProcc){
        int sz = Math.min(x.length, nGrPoints);
        System.arraycopy(xCoords[nc], 0, x, 0, sz);
        System.arraycopy(yCoords[nc], 0, y, 0, sz);
        return sz;
//        }
    }

    /**
     * Возвращает тощину канала толщинометрии в зависимости от режима
     * калибровки. Если калибровка разрешена, то возвращается значение в
     * миллиметрах, иначе в условных единицах времени.
     *
     * @param nc номер канала.
     * @return значение толщины или времени.
     */
    @Override
    public float getThick(int nc) {
        float ret = -1f;
        if (toProc != null) {
            if (params.prms[nc].getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
                while (blockToProc) {
                    synchronized (newPntProc) {
                        try {
                            newPntProc.wait();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
                ret = params.prms[nc].getThick(toProc[nc].srSnach);
            }
        }
        return ret;
    }

    /**
     * Возвращает значение толщиномера в условных единицах времени.
     *
     * @param nc номер канала.
     * @return значение времени, измеренное толщиномером в условных единицах.
     * Если данных нет, то возвращается -1.
     */
    public int getThickVal(int nc) {
        int ret = -1;
        if (toProc != null) {
            if (params.prms[nc].getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
                while (blockToProc) {
                    synchronized (newPntProc) {
                        try {
                            newPntProc.wait();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
                ret = (int) toProc[nc].srSnach;
            }
        }
        return ret;
    }

    @Override
    public float getMaxThick(int nc) {
        if (params.prms[nc].getKalibrEnable()) {
            return 15.0f;
        }
        return 2048f;
    }

    @Override
    public int getThickGrafLength(int nc) {
        return getGraficLength(nc);
    }

    @Override
    public int getThickGrafic(int nc, float[] x, float[] y) {
        int sz = 0;
        if (params.prms[nc].getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
            sz = Math.min(x.length, nGrPoints);
            System.arraycopy(xCoords[nc], 0, x, 0, sz);
            System.arraycopy(yCoords[nc], 0, y, 0, sz);
        }
        return sz;
    }

    public void resetBufs() {
        log.debug("Charts has been dropped.");
        nGrPoints = 0;
        for (int i = 0; i < 8; i++) {
            buf1[i].reset();
            buf2[i].reset();
        }
        toProc = null;
    }
}
