/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DatesInterval.java
 *
 * Created on 21.10.2011, 10:28:36
 */
package ru.npptmk.guiObjects;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

/**
 *
 * @author MalginAS
 */
public class DateInterval extends javax.swing.JDialog {
    
    public Date savedBeg;
    public Date savedEnd;
    public boolean isOk = false;
    private JCheckBox jCheckBox;
    VirtualKeyboard vk = new VirtualKeyboard(null);
    
    public Date getSavedBeg() {
        return savedBeg;
    }

    public void setSavedBeg(Date savedBeg) {
        this.savedBeg = savedBeg;
        edBegin.setValue(savedBeg);
    }

    public Date getSavedEnd() {
        return savedEnd;
    }

    public void setSavedEnd(Date savedEnd) {
        this.savedEnd = savedEnd;
        edEnd.setValue(savedEnd);
    }
    /**
     * Модальный диалог дат посередине экрана,
     * инициированный началом и концом текущего дня.
     */
    public DateInterval() {
        super((Frame)null, true);
        initComponents();
        setLocationRelativeTo(null);
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                savedBeg = (Date) edBegin.getValue();
                savedEnd = (Date) edEnd.getValue();
                isOk = false;
            }

            @Override
            public void windowClosed(WindowEvent e) {
                edBegin.setValue(savedBeg);
                edEnd.setValue(savedEnd);
            }
        });
        new AbstractVKEditor(edBegin, vk) {
            @Override
            public void newValue(String newVal) {
                edBegin.setValue(newVal);
            }
        };
        new AbstractVKEditor(edEnd, vk) {
            @Override
            public void newValue(String newVal) {
                edEnd.setValue(newVal);
            }
        };
    }
    
    /**
     * Модальный диалог с заданием дат начала и окончания
     * @param svBeg дата начала
     * @param svEnd дата окончания
     */
    public DateInterval(Date svBeg, Date svEnd) {
        this();
        this.savedBeg = svBeg;
        edBegin.setValue(svBeg);
        this.savedEnd = svEnd;
        edEnd.setValue(svEnd);
    }
    
    /**Модальный диалог с текущей датой со временем 8:00 и 
     * завтрешней датой с таким же временем.
     * Параметры конструктора оставлены для совместимости 
     * с предыдущими версиями
     * @param parent родительский фрейм
     * @param modal режим работы*/
    public DateInterval(java.awt.Frame parent, boolean modal) {
        this();
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        savedBeg = cal.getTime();
        cal.roll(Calendar.DAY_OF_YEAR, 1);
        savedEnd = cal.getTime();
        edBegin.setValue(savedBeg);
        edEnd.setValue(savedEnd);
   }

    /**Модальный диалог с текущую дату с заданным временем 
     * завтряшнюю дату с таким же временем
     * @param hours заданный час*/
    public DateInterval(int hours) {
        this();
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        savedBeg = cal.getTime();
        cal.roll(Calendar.DAY_OF_YEAR, 1);
        savedEnd = cal.getTime();
        edBegin.setValue(savedBeg);
        edEnd.setValue(savedEnd);
        
   }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        edBegin = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        edEnd = new javax.swing.JFormattedTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jCheckBox = new javax.swing.JCheckBox();
        jCheckBox.setVisible(false);

        setTitle("Задайте интервал дат для отчета");
        setResizable(false);

        edBegin.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss"))));
        edBegin.setValue(new Date());

        jLabel1.setText("Начало периода");

        jLabel2.setText("Конец периода");

        edEnd.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss"))));
        edEnd.setValue(new Date());

        jButton1.setText("ОК");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Отмена");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            @Override
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edBegin, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(edEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(edBegin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(edEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Date beg = (Date) edBegin.getValue();
        Date end = (Date) edEnd.getValue();
        if (end.before(beg)){
            JOptionPane.showMessageDialog(rootPane,"Конец периода раньше начала!", 
                    "Предупреждение.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        savedBeg = (Date) edBegin.getValue();
        savedEnd = (Date) edEnd.getValue();
        isOk = true;
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        edBegin.setValue(savedBeg);
        edEnd.setValue(savedEnd);
        setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JFormattedTextField edBegin;
    public javax.swing.JFormattedTextField edEnd;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables

    public void initialize(Date date, Date date0) {
        edBegin.setValue(date);
        edEnd.setValue(date0);
    }
    /**Установить запрос к пользователю.
     В виде чекбокса с заданным текстом.
     * @param conf текст чекбокса
     * @param b изначальное состояние*/
    public void setConfirm(String conf, boolean b) {
        jCheckBox.setVisible(true);
        jCheckBox.setText(conf);
        jCheckBox.setSelected(b);
        pack();
    }
    /**Было ли согласие на запрос
     * @return состояние чекбокса*/
    public boolean isConfirm(){
        return jCheckBox.isSelected();
    }
}
