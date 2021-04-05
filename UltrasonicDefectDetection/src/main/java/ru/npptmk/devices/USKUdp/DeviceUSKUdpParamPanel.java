/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.devices.USKUdp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.npptmk.commonObjects.GeneralUDPDevice;
import ru.npptmk.guiObjects.AbstractVKEditor;
import ru.npptmk.guiObjects.Grafic;
import ru.npptmk.guiObjects.GraficsIcon;
import ru.npptmk.guiObjects.VirtualKeyboard;

/**
 * Панель настройки параметров УЗК.<br>
 * Обеспечивает выполнение настройки всех параметров каналов УЗК. Может работать
 * с несколькими блоками дефектоскопии одновременно. Для этого используется
 * отдельный массив описателей каналов. В нем для каждого настраиваемого канала
 * задается драйвер и индекс канала в этом драйвере. Таким образом можно одной
 * панелью настраивать каналы в различных блоках УЗК.<br>
 * Панель имеет один публичный метод - {@code updateAScan(..)}. Его должна
 * вызывать внешняя программа по мере готовности данных А - скана для
 * отображения.
 *
 * @author MalginAS
 */
public class DeviceUSKUdpParamPanel extends javax.swing.JPanel implements ListSelectionListener {
    private static final Logger log = LoggerFactory.getLogger(DeviceUSKUdpParamPanel.class);

    private final VirtualKeyboard vk = new VirtualKeyboard(null);
    public DeviceUSKUdp drv;                    // Драйвер текущего канала.
    private DeviceUSKUdpParam activChanParam;   // Параметры текущего канала.
    private int actChan;                        // Текущий канал для настроек
    private boolean isUpdatingParams = false;   // Признак обновления параметров
    private int curVRCPoint = -1;               // Текущая точка ВРЧ
    // используется для блокировки реакции на события в чекбоксах и комбобоксах
    // во время установки их значений из программы а не пользователем.
    private final DefaultTableModel tm;         // Данные таблицы точек ВРЧ
    private final GraficsIcon grfAScan;         // Иконка с графиком А-скана и зонами.
    // График А - зоны.
    private final float[] xAzone = new float[4];
    private final float[] yAzone = new float[4];
    private static final int ID_AZONE = 0;      // Идентификатор графика
    // График В - зоны.
    private final float[] xBzone = new float[4];
    private final float[] yBzone = new float[4];
    private static final int ID_BZONE = 1;      // Идентификатор графика
    // График I - зоны
    private final float[] xIzone = new float[4];
    private final float[] yIzone = new float[4];
    private static final int ID_IZONE = 2;      // Идентификатор графика
    // График ВРЧ
    private float[] xVRCH;
    private float[] yVRCH;
    private static final int ID_VRCH = 3;      // Идентификатор графика
    // График отсечки
    private float[] ySUP = new float[2];
    private float[] xSUP = new float[2];
    private static final int ID_SUPRESS = 4;   // Идентификатор графика отсечки
    private DeviceUSKUdpParam clipbPrm = null; //Параметры для копирования.
    private float curFail = 0;//Ослабление усиления активного канала

    private Boolean clickRunning = false;
    private final Object waitPress = new Object();
    private String clipbName;
    public float deltaGainThick;
    public float deltaGainAxial;
    public float deltaGainDir;
    public boolean changePrm1;
    public boolean changePrm2;
    private long idChangeDrv;
    private Timer pressRepTimer;
    private int prevSelChanInd;
    private boolean prgSelCange;
    private DeltaGainPanel dgPnl;

    /**
     * Создает панельку для настройки УЗК.
     *
     * @param chanDefs Описатели каналов УЗК.
     */
    public DeviceUSKUdpParamPanel(DeviceUSKUdpChanDef[] chanDefs) {
        isUpdatingParams = true;
        initComponents();
        setVKEditors();
        // Подготовка иконки
        grfAScan = new GraficsIcon(100, 100, 10, 10);
        grfAScan.fonStr = new BasicStroke();
        lbAScan.setIcon(grfAScan);
        tbVRC.getSelectionModel().addListSelectionListener(this);
        tm = (DefaultTableModel) tbVRC.getModel();
        //Инициализация усиления для работы и для настройки, если не соотв.
        for (DeviceUSKUdpChanDef chanDef : chanDefs) {
            cbChanList.addItem(chanDef);
        }
        isUpdatingParams = false;
        jPanel6.setVisible(false);
        setActiveChan();
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла:");
        UIManager.put("FileChooser.lookInLabelText", "Папка:");
        UIManager.put("FileChooser.saveInLabelText", "Папка:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файла:");
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.cancelButtonText", "Отменить");
        UIManager.put("FileChooser.openButtonText", "Открыть");
    }

