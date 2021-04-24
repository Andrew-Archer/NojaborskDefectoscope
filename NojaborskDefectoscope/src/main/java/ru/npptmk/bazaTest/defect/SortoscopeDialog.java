/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import ru.npptmk.bazaTest.defect.Util.ProgressDialog;
import javax.swing.SwingWorker;
import ru.npptmk.sortoscope.main.Sortoscope4Driver;
import ru.npptmk.sortoscope.main.SortoscopeDriver;
import ru.npptmk.sortoscope.model.Diameter;
import ru.npptmk.sortoscope.model.Diameters;
import ru.npptmk.sortoscope.model.DurabilityGroups;

/**
 *
 * @author RazumnovAA
 */
public class SortoscopeDialog extends javax.swing.JDialog {

    /**
     * Creates new form SortoscopeDialog
     */
    public SortoscopeDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        driver = new Sortoscope4Driver();
        initComponents();
        progressDialog = new ProgressDialog(null);
    }

    private final ProgressDialog progressDialog;
    private TubeType currentTubeType = new TubeType(0l, "", 0, 0);

    private boolean saveSettings;
    private final SortoscopeDriver driver;

    private Diameter clonnedSortospoeSettings;

    /**
     * Позаывает диалог настройки сортоскопа.
     *
     * @param visible true - видимый, false - невидимый
     * @param aTubeType текущий тип трубы, установленный вызывающем блоке кода.
     */
    public void setVisible(
            boolean visible,
            TubeType aTubeType) {
        setLocationRelativeTo(null);
        currentTubeType = aTubeType;
        //Клонируем настройки сортоскопа
        if (currentTubeType.getSortoscopeSettings() == null) {
            clonnedSortospoeSettings = new Diameter(Diameters.mmToDiametersValues(currentTubeType.getDiameter()));
        } else {
            //Проверяем, не заполненены ли все заначения 0ми
            if (currentTubeType.getSortoscopeSettings().isAllFieldZero()) {
                clonnedSortospoeSettings = new Diameter(Diameters.mmToDiametersValues(currentTubeType.getDiameter()));
            } else {
                clonnedSortospoeSettings = Utill.getClone(currentTubeType.getSortoscopeSettings());
            }
        }

        //Выводим тип трубы
        label_tubeType.setText(currentTubeType.toString());
        clonnedSortospoeSettings
                .setDiameter(Diameters.mmToDiametersValues(currentTubeType.getDiameter()));

        //Показываем текущую частоту замеров для сортоскопа
        textFIeld_FrequencyValue.setText(String.valueOf(clonnedSortospoeSettings
                .getMesurmentFrequency()));

        //Показываем значения сигналов для групп прочности
        for (int i = 0; i < DurabilityGroups.values().length; i++) {
            table_GroupsSignals.setValueAt(
                    clonnedSortospoeSettings
                            .getDurabilityGroupsSignals()
                            .getGroupsSignalsMap()
                            .get(DurabilityGroups.values()[i]),
                    i,
                    1
            );
        }

        //Показываем значение сигнала соостветствующего отсутствие трубы
        table_GroupsSignals.setValueAt(
                clonnedSortospoeSettings.getNoTubeSignal(),
                6,
                1
        );
        //Показываем диалог
        setVisible(visible);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sortoscopeCaption = new javax.swing.JLabel();
        diameterLabel = new javax.swing.JLabel();
        frequencyLabel = new javax.swing.JLabel();
        textFIeld_FrequencyValue = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_GroupsSignals = new javax.swing.JTable();
        button_SaveSettings = new javax.swing.JButton();
        label_tubeType = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(10, 10));
        setResizable(false);

        sortoscopeCaption.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        sortoscopeCaption.setText("НАСТРОЙКА СОРТОСКОПА");

        diameterLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        diameterLabel.setText("Тип трубы:");

        frequencyLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        frequencyLabel.setText("Частота проведения замеров в Гц:");

        textFIeld_FrequencyValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        table_GroupsSignals.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 18));
        table_GroupsSignals.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        table_GroupsSignals.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Д", null},
                {"К", null},
                {"Е", null},
                {"Л", null},
                {"М", null},
                {"Р", null},
                {"Нет трубы", null}
            },
            new String [] {
                "Группа", "Уровень сигнала"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Short.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_GroupsSignals.setRowHeight(28);
        table_GroupsSignals.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(table_GroupsSignals);
        if (table_GroupsSignals.getColumnModel().getColumnCount() > 0) {
            table_GroupsSignals.getColumnModel().getColumn(0).setMinWidth(100);
            table_GroupsSignals.getColumnModel().getColumn(0).setPreferredWidth(100);
            table_GroupsSignals.getColumnModel().getColumn(1).setMinWidth(200);
            table_GroupsSignals.getColumnModel().getColumn(1).setPreferredWidth(200);
        }

        button_SaveSettings.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_SaveSettings.setText("Сохранить");
        button_SaveSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_SaveSettingsActionPerformed(evt);
            }
        });

        label_tubeType.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_tubeType.setMinimumSize(new java.awt.Dimension(50, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(button_SaveSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 73, Short.MAX_VALUE)
                        .addComponent(sortoscopeCaption)
                        .addGap(0, 73, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(frequencyLabel)
                        .addGap(6, 6, 6)
                        .addComponent(textFIeld_FrequencyValue, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(diameterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tubeType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sortoscopeCaption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(diameterLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_tubeType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frequencyLabel)
                    .addComponent(textFIeld_FrequencyValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(button_SaveSettings)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_SaveSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_SaveSettingsActionPerformed
        clonnedSortospoeSettings.setNoTubeSignal((short) table_GroupsSignals.getModel().getValueAt(6, 1));
        clonnedSortospoeSettings.setMesurmentFrequency(Short.valueOf(textFIeld_FrequencyValue.getText()));
        clonnedSortospoeSettings.setDurabilityGropSignalValue(DurabilityGroups.Д, (short) table_GroupsSignals.getModel().getValueAt(0, 1));
        clonnedSortospoeSettings.setDurabilityGropSignalValue(DurabilityGroups.К, (short) table_GroupsSignals.getModel().getValueAt(1, 1));
        clonnedSortospoeSettings.setDurabilityGropSignalValue(DurabilityGroups.Е, (short) table_GroupsSignals.getModel().getValueAt(2, 1));
        clonnedSortospoeSettings.setDurabilityGropSignalValue(DurabilityGroups.Л, (short) table_GroupsSignals.getModel().getValueAt(3, 1));
        clonnedSortospoeSettings.setDurabilityGropSignalValue(DurabilityGroups.М, (short) table_GroupsSignals.getModel().getValueAt(4, 1));
        clonnedSortospoeSettings.setDurabilityGropSignalValue(DurabilityGroups.Р, (short) table_GroupsSignals.getModel().getValueAt(5, 1));
        currentTubeType.setParamsSort(clonnedSortospoeSettings);

        progressDialog.startProcessing(
                "Отправляем параметры в сортоскоп.",
                new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                try {
                    driver.setDiameterParameters(clonnedSortospoeSettings);
                    //Присваиваем новые параметры сортоскопа типк трубы
                    //Если отправка параметров в сортоскоп не удасться, то 
                    //и в типе трубы параметры остануться старые.
                    currentTubeType.setParamsSort(clonnedSortospoeSettings);
                    JOptionPane.showMessageDialog(
                            null,
                            "Параметры успешно отправлены в сортоскоп.",
                            "Уведомление",
                            INFORMATION_MESSAGE,
                            null
                    );
                } catch (IOException ex) {
                    Logger.getLogger(SortoscopeDialog.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "Не удалось отправить параметры в сортоскоп.",
                            ERROR_MESSAGE,
                            null
                    );
                }
                progressDialog.setVisible(false);
                return 0;
            }
        });
        this.setVisible(false);

    }//GEN-LAST:event_button_SaveSettingsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_SaveSettings;
    private javax.swing.JLabel diameterLabel;
    private javax.swing.JLabel frequencyLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_tubeType;
    private javax.swing.JLabel sortoscopeCaption;
    private javax.swing.JTable table_GroupsSignals;
    private javax.swing.JTextField textFIeld_FrequencyValue;
    // End of variables declaration//GEN-END:variables
}
