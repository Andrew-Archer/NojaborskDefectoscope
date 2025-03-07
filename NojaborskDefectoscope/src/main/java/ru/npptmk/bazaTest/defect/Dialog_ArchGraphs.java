package ru.npptmk.bazaTest.defect;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.npptmk.bazaTest.defect.model.BasaTube;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author RazumnovAA
 */
public class Dialog_ArchGraphs extends javax.swing.JDialog {

    /**
     * Creates new form dialog_ArchGraphs
     */
    public Dialog_ArchGraphs(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        gridLayout = new GridLayout(5, 1);
        getContentPane().setLayout(gridLayout);
        chartPanel = new ChartPanel(configureDefectsGraph(createDataset(), "ЦЕА", "Н", "Y", 10000F));
        chartPanel.setPreferredSize(new Dimension(300, 100));

        chartPane2 = new ChartPanel(configureDefectsGraph(createDataset(), "ЦЕА", "Н", "Y", 10000F));
        chartPane2.setPreferredSize(new Dimension(300, 100));
        chartPane3 = new ChartPanel(configureDefectsGraph(createDataset(), "ЦЕА", "Н", "Y", 10000F));
        chartPane3.setPreferredSize(new Dimension(300, 100));
        chartPane4 = new ChartPanel(configureDefectsGraph(createDataset(), "ЦЕА", "Н", "Y", 10000F));
        chartPane4.setPreferredSize(new Dimension(300, 100));
        chartPane5 = new ChartPanel(configureDefectsGraph(createDataset(), "ЦЕА", "Н", "Y", 10000F));
        chartPane5.setPreferredSize(new Dimension(300, 100));
        chartPane5.setDisplayToolTips(true);
        chartPane5.setInitialDelay(1);

        getContentPane().add(chartPanel);
        getContentPane().add(chartPane2);
        getContentPane().add(chartPane3);
        getContentPane().add(chartPane4);
        getContentPane().add(chartPane5);
        setVisible(true);

    }
    private GridLayout gridLayout;

    public ChartPanel chartPanel = null;
    public ChartPanel chartPane2 = null;
    public ChartPanel chartPane3 = null;
    public ChartPanel chartPane4 = null;
    public ChartPanel chartPane5 = null;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setTitle("ГРАФИКИ ДЕФЕКТОСКОПИИ");
        setMaximumSize(new java.awt.Dimension(1280, 1024));
        setMinimumSize(new java.awt.Dimension(200, 325));
        setPreferredSize(new java.awt.Dimension(400, 1000));
        setSize(new java.awt.Dimension(400, 1000));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void updateGraphs(BasaTube tube, TubeType tubeType) {
        List<BazaDefectResults> results = tube.getTubeResults();
        XYDataset mdDataSet = createMDDataset(tube, tubeType);
        chartPanel.setChart(configureMDGraph(
                mdDataSet,
                "Магнитная дефектоскопия",
                "Координата, мм",
                "Уровень сигнала",
                tube.getLengthInMeters() * 1000)
        );
        XYDataset thickDataSet = createThickDataset(tube);
        chartPane2.setChart(configureThickGraph(
                thickDataSet,
                "Толщиномер\n ",
                "Координата, мм",
                "Толщина, мм",
                tube.getLengthInMeters() * 1000)
        );
        XYDataset lengthWiseDataSet = createLengthWise(tube);
        chartPane3.setChart(configureLengthWiseGraph(
                lengthWiseDataSet,
                "Продольные дефекты",
                "Координата, мм",
                "Уровень сигнала",
                tube.getLengthInMeters() * 1000)
        );
        XYDataset crossWiseDataSet = createCrossWise(tube);
        chartPane4.setChart(configureLengthWiseGraph(
                crossWiseDataSet,
                "Поперечные дефекты",
                "Координата, мм",
                "Уровень сигнала",
                tube.getLengthInMeters() * 1000)
        );
        XYDataset defectsDataSet = createDefectsDataset(tube);
        chartPane5.setChart(configureDefectsGraph(
                defectsDataSet,
                "Расположение дефектов",
                "Координата, мм",
                " ",
                tube.getLengthInMeters() * 1000)
        );
        this.setVisible(true);
    }

