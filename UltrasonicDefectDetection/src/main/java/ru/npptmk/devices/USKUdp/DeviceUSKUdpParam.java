package ru.npptmk.devices.USKUdp;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Properties;
import ru.npptmk.commonObjects.RevDataOutputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Параметры одного канала установки УНКТ Кропус, работающей по УДП протоколу.
 * <br>
 * Все значения в объекте хранятся в единицах измерения, используемых в
 * устройстве. Преобразование в физические величины производится функциями
 * получения и установки значений параметров.<br>
 * Если не оговорено особо, то все временные показатели измеряются в сотых долях
 * микросекунды (частота дискретизации 100МГц).
 *
 * @author SmorkalovAV
 */
public class DeviceUSKUdpParam implements Serializable {

    public static final long serialVersionUID = 4713036714342807576L;
    /**
     * тип канала - дефектоскопия
     */
    public final static short FLAW_CONTROL = 0;
    /**
     * тип канала - толщинометрия
     */
    public final static short TIME_CONTROL = 0x0111;
    /**
     * тип канала - тестовый
     */
    public final static short TEST_CONTROL = 0x0fff;

    /**
     * время 1-ого образца во внутренних единицах устройства.
     */
    private short kalibr1Value;
    /**
     * время 2-ого образца во внутренних единицах устройства.
     */
    private short kalibr2Value;

    /**
     * Признак вывода графика ВРЧ на экран
     */
    private short vrch_visible = 0;

    /**
     * Признак вывода и-Зоны на экран
     */
    private short i_visible = 0;

    /**
     * толщина 1-ого образца в миллиметрах
     */
    private float kalibr1Obrazec;
    /**
     * толщина 2-ого образца в миллиметрах
     */
    private float kalibr2Obrazec;
    /**
     * Калибровочная скорость звука в условных единицах - рассчитана по
     * результатам калибровки
     */
    private double kalibrSpeed;
    /**
     * Калибровочное смещение - рассчитано по результатам калибровки
     */
    private double kalibrDelta;
    /**
     * Признак использования результатов калибровки при вычислении толщины. Если
     * этот параметр нулевой, то результаты для канала выдаются в единицах
     * времени, иначе - в десятых долях миллиметра.
     */
    private byte kalibrEnable;
    /**
     * Отсечка от 0 до 95% высоты экрана
     */
    private short supression = 0;

    /**
     * Координата датчика канала (мм)
     */
    private int mech_pos;

    /**
     * Ослабление усиления канала.
     */
    public float fail;

    /**
     * Название канала
     */
    private String name = new String();
    /**
     * Тип канала<ul>
     * <li>FLAW_CONTROL - дефектоскоп
     * <li>TIME_CONTROL - толщинометрия
     * </ul>
     */
    private short hardware_type;
    /**
     * Тип дефекта:<ul>
     * <li> 0 - продольный прямой
     * <li> 1 - продольный обратный
     * <li> 2 - попреречный прямой
     * <li> 3 - попреречный обратный </ul>
     */
    private int defect_type;
    /**
     * Услиение с шагом 0.5 дБ до 110 дБ
     */
    private short gain = 40;
    
    /**
     * Развертка экрана От 250 до 20000 ед. времени (должна быть кратна 250
     * точкам)
     */
    private short range = 5000;
    /**
     * Задержка разверткти (от -240 до 20000) ед. времени
     */
    private short signal_delay = 3000;
    /**
     * Порог а-Зона от 0 до 95% высоты экрана, или от -95% (при радиосигнале)
     */
    private short a_thresh = 50;
    /**
     * Начало а-Зона (от начала экрана) от 0 до 20000 ед. времени
     */
    private short a_start = 1500;
    /**
     * Ширина а-Зона (от начала а-Зона) от 0 до (20000 - а-Начало)
     */
    private short a_width = 3000;
    /**
     * Порог б-Зона от 0 до 95% высоты экрана, или от -95% (при радиосигнале)
     */
    private short b_thresh = 0;
    /**
     * Начало б-Зона (от начала экрана) от 0 до 20000 ед.времени
     */
    private short b_start = 0;
    /**
     * Ширина б-Зона (от начала б-Зона) от 0 до (20000 - b-Начало) <br>
     * Для дефектоскопии не нужна - ставить длину 0 чтоб не тратить время на
     * обработку.
     */
    private short b_width = 0;
    /**
     * Порог и-Зона от 0 до 95% высоты экрана, или от -95% (при радиосигнале)
     */
    private short i_thresh = 0;
    /**
     * Начало и-Зона (от начала экрана) от 0 до (20000 - 900), где 900 - ширина
     * и-Зоны = 9 мкс
     */
    private short i_start = 0;
    /**
     * Признак включение синхронизации по и-Зоне (1 - включено, 0 - выключено)
     */
    private short i_active = 0;
    /**
     * Число точек кривой ВРЧ
     */
    private short vrch_points = 4;
    /**
     * ВременнОе положение точек ВРЧ с дискретностью 0.2 мкс (5 МГц) от 0 до
     * 1000. Точки должны идти строго в порядке возастания по времени. <br>
     * Так как временное положение точек связано с задержкой сигнала, то для
     * предотвращения искажения координат точек при изменении задержки, время
     * точек ВРЧ хранится в объекте в тех же временных единицах, что и задержка
     * сигнала. А перед передачей в Кропус ни делятся на 20.
     */
    private final short vrch_times[] = new short[10];
    /**
     * Усиление в точках ВРЧ с дискретностью 0.5 дБ (от -180 до +180)
     */
    private final short vrch_gains[] = new short[10];
    /**
     * Идентификатор канала, т.е. номер в массиве
     */
    private int id = -1;
    /**
     * Признак включения режима ВРЧ (должно быть более 2 точек). 1- включено, 0
     * - выключено.
     */
    private short vrch_go = 0;
    /**
     * Значение фильтра приемника. От 0 до 15.
     */
    private short anfilter;
    /**
     * Вид детектора: <ul>
     * <li>0 - радиосигнал
     * <li>1 - плюс
     * <li>2 - минус
     * <li>3 - полный</ul>
     */
    private short detector = 3;
    /**
     * Раздельный(0)/совмещенный(1) режим работы генератора/усилителя
     */
    private short probe_mode = 1;
    /**
     * Выключение(0)/включение(1) депфера 50 Ом на входе усилителя
     */
    private short rvhoda;
    /**
     * Выключение(0)/включение(1) депфера 50 Ом на выходе генератора
     */
    private short dempfer;
    /**
     * Условное значение ширины полупериода импульса возбуждения. Фактическая
     * ширина полупериода импульса возбуждения составит (gzi_width + 1) / 200
     * мкс. Таким образом, значение параметра связано с частотой датчика
     * следующей формулой: f = 100/(gzi_width + 1) МГц. Значение параметрв
     * должно быть в диапазоне от 3 до 199.
     */
    private short gzi_width = 39;
    
