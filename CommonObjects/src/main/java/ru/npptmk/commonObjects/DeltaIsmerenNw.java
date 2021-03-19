/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.commonObjects;

import java.util.Arrays;

/**
 * Статистическая обработка результатов измерения <br> Используется для
 * работы со множествеными в расчетный интервал значениями, например резульлтаты 
 * пришедшие по УДП протоколоу от устройств КРОПУС <br> Объект работает по следующему
 * алгоритму:<ol><li> Находим среднее значение из массива
 * <li> Для этих значений считаем стандартное отклонение
 * <li> Из массива всех значений отбрасываем значения отклонение от среднего в
 * которых превышает три стандартных отклнения повторяем пункты 1, 2 и 3 пока
 * отбрасывать будет нечего <li> рассчитать доверительный интервал </ol>
 * Есть возможность произвести добавление пришедших значений в массив для обработки
 * методом {@code addItem()}. <br>
 * Все вычисления происходят при вызове метода {@code calculate()}, при этом следует
 * учесть, что метод {@code addItem()} будет блокирован.<br>
 * Результаты вычисления содеожатся в переменных {@code srSnach} и {@code dovIntrv}.
 *
 * @author SmorkalovAV
 */
public class DeltaIsmerenNw {
    /**
     * Значение доверительной вероятности 0.8
     */
    public static final int DOVVER0_8 = 0;
    /**
     * Значение доверительной вероятности 0.9
     */
    public static final int DOVVER0_9 = 1;
   /**
    *  Значение доверительной вероятности 0.95
    */
    public static final int DOVVER0_95 = 2;
    /**
     *  Значение доверительной вероятности 0.98
     */
    public static final int DOVVER0_98 = 3;
    /**
     *  Значение доверительной вероятности 0.99
     */
    public static final int DOVVER0_99 = 4;
    /**
     * Вычисленное среднее значение.
     */
    public float srSnach = -1;        //среднее значение замеров
    /**
     * Вычисленный доверительный интервал.
     */
    public float dovIntrv = -1;     //доверительных интервал
    /**Уровень значений, ниже которых игнорируем, 0 - используем все*/
    public float noise = 0;
    private float sKv = 0;                  //сумма квадратов разностей
    private int coSm;    //количество значений для интервала
    private double smSm;    //сумма замеров для интервала
    /**индекс в хранилище данных*/
    private int indTmD;
    private short[] tmpData;
    private int dovVer = 4;
    private boolean fill = false;
    /**
     * Создание объекта для доверительной вероятности 0.99, уровень
     * игнорируемых значений 0, размер буфера промежуточных значений 5000.
     */
    public DeltaIsmerenNw() {
        this(0,DOVVER0_99,5000);    
    }
    /**
     *  Создание объекта для доверительной вероятности 0.99, заданного уровеня
     * игнорируемых значений, размер буфера промежуточных значений 5000.
     * @param noise уровень ниже которого игнорируются значения.
     */
    public DeltaIsmerenNw(float noise) {
        this(noise,DOVVER0_99,5000);
    }
    /**
     * Создание объекта для заданной доверительной вероятности и уровня
     * игнорируемых значений, с размером буфера 5000.
     * @param noise порог игнорируемых значений.
     * @param dVer доверительная вероятность, одно из:
     * DOVVER0_8, DOVVER0_9, DOVVER0_95, DOVVER0_98, DOVVER0_99. 
     */
    public DeltaIsmerenNw(float noise, int dVer) {
        this(noise, dVer, 5000);
    }
    
    /**
     * Конструктор с заданием всех параметров объекта.
     * @param noise порог игнорируемыъх значений.
     * @param dVer доверительная вероятность, одно из:
     * DOVVER0_8, DOVVER0_9, DOVVER0_95, DOVVER0_98, DOVVER0_99.
     * @param tmpsize размер буфера для накопления результатов.
     */
    public DeltaIsmerenNw(float noise, int dVer, int tmpsize ){
        this.noise = noise;
        tmpData = new short[tmpsize];
        this.dovVer = dVer;
    }
    