    private XYDataset createMDDataset(BasaTube tube, TubeType tubeType) {
        BazaMDResult basaResult = tube.getTubeResults().get(0).mdRes;
        XYSeriesCollection dataset = new XYSeriesCollection();
        //Массивы для хранения графика
        float[][] x = new float[8][];
        float[][] y = new float[8][];
        //Зполняем каждый из 8 каналов графика
        for (int i = 0; i < 8; i++) {
            //Создаем график для текущего канала
            XYSeries currentChanel = new XYSeries("Канал " + i);

            y[i] = new float[basaResult.getGraficLength(i)];
            x[i] = new float[basaResult.getGraficLength(i)];
            //Копируем график из результатов
            basaResult.getGrafic(i, x[i], y[i]);
            for (int j = 0; j < basaResult.getGraficLength(i); j++) {
                //Добавляем точку в график текущего канала
                currentChanel.add(x[i][j], y[i][j]);
            }
            //Добавляем в датасет ссылку на созданный график
            dataset.addSeries(currentChanel);
        }
        //Добавляем уровень сигнала, привышение которого считается дефектом.
        int[] magneticBorders = tubeType.getParamsMD().porog;
        for (int i = 0; i < magneticBorders.length; i++) {
            //Создаем график для текущего канала
            XYSeries defectsLimit = new XYSeries("Граница дефектов " + i);
            defectsLimit.add(0, magneticBorders[i]);
            defectsLimit.add(tube.getLengthInMeters() * 1000, magneticBorders[i]);
            dataset.addSeries(defectsLimit);
        }

        return dataset;
    }

    private XYDataset createThickDataset(BasaTube tube) {
        BazaUSDResult thickRes = tube.getTubeResults().get(0).usk2Res;
        XYSeriesCollection dataset = new XYSeriesCollection();
        //Массивы для хранения графика
        float[][] y = new float[4][];
        float[][] x = new float[4][];

        for (int i = 0; i < 4; i++) {
            //Создаем график для текущего канала
            XYSeries currentChanel = new XYSeries("Канал " + i);

            y[i] = new float[thickRes.getThickGrafLength(i)];
            x[i] = new float[thickRes.getThickGrafLength(i)];
            thickRes.getThickGrafic(i + 4, x[i], y[i]);
            for (int j = 0; j < thickRes.getThickGrafLength(i); j++) {
                //Добавляем точку в график текущего канала
                currentChanel.add(x[i][j], y[i][j]);
            }
            //Добавляем в датасет ссылку на созданный график
            dataset.addSeries(currentChanel);
        }
        //Задаем пороги толщин
        float[] thickBorders = tube.getTubeResults().get(0).tbRes.getTubeThicks();
        for (int i = 0; i < thickBorders.length; i++) {
            //Если прого не равен 0
            if (thickBorders[i] != 0) {
                XYSeries defectsLimit = new XYSeries("Границы толщин" + i);
                defectsLimit.add(0, thickBorders[i]);
                defectsLimit.add(tube.getLengthInMeters() * 1000, thickBorders[i]);
                dataset.addSeries(defectsLimit);
            }
        }
        XYSeries defectsLimit = new XYSeries("Потеря аккустического контакта");
        defectsLimit.add(0, 3.0);
        defectsLimit.add(tube.getLengthInMeters() * 1000, 3.0);
        dataset.addSeries(defectsLimit);
        return dataset;
    }

    private XYDataset createDefectsDataset(BasaTube tube) {
        BazaTubeResult defects = tube.getTubeResults().get(0).tbRes;
        XYSeriesCollection dataset = new XYSeriesCollection();
        //Лист для хранения графиков для каждого канала
        List<XYSeries> serieses = new ArrayList<>();
        //Создаем график для текущего канала
        serieses.add(new XYSeries("МД"));
        serieses.add(new XYSeries("Продолные/поперечные"));
        serieses.add(new XYSeries("Толщина"));
        for (int i = 0; i < 3; i++) {
            //Добавляем в датасет ссылку на созданный график
            dataset.addSeries(serieses.get(i));
            for (Float defectPosition : defects.getDefects(i)) {
                //Добавляем точку в график текущего канала
                serieses.get(i).add((Number) defectPosition, 100F);
            }
        }
        return dataset;
    }