    /**
     * Привязываем виртуальные клавиатуры к полям с числовыми данными.
     */
    private void setVKEditors() {
        new AbstractVKEditor(edGain, vk) {
            @Override
            public void newValue(String newVal) {
                float val = Float.parseFloat(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                
                activChanParam.setGain(val);
                changeParams();
                refreshActParams();
            }

        };
        new AbstractVKEditor(edRange, vk) {
            @Override
            public void newValue(String newVal) {
                float val = Float.parseFloat(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setRange(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edDelay, vk) {
            @Override
            public void newValue(String newVal) {
                activChanParam.setSignal_delay(Float.parseFloat(newVal));
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edAStart, vk) {
            @Override
            public void newValue(String newVal) {
                activChanParam.setA_start(Float.parseFloat(newVal));
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edAWidth, vk) {
            @Override
            public void newValue(String newVal) {
                float val = Float.parseFloat(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setA_width(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edAThresh, vk) {
            @Override
            public void newValue(String newVal) {
                short val = Short.parseShort(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setA_thresh(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edBStart, vk) {
            @Override
            public void newValue(String newVal) {
                activChanParam.setB_start(Float.parseFloat(newVal));
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edBWidth, vk) {
            @Override
            public void newValue(String newVal) {
                float val = Float.parseFloat(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setB_width(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edBThresh, vk) {
            @Override
            public void newValue(String newVal) {
                short val = Short.parseShort(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setB_thresh(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edIStart, vk) {
            @Override
            public void newValue(String newVal) {
                float val = Float.parseFloat(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setI_start(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edIThresh, vk) {
            @Override
            public void newValue(String newVal) {
                short val = Short.parseShort(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setI_thresh(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edVRCTime, vk) {
            @Override
            public void newValue(String newVal) {
                if (curVRCPoint != -1) {
                    activChanParam.setVrch_times(Float.parseFloat(newVal), curVRCPoint);
                    changeParams();
                    refreshActParams();
                }
            }
        };
        new AbstractVKEditor(edVRCGain, vk) {
            @Override
            public void newValue(String newVal) {
                if (curVRCPoint != -1) {
                    activChanParam.setVrch_gains(Float.parseFloat(newVal), curVRCPoint);
                    changeParams();
                    refreshActParams();
                }
            }
        };
        new AbstractVKEditor(edGziWidth, vk) {
            @Override
            public void newValue(String newVal) {
                float val = Float.parseFloat(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setGzi_width(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edGziCount, vk) {
            @Override
            public void newValue(String newVal) {
                short val = Short.parseShort(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setGzi_count(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edPeakMode, vk) {
            @Override
            public void newValue(String newVal) {
                short val = Short.parseShort(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setPeak_mode(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edASDFiltr, vk) {
            @Override
            public void newValue(String newVal) {
                short val = Short.parseShort(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setAsd_filter(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edAdcOffs, vk) {
            @Override
            public void newValue(String newVal) {
                activChanParam.setAds_offset(Short.parseShort(newVal));
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edSyncDelay, vk) {
            @Override
            public void newValue(String newVal) {
                short val = Short.parseShort(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setSync_delay(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edKalibr1, vk) {
            @Override
            public void newValue(String newVal) {
                float val = Float.parseFloat(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setKalibr1Obrazec(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edKalibr2, vk) {
            @Override
            public void newValue(String newVal) {
                float val = Float.parseFloat(newVal);
                if(val < 0) {
                    refreshActParams();
                    return;
                }
                activChanParam.setKalibr2Obrazec(val);
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(edMechPos, vk) {
            @Override
            public void newValue(String newVal) {
                activChanParam.setMech_pos(Integer.parseInt(newVal));
                changeParams();
                refreshActParams();
            }
        };
        new AbstractVKEditor(supress, vk) {
            @Override
            public void newValue(String newVal) {
                activChanParam.setSupression(Short.parseShort(newVal));
                changeParams();
                refreshActParams();
            }
        };
    }

    public final void setActiveChan() {
        if(prgSelCange) return;
        DeviceUSKUdpChanDef def = (DeviceUSKUdpChanDef) cbChanList.getSelectedItem();
        if (def != null) {
            drv = def.driver;
            actChan = def.chanIndex;
            short ret = drv.setActiveChan(actChan);
            if(ret != GeneralUDPDevice.CS_OK){
//                JOptionPane.showMessageDialog(null, "Канал не переключился");
//                prgSelCange = true;
////                cbChanList.setSelectedIndex(prevSelChanInd);
//                prgSelCange = false;
            } else {
                prevSelChanInd = cbChanList.getSelectedIndex();
            }
//для отладки работы, когда подключения к блокам нет
//    if(drv.setActiveChan(actChan) == GeneralUDPDevice.CS_OK)
//        activChanParam = drv.getActiveChanParam();
//    else if(drv.setActiveChan(actChan) == GeneralUDPDevice.CS_NORESPONCE)
            activChanParam = drv.getParams().prms[actChan];
            refreshActParams();
        }
        curVRCPoint = 0;
    }

    /**
     * Обновление данных А - скана на панельке.<br>
     * Функция проверяет, является ли переданный драйвер тем драйвером, с
     * которым ведется работа, и если это так, то его данные по А - скану
     * используются для рисования. Метод вызвать в потоке работы с компонентами,
     * использовать {@code  EventQueue.invokeLater}.
     *
     * @param drvAsc драйвер с новым А - сканом.
     */
    public void updateAScan(DeviceUSKUdp drvAsc) {
        if (drv == drvAsc) {;
            grfAScan.setAScan(drv.getAScanMax(), drv.getAScanMin());
            factBasFrq.setText(String.format("%d", drv.getTimer_base_freq()));
            lbAScan.repaint();
        }
    }

    public DeltaGainPanel getDeltaGainPanel(){
        if(dgPnl == null)
            dgPnl = new DeltaGainPanel(this);
        dgPnl.refresh();
        return dgPnl;
    }
     /**
     * Обновление значений органов редатирования текущей строки ВРЧ при
     * изменении номера текущей точки ВРЧ
     */
    private void updateVrchPos() {
        if(curVRCPoint >= tm.getRowCount() || curVRCPoint < 0) return;
        edVRCTime.setText((String) tm.getValueAt(curVRCPoint, 0));
        edVRCGain.setText((String) tm.getValueAt(curVRCPoint, 1));
    }
    /**Обработка увеличения усиления продольных датиков*/
    public void upDeltaGainAx(){
        //продольные увеличение
        deltaGainAxial += 0.5f;
        refreshFailAx();
    }
    /**Обработка уменшения усиления продольных датиков*/
    public void dwnDeltaGainAx() {
        //продольные уменьшение
        deltaGainAxial -= 0.5f;
        if(deltaGainAxial < 0)
            deltaGainAxial = 0;
        refreshFailAx();
    }
    /**Обработка увеличения усиления поперечных датиков*/
    public void upDeltaGainDir() {
        //поперечные увеличение
        deltaGainDir += 0.5f;
        refreshFailDir();
    }
    /**Обработка уменшения усиления поперечных датиков*/
    public void dwnDeltaGainDir() {
        //поперечные уменьшение
        deltaGainDir -= 0.5f;
        if(deltaGainDir < 0)
            deltaGainDir = 0;
        refreshFailDir();
    }
    /**Обработка увеличения усиления датчиков толщины*/
    public void upDeltaGainThick() {
        //толщина увеличение
        deltaGainThick += 0.5f;
        refreshFailTolsh();
    }
    /**Обработка уменшения усиления датчиков толщины*/
    public void dwnDeltaGainThick() {
        //толщина уменьшение
        deltaGainThick -= 0.5f;
        if(deltaGainThick < 0)
            deltaGainThick = 0;
        refreshFailTolsh();
    }
    /**
     * Метод показывает диалог изменения усиления для каналов: 
     * поперечных, продольных и тольщиномера. 
     * Метод вызывается при вызове контекстного меню на соотв строке.
     * 
     * @param e событие мышки.
     */
    
    private void showPopupPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            String idMn = ((JComponent)e.getSource()).getName(),
                    mess;
        }
    }

    synchronized private void refreshActParams() {
        short tsh;
        isUpdatingParams = true;
        // Общая часть панели
        edGain.setText(String.format("%4.1f", activChanParam.getGain()));
        edRange.setText(String.format("%4.1f", activChanParam.getRange()));
        edDelay.setText(String.format("%4.1f", activChanParam.getSignal_delay()));
        cbDetect.setSelectedIndex(activChanParam.getDetector());
        cbEnabled.setSelected(activChanParam.getEnabled());
        tsh = activChanParam.getHardware_type();
        if (tsh == DeviceUSKUdpParam.TIME_CONTROL) {
            cbHardwareType.setSelectedIndex(0);
        } else {
            cbHardwareType.setSelectedIndex(1);
        }
        // Вкладка "Зоны"
        edAStart.setText(String.format("%4.1f", activChanParam.getA_start()));
        edAWidth.setText(String.format("%4.1f", activChanParam.getA_width()));
        edAThresh.setText(String.format("%d", activChanParam.getA_thresh()));
        edBStart.setText(String.format("%4.1f", activChanParam.getB_start()));
        edBWidth.setText(String.format("%4.1f", activChanParam.getB_width()));
        edBThresh.setText(String.format("%d", activChanParam.getB_thresh()));
        edIStart.setText(String.format("%4.1f", activChanParam.getI_start()));
        edIThresh.setText(String.format("%d", activChanParam.getI_thresh()));
        cbIActive.setSelected(activChanParam.getI_active());
        cbIVisible.setSelected(activChanParam.getI_visible());
        grfAScan.removeGrafics();
        supress.setText(String.format("%d", activChanParam.getSupression()));

        // Графики зон.
        float koeff = 100.0f / activChanParam.getRange();
        xAzone[0] = activChanParam.getA_start() * koeff;
        xAzone[1] = xAzone[0];
        xAzone[2] = xAzone[0] + activChanParam.getA_width() * koeff;
        xAzone[3] = xAzone[2];
        yAzone[1] = activChanParam.getA_thresh();
        yAzone[0] = yAzone[1] + 2;
        yAzone[2] = yAzone[1];
        yAzone[3] = yAzone[0];
        grfAScan.addGrafic(ID_AZONE, new Grafic(Color.red, xAzone, yAzone));
        if (activChanParam.getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
            xBzone[0] = activChanParam.getB_start() * koeff;
            xBzone[1] = xBzone[0];
            xBzone[2] = xBzone[0] + activChanParam.getB_width() * koeff;
            xBzone[3] = xBzone[2];
            yBzone[1] = activChanParam.getB_thresh();
            yBzone[2] = yBzone[1];
            yBzone[0] = yBzone[1] + 2;
            yBzone[3] = yBzone[0];
            grfAScan.addGrafic(ID_BZONE, new Grafic(new Color(255, 0, 255), xBzone, yBzone));
            jPanel3.setVisible(true);
        } else {
            jPanel3.setVisible(false);
        }
        if (activChanParam.getI_visible()) {
            xIzone[0] = activChanParam.getI_start() * koeff;
            xIzone[1] = xIzone[0];
            xIzone[2] = xIzone[0] + 15 * koeff;
            xIzone[3] = xIzone[2];
            yIzone[1] = activChanParam.getI_thresh();
            yIzone[2] = yIzone[1];
            yIzone[0] = yIzone[1] + 2;
            yIzone[3] = yIzone[0];
            grfAScan.addGrafic(ID_IZONE, new Grafic(new Color(0, 255, 255), xIzone, yIzone));
        }
        if (activChanParam.getDetector() != 0) {
            xSUP[0]  = 0;
            xSUP[1] = lbAScan.getWidth();
            ySUP[0] = activChanParam.getSupression();
            ySUP[1] = ySUP[0];
            grfAScan.addGrafic(ID_SUPRESS, new Grafic(Color.GRAY, xSUP, ySUP));
        }
        // Вкладка "ВРЧ"
        while (tm.getRowCount() > 0) {
            tm.removeRow(0);
        }
        int points = activChanParam.getVrch_points();
        xVRCH = new float[points];
        yVRCH = new float[points];
        Object[] data = new Object[2];
        for (int i = 0; i < points; i++) {
            xVRCH[i] = activChanParam.getVrch_times(i);
            yVRCH[i] = activChanParam.getVrch_gains(i);
            data[0] = String.format("%4.1f", xVRCH[i]);
            data[1] = String.format("%4.1f", yVRCH[i]);
            xVRCH[i] *= koeff;
            tm.addRow(data);
        }
        if (activChanParam.getVrch_visible()) {
            grfAScan.addGrafic(ID_VRCH, new Grafic(Color.BLUE, xVRCH, yVRCH));
        }
        if (curVRCPoint != -1) {
            if (curVRCPoint >= tm.getRowCount()) {
                curVRCPoint = tm.getRowCount();
            }
            if(curVRCPoint < tbVRC.getRowCount() && curVRCPoint >= 0){
                tbVRC.setRowSelectionInterval(curVRCPoint, curVRCPoint);
            }
            updateVrchPos();
        }
        cbVrchGo.setSelected(activChanParam.getVrch_go());
        cbVRCVis.setSelected(activChanParam.getVrch_visible());
        lbAScan.invalidate();
        // Вкладка "Генератор"
        edGziWidth.setText(String.format("%4.1f", activChanParam.getGzi_width()));
        edGziCount.setText(String.format("%d", activChanParam.getGzi_count()));
        cbSoglas.setSelectedIndex(activChanParam.getSoglas());
        cbDempfer.setSelected(activChanParam.getDempfer());
        // Вкладка "Приемник"
        cbRVhoda.setSelected(activChanParam.getRvhoda());
        cbAnifiltr.setSelectedIndex(activChanParam.getAnfilter());
        edPeakMode.setText(String.format("%d", activChanParam.getPeak_mode()));
        edASDFiltr.setText(String.format("%d", activChanParam.getAsd_filter()));
        cbDefectType.setSelectedIndex(activChanParam.getDefect_type()<2?0:1);
//        cbType.setSelectedIndex(activChanParam.getTime_mode());
        // Вкладка "Прочее"
        cbComp.setSelected(activChanParam.getProbe_mode());
        edAdcOffs.setText(String.format("%d", activChanParam.getAds_offset()));
        cbProbe.setSelectedIndex(activChanParam.getPulser_takt());
        edSyncDelay.setText(String.format("%d", activChanParam.getSync_delay()));
        edMechPos.setText(String.format("%d", activChanParam.getMech_pos()));
        //режим "работа/настройка"
        DeviceUSKUdpChanDef ch;
        DeviceUSKUdpParams prs;
        boolean dr = false, ax = false, th = false;
        ch = (DeviceUSKUdpChanDef) cbChanList.getSelectedItem();
        prs = ch.driver.getParams();
        deltaGainAxial = prs.deltaGainAx;
        deltaGainDir = prs.deltaGainDir;
        deltaGainThick = prs.deltaGainThick;
        // Вкладка "Калибровка"
        if (activChanParam.getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
            jTabbedPane1.setEnabledAt(4, true);
            edKalibr1.setText(String.format("%4.1f", activChanParam.getKalibr1Obrazec()));
            edKalibr2.setText(String.format("%4.1f", activChanParam.getKalibr2Obrazec()));
            lbCbr1.setText(String.format("%d", activChanParam.getKalibr1Value()));
            lbCbr2.setText(String.format("%d", activChanParam.getKalibr2Value()));
            cbTimeMode.setSelectedIndex(activChanParam.getTime_mode());
            cbKalibrOn.setSelected(activChanParam.getKalibrEnable());
            if (activChanParam.getKalibr1Value() == 0 && activChanParam.getKalibr2Value() == 0) {
                cbKalibrOn.setEnabled(false);
            } else {
                cbKalibrOn.setEnabled(true);
            }
            cbDefectType.setVisible(false);
            jLabel34.setVisible(false);
        } else {
            jTabbedPane1.setEnabledAt(4, false);
            cbDefectType.setVisible(true);
            jLabel34.setVisible(true);
        }
        isUpdatingParams = false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbChanList = new javax.swing.JComboBox();
        cbHardwareType = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        cbEnabled = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        lbAScan = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        edGain = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        edRange = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        edDelay = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        cbDetect = new javax.swing.JComboBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnZone = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        edAStart = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        edAWidth = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        edAThresh = new javax.swing.JTextField();
        jButton13 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        edBStart = new javax.swing.JTextField();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        edBWidth = new javax.swing.JTextField();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        edBThresh = new javax.swing.JTextField();
        jButton19 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton20 = new javax.swing.JButton();
        edIStart = new javax.swing.JTextField();
        jButton21 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        edIThresh = new javax.swing.JTextField();
        jButton25 = new javax.swing.JButton();
        cbIActive = new javax.swing.JCheckBox();
        cbIVisible = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jButton56 = new javax.swing.JButton();
        supress = new javax.swing.JTextField();
        jButton57 = new javax.swing.JButton();
        pnMOT = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbVRC = new javax.swing.JTable();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jButton46 = new javax.swing.JButton();
        edVRCTime = new javax.swing.JTextField();
        jButton47 = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jButton48 = new javax.swing.JButton();
        edVRCGain = new javax.swing.JTextField();
        jButton49 = new javax.swing.JButton();
        cbVrchGo = new javax.swing.JCheckBox();
        cbVRCVis = new javax.swing.JCheckBox();
        jButton50 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        pnGener = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jButton22 = new javax.swing.JButton();
        edGziWidth = new javax.swing.JTextField();
        jButton23 = new javax.swing.JButton();
        cbDempfer = new javax.swing.JCheckBox();
        cbSoglas = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton26 = new javax.swing.JButton();
        edGziCount = new javax.swing.JTextField();
        jButton27 = new javax.swing.JButton();
        pnRec = new javax.swing.JPanel();
        cbRVhoda = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        cbAnifiltr = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jButton28 = new javax.swing.JButton();
        edPeakMode = new javax.swing.JTextField();
        jButton29 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jButton30 = new javax.swing.JButton();
        edASDFiltr = new javax.swing.JTextField();
        jButton31 = new javax.swing.JButton();
        cbDefectType = new javax.swing.JComboBox();
        jLabel34 = new javax.swing.JLabel();
        pnKalibr = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jButton36 = new javax.swing.JButton();
        edKalibr1 = new javax.swing.JTextField();
        jButton37 = new javax.swing.JButton();
        lbCbr1 = new javax.swing.JLabel();
        jButton38 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        jButton39 = new javax.swing.JButton();
        edKalibr2 = new javax.swing.JTextField();
        jButton40 = new javax.swing.JButton();
        lbCbr2 = new javax.swing.JLabel();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        cbKalibrOn = new javax.swing.JCheckBox();
        jLabel31 = new javax.swing.JLabel();
        cbTimeMode = new javax.swing.JComboBox();
        pnOther = new javax.swing.JPanel();
        cbComp = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        jButton32 = new javax.swing.JButton();
        edAdcOffs = new javax.swing.JTextField();
        jButton33 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        cbProbe = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        jButton34 = new javax.swing.JButton();
        edSyncDelay = new javax.swing.JTextField();
        jButton35 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        edMechPos = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        factBasFrq = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(100, 32));
        setMinimumSize(new java.awt.Dimension(100, 32));
        setPreferredSize(new java.awt.Dimension(100, 32));

        cbChanList.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbChanList.setMaximumSize(new java.awt.Dimension(100, 32));
        cbChanList.setMinimumSize(new java.awt.Dimension(100, 32));
        cbChanList.setPreferredSize(new java.awt.Dimension(100, 32));
        cbChanList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbChanListActionPerformed(evt);
            }
        });

        cbHardwareType.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbHardwareType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Толщиномер", "Дефектоскоп" }));
        cbHardwareType.setMaximumSize(new java.awt.Dimension(100, 32));
        cbHardwareType.setMinimumSize(new java.awt.Dimension(100, 32));
        cbHardwareType.setPreferredSize(new java.awt.Dimension(100, 32));
        cbHardwareType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbHardwareTypeActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton1.setText("Парамеры");
        jButton1.setToolTipText("Скопировать параметры у другого канала.");
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.setMaximumSize(new java.awt.Dimension(100, 32));
        jButton1.setMinimumSize(new java.awt.Dimension(100, 32));
        jButton1.setPreferredSize(new java.awt.Dimension(100, 32));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cbEnabled.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbEnabled.setText("Вкл.");
        cbEnabled.setMaximumSize(new java.awt.Dimension(100, 32));
        cbEnabled.setMinimumSize(new java.awt.Dimension(100, 32));
        cbEnabled.setPreferredSize(new java.awt.Dimension(100, 32));
        cbEnabled.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cbEnabledMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cbEnabledMouseClicked(evt);
            }
        });
        cbEnabled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEnabledActionPerformed(evt);
            }
        });

        lbAScan.setBackground(new java.awt.Color(0, 0, 0));
        lbAScan.setForeground(new java.awt.Color(255, 255, 255));
        lbAScan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbAScan.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setText("Усиление дБ.");
        jLabel1.setMaximumSize(new java.awt.Dimension(100, 32));
        jLabel1.setMinimumSize(new java.awt.Dimension(100, 32));
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 32));

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton4.setIconTextGap(0);
        jButton4.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton4.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton4.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton4.setName("010"); // NOI18N
        jButton4.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                prmMousReleas(evt);
            }
        });
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        edGain.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edGain.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        edGain.setText("99,5");
        edGain.setMaximumSize(new java.awt.Dimension(100, 32));
        edGain.setMinimumSize(new java.awt.Dimension(100, 32));
        edGain.setPreferredSize(new java.awt.Dimension(100, 32));
        edGain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edGainActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton3.setIconTextGap(0);
        jButton3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton3.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton3.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton3.setName("011"); // NOI18N
        jButton3.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                prmMousReleas(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel2.setText("Развертка мкс.");
        jLabel2.setMaximumSize(new java.awt.Dimension(100, 32));
        jLabel2.setMinimumSize(new java.awt.Dimension(100, 32));
        jLabel2.setPreferredSize(new java.awt.Dimension(100, 32));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton6.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton6.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton6.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton6.setName("020"); // NOI18N
        jButton6.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton6.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        edRange.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edRange.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        edRange.setText("100.3");
        edRange.setMaximumSize(new java.awt.Dimension(100, 32));
        edRange.setMinimumSize(new java.awt.Dimension(100, 32));
        edRange.setPreferredSize(new java.awt.Dimension(100, 32));
        edRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edRangeActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton5.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton5.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton5.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton5.setName("021"); // NOI18N
        jButton5.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton5.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel3.setText("Задержка мкс.");
        jLabel3.setMaximumSize(new java.awt.Dimension(100, 32));
        jLabel3.setMinimumSize(new java.awt.Dimension(100, 32));
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 32));

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton8.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton8.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton8.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton8.setName("030"); // NOI18N
        jButton8.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton8.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        edDelay.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edDelay.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        edDelay.setText("20000");
        edDelay.setMaximumSize(new java.awt.Dimension(100, 32));
        edDelay.setMinimumSize(new java.awt.Dimension(100, 32));
        edDelay.setPreferredSize(new java.awt.Dimension(100, 32));
        edDelay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edDelayActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton9.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton9.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton9.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton9.setName("031"); // NOI18N
        jButton9.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton9.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        cbDetect.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbDetect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Радио", "Положительный", "Отрицательны", "Полный" }));
        cbDetect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDetectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbDetect, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(edGain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(edDelay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edRange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edGain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cbDetect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbAScan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbAScan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jTabbedPane1.setMaximumSize(new java.awt.Dimension(32, 32));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(32, 32));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(32, 32));

        pnZone.setMaximumSize(new java.awt.Dimension(370, 32767));
        pnZone.setPreferredSize(new java.awt.Dimension(350, 406));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "А - зона", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 20))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel4.setText("Начало");
        jLabel4.setMaximumSize(new java.awt.Dimension(132, 32));
        jLabel4.setMinimumSize(new java.awt.Dimension(132, 32));
        jLabel4.setPreferredSize(new java.awt.Dimension(132, 32));

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel5.setText("Ширина");
        jLabel5.setMaximumSize(new java.awt.Dimension(132, 32));
        jLabel5.setMinimumSize(new java.awt.Dimension(132, 32));
        jLabel5.setPreferredSize(new java.awt.Dimension(132, 32));

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel6.setText("Порог");
        jLabel6.setMaximumSize(new java.awt.Dimension(132, 32));
        jLabel6.setMinimumSize(new java.awt.Dimension(132, 32));
        jLabel6.setPreferredSize(new java.awt.Dimension(132, 32));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton2.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton2.setName("040"); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        edAStart.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edAStart.setMaximumSize(new java.awt.Dimension(2147483647, 32));
        edAStart.setMinimumSize(new java.awt.Dimension(4, 32));
        edAStart.setPreferredSize(new java.awt.Dimension(80, 32));
        edAStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edAStartActionPerformed(evt);
            }
        });

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton7.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton7.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton7.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton7.setName("041"); // NOI18N
        jButton7.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton7.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton10.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton10.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton10.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton10.setName("050"); // NOI18N
        jButton10.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton10.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        edAWidth.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edAWidth.setMaximumSize(new java.awt.Dimension(2147483647, 32));
        edAWidth.setMinimumSize(new java.awt.Dimension(4, 32));
        edAWidth.setPreferredSize(new java.awt.Dimension(80, 32));
        edAWidth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edAWidthActionPerformed(evt);
            }
        });

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton11.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton11.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton11.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton11.setName("051"); // NOI18N
        jButton11.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton11.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton12.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton12.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton12.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton12.setName("060"); // NOI18N
        jButton12.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton12.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        edAThresh.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edAThresh.setMaximumSize(new java.awt.Dimension(2147483647, 32));
        edAThresh.setMinimumSize(new java.awt.Dimension(4, 32));
        edAThresh.setPreferredSize(new java.awt.Dimension(80, 32));
        edAThresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edAThreshActionPerformed(evt);
            }
        });

        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton13.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton13.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton13.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton13.setName("061"); // NOI18N
        jButton13.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton13.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(edAStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edAWidth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edAThresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edAStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edAThresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edAWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "B - зона", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 20))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel7.setText("Начало");
        jLabel7.setMaximumSize(new java.awt.Dimension(132, 32));
        jLabel7.setMinimumSize(new java.awt.Dimension(32, 32));
        jLabel7.setPreferredSize(new java.awt.Dimension(132, 32));

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel8.setText("Ширина");
        jLabel8.setMaximumSize(new java.awt.Dimension(132, 32));
        jLabel8.setMinimumSize(new java.awt.Dimension(32, 32));
        jLabel8.setPreferredSize(new java.awt.Dimension(132, 32));

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel9.setText("Порог");
        jLabel9.setMaximumSize(new java.awt.Dimension(132, 32));
        jLabel9.setMinimumSize(new java.awt.Dimension(32, 32));
        jLabel9.setPreferredSize(new java.awt.Dimension(132, 32));

        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton14.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton14.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton14.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton14.setName("070"); // NOI18N
        jButton14.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton14.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        edBStart.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edBStart.setMaximumSize(new java.awt.Dimension(80, 32));
        edBStart.setMinimumSize(new java.awt.Dimension(80, 32));
        edBStart.setPreferredSize(new java.awt.Dimension(80, 32));
        edBStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edBStartActionPerformed(evt);
            }
        });

        jButton15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton15.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton15.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton15.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton15.setName("071"); // NOI18N
        jButton15.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton15.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton16.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton16.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton16.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton16.setName("080"); // NOI18N
        jButton16.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton16.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        edBWidth.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edBWidth.setMaximumSize(new java.awt.Dimension(80, 32));
        edBWidth.setMinimumSize(new java.awt.Dimension(80, 32));
        edBWidth.setPreferredSize(new java.awt.Dimension(80, 32));
        edBWidth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edBWidthActionPerformed(evt);
            }
        });

        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton17.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton17.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton17.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton17.setName("081"); // NOI18N
        jButton17.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton17.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton18.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton18.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton18.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton18.setName("250"); // NOI18N
        jButton18.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton18.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        edBThresh.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edBThresh.setMaximumSize(new java.awt.Dimension(80, 32));
        edBThresh.setMinimumSize(new java.awt.Dimension(80, 32));
        edBThresh.setPreferredSize(new java.awt.Dimension(80, 32));
        edBThresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edBThreshActionPerformed(evt);
            }
        });

        jButton19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton19.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton19.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton19.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton19.setName("251"); // NOI18N
        jButton19.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton19.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(edBThresh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(edBStart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(edBWidth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edBStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edBThresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edBWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "I - зона", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 20))); // NOI18N

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel10.setText("Начало");
        jLabel10.setMaximumSize(new java.awt.Dimension(80, 32));
        jLabel10.setMinimumSize(new java.awt.Dimension(80, 32));
        jLabel10.setName(""); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(80, 32));

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel12.setText("Порог");
        jLabel12.setMaximumSize(new java.awt.Dimension(80, 32));
        jLabel12.setMinimumSize(new java.awt.Dimension(80, 32));
        jLabel12.setName(""); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(80, 32));

        jButton20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton20.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton20.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton20.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton20.setName("090"); // NOI18N
        jButton20.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton20.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        edIStart.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edIStart.setMaximumSize(new java.awt.Dimension(80, 32));
        edIStart.setMinimumSize(new java.awt.Dimension(80, 32));
        edIStart.setPreferredSize(new java.awt.Dimension(80, 32));
        edIStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edIStartActionPerformed(evt);
            }
        });

        jButton21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton21.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton21.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton21.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton21.setName("091"); // NOI18N
        jButton21.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton21.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton24.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton24.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton24.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton24.setName("100"); // NOI18N
        jButton24.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton24.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        edIThresh.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edIThresh.setMaximumSize(new java.awt.Dimension(80, 32));
        edIThresh.setMinimumSize(new java.awt.Dimension(80, 32));
        edIThresh.setPreferredSize(new java.awt.Dimension(80, 32));

        jButton25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton25.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton25.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton25.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton25.setName("101"); // NOI18N
        jButton25.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton25.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        cbIActive.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbIActive.setText("Включить");
        cbIActive.setMaximumSize(new java.awt.Dimension(80, 32));
        cbIActive.setMinimumSize(new java.awt.Dimension(80, 32));
        cbIActive.setName(""); // NOI18N
        cbIActive.setPreferredSize(new java.awt.Dimension(80, 32));
        cbIActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIActiveActionPerformed(evt);
            }
        });