    private byte[] rezerv = new byte[64];

    /**
     * Число полупериодов импульса возбуждения (число разгонных импульсов)
     */
    private short gzi_count = 2;
    /**
     * Согласующая индуктивность на выходе генератора
     */
    private short soglas;
    /**
     * Режим измерения врменных интервалов для каналов толщинометрии <ul>
     * <li>0 - от 0 экрана до сигнала в а-Зоне
     * <li>1 - от сигнала в а-Зоне до сигнала в б-Зоне
     * <li>2 - автомат</ul>
     * Для этого алгоритма уровень а-Зоны задает уровень работы компаратора
     * фикации времени прихода сигнала, начало а-Зоны определяет начало
     * измерения сигнала, ширина а-Зоны определяет общую длину обработки
     * сигнала, длительность б-Зоны определяет максимальное время ожидания
     * следующего сигнала (максимальную толщину) - т.е. если за это время,
     * компаратор не сработал второй раз, то поиск следующих отражений
     * заканчивается.
     */
    private short time_mode;
    /**
     * Параметры фильтрации результата измерения от 0 до 32<br>
     * для канала дефектоскопии задает число измерения для<br>
     * усреднения значения маплитуды,<br>
     * а для канала толщинометрии задает максимальное повторение
     * преддыдущего<br>
     * результата толщины в канале, если текущее значение не удалось
     * вычислить<br>
     */
    private short asd_filter;
    /**
     * такт работы канала 0 - работаем в каждом такте 1 - четные 2 - нечентые
     */
    private short pulser_takt;
    /**
     * Сдвиег нулевой точки графика по вертикали
     */
    private short ads_offset;
    /**
     * Число А сканов для накопления в режиме детектирования
     */
    private short peak_mode;

    /**
     * Длительность электрического демпфера генератора, от 0 до 100, с шагом 5
     * нс
     */
    private short gzi_demp_width;
    /**
     * задержка электрического демпфера генератора, от 0 до 100, с шагом 5 нс
     */
    private short gzi_demp_delay;
    /**
     * флаг работы канала
     */
    private short enabled = 1;
    /**
     * задержка синхронизации запуска канала определяет сколько тактов должно
     * быть между запусками каналов - для понижения частоты посылок
     */
    private short sync_delay;
    /**
     * базовая частота послок дефектоскопа в котором стоит канал
     */
    private short time_base_freq = 2500;

    public byte[] getRezerv() {
        return rezerv;
    }

    public void setRezerv(byte[] rezerv) {
        this.rezerv = rezerv;
    }

    /**
     * базовая частота послок дефектоскопа в котором стоит канал
     * @return 
     */
    public short getTime_base_freq() {
        return time_base_freq;
    }

    /**
     * базовая частота послок дефектоскопа в котором стоит канал
     * @param time_base_freq
     */
    public void setTime_base_freq(short time_base_freq) {
        this.time_base_freq = time_base_freq;
    }

    public short getGzi_width_int() {
        return gzi_width;
    }

    public void setGzi_width_int(short gzi_width) {
        this.gzi_width = gzi_width;
    }
    
    public DeviceUSKUdpParam() {
        
        resetToDefault();
        checkParam();

    }

    public final void checkParam() {
        if (gain < 0) {
            gain = 0;
        }
        if (gain > 220) {
            gain = 220;
        }

        int ir = (int) (int) Math.round(range / 250.0);
        range = (short) (ir * 250);
        if (range < 250) {
            range = 250;
        }
        if (range > 20000) {
            range = 20000;
        }

        if (signal_delay < -400) {
            signal_delay = -400;
        }
        if (signal_delay > 20000) {
            signal_delay = 20000;
        }
        if (a_width < 0) {
            a_width = 0;
        }
        if (a_width > 20000) {
            a_width = (short) (20000);
        }
        if (a_thresh < -100) {
            a_thresh = -100;
        }
        if (a_thresh > 100) {
            a_thresh = 100;
        }
        if (b_width < 0) {
            b_width = 0;
        }
        if (b_width > 20000) {
            b_width = (short) 20000;
        }
        if (b_thresh < -100) {
            b_thresh = -100;
        }
        if (b_thresh > 100) {
            b_thresh = 100;
        }
        if (i_thresh < -100) {
            i_thresh = -100;
        }
        if (i_thresh > 100) {
            i_thresh = 100;
        }

        if (anfilter < 0) {
            anfilter = 0;
        }
        if (anfilter > 15) {
            anfilter = 15;
        }
        if (detector < 0) {
            detector = 0;
        }
        if (detector > 3) {
            detector = 3;
        }

        if (gzi_width < 3) {
            gzi_width = 3;
        }
        if (gzi_width > 199) {
            gzi_width = 199;
        }
        if (gzi_count < 0) {
            gzi_count = 0;
        }
        if (gzi_count > 15) {
            gzi_count = 15;
        }

        if (soglas < 0) {
            soglas = 0;
        }
        if (soglas > 7) {
            soglas = 7;
        }

        if (time_mode < 0) {
            time_mode = 0;
        }
        if (time_mode > 2) {
            time_mode = 2;
        }

        if (asd_filter < 0) {
            asd_filter = 0;
        }
        if (asd_filter > 32) {
            asd_filter = 32;
        }
        if (ads_offset < -100) {
            ads_offset = -100;
        }
        if (ads_offset > 100) {
            ads_offset = 100;
        }
        if (peak_mode < 0) {
            peak_mode = 0;
        }
        if (peak_mode > 11) {
            peak_mode = 11;
        }

    }

    /**
     * Преобразует класс в выходной байтовый поток для работы с КРОПУСом UDP
     *
     * @return
     * @throws java.io.IOException
     */
    public RevDataOutputStream getValue() throws IOException {
        RevDataOutputStream os = new RevDataOutputStream();
        os.writeRevShort(0);
        os.writeRevShort(1);
        os.writeRevShort(hardware_type);
        os.writeRevShort(gain);
        os.writeRevShort(range);
        os.writeRevShort(signal_delay);
        os.writeRevShort(supression);
        os.writeRevShort(a_thresh);
        os.writeRevShort(a_start);
        os.writeRevShort(a_width);
        os.writeRevShort(b_thresh);
        os.writeRevShort(b_start);
        os.writeRevShort(b_width);
        os.writeRevShort((short) 0/*c_thresh*/);
        os.writeRevShort((short) 0/*c_start*/);
        os.writeRevShort((short) 0/*c_width*/);
        os.writeRevShort(i_thresh);
        os.writeRevShort(i_start);
        os.writeRevShort(i_active);
        os.writeRevShort(vrch_points);
        for (int po = 0; po < 10; po++) {
            os.writeRevShort((vrch_times[po] / 20));
        }
        for (int po = 0; po < 10; po++) {
            os.writeRevShort(vrch_gains[po]);
        }
        os.writeRevShort(vrch_go);

        os.writeRevShort(anfilter);
        os.writeRevShort(detector);

        os.writeRevShort(probe_mode);
        os.writeRevShort(rvhoda);

        os.writeRevShort(dempfer);
        os.writeRevShort(gzi_width);
        os.writeRevShort(gzi_count);
        os.writeRevShort((short) 0/*gzi_demp_width*/);
        os.writeRevShort((short) 0/*gzi_demp_delay*/);
        os.writeRevShort(soglas);

        os.writeRevShort(time_mode);
        os.writeRevShort(peak_mode);
        os.writeRevShort(ads_offset);

        os.writeRevShort((short) 0/*asd_filter*/);
        os.writeRevShort((short) 0/*last_var*/);
        os.writeRevShort(enabled);

        os.writeRevShort(pulser_takt);
        os.writeRevShort(sync_delay);
        
        os.writeRevShort(0);
        
        if(rezerv == null){
            rezerv = new byte[64];
            Arrays.fill(rezerv, (byte)0);
        }
        os.write(rezerv);
        
        

        return os;
    }

