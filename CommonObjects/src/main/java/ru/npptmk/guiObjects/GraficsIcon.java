/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.AbstractListModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Иконка с графиками исследования трубы.<br>
 * График представляет собой масштабируемое изображение c вертикальными и
 * горизонтальными линиями сетки и линиями графиков, добавляемых динамически.
 * Есть возможность задать вывод подписей около линий сетки. Координаты графиков
 * задаются в физических величинах и отсчитываются от нижней левой точки экрана.
 * <br>
 * Иконку необходибо расположить на любом компоненте допускающем размещение
 * {@code Icon}, при этом выравнивание необходибо сделать по левому верхнему
 * краю. Линии сетки будут выведены цветом фона {@code getBakcground()}, цвет
 * горизонтальных линий можно задать в {@code colorGLines}. В конструкторах
 * передаем размер области вывода в физицеских величинах, число в. г. линий и
 * коэффициенты изменения текста в подписях. Если первую и последнюю точку
 * графика совместить, то будет нарисована не линия а область, закрашенная
 * цветом линии и прозрачностью {@code alpha = 60}<br>
 * Иконка дополнена методом, возвращающим компонент {@code JList},
 * содержащий список графиков, с их цветами и признаками
 * видимость. Этот компонент можно отобразить на экране и дать пользователю
 * возможность управлять видимостью графиков на иконке. Обновление
 * иконки происходит сразу же после изменения признака видимости пользователем.
 * Дополнительная обработка событий не требуется.
 *
 * @author SmorkalovAV
 */
public class GraficsIcon implements Icon {
    
    HashMap<Integer, Grafic> lGrf = new HashMap<>();
    /**
     * максимальный реальный размер графика по Х.
     */
    public int lenX;
    /**
     * максимальный реальный размер графика по У.
     */
    private int lenY;
    /**
     * число вертикальных линий цветом {@code getForeground()}
     */
    private int couV;
    /**
     * число горизонтальных линий цветом {@code getForeground()}
     */
    private int couG;
    /**
     * масштаб изображения по оси Х
     */
    private float mX;
    /**
     * масштаб изображения по оси У
     */
    private float mY = 0f;
    private int [] x = null;
    private int [] y = null;

    private Component c;
    /**
     * испольуем чтобы создать крестики на пересечении линий сетки, достаточно
     * не нулевого объекта
     */
    public Stroke fonStr = null;
    /**
     * цвет горизонтальных линий
     */
    public Color colorGLines = null;
    private int[] aScanMax;
    private int[] aScanMin;
    /**
     * Коэффициент выводимой цифры по х
     */
    private float kfX = 0;
    /**
     * Коэффициент выводимой цифры по у
     */
    private float kfY = 0;
    /**
     * для перевода максимального размера графика в число верт. линий
     */
    private int kft;
    private JWindow wn;
    private JLabel plbP;
    private String vPodp = null;
    private String gPodp = null;
    /**
     * Положение графика на экране относительно иконки,
     * остальные поля оставляем для подписей
     */
    private Rectangle ofs;
    /**
     * Минимальное и максимальное возможное значение реальных
     * величин, из этих величин рассчитываем коэффициент преобразования
     * в экранные координаты.
     */
    private float realMin;   //Минимельная возможная Укоорд реалная м.б < 0.
    private float realMax;  //Максимальная возможная Укоорд реалная.
    private int wei; //ширина иконки в прошлую перерисовку
    private int hei; //высота
    
    private final AbstractListModel lm = new AbstractListModel() {
        
        @Override
        public int getSize() {
            return lGrf.size();
        }
        
        @Override
        public Object getElementAt(int index) {
            return lGrf.get(index);
        }
        
    };
    
    private final JList gl = new JList(lm);
    private final GrafListCellRenderer cr = new GrafListCellRenderer();
    private boolean onMsh = false;
    private Rectangle offsetGrf = null; //Отступы от фона до границы панельки.
    
    /**
     * Разрешение масштабировать график по горизотали и вертикали
     * @param onMsh да - разрешено
     */
    public void setOnMsh(boolean onMsh) {
        this.onMsh = onMsh;
    }


