/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import ru.npptmk.devices.USKUdp.DeviceUSKUdp;
import ru.npptmk.devices.USKUdp.DeviceUSKUdpParam;
import ru.npptmk.guiObjects.SamopPnlTLSParams;

/**
 * Диалог параметров самописца толщиномера.
 *
 * @author MalginAS
 */
public class TlsPanelParamDialog extends javax.swing.JDialog {

    private final DeviceUSKUdp drvUSK1;
    private final DeviceUSKUdp drvUSK2;
    private final SamopPnlTLSParams par;
    private final String oldName;
    private final long oldUnit;
    private final int oldChan;

    private boolean isChanging = false;
    public boolean okButton;

    /**
     * Конструктор панели редактирования параметров самописца толщиномера.
     *
     * @param drvUSK1 драйвер первого модуля УЗК.
     * @param drvUSK2 драйвер второго модуля УЗК.
     * @param par параметры средства отображения самописца.
     */
    public TlsPanelParamDialog(DeviceUSKUdp drvUSK1, DeviceUSKUdp drvUSK2, SamopPnlTLSParams par) {
        super((java.awt.Frame) null, true);
        this.drvUSK1 = drvUSK1;
        this.drvUSK2 = drvUSK2;
        this.par = par;
        oldName = par.name;
        oldUnit = par.devId;
        oldChan = par.chanId;
        isChanging = true;
        initComponents();
        cbUnit.addItem("Блок УЗК1");
        if (par.devId == Devicess.ID_USK1) {
            cbUnit.setSelectedIndex(0);
        }
        cbUnit.addItem("Блок УЗК2");
        if (par.devId == Devicess.ID_USK2) {
            cbUnit.setSelectedIndex(1);
        }
        edName.setText(par.name);
        setUnit();
        setLocationRelativeTo(null);
        isChanging = false;
    }

    private void setName() {
        if (isChanging) {
            return;
        }
        par.name = "Толщиномер " + cbUnit.getSelectedItem() + " " + cbChannel.getSelectedItem();
        edName.setText(par.name);
    }

    private void setUnit() {
        isChanging = true;
        cbChannel.removeAllItems();
        DeviceUSKUdp cd;
        if (par.devId == drvUSK1.getDeviceId()) {
            cd = drvUSK1;
        } else {
            cd = drvUSK2;
        }
        for (int i = 0; i < 8; i++) {
            if (cd.getParams().prms[i].getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
                cbChannel.addItem("Канал " + (i + 1));
            }
            if (par.chanId == i) {
                cbChannel.setSelectedIndex(cbChannel.getItemCount() - 1);
            }
        }
        isChanging = false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        edName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cbUnit = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        cbChannel = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Параметры самописца толщиномера.");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Название канала");

        edName.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Модуль");

        cbUnit.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbUnitActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Канал");

        cbChannel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbChannel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbChannelActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setText("Применить");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton2.setText("Закрыть");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(edName)
                            .addComponent(cbUnit, 0, 169, Short.MAX_VALUE)
                            .addComponent(cbChannel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(edName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbChannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbUnitActionPerformed
        if (isChanging) {
            return;
        }
        int i = cbUnit.getSelectedIndex();
        switch (i) {
            case 0:
                par.devId = Devicess.ID_USK1;
                break;
            case 1:
                par.devId = Devicess.ID_USK2;
                break;
        }
        setUnit();
        setName();
    }//GEN-LAST:event_cbUnitActionPerformed

    private void cbChannelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbChannelActionPerformed
        if (isChanging) {
            return;
        }
        String chan = (String) cbChannel.getSelectedItem();
        if (chan != null) {
            int n = Integer.parseInt(chan.substring(6));
            par.chanId = n - 1;
        }
        setName();
    }//GEN-LAST:event_cbChannelActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        par.name = edName.getText();
        okButton = true;
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        par.name = oldName;
        par.devId = oldUnit;
        par.chanId = oldChan;
        okButton = false;
        setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbChannel;
    private javax.swing.JComboBox cbUnit;
    private javax.swing.JTextField edName;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}