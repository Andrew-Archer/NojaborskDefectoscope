/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.awt.Color;
import java.io.Serializable;
import javax.swing.JOptionPane;
import ru.npptmk.devices.USKUdp.DeviceUSKUdp;
import ru.npptmk.devices.md8Udp.DeviceMD8Udp;
import ru.npptmk.guiObjects.GraphicsPnlDf;
import ru.npptmk.guiObjects.GraphicsPnlDfParams;
import ru.npptmk.guiObjects.GraphicsPnlTls;
import ru.npptmk.guiObjects.GraphicsPnlTlsParams;
import ru.npptmk.guiObjects.IDrvsDataReader;
import ru.npptmk.guiObjects.IPanelFactory;
import ru.npptmk.guiObjects.PanelForGraphics;
import ru.npptmk.guiObjects.SamopPnlDf;
import ru.npptmk.guiObjects.SamopPnlDfParams;
import ru.npptmk.guiObjects.SamopPnlTLS;
import ru.npptmk.guiObjects.SamopPnlTLSParams;
import ru.npptmk.guiObjects.TubeGrphParams;
import ru.npptmk.guiObjects.TubePnl;

/**
 * Построитель панелек
 *
 * @author MalginAS
 */
public class BazaPanelFactory implements IPanelFactory {

    private final DeviceMD8Udp drvMD;
    private final DeviceUSKUdp drvUSK1;
    private final DeviceUSKUdp drvUSK2;

    /**
     * Конструктор фабрики компонент для отображения данных.
     *
     * @param drvMD драайвер устройства магнитной дефектоскопии.
     * @param drvUSK1 драйвер первого блока УЗК.
     * @param drvUSK2 драйвер второго блока УЗК.
     */
    public BazaPanelFactory(DeviceMD8Udp drvMD, DeviceUSKUdp drvUSK1, DeviceUSKUdp drvUSK2) {
        this.drvMD = drvMD;
        this.drvUSK1 = drvUSK1;
        this.drvUSK2 = drvUSK2;
    }

    @Override
    public IDrvsDataReader getPanel(PanelForGraphics pnl, int i) {
        Serializable pr = pnl.getParamCollection().get(i);
        if (pr instanceof SamopPnlTLSParams) {
            return new SamopPnlTLS(pnl, i);
        }
        if (pr instanceof SamopPnlDfParams) {
            return new SamopPnlDf(pnl, i);
        }
        if (pr instanceof TubeGrphParams) {
            return new TubePnl(pnl, i);
        }
        if (pr instanceof GraphicsPnlDfParams) {
            return new GraphicsPnlDf(pnl, i);
        }
        if (pr instanceof GraphicsPnlTlsParams) {
            return new GraphicsPnlTls(pnl, i);
        }
        return null;
    }

    @Override
    public Serializable getPanelParams(Serializable pr) {
        if (pr == null) {
            pr = selectComponent();
        }
        if (pr == null) {
            return pr;
        }
        // Вызов редакторов параметров компонент.
        if (pr instanceof SamopPnlTLSParams) {
            TlsPanelParamDialog pd = new TlsPanelParamDialog(drvUSK1, drvUSK2, (SamopPnlTLSParams) pr);
            pd.setVisible(true);
            if (!pd.okButton) {
                return null;
            }
            return pr;
        }
        if (pr instanceof SamopPnlDfParams) {
            DfPanelParamDialog pd = new DfPanelParamDialog(drvMD, drvUSK1, drvUSK2, (SamopPnlDfParams) pr);
            pd.setVisible(true);
            if (!pd.okButton) {
                return null;
            }
            return pr;
        }
        if (pr instanceof TubeGrphParams) {
            TubePnlParamDialog pd = new TubePnlParamDialog(drvMD, drvUSK1, drvUSK2, (TubeGrphParams) pr);
            pd.setVisible(true);
            if (!pd.okButton) {
                return null;
            }
            return pr;
        }
        if (pr instanceof GraphicsPnlDfParams) {
            GraphicsPnlDfParamDialog pd = new GraphicsPnlDfParamDialog((GraphicsPnlDfParams) pr, drvUSK1, drvUSK2);
            pd.setVisible(true);
            if (!pd.okButton) {
                return null;
            }
            return pr;
        }
        if (pr instanceof GraphicsPnlTlsParams) {
            GraphicsPnlTlsParamDialog pd = new GraphicsPnlTlsParamDialog((GraphicsPnlTlsParams) pr, drvUSK1, drvUSK2);
            pd.setVisible(true);
            if (!pd.okButton) {
                return null;
            }
            return pr;
        }
        return null;
    }

    /**
     * Диалоговый выбор компонента для отображения данных.
     *
     * @return
     */
    private Serializable selectComponent() {
        // Выясняем какой вид графика нужен:
        // варивнты:
        // 1 - самописец дефектоскопа,
        // 2 - самописец толщиномера
        // 3 - графики дефектоскопа
        // 4 - графики толщиномера
        // 5 - Изображение трубы с дефектами.
        String[] selectionValues = {"Самописец дефектоскопа", "Самописец толщиномера",
            "Графики дефектоскопа", "Графики толщиномера", "Схема дефектов"
        };
        String type = (String) JOptionPane.showInputDialog(null, "Какой тип графика нужен?", "Тип графика", JOptionPane.QUESTION_MESSAGE,
                null, selectionValues, selectionValues[0]);
        if (type != null) {
            switch (type) {
                case "Самописец дефектоскопа":
                    return new SamopPnlDfParams("Дефектоскоп Блок МД Канал 1", Devicess.ID_MD, 0);
                case "Самописец толщиномера":
                    return new SamopPnlTLSParams("Толщиномер Блок УЗК1 Канал 1", Devicess.ID_USK1, 0);
                case "Графики дефектоскопа":
                    return new GraphicsPnlDfParams(new long[0],
                            new int[0],
                            new Color[0],
                            new String[0], "Дефектоскоп", 12000, 6, 12);
                case "Графики толщиномера":
                    Color[] redGreen = {Color.GREEN, Color.MAGENTA, Color.BLUE};
                    return new GraphicsPnlTlsParams(new long[0], new int[0], new Color[0], new String[0],
                            redGreen, "Толщиномер", 15, 12000, 15, 12);
                case "Схема дефектов":
                    Color[] threeCol = {Color.GREEN, Color.RED, Color.BLUE};
                    String[] names = {"Магнитный", "УЗК", "Толщина"};
                    long[] dev = {Devicess.ID_R4, Devicess.ID_R4, Devicess.ID_R4};
                    int[] chans = {0, 1, 2};
                    return new TubeGrphParams(threeCol, names, dev, chans);
            }
        }
        return null;
    }
}
