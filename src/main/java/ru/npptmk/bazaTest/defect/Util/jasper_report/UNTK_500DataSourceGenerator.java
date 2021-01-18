/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.Util.jasper_report;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Класс для генерации DataSource для отчета УНКТ-500 в городе Ноябрьске.
 * <p>
 * Использование:
 * <p>
 *
 * //TODO Реализовать методы setStratXForBorder setEndXForBorder.
 *
 * @author RazumnovAA
 */
public class UNTK_500DataSourceGenerator {

    private final List<XYChartPointBean> defects;
    private final Map<SENSOR_TYPES, List<XYChartPointBean>> sensorsSignals;
    private final String CHANEL_BORDER_NAME_ADDITION = " Порог";
    private Double graphLength;
    private LocalDateTime defectDetectionTime = LocalDateTime.MIN;
    private Long tubeNumber = 0L;
    private final String DEFECT_CHART_NAME = "ПОЛОЖЕНИЕ ДЕФЕКТОВ НА ТРУБЕ";
    private final String MAGNETIC_CHART_NAME = "ИЗМЕНЕНИЕ СИГНАЛА ОТ МАГНИТНЫХ ДАТЧИКОВ ПО ДЛИНЕ ТРУБЫ";
    private final String LENGTHWISE_CHART_NAME = "ИЗМЕНЕНИЕ СИГНАЛА ОТ ДАТЧИКОВ ПРОДОЛЬНЫХ ДЕФЕКТОВ ПО ДЛИНЕ ТРУБЫ";
    private final String CONTROVERSAL_CHART_NAME = "ИЗМЕНЕНИЕ СИГНАЛА ОТ ДАТЧИКОВ ПОПЕРЕЧНЫХ ДЕФЕКТОВ ПО ДЛИНЕ ТРУБЫ";
    private final String THICKNESS_CHART_NAME = "ИЗМЕНЕНИЕ СИГНАЛА ОТ ТОЛЩИНОМЕРОВ ПО ДЛИНЕ ТРУБЫ";

    public LocalDateTime getDefectDetectionTime() {
        return defectDetectionTime;
    }

    public void setDefectDetectionTime(LocalDateTime defectDetectionTime) {
        this.defectDetectionTime = defectDetectionTime;
    }

    public Long getTubeNumber() {
        return tubeNumber;
    }

    public void setTubeNumber(Long tubeNumber) {
        this.tubeNumber = tubeNumber;
    }

    private final Map<SENSOR_TYPES, Map<String, List<XYChartPointBean>>> borders;

    private void setStratXForBorder(
            SENSOR_TYPES sensorType,
            String chanelName,
            Double position) {
        throw new RuntimeException("Метод не реализован.");
    }

    /**
     * Позволяет получить последнее установленное значение длины графика.
     *
     * @return Длина графика в мм трубы.
     */
    public Double getGraphLength() {
        return graphLength;
    }

    /**
     * Устанавливает максимальную длину графика.
     * <p>
     * Каждый раз, когда вы добавляете точку графика это значение может быть
     * изменено, если значение координаты x вновь добавленной точки больше
     * установленного Вами значения.
     *
     * @param graphLength Длина трубы на графике в мм.
     */
    public void setGraphLength(Double graphLength) {
        this.graphLength = graphLength;
    }

    private void setEndXForBorder(
            SENSOR_TYPES sensorType,
            String chanelName,
            Double position) {
        throw new RuntimeException("Метод не реализован.");
    }

    public UNTK_500DataSourceGenerator() {
        defects = new ArrayList<>();
        sensorsSignals = new HashMap<>();
        borders = new HashMap<>();
        for (SENSOR_TYPES sensorType : SENSOR_TYPES.values()) {
            borders.put(sensorType, new HashMap<>());
            sensorsSignals.put(sensorType, new ArrayList<>());
        }
        graphLength = 5000.0;
    }