    public void loadFromIni(Properties ini) {
        loadFromIni(ini, -1);
    }

    public void writeToIni(Properties ini) {
        writeToIni(ini, -1);
    }

    /**
     * Получает значение флага видимости линии ВРЧ
     *
     * @return {@code true} если линия ВРЧ видна, {@code false} - в
     * противоположном случае.
     */
    public boolean getVrch_visible() {
        return vrch_visible == 1;
    }

    /**
     * Устанавливает значение флага видимости линии ВРЧ.
     *
     * @param vrch_visible {@code true} если линия ВРЧ видна, {@code false} - в
     * противоположном случае.
     */
    public void setVrch_visible(boolean vrch_visible) {
        if (vrch_visible) {
            this.vrch_visible = 1;
        } else {
            this.vrch_visible = 0;
        }
    }

    /**
     * Получает значение флага видимости линии i-зоны.
     *
     * @return {@code true} если линия i-зоны видна, {@code false} - в
     * противоположном случае.
     */
    public boolean getI_visible() {
        return i_visible == 1;
    }

    /**
     * Устанавливает значение флага видимости линии i-зоны.
     *
     * @param i_visible {@code true} если линия i-зоны видна, {@code false} - в
     * противоположном случае.
     */
    public void setI_visible(boolean i_visible) {
        if (i_visible) {
            this.i_visible = 1;
        } else {
            this.i_visible = 0;
        }
    }

    /**
     * Возвращает положение зоны чувствительности датчика относительно начала
     * координат установки.
     *
     * @return Положение зоны чувствительности датчика относительно начала
     * координат установки в мм.
     */
    public int getMech_pos() {
        return mech_pos;
    }

    /**
     * Задает положение зоны чувствительности датчика относительно начала
     * координат установки.
     *
     * @param mech_pos положение зоны чувствительности датчика относительно
     * начала координат установки в мм.
     */
    public void setMech_pos(int mech_pos) {
        this.mech_pos = mech_pos;
    }

    /**
     * Возвращает наименование канала.
     *
     * @return Наименование канала.
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает наименование канала.
     *
     * @param name наименование канала.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает тип канала.
     *
     * @return Одна из следующих констант:
     * <ul>
     * <li>FLAW_CONTROL - дефектоскоп
     * <li>TIME_CONTROL - толщинометрия
     * </ul>
     */
    public short getHardware_type() {
        return hardware_type;
    }

    /**
     * Устанавливает тип канала.
     *
     * @param hardware_type новый тип канала. Одна из следующих констант:
     * <ul>
     * <li>FLAW_CONTROL - дефектоскоп
     * <li>TIME_CONTROL - толщинометрия
     * </ul>
     */
    public void setHardware_type(short hardware_type) {
        this.hardware_type = hardware_type;
    }

    /**
     * Возвращает тип датчика дефектоскопии.<br>
     * Имеет смысл только для каналов дефектоскопии.
     *
     * @return Тип датчика дефектоскопии:
     * <ul>
     * <li> 0 - продольный прямой
     * <li> 1 - продольный обратный
     * <li> 2 - поперечный прямой
     * <li> 3 - поперечный обратный </ul>
     */
    public int getDefect_type() {
        return defect_type;
    }

    /**
     * Задает тип датчика дефектоскопии.
     *
     * @param defect_type тип датчика дефектоскопии:
     * <ul>
     * <li> 0 - продольный прямой
     * <li> 1 - продольный обратный
     * <li> 2 - поперечный прямой
     * <li> 3 - поперечный обратный </ul>
     */
    public void setDefect_type(int defect_type) {
        this.defect_type = defect_type;
    }

    /**
     * Возвращает значение усиления в децибеллах
     *
     * @return Значение усиления в децибеллах.
     */
    public float getGain() {
        return (float) gain / 2.0f;
    }

    /**
     * Задает значение усиления в децибеллах.
     *
     * @param gain значение усиления в децибеллах.
     */
    public void setGain(float gain) {
        this.gain = (short) (gain * 2.0);
    }

    /**
     * Получает значение разверки в микросекундах.
     *
     * @return Значение развёртки в микросекундах.
     */
    public float getRange() {
        return (float) range / 100.0f;
    }

    /**
     * Задает значение развертки в микросекуедах
     *
     * @param range новое значение развертки в микросекундах.
     */
    public void setRange(float range) {
        this.range = (short) (range * 100f);
    }

    /**
     * Получает значение задержки в микросекундах.
     *
     * @return значение задержки в микросекундах.
     */
    public float getSignal_delay() {
        return (float) signal_delay / 100.0f;
    }
    /**
     * Значение отсечки (подавления сигнала)
     * @return 
     */
    public short getSupression() {
        return supression;
    }
    /**
     * Значение отсечки (подавления сигнала)
     * @return 
     */
    public void setSupression(short supression) {
        this.supression = supression;
    }

    /**
     * Задает значение задержки в микросекундах.<br>
     * @param signal_delay новое значение задержки в микросекундах.
     */
    public void setSignal_delay(float signal_delay) {
        this.signal_delay = (short) (signal_delay * 100.0f);
    }

    /**
     * Возвращает значение порога А - зоны.
     *
     * @return Значение порога А - зоны в % от высоты экрана.
     */
    public short getA_thresh() {
        return a_thresh;
    }

    /**
     * Устанавливает значение порога А - зоны.
     *
     * @param a_thresh значение порога А - зоны в % от высоты экрана.
     */
    public void setA_thresh(short a_thresh) {
        this.a_thresh = a_thresh;
    }

    /**
     * Возвращвет начало а-зоны относительно начала цикла измерения в
     * микросекундах.
     *
     * @return Начало а-зоны относительно начала цикла измерения в
     * микросекундах.
     */
    public float getA_start() {
        return (float) a_start / 100.0f;
    }

