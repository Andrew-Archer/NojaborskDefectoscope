package ru.npptmk.sortoscope.main;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RazumnovAA
 */
public class Main {

    public static void main(String[] arg) {
        SortoscopeDriver driver = new Sortoscope4Driver("192.168.0.199", 13120);
        ((Sortoscope4Driver)driver).enableDebuggingLog();
        try {
            /*Diameter diameter = new Diameter(DiametersValues.D73мм, (short)-3000);
            diameter.getDurabilityGroupsSignals().getGroupsSignalsMap().put(DurabilityGroups.Д, (short)0);
                    diameter.getDurabilityGroupsSignals().getGroupsSignalsMap().put(DurabilityGroups.К, (short)-580);
                    diameter.getDurabilityGroupsSignals().getGroupsSignalsMap().put(DurabilityGroups.Е, (short)-730);
                    diameter.getDurabilityGroupsSignals().getGroupsSignalsMap().put(DurabilityGroups.Л, (short)-980);
                    diameter.getDurabilityGroupsSignals().getGroupsSignalsMap().put(DurabilityGroups.М, (short)-1190);
                    diameter.getDurabilityGroupsSignals().getGroupsSignalsMap().put(DurabilityGroups.Р, (short)-1260);
                    diameter.setMesurmentFrequency((short)16);
                    diameter.setNoTubeSignal((short)-3000);
            driver.setDiameterParameters(diameter);
            JOptionPane.showMessageDialog(null, "Новые параметры отправлены в сортоскоп.", "Уведомление", INFORMATION_MESSAGE);*/
            driver.getDurabilityGroup();
        } catch (NoMeasurementsReadyException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