    /**
     * Коэффициенты Стьюдента 1 изменение - колич. изменений, 30 - для больше 30
     * 2 изменение - доверительная вероятность: 0,8 0,9 0,95 0,98 0,99
     */
    static double stj[][] = {
        {3.078, 6.314, 12.706, 31.821, 63.656},
        {1.886, 2.92, 4.303, 6.965, 9.925},
        {1.638, 2.353, 3.182, 4.541, 5.841},
        {1.533, 2.132, 2.776, 3.747, 4.604},
        {1.476, 2.015, 2.571, 3.365, 4.032},
        {1.440, 1.943, 2.447, 3.143, 3.707},
        {1.415, 1.895, 2.365, 2.998, 3.499},
        {1.397, 1.860, 2.306, 2.896, 3.355},
        {1.383, 1.833, 2.262, 2.821, 3.250},
        {1.372, 1.812, 2.228, 2.764, 3.169},
        {0.540, 1.796, 2.201, 2.718, 3.106},
        {1.356, 1.782, 2.179, 2.681, 3.055},
        {1.350, 1.771, 2.160, 2.650, 3.012},
        {1.345, 1.761, 2.145, 2.624, 2.977},
        {1.341, 1.753, 2.131, 2.602, 2.947},
        {1.337, 1.746, 2.120, 2.583, 2.921},
        {1.333, 1.740, 2.110, 2.567, 2.898},
        {1.330, 1.734, 2.101, 2.552, 2.878},
        {1.328, 1.729, 2.093, 2.539, 2.861},
        {1.325, 1.725, 2.086, 2.528, 2.845},
        {1.323, 1.721, 2.080, 2.518, 2.831},
        {1.321, 1.717, 2.074, 2.508, 2.819},
        {1.319, 1.714, 2.069, 2.500, 2.807},
        {1.318, 1.711, 2.064, 2.492, 2.707},
        {1.316, 1.708, 2.060, 2.485, 2.787},
        {1.315, 1.706, 2.056, 2.479, 2.779},
        {1.314, 1.307, 2.052, 2.473, 2.771},
        {1.313, 1.701, 2.048, 2.467, 2.763},
        {1.311, 1.699, 2.045, 2.462, 2.756},
        {1.310, 1.697, 2.42, 2.457, 2.750},
        {1.2816, 1.6449, 1.600, 2.3263, 2.5758}};
    int limU;
    int limD;

    /**
     * Внутренний метод.
     * Производит расчет среднего и мат. погрешности, по всем элементам
     * массива, вычисления происходят до тех пор пока количество учавствующих
     * элементов не будут уменшаться
     * @param mas массив значений
     * @param lU верхняя граница обрабатываемых значений
     * @param lD нижняя граница обрабатываемых значений
     * @param st индекс в массиве начала вычислений
     * @param en индекс в массиве окончания вычислений
     * @return да при удачном вычислении
     */
    private boolean calculate(short[] mas, int lU, int lD, int st, int en) {
        int coPrSm = 0;
        double stO;
        double tm;
        do {
            smSm = 0;
            coSm = 0;
            for (int i = st; i < en; i++) {
                if (mas[i] < lU && mas[i] > lD) {
                    smSm += mas[i];
                    coSm++;
                }
            }
            if (coSm == 0) {
                return false;
            }
            srSnach = (float) (smSm / coSm);
            sKv = 0;
            for (int i = st; i < en; i++) {
                if (mas[i] < lU && mas[i] > lD) {
                    tm = mas[i] - srSnach;
                    sKv += (tm * tm);
                }
            }
            stO = Math.sqrt(sKv / (coSm - 1));
            if (coPrSm == coSm) {
                break;
            }
            limD = (int) (srSnach - stO * 3);
            limU = (int) (srSnach + stO * 3);
            lD = limD;
            lU = limU;
            if (lD == lU) {
                break;
            }
            coPrSm = coSm;
        } while (true);
        dovIntrv = (float) (stj[coSm > 30 ? 30 : coSm - 1][dovVer] * Math.sqrt(sKv / (coSm * (coSm - 1))));
        return true;
    }
    
    /**
     * Добавляем данные для расчета при переполнении массив начинает заполняться
     * заново сохраняя старые значения.
     * @param res_count количество добавляемых данных
     * @param result массив результатов
     */
    public synchronized void addItem(int res_count, short [] result) {
        if (indTmD + res_count > tmpData.length) {
            int addSize = indTmD + res_count - tmpData.length;
            System.arraycopy(result, 0, tmpData, indTmD, res_count - addSize);
            result = Arrays.copyOfRange(result, res_count - addSize, res_count);
            res_count = result.length;
            fill = true;
            indTmD = 0;//переполнение
        }
        System.arraycopy(result, 0, tmpData, indTmD, res_count);
        indTmD += res_count;
        if(indTmD == tmpData.length){
            indTmD = 0;
            fill = true;
        }
    }

    /**
     * вызывается когда необходимо рассчитать значения
     */
    public synchronized void calculate() {
        dovIntrv = srSnach = -1;
        if(fill){
            indTmD = tmpData.length;
        }
        fill = false;
        calculate(tmpData, 0xffff, (int) noise, 0, indTmD);
        indTmD = 0; Arrays.fill(tmpData, (short)0);
    }
}
