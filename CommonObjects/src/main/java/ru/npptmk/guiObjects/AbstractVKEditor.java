/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;

/**
 * Класс для подключения возможности редактирования числового значения 
 * текстового поля с помощью экранной клавиатуры.<br>
 * Конструктор объекта данного класса подключается к событияю нажатия
 * правой кнопки мыши для текстового поля и при возникновении этого
 * события открывает для редактирования виртуальную клавиатуру. В случае
 * ввода нового значения вызывается абстрактный метод {@code newValue()},
 * который может быть использован для обработки этого значения.<br>
 * Обычное использование данного класса - создание анонимных объектов 
 * в конструкторе панели для каждого поля, требующего редактирования данных
 * с помощью экранной клавиатуры.
 * @author MalginAS
 */
public abstract class AbstractVKEditor extends MouseAdapter {
    JTextField ed;
    VirtualKeyboard vk;
    
    public abstract void newValue(String newVal);

    public AbstractVKEditor(JTextField ed, VirtualKeyboard vk) {
        this.ed = ed;
        this.vk = vk;
        this.ed.addMouseListener(this);
    }
    
    private void editValue(){
        String newval = vk.getValue(ed, ed.getText());
        if (newval != null) {
            if (!newval.isEmpty()){
                newValue(newval);
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()){
            editValue();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()){
            editValue();
        }
    }
    
}
