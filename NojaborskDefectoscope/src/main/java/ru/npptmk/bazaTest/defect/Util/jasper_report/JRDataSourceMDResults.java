/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.Util.jasper_report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import ru.npptmk.bazaTest.defect.BazaMDResult;

/**
 *
 * @author RazumnovAA
 */
public class JRDataSourceMDResults implements JRDataSource {

    private final float[][] y;
    private final float[][] x;
    private final float borderAge;
    private int index;

    /**
     * Создаем обретку для BazaMDResult, чтобы передать в JasperReport как
     * JRDataSource
     *
     * @param mdResult
     */
    public JRDataSourceMDResults(
            BazaMDResult mdResult, float borderAge) {

        y = new float[8][];
        x = new float[8][];
        this.borderAge = borderAge;
        index = - 1;
        for (int i = 0; i < 8; i++) {
            y[i] = new float[mdResult.getGraficLength(i)];
            x[i] = new float[mdResult.getGraficLength(i)];
            mdResult.getGrafic(i, x[i], y[i]);
        }
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {

        if (jrf != null) {
            switch (jrf.getName()) {
                case "MDChanel1":
                    return y[0][index];
                case "MDChanel2":
                    return y[1][index];
                case "MDChanel3":
                    return y[2][index];
                case "MDChanel4":
                    return y[3][index];
                case "MDChanel5":
                    return y[4][index];
                case "MDChanel6":
                    return y[5][index];
                case "MDChanel7":
                    return y[6][index];
                case "MDChanel8":
                    return y[7][index];
                case "MDxCoordinate1":
                    return x[0][index];
                case "MDxCoordinate2":
                    return x[1][index];
                case "MDxCoordinate3":
                    return x[2][index];
                case "MDxCoordinate4":
                    return x[3][index];
                case "MDxCoordinate5":
                    return x[4][index];
                case "MDxCoordinate6":
                    return x[5][index];
                case "MDxCoordinate7":
                    return x[6][index];
                case "MDxCoordinate8":
                    return x[7][index];
                case "MDBorderAge":
                    return borderAge;
                case "MDBorderAgeX":
                    return x[7][index];
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
        return index < x[0].length;
    }

}
