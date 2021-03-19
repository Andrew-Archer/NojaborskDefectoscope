/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

/**
 * Иконка с самописцем.<br> Представляет собой динамически обновляемое изображение
 * ленты самописца с нанесенными на нее отметками графиков. <br> Горизонтальная ось
 * графиков (ось X) имеет размерность пиксел. Каждый вызов метода {@code addPont()
 * } приводит к сдвигу изображения вправо на один пиксел и изображения новой
 * колонки пикселов слева.<br> Вертикальная ось (ось Y) имеет целочисленное
 * значение в заданном диапазоне. Границы диапазона соответствуют верхней и
 * нижней границам графика. Этот диапазон задается в конструкторе и не может
 * быть изменен в дальнейшем. Все поступающие значения точек графика должны
 * находиться внутри данного диапазона. Если значение выходит за диапазон, то
 * такая точка изображается на его границе.<br> Размеры иконки могут быть
 * динамически изменены в процессе работы. Изменение вертикального размера
 * приводит к перемасштабированию изображения по вертикальной оси. Изменение
 * горизонтального размера приводит к усечению или дополнению избражения справа.
 * Дополнение производится цветом фона.<br> Иконка может быть включена в любой
 * компонент, допускающий наличие иконок. При рисовании размер иконки
 * принимается равным размеру компонента. Следует учитывать, что методы
 * {@code getIconHeight()} и {@code getIconWidth()} данного объекта всегда
 * возвращают 0. Поэтому целесообразно задавать выравниваие иконки в компоненте
 * по левому краю и по верхней границе.<br> Цвет линий сетки определяется
 * основным цветом компонента, возвращаемым методом компонта
 * {@code getForeground()}. Цвет фона определяется цветом фона компонента,
 * возвращаемым методом {@code getBackground()}. Цвет выводимых на график
 * отметок задается в самих отметках (см. {@link RecorderPoint})
 *
 * @author MalginAS
 */
public class RecorderIcon implements Icon {

    private final int lBound;     // Нижняя граница диапазона Y.
    private final int hBound;     // Верхняя граница диапазона Y.
    private final int nRows;      // Количество горизонтальных линий сетки.
    private final int nPicPerCol; // Шаг вертикальных линий сетки в пикселах.
    private Component c = null; // Компонент, на котором изображена иконка.
    // Размер иконки равен размеру компонента.
    private BufferedImage img;  // Изображение самописца.  
    private int cPicPerCol = 0; // Счетчик пикселов для рисования вертикальных
    // линий сетки.

    /**
     * Конструктор иконки самописца.
     *
     * @param lBound Минимальное значение по Y
     * @param hBound Максимальное значение по Y
     * @param nRows Количество горизонтальных линий сетки самописца. Не менее 2.
     * @param nPicPerCol Шаг вертикальных линий сетки в пикселах.
     */
    public RecorderIcon(int lBound, int hBound, int nRows, int nPicPerCol) {
        this.lBound = lBound;
        this.hBound = hBound;
        this.nRows = nRows;
        this.nPicPerCol = nPicPerCol;
    }
    /**
     * Конструктор для создания иконки для известного компонента. <br>
     * Используется
     * в ситуации, когда данные на иконку могут начать поступать до момента
     * отображения иконуи на панели (до вызова метода {@code paintIcon(...)}.
     * В этой ситуации при вызове метода {@code addPoint(..)} значение
     * текущего компонента окажется нулевым и точка на график не добавится. Для
     * предотвращения такой ситуации следует применять этот конструктор.
     * @param lBound Минимальное значение по Y
     * @param hBound Максимальное значение по Y
     * @param nRows Количество горизонтальных линий сетки самописца. Не менее 2.
     * @param nPicPerCol Шаг вертикальных линий сетки в пикселах.
     * @param c Компонент, в который будет вставлена иконка.
     */
    public RecorderIcon(int lBound, int hBound, int nRows, int nPicPerCol,Component c){
        this(lBound,hBound,nRows,nPicPerCol);
        this.c = c;
    }

