/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.commonObjects;

/**
 * Класс расширяет возможности базового класса расчетом минимума и максимума из
 * добавленных значений для одного канала.
 *
 * @author SmorkalovAV
 */
public class DeltaIsmerenMinMax extends DeltaIsmerenNw {

    public int min; //Минимальное значение из поступивших.
    public int max; //Максимальное значение из поступивших.

    public DeltaIsmerenMinMax() {
        super();
        reset();
    }

    public DeltaIsmerenMinMax(float noise) {
        super(noise);
        reset();
    }

    public DeltaIsmerenMinMax(float noise, int dVer) {
        super(noise, dVer);
        reset();
    }

    public DeltaIsmerenMinMax(float noise, int dVer, int tmpsize) {
        super(noise, dVer, tmpsize);
        reset();
    }

    /**
     * Сбрасывает значения мин и макс на исходные. Эта инициализация требуется
     * перед началом нового цикла обработки результатов.
     */
    public final void reset() {
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
    }

    /**
     * Обновляет минимум и максимум в соответствии с принятым значением
     *
     * @param val принятое значение
     */
    public void addMinMax(int val) {
        if (min > val) {
            min = val;
        }
        if (max < val) {
            max = val;
        }
    }

    /**
     * Сброс значений перед началом обработки новой точки.<br>
     * Этот метод используется для получения последовательности связанных между
     * собой интервалов. В этом случае максимальное значение следующей точки
     * иниуиализируется минимальным значением предыдущей, а минимальное значение
     * следующей точки инициализируется максимумом предыдущей.<br>
     * В этом случае при любом наборе поступающих значений интервалы (min,max)
     * последовательных точке имеют хотябы одно совпадающее значение. Это
     * позволяет строить на самописцах более удобные для восприятия картинки.
     */
    public void resetCont() {
        //Если мин и максимум были инициализированы
        if (min != Integer.MAX_VALUE && max != Integer.MIN_VALUE) {
            int prevMax = max;
            max = min;
            min = prevMax;
        }
    }

}