    /**
     * Задает начало а-зоны относительно начала цикла измерения в микросекундах.
     *
     * @param a_start новое значение начала а-зоны относительно начала цикла
     * измерения в микросекундах.
     */
    public void setA_start(float a_start) {
        this.a_start = (short) (a_start * 100.0f);
    }

    /**
     * Получает ширину а-зоны в микросекундах.
     *
     * @return ширина а-зоны в микросекундах.
     */
    public float getA_width() {
        return a_width / 100f;
    }

    /**
     * Задает ширину а-зоны в микросекундах.
     *
     * @param a_width новое значение ширины а-зоны в микросекундах.
     */
    public void setA_width(float a_width) {
        this.a_width = (short) (a_width * 100f);
    }

    /**
     * Возвращает значение порога В - зоны.
     *
     * @return Значение порога В - зоны в % от высоты экрана.
     */
    public short getB_thresh() {
        return b_thresh;
    }

    /**
     * Устанавливает значение порога B - зоны.
     *
     * @param b_thresh значение порога В - зоны в % от высоты экрана.
     */
    public void setB_thresh(short b_thresh) {
        this.b_thresh = b_thresh;
    }

    /**
     * Возвращвет начало B-зоны относительно начала цикла измерения в
     * микросекундах.
     *
     * @return Начало B-зоны относительно начала цикла измерения в
     * микросекундах.
     */
    public float getB_start() {
        return (float) b_start / 100.0f;
    }

    /**
     * Задает начало B-зоны относительно начала цикла измерения в микросекундах.
     *
     * @param b_start новое значение начала B-зоны относительно начала цикла
     * измерения в микросекундах.
     */
    public void setB_start(float b_start) {
        this.b_start = (short) (b_start * 100.0f);
    }

    /**
     * Получает ширину B-зоны в микросекундах.
     *
     * @return ширина B-зоны в микросекундах.
     */
    public float getB_width() {
        return (float) b_width / 100f;
    }

    /**
     * Задает ширину B-зоны в микросекундах.
     *
     * @param b_width новое значение ширины B-зоны в микросекундах.
     */
    public void setB_width(float b_width) {
        this.b_width = (short) (b_width * 100f);
    }

    /**
     * Возвращает значение порога I - зоны.
     *
     * @return Значение порога I - зоны в % от высоты экрана.
     */
    public short getI_thresh() {
        return i_thresh;
    }

    /**
     * Устанавливает значение порога I - зоны.
     *
     * @param i_thresh значение порога I - зоны в % от высоты экрана.
     */
    public void setI_thresh(short i_thresh) {
        this.i_thresh = i_thresh;
    }

    /**
     * Возвращвет начало I-зоны относительно начала цикла измерения в
     * микросекундах.
     *
     * @return Начало I-зоны относительно начала цикла измерения в
     * микросекундах.
     */
    public float getI_start() {
        return (float) (i_start) / 100.0f;
    }

    /**
     * Задает начало I-зоны относительно начала цикла измерения в микросекундах.
     *
     * @param i_start новое значение начала i-зоны относительно начала цикла
     * измерения в микросекундах.
     */
    public void setI_start(float i_start) {
        this.i_start = (short) (i_start * 100.0f);
    }

    /**
     * Возвращает флаг активности I - зоны.
     *
     * @return {@code true} если I - зона активна, {@code false} - в
     * противоположном случае.
     */
    public boolean getI_active() {
        return i_active == 1;
    }

    /**
     * Устанавливает флаг активности I - зоны.
     *
     * @param i_active {@code true} если I - зона активна, {@code false} - в
     * противоположном случае.
     */
    public void setI_active(boolean i_active) {
        if (i_active) {
            this.i_active = 1;
        } else {
            this.i_active = 0;
        }
    }

    /**
     * Возвращает количество точек линии ВРЧ.
     *
     * @return Количество точек линии ВРЧ.
     */
    public short getVrch_points() {
        return vrch_points;
    }

    /**
     * Возвращает положение по времени относительно начала экрана
     * заданной точки кривой ВРЧ.
     *
     * @param i индекс точки (начиная с 0).
     * @return Положение по времени относительно начала экрана
     * указанной точки ВРЧ в микросекундах.
     */
    public float getVrch_times(int i) {
        if (i >= vrch_points) {
            return 0.0f;
        }
        if (i < 0) {
            return 0.0f;
        }
        return (vrch_times[i]) / 100.0f;
    }

    /**
     * Устанавливает время для точки ВРЧ.
     *
     *
     * @param time время точки ВРЧ относительно начала экрана
     * импульса в микросекундах.
     * @param i индекс точки (начиная с 0).
     */
    public void setVrch_times(float time, int i) {
        if (i < 0 || i >= vrch_points) {
            return;
        }
        this.vrch_times[i] = (short) (time * 100.0f);
        for (int j = 0; j < i; j++) {
            if (vrch_times[j] > vrch_times[i]) {
                vrch_times[j] = vrch_times[i];
            }
        }
        for (int j = i + 1; j < vrch_points; j++) {
            if (vrch_times[j] < vrch_times[i]) {
                vrch_times[j] = vrch_times[i];
            }
        }
    }

    /**
     * Возвращает значение усиления в точке ВРЧ.
     *
     * @param i индекс точки (начиная с 0).
     * @return Значение усиления для заданной точки ВРЧ в децибеллах.
     */
    public float getVrch_gains(int i) {
        if (i < 0 || i >= vrch_points) {
            return 0f;
        }
        return vrch_gains[i] * 0.5f;
    }

    /**
     * Задает значение усиления в точке ВРЧ
     *
     * @param vrch_gains значение усиления для заданной точки ВРЧ в децибеллах.
     * @param i индекс точки (начиная с 0).
     */
    public void setVrch_gains(float vrch_gains, int i) {
        if (i < 0 || i >= vrch_points) {
            return;
        }
        this.vrch_gains[i] = (short) (vrch_gains * 2.0f);
    }

    /**
     * Возвращает индекс канала (начиная с 0)
     *
     * @return Индекс канала.
     */
    public int getId() {
        return id;
    }

    /**
     * Задает индекс канала (начиная с 0)
     *
     * @param id индекс канала.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает флаг использования ВРЧ.
     *
     * @return {@code true} - если ВРЧ включена, {@code false} - если ВРЧ
     * отключена.
     */
    public boolean getVrch_go() {
        return vrch_go == 1;
    }

    /**
     * Включает или выключает ВРЧ
     *
     * @param vrch_go {@code true} - если ВРЧ включена, {@code false} - если ВРЧ
     * отключена.
     */
    public void setVrch_go(boolean vrch_go) {
        if (vrch_go) {
            this.vrch_go = 1;
        } else {
            this.vrch_go = 0;
        }
    }

    /**
     * Возвращает значение параметра фильтра приемника сигналов.
     *
     * @return Значение параметра фильтра приемника сигналов.
     */
    public short getAnfilter() {
        return anfilter;
    }

