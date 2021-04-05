/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Иконка представляет собой овал заданного 
 * размера, нарисован и залит заданнымм цветами.
 * Используется для отображения состояния датчиков на мнемосхеме.
 * Предполагаемое использование:
 * предполагается использовать с элементом {@code JCheckBox},
 * перед созданием элемента типа {@code JCheckBox}(вызов метода {@code initComponents()}
 * необходибо создать два элемента типа {@code OvalIcon}, соответственного 
 * размера и цвета, соответствующего включенному и выключенному состоянию датчика.
 * Далее в свойствах элемента {@code JCheckBox} задать свойству {@code icon} 
 * и {@code selectedIcon} соостветствующие элементы типа {@code OvalIcon}.
 * Если при движении указателя мышки через элемент от будет изменять цвет необходмо
 * задать соотв. элементы свойствам {@code rolloverIcon} и {@code rolloverSelectedIcon}
 * @author SmorkalovAV
 */
public class OvalIcon extends ImageIcon {

    BufferedImage im;
    /**
     * Создаем иконку c изображением овала заданного размера (пикс)
     * и заданного цвета заливки и цвета линии, которой он нарисован
     * @param wei ширина прамоугольника в который вписан овал
     * @param hei высота прамоугольника в который вписан овал
     * @param clr цвет заливки овала
     * @param clrBrd цвет линии овала
     */
    public OvalIcon(int wei, int hei, Color clr, Color clrBrd) {
        im = new BufferedImage(wei, hei, BufferedImage.BITMASK);
        Graphics g = im.getGraphics();
        g.setColor(clr);
        g.fillOval(0, 0, wei-1, hei-1);
        g.setColor(clrBrd);
        g.drawOval(0, 0, wei-1, hei-1);
        setImage(im);
    }
    /**
     * Создаем иконку c изображением овала с размерами и цветами по
     * умолчанию 10, 10, Color.green, Color.red.
     */
    public OvalIcon() {
        this(12, 12, Color.green, Color.green);
    }
}
