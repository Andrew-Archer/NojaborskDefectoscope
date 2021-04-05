/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.devices.md8Udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import javax.swing.JPanel;
import ru.npptmk.commonObjects.DeltaIsmerenMinMax;
import ru.npptmk.commonObjects.GeneralUDPDevice;
import ru.npptmk.commonObjects.RevDataInputStream;
import ru.npptmk.commonObjects.RevDataOutputStream;
import ru.npptmk.guiObjects.IDefectScanDataProvider;

/**
 * Драйвер предназначен для работы с восьмиканальным блоком электромагнитного
 * контроля МД-8 (устройство) по протоколу УДП.<br>
 * Процесс общения с устройством обеспечивается базовым классом
 * {@code GeneralUDPDevice}.<br>
 * Данные от устройства поступают пакетами по 1 Кб. При поступлении каждого
 * пакета базовый класс вызывает метод {@code newDataAvailable()}. Этот метод
 * содержит весь функционал обработки данных.<br>
 * Данный драйвер производит следующую обработку поступившего пакета:
 * <ol>
 * <li> Проверка корректности данных по кодам команды. Неправильные пакеты
 * игнорируются молча.
 * <li> Выделение кода завершения разовой команды, если такой существует.
 * </li> Выделение массивов результатов измерения по каналам и накопление их в
 * специальных объектах, обеспечивающих сохранение минимального и максимального
 * значений резултатов по каждому каналу для предоставления их вызывающей
 * проограмме по мере надобности.
 * </ol>
 * После накопления результатов для 5 пакетов производится формирование новой
 * точки результатов сканирования. Эта обработка производится в методе
 * {@code newPointAvailable()}. Этот метод вызывается базовым классом в
 * отдельном потоке в ответ на вызов метода {@code newPointReady()}.<br>
 * Формирование новой точки может продолжаться не более чем время накопления
 * результатов для очередной точки - то есть 0.1 сек. В процессе формирования
 * точки производится привязка накопленного максимального значения сигнала
 * дефектоскопии к текущей координате и сохранение результата в массиве графиков
 * результатов. Если координата очередной точки отличается от предыдущей менее
 * чем на 15мм., то новая точка к графику не добавляется, а обновляется значение
 * последней.<br><br>
 * Драйвер предоставляет результаты в реальном масштабе времени посредством
 * интерфейса {@code IDefectScanDataProvider}. В рамкахэтого интерфейса
 * предоставляются минимальное, максимальное и пороговое значение для текущего
 * момента времени для каждого канала, а также графики изменения сигнала в
 * зависимости от текущей координаты.
 *
 * @author Администратор
 */
public class DeviceMD8Udp extends GeneralUDPDevice implements IDefectScanDataProvider {

    static final long CMDACTIVE = 0xCD8000AAl;
    static final long CMDSETPARAMS = 0xCD900ACDl;

    private final DeltaIsmerenMinMax buf1[] = new DeltaIsmerenMinMax[8];  // Буферы для накопления результатов
    private final DeltaIsmerenMinMax buf2[] = new DeltaIsmerenMinMax[8];

    private DeltaIsmerenMinMax toSave[];                        // Текущий буфер для накопления результатов
    private DeltaIsmerenMinMax toProc[];                        // Текущий буфер для обработки результатов

    int pkgCount = 0;                           // Счетчик принятых пакетов
    // содержимое пакета
    private long pkgNo;
    private long cmdId;
    private short cmdStat;
    private final short[] resCount = new short[8];
    private final short[][] resDat = new short[8][50];

    private ParamsMD8Udp param;                 // Текущие параметры дефектоскопа.
    private int setParamStatus;                 // Статус команды установки параметров.
    private long prgCmdNo;              //номер пакета с командой, на которую прислан ответ
    private byte[] serCmdBuf = new byte[1024];
    private int curretnCoord;
    private int prevCoord;
    private int[] x = new int[8];
    private long devId;
    
    private boolean isChanged = false;          // Признак наличия изменений в параметрах.

    private int npoints;         // Количество точек в графиках каналов.
    // Массивы координат создаем исходя из предположения, что точки на графике
    // идут с шагом не менее 10 мм. Тогда для 15м трубы достаточно 1500 точек на графике.
    // Общий объем памяти под графики составит 96Кб. Динамическое переразмещение
    // массивов в этом случае не потребуется.
    private float[][] xCoord = new float[8][1500];  // Координата X
    private float[][] yCoord = new float[8][1500];  // Координата Y
    private boolean newCoord;

