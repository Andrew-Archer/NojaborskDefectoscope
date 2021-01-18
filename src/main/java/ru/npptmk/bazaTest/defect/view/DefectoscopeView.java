/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.view;

import com.ghgande.j2mod.modbus.ModbusException;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import ru.npptmk.bazaTest.defect.MainFrame;

/**
 *
 * @author RazumnovAA
 */
public class DefectoscopeView extends JPanel {

    public static void isClosedByOperator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private MainFrame mainFrame;

    /**
     * Creates new form DefectoscopeView
     * @param mainFrame
     */
    public DefectoscopeView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    public void updateView(JPanel panel) {
        try {
            for (Component component : panel.getComponents()) {
                if (component instanceof JPanel) {//Рекурсия
                    updateView((JPanel) component);
                }
                if (component instanceof JCheckBox){
                    
                }
                if (component instanceof JLabel) {
                    ((JLabel) component).setVisible(
                            mainFrame.plc.getOuputState(component.getName()));
                }
                if (component instanceof JToggleButton) {
                    ((JToggleButton) component).setSelected(
                            mainFrame.plc.getOuputState(component.getName()));
                }
            }
        } catch (ModbusException ex) {
            JOptionPane.showMessageDialog(panel,
                    "Сообщение: " + ex.getMessage() + "\n"
                    + "Причина: " + ex.getCause().toString());
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Методы управления визуализацией установки">
    //</editor-fold>
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        holder1Label = new javax.swing.JLabel();
        MagnetizingLabel = new javax.swing.JLabel();
        demagnetizerLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        DryerLabel = new javax.swing.JLabel();
        wetterLabel = new javax.swing.JLabel();
        carriageLabel = new javax.swing.JLabel();
        holder3Label = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        reloader2 = new javax.swing.JLabel();
        reloader_1_Label = new javax.swing.JLabel();
        engine2Label = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        engine1Label = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1020, 198));
        setMinimumSize(new java.awt.Dimension(1020, 198));
        setPreferredSize(new java.awt.Dimension(1020, 198));
        setLayout(null);

        jCheckBox11.setText("SQ11_Tube_On_Out_Rolgang");
        jCheckBox11.setToolTipText("");
        jCheckBox11.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox11.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox11.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox11.setName("SQ11_Tube_On_Out_Rolgang"); // NOI18N
        add(jCheckBox11);
        jCheckBox11.setBounds(790, 170, 170, 23);

        jCheckBox10.setText("SQ10_Nube_left_Device");
        jCheckBox10.setToolTipText("");
        jCheckBox10.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox10.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox10.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox10.setName("SQ10_Nube_left_Device"); // NOI18N
        add(jCheckBox10);
        jCheckBox10.setBounds(810, 10, 160, 23);

        jCheckBox9.setText("SQ9_High_Water_Level");
        jCheckBox9.setToolTipText("");
        jCheckBox9.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox9.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox9.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox9.setName("SQ9_High_Water_Level"); // NOI18N
        add(jCheckBox9);
        jCheckBox9.setBounds(320, 20, 160, 23);

        jCheckBox8.setText("SQ8_Holder_3_Up");
        jCheckBox8.setToolTipText("");
        jCheckBox8.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox8.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox8.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox8.setName("SQ8_Holder_3_Up"); // NOI18N
        add(jCheckBox8);
        jCheckBox8.setBounds(590, 160, 160, 23);

        jCheckBox7.setText("SQ7_Holder_2_Up");
        jCheckBox7.setToolTipText("");
        jCheckBox7.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox7.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox7.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox7.setName("SQ7_Holder_2_Up"); // NOI18N
        add(jCheckBox7);
        jCheckBox7.setBounds(530, 140, 160, 23);

        jCheckBox6.setText("SQ6_Tube_Before_USK");
        jCheckBox6.setToolTipText("");
        jCheckBox6.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox6.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox6.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox6.setName("SQ6_Tube_Before_USK"); // NOI18N
        add(jCheckBox6);
        jCheckBox6.setBounds(400, 170, 160, 23);

