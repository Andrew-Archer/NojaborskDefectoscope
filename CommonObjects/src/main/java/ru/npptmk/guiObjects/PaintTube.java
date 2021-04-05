/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Представляет собой иконку, изображение масштабируется.<br>
 * Если вставляется в {@code JLabel} необходимо удалить текст, и зделать 
 * выравнивание по леву и верху. <br>
 * При создании класса необходимо задать максимальную возможную длину трубы, м.<br>
 * Класс реализует прорисовку трубы с линиями(дефектами).<br>
 * Цвета линий должны быть заданы после создания в переменную {@code prm}
 * {@code setLength} задает длину трубы и линии (дефекты).<br>
 * 
 * @author SmorkalovAV
 */
public class PaintTube implements Icon {

    /**максимальная длина реальной трубы, мm*/
    public int maxLenTube = 12000; 
    /**масштаб*/
    private float mX = 0;
    private int lenTube;
    /**рисуемые дефекты*/
    private Component c;
    public HashMap<Integer,Color> prm;

    /**
     * конструктор с максимальной длиной трубы, м
     * @param maxLenTube максимальная длина трубы, м
     */
    public PaintTube(int maxLenTube) {
        super();
        this.maxLenTube = maxLenTube * 1000;
    }
    /**
     * рисуем фон для отображения трубы
     * @param g2 контекст рисования
     */
    private void pntFon(Graphics2D g2) {
        g2.setColor(c.getBackground());
        g2.fillRect(0, 0, c.getWidth(), c.getHeight());
        g2.setColor(Color.black);
        g2.drawLine(10, 0, c.getWidth()-10, 0);
        g2.drawLine(10, c.getHeight()-1, c.getWidth()-10, c.getHeight()-1);
        mX = ((float)c.getWidth()-20)/maxLenTube;
        float ms = mX*1000;int i = 0;
        for(;i<=maxLenTube/1000 - 1;i++){
            g2.drawLine((int)(i * ms)+10, 0, (int)(i * ms)+10, c.getHeight());
            g2.drawString(String.valueOf(i), (int)(i * ms)+(i<10?14:12), c.getHeight()-5);
        }
        g2.drawLine((int)(i * ms)+10, 0, (int)(i * ms)+10, c.getHeight());
//        g2.drawString("Длина трубы, м", c.getWidth()/3, c.getHeight()-6);
    }

    /**
     * отрисовывает трубу на указанном графич. контексте
     * @param g2 графич. контексте
     */
    public void drawTube(Graphics2D g2) {
        if(lenTube == 0) {
            return;
        }
        int x = (int) (lenTube * mX);
        g2.setColor(Color.red);
        GeneralPath gp = new GeneralPath();//отрисовка трубы
        gp.append(new Arc2D.Float(0, 16, 20, (c.getHeight()-15)*3/4, 90, 180, Arc2D.OPEN), true);
        gp.append(new Arc2D.Float(x, 16, 20, (c.getHeight()-15)*3/4, 270, 180, Arc2D.OPEN), true);
        gp.closePath();
        GradientPaint grp = new GradientPaint(0,(c.getHeight()-15)*3/4,Color.BLACK,0, c.getHeight()/6,Color.white, true);        
        g2.setPaint(grp);
        g2.fill(gp);
        GeneralPath gp1 = new GeneralPath();
        gp1.append(new Arc2D.Float(x, 16, 20, (c.getHeight()-15)*3/4, 0, 359, Arc2D.OPEN),false);
        g2.setPaint(Color.BLACK);
        g2.fill(gp1);
//        setIcon(new ImageIcon(img));
        if(prm!=null){
            Iterator<Color> it = prm.values().iterator();
            Set<Integer> po = prm.keySet();
            for(Integer p : po) {
                x = (int) (p * mX);
                g2.setColor(prm.get(p));
                g2.drawLine(x + 10, 16, x + 10, (c.getHeight()-15)*3/4+15);
            }
        }
    }
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        // Запоминаем компонент.
        this.c = c;
        mX = ((float)c.getWidth()-20) / maxLenTube;
        pntFon((Graphics2D)g);
        drawTube((Graphics2D)g);
        g.dispose();
    }

    @Override
    public int getIconWidth() {
        return 0;
    }

    @Override
    public int getIconHeight() {
        return 0;
    }
    /**
     * устанавливает длину трубы
     * @param i новая длина трубы, мм
     * @param lineDf коллекция отображаемых дефектов
     */
    public void setLength(int i, HashMap<Integer, Color> lineDf) {
        lenTube = i;
        prm = lineDf;
    }
    /**
     * иконка трубы, для отчета
     * @param re размер иконки
     * @return иконка трубы
     */
    public Image getImage(Rectangle re) {
        BufferedImage bp = new BufferedImage(re.width, re.height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bp.getGraphics();
        JLabel cc = new JLabel();
        cc.setBounds(re);
        paintIcon(cc,g,0,0);
        ImageIcon pic = new ImageIcon(bp);
        return pic.getImage();
    }

    public void repaint() {
        if(c != null){
            c.repaint();
        }
    }
}