    /**
     * Возможные типы датчиков.
     * <p>
     * Сигналы с каждого типа датчиков будут выводится на отдельном графике.
     */
    public static enum SENSOR_TYPES {
        /**
         * Сигнал с магнитных датчиков.
         */
        MAGNETIC,
        /**
         * Сигнал с толщиномеров.
         */
        THICKNESS,
        /**
         * Сигнал с датчиков продольных дефектов.
         */
        TRANSVERSAL,
        /**
         * Сигнал с датчиков поперечных дефектов.
         */
        LENGTHWISE
    }

    /**
     * Добавляет дефект к набору дефектов.
     *
     * @param defectType строка для названия типа дефекта. На графике дефекты
     * сгруппируются по именам типов дефектов.
     * @param yHeight Если хотите, чтобы дефекты не сливались, то выводите их на
     * разной высоте по шкале Y.
     * @param position место положения дефекта по длине трубы.
     */
    public void addDefectPoint(
            String defectType,
            int yHeight,
            Double position) {
        defects.add(new XYChartPointBean(defectType, position, (double) yHeight));
    }

    /**
     * Добавляет значение сигнала от магнитного датчика.
     *
     * @param chanelName Имя канала на котором сидит датчик. Данные на графике
     * сгруппируются по именам каналов.
     * @param sensorType тип датчика с которого пришел сигнал.
     * @param signalLevel Уровень сигнала полученный от датчика.
     * @param position место положения сигнала по длине трубы.
     */
    public void addSignalPoint(
            String chanelName,
            SENSOR_TYPES sensorType,
            Double signalLevel,
            Double position) {
        sensorsSignals
                .get(sensorType)
                .add(new XYChartPointBean(chanelName, position, signalLevel));
        //Обновляем длину графика если она меньше значения x координаты
        //переданной точки сигнала.
        if (graphLength < position) {
            graphLength = position;
        }
    }

