
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.devices.md8Udp;


import java.awt.Point;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import ru.npptmk.guiObjects.VirtualKeyboard;
/**
 * Панелька отображает параметры настройки каналов электромагнитной
 * дефектоскопии. Считывание и запись в устройство происходит при
 * нажатии кнопки "Сохранить", по кнопке "Отмена" восстанавливаются
 * исходное значение полей
 * @author SmorkalovAV
 */
public class DeviceMD8UdpParamsPanel extends javax.swing.JPanel {
    private ParamsMD8Udp prm;
    private final TableModel tm;

    /**
     * Creates new form DeviceMD8UdpParamsPanel
     * @param pr Набор параметров установки МД.
     */
    public DeviceMD8UdpParamsPanel(ParamsMD8Udp pr) {
        initComponents();
        this.prm = pr;
        tm = frTable.getModel();
        restoreParams(pr);
    }
    VirtualKeyboard vk = new VirtualKeyboard(null);

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        frTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        frFiltr = new javax.swing.JTextField();
        Default = new javax.swing.JButton();
        GainUP = new javax.swing.JButton();
        GainDn = new javax.swing.JButton();
        PorogUp = new javax.swing.JButton();
        PorogDn = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Настройка каналов");

        frTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "<html><br>Канал</html>", "Усиление", "Порог, %", "<html>Положение <br>датчика, мм</html>"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        frTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        frTable.setRowHeight(20);
        frTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 18));
        frTable.setFont(new java.awt.Font("Segoe UI", 0, 18));
        frTable.setRowSelectionAllowed(false);
        frTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        frTable.getTableHeader().setResizingAllowed(false);
        frTable.getTableHeader().setReorderingAllowed(false);
        frTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                frTableMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(frTable);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Фильтр сглаживания");

        frFiltr.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        frFiltr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frFiltrActionPerformed(evt);
            }
        });

        Default.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Default.setText("По умолчанию");
        Default.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DefaultActionPerformed(evt);
            }
        });

        GainUP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        GainUP.setText("Усиление +5");
        GainUP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GainUPActionPerformed(evt);
            }
        });

        GainDn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        GainDn.setText("Усиление -5");
        GainDn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GainDnActionPerformed(evt);
            }
        });

        PorogUp.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        PorogUp.setText("Порог +5");
        PorogUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PorogUpActionPerformed(evt);
            }
        });

        PorogDn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        PorogDn.setText("Порог -5");
        PorogDn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PorogDnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Default)
                .addGap(34, 34, 34)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frFiltr))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GainUP)
                    .addComponent(GainDn))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PorogUp, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                    .addComponent(PorogDn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GainUP)
                    .addComponent(PorogUp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PorogDn)
                    .addComponent(GainDn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Default)
                    .addComponent(jLabel2)
                    .addComponent(frFiltr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void frFiltrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frFiltrActionPerformed
        
    }//GEN-LAST:event_frFiltrActionPerformed

    private void frTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_frTableMousePressed
        if (SwingUtilities.isRightMouseButton(evt))
        {
        Point point = evt.getPoint();
        int column = frTable.columnAtPoint(evt.getPoint());
            int row = frTable.rowAtPoint(evt.getPoint());
            frTable.setColumnSelectionInterval(column, column);
            frTable.setRowSelectionInterval(row, row);
            if ((column == 1) || (column == 2) || (column == 3)) {
                String vl = (String) (tm.getValueAt(row, column));
                String newVal = vk.getValue(frTable, vl);
                if (newVal != null) {
                    tm.setValueAt(newVal, row, column);
                }
            }
}
    }//GEN-LAST:event_frTableMousePressed

       // Кнопка отмена. Возвращает параметры Усиление 20; Порог 50.
    private void DefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DefaultActionPerformed
        if (Default.isEnabled()){
        }
        for (int ii = 0; ii < 8; ii++) {
        
        tm.setValueAt(String.valueOf(20), ii, 1);
        tm.setValueAt(String.valueOf(50), ii, 2);
        getActualParam();
    }
    }//GEN-LAST:event_DefaultActionPerformed
        // Усиление +5
    private void GainUPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GainUPActionPerformed
        if (GainUP.isEnabled()){
            for (int ii = 0; ii < 8; ii++) {
            tm.setValueAt(String.valueOf(prm.gain[ii]+5), ii, 1);
            }
        getActualParam();}
    }//GEN-LAST:event_GainUPActionPerformed
        // Порог +5
    private void PorogUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PorogUpActionPerformed
        if (GainUP.isEnabled()){
            for (int ii = 0; ii < 8; ii++) {
            tm.setValueAt(String.valueOf(prm.porog[ii]+5), ii, 2);
            }
        getActualParam();}
    }//GEN-LAST:event_PorogUpActionPerformed
        // Усиление -5
    private void GainDnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GainDnActionPerformed
        if (GainUP.isEnabled()){
            for (int ii = 0; ii < 8; ii++) {
            tm.setValueAt(String.valueOf(prm.gain[ii]-5), ii, 1);
            }
        getActualParam();}
    }//GEN-LAST:event_GainDnActionPerformed
        // Порог -5
    private void PorogDnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PorogDnActionPerformed
        if (PorogDn.isEnabled()){
            for (int ii = 0; ii < 8; ii++) {
            tm.setValueAt(String.valueOf(prm.porog[ii]-5), ii, 2);
            }
        getActualParam();}
    }//GEN-LAST:event_PorogDnActionPerformed

   /**
     * Возвращение параметров с формы редактирования
     * @return 
     */
    public ParamsMD8Udp getActualParam(){
        for (int ii = 0; ii < 8; ii++) {
            prm.gain[ii] = Short.valueOf(tm.getValueAt(ii, 1).toString());
            prm.porog[ii] = Short.valueOf(tm.getValueAt(ii, 2).toString());
            prm.offset[ii] = Short.valueOf(tm.getValueAt(ii, 3).toString());
        }
        prm.filtr = Short.valueOf(frFiltr.getText());
        return prm;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Default;
    private javax.swing.JButton GainDn;
    private javax.swing.JButton GainUP;
    private javax.swing.JButton PorogDn;
    private javax.swing.JButton PorogUp;
    private javax.swing.JTextField frFiltr;
    private javax.swing.JTable frTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    /**
     * Обновление параметров на форме
     * @param prm параметры, которые выведутся на панель
     */
    public void restoreParams(ParamsMD8Udp prm) {
        this.prm = prm;
        for (int ii = 0; ii < 8; ii++) {
            tm.setValueAt(String.valueOf(ii + 1), ii, 0);
            tm.setValueAt(String.valueOf(prm.gain[ii]), ii, 1);
            tm.setValueAt(String.valueOf(prm.porog[ii]), ii, 2);
            tm.setValueAt(String.valueOf(prm.offset[ii]), ii, 3);
        }
        frFiltr.setText(String.valueOf(prm.filtr));
    }

}