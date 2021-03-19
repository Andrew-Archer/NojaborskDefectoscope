/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.util.Arrays;

/**
 * Отдельный график для отрисовки в {@code GraficsIcon}
 * для удобства хранения выводиых в иконку графиков.
 * Если координата (х,у) первой точки графика совпадает с 
 * последней, то изображается закрашенная область без ее границы.<br>
 * Массивы для размещения координат можно создавать с запасом. При
 * добавлении новой точки к графику массивы при необходимости расширяются
 * с шагом в 10 точек.
 * В этот класс предназначен для хранения данных графика в координатах
 * удобных для пользователя.
 * Рисование графика происхдит в классе {@code GraficsIcon} или другом,
 * связанным с элементом управления.
 * @author SmorkalovAV
 */
public class Grafic {
    /**степень непрозрачности области в которой х-координата возвращается в начало*/
    public int alpha = 60;
    /**это показываем*/
    public boolean visible = true;
    public int grLen;      // Количество точек в графике. Испульзуется, чтобы не 
                            // пересоздавать массивы каждый раз при добавлении точки.
    
    public boolean clPath = false; 
    /**
     * Наименование графика.
     */
    public String name;     
    /**
     * Создает пустой график черного цвета
     */
    public Grafic() {
        this(Color.black, null, null);
    }

    /**
     * Конструктор с полным набором параметров.
     * @param name наименование графика.
     * @param grLen текущее количество точек в графике.
     * @param clr цвет графика.
     * @param xPo массив для размещения X координат точек графика.
     * @param yPo массив для размещения Y координат точек графика.
     */
    public Grafic(String name,int grLen, Color clr, float[] xPo, float[] yPo) {
        this.name = name;
        this.grLen = grLen;
        this.clr = clr;
        this.xPo = xPo;
        this.yPo = yPo;
    }

    
    /**
     * конструктор одного графика на иконке
     * @param clr цвет графика
     * @param xPo массив х координат
     * @param yPo массив у координат
     * @param basa начало графика, мм
     * @param direct направление перебора значений буфера
     * @param str если линию рисуем пунктиром
     */
    public Grafic(Color clr, float[] xPo, float[] yPo, int basa, 
            boolean direct, Stroke str) {
        this("", xPo == null ? 0: xPo.length, clr, xPo, yPo);
        this.bas = basa;
        this.direct = direct;
        this.str = str;
    }
    
    
    public int getGrLen(){
        return grLen;
    }
    
    public void setGrLen(int len){
        grLen = len;
    }
    /**
     * Добавляем точку к графику
     * @param y координата точки
     * @param x координата точки 
     */
    public void addPoint(float y, float x){
        if(yPo == null){
            yPo = new float[grLen+10];
        }
        if(xPo == null){
            xPo = new float[grLen+10];
        }
        grLen++;
        if (grLen > xPo.length) {
            // Расширение размеров массивов на 10.
            yPo = Arrays.copyOf(yPo, grLen + 10);
            xPo = Arrays.copyOf(xPo, grLen + 10);
        }
        yPo[grLen - 1] = y;
        xPo[grLen - 1] = x;
    }
    /**
     * Добавляет точку к графику так чтобы она была отсортирована
     * по Х координате. 
     * @param x х-координата
     * @param y у-координата
     */
    public void insertSortPoint(float x, float y){
        addPoint(y, x);
        for(int i=grLen-2;i>=0;i--){
            if(xPo[i] < x){
                xPo[i+1] = x;
                yPo[i+1] = y;
                break;
            }
            xPo[i+1] = xPo[i];
            yPo[i+1] = yPo[i];
        }
    }
    /**
     * конструктор одного графика на иконке
     * @param clr цвет графика
     * @param xPo массив х координат
     * @param yPo массив у координат
     */
    public Grafic(Color clr, float[] xPo, float[] yPo) {
        this("", xPo == null ? 0: xPo.length, clr, xPo, yPo);
    }

    public Color clr;
    /**массив х-кординат полилини, если начало
     замкнуто на конец то рисуем полигоном, залитым цветом
     лнии*/
    public float [] xPo;
    public float [] yPo;
    /**
     * Тип линии графика.
     */
    public Stroke str = null;
    /***/
    public int bas = 0;
    /**Сдвиг графика по координате Y, коорд. графика*/
    public int ofsY = 0;
    /**Сдвиг графика по координате Х, коорд. графика*/
    public int ofsX = 0;
    public boolean direct = true;
    /**коэффициент изменения по оси Y для графика*/
    public float msY = 1;
    /**коэффициент изменения по оси X для графика*/
    public float msX = 1;
    /**
     * Возвращает точку графика в координатах реальных значений
     * @param ind индекс запрашиваемой точки
     * @return точка
     */
    public Point getPoint(int ind) {
        return new Point((int)xPo[ind], (int)yPo[ind]);
    }
    /**
     * Очищает графики
     */
    public void clearGrf() {
        xPo = null;
        yPo = null;
        grLen = 0;
    }
    /**
     * Является ли график замкнутым, т.е. первая и последняя коорд.
     * графика совпадают в этом случае рисовальщик нарисует замкнутый
     * контур заполненный на {@code alpha} процетов не прозрачности цвета
     * графика.
     * @return замкнуты ли координаты графика. 
     */
    public boolean isClosedGraph() {
        if(grLen == 0) return false;
        return xPo[0] == xPo[grLen-1] && yPo[0] == yPo[grLen-1];
    }
    /**
     * Устанавливает новый график ко переданным координатам.
     * в {@code xPos} массиве допускается элемент равный нулю только в первом
     * элементе, при обнаружении элемента равным нулю 
     * @param xPos
     * @param yPos 
     */
    public void setCoords(float[] xPos, float[] yPos) {
        clearGrf();
        for (int i = 0; i < xPos.length; i++) {
            if (i > 0 && xPos[i] == 0) {
                break;
            }
            addPoint(yPos[i], xPos[i]);
        }
    }

}