    /**
     * Создание иконки с графиком, подписи под осями не выводятся
     *
     * @param lenX максимальный размер графика по Х - максимальное значения и
     * буфера х-координат, должна быть кратна числу верт. линий {@code couV}
     * @param lenY максимальный размер графика по У - значения и буфера
     * у-координат, должна быть кратна числу гориз. линий {@code couG}
     * @param couV число вертикальных линий цветом {@code GetColor()}
     * @param couG число горизонтальных линий цветом {@code GetColor()}
     */
    public GraficsIcon(int lenX, int lenY, int couV, int couG) {
        this(lenX, 0, lenY, couV, couG, 0, 0, null, null);
    }
    /**
     * Устанавливает предельные реальные значения по у
     * @param min минимальная у координата
     * @param max максимальная у координата
     */
    public void setLimitsRealValue(float min, float max){
        realMax = max;
        realMin = min;
        lenY = (int) (max - min);
        recalkMasshtb();
    }
    /**
     * В этом методе графику передается координата указателя мыши (в координатах
     * компонета, на котором расположена иконка), при попадении указателя на
     * последний график коллекции рядом высвечивается окно с координатами точки
     * графика. При пересоздании иконки этот метод не вызывать.
     *
     * @param e
     */
    public void mouseMoved(MouseEvent e) {
        Point mp = e.getPoint();
        Grafic grf = getGrafic(getGrafsCount() - 1);
        if (grf == null) {
            return;
        }
        int ind = getPntOnGraf(mp);
        if (c != null) {
            if (wn == null) {
                wn = new JWindow(c.getGraphicsConfiguration());
                plbP = new JLabel();
                wn.setSize(40, 40);
                plbP.setSize(wn.getSize());
                wn.add(plbP);
            }
            if (ind > -1) {
                Point pn = new Point(e.getX() + c.getBounds().x + 15, e.getY() + c.getBounds().y + 20);
                wn.setLocation(pn);
                wn.setVisible(true);
                Point pnn = grf.getPoint(ind);
                plbP.setText("<html>" + String.format("%.1f", (float) pnn.x) + "<br>  "
                        + String.format("%.1f", (float) pnn.y) + "</html>");
            } else {
                wn.setVisible(false);
            }
        }
    }

    /**
     * Создание иконки с графикомю Подпись - текст, который выводится внизу и
     * слева около каждой линии сетки графика, содержит численное значение
     * положения линии. Чтобы подписи выводились значения {@code kfX} и
     * {@code kfY} должны быть больше нуля.
     *
     * @param lenX максимальный размер графика по Х - максимальное значения и
     * буфера х-координат, должна быть кратна числу верт. линий {@code couV}
     * @param lenY максимальный размер графика по У - значения и буфера
     * у-координат, должна быть кратна числу гориз. линий {@code couG}
     * @param couV число вертикальных линий цветом {@code GetColor()}
     * @param couG число горизонтальных линий цветом {@code GetColor()}
     * @param kfX множитель текста подписи вертикальных линий сетки
     * @param kfY множитель текста подписи горизонтальных линий сетки
     */
    public GraficsIcon(int lenX, int lenY, int couV, int couG, float kfX, float kfY) {
        this(lenX, lenY, couV, couG, kfX, kfY, null, null);
    }

    /**
     * Создание иконки с графикомю Подпись - текст, который выводится внизу и
     * слева около каждой линии сетки графика, содержит численное значение
     * положения линии. Чтобы подписи около линий выводились значения
     * {@code kfX} и {@code kfY} должны быть больше нуля.
     *
     * @param lenX максимальный размер графика по Х - максимальное значения и
     * буфера х-координат, должна быть кратна числу верт. линий {@code couV}
     * @param lenY максимальный размер графика по У - значения и буфера
     * у-координат, должна быть кратна числу гориз. линий {@code couG}
     * @param couV число вертикальных линий цветом {@code GetColor()}
     * @param couG число горизонтальных линий цветом {@code GetColor()}
     * @param kfX множитель текста подписи вертикальных линий сетки
     * @param kfY множитель текста подписи горизонтальных линий сетки
     * @param pdG подпись под графиком
     * @param pdV подпись слева от графика
     */
    public GraficsIcon(int lenX, int lenY, int couV, int couG, float kfX, float kfY,
            String pdG, String pdV) {
        this.lenX = lenX;
        this.lenY = lenY;
        this.couV = couV;
        this.couG = couG;
        this.kfX = kfX;
        this.kfY = kfY;
        kft = lenX / couV;
        gPodp = pdG;
        vPodp = pdV;
        setupGraphList();
    }

