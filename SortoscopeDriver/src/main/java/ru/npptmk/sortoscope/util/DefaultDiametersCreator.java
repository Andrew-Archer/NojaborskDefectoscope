/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.sortoscope.util;

import ru.npptmk.sortoscope.model.Diameter;
import ru.npptmk.sortoscope.model.DiametersValues;
import ru.npptmk.sortoscope.model.DurabilityGroups;

public class DefaultDiametersCreator {

    public static Diameter createByDiameter(DiametersValues diameter) {
        //Создаем новый пустой диаметр
        Diameter diameterToReturn = new Diameter();
        diameterToReturn.setNoTubeSignal((short) -3000);
        diameterToReturn.setMesurmentFrequency((short) -16);
        switch (diameter) {
            case D60мм:
                diameterToReturn.setDiameter(DiametersValues.D60мм);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Д, (short) -373);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Е, (short) -472);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.К, (short) -808);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Л, (short) -1027);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) -1151);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) -1606);
                break;
            case D73мм:
                diameterToReturn.setDiameter(DiametersValues.D73мм);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Д, (short) 14);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Е, (short) -92);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.К, (short) -453);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Л, (short) -688);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) -820);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) -1309);
                break;
            case D89мм:
                diameterToReturn.setDiameter(DiametersValues.D89мм);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Д, (short) 563);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Е, (short) 469);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.К, (short) 147);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Л, (short) -63);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) -181);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) -618);
                break;
            case D102мм:
                diameterToReturn.setDiameter(DiametersValues.D102мм);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Д, (short) 877);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Е, (short) 755);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.К, (short) 262);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Л, (short) -2);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) -116);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) -640);
                break;
            case D114мм:
                diameterToReturn.setDiameter(DiametersValues.D114мм);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Д, (short) 1902);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Е, (short) 1731);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.К, (short) 1056);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.Л, (short) 702);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) 541);
                diameterToReturn.setDurabilityGropSignalValue(DurabilityGroups.М, (short) -208);
                break;
        }
        return diameterToReturn;
    }

}
