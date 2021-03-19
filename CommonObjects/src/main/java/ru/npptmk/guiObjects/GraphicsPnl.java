/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Средсство отображения данных в виде набора графиков.<br>
 * Используется в составе контейнера {@code PanelForGraphics}, хотя сама
 * она не поддерживает интерфейс {@code IDrvsDataReader}, так как не 
 * предназначена для непосредственного приема данных от драйверов. Этот
 * функционал должен быть реализован в суерклассах.<br>
 * Данные отображаются в виде линейных графиков. Каждый график
 * задается в виде двух массивов координат X и Y. Массивы должны
 * иметь одинаковый размер. Значения координат должны меняться от 0 до
 * максимального значения, заданного при создании панели.
 * Точки графика отображаются последовательно,
 * начиная с 0 индекса массивов координат. Если координаты начальной и конечной 
 * точек графика не совпадают, то график изображается в виде ломаной линии.
 * Если координаты начальной и конечной точек совпадают, то полученный
 * замкнутый контур закрашивается. Описание каждого графика содержится в 
 * объекте класса {@code Grafic}.<br>
 * Цвет фона панели графиков определяется фоном объекта {@code  frGr}. Для 
 * облегчения интерпретации результатов на графике изображаются вертикальные и 
 * горизонтальные линии координатной сетки. Они изображаются основным
 * цветом объекта {@code  frGr}. Количество вертикальных и горизонтальных линий
 * сетки задаются при создании компонента.<br>
 * Помимо графика компонент содержит текстовое поле для вывода заголовка.<br>
 * В составе компонента имеются кнопки позволяющие пользователю выполнять
 * следующие операции:
 * <ul>
 * <li> Удаление компонента из объекта {@code PanelForGraphics}.
 * <li> Перемещение компонента вверх и вниз в коллекции аналогичных
 * компонентов объекта {@code PanelForGraphics}.
 * <li> Изменение настроек данного компонента.
 * </ul>
 * @author SmorkalovAV
 */
public class GraphicsPnl extends javax.swing.JPanel {
    private final PanelForGraphics parentPnl;
    private int visiblePos;
    private final GraficsIcon grIcon;
    
    /**
     * Конструктор объекта.
     * 
     * @param prn контейнер средст отображения данных.
     * @param vizPos позиция отображения (индекс в параметрах, возвращаемых
     * {@code getParamCollection} родителя.
     */
    public GraphicsPnl(PanelForGraphics prn, int vizPos) {
        initComponents();
        this.visiblePos = vizPos;
        GraphicsPnlParams prm = (GraphicsPnlParams) prn.getParamCollection().get(vizPos);
        grIcon = new GraficsIcon(prm.maxX, prm.maxY, prm.nVertLines,prm.nHorLines,0.001f,0);
        grIcon.setOffset(new Rectangle(10, 0, 10, 0));
        frGr.setIcon(grIcon);
        frName.setText(prm.name);
        this.parentPnl = prn;
    }
    /**
     * Добавляет график на панельку.
     * @param grf график для отображения
     * @param i индекс графика.
     */
    public void addGraphic (Grafic grf, int i){
        grIcon.addGrafic(i, grf);
    }
    
    public HashMap<Integer,Grafic> getGrafsHash(){
        return grIcon.getGrafs();
    }
    
    /**
     * Удаляет все графики с панельки.
     */
    public void clear(){
        grIcon.removeGrafics();
    }
    /**
     * Устанавливает новое максимальное значение для координаты X
     * @param maxX 
     * @param nVert 
     * 
     */
    public void setMaxX(int maxX, int nVert){
        grIcon.setNewLen(maxX,nVert);
    }
    