        jCheckBox5.setText("SQ5_Calibration_End");
        jCheckBox5.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox5.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox5.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox5.setName("SQ5_Calibration_End"); // NOI18N
        add(jCheckBox5);
        jCheckBox5.setBounds(240, 140, 160, 23);

        jCheckBox4.setText("SQ4_Holder_1_Up");
        jCheckBox4.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox4.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox4.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox4.setName("SQ4_Holder_1_Up"); // NOI18N
        add(jCheckBox4);
        jCheckBox4.setBounds(270, 170, 160, 23);

        jCheckBox3.setText("SQ3_Calibration_Start");
        jCheckBox3.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox3.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox3.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox3.setName("SQ3_Calibration_Start"); // NOI18N
        add(jCheckBox3);
        jCheckBox3.setBounds(80, 170, 160, 23);

        jCheckBox2.setText("SQ2_Holder_1_Must_Go_ Up");
        jCheckBox2.setDisabledIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox2.setDisabledSelectedIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\Lamp.png")); // NOI18N
        jCheckBox2.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox2.setName("SQ2_Holder_1_Must_Go_ Up"); // NOI18N
        add(jCheckBox2);
        jCheckBox2.setBounds(90, 20, 160, 23);

        jCheckBox1.setBackground(new java.awt.Color(255, 0, 51));
        jCheckBox1.setText("SQ1_Tube_On_In_Rollgang");
        jCheckBox1.setIcon(new javax.swing.ImageIcon("C:\\Users\\RazumnovAA\\Pictures\\УЗМДДФ\\LampON.png")); // NOI18N
        jCheckBox1.setName("SQ1_Tube_On_In_Rollgang"); // NOI18N
        jCheckBox1.setOpaque(false);
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        add(jCheckBox1);
        jCheckBox1.setBounds(90, 110, 159, 23);

        holder1Label.setBackground(new java.awt.Color(153, 255, 102));
        holder1Label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Holder1.png"))); // NOI18N
        holder1Label.setText("jLabel3");
        holder1Label.setName("holder1"); // NOI18N
        add(holder1Label);
        holder1Label.setBounds(271, 0, 41, 84);

        MagnetizingLabel.setBackground(new java.awt.Color(153, 255, 102));
        MagnetizingLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Magnetizing1.png"))); // NOI18N
        MagnetizingLabel.setText("jLabel5");
        MagnetizingLabel.setMaximumSize(new java.awt.Dimension(54, 59));
        MagnetizingLabel.setMinimumSize(new java.awt.Dimension(54, 59));
        MagnetizingLabel.setOpaque(true);
        MagnetizingLabel.setPreferredSize(new java.awt.Dimension(54, 59));
        add(MagnetizingLabel);
        MagnetizingLabel.setBounds(341, 61, 54, 59);

        demagnetizerLabel.setBackground(new java.awt.Color(153, 255, 102));
        demagnetizerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Demagnetizing.png"))); // NOI18N
        demagnetizerLabel.setText("Demagnetizer");
        demagnetizerLabel.setMaximumSize(new java.awt.Dimension(50, 32));
        demagnetizerLabel.setMinimumSize(new java.awt.Dimension(50, 32));
        demagnetizerLabel.setPreferredSize(new java.awt.Dimension(50, 32));
        add(demagnetizerLabel);
        demagnetizerLabel.setBounds(474, 71, 50, 32);

        jLabel8.setBackground(new java.awt.Color(153, 255, 102));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Engine3.png"))); // NOI18N
        jLabel8.setText("jLabel8");
        jLabel8.setMaximumSize(new java.awt.Dimension(54, 33));
        jLabel8.setMinimumSize(new java.awt.Dimension(54, 33));
        jLabel8.setPreferredSize(new java.awt.Dimension(54, 33));
        add(jLabel8);
        jLabel8.setBounds(523, 84, 54, 33);

        jLabel9.setBackground(new java.awt.Color(153, 255, 102));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Holder2.png"))); // NOI18N
        jLabel9.setText("jLabel9");
        jLabel9.setMaximumSize(new java.awt.Dimension(31, 84));
        jLabel9.setMinimumSize(new java.awt.Dimension(31, 84));
        jLabel9.setPreferredSize(new java.awt.Dimension(31, 84));
        add(jLabel9);
        jLabel9.setBounds(534, 0, 31, 84);

        DryerLabel.setBackground(new java.awt.Color(153, 255, 102));
        DryerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Dryer.png"))); // NOI18N
        DryerLabel.setText("Dryer");
        add(DryerLabel);
        DryerLabel.setBounds(662, 69, 35, 32);

        wetterLabel.setBackground(new java.awt.Color(153, 255, 102));
        wetterLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Wetter.png"))); // NOI18N
        wetterLabel.setText("Wetter");
        wetterLabel.setOpaque(true);
        add(wetterLabel);
        wetterLabel.setBounds(576, 70, 29, 31);

        carriageLabel.setBackground(new java.awt.Color(153, 255, 102));
        carriageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Carriage.png"))); // NOI18N
        carriageLabel.setText("Carriage");
        add(carriageLabel);
        carriageLabel.setBounds(605, 72, 58, 42);

        holder3Label.setBackground(new java.awt.Color(153, 255, 102));
        holder3Label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Holder3.png"))); // NOI18N
        holder3Label.setText("Holder3");
        add(holder3Label);
        holder3Label.setBounds(734, 0, 36, 84);

        jLabel14.setBackground(new java.awt.Color(153, 255, 102));
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Engine4.png"))); // NOI18N
        jLabel14.setText("jLabel14");
        jLabel14.setMaximumSize(new java.awt.Dimension(58, 27));
        jLabel14.setMinimumSize(new java.awt.Dimension(58, 27));
        jLabel14.setPreferredSize(new java.awt.Dimension(58, 27));
        add(jLabel14);
        jLabel14.setBounds(723, 84, 58, 27);

        reloader2.setBackground(new java.awt.Color(153, 255, 102));
        reloader2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Reloader 2.png"))); // NOI18N
        reloader2.setText("jLabel15");
        add(reloader2);
        reloader2.setBounds(805, 84, 142, 56);

        reloader_1_Label.setBackground(new java.awt.Color(153, 255, 102));
        reloader_1_Label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Reloader1.png"))); // NOI18N
        reloader_1_Label.setText("Reloader1");
        reloader_1_Label.setMaximumSize(new java.awt.Dimension(151, 198));
        reloader_1_Label.setMinimumSize(new java.awt.Dimension(151, 198));
        reloader_1_Label.setName("Holder1"); // NOI18N
        reloader_1_Label.setPreferredSize(new java.awt.Dimension(151, 198));
        add(reloader_1_Label);
        reloader_1_Label.setBounds(113, 75, 145, 120);

        engine2Label.setBackground(new java.awt.Color(153, 255, 102));
        engine2Label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Engine2.png"))); // NOI18N
        engine2Label.setText("jLabel7");
        engine2Label.setMaximumSize(new java.awt.Dimension(53, 178));
        engine2Label.setMinimumSize(new java.awt.Dimension(53, 178));
        engine2Label.setOpaque(true);
        engine2Label.setPreferredSize(new java.awt.Dimension(53, 178));
        add(engine2Label);
        engine2Label.setBounds(419, 76, 56, 54);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Small.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        add(jLabel1);
        jLabel1.setBounds(0, 0, 1020, 198);

        engine1Label.setBackground(new java.awt.Color(153, 255, 102));
        engine1Label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/Engine1.png"))); // NOI18N
        engine1Label.setText("jLabel4");
        engine1Label.setMaximumSize(new java.awt.Dimension(67, 114));
        engine1Label.setMinimumSize(new java.awt.Dimension(67, 114));
        engine1Label.setPreferredSize(new java.awt.Dimension(67, 114));
        add(engine1Label);
        engine1Label.setBounds(260, 84, 67, 114);
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel DryerLabel;
    private javax.swing.JLabel MagnetizingLabel;
    private javax.swing.JLabel carriageLabel;
    private javax.swing.JLabel demagnetizerLabel;
    private javax.swing.JLabel engine1Label;
    private javax.swing.JLabel engine2Label;
    private javax.swing.JLabel holder1Label;
    private javax.swing.JLabel holder3Label;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel reloader2;
    private javax.swing.JLabel reloader_1_Label;
    private javax.swing.JLabel wetterLabel;
    // End of variables declaration//GEN-END:variables
}
