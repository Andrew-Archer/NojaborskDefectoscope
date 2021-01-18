/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 *
 * @author RazumnovAA
 */
public class Utill {

    /**
     * Производит грлубинное клонирование данного листа типов. Клонирование
     * производится методом серриализации/десерриализации из потока объектов.
     *
     * @param <T>
     * @param objectToClone лист заданный для клонирования.
     * @return
     */
    public static <T> T getClone(T objectToClone) {

        //Записать пустой список типов труб, если на входе null.
        if (objectToClone == null) {
            return null;
            //Если объекта для клонирования не null.    
        } else {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);) {
                oos.writeObject(objectToClone);
                oos.flush();

                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bais);

                return (T) ois.readObject();

            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "Не получилось клоннировать объект из-за проблемами с потоками.");
                return null;
            } catch (ClassNotFoundException e) {
                Logger.getGlobal().log(Level.SEVERE, "Не получилось клоннировать объект так как не найден класс по имени.");
                return null;
            }

        }
    }
}