    public void setMaxY(int maxY, int nHor){
        grIcon.setNewHight(maxY, nHor);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frGr = new javax.swing.JLabel();
        frName = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        frBtCls = new javax.swing.JButton();
        frBtUp = new javax.swing.JButton();
        frBtDvn = new javax.swing.JButton();
        frBtPrp = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setMinimumSize(new java.awt.Dimension(0, 100));
        setPreferredSize(new java.awt.Dimension(100, 130));

        frGr.setBackground(new java.awt.Color(255, 255, 255));
        frGr.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        frGr.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        frGr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                frGrMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                frGrMouseReleased(evt);
            }
        });

        frName.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        frName.setText("jLabel2");
        frName.setMaximumSize(new java.awt.Dimension(111111, 111111111));
        frName.setMinimumSize(new java.awt.Dimension(0, 17));

        jPanel1.setAlignmentX(0.0F);
        jPanel1.setAlignmentY(0.0F);

        frBtCls.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        frBtCls.setIcon(new javax.swing.ImageIcon(getClass().getResource("/042-cancel.png"))); // NOI18N
        frBtCls.setMargin(new java.awt.Insets(0, 0, 0, 0));
        frBtCls.setMaximumSize(new java.awt.Dimension(32, 32));
        frBtCls.setMinimumSize(new java.awt.Dimension(32, 32));
        frBtCls.setPreferredSize(new java.awt.Dimension(32, 32));
        frBtCls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frBtClsActionPerformed(evt);
            }
        });

        frBtUp.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        frBtUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/006-up-arrow-2.png"))); // NOI18N
        frBtUp.setMargin(new java.awt.Insets(0, 0, 0, 0));
        frBtUp.setMaximumSize(new java.awt.Dimension(32, 32));
        frBtUp.setMinimumSize(new java.awt.Dimension(32, 32));
        frBtUp.setPreferredSize(new java.awt.Dimension(32, 32));
        frBtUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frBtUpActionPerformed(evt);
            }
        });

        frBtDvn.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        frBtDvn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/035-down-arrow-2.png"))); // NOI18N
        frBtDvn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        frBtDvn.setMaximumSize(new java.awt.Dimension(32, 32));
        frBtDvn.setMinimumSize(new java.awt.Dimension(32, 32));
        frBtDvn.setPreferredSize(new java.awt.Dimension(32, 32));
        frBtDvn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frBtDvnActionPerformed(evt);
            }
        });

        frBtPrp.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        frBtPrp.setText("...");
        frBtPrp.setMargin(new java.awt.Insets(0, 0, 0, 0));
        frBtPrp.setMaximumSize(new java.awt.Dimension(32, 32));
        frBtPrp.setMinimumSize(new java.awt.Dimension(32, 32));
        frBtPrp.setPreferredSize(new java.awt.Dimension(32, 32));
        frBtPrp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frBtPrpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(frBtCls, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(frBtUp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(frBtDvn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(frBtPrp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(frBtCls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frBtUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frBtDvn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frBtPrp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(frName, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .addComponent(frGr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frGr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void frBtClsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frBtClsActionPerformed
        List<Serializable> op = parentPnl.getParamCollection();
        op.remove(visiblePos);
        parentPnl.redrawPanels();
    }//GEN-LAST:event_frBtClsActionPerformed

    private void frBtUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frBtUpActionPerformed
        List<Serializable> op = parentPnl.getParamCollection();
        if(visiblePos>0){
            Serializable el = op.get(visiblePos);
            op.set(visiblePos, op.get(visiblePos - 1));
            op.set(visiblePos - 1, el);
            parentPnl.redrawPanels();
        }
    }//GEN-LAST:event_frBtUpActionPerformed

    private void frBtDvnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frBtDvnActionPerformed
        List<Serializable> op = parentPnl.getParamCollection();
        if(visiblePos<op.size()-1){
            Serializable el = op.get(visiblePos);
            op.set(visiblePos, op.get(visiblePos + 1));
            op.set(visiblePos + 1, el);
            parentPnl.redrawPanels();
        }
    }//GEN-LAST:event_frBtDvnActionPerformed

    private void frBtPrpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frBtPrpActionPerformed
        parentPnl.editParams(visiblePos);
    }//GEN-LAST:event_frBtPrpActionPerformed

    /**
     * Отображение окна для выбора списка отображаемых графиков.
     */
    private void processPopUp(){
        GrafsVisibilityDialog dg = new GrafsVisibilityDialog(grIcon);
        dg.setVisible(true);
    }
    
    public void refresh(){
        grIcon.repaint();
    }
    
    
    private void frGrMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_frGrMousePressed
        if (evt.isPopupTrigger()){
            processPopUp();
        }
    }//GEN-LAST:event_frGrMousePressed

    private void frGrMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_frGrMouseReleased
        if (evt.isPopupTrigger()){
            processPopUp();
        }
    }//GEN-LAST:event_frGrMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton frBtCls;
    private javax.swing.JButton frBtDvn;
    private javax.swing.JButton frBtPrp;
    private javax.swing.JButton frBtUp;
    private javax.swing.JLabel frGr;
    private javax.swing.JLabel frName;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