    /**
     * Рисование иконки самописца.
     *
     * @param c Компонент, в который включена иконка.
     * @param g Графический контекст для рисования.
     * @param x Координата левого верхнего угла.
     * @param y Координата левого верхнего угла.
     */
    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        // Запоминаем компонент.
        if (this.c == null) {
            this.c = c;
        }
        // Создаем пустой образ при необходимости. 
        if (img == null) {
            img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D ig = img.createGraphics();
            ig.setColor(c.getBackground());
            ig.fillRect(0, 0, img.getWidth(), img.getHeight());
            ig.dispose();
        }
        // Определяемся с габаритами зоны рисования.
        if (img.getHeight() != c.getHeight()) {
            // Надо масштабировать по вертикали.
            BufferedImage nimg = new BufferedImage(img.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D ig = nimg.createGraphics();
            ig.drawImage(img, 0, 0, img.getWidth(), c.getHeight(), null);
            ig.dispose();
            img = nimg;
        }
        if (img.getWidth() != c.getWidth()) {
            // Надо усечь картинку справа или дополнить фоном
            BufferedImage nimg = new BufferedImage(c.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D ig = nimg.createGraphics();
            ig.drawImage(img, 0, 0, null);
            if (c.getWidth() > img.getWidth()) {
                ig.setColor(c.getBackground());
                ig.fillRect(img.getWidth(), 0, c.getWidth() - img.getWidth(), img.getHeight());
            }
            ig.dispose();
            img = nimg;
        }
        // Теперь все нормально. Можно рисовать.
        g.drawImage(img, x, y, c);
    }

    @Override
    public int getIconWidth() {
        return 0;
    }

    @Override
    public int getIconHeight() {
        return 0;
    }

    public synchronized void addPoint(RecorderPoint[] pts) {
        if (c == null) {
            return;
        }
        if (c.getWidth() <= 0 || c.getHeight() <= 0){
            return;
        }
        // Создаем пустой образ при необходимости. 
        if (img == null) {
            img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D ig = img.createGraphics();
            ig.setColor(c.getBackground());
            ig.fillRect(0, 0, img.getWidth(), img.getHeight());
            ig.dispose();
        }
        BufferedImage nimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        double cy = ((double) img.getHeight()) / ((double) (hBound - lBound));
        Graphics2D ig = nimg.createGraphics();
        // Рисуем фон. Для линий сетки используем основной цвет компонента,
        // для всех остальных - фоновый цвет.
        if (cPicPerCol == 0) {
            ig.setColor(c.getForeground());
        } else {
            ig.setColor(c.getBackground());
        }
        ig.fillRect(0, 0, 1, img.getHeight());
        // Рисуем горизонтальные линии сетки.
        // Если нарисована вертикальная линия сетки, то горизонтальные не рисуем (цвет тот-же).
        if (cPicPerCol != 0) {
            ig.setColor(c.getForeground());
            double hs = cy * ((double) (hBound - lBound)) / ((double) (nRows - 1));
            for (int i = 0; i < nRows; i++) {
                int rw = img.getHeight() - 1 - (int) Math.round(hs * i);
                ig.fillRect(0, rw, 1, 1);
            }
        }
        for (RecorderPoint pt : pts) {
            int upb = pt.up;
            int lwb = pt.low;
            upb = (upb > hBound) ? hBound : upb;
            upb = (upb < lBound) ? lBound : upb;
            lwb = (lwb > hBound) ? hBound : lwb;
            lwb = (lwb < lBound) ? lBound : lwb;
            int dn = (int) (img.getHeight() - 1 - Math.round((upb - lBound) * cy));
            int hi = (int) (img.getHeight() - 1 - Math.round((lwb - lBound) * cy));
            ig.setColor(pt.color);
            ig.fillRect(0, dn, 1, (hi - dn + 1));
        }
        // Дорисовываем старый график
        ig.drawImage(img, 1, 0, c);
        ig.dispose();
        img = nimg;
        // Сдвигаем счетчик пикселов
        cPicPerCol++;
        if (cPicPerCol == nPicPerCol) {
            cPicPerCol = 0;
        }
        c.repaint();
    }
}