    /**
     * Создает дата сет для графиков продольных дефектов.
     *
     * @param lengWiseRes результаты для создания DataSet
     * @return график продольных дефектов
     */
    private XYDataset createLengthWise(BasaTube tube) {
        BazaUSDResult lengWiseRes = tube.getTubeResults().get(0).usk1Res;
        XYSeriesCollection dataset = new XYSeriesCollection();
        //Массивы для хранения графика
        float[][] y = new float[6][];
        float[][] x = new float[6][];
        for (int i = 0; i < 6; i++) {
            //Создаем график для текущего канала
            XYSeries currentChannel = new XYSeries("Канал " + i);

            y[i] = new float[lengWiseRes.getGraficLength(i)];
            x[i] = new float[lengWiseRes.getGraficLength(i)];
            lengWiseRes.getGrafic(i, x[i], y[i]);
            for (int j = 0; j < lengWiseRes.getGraficLength(i); j++) {
                //Добавляем точку в график текущего канала
                currentChannel.add(x[i][j], y[i][j]);
            }
            //Добавляем в датасет ссылку на созданный график
            dataset.addSeries(currentChannel);
        }

        //Добавляем уровень сигнала, привышение которого считается дефектом.
        //Создаем график для текущего канала
        BazaUSDResult basaUsdResult = tube.getTubeResults().get(0).usk1Res;
        for (int i = 0; i < 6; i++) {
            XYSeries defectsLimit = new XYSeries("Граница дефектов " + i);
            defectsLimit.add(0, basaUsdResult.getThreshold(i));
            defectsLimit.add(tube.getLengthInMeters() * 1000, basaUsdResult.getThreshold(i));
            dataset.addSeries(defectsLimit);
        }
        return dataset;
    }

    /**
     * Создает дата сет для графиков поперечных дефектов.
     *
     * @param usk1 результаты для создания DataSet
     * @return график поперечных дефектов
     */
    private XYDataset createCrossWise(BasaTube tube) {
        BazaUSDResult usk2 = tube.getTubeResults().get(0).usk2Res;
        BazaUSDResult usk1 = tube.getTubeResults().get(0).usk1Res;
        XYSeriesCollection dataset = new XYSeriesCollection();
        //Массивы для хранения графика
        float[][] y = new float[6][];
        float[][] x = new float[6][];
        //Лист для хранения графиков для каждого канала
        List<XYSeries> serieses = new ArrayList<>();
        //Берем графикии из 2 блока УЗК
        for (int i = 0; i < 4; i++) {
            //Создаем график для текущего канала
            serieses.add(new XYSeries("Канал " + i));
            //Добавляем в датасет ссылку на созданный график
            dataset.addSeries(serieses.get(i));
            y[i] = new float[usk2.getGraficLength(i)];
            x[i] = new float[usk2.getGraficLength(i)];
            usk2.getGrafic(i, x[i], y[i]);
            for (int j = 0; j < usk2.getGraficLength(i); j++) {
                //Добавляем точку в график текущего канала
                serieses.get(i).add(x[i][j], y[i][j]);
            }
        }
        //Берем оставшиеся графики из 1 блока
        for (int i = 4; i < 6; i++) {
            //Создаем график для текущего канала
            serieses.add(new XYSeries("Канал " + (i)));
            //Добавляем в датасет ссылку на созданный график
            dataset.addSeries(serieses.get(i));
            y[i] = new float[usk1.getGraficLength(i + 2)];
            x[i] = new float[usk1.getGraficLength(i + 2)];
            usk1.getGrafic(i + 2, x[i], y[i]);
            for (int j = 0; j < usk1.getGraficLength(i + 2); j++) {
                //Добавляем точку в график текущего канала
                serieses.get(i).add(x[i][j], y[i][j]);
            }
        }
        //Добавляем уровень сигнала, привышение которого считается дефектом.
        //Создаем график для текущего канала
        BazaUSDResult basaUsd1Result = tube.getTubeResults().get(0).usk1Res;
        BazaUSDResult basaUsd2Result = tube.getTubeResults().get(0).usk2Res;
        for (int i = 6; i < 8; i++) {
            XYSeries defectsLimit = new XYSeries("Граница дефектов " + i);
            defectsLimit.add(0, basaUsd1Result.getThreshold(i));
            defectsLimit.add(tube.getLengthInMeters() * 1000, basaUsd1Result.getThreshold(i));
            dataset.addSeries(defectsLimit);
        }
        for (int i = 0; i < 4; i++) {
            XYSeries defectsLimit = new XYSeries("Граница дефектов " + i);
            defectsLimit.add(0, basaUsd2Result.getThreshold(i));
            defectsLimit.add(tube.getLengthInMeters() * 1000, basaUsd2Result.getThreshold(i));
            dataset.addSeries(defectsLimit);
        }
        return dataset;
    }

    private XYDataset createDataset() {
        XYSeries series1 = new XYSeries("First");
        series1.add(1.0, 1.0);
        series1.add(2.0, 4.0);
        series1.add(3.0, 3.0);
        series1.add(4.0, 5.0);
        series1.add(5.0, 5.0);
        series1.add(6.0, 7.0);
        series1.add(7.0, 7.0);
        series1.add(8.0, 8.0);
        XYSeries series2 = new XYSeries("Second");
        series2.add(1.0, 5.0);
        series2.add(2.0, 7.0);
        series2.add(3.0, 6.0);
        series2.add(4.0, 8.0);
        series2.add(5.0, 4.0);
        series2.add(6.0, 4.0);
        series2.add(7.0, 2.0);
        series2.add(8.0, 1.0);
        XYSeries series3 = new XYSeries("Third");
        series3.add(3.0, 4.0);
        series3.add(4.0, 3.0);
        series3.add(5.0, 2.0);
        series3.add(6.0, 3.0);
        series3.add(7.0, 6.0);
        series3.add(8.0, 3.0);
        series3.add(9.0, 4.0);
        series3.add(10.0, 3.0);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        return dataset;
    }

