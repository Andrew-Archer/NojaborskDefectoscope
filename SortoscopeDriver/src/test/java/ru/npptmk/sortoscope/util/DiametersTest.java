/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.sortoscope.util;

import ru.npptmk.sortoscope.model.Diameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import ru.npptmk.sortoscope.model.Diameter;
import ru.npptmk.sortoscope.model.DiametersValues;
import ru.npptmk.sortoscope.model.DurabilityGroups;

/**
 *
 * @author RazumnovAA
 */
public class DiametersTest {

    /**
     * Test of getAsArrayOfShort method, of class Diameters.
     */
    @Test
    @Ignore
    public void testGetAsArrayOfShort() {
        //Ожидаемый результат массив чисел от 1 до 41 включительно
        short[] expectedResult = new short[41];
        //текущим диаметром будет 1 так как не один из 
        //диаметров не установлен как текущий.
        expectedResult[0] = 1;
        for (int i = 1; i < expectedResult.length; i++) {
            expectedResult[i] = (short) (i);
        }

        //Создаем 5 тестовых настроек диаметров
        List<Diameter> diametersList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            //Устанавливаем частоту замеров
            diametersList.add(new Diameter(DiametersValues.values()[i - 1], (short) i));
            //Устанавливаем порог осутствия трубы
            diametersList.get(i - 1).setNoTubeSignal((short) (i + 5));
            //Устанавливаем порорги групп прочности
            for (DurabilityGroups durabilityGroup : DurabilityGroups.values()) {
                diametersList
                        .get(i - 1)
                        .getDurabilityGroupsSignals()
                        .getGroupsSignalsMap()
                        .put(
                                durabilityGroup,
                                (short) (i + 10 + (i - 1) * DurabilityGroups.values().length + durabilityGroup.ordinal()));
            }
        }

        //Фактический результат
        short[] factResult = Diameters.getAsArrayOfShort(diametersList);
        
        //Вывод для сравнения массивов
        System.out.println("#, Expected, Actual");
        for (int i = 0; i < 41; i++){
            System.out.println(i + ", " + expectedResult[i] + "," + factResult[i]);            
        }
       

        assertTrue(
                "Expected: " + expectedResult.toString() + "\n"
                + "Actually have got: " + factResult.toString() + "\n",
                Arrays.equals(expectedResult, factResult));
    }

}