    /**
     * Создание иконки с графикомю Подпись - текст, который выводится внизу и
     * слева около каждой линии сетки графика, содержит численное значение
     * положения линии. Чтобы подписи около линий выводились значения
     * {@code kfX} и {@code kfY} должны быть больше нуля.
     *
     * @param lenX максимальный размер графика по Х - максимальное значения и
     * буфера х-координат, должна быть кратна числу верт. линий {@code couV}
     * @param Ymin минимальное значение графика в массиве У - размер массива должен 
     * быть кратен числу гориз. линий {@code couG}
     * @param Ymax максимальный значение графика в массиве У - размер массива у-координат
     * должен, должен быть кратен числу гориз. линий {@code couG}
     * @param couV число вертикальных линий цветом {@code GetColor()}
     * @param couG число горизонтальных линий цветом {@code GetColor()}
     * @param kfX множитель текста подписи вертикальных линий сетки
     * @param kfY множитель текста подписи горизонтальных линий сетки
     * @param pdG подпись под графиком
     * @param pdV подпись слева от графика
     */
    public GraficsIcon(int lenX, int Ymin, int Ymax, int couV, int couG, float kfX, float kfY,
            String pdG, String pdV) {
        this(lenX, Ymax - Ymin, couV, couG, kfX, kfY, pdG, pdV);
        realMin = Ymin;
        realMax = Ymax;
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
    public void paintIcon(Component c, Graphics g, int x, int y) {
        //если компонент появился, или изменил размеры пересчитываем размер
        if (this.c == null && c != null) {
            this.c = c;
        }
        if(c == null) return;
        
        if (ofs == null || (c.getWidth() != wei
                || c.getHeight() != hei)) {//отрабатываем изменение размера иконки
            if(ofs == null) ofs = new Rectangle();
            ofs.setBounds(0,0,c.getWidth(),c.getHeight());
            if (vPodp != null || gPodp != null) {
                int hi = g.getFontMetrics().getHeight();
                if(gPodp != null)
                    ofs.height -= (hi + 15);
                if(vPodp != null){
                    ofs.width -= (hi + 6);
                    ofs.x += (hi + 5);
                }
            }
            if(offsetGrf != null){
                ofs.x += offsetGrf.x;
                ofs.y += offsetGrf.y;
                ofs.width -= offsetGrf.width + offsetGrf.x;
                ofs.height -= (offsetGrf.height + offsetGrf.y + 2);
            }
            wei = c.getWidth();
            hei = c.getHeight();
            recalkMasshtb();
        }
        
        drawFon((Graphics2D) g);
        Grafic gr;
        Iterator<Grafic> it = lGrf.values().iterator();
        while (it.hasNext()) {
            gr = it.next();
            if (gr == null || !gr.visible || gr.xPo == null) {
                continue;
            }
            drawGraphic((Graphics2D) g, gr);
        }
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
     * Устанавливает отступы графика.
     * @param off отступы, соответственно отступ 
     * слева, сверху, справа и снизу
     */
    public void setOffset(Rectangle off){
        offsetGrf = off;
    }
    /**
     * рисуем сетку графика на графике рисуем цифры около линий по вертикали и
     * горизонтали, обозначающие значение этой линии.
     *
     * @param ig
     */
    private void drawFon(Graphics2D g) {
        int h = c.getHeight();
        int w = c.getWidth();
        int ofsY = h - (ofs.y + ofs.height);
        g.setColor(c.getBackground());
        g.fillRect(0, 0, w, h + 2);
        g.setColor(c.getForeground());
        g.drawRect(ofs.x, ofs.y, ofs.width, ofs.height);
        w = ofs.width;
        h = ofs.height;
        Stroke st = g.getStroke();
        if (fonStr != null) {
            float[] dh = {(float) (h +1)/ couG / 10, (float) (h+1) * 9 / couG / 10};
            Stroke nSt = new BasicStroke(0, BasicStroke.CAP_SQUARE,
                    BasicStroke.JOIN_MITER, 1, dh, 0);
            g.setStroke(nSt);
        }
        Font ft = g.getFont();
        Graphics2D g2 = (Graphics2D) g;
        boolean drwTxt = false;
        //координата рисования линии и коэффициент вывода цифр около линий
        float tx;
        int kf = 1;
        if (kfX > 0 || kfY > 0) {
            float sz = 11;
            if (sz > lenY / couG) {
                sz = lenY / couG;
            }
            if (sz < 11) {
                sz = 11;
            }
            g.setFont(ft.deriveFont(sz));
            drwTxt = true;
        }
        int fnWd = g.getFontMetrics().stringWidth(String.valueOf(couV));
        if ((float)w / couV < fnWd) { //для пропускания линий если маленькое расст.
            kf = (int) Math.ceil(fnWd / ((float)w / couV));
        }
        if (ofsY != 0 && gPodp != null && !gPodp.isEmpty()) {
            g.drawString(gPodp, w / 2 - g.getFontMetrics().stringWidth(gPodp) / 2, h + ofsY / 2 + 4);
        }
        if (colorGLines != null) {
            g.setColor(colorGLines);
        }
        tx = realMin;
        if (tx == 0) {
            float tx1;
            for (int y = 0; y < couV; y++) {
                if (y % kf != 0) {
                    continue;
                }
                int poX = Math.round(((float)w)*y/couV)+ofs.x;
                g.drawLine(poX, -1, poX, h); //Вертикальные линии
                if(drwTxt && kfX != 0){
                    tx1 = lenX * (float)y / couV;
                    if (tx1 - (int) tx1 == 0) {
                        g.drawString(String.format("%d", (int) (tx1 * kfX)), poX + (y<10?4:3), h - 5);
                    } else {
                        g.drawString(String.format("%.1f", tx1 * kfX), poX + (y<10?4:3), h - 5);
                    }
                }
            }
        }
        if (fonStr != null) {
            float[] dh = {(float) w / couV / 10, (float) w * 9 / couV / 10};
            Stroke nSt = new BasicStroke(0, BasicStroke.CAP_SQUARE,
                    BasicStroke.JOIN_MITER, 1, dh, 0);
            g.setStroke(nSt);
        }
        tx = lenY; 
        for (int i = 1; i < couG; i++) {
            int po = Math.round(h*i/couG);
            if(po < ofs.height + ofs.y - 11)
                g.drawLine(ofs.x-1, po, ofs.x + w +1, po); //Горизонтальные линии
            if (drwTxt && kfY != 0) {
                tx -= lenY / couG;
                if (tx - (int) tx == 0) {
                    g.drawString(String.format("%d", (int) (tx * kfY)), 10 + ofsY, po - 3);
                } else {
                    g.drawString(String.format("%.1f", tx * kfY), 10 + ofsY, po - 3);
                }
            }
            if(tx == 0){
                //g.drawLine(ofsY, po+1, w, po+1);
                float tx1;
                for (int y = 1; y < couV; y++) {
                    if (y % kf != 0) {
                        continue;
                    }
                    int poX = koordXToPoint(((float) lenX) / couV * y, 0, true, 1, 0);
//                    g.drawLine(poX, 0, poX, h);
                    if(drwTxt){
                        tx1 = lenX * (float)y / couV;
                        if (tx1 - (int) tx1 == 0) {
                            g.drawString(String.format("%d", (int) (tx1 * kfX)), poX + 1, po - 3);
                        } else {
                            g.drawString(String.format("%.1f", tx1 * kfX), poX + 1, po - 3);
                        }
                    }
                }
            }
        }
        if (ofsY != 0 && drwTxt && vPodp != null && !vPodp.isEmpty()) {
            Font prFn = g.getFont();
            AffineTransform oaf = g2.getTransform();
            AffineTransform af = new AffineTransform(g2.getTransform());
            int wd = g.getFontMetrics().stringWidth(vPodp) / 2;
            if(wd > h - 50){
                af.scale(1, 0.6);
                wd /= 0.6;
            }
            af.rotate(-Math.PI / 2, 2, h / 2 + wd - 20);
            g2.setTransform(af);
            g.drawString(vPodp, 1, h / 2 + wd - 7);
            g.setFont(prFn);
            g2.setTransform(oaf);
        }
        g.setStroke(st);
        // Рисование графика а-Скана
        g.setColor(Color.YELLOW);
        if (aScanMax != null && aScanMin != null) {
            int xa = 0;
            int prevMin = h;
            int prevMax = 0;
            double cx = ((double) w) / ((double) aScanMax.length);
            double cy = ((double) h) / 100.0;
            for (int i = 0; i < aScanMax.length; i++) {
                // Вертикальная координата в системе графических координат
                // в пикселах, соответствующая точке минимума.
                int min = Math.min(aScanMax[i], aScanMin[i]);
                if (min < 0) {
                    min = 0;
                }
                min = h - (int) Math.round(cy * min);
                if (min < prevMax) {
                    min = prevMax;
                }
                int max = Math.max(aScanMax[i], aScanMin[i]);
                if (max > 100) {
                    max = 100;
                }
                max = h - (int) Math.round(cy * max);
                if (max > prevMin) {
                    max = prevMin;
                }
                if (max == min && max > 0) {
                    max--;
                }
                if (max == min && min < h) {
                    min++;
                }
                int wa = (int) Math.round(cx * (i + 1)) - xa;
                g.fillRect(xa, max, wa, min - max);
                xa += wa;
                prevMax = max;
                prevMin = min;
            }
        }
        g.setFont(ft);
    }

    /**
     * Добавляем графики для отображения
     *
     * @param xPnt массив x точек графика по каналам
     * @param yPnt массив у точек граффика по каналам
     * @param clr цвета графиков по каналам
     * @param bas начало х коорд. на графике, мм
     * @param direct направление построения true - прямое
     * @param str если рисуем пунктиром
     */
    public void addGrafs(float[][] xPnt, float[][] yPnt,
            Color[] clr, int bas, boolean direct, Stroke[] str) {
        for (int i = 0; i < xPnt.length; i++) {
            Grafic gr = new Grafic(clr[i], xPnt[i], yPnt[i], bas, direct, str == null ? null : str[i]);
            lGrf.put(i, gr);
            gl.setSelectedIndex(i);
        }
        if (c != null) {
            c.repaint();
        }
    }

    /**
     * Добавляем график в следующуу после последней позицию в коллекции графиков
     * для отображения
     *
     * @param gr график
     */
    public void addGrafic(Grafic gr) {
        lGrf.put(lGrf.size(), gr);
        if (gr.visible){
            gl.setSelectedIndex(lGrf.size());
        }
        if (c != null) {
            c.repaint();
        }
    }

    /**
     * Возвращает число отрисовываемых графиков
     *
     * @return число отрисовываемых графиков
     */
    public int getGrafsCount() {
        return lGrf.size();
    }

    /**
     * Включает выбранные графики в прорисовку перерисовывает вызывающая
     * программа
     *
     * @param onGrf выбранные для рисования графики, д.б. равно числу графиков
     */
    public void setOnGrafs(boolean[] onGrf) {
        for (int i = 0; i < onGrf.length && i < lGrf.size(); i++) {
            lGrf.get(i).visible = onGrf[i];
        }
    }

    /**
     * масштам отрисовки по оси Y, изменяет в зависимости от размеров
     * измображения
     *
     * @return масштам отрисовки по оси Y
     */
    public float getMY() {
        return mY;
    }

    /**
     * возвращает высоту контрола
     *
     * @return высота контрола
     */
    public float getHeight() {
        return c.getHeight();
    }

    /**
     * изменяем график в определенной позиции коллекции графиков
     *
     * @param i номер позиции графика
     * @param gr новый график
     */
    public void addGrafic(int i, Grafic gr) {
        lGrf.put(i, gr);
//        if (gr.visible){
//            gl.setSelectedIndex(i);
//        }
        if (c != null) {
            c.repaint();
        }
    }

    /**
     * возвращает график из коллекции графиков
     *
     * @param i номер позиции графика
     * @return график
     */
    public Grafic getGrafic(int i) {
        return lGrf.get(i);
    }
    
    public void repaint() {
        if (c != null) {
            c.repaint();
        }
    }
    
    public HashMap getGrafs() {
        return lGrf;
    }
    
    public void setAScan(int[] aMax, int[] aMin) {
        aScanMax = aMax;
        aScanMin = aMin;
    }

    /**
     * Удаляем все графики иконки
     */
    public void removeGrafics() {
        lGrf.clear();
    }

    /**
     * иконка графиков, для отчета
     *
     * @param re размер иконки
     * @return иконка трубы
     */
    public Image getImage(Rectangle re) {
        BufferedImage bp = new BufferedImage(re.width, re.height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bp.getGraphics();
        JLabel cc = new JLabel();
        cc.setBounds(re);
        cc.setBackground(Color.WHITE);
        cc.setForeground(Color.GRAY);
        Component oldc = c;
        g.setClip(0, 0, re.width, re.height);
        c = cc;
        paintIcon(cc, g, 0, 0);
        c = oldc;
        return bp;
    }

    /**
     * Установка максимальной реальнаяой х координаты графика, мм
     *
     * @param begLength максимальная реальная х координата графика
     */
    public void setLen(float begLength) {
        lenX = (int) begLength;
        //масштаб пересчитываем если изменилось колическтво верт. линий
        if (couV != (int) lenX / kft + 1) {
            couV = lenX / kft + 1;
            if (c != null && c.getWidth() > 0) {
                mX = (float) c.getWidth() / (couV * 1000);
            }
        }
    }

    public void setNewLen(int begLength, int nlines) {
        couV = nlines;
        lenX = begLength;
        kft = lenX / couV;
        if(ofs != null){
            mX = (float) ofs.width / lenX;
        }
    }

    public void setNewHight(int height, int nlines) {
        couG = nlines;
        lenY = height;
        if(ofs != null){
            mY = (float) ofs.height / lenY;
        }
    }

    /**
     * Преобразование буфера х-координат в пиксел графика
     *
     * @param xPo точка буфера
     * @param bas смещение графика в координатак буфера
     * @param direct направление рисования по х-координата
     * @param msX допалнительный коэффициент пересчета
     * @param ofsX смещение графика в пикселах
     * @return координату в пикселах
     */
    protected int koordXToPoint(float xPo, int bas, boolean direct, float msX, int ofsX) {
        if (direct) {
            return Math.round((bas + xPo) * mX * msX + ofsX + ofs.x);
        } else {
            return Math.round((bas - xPo) * mX * msX + ofsX + ofs.x);
        }
    }

    /**
     * Преобразование буфера y-координат в пиксел графика
     *
     * @param yPo точка буфера
     * @param msY допалнительный коэффициент пересчета
     * @param ofsY смещение графика в пикселах
     * @return координату в пикселах
     */
    protected int koordYToPoint(float yPo, float msY, int ofsY) {
        if(realMin < 0){
            yPo += Math.abs(realMin);
        }
        return Math.round(hei - yPo * mY * msY - ofsY - (hei - (ofs.height + ofs.y)));
    }

    /**
     * Формирует компонент со списком графиков для управления видимостью.
     */
    private void setupGraphList() {
        
        gl.setCellRenderer(cr);
        gl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        gl.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    for (int i = 0; i < lGrf.size(); i++) {
                        Grafic gr = lGrf.get(i);
                        gr.visible = gl.isSelectedIndex(i);
                    }
                    repaint();
                }
            }
        });
    }

    /**
     * Возвращает компонент {@code JList} со списком графиков для управления их
     * видимостью. Компонент имеет встроенный рендерер для отображения графиков
     * с их цветами и связан непосредственно с коллекцией графиков. Таким
     * образом, изменение признака видимости графика в данном компоненте сразу
     * приводит к перерисовке иконки и не требует дополнительного программирования.
     *
     * @return список графиков иконки.
     */
    public JList getGraphList(){
        return gl;
    }
    public int getPntOnGraf(Point mp) {
//        if(hei == 0) return -1;
//        for(int i=0;i<grLen;i++){
//            int gx = Math.round((bas + xPo[i]) * mx * msX + ofsX);
//            int gy = Math.round(hei - yPo[i] * my * msY);
//            Rectangle re = new Rectangle(gx-3, ofsY, 6, (int) hei);
//            if(re.contains(mp)){
//                return i;
//            }
//        }
        return -1;
    }
    

    private void drawGraphic(Graphics2D g, Grafic gr) {
        if(!gr.visible) {
            return;
        }
        hei = c.getHeight();
        g.setColor(gr.clr);
        Stroke st = g.getStroke();
        if(gr.str != null){
            st = g.getStroke();
            g.setStroke(gr.str);
        }
        if(x == null){
            x = new int[gr.xPo.length];
            y = new int[gr.yPo.length];
        }
        if(x.length <= gr.xPo.length){
            x = Arrays.copyOf(x, gr.xPo.length + 10);
            y = Arrays.copyOf(y, gr.yPo.length + 10);
        }
        int po = 0;
        
        for(float xx : gr.xPo){
            x[po] = koordXToPoint(xx, 0, true, 1, 0);
            y[po] = koordYToPoint(gr.yPo[po], 1, 0);
            if(x[po] == 0 && po != 0)x[po] = 1;
            if(x[po] >= ofs.width + ofs.x){//вышли за график по горизонтали, перерисовываем
                if(onMsh){
                    setLen(xx);
                    repaint();
                    return;
                } else {
                    x[po] = ofs.width + ofs.x;
                }
            }//вышли за график по вертикали
            if((y[po] < 0 || y[po] > ofs.height - ofs.y)){
                if(onMsh){
                    if(y[po] < 0){
                        setLimitsRealValue(realMin, gr.yPo[po]);
                    }
                    if(y[po] > hei){
                        setLimitsRealValue(gr.yPo[po], realMax);
                    }
                    repaint();
                    return;
                } else {
                    if(y[po] < 0) y[po] = 0;
                    else y[po] = ofs.height - ofs.y;
                }
            }
            po++;
        }
        if(gr.isClosedGraph()){
            g.setColor(new Color(gr.clr.getRed(), gr.clr.getGreen(), gr.clr.getBlue(), gr.alpha));
            g.fillPolygon(x, y, gr.grLen);
        }else{
            g.drawPolyline(x, y, gr.grLen);
        }
        g.setStroke(st);
        
    }

    /**
     * Перерасчет масштабных коэффициентов по обеим осям
     * на основе ширины высоты области рисования и максимальной
     * реальной х и у координаты графика.
     */
    private void recalkMasshtb() {
        if(ofs == null) return;
        if (lenX != 0) {
            mX = (float) ofs.width / lenX;
        } else {
            mX = 1;
        }
        if (lenY != 0) {
            mY = (float) ofs.height / lenY;
        } else {
            mY = 1;
        }
    }
    /**
     * Класс для изобоажения графика в списке в виде чекбокса цветом графика и
     * признаком выделения соответствующим видимости графика.
     */
    public class GrafListCellRenderer extends JCheckBox implements ListCellRenderer<Object> {

        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Grafic go = (Grafic) value;
            setText(go.name);
            setForeground(go.clr);
            setBackground(list.getBackground());
            setSelected(go.visible);
            return this;
        }
        
    }
    
}