    /**
     * Устанавливает значение параметра фильтра приемника сигналов.
     *
     * @param anfilter значение параметра фильтра приемника сигналов.
     */
    public void setAnfilter(short anfilter) {
        this.anfilter = anfilter;
    }

    /**
     * Возвращает тип детектора входного сигнала.
     *
     * @return тип детектора входного сигнала
     * <ul>
     * <li>0 - радиосигнал
     * <li>1 - плюс
     * <li>2 - минус
     * <li>3 - полный
     * </ul>
     */
    public short getDetector() {
        return detector;
    }

    /**
     * Устанавливает тип детектора входного сигнала.
     *
     * @param detector тип детектора входного сигнала.
     * <ul>
     * <li>0 - радиосигнал
     * <li>1 - плюс
     * <li>2 - минус
     * <li>3 - полный
     * </ul>
     */
    public void setDetector(short detector) {
        this.detector = detector;
    }

    /**
     * Возвращает режм работы канала.
     *
     * @return Режим работы канала. {@code true} - если канал совмещенный,
     * {@code false} - если канал раздельный.
     */
    public boolean getProbe_mode() {
        return probe_mode == 1;
    }

    /**
     * Устанавливает режм работы канала.
     *
     * @param probe_mode режим работы канала. {@code true} - если канал
     * совмещенный, {@code false} - если канал раздельный.
     */
    public void setProbe_mode(boolean probe_mode) {
        if (probe_mode) {
            this.probe_mode = 1;
        } else {
            this.probe_mode = 0;
        }
    }

    /**
     * Возвращает флаг включения демпфера 50 Ом на входе усилителя.
     *
     * @return Флаг включения демпфера 50 Ом на входе усилителя. {@code true} -
     * если демпфер включен, {@code false} - если демпфер выключен.
     */
    public boolean getRvhoda() {
        return rvhoda == 1;
    }

    /**
     * Устанавливает флаг включения демпфера 50 Ом на входе усилителя.
     *
     * @param rvhoda флаг включения демпфера 50 Ом на входе усилителя.
     * {@code true} - если демпфер включен, {@code false} - если демпфер
     * выключен.
     */
    public void setRvhoda(boolean rvhoda) {
        if (rvhoda) {
            this.rvhoda = 1;
        } else {
            this.rvhoda = 0;
        }
    }

    /**
     * Возвращает флаг включения демпфера 50 Ом на выходе генератора.
     *
     * @return Флаг включения демпфера 50 Ом на выходе генератора. {@code true}
     * - если демпфер включен, {@code false} - если демпфер выключен.
     */
    public boolean getDempfer() {
        return dempfer == 1;
    }

    /**
     * Устанавливает флаг включения демпфера 50 Ом на выходе генератора.
     *
     * @param dempfer флаг включения демпфера 50 Ом на выходе генератора.
     * {@code true} - если демпфер включен, {@code false} - если демпфер
     * выключен.
     */
    public void setDempfer(boolean dempfer) {
        if (dempfer) {
            this.dempfer = 1;
        } else {
            this.dempfer = 0;
        }

    }

    /**
     * Возвращает частоту зондирующего импульса.
     *
     * @return Частота злндирующего импульса в МГц.
     */
    public float getGzi_width() {
        return 100f / ((float) gzi_width + 1f);
    }

    /**
     * Задает частоту зондирующего импульса.
     *
     * @param gzi_width частота злндирующего импульса в МГц.
     */
    public void setGzi_width(float gzi_width) {
        this.gzi_width = (short) (100f / gzi_width - 1);
    }
    /**
     * Возвращает частоту зондирующего импульса.
     *
     * @return Частота злндирующего импульса в МГц.
     */

    /**
     * Возвращает число возбуждающих импульсов.
     *
     * @return Число возбуждающих ипульсов.
     */
    public short getGzi_count() {
        return gzi_count;
    }

    /**
     * Задет число возбуждающих импульсов.
     *
     * @param gzi_count число возбуждающих импульсов.
     */
    public void setGzi_count(short gzi_count) {
        this.gzi_count = gzi_count;
    }

    /**
     * Возвращает код согласующей индуктивности на выходе генератора.
     *
     * @return код согласующей индуктивности на выходе генератора.
     */
    public short getSoglas() {
        return soglas;
    }

    /**
     * Устанавливает согласующую индуктивность на выходе генератора.
     *
     * @param soglas код согласующей индуктивности на выходе генератора.
     */
    public void setSoglas(short soglas) {
        this.soglas = soglas;
    }

    /**
     * Возвращает режим измерения толщины.
     *
     * @return режим измерения толщины.
     * <ul>
     * <li> 0 - от 0 до А - зоны.
     * <li> 1 - от А - зоны до B - зоны.
     * <li> 2 - автомат.
     * </ul>
     */
    public short getTime_mode() {
        return time_mode;
    }

    /**
     * Устанавливает режим измерения толщины.
     *
     * @param time_mode режим измерения толщины.
     * <ul>
     * <li> 0 - от 0 до А - зоны.
     * <li> 1 - от А - зоны до B - зоны.
     * <li> 2 - автомат.
     * </ul>
     */
    public void setTime_mode(short time_mode) {
        this.time_mode = time_mode;
    }

    /**
     * Возвращает степень фильтрации при обработке результатов.
     *
     * @return значение степени фильтрации.
     */
    public short getAsd_filter() {
        return asd_filter;
    }

    /**
     * Устанавливает степень фильтрации при обработке резкльтатов.
     *
     * @param asd_filter значение степени фильтрации.
     */
    public void setAsd_filter(short asd_filter) {
        this.asd_filter = asd_filter;
    }

    /**
     * Возвращает такт работы канала.
     *
     * @return Такт работы канала. Возможные значения:
     * <ul>
     * <li> 0 - в каждом такте.
     * <li> 1 - только в четных.
     * <li> 2 - только в нечетных.
     * </ul>
     */
    public short getPulser_takt() {
        return pulser_takt;
    }

    /**
     * Устанавливает такт работы канала.
     *
     * @param pulser_takt такт работы канала. Возможные значения:
     * <ul>
     * <li> 0 - в каждом такте.
     * <li> 1 - только в четных.
     * <li> 2 - только в нечетных.
     * </ul>
     */
    public void setPulser_takt(short pulser_takt) {
        this.pulser_takt = pulser_takt;
    }

    /**
     * Возвращает сдвиг нулевой точки графика по вертикали.
     *
     * @return Сдвиг нуля графика по вертикали в процентах высоты экрана.
     */
    public short getAds_offset() {
        return ads_offset;
    }

    /**
     * Задает сдвиг нулевой точки графика по вертикали.
     *
     * @param ads_offset сдвиг нуля графика по вертикали в процентах высоты
     * экрана.
     */
    public void setAds_offset(short ads_offset) {
        this.ads_offset = ads_offset;
    }

    /**
     * Возвращает количество А - сканов в режиме накопления.
     *
     * @return количество А - сканов в режиме накопления.
     */
    public short getPeak_mode() {
        return peak_mode;
    }