    public DeviceMD8Udp(InetSocketAddress local, InetSocketAddress remote, ParamsMD8Udp prm, long devId) {
        super(1000, 750, local, remote, 1024);
        driverName = "MD8_UDP";
        this.devId = devId;
        for (int i = 0; i < 8; i++) {
            buf1[i] = new DeltaIsmerenMinMax();
            buf2[i] = new DeltaIsmerenMinMax();
        }
        toSave = buf1;
        toProc = null;
        param = prm;
        RevDataOutputStream stcm = new RevDataOutputStream(serCmdBuf);
        try {
            stcm.write(getLocal().getAddress().getAddress());
            stcm.writeShort((short) getLocal().getPort());
            stcm.writeRevInt(0);
            stcm.writeRevInt(CMDACTIVE);
        } catch (IOException ex) {
        }
    }

    @Override
    public void newDataAvailable(byte[] ibuf) {
        RevDataInputStream ris = new RevDataInputStream(ibuf);
        pkgCount++;
        try {
            // Чтение пакета данных.
            pkgNo = ris.readRevInt();
            prgCmdNo = ris.readRevInt();
            cmdId = ris.readRevInt();
            cmdStat = (short) ris.readRevShort();
            for (int i = 0; i < 8; i++) {
                resCount[i] = (short) ris.readRevShort();
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 50; j++) {
                    resDat[i][j] = (short) ((float) ris.readRevShort());
                    if (j < resCount[i]) {
                        toSave[i].addMinMax(resDat[i][j]);
                    }
                }
            }
        } catch (IOException ex) {
        }
        // разборка с результатами
        if (cmdId == CMDSETPARAMS) {
        // пришел ответ на команду установки параметров.
            setParamStatus = cmdStat;
            setCurCmdStatus(RC_OK);
        }
        // Если накоплено нужное количество результатов, обрабатываем их.
        if (pkgCount == 5) {
            if (toSave == buf1) {
                toProc = buf1;
                toSave = buf2;
            } else {
                toProc = buf2;
                toSave = buf1;
            }
            int i = 0;
            for (DeltaIsmerenMinMax d : toSave) {
                d.resetCont();
            }
            pkgCount = 0;
            newPointReady();
        }
    }

    /**
     * Обработка факта потери связи. Пока игнорируем.
     */
    @Override
    public void noResponce() {

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
        setPrevCoord(0);
        curretnCoord = 0;
        resetBufs();
        setSerCommand(serCmdBuf);
    }

    /**
     * Останавливает работу дефектоскопии.
     */
    public void stopScan() {
        setSerCommand(null);
    }

    /**
     * Добавление координаты сечения Метод нужно вызвать когда от драйвера
     * транспортной системы приходит новое значение координаты.
     *
     * @param crd координата в миллиметрах.
     */
    public void setCurrenCoord(int crd) {
        curretnCoord = crd;
        newCoord = true;
    }

    /**
     * Устанавливаем координату предыдущего сечения.<br>
     * Если координата отрицательная или нулевая, то обнуляются все накопленные
     * графики сигналов.
     *
     * @param crd координата, мм.
     */
    public void setPrevCoord(int crd) {
        prevCoord = crd;
    }

    /**
     * Обработка результатов для одной точки графика.<br>
     * Выполняется добавление в случае необходимости новой точки в массивы
     * графиков изменения сигнала по каналам. Новая точка добавляется при
     * условии, что координата за это время сместилась не менее чем на 15 мм.
     */
    @Override
    public void newPointAvailable() {
        if (!isSerCmdOn()) {
            return;
        }
//        System.out.println("Точка магнитки " + curretnCoord);
        // Если разница между текущей и предыдущей координатами
        // превышает 15 мм., то формируем новую точку на графике.
        if (newCoord) {
            int pointCoord = (curretnCoord + prevCoord) / 2;
            for (int i = 0; i < 8; i++) {
                xCoord[i][npoints] = pointCoord + param.offset[i];
                yCoord[i][npoints] = getMaxValue(i);
            }
            npoints++;
            if (npoints == 1500) {
                // Страховка от переполнения массива.
                npoints = 1499;
            }
            prevCoord = curretnCoord;
            newCoord = false;
        } else {
            // В противном случае, обновляем значение последней точки.
            for (int i = 0; i < 8; i++) {
                if (npoints == 0) {
                    // Точек на графике еще не было. Нужно добавить первую.
                    int pointCoord = (curretnCoord + prevCoord) / 2;
                    xCoord[i][npoints] = pointCoord + param.offset[i];
                    yCoord[i][npoints] = getMaxValue(i);
                    npoints++;
                } else {
                    yCoord[i][npoints - 1] = Math.max(yCoord[i][npoints - 1], getMaxValue(i));
                }
            }
        }
    }

    /**
     * Установка парамеров дефектоскопа
     *
     * @param par Параметры дефектоскопа.
     * @return Код завершения установки параметров.
     */
    public int setParam(ParamsMD8Udp par) {
        param = par;
        RevDataOutputStream stcm = new RevDataOutputStream();
        try {
            stcm.write(getLocal().getAddress().getAddress());
            stcm.writeShort((short) getLocal().getPort());
            stcm.writeRevInt(getSendedCmd() + 1);
            stcm.writeRevInt(CMDSETPARAMS);
            stcm.write(par.GetValue());
        } catch (IOException ex) {
        }
        setCommand(stcm.getBuff());
        if (waitForCommandResponce() == CS_OK) {
            isChanged = true;
            return setParamStatus;
        }
        return CS_NORESPONCE;
    }
    /**
     * Копирует в текущий набор параметров значения параметров
     * другого типоразмера.
     * @param par Параметры, чьи значения будут скопированы.
     * @return Код завершения установки параметров.
     */
    public int copyParam(ParamsMD8Udp par){
        param.filtr = par.filtr;
        param.gain = Arrays.copyOf(par.gain, par.gain.length);
        param.offset = Arrays.copyOf(par.offset, par.offset.length);
        param.porog = Arrays.copyOf(par.porog, par.porog.length);
        return setParam(param);
    }

    /**
     * Получение действующих параметров дефектоскопа.
     *
     * @return Действующие параметры.
     */
    public ParamsMD8Udp getParam() {
        return param;
    }

    public JPanel getParamsPanel() {
        return new DeviceMD8UdpParamsPanel(param);
    }

    /**
     * Сбрасывает признак наличия измененных параметров.<br>
     * Нужно вызывать каждый раз после сохранения измененных параметров.
     */
    public void resetChangeFlag(){
        isChanged = false;
    }
    /**
     * Возвращает признак наличия несохраненных изменений в параметрах установки.
     * 
     * @return {@code true} если изменения есть, {@code false} - в противном
     * случае.
     */
    public boolean getChangeFlag(){
        return isChanged;
    }
    @Override
    public int getMinValue(int nc) {
        if (nc < 0 || nc > 7) {
            return -1;
        }
        if (toProc != null) {
            return (int) (toProc[nc].min * ParamsMD8Udp.msY);
        }
        return -1;
    }

    @Override
    public int getMaxValue(int nc) {
        if (nc < 0 || nc > 7) {
            return -1;
        }
        if (toProc != null) {
            return (int) (toProc[nc].max * ParamsMD8Udp.msY);
        }
        return -1;
    }

    @Override
    public int getThreshold(int nc) {
        if (nc < 0 || nc > 7) {
            return -1;
        }
        if (param != null) {
            return param.porog[nc];
        }
        return -1;
    }

    @Override
    public long getDeviceId() {
        return devId;
    }

    @Override
    public int getGraficLength(int nc) {
        return npoints;
    }

    @Override
    public int getGrafic(int nc, float[] x, float[] y) {
        int size = Math.min(npoints, x.length);
        size = Math.min(size, y.length);
        System.arraycopy(xCoord[nc], 0, x, 0, size);
        System.arraycopy(yCoord[nc], 0, y, 0, size);
        return size;
    }

    public void resetBufs() {
        npoints = 0;
        toProc = null;
        toSave = buf1;
        for (int i = 0; i < 8; i++) {
            buf1[i].reset();
            buf2[i].reset();
        }
    }

}
