/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.Util.jasper_report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import ru.npptmk.bazaTest.defect.BazaUSDResult;
import ru.npptmk.bazaTest.defect.TubeType;

/**
 *
 * @author RazumnovAA
 */
public class JRDataSourceUSKThickResults implements JRDataSource {

    private final float[][] y;
    private final float[][] x;
    private final TubeType tubeType;
    private int index;
    private final int graphLength;

    /**
     * Создаем обретку для BazaMDResult, чтобы передать в JasperReport как
     * JRDataSource
     *
     * @param uSKResults
     * @param tubeType тип трубы для получения порогов толщин стенки
     */
    public JRDataSourceUSKThickResults(
            BazaUSDResult uSKResults, TubeType tubeType) {

        y = new float[4][];
        x = new float[4][];
        this.tubeType = tubeType;
        graphLength = uSKResults.getGraficLength(1);
        index = - 1;
        for (int i = 0; i < 4; i++) {
            y[i] = new float[uSKResults.getThickGrafLength(i)];
            x[i] = new float[uSKResults.getThickGrafLength(i)];
            uSKResults.getThickGrafic(i + 4, x[i], y[i]);
        }
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {

        if (jrf != null) {
            switch (jrf.getName()) {
                case "ThickChanel1":
                    return y[0][index];
                case "ThickChanel2":
                    return y[1][index];
                case "ThickChanel3":
                    return y[2][index];
                case "ThickChanel4":
                    return y[3][index];

                case "ThickxCoordinate1":
                    return x[0][index];
                case "ThickxCoordinate2":
                    return x[1][index];
                case "ThickxCoordinate3":
                    return x[2][index];
                case "ThickxCoordinate4":
                    return x[3][index];
                case "borderEdge1":
                    return tubeType.getThickClassBorderValue(TubeType.ThickClasses.CLASS_1);
                case "borderEdge2":
                    return tubeType.getThickClassBorderValue(TubeType.ThickClasses.CLASS_2);
                case "borderEdge3":
                    return tubeType.getThickClassBorderValue(TubeType.ThickClasses.CLASS_3);
                default:
                    return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean next() throws JRException {
        //Передвигаем указатель на следующую строку.
        index++;
        //Проверяем, что строка на которую ссылается указатель
        //не вышла за пределы списка.
        return index < graphLength;
    }
}