    private JFreeChart configureDefectsGraph(
            XYDataset dataset,
            String Caption,
            String xCaption,
            String yCaption,
            Float tubeLength) {
        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                Caption, // chart title
                xCaption, // x axis label
                yCaption, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                false // urls
        );
        // get a reference to the plot for further customisation...
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer
                = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseToolTipGenerator(new XYToolTipGenerator() {
            @Override
            public String generateToolTip(XYDataset xyd, int i, int i1) {
                return (String) ((XYSeries) ((XYSeriesCollection) xyd).getSeries().get(i)).getKey() + " на " + xyd.getXValue(i, i1);
            }

        });
        //Устанавливаем длину оси х
        plot.getDomainAxis().setRange(0, tubeLength / 100 * 100);
        renderer.setSeriesShape(0, new Rectangle2D.Double(0, 0, 1, 100));
        renderer.setSeriesShape(1, new Rectangle2D.Double(0, 0, 1, 100));
        renderer.setSeriesShape(2, new Rectangle2D.Double(0, 0, 1, 100));
        renderer.setBaseShapesVisible(true);
        renderer.setBaseLinesVisible(false);
        return chart;
    }

    private JFreeChart configureMDGraph(
            XYDataset dataset,
            String Caption,
            String xCaption,
            String yCaption,
            Float tubeLength) {
        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                Caption, // chart title
                xCaption, // x axis label
                yCaption, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                false // urls
        );
        // get a reference to the plot for further customisation...
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer
                = (XYLineAndShapeRenderer) plot.getRenderer();
        //Устанавливаем генератор подсказок
        renderer.setBaseToolTipGenerator(new XYToolTipGenerator() {
            @Override
            public String generateToolTip(XYDataset xyd, int i, int i1) {
                return (String) ((XYSeries) ((XYSeriesCollection) xyd).getSeries().get(i)).getKey() + " на " + xyd.getXValue(i, i1);
            }

        });
        //Устанавливаем длину оси х
        plot.getDomainAxis().setRange(0, tubeLength / 100 * 100);
        //Устанавливаем длину оси y
        //plot.getRangeAxis().setRange(0, 100);
        return chart;
    }

    private JFreeChart configureLengthWiseGraph(
            XYDataset dataset,
            String Caption,
            String xCaption,
            String yCaption,
            Float tubeLength) {
        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                Caption, // chart title
                xCaption, // x axis label
                yCaption, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                false // urls
        );
        // get a reference to the plot for further customisation...
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer
                = (XYLineAndShapeRenderer) plot.getRenderer();
        //Устанавливаем генератор подсказок
        renderer.setBaseToolTipGenerator(new XYToolTipGenerator() {
            @Override
            public String generateToolTip(XYDataset xyd, int i, int i1) {
                return (String) ((XYSeries) ((XYSeriesCollection) xyd).getSeries().get(i)).getKey() + " на " + xyd.getXValue(i, i1);
            }

        });
        //Устанавливаем длину оси х
        plot.getDomainAxis().setRange(0, tubeLength / 100 * 100);
        //Устанавливаем длину оси y
        //plot.getRangeAxis().setRange(0, 100);
        return chart;
    }

    private JFreeChart configureThickGraph(
            XYDataset dataset,
            String Caption,
            String xCaption,
            String yCaption,
            Float tubeLength) {
        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                Caption, // chart title
                xCaption, // x axis label
                yCaption, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                false // urls
        );
        // get a reference to the plot for further customisation...
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer
                = (XYLineAndShapeRenderer) plot.getRenderer();
        //Устанавливаем генератор подсказок
        renderer.setBaseToolTipGenerator(new XYToolTipGenerator() {
            @Override
            public String generateToolTip(XYDataset xyd, int i, int i1) {
                return (String) ((XYSeries) ((XYSeriesCollection) xyd).getSeries().get(i)).getKey() + " на " + xyd.getXValue(i, i1);
            }

        });
        //Устанавливаем длину оси х
        plot.getDomainAxis().setRange(0, tubeLength / 100 * 100);
        return chart;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