    /**
     * Задает количество А - сканов в режиме накопления.
     *
     * @param peak_mode количество А - сканов в режиме накопления.
     */
    public void setPeak_mode(short peak_mode) {
        this.peak_mode = peak_mode;
    }

    /**
     * Возвращает флаг включения канала.
     *
     * @return {@code true} - если канал включен, {@code false} - если выключен.
     */
    public boolean getEnabled() {
        return enabled == 1;
    }

    /**
     * Включает или выключает канал.
     *
     * @param enabled {@code true} - если канал включен, {@code false} - если
     * выключен.
     */
    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enabled = 1;
        } else {
            this.enabled = 0;
        }
    }

    /**
     * Возвращает значение делителя синхронизации при запуске канала.
     *
     * @return Значение делителя синхронизации при запуске канала.
     */
    public short getSync_delay() {
        return sync_delay;
    }

    /**
     * Задает значение делителя синхронизации при запуске канала.
     *
     * @param sync_delay значение делителя синхронизации при запуске канала.
     */
    public void setSync_delay(short sync_delay) {
        this.sync_delay = sync_delay;
    }

    /**
     * Возвращает значение времени для первого образца во внутренних единицах.
     *
     * @return Значение времени для первого образца во внутренних единицах.
     */
    public short getKalibr1Value() {
        return kalibr1Value;
    }

    /**
     * Устанавливает значение времени для первого образца во внутренних
     * единицах.
     *
     * @param kalibr1Value значение времени для первого образца во внутренних
     * единицах.
     */
    public void setKalibr1Value(short kalibr1Value) {
        this.kalibr1Value = kalibr1Value;
    }

    /**
     * Возвращает значение времени для второго образца во внутренних единицах.
     *
     * @return Значение времени для второго образца во внутренних единицах.
     */
    public short getKalibr2Value() {
        return kalibr2Value;
    }

    /**
     * Устанавливает значение времени для втого образца во внутренних единицах.
     *
     * @param kalibr2Value значение времени для второго образца во внутренних
     * единицах.
     */
    public void setKalibr2Value(short kalibr2Value) {
        this.kalibr2Value = kalibr2Value;
    }

    /**
     * Возвращает толщину первого образца в мм.
     *
     * @return Толщина первого образца в мм.
     */
    public float getKalibr1Obrazec() {
        return kalibr1Obrazec;
    }

    /**
     * Устанавливает толщину первого образца в мм.
     *
     * @param kalibr1Obrazec толщина первого образца в мм.
     */
    public void setKalibr1Obrazec(float kalibr1Obrazec) {
        this.kalibr1Obrazec = kalibr1Obrazec;
    }

    /**
     * Возвращает толщину второго образца в мм.
     *
     * @return Толщина второго образца в мм.
     */
    public float getKalibr2Obrazec() {
        return kalibr2Obrazec;
    }

    /**
     * Устанавливает толщину второго образца в мм.
     *
     * @param kalibr2Obrazec толщина второго образца в мм.
     */
    public void setKalibr2Obrazec(float kalibr2Obrazec) {
        this.kalibr2Obrazec = kalibr2Obrazec;
    }

    /**
     * Добавляет новую точку ВРЧ перед путем дублирования указанной.
     *
     * @param i Индекс указанной точки. Должен быть меньше, чем
     * {@code vrch_points}.
     */
    public void addVRCHPoint(int i) {
        if (i >= vrch_points) {
            return;
        }
        if (vrch_points < 10) {
            if (vrch_points == 0) {
                vrch_points = 1;
                vrch_gains[0] = 20;
                vrch_times[0] = 0;
                return;
            }
            for (int j = (vrch_points - 1); j >= i; j--) {
                vrch_gains[j + 1] = vrch_gains[j];
                vrch_times[j + 1] = vrch_times[j];
            }
            vrch_points++;
        }
    }

    /**
     * Удаляет указанную точку из массива ВРЧ.
     *
     * @param i индекс удаляемой точки. Должен быть меньше чем
     * {@code vrch_points}.
     */
    public void remVRCHPoint(int i) {
        if (vrch_points < 2) {
            return;
        }
        if (i < vrch_points) {
            vrch_points--;
            for (int j = i; j < vrch_points; j++) {
                vrch_gains[j] = vrch_gains[j + 1];
                vrch_times[j] = vrch_times[j + 1];
            }
        }
    }

    /**
     * Возвращает флаг возможности калибровки.
     *
     * @return {@code true} - калибровка возможна, {@code false} - калибровка
     * невозможна.
     */
    public boolean getKalibrEnable() {
        return kalibrEnable == 1;
    }

    public void setKalibrEnable(boolean kalibrEnable) {
        if (kalibrEnable) {
            if (kalibr2Value == kalibr1Value) {
                this.kalibrEnable = 0;
                return;
            }
            if (kalibr2Value == 0) {
                kalibr2Obrazec = 0.0f;
            }
            kalibrSpeed = (double) (kalibr2Obrazec - kalibr1Obrazec) / (double) (kalibr2Value - kalibr1Value);
            kalibrDelta = (double) kalibr1Obrazec - (double) kalibrSpeed * kalibr1Value;
            this.kalibrEnable = 1;
        } else {
            this.kalibrEnable = 0;
        }
    }

    /**
     * Преобразовывает толщину измеренную толщиномером в милиметры
     *
     * @param value значение времени в условных единицах.
     * @return значение толщины в мм. Если калибровка отсутствует, то
     * возвращается переданное значение.
     */
    public float getThick(float value) {
        if (kalibrEnable == 1) {
            return (float) (kalibrSpeed * value + kalibrDelta);
        }
        return value;
    }
    
    public void copyFrom(DeviceUSKUdpParam src){
        a_start = src.a_start;
        a_thresh = src.a_thresh;
        a_width = src.a_width;
        ads_offset = src.ads_offset;
        anfilter = src.anfilter;
        asd_filter = src.asd_filter;
        b_start = src.b_start;
        b_thresh = src.b_thresh;
        b_width = src.b_width;
        defect_type = src.defect_type;
        dempfer = src.dempfer;
        detector = src.detector;
        enabled = src.enabled;
        gain = src.gain;
        gzi_count = src.gzi_count;
        gzi_demp_delay = src.gzi_demp_delay;
        gzi_demp_width = src.gzi_demp_width;
        gzi_width = src.gzi_width;
        hardware_type = src.hardware_type;
        i_active = src.i_active;
        i_start = src.i_start;
        i_thresh = src.i_thresh;
        i_visible = src.i_visible;
        kalibr1Obrazec = src.kalibr1Obrazec;
        kalibr1Value = src.kalibr1Value;
        kalibr2Obrazec = src.kalibr2Obrazec;
        kalibr2Value = src.kalibr2Value;
        kalibrDelta = src.kalibrDelta;
        kalibrEnable = src.kalibrEnable;
        kalibrSpeed = src.kalibrSpeed;
        mech_pos = src.mech_pos;
        peak_mode = src.peak_mode;
        probe_mode = src.probe_mode;
        pulser_takt = src.pulser_takt;
        range = src.range;
        rvhoda = src.rvhoda;
        signal_delay = src.signal_delay;
        soglas = src.soglas;
        supression = src.supression;
        sync_delay = src.sync_delay;
        time_mode = src.time_mode;
        System.arraycopy(src.vrch_gains, 0, vrch_gains, 0, 10);
        vrch_go = src.vrch_go;
        vrch_points = src.vrch_points;
        System.arraycopy(src.vrch_times, 0, vrch_times, 0, 10);
        vrch_visible = src.vrch_visible;
        time_base_freq = src.time_base_freq;
        System.arraycopy(src.rezerv, 0, rezerv, 0, rezerv.length);
    }

    void loadFromIni(Properties ini, int ch) {
        if(ch == -1) ch = id + 1;
        kalibr1Obrazec = Float.valueOf(ini.getProperty("ch" + ch + "Kalibr1Obrazec",
                String.valueOf(kalibr1Obrazec)));
        kalibr2Obrazec = Float.valueOf(ini.getProperty("ch" + ch + "Kalibr2Obrazec",
                String.valueOf(kalibr2Obrazec)));
        kalibr1Value = Short.valueOf(ini.getProperty("ch" + ch + "Kalibr1Value",
                String.valueOf(kalibr1Value)));
        kalibr2Value = Short.valueOf(ini.getProperty("ch" + ch + "Kalibr2Value",
                String.valueOf(kalibr2Value)));
        kalibrDelta = Double.valueOf(ini.getProperty("ch" + ch + "KalibrDelta",
                String.valueOf(kalibrDelta)));
        kalibrEnable = Integer.valueOf(ini.getProperty("ch" + ch + "KalibrEnable",
                String.valueOf(kalibrEnable))).byteValue();
        kalibrSpeed = Double.valueOf(ini.getProperty("ch" + ch + "KalibrSpeed",
                String.valueOf(kalibrSpeed)));
        a_start = Integer.valueOf(ini.getProperty("ch" + ch + "a_start",
                String.valueOf(a_start))).shortValue();
        a_thresh = Integer.valueOf(ini.getProperty("ch" + ch + "a_thresh",
                String.valueOf(a_thresh))).shortValue();
        a_width = Integer.valueOf(ini.getProperty("ch" + ch + "a_width",
                String.valueOf(a_width))).shortValue();
        ads_offset = Integer.valueOf(ini.getProperty("ch" + ch + "ads_offset",
                String.valueOf(ads_offset))).shortValue();
        anfilter = Integer.valueOf(ini.getProperty("ch" + ch + "anfilter",
                String.valueOf(anfilter))).shortValue();
        b_start = Integer.valueOf(ini.getProperty("ch" + ch + "b_start",
                String.valueOf(b_start))).shortValue();
        b_thresh = Integer.valueOf(ini.getProperty("ch" + ch + "b_thresh",
                String.valueOf(b_thresh))).shortValue();
        b_width = Integer.valueOf(ini.getProperty("ch" + ch + "b_width",
                String.valueOf(b_width))).shortValue();
        defect_type = Integer.valueOf(ini.getProperty("ch" + ch + "defect_type",
                String.valueOf(defect_type))).shortValue();
        dempfer = Integer.valueOf(ini.getProperty("ch" + ch + "dempfer",
                String.valueOf(dempfer))).shortValue();
        enabled = Integer.valueOf(ini.getProperty("ch" + ch + "enabled",
                String.valueOf(enabled))).shortValue();
        gain = Integer.valueOf(ini.getProperty("ch" + ch + "gain",
                String.valueOf(gain))).shortValue();
        gzi_count = Integer.valueOf(ini.getProperty("ch" + ch + "gzi_count",
                String.valueOf(gzi_count))).shortValue();
        gzi_demp_delay = Integer.valueOf(ini.getProperty("ch" + ch + "gzi_demp_delay",
                String.valueOf(gzi_demp_delay))).shortValue();
        gzi_demp_width = Integer.valueOf(ini.getProperty("ch" + ch + "gzi_demp_width",
                String.valueOf(gzi_demp_width))).shortValue();
        gzi_width = Integer.valueOf(ini.getProperty("ch" + ch + "gzi_width",
                String.valueOf(gzi_width))).shortValue();
        hardware_type = Integer.valueOf(ini.getProperty("ch" + ch + "hardware_type",
                String.valueOf(hardware_type))).shortValue();
        i_active = Integer.valueOf(ini.getProperty("ch" + ch + "i_active",
                String.valueOf(i_active))).shortValue();
        i_start = Integer.valueOf(ini.getProperty("ch" + ch + "i_start",
                String.valueOf(i_start))).shortValue();
        i_thresh = Integer.valueOf(ini.getProperty("ch" + ch + "i_thresh",
                String.valueOf(i_thresh))).shortValue();
        i_visible = Integer.valueOf(ini.getProperty("ch" + ch + "i_visible",
                String.valueOf(i_visible))).shortValue();
        id = Integer.valueOf(ini.getProperty("ch" + ch + "id",
                String.valueOf(id)));
        mech_pos = Integer.valueOf(ini.getProperty("ch" + ch + "mech_pos",
                String.valueOf(mech_pos)));
        name = ini.getProperty("ch" + ch + "name",
                String.valueOf(name));
        peak_mode = Integer.valueOf(ini.getProperty("ch" + ch + "peak_mode",
                String.valueOf(peak_mode))).shortValue();
        probe_mode = Integer.valueOf(ini.getProperty("ch" + ch + "probe_mode",
                String.valueOf(probe_mode))).shortValue();
        pulser_takt = Integer.valueOf(ini.getProperty("ch" + ch + "pulser_takt",
                String.valueOf(pulser_takt))).shortValue();
        range = Integer.valueOf(ini.getProperty("ch" + ch + "range",
                String.valueOf(range))).shortValue();
        rvhoda = Integer.valueOf(ini.getProperty("ch" + ch + "rvhoda",
                String.valueOf(rvhoda))).shortValue();
        signal_delay = Integer.valueOf(ini.getProperty("ch" + ch + "signal_delay",
                String.valueOf(signal_delay))).shortValue();
        soglas = Integer.valueOf(ini.getProperty("ch" + ch + "soglas",
                String.valueOf(soglas))).shortValue();
        supression = Integer.valueOf(ini.getProperty("ch" + ch + "supression",
                String.valueOf(supression))).shortValue();
        sync_delay = Integer.valueOf(ini.getProperty("ch" + ch + "sync_delay",
                String.valueOf(sync_delay))).shortValue();
        time_mode = Integer.valueOf(ini.getProperty("ch" + ch + "time_mode",
                String.valueOf(time_mode))).shortValue();
        vrch_go = Integer.valueOf(ini.getProperty("ch" + ch + "vrch_go",
                String.valueOf(vrch_go))).shortValue();
        vrch_points = Integer.valueOf(ini.getProperty("ch" + ch + "vrch_points",
                String.valueOf(vrch_points))).shortValue();
        vrch_visible = Integer.valueOf(ini.getProperty("ch" + ch + "vrch_visible",
                String.valueOf(vrch_visible))).shortValue();
        time_base_freq = Integer.valueOf(ini.getProperty("ch" + ch + "time_base_freq",
                String.valueOf(time_base_freq))).shortValue();
        for (int i = 0; i < 10; i++) {
            vrch_gains[i] = Integer.valueOf(ini.getProperty("ch" + ch + "vrch_gainsPt" + i,
                    String.valueOf(vrch_gains[i]))).shortValue();
            vrch_times[i] = Integer.valueOf(ini.getProperty("ch" + ch + "vrch_timesPt" + i,
                    String.valueOf(vrch_times[i]))).shortValue();
        }
    }

    void writeToIni(Properties ini, int ch) {
        if(ch == -1) ch = id + 1;
        ini.setProperty("ch" + ch + "Kalibr1Obrazec", String.valueOf(kalibr1Obrazec));
        ini.setProperty("ch" + ch + "Kalibr2Obrazec", String.valueOf(kalibr2Obrazec));
        ini.setProperty("ch" + ch + "Kalibr1Value", String.valueOf(kalibr1Value));
        ini.setProperty("ch" + ch + "Kalibr2Value", String.valueOf(kalibr2Value));
        ini.setProperty("ch" + ch + "KalibrDelta", String.valueOf(kalibrDelta));
        ini.setProperty("ch" + ch + "KalibrEnable", String.valueOf(kalibrEnable));
        ini.setProperty("ch" + ch + "KalibrSpeed", String.valueOf(kalibrSpeed));
        ini.setProperty("ch" + ch + "a_start", String.valueOf(a_start));
        ini.setProperty("ch" + ch + "a_thresh", String.valueOf(a_thresh));
        ini.setProperty("ch" + ch + "a_width", String.valueOf(a_width));
        ini.setProperty("ch" + ch + "ads_offset", String.valueOf(ads_offset));
        ini.setProperty("ch" + ch + "anfilter", String.valueOf(anfilter));
        ini.setProperty("ch" + ch + "b_start", String.valueOf(b_start));
        ini.setProperty("ch" + ch + "b_thresh", String.valueOf(b_thresh));
        ini.setProperty("ch" + ch + "b_width", String.valueOf(b_width));
        ini.setProperty("ch" + ch + "defect_type", String.valueOf(defect_type));
        ini.setProperty("ch" + ch + "dempfer", String.valueOf(dempfer));
        ini.setProperty("ch" + ch + "enabled", String.valueOf(enabled));
        ini.setProperty("ch" + ch + "gain", String.valueOf(gain));
        ini.setProperty("ch" + ch + "gzi_count", String.valueOf(gzi_count));
        ini.setProperty("ch" + ch + "gzi_demp_delay", String.valueOf(gzi_demp_delay));
        ini.setProperty("ch" + ch + "gzi_demp_width", String.valueOf(gzi_demp_width));
        ini.setProperty("ch" + ch + "gzi_width", String.valueOf(gzi_width));
        ini.setProperty("ch" + ch + "hardware_type", String.valueOf(hardware_type));
        ini.setProperty("ch" + ch + "i_active", String.valueOf(i_active));
        ini.setProperty("ch" + ch + "i_start", String.valueOf(i_start));
        ini.setProperty("ch" + ch + "i_thresh", String.valueOf(i_thresh));
        ini.setProperty("ch" + ch + "i_visible", String.valueOf(i_visible));
        ini.setProperty("ch" + ch + "id", String.valueOf(id));
        ini.setProperty("ch" + ch + "mech_pos", String.valueOf(mech_pos));
        ini.setProperty("ch" + ch + "name", name);
        ini.setProperty("ch" + ch + "peak_mode", String.valueOf(peak_mode));
        ini.setProperty("ch" + ch + "probe_mode", String.valueOf(probe_mode));
        ini.setProperty("ch" + ch + "pulser_takt", String.valueOf(pulser_takt));
        ini.setProperty("ch" + ch + "range", String.valueOf(range));
        ini.setProperty("ch" + ch + "rvhoda", String.valueOf(rvhoda));
        ini.setProperty("ch" + ch + "signal_delay", String.valueOf(signal_delay));
        ini.setProperty("ch" + ch + "soglas", String.valueOf(soglas));
        ini.setProperty("ch" + ch + "supression", String.valueOf(supression));
        ini.setProperty("ch" + ch + "sync_delay", String.valueOf(sync_delay));
        ini.setProperty("ch" + ch + "time_mode", String.valueOf(time_mode));
        ini.setProperty("ch" + ch + "vrch_go", String.valueOf(vrch_go));
        ini.setProperty("ch" + ch + "vrch_points", String.valueOf(vrch_points));
        ini.setProperty("ch" + ch + "vrch_visible", String.valueOf(vrch_visible));
        ini.setProperty("ch" + ch + "time_base_freq", String.valueOf(time_base_freq));
        for (int i = 0; i < 10; i++) {
            ini.setProperty("ch" + ch + "vrch_gainsPt" + i, String.valueOf(vrch_gains[i]));
            ini.setProperty("ch" + ch + "vrch_timesPt" + i, String.valueOf(vrch_times[i]));
        }
    }

    @Override
    public String toString() {
        return name; 
    }

    public final void resetToDefault() {

        peak_mode = 0;
        probe_mode = 1;
        vrch_gains[0] = 0;
        vrch_times[0] = 898;
        vrch_gains[1] = 40;
        vrch_times[1] = 1193;
        vrch_gains[2] = 60;
        vrch_times[2] = 4000;
        vrch_gains[3] = 80;
        vrch_times[3] = 5000;
        time_base_freq = 2500;
        
        if(hardware_type == TIME_CONTROL){
            vrch_points = 5;
            vrch_gains[0] = 0;
            vrch_times[0] = 898;
            vrch_gains[1] = 8;
            vrch_times[1] = 959;
            vrch_gains[2] = 28;
            vrch_times[2] = 1193;
            vrch_gains[3] = 50;
            vrch_times[3] = 1602;
            vrch_gains[4] = 59;
            vrch_times[4] = 2502;
            vrch_visible = 1;
            vrch_go = 1;
            
            gzi_width = 19;
            gzi_count = 2;
            soglas = 0;
            
            a_start = 1102;
            a_width = 840;
            a_thresh = 50;

            b_start = 1102;
            b_width = 1217;
            b_thresh = 38;

            i_start = 1115;
            i_thresh = 20;
            i_visible = 1;
            i_active = 1;
            
            gain = 10;
            range = 4250;
            signal_delay = 2498;
            time_mode = 2;
        }
    }
}