        cbIVisible.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbIVisible.setText("Показать");
        cbIVisible.setMaximumSize(new java.awt.Dimension(80, 32));
        cbIVisible.setMinimumSize(new java.awt.Dimension(80, 32));
        cbIVisible.setName(""); // NOI18N
        cbIVisible.setPreferredSize(new java.awt.Dimension(80, 32));
        cbIVisible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIVisibleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edIStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbIActive, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edIThresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbIVisible, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edIStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbIActive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edIThresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbIVisible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Отсечка", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 20))); // NOI18N

        jButton56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton56.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton56.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton56.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton56.setName("090"); // NOI18N
        jButton56.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton56.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jButton56prmMousDragg(evt);
            }
        });
        jButton56.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton56prmMousPress(evt);
            }
        });
        jButton56.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton56ActionPerformed(evt);
            }
        });

        supress.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        supress.setMaximumSize(new java.awt.Dimension(80, 32));
        supress.setMinimumSize(new java.awt.Dimension(80, 32));
        supress.setPreferredSize(new java.awt.Dimension(80, 32));
        supress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supressActionPerformed(evt);
            }
        });

        jButton57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton57.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton57.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton57.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton57.setName("091"); // NOI18N
        jButton57.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton57.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jButton57prmMousDragg(evt);
            }
        });
        jButton57.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton57prmMousPress(evt);
            }
        });
        jButton57.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton57ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jButton56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(supress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(supress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButton57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnZoneLayout = new javax.swing.GroupLayout(pnZone);
        pnZone.setLayout(pnZoneLayout);
        pnZoneLayout.setHorizontalGroup(
            pnZoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnZoneLayout.createSequentialGroup()
                .addGroup(pnZoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnZoneLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnZoneLayout.setVerticalGroup(
            pnZoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnZoneLayout.createSequentialGroup()
                .addGroup(pnZoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Зоны", pnZone);

        tbVRC.getTableHeader().setFont(new java.awt.Font("Dialog", 0, 18));
        tbVRC.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        tbVRC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Время мкс.", "Усиление дБ."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbVRC.setRowHeight(28);
        tbVRC.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tbVRC);

        jButton43.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton43.setText("Добавить перед");
        jButton43.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        jButton44.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton44.setText("Удалить");
        jButton44.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        jButton45.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton45.setText("Добавить после");
        jButton45.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jLabel26.setText("Текущая точка:");

        jLabel27.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel27.setText("Время:");
        jLabel27.setMaximumSize(new java.awt.Dimension(100, 32));
        jLabel27.setMinimumSize(new java.awt.Dimension(100, 32));
        jLabel27.setPreferredSize(new java.awt.Dimension(100, 32));

        jButton46.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton46.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton46.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton46.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton46.setName("110"); // NOI18N
        jButton46.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton46.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton46.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });

        edVRCTime.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edVRCTime.setMaximumSize(new java.awt.Dimension(80, 32));
        edVRCTime.setMinimumSize(new java.awt.Dimension(80, 32));
        edVRCTime.setPreferredSize(new java.awt.Dimension(80, 32));
        edVRCTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edVRCTimeActionPerformed(evt);
            }
        });

        jButton47.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton47.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton47.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton47.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton47.setName("111"); // NOI18N
        jButton47.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton47.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton47.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel28.setText("Усиление:");
        jLabel28.setMaximumSize(new java.awt.Dimension(100, 32));
        jLabel28.setMinimumSize(new java.awt.Dimension(100, 32));
        jLabel28.setPreferredSize(new java.awt.Dimension(100, 32));

        jButton48.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton48.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton48.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton48.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton48.setName("120"); // NOI18N
        jButton48.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton48.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton48.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });

        edVRCGain.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edVRCGain.setMaximumSize(new java.awt.Dimension(80, 32));
        edVRCGain.setMinimumSize(new java.awt.Dimension(80, 32));
        edVRCGain.setPreferredSize(new java.awt.Dimension(80, 32));

        jButton49.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton49.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton49.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton49.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton49.setName("121"); // NOI18N
        jButton49.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton49.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton49.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton49ActionPerformed(evt);
            }
        });

        cbVrchGo.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbVrchGo.setText("Включить");
        cbVrchGo.setMaximumSize(new java.awt.Dimension(100, 32));
        cbVrchGo.setMinimumSize(new java.awt.Dimension(100, 32));
        cbVrchGo.setPreferredSize(new java.awt.Dimension(100, 32));
        cbVrchGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbVrchGoActionPerformed(evt);
            }
        });

        cbVRCVis.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbVRCVis.setText("Показать");
        cbVRCVis.setMaximumSize(new java.awt.Dimension(100, 32));
        cbVRCVis.setMinimumSize(new java.awt.Dimension(100, 32));
        cbVRCVis.setPreferredSize(new java.awt.Dimension(100, 32));
        cbVRCVis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbVRCVisActionPerformed(evt);
            }
        });

        jButton50.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton50.setText("Общ+");
        jButton50.setName("130"); // NOI18N
        jButton50.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton50.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton50ActionPerformed(evt);
            }
        });

        jButton51.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton51.setText("Общ-");
        jButton51.setName("131"); // NOI18N
        jButton51.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton51.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton51ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnMOTLayout = new javax.swing.GroupLayout(pnMOT);
        pnMOT.setLayout(pnMOTLayout);
        pnMOTLayout.setHorizontalGroup(
            pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMOTLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnMOTLayout.createSequentialGroup()
                        .addGroup(pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cbVRCVis, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbVrchGo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnMOTLayout.createSequentialGroup()
                                .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edVRCTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnMOTLayout.createSequentialGroup()
                                .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edVRCGain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jButton51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton50)))
                        .addContainerGap())
                    .addGroup(pnMOTLayout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(pnMOTLayout.createSequentialGroup()
                        .addGroup(pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65))))
        );
        pnMOTLayout.setVerticalGroup(
            pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(pnMOTLayout.createSequentialGroup()
                .addGroup(pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton43)
                    .addComponent(jButton44))
                .addGap(5, 5, 5)
                .addComponent(jButton45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edVRCTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edVRCGain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnMOTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnMOTLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jButton50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton51))
                    .addGroup(pnMOTLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbVrchGo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbVRCVis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("ВРЧ", pnMOT);

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel11.setText("Частота МГц.");
        jLabel11.setMaximumSize(new java.awt.Dimension(100, 32));
        jLabel11.setMinimumSize(new java.awt.Dimension(100, 32));
        jLabel11.setPreferredSize(new java.awt.Dimension(100, 32));

        jButton22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton22.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton22.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton22.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton22.setName("140"); // NOI18N
        jButton22.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton22.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        edGziWidth.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edGziWidth.setMaximumSize(new java.awt.Dimension(100, 32));
        edGziWidth.setMinimumSize(new java.awt.Dimension(100, 32));
        edGziWidth.setPreferredSize(new java.awt.Dimension(100, 32));
        edGziWidth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edGziWidthActionPerformed(evt);
            }
        });

        jButton23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton23.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton23.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton23.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton23.setName("141"); // NOI18N
        jButton23.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton23.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        cbDempfer.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbDempfer.setText("Демпфер на выходе 50 Ом");
        cbDempfer.setMaximumSize(new java.awt.Dimension(100, 32));
        cbDempfer.setMinimumSize(new java.awt.Dimension(100, 32));
        cbDempfer.setPreferredSize(new java.awt.Dimension(100, 32));
        cbDempfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDempferActionPerformed(evt);
            }
        });

        cbSoglas.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbSoglas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "нет", "0,66 мкГн", "1 мкГн", "2,2 мкГн", "3,3 мкГн", "4,7 мкГн", "6,8 мкГн", "15 мкГн" }));
        cbSoglas.setMaximumSize(new java.awt.Dimension(100, 32));
        cbSoglas.setMinimumSize(new java.awt.Dimension(100, 32));
        cbSoglas.setPreferredSize(new java.awt.Dimension(100, 32));
        cbSoglas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSoglasActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel13.setText("Согласующая индуктивность на выходе.");
        jLabel13.setMaximumSize(new java.awt.Dimension(100, 32));
        jLabel13.setMinimumSize(new java.awt.Dimension(100, 32));
        jLabel13.setPreferredSize(new java.awt.Dimension(100, 32));

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel14.setText("Число импульсов возбуждения.");
        jLabel14.setMaximumSize(new java.awt.Dimension(100, 32));
        jLabel14.setMinimumSize(new java.awt.Dimension(100, 32));
        jLabel14.setPreferredSize(new java.awt.Dimension(100, 32));

        jButton26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton26.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton26.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton26.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton26.setName("150"); // NOI18N
        jButton26.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton26.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        edGziCount.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edGziCount.setMaximumSize(new java.awt.Dimension(100, 32));
        edGziCount.setMinimumSize(new java.awt.Dimension(100, 32));
        edGziCount.setPreferredSize(new java.awt.Dimension(100, 32));
        edGziCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edGziCountActionPerformed(evt);
            }
        });

        jButton27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton27.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton27.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton27.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton27.setName("151"); // NOI18N
        jButton27.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton27.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnGenerLayout = new javax.swing.GroupLayout(pnGener);
        pnGener.setLayout(pnGenerLayout);
        pnGenerLayout.setHorizontalGroup(
            pnGenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnGenerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnGenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addComponent(cbDempfer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnGenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnGenerLayout.createSequentialGroup()
                        .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edGziWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnGenerLayout.createSequentialGroup()
                        .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edGziCount, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cbSoglas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(192, 192, 192))
        );
        pnGenerLayout.setVerticalGroup(
            pnGenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnGenerLayout.createSequentialGroup()
                .addGroup(pnGenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edGziWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnGenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edGziCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnGenerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbSoglas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbDempfer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Генератор", pnGener);

        cbRVhoda.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbRVhoda.setText("Демпфер на входе 50 Ом");
        cbRVhoda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRVhodaActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel15.setText("Фильтр приемника");

        cbAnifiltr.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbAnifiltr.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "нет     ", "0,8..2,1      ", "1,1..2,2      ", "1,2..4,6      ", "1,5..3,0      ", "1,8..2,8      ", "2,4..4,7      ", "2,5..8,6      ", "2,6..3,6      ", "2,7..4,1      ", "2,9..3,8      ", "3,1..4,3      ", "3,7..6,6      ", "4,0..6,0      ", "5,1..9,3      ", "5,5..13,0" }));
        cbAnifiltr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAnifiltrActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel16.setText("Число накапливаемых А - сканов");

        jButton28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton28.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton28.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton28.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton28.setName("160"); // NOI18N
        jButton28.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton28.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        edPeakMode.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edPeakMode.setMaximumSize(new java.awt.Dimension(100, 32));
        edPeakMode.setMinimumSize(new java.awt.Dimension(100, 32));
        edPeakMode.setPreferredSize(new java.awt.Dimension(100, 32));
        edPeakMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edPeakModeActionPerformed(evt);
            }
        });

        jButton29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton29.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton29.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton29.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton29.setName("161"); // NOI18N
        jButton29.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton29.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel17.setText("Степень фильтрации");

        jButton30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton30.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton30.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton30.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton30.setName("170"); // NOI18N
        jButton30.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton30.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        edASDFiltr.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edASDFiltr.setMaximumSize(new java.awt.Dimension(100, 32));
        edASDFiltr.setMinimumSize(new java.awt.Dimension(100, 32));
        edASDFiltr.setPreferredSize(new java.awt.Dimension(100, 32));
        edASDFiltr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edASDFiltrActionPerformed(evt);
            }
        });

        jButton31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton31.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton31.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton31.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton31.setName("171"); // NOI18N
        jButton31.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton31.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton31.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        cbDefectType.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbDefectType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Продольный", "Поперечный" }));
        cbDefectType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDefectTypeActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel34.setText("Тип датчика");

        javax.swing.GroupLayout pnRecLayout = new javax.swing.GroupLayout(pnRec);
        pnRec.setLayout(pnRecLayout);
        pnRecLayout.setHorizontalGroup(
            pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRecLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cbRVhoda, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbAnifiltr, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnRecLayout.createSequentialGroup()
                        .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(edPeakMode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(edASDFiltr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(cbDefectType, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnRecLayout.setVerticalGroup(
            pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRecLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbRVhoda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(cbAnifiltr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edPeakMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edASDFiltr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnRecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(cbDefectType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Приемник", pnRec);

        jLabel22.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel22.setText("1 Образец мм.");

        jButton36.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton36.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton36.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton36.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton36.setName("230"); // NOI18N
        jButton36.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton36.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton36.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        edKalibr1.setMaximumSize(new java.awt.Dimension(100, 32));
        edKalibr1.setMinimumSize(new java.awt.Dimension(100, 32));
        edKalibr1.setPreferredSize(new java.awt.Dimension(100, 32));
        edKalibr1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edKalibr1ActionPerformed(evt);
            }
        });

        jButton37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton37.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton37.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton37.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton37.setName("231"); // NOI18N
        jButton37.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton37.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton37.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        lbCbr1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lbCbr1.setText("jLabel23");

        jButton38.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton38.setText("Запомнить");
        jButton38.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel24.setText("2 Образец мм.");

        jButton39.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton39.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton39.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton39.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton39.setName("240"); // NOI18N
        jButton39.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton39.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton39.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        edKalibr2.setMaximumSize(new java.awt.Dimension(100, 32));
        edKalibr2.setMinimumSize(new java.awt.Dimension(100, 32));
        edKalibr2.setPreferredSize(new java.awt.Dimension(100, 32));
        edKalibr2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edKalibr2ActionPerformed(evt);
            }
        });

        jButton40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton40.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton40.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton40.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton40.setName("241"); // NOI18N
        jButton40.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton40.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton40.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        lbCbr2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lbCbr2.setText("jLabel23");

        jButton41.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton41.setText("Запомнить");
        jButton41.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jButton42.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton42.setText("Очистить");
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        cbKalibrOn.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbKalibrOn.setText("Калибровка включена");
        cbKalibrOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbKalibrOnActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel31.setText("Режим измерения");

        cbTimeMode.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbTimeMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "От 0 до А - зоны", "От А до В - зоны", "Автомат" }));
        cbTimeMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTimeModeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnKalibrLayout = new javax.swing.GroupLayout(pnKalibr);
        pnKalibr.setLayout(pnKalibrLayout);
        pnKalibrLayout.setHorizontalGroup(
            pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnKalibrLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnKalibrLayout.createSequentialGroup()
                        .addComponent(cbKalibrOn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton42))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnKalibrLayout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbTimeMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnKalibrLayout.createSequentialGroup()
                        .addGroup(pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnKalibrLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edKalibr1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(pnKalibrLayout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edKalibr2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addGroup(pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbCbr2, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                            .addComponent(lbCbr1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton38, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(jButton41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnKalibrLayout.setVerticalGroup(
            pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnKalibrLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(cbTimeMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edKalibr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCbr1)
                    .addComponent(jButton38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edKalibr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCbr2)
                    .addComponent(jButton41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnKalibrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton42)
                    .addComponent(cbKalibrOn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Калибровка", pnKalibr);

        cbComp.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbComp.setText("Совмещенный режим работы");
        cbComp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCompActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel19.setText("Сдвиг 0 точки по вертикали");

        jButton32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton32.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton32.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton32.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton32.setName("180"); // NOI18N
        jButton32.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton32.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        edAdcOffs.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edAdcOffs.setMaximumSize(new java.awt.Dimension(100, 32));
        edAdcOffs.setMinimumSize(new java.awt.Dimension(100, 32));
        edAdcOffs.setPreferredSize(new java.awt.Dimension(100, 32));

        jButton33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton33.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton33.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton33.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton33.setName("181"); // NOI18N
        jButton33.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton33.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton33.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel20.setText("Режим синхронизации");

        cbProbe.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cbProbe.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Всегда", "По четным", "По нечетным" }));
        cbProbe.setMaximumSize(new java.awt.Dimension(100, 32));
        cbProbe.setMinimumSize(new java.awt.Dimension(100, 32));
        cbProbe.setPreferredSize(new java.awt.Dimension(100, 32));
        cbProbe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbProbeActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel21.setText("Делитель синхронизации");

        jButton34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/051-add.png"))); // NOI18N
        jButton34.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton34.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton34.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton34.setName("190"); // NOI18N
        jButton34.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton34.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton34.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        edSyncDelay.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edSyncDelay.setMaximumSize(new java.awt.Dimension(100, 32));
        edSyncDelay.setMinimumSize(new java.awt.Dimension(100, 32));
        edSyncDelay.setPreferredSize(new java.awt.Dimension(100, 32));

        jButton35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/038-delete.png"))); // NOI18N
        jButton35.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton35.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton35.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton35.setName("191"); // NOI18N
        jButton35.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton35.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                prmMousDragg(evt);
            }
        });
        jButton35.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                prmMousPress(evt);
            }
        });
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel23.setText("Координата датчика мм.");

        edMechPos.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        edMechPos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        edMechPos.setText("jTextField1");
        edMechPos.setMaximumSize(new java.awt.Dimension(100, 32));
        edMechPos.setMinimumSize(new java.awt.Dimension(100, 32));
        edMechPos.setPreferredSize(new java.awt.Dimension(100, 32));
        edMechPos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edMechPosActionPerformed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel33.setText("Фактическая базовая частота пакетов");

        factBasFrq.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        factBasFrq.setMaximumSize(new java.awt.Dimension(100, 32));
        factBasFrq.setMinimumSize(new java.awt.Dimension(100, 32));
        factBasFrq.setPreferredSize(new java.awt.Dimension(100, 32));

        javax.swing.GroupLayout pnOtherLayout = new javax.swing.GroupLayout(pnOther);
        pnOther.setLayout(pnOtherLayout);
        pnOtherLayout.setHorizontalGroup(
            pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnOtherLayout.createSequentialGroup()
                        .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(75, 75, 75)
                        .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnOtherLayout.createSequentialGroup()
                                .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edSyncDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cbProbe, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnOtherLayout.createSequentialGroup()
                                    .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(edAdcOffs, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(pnOtherLayout.createSequentialGroup()
                        .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addComponent(jLabel23))
                        .addGap(72, 72, 72)
                        .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(factBasFrq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(edMechPos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(20, 20, 20))
            .addGroup(pnOtherLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(cbComp)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnOtherLayout.setVerticalGroup(
            pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbComp, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edAdcOffs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(cbProbe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edSyncDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(factBasFrq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(edMechPos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(227, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Прочее", pnOther);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(cbChanList, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbHardwareType, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbHardwareType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbChanList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
//          System.out.println("Отработасось нажатие");
          if((activChanParam.getGain() - 0.5f) < 0){
            refreshActParams();
            return;
        }
        activChanParam.setGain(activChanParam.getGain() - 0.5f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if((activChanParam.getGain() + 0.5f) > 110){
            refreshActParams();
            return;
        }
        activChanParam.setGain(activChanParam.getGain() + 0.5f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if((activChanParam.getRange()- 2.5f) < 0){
            refreshActParams();
            return;
        }
        activChanParam.setRange(activChanParam.getRange() - 2.5f);
        changeParams();
        refreshActParams();

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        activChanParam.setRange(activChanParam.getRange() + 2.5f);
        changeParams();
        refreshActParams();

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        activChanParam.setSignal_delay(activChanParam.getSignal_delay() + 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        activChanParam.setSignal_delay(activChanParam.getSignal_delay() - 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void cbDetectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDetectActionPerformed
        if (isUpdatingParams) {
            return;
        }
        int i = cbDetect.getSelectedIndex();
        if (i != -1) {
            activChanParam.setDetector((short) i);
            changeParams();
            refreshActParams();
        }
    }//GEN-LAST:event_cbDetectActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JPopupMenu pp = new JPopupMenu();
        JMenuItem mi = new JMenuItem("Копировать настройки канала");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(clipbPrm == null) clipbPrm = new DeviceUSKUdpParam();
                clipbPrm.copyFrom(activChanParam);
                clipbName = ((DeviceUSKUdpChanDef) cbChanList.getSelectedItem()).chanDef;
            }
        });
        pp.add(mi);
        mi = new JMenuItem("Вставить из " + clipbName);
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(clipbPrm != null){
                    activChanParam.copyFrom(clipbPrm);
                    changeParams();
                    refreshActParams();
                }
            }
        });
        if(clipbPrm == null){
            mi.setEnabled(false);
        }
        pp.add(mi);
        mi = new JMenuItem("Установить поумолчанию");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeviceUSKUdpChanDef ch = (DeviceUSKUdpChanDef) cbChanList.getSelectedItem();
                DeviceUSKUdpParam pr = ch.driver.getParams().prms[ch.chanIndex];
                pr.resetToDefault();
                ch.driver.setChnlParam(pr);
                refreshActParams();
            }
        });
        pp.add(mi);
        mi = new JMenuItem("Сохранить все параметры в файл");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser flDl = new JFileChooser();
                flDl.setDialogType(JFileChooser.FILES_ONLY);
                flDl.setDialogTitle("Выберете файл для сохранения параметров");
                flDl.showSaveDialog(null);
                File saveFile = flDl.getSelectedFile();
                if (JOptionPane.showConfirmDialog(null, "<html>Вы уверены!!! "
                        + "<br> В том что вы хотите СОХРАНИТЬ В ФАЙЛ "
                        + saveFile + "</html>", "Запрос", JOptionPane.YES_NO_OPTION)
                        == JOptionPane.NO_OPTION) {
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(saveFile);

                    Properties prp = new Properties();
                    for(int i = 0; i < cbChanList.getItemCount(); i++){
                        DeviceUSKUdpChanDef cd = (DeviceUSKUdpChanDef) cbChanList.getItemAt(i);
                        cd.driver.getParams().prms[cd.chanIndex].writeToIni(prp,i+1);
                    }
                    prp.store(fos, "");
                } catch (IOException ee) {
                    JOptionPane.showMessageDialog(null, "Не удалось записать параметры");
                }
            }
        });
        pp.add(mi);
        mi = new JMenuItem("Восстановить все параметры из файла");
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser flDl = new JFileChooser();
                flDl.setDialogType(JFileChooser.FILES_ONLY);
                flDl.setDialogTitle("Выберете файл для загрузки параметров");
                flDl.showOpenDialog(null);
                File saveFile = flDl.getSelectedFile();
                if (JOptionPane.showConfirmDialog(null, "<html>Вы уверены!!! "
                        + "<br>Что вы хотите ЗАГРУЗИТЬ ИЗ ФАЙЛА <br>"
                        + saveFile + "</html>", "Запрос", JOptionPane.YES_NO_OPTION)
                        == JOptionPane.NO_OPTION) {
                    return;
                }
                try {
                    FileInputStream fi = new FileInputStream(saveFile);
                    Properties prp = new Properties();
                    prp.load(fi);
                    long prDrv = 0;
                    DeviceUSKUdpParam pr;
                    DeviceUSKUdpChanDef cd;
                    DeviceUSKUdp dr = ((DeviceUSKUdpChanDef)cbChanList.getItemAt(0)).driver;
                    prDrv = dr.getDeviceId();
                    for(int i = 0; i < cbChanList.getItemCount(); i++){
                        cd = (DeviceUSKUdpChanDef) cbChanList.getItemAt(i);
                        if(cd.driver.getDeviceId() != prDrv){
                            dr.setParams(dr.getParams());
                        }
                        dr = cd.driver;
                        pr = new DeviceUSKUdpParam();
                        pr.loadFromIni(prp, i+1);
                        dr.getParams().prms[cd.chanIndex] = pr;
                        prDrv = dr.getDeviceId();
                    }
                    dr.setParams(dr.getParams());
                    changePrm1 = changePrm2 = true;
                    refreshActParams();
                } catch (IOException ex) {
                    log.error("Error loading usk properties", ex);
                }
            }
        });
        pp.add(mi);
        pp.show((Component) evt.getSource(), 0, 0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        activChanParam.setGzi_width_int((short) (activChanParam.getGzi_width_int() - 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        activChanParam.setGzi_width_int((short) (activChanParam.getGzi_width_int() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton23ActionPerformed

    private void cbDempferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDempferActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setDempfer(cbDempfer.isSelected());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbDempferActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        activChanParam.setGzi_count((short) (activChanParam.getGzi_count() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        activChanParam.setGzi_count((short) (activChanParam.getGzi_count() - 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton27ActionPerformed

    private void cbRVhodaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRVhodaActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setRvhoda(cbRVhoda.isSelected());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbRVhodaActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        activChanParam.setPeak_mode((short) (activChanParam.getPeak_mode() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        activChanParam.setPeak_mode((short) (activChanParam.getPeak_mode() - 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        activChanParam.setAsd_filter((short) (activChanParam.getAsd_filter() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        activChanParam.setAsd_filter((short) (activChanParam.getAsd_filter() - 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        activChanParam.setKalibr1Obrazec(activChanParam.getKalibr1Obrazec() + 0.1f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        activChanParam.setKalibr1Obrazec(activChanParam.getKalibr1Obrazec() - 0.1f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        activChanParam.setKalibr2Obrazec(activChanParam.getKalibr2Obrazec() + 0.1f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        activChanParam.setKalibr2Obrazec(activChanParam.getKalibr2Obrazec() - 0.1f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton40ActionPerformed

    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        if (curVRCPoint != -1) {
            activChanParam.setVrch_times(activChanParam.getVrch_times(curVRCPoint) + 0.2f, curVRCPoint);
            changeParams();
            refreshActParams();
        }
    }//GEN-LAST:event_jButton46ActionPerformed

    private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
        if (curVRCPoint != -1) {
            activChanParam.setVrch_times(activChanParam.getVrch_times(curVRCPoint) - 0.2f, curVRCPoint);
            changeParams();
            refreshActParams();
        }
    }//GEN-LAST:event_jButton47ActionPerformed

    private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
        if (curVRCPoint != -1) {
            activChanParam.setVrch_gains(activChanParam.getVrch_gains(curVRCPoint) + 0.5f, curVRCPoint);
            changeParams();
            refreshActParams();
        }
    }//GEN-LAST:event_jButton48ActionPerformed

    private void jButton49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton49ActionPerformed
        if (curVRCPoint != -1) {
            activChanParam.setVrch_gains(activChanParam.getVrch_gains(curVRCPoint) - 0.5f, curVRCPoint);
            changeParams();
            refreshActParams();
        }
    }//GEN-LAST:event_jButton49ActionPerformed

    private void cbChanListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbChanListActionPerformed
        if (!isUpdatingParams) {
            setActiveChan();
        }
    }//GEN-LAST:event_cbChanListActionPerformed

    private void cbVrchGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbVrchGoActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setVrch_go(cbVrchGo.isSelected());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbVrchGoActionPerformed

    private void cbVRCVisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbVRCVisActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setVrch_visible(cbVRCVis.isSelected());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbVRCVisActionPerformed

    private void cbSoglasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSoglasActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setSoglas((short) cbSoglas.getSelectedIndex());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbSoglasActionPerformed

    private void cbAnifiltrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAnifiltrActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setAnfilter((short) cbAnifiltr.getSelectedIndex());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbAnifiltrActionPerformed

    private void cbKalibrOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbKalibrOnActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setKalibrEnable(cbKalibrOn.isSelected());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbKalibrOnActionPerformed

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        activChanParam.setKalibrEnable(false);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton42ActionPerformed

    private void cbEnabledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEnabledActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setEnabled(cbEnabled.isSelected());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbEnabledActionPerformed

    private void cbHardwareTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbHardwareTypeActionPerformed
        if (isUpdatingParams) {
            return;
        }
        int sel = cbHardwareType.getSelectedIndex();
        switch (sel) {
            case 0:
                activChanParam.setHardware_type(DeviceUSKUdpParam.TIME_CONTROL);
                break;
            case 1:
                activChanParam.setHardware_type(DeviceUSKUdpParam.FLAW_CONTROL);
                break;
        }
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbHardwareTypeActionPerformed

    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        if (curVRCPoint != -1) {
            activChanParam.remVRCHPoint(curVRCPoint);
            curVRCPoint = 0;
            changeParams();
            refreshActParams();
        }
    }//GEN-LAST:event_jButton44ActionPerformed

    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        if (curVRCPoint != -1) {
            activChanParam.addVRCHPoint(curVRCPoint);
            changeParams();
            refreshActParams();
        }
    }//GEN-LAST:event_jButton43ActionPerformed

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        if (curVRCPoint != -1) {
            activChanParam.addVRCHPoint(curVRCPoint);
            curVRCPoint++;
            changeParams();
            refreshActParams();
        }
    }//GEN-LAST:event_jButton45ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        activChanParam.setKalibr1Value((short) drv.getThickVal(activChanParam.getId()));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton38ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        activChanParam.setKalibr2Value((short) drv.getThickVal(activChanParam.getId()));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton50ActionPerformed
        for (int i = 0; i < activChanParam.getVrch_points(); i++) {
            float gain = activChanParam.getVrch_gains(i);
            gain += 0.5f;
            activChanParam.setVrch_gains(gain, i);
        }
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton50ActionPerformed

    private void jButton51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton51ActionPerformed
        for (int i = 0; i < activChanParam.getVrch_points(); i++) {
            float gain = activChanParam.getVrch_gains(i);
            gain -= 0.5f;
            if (gain < 0f) {
                gain = 0;
            }
            activChanParam.setVrch_gains(gain, i);
        }
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton51ActionPerformed

    private void cbEnabledMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbEnabledMouseClicked
        if (evt.isPopupTrigger()) {
            JPopupMenu pm = new JPopupMenu();
            JMenuItem jm = new JMenuItem("Включить все");
            jm.addActionListener((ActionEvent e) -> {
                DeviceUSKUdpChanDef ch;
                DeviceUSKUdpParam pr;
                for (int i = 0; i < cbChanList.getItemCount(); i++) {
                    ch = (DeviceUSKUdpChanDef) cbChanList.getItemAt(i);
                    pr = ch.driver.getParams().prms[ch.chanIndex];
                    pr.setEnabled(true);
                    ch.driver.setChnlParam(pr);
                }
                changeParams();
                refreshActParams();
            });
            pm.add(jm);
            jm = new JMenuItem("Все выключить");
            jm.addActionListener((ActionEvent e) -> {
                DeviceUSKUdpChanDef ch;
                DeviceUSKUdpParam pr;
                for (int i = 0; i < cbChanList.getItemCount(); i++) {
                    ch = (DeviceUSKUdpChanDef) cbChanList.getItemAt(i);
                    pr = ch.driver.getParams().prms[ch.chanIndex];
                    pr.setEnabled(false);
                    ch.driver.setChnlParam(pr);
                }
                changeParams();
                refreshActParams();
            });
            pm.add(jm);
            pm.show(cbEnabled, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_cbEnabledMouseClicked

    private void edGziWidthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edGziWidthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edGziWidthActionPerformed

    private void edGainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edGainActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edGainActionPerformed

    private void edDelayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edDelayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edDelayActionPerformed

    private void edRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edRangeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edRangeActionPerformed

    private void edVRCTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edVRCTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edVRCTimeActionPerformed

    private void edGziCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edGziCountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edGziCountActionPerformed

    private void edPeakModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edPeakModeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edPeakModeActionPerformed

    private void edASDFiltrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edASDFiltrActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edASDFiltrActionPerformed

    private void edKalibr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edKalibr1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edKalibr1ActionPerformed

    private void edKalibr2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edKalibr2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edKalibr2ActionPerformed

    private void cbTimeModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTimeModeActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setTime_mode((short) cbTimeMode.getSelectedIndex());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbTimeModeActionPerformed

    private void prmMousPress(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prmMousPress
//        EventQueue.invokeLater(new MousPressClick((JButton) evt.getSource()));
//        if(pressRepTimer == null){
//            pressRepTimer = new Timer(1000, (ActionEvent e) -> {
//                repeatPress(evt.getComponent());
//            });
//        }
//        if(pressRepTimer.isRunning()) pressRepTimer.stop();
//        else pressRepTimer.start();
    }//GEN-LAST:event_prmMousPress

    private void prmMousDragg(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prmMousDragg
    }//GEN-LAST:event_prmMousDragg

    private void prmMousReleas(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prmMousReleas
//        clickRunning = false;
//        synchronized(waitPress){
//            waitPress.notifyAll();
//        }
        if(pressRepTimer != null)
            pressRepTimer.stop();
    }//GEN-LAST:event_prmMousReleas

    private void cbDefectTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDefectTypeActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setDefect_type(cbDefectType.getSelectedIndex() == 0? 0: 2);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbDefectTypeActionPerformed

    private void edMechPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edMechPosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edMechPosActionPerformed

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        activChanParam.setSync_delay((short) (activChanParam.getSync_delay() - 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton35ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        activChanParam.setSync_delay((short) (activChanParam.getSync_delay() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton34ActionPerformed

    private void cbProbeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbProbeActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setPulser_takt((short) cbProbe.getSelectedIndex());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbProbeActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        activChanParam.setAds_offset((short) (activChanParam.getAds_offset() - 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton33ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        activChanParam.setAds_offset((short) (activChanParam.getAds_offset() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton32ActionPerformed

    private void cbCompActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCompActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setProbe_mode(cbComp.isSelected());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbCompActionPerformed

    private void cbIVisibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbIVisibleActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setI_visible(cbIVisible.isSelected());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbIVisibleActionPerformed

    private void cbIActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbIActiveActionPerformed
        if (isUpdatingParams) {
            return;
        }
        activChanParam.setI_active(cbIActive.isSelected());
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_cbIActiveActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        activChanParam.setI_thresh((short) (activChanParam.getI_thresh() - 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        activChanParam.setI_thresh((short) (activChanParam.getI_thresh() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        activChanParam.setI_start(activChanParam.getI_start() - 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void edIStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edIStartActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edIStartActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        activChanParam.setI_start(activChanParam.getI_start() + 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        activChanParam.setB_thresh((short) (activChanParam.getB_thresh() - 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void edBThreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edBThreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edBThreshActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        activChanParam.setB_thresh((short) (activChanParam.getB_thresh() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        activChanParam.setB_width(activChanParam.getB_width() - 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void edBWidthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edBWidthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edBWidthActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        activChanParam.setB_width(activChanParam.getB_width() + 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        activChanParam.setB_start(activChanParam.getB_start() - 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void edBStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edBStartActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edBStartActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        activChanParam.setB_start(activChanParam.getB_start() + 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        activChanParam.setA_thresh((short) (activChanParam.getA_thresh() - 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void edAThreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edAThreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edAThreshActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        activChanParam.setA_thresh((short) (activChanParam.getA_thresh() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        activChanParam.setA_width(activChanParam.getA_width() - 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void edAWidthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edAWidthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edAWidthActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        activChanParam.setA_width(activChanParam.getA_width() + 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        activChanParam.setA_start(activChanParam.getA_start() - 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void edAStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edAStartActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edAStartActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        activChanParam.setA_start(activChanParam.getA_start() + 0.2f);
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton56prmMousDragg(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton56prmMousDragg
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton56prmMousDragg

    private void jButton56prmMousPress(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton56prmMousPress
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton56prmMousPress

    private void jButton56ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton56ActionPerformed
        activChanParam.setSupression((short) (activChanParam.getSupression() + 1));
        changeParams();
        refreshActParams();
    }//GEN-LAST:event_jButton56ActionPerformed

    private void supressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_supressActionPerformed

    private void jButton57prmMousDragg(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton57prmMousDragg
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton57prmMousDragg

    private void jButton57prmMousPress(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton57prmMousPress
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton57prmMousPress

    private void jButton57ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton57ActionPerformed
        activChanParam.setSupression((short) (activChanParam.getSupression() - 1));
        if(activChanParam.getSupression() < 0) 
            activChanParam.setSupression((short)0);
        changeParams();
        refreshActParams();
       // TODO add your handling code here:
    }//GEN-LAST:event_jButton57ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbAnifiltr;
    private javax.swing.JComboBox cbChanList;
    private javax.swing.JCheckBox cbComp;
    private javax.swing.JComboBox cbDefectType;
    private javax.swing.JCheckBox cbDempfer;
    private javax.swing.JComboBox cbDetect;
    private javax.swing.JCheckBox cbEnabled;
    private javax.swing.JComboBox cbHardwareType;
    private javax.swing.JCheckBox cbIActive;
    private javax.swing.JCheckBox cbIVisible;
    private javax.swing.JCheckBox cbKalibrOn;
    private javax.swing.JComboBox cbProbe;
    private javax.swing.JCheckBox cbRVhoda;
    private javax.swing.JComboBox cbSoglas;
    private javax.swing.JComboBox cbTimeMode;
    private javax.swing.JCheckBox cbVRCVis;
    private javax.swing.JCheckBox cbVrchGo;
    private javax.swing.JTextField edASDFiltr;
    private javax.swing.JTextField edAStart;
    private javax.swing.JTextField edAThresh;
    private javax.swing.JTextField edAWidth;
    private javax.swing.JTextField edAdcOffs;
    private javax.swing.JTextField edBStart;
    private javax.swing.JTextField edBThresh;
    private javax.swing.JTextField edBWidth;
    private javax.swing.JTextField edDelay;
    private javax.swing.JTextField edGain;
    private javax.swing.JTextField edGziCount;
    private javax.swing.JTextField edGziWidth;
    private javax.swing.JTextField edIStart;
    private javax.swing.JTextField edIThresh;
    private javax.swing.JTextField edKalibr1;
    private javax.swing.JTextField edKalibr2;
    private javax.swing.JTextField edMechPos;
    private javax.swing.JTextField edPeakMode;
    private javax.swing.JTextField edRange;
    private javax.swing.JTextField edSyncDelay;
    private javax.swing.JTextField edVRCGain;
    private javax.swing.JTextField edVRCTime;
    private javax.swing.JLabel factBasFrq;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton56;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbAScan;
    private javax.swing.JLabel lbCbr1;
    private javax.swing.JLabel lbCbr2;
    private javax.swing.JPanel pnGener;
    private javax.swing.JPanel pnKalibr;
    private javax.swing.JPanel pnMOT;
    private javax.swing.JPanel pnOther;
    private javax.swing.JPanel pnRec;
    private javax.swing.JPanel pnZone;
    private javax.swing.JTextField supress;
    private javax.swing.JTable tbVRC;
    // End of variables declaration//GEN-END:variables

    /**Параметры установки были изменены*/
    synchronized private void changeParams() {
        if(drv.setChnlParam(activChanParam)==0){
            if(changePrm1) return;
            changePrm1 = true;
        }
    }
    /**Сброс признака изменения параметров*/
    public void resetChanges(){
        changePrm1 = changePrm2 = false;
        deltaGainThick = deltaGainDir = 0;
    }
    /**Параметры были изменены*/
    public boolean isChangeParams(){
        return (changePrm1 || changePrm2);
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isUpdatingParams) {
            return;
        }
        int sel = tbVRC.getSelectedRow();
        if (sel != -1) {
            if (sel != curVRCPoint) {
                curVRCPoint = sel;
                updateVrchPos();
            }
        }
        changeParams();
    }
    /**
     * Обновляет ослабление каналов толщинометрии в УЗК.
     */
    synchronized public void refreshFailTolsh() {
        DeviceUSKUdpChanDef ch, selCh;
        DeviceUSKUdpParam pr;
        curFail = 0;
        for (int i = 0; i < cbChanList.getItemCount(); i++) {
            ch = (DeviceUSKUdpChanDef) cbChanList.getItemAt(i);
            pr = ch.driver.getParams().prms[ch.chanIndex];
            if (pr.getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
                if (pr.getGain() - deltaGainThick <= 0) {
                    ch.driver.setChnlParam(pr, pr.getGain());
                } else {
                    ch.driver.setChnlParam(pr, deltaGainThick);
                }
                if(ch.driver.getParams().deltaGainThick != deltaGainThick)
                    ch.driver.getParams().deltaGainThick = deltaGainThick;
                if (!changePrm1 || !changePrm2) {
                    long idDrv = ch.driver.getDeviceId();
                    if (!changePrm1) {
                        idChangeDrv = idDrv;
                        changePrm1 = true;
                        return;
                    }
                    if (!changePrm2 && idChangeDrv != idDrv) {
                        changePrm2 = true;
                    }
                }
            }
        }
    }
    /**Повторение нажатия, если кнопку держали*/
    private void repeatPress(Component src) {
        int cod = Integer.valueOf(src.getName());
        
        int delta = 1;
        switch (cod) {
            case 11:
            case 10:
                activChanParam.setGain((float) (activChanParam.getGain()
                        + delta * (cod % 10 == 1 ? -1 : 1)));
                break;
            case 20:
            case 21:
                activChanParam.setRange(activChanParam.getRange()
                        + delta * (cod % 10 == 1 ? -1 : 1));
                break;
            case 30:
            case 31:
                activChanParam.setSignal_delay(activChanParam.getSignal_delay()
                        + delta * (cod % 10 == 1 ? -1 : 1));
                break;
            case 40:
            case 41:
                activChanParam.setA_start(activChanParam.getA_start()
                        + delta * (cod % 10 == 1 ? -1 : 1));
                break;
            case 50:
            case 51:
                activChanParam.setA_width(activChanParam.getA_width()
                        + delta * (cod % 10 == 1 ? -1 : 1));
                break;
            case 60:
            case 61:
                activChanParam.setA_thresh((short) (activChanParam.getA_thresh()
                        + delta * (cod % 10 == 1 ? -1 : 1)));
                break;
            case 70:
            case 71:
                activChanParam.setB_start(activChanParam.getB_start()
                        + delta * (cod % 10 == 1 ? -1 : 1));
                break;
            case 80:
            case 81:
                activChanParam.setB_width(activChanParam.getB_width()
                        + delta * (cod % 10 == 1 ? -1 : 1));
                break;
            case 90:
            case 91:
                activChanParam.setI_start(activChanParam.getI_start()
                        + delta * (cod % 10 == 1 ? -1 : 1));
                break;
            case 100:
            case 101:
                activChanParam.setI_thresh((short) (activChanParam.getI_thresh()
                        + delta * (cod % 10 == 1 ? -1 : 1)));
                break;
            case 110:
            case 111:
                activChanParam.setVrch_times(activChanParam.getVrch_times(curVRCPoint)
                        + delta * (cod % 10 == 1 ? -1 : 1), curVRCPoint);
                break;
            case 120:
            case 121:
                activChanParam.setVrch_gains(activChanParam.getVrch_gains(curVRCPoint)
                        + delta * (cod % 10 == 1 ? -1 : 1), curVRCPoint);
                break;
            case 170:
            case 171:
                activChanParam.setAsd_filter((short) (activChanParam.getAsd_filter()
                        + delta * (cod % 10 == 1 ? -1 : 1)));
                break;
            case 180:
            case 181:
                activChanParam.setAds_offset((short) (activChanParam.getAds_offset()
                        + delta * (cod % 10 == 1 ? -1 : 1)));
                break;
            case 190:
            case 191:
                activChanParam.setSync_delay((short) (activChanParam.getSync_delay()
                        + delta * (cod % 10 == 1 ? -1 : 1)));
                break;
            case 200:
            case 201:
            case 210:
            case 211:
                deltaGainDir = deltaGainDir
                        + delta * (cod % 10 == 1 ? -1 : 1);
                break;
            case 220:
            case 221:
                deltaGainThick = deltaGainThick
                        + delta * (cod % 10 == 1 ? -1 : 1);
                break;
            case 230:
            case 231:
                activChanParam.setKalibr1Obrazec(activChanParam.getKalibr1Obrazec()
                        + delta * (cod % 10 == 1 ? -1 : 1));
                break;
            case 240:
            case 241:
                activChanParam.setKalibr2Obrazec(activChanParam.getKalibr2Obrazec()
                        + delta * (cod % 10 == 1 ? -1 : 1));
                break;
            case 250:
            case 251:
                activChanParam.setB_thresh((short) (activChanParam.getB_thresh()
                        + delta * (cod % 10 == 1 ? -1 : 1)));
        }
//            changeParams();
        refreshActParams();
    }
    /**
     * Обновление ослаблений поперечных каналов дефектоскопии.
     */
    synchronized public void refreshFailDir() {
        DeviceUSKUdpChanDef ch;
        DeviceUSKUdpParam pr;
        for (int i = 0; i < cbChanList.getItemCount(); i++) {
            ch = (DeviceUSKUdpChanDef) cbChanList.getItemAt(i);
            pr = ch.driver.getParams().prms[ch.chanIndex];
            if (pr.getHardware_type() == DeviceUSKUdpParam.FLAW_CONTROL && (pr.getDefect_type() == 2 
                    || pr.getDefect_type() == 3)) {
                if (pr.getGain() - deltaGainDir <= 0) {
                    ch.driver.setChnlParam(pr, pr.getGain());
                } else {
                    ch.driver.setChnlParam(pr, deltaGainDir);
                }
                if(ch.driver.getParams().deltaGainDir != deltaGainDir)
                    ch.driver.getParams().deltaGainDir = deltaGainDir;
                if (!changePrm1 || !changePrm2) {//Выяснение в каком блоке изменились
                    long idDrv = ch.driver.getDeviceId();//параметры, чтобы поставить флаг
                    if (!changePrm1) {
                        idChangeDrv = idDrv;
                        changePrm1 = true;
                        return;
                    }
                    if (!changePrm2 && idChangeDrv != idDrv) {
                        changePrm2 = true;
                    }
                }
            }
        }
    }
    /**
     * Обновление ослаблений продольных каналов дефектоскопии.
     */
    synchronized public void refreshFailAx() {
        DeviceUSKUdpChanDef ch;
        DeviceUSKUdpParam pr;
        for (int i = 0; i < cbChanList.getItemCount(); i++) {
            ch = (DeviceUSKUdpChanDef) cbChanList.getItemAt(i);
            pr = ch.driver.getParams().prms[ch.chanIndex];
            if (pr.getHardware_type() == DeviceUSKUdpParam.FLAW_CONTROL && (pr.getDefect_type() == 0 
                    || pr.getDefect_type() == 1)) {
                if (pr.getGain() - deltaGainAxial <= 0) {
                    ch.driver.setChnlParam(pr, pr.getGain());
                } else {
                    ch.driver.setChnlParam(pr, deltaGainAxial);
                }
                if(ch.driver.getParams().deltaGainAx != deltaGainAxial)
                    ch.driver.getParams().deltaGainAx = deltaGainAxial;
                if (!changePrm1 || !changePrm2) {
                    long idDrv = ch.driver.getDeviceId();
                    if (!changePrm1) {
                        idChangeDrv = idDrv;
                        changePrm1 = true;
                        return;
                    }
                    if (!changePrm2 && idChangeDrv != idDrv) {
                        changePrm2 = true;
                    }
                }
            }
        }
    }

}