    /**
     * Задает порог уровня сигнала для указанного канала и типа датчика.
     * <p>
     * Данный порог буде выведен как прямая горизонтальная линия ограничивающая
     * сигнал.
     *
     * @param chanelName имя канала для которого задается порог.
     * @param sensorType тип датчика для которого задается порог.
     * @param borderValue значение ограничивающие уровень сигнала.
     */
    public void setSignalBorder(
            String chanelName,
            SENSOR_TYPES sensorType,
            Double borderValue) {
        chanelName = chanelName + CHANEL_BORDER_NAME_ADDITION;
        //Если граница для такого типа датчиков уже есть
        if (borders.containsKey(sensorType)) {
            //Если граница для такого канала уже существует
            if (borders.get(sensorType).containsKey(chanelName)) {
                borders.get(sensorType).get(chanelName).get(0).setYValue(borderValue);
                borders.get(sensorType).get(chanelName).get(1).setYValue(borderValue);
            } else {//Если границы для заданного канала не существует
                List<XYChartPointBean> newBorder = new ArrayList<>();
                newBorder.add(new XYChartPointBean(chanelName, 0.0, borderValue));
                newBorder.add(new XYChartPointBean(chanelName, 0.0, borderValue));
                borders.get(sensorType).put(chanelName, newBorder);
            }
        } else {//Если такого типа датчиков не существует
            JOptionPane.showMessageDialog(
                    null,
                    "Вы пытаетесь задать границу"
                    + "сигнала для несуществующего типа датчиков.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Обновляет длину границ по длине графиков.
     */
    private void updateBordersLength() {
        //Ищем самы длинный график и сохгаяем это значение
        for (SENSOR_TYPES sensorType : SENSOR_TYPES.values()) {
            sensorsSignals.get(sensorType).forEach((XYChartPointBean graphPoint) -> {
                //Если максимальная длина графика меньше координыты x текущей точки
                if (graphLength < graphPoint.getxValue()) {
                    //Записываем x координату текущей точки в максимальную длину
                    graphLength = graphPoint.getxValue();
                }
            });
        }
        //Обновляем x координату второй точки границ в соответствии с длиной графика
        for (SENSOR_TYPES sensorType : SENSOR_TYPES.values()) {
            borders.get(sensorType).keySet().forEach((String chanelName) -> {
                borders.get(sensorType).get(chanelName).get(1).setxValue(graphLength);
            });

        }
    }

    public JRBeanCollectionDataSource generateDataSource() {
        //Обновляем длину границ по x
        updateBordersLength();
        List<XYChartBean> result = new ArrayList<>();
        result.add(new XYChartBean(
                DEFECT_CHART_NAME,
                null,
                graphLength.floatValue() * 1.05f,
                true,
                true,
                new JRBeanCollectionDataSource(defects))
        );

        for (SENSOR_TYPES sensorType : SENSOR_TYPES.values()) {
            //Обоъединяем пороги и сигналы
            List<XYChartPointBean> signalAndBorders = new ArrayList<>();
            signalAndBorders.addAll(sensorsSignals.get(sensorType));
            //Добавляем ограничения сигналов
            borders.get(sensorType).keySet().forEach((String chanelName) -> {
                signalAndBorders.addAll(borders.get(sensorType).get(chanelName));
            });
            //Добавляем график сигналов.
            switch (sensorType) {
                case MAGNETIC: {
                    result.add(new XYChartBean(
                            MAGNETIC_CHART_NAME,
                            100.0f,
                            graphLength.floatValue() * 1.05f,
                            true,
                            true,
                            new JRBeanCollectionDataSource(signalAndBorders))
                    );
                    break;
                }
                case LENGTHWISE: {
                    result.add(new XYChartBean(
                            LENGTHWISE_CHART_NAME,
                            100.0f,
                            graphLength.floatValue() * 1.05f,
                            true,
                            true,
                            new JRBeanCollectionDataSource(signalAndBorders))
                    );
                    break;
                }
                case TRANSVERSAL: {
                    result.add(new XYChartBean(
                            CONTROVERSAL_CHART_NAME,
                            100.0f,
                            graphLength.floatValue() * 1.05f,
                            true,
                            true,
                            new JRBeanCollectionDataSource(signalAndBorders))
                    );
                    break;
                }
                case THICKNESS: {
                    result.add(new XYChartBean(
                            THICKNESS_CHART_NAME,
                            null,
                            graphLength.floatValue() * 1.05f,
                            true,
                            true,
                            new JRBeanCollectionDataSource(signalAndBorders))
                    );
                    break;
                }
            }
        }
        return new JRBeanCollectionDataSource(result);
    }

    public Map<String, Object> generate() {
        //Обновляем длину границ по x
        updateBordersLength();
        Map<String, Object> result = new HashMap<>();

        for (SENSOR_TYPES sensorType : SENSOR_TYPES.values()) {
            //Обоъединяем пороги и сигналы
            List<XYChartPointBean> signalAndBorders = new ArrayList<>();
            signalAndBorders.addAll(sensorsSignals.get(sensorType));
            //Добавляем ограничения сигналов
            borders.get(sensorType).keySet().forEach((String chanelName) -> {
                signalAndBorders.addAll(borders.get(sensorType).get(chanelName));
            });
            //Добавляем график сигналов.
            result.put(
                    sensorType.toString(),
                    new JRBeanCollectionDataSource(signalAndBorders)
            );
        }
        //Добавляем дефекты
        result.put(
                "DEFECTS",
                new JRBeanCollectionDataSource(defects)
        );

        //Добавляем дату проведения контроля
        StringBuilder dateAsString = new StringBuilder();
        dateAsString.append(getDefectDetectionTime().getDayOfMonth());
        dateAsString.append(".");
        dateAsString.append(getDefectDetectionTime().getMonthValue());
        dateAsString.append(".");
        dateAsString.append(getDefectDetectionTime().getYear());
        dateAsString.append(" ");
        dateAsString.append(getDefectDetectionTime().getHour());
        dateAsString.append(":");
        dateAsString.append(getDefectDetectionTime().getMinute());
        dateAsString.append(":");
        dateAsString.append(getDefectDetectionTime().getSecond());
        result.put("DEFECT_DETECTION_DATE", dateAsString.toString());

        //Добавляем номер трубы
        result.put("TUBE_NUMBER", getTubeNumber().toString());

        //Устанавливаем длину графика с запасом округляя им прибавляя 25 см.
        result.put("GRAPH_LENGTH", (Math.round(getGraphLength() / 100) + 2.5) * 100.0);

        //Обновляем длину границ по x
        updateBordersLength();
        result.put("DEFECTS", new XYChartBean(
                DEFECT_CHART_NAME,
                null,
                graphLength.floatValue() * 1.05f,
                true,
                true,
                new JRBeanCollectionDataSource(defects))
        );

        for (SENSOR_TYPES sensorType : SENSOR_TYPES.values()) {
            //Обоъединяем пороги и сигналы
            List<XYChartPointBean> signalAndBorders = new ArrayList<>();
            //Добавляем сигналы
            signalAndBorders.addAll(sensorsSignals.get(sensorType));
            //Добавляем ограничения сигналов
            borders.get(sensorType).keySet().forEach((String chanelName) -> {
                signalAndBorders.addAll(borders.get(sensorType).get(chanelName));
            });
            result.put(sensorType.toString(),
                    new JRBeanCollectionDataSource(signalAndBorders));
        }
        result.put("DEFECTS",
                new JRBeanCollectionDataSource(defects));
        return result;
    }

    /**
     * @param args the command line arguments
     *
     */
    public static void main(String[] args) {
        int MAX_X = 50;
//Создаем данные для графика
        UNTK_500DataSourceGenerator pmg = new UNTK_500DataSourceGenerator();

        //Добавляем сигнал от продольного дефекта
        for (SENSOR_TYPES type : SENSOR_TYPES.values()) {
            pmg.setSignalBorder("Канал 1", type, 60.0);
            pmg.setSignalBorder("Канал 2", type, 70.0);
            pmg.setSignalBorder("Канал 3", type, 65.0);
            pmg.setSignalBorder("Канал 4", type, 80.0);

            for (double i = 0; i <= MAX_X; i++) {
                pmg.addSignalPoint("Канал 1", type, Math.random() * 100, i);
                pmg.addSignalPoint("Канал 2", type, Math.random() * 95, i);
                pmg.addSignalPoint("Канал 3", type, Math.random() * 80, i);
                pmg.addSignalPoint("Канал 4", type, Math.random() * 85, i);
            }
        }

        for (double i = 0; i <= MAX_X; i++) {

            //Добавляем дефекты
            pmg.addDefectPoint(SENSOR_TYPES.LENGTHWISE.toString(), 1, Math.random() * 10);
            pmg.addDefectPoint(SENSOR_TYPES.TRANSVERSAL.toString(), 2, Math.random() * 10);
            pmg.addDefectPoint(SENSOR_TYPES.MAGNETIC.toString(), 3, Math.random() * 10);
            pmg.addDefectPoint(SENSOR_TYPES.THICKNESS.toString(), 4, Math.random() * 10);
        }

        //Имя файла скомпилированного отчёта, готового для заполнения.
        URL compiledReport = UNTK_500DataSourceGenerator.class.getResource("../../Resource/AllGraphsInOnOnePage.jasper");

        if (compiledReport == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Шаблон отчета не найден. \nНеправильно откомпилировали программу.",
                    "Ошибка",
                    ERROR_MESSAGE);
            return;
        }

        try (InputStream is = compiledReport.openStream();) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(is, pmg.generate());
            JasperViewer.viewReport(jasperPrint, false, Locale.getDefault());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка доступа к отчету: " + ex.getLocalizedMessage());
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка заполнения отчета: " + ex.getLocalizedMessage());
        }
    }

}
