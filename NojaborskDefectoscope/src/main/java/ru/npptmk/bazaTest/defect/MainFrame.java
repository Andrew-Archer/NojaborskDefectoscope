package ru.npptmk.bazaTest.defect;

import com.ghgande.j2mod.modbus.ModbusException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import static java.lang.Math.toIntExact;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.derby.drda.NetworkServerControl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static ru.npptmk.bazaTest.defect.INPUTS_NAMES.*;
import ru.npptmk.bazaTest.defect.TubeType.ThickClasses;
import ru.npptmk.bazaTest.defect.Util.DbSchemeUpdater;
import ru.npptmk.bazaTest.defect.Util.FromClassPathSQLUpdater;
import ru.npptmk.bazaTest.defect.Util.ProgressDialog;
import ru.npptmk.bazaTest.defect.Util.jasper_report.JRDataSourceTablesList;
import ru.npptmk.bazaTest.defect.Util.jasper_report.JRDataSourceUSKLengthWiseResults;
import ru.npptmk.bazaTest.defect.Util.jasper_report.UNTK_500DataSourceGenerator;
import ru.npptmk.bazaTest.defect.Util.jasper_report.UNTK_500DataSourceGenerator.SENSOR_TYPES;
import ru.npptmk.bazaTest.defect.Util.jasper_report.XYChartBean;
import ru.npptmk.bazaTest.defect.Util.jasper_report.XYChartPointBean;
import ru.npptmk.bazaTest.defect.model.BasaTube;
import ru.npptmk.bazaTest.defect.model.Customer;
import ru.npptmk.bazaTest.defect.model.Operator;
import ru.npptmk.bazaTest.defect.view.Dialog_RestrictedAccess;
import ru.npptmk.bazaTest.defect.view.Dialog_changePass;
import ru.npptmk.commonObjects.GeneralUDPDevice;
import ru.npptmk.devices.USKUdp.DeviceUSKUdp;
import ru.npptmk.devices.USKUdp.DeviceUSKUdpChanDef;
import ru.npptmk.devices.USKUdp.DeviceUSKUdpParam;
import ru.npptmk.devices.USKUdp.DeviceUSKUdpParamPanel;
import ru.npptmk.devices.USKUdp.DeviceUSKUdpParams;
import ru.npptmk.devices.md8Udp.DeviceMD8Udp;
import ru.npptmk.devices.md8Udp.DeviceMD8UdpParamsPanel;
import ru.npptmk.devices.md8Udp.ParamsMD8Udp;
import ru.npptmk.guiObjects.IScanDataProvider;
import ru.npptmk.guiObjects.ITubeDataProvider;
import ru.npptmk.guiObjects.OvalIcon;
import ru.npptmk.guiObjects.PanelForGraphics;
import ru.npptmk.plcinterfaces.s7_1200.S7_1200;
import ru.npptmk.plcinterfaces.s7_1200.TagDef;
import ru.npptmk.sortoscope.main.NoMeasurementsReadyException;
import ru.npptmk.sortoscope.main.Sortoscope4Driver;
import ru.npptmk.sortoscope.main.SortoscopeDriver;
import ru.npptmk.sortoscope.model.DurabilityGroups;
import ru.npptmk.transpmanager.Device;
import ru.npptmk.transpmanager.EventEnt;
import ru.npptmk.transpmanager.Roller1Tube;
import ru.npptmk.transpmanager.TranspManager;

/**
 * Главный класс приложения АРМ дефектоскописта
 *
 *
 * @author MalginAS
 */
public class MainFrame extends javax.swing.JFrame implements ITubeDataProvider,
        TubesCounter.TubesCounterUpdatedListener {

    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
    private static final Font SEGOE_UI_18 = new Font("Segoe UI", Font.PLAIN, 18);

    private static final int PIPE_POSITION_COLUMN = 5;
    private static final int PIPE_STATE_COLUMN = 3;

    private JasperPrint jasperPrint;
    /**
     * Последние получение из архива результаты. Необходимы для формирования
     * отчета.
     */
    private Dialog_RestrictedAccess restrictedAccessDialog;
    private List<BasaTube> archResults;
    /**
     * Время, начиная с которого необходима искать данные в архиве. Необходимо
     * для формирования отчета, так как данные в полях визуального интерфейса
     * могут изменить, а данные заново не запросить.
     */
    private Date reportStartTime;
    /**
     * Время, на котором необходимо закончить поиск данных в архиве. Необходимо
     * для формирования отчета, так как данные в полях визуального интерфейса
     * могут изменить, а данные заново не запросить.
     */
    private Date reportFinishTime;
    private boolean previousLoopButtonSate = false;

    /**
     * Переменная для сохранения результатов сортоскопии.
     */
    DurabilityGroups sortoscopeResult = null;

    /**
     * Список режимов работы установки находится {@link  Modes}.
     */
    private int mode;
    private boolean uskSettingsIsEnabled;
    private boolean mdSettingsIsEnabled;

    /**
     * Список этапов техпроцесса находится в классе {@link States}.
     */
    private int state;
    private int coord;       // Текущая координата трубы.

    // Данные для драйвера транспортной системы.
    public DeviceMD8Udp blockMD;
    private ShiftManager shiftManager;
    public final PanelForGraphics grPnlMD;
    public Dialog_ArchGraphs jDialog_ArchiveGraphs;
    private NetworkServerControl server;
    /**
     * Счетчик бракованных и годных труб.
     */
    private TubesCounter tubesCounter;
    private final ResourceBundle gui_text;
    public DeviceUSKUdp blockUSK1;
    public DeviceUSKUdp blockUSK2;
    private final PanelForGraphics grPnlUS;
    private final PanelForGraphics grPnlAll;
    private final DeviceMD8UdpParamsPanel prmMDEditPnl;
    private DeviceUSKUdpParamPanel prmUSEditPnl;
    public BazaPanelFactory panFact;
    private int tubeLength;          // Фактическая длина трубы в мм.
    private final DialogCustomerSelection dialogCustomerSelection;
    private static final String PLC_IP = "localhost";

    private Dialog_createAdmin dialog_createAdmin;

    public S7_1200 plc;           // Интерфейс когтроллера.
    static final TagDef[] INPUTS = {
        new TagDef(SQ1_TUBE_ON_IN_ROLLGANG, 6),//Наличие трубы на рольганге перед установкой
        new TagDef(SQ2_FIX1_MUST_BE_DOWN, 7),//Подъем прижима 1.
        new TagDef(SQ3_CALIBRATION_SECTION_START, 10),//Начало калибровочного участка.
        new TagDef(SQ4_FIX1_UP, 11),//Исходное положение прижима1.
        new TagDef(SQ5_CALIBRATION_SECTION_END, 12),//Конец калибровочного участка (0 - координат)
        new TagDef(SQ6_BEFORE_ULTRA_SOUND_BATH, 13),//Наличие трубы перед УЗК
        new TagDef(SQ7_FIX2_UP, 14),//Исхоное положение прижима 2
        new TagDef(SQ8_FIX3_UP, 15),//Исходное положение прижима 3
        new TagDef(SQ9_HIGH_WATER_LEVEL, 30),//Высокий уровень воды
        new TagDef(SQ10_AFTER_SORTOSCOPE, 31),//Базовый торец трубы выхода из установки (по сходу)
        new TagDef(SQ11_TUBE_ON_OUT_ROLLGANG, 32),//Наличие трубы после установки
        new TagDef(SB4_EMERGENCY_STOP_NOT_PRESSED, 33),//Останов операции.
        new TagDef(SA1_MODE_AUTO_ENABLED, 34),//Режим работы 1 - работа 0 - наладка.
        new TagDef(SA2_SHIELD1_POWER_ON, 35),//Включение - выключение ШУ1
        new TagDef(SB3_STOP_OPERATION_NOT_PRESSED, 36),//Останов операции (нормально замкнутая)
        new TagDef(SB6_LOOP_PRESSED, 37),//Кнопка цикл
        new TagDef(SB5_PC_ON_PRESSED, 40),//Включение ПК
        new TagDef(SHIELD1_IS_ON, 41),//ШУ1 включен
        new TagDef(ROLLGANG_THERMAL_RELAY, 42),//Тепловое реле рольгангов
        new TagDef(PUMPS_THERMAL_RELAY, 43),//Тепловое реле насосов
        new TagDef(SQ15_IN_RELOADER_DOWN, 44),//Свходной перекладчик опущен
        new TagDef(SQ16_IN_RELOADER_UP, 45),//Входной перекладчик поднят
        new TagDef(SQ14_TUBE_ON_IN_RELOADER, 46),//Есть труба на входном перекладчике
        new TagDef(SQ12_OUT_RELOADER_DOWN, 47),//Выходной перекладчик опущен
        new TagDef(SQ13_OUT_RELOADER_UP, 50),//Выходной перекладчик поднят
        new TagDef(SQ17_TUBE_ON_OUT_TABLE, 51)//Труба на выходном стелаже (Вместимостью 1 труба)
    };

    static final TagDef[] OUTPUTS = {
        new TagDef("PC_Off", 0),
        new TagDef("Holder_1_Down", 01),
        new TagDef("Holder_2_Down", 02),
        new TagDef("Holder_3_Down", 03),
        new TagDef("Magnetic_Heads_Close", 04),
        new TagDef("USD_Carriage_Up", 05),
        new TagDef("Wetter_Close", 06),
        new TagDef("Magnetic_Coil_On", 07),
        new TagDef("Pumping_Out_On", 10),
        new TagDef("Carriage_Pump_on", 11),
        new TagDef("Pwr_1_On", 30),
        new TagDef("Demagnetization_Coil_On", 31),
        new TagDef("Rollgang_FWD", 32),
        new TagDef("Rollgang_REV", 33),
        new TagDef("HL4_PwrOn", 34),
        new TagDef("HL7_Loop", 37),
        new TagDef("Reloader_1_Up", 42),
        new TagDef("Dryer_On", 44),
        new TagDef("Reloader_2_Up", 43)
    };

    static final TagDef[] REGS = {
        new TagDef("Mode", 0),
        new TagDef("state", 2),
        new TagDef("XCoord", 4),
        new TagDef("TubeLen", 6),
        new TagDef("Err", 8),
        new TagDef("Cmd", 10),
        new TagDef("CalibrLen", 12),
        new TagDef("MDStart", 14),
        new TagDef("SecLirStart", 16),
        new TagDef("startUSK_Coordinate", 18),
        new TagDef("ThdLirStart", 20),
        new TagDef("MDEnd", 22),
        new TagDef("stopUSK_Coordinate", 24),
        new TagDef("TailLen", 26),
        new TagDef("FillTime", 28),
        new TagDef("IP_1", 30),
        new TagDef("IP_2", 32),
        new TagDef("IP_3", 34),
        new TagDef("IP_4", 36),
        new TagDef("Port", 38),
        new TagDef("IntSave", 40),
        new TagDef("OnWatt", 42),
        new TagDef("OffWatt", 44),
        new TagDef("OnDry", 46),
        new TagDef("OffDry", 48),
        new TagDef("Errors", 8)
    };

    static final TagDef[] DWREGS = {};
    private final TranspManager tmn = new TranspManager();
    private SortoscopeDriver sortoscopeDriver;
    private final Object accessBd = new Object();
    private int prevTabInd;
    private final MainFrame mFrm;
    private final GrafsUpdater grUpdaterMD;
    private final GrafsUpdater grUpdaterUSK1;
    private final GrafsUpdater grUpdaterUSK2;
    private final AScanUpdater updaterAScanUSK1;
    private final AScanUpdater updaterAScanUSK2;
    private final MDGrafsUpdater MDgrUpdater;
    private final USKGrafsUpdater USKgrUpdater;
    private TubeTypesDialog tubeTypesDialog;
    private ProgressDialog progressDialog;
    private boolean mayScan;
    private EntityManagerFactory emf;
    private Dialog_changePass newPass;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        //System.setProperty("log4j.configurationFile", "C:\\log4j2-test.xml");

        this.gui_text = ResourceBundle.getBundle("gui_text", new Locale("ru", "RU"));
        this.tubesCounter = new TubesCounter();
        this.sortoscopeDialog = new SortoscopeDialog(this, true);
        this.sortoscopeDriver = new Sortoscope4Driver();
        this.jasperPrint = new JasperPrint();

        System.setProperty("derby.system.home", ".\\db");
        System.setProperty("java.net.preferIPv4Stack", "true");

        this.progressDialog = new ProgressDialog(this);

        progressDialog.startProcessing(
                t("connectingToBDMessage"),
                new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                try {
                    server = new NetworkServerControl(InetAddress.getByName("0.0.0.0"), 1527);
                    server.start(null);
                } catch (Throwable ex) {
                    log.error("Can't start derby BD Server. %s", ex.getMessage());
                    JOptionPane.showMessageDialog(null, String.format(t("cantCreateDB"), ex.getMessage()), "Ошибка", ERROR_MESSAGE);
                    System.exit(1);
                }
                return 0;
            }

            @Override
            protected void done() {
                progressDialog.setVisible(false);
            }

        });

        try {
            emf = Persistence.createEntityManagerFactory("DefectPU");
        } catch (Exception ex) {
            log.error("Can't establish connection with derby BD.", ex);
            JOptionPane.showMessageDialog(null, String.format(t("cantConnectToDB"), ex.getMessage()), "Ошибка", ERROR_MESSAGE);
            System.exit(1);
        }
        DbSchemeUpdater dbSchemeUpdater = new FromClassPathSQLUpdater(emf);
        dbSchemeUpdater.update();
        //Запускаем менеджер смен.
        shiftManager = new ShiftManagerImpl(emf);

        if (!tmn.startManager(emf.createEntityManager())) {
            JOptionPane.showMessageDialog(this, tmn.getError().getComment());
        }

        if (!tmn.isDevPresent(Devicess.ID_R4)) {
            tmn.addNewDevice(Devicess.ID_R4, new UEParams(), "Рольганг Р4", Roller1Tube.class, 1);
        }

        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        if (prm == null) {
            prm = new UEParams();
            tmn.setParam(Devicess.ID_R4, prm);
        }
        try {
            blockMD = new DeviceMD8Udp(prm.connMDL, prm.connMDR, prm.getParamMD(), Devicess.ID_MD);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Настройки блока МД загружены с ошибкой.\n Будут использованы настройки по умолчанию.",
                    "Ошибка",
                    ERROR_MESSAGE);

            blockMD = new DeviceMD8Udp(
                    InetSocketAddress.createUnresolved("192.168.0.242", 32973),
                    InetSocketAddress.createUnresolved("192.168.0.239", 2201),
                    new ParamsMD8Udp(),
                    Devicess.ID_MD);
        }
        try {
            blockUSK1 = new DeviceUSKUdp(prm.connUSK1L, prm.connUSK1R, prm.getParamUSK1(), Devicess.ID_USK1);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Настройки блока USK1 загружены с ошибкой.\n Будут использованы настройки по умолчанию.",
                    "Ошибка",
                    ERROR_MESSAGE);
            blockUSK1 = new DeviceUSKUdp(
                    InetSocketAddress.createUnresolved("192.168.0.200", 24769),
                    InetSocketAddress.createUnresolved("192.168.0.239", 2202),
                    new DeviceUSKUdpParams("УЗК1"),
                    Devicess.ID_USK1);
        }
        try {
            blockUSK2 = new DeviceUSKUdp(prm.connUSK2L, prm.connUSK2R, prm.getParamUSK2(), Devicess.ID_USK2);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Настройки блока USK2 загружены с ошибкой.\n Будут использованы настройки по умолчанию.",
                    "Ошибка",
                    ERROR_MESSAGE);
            blockUSK2 = new DeviceUSKUdp(
                    InetSocketAddress.createUnresolved("192.168.0.201", 24769),
                    InetSocketAddress.createUnresolved("192.168.0.239", 2202),
                    new DeviceUSKUdpParams("УЗК2"),
                    Devicess.ID_USK2);
        }
        panFact = new BazaPanelFactory(blockMD, blockUSK1, blockUSK2);

        //Подкоючаемся к PLC.
        plc = new S7_1200(PLC_IP, INPUTS, OUTPUTS, REGS, DWREGS);
        try {
            plc.connect();
            if (plc == null) {
                log.error("Can't connect to PLC on IP [{}].", PLC_IP);
                System.exit(-1);
            }
        } catch (Exception ex) {
            log.error("Can't connect to PLC on IP [{}].", PLC_IP, ex);
            System.exit(-1);
        }
        mFrm = this;
        initComponents();

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        plc.setUpdateProc(REGS[1].getWordModbusAddr(), this::reactOnUpdatedState);
        plc.setUpdateProc(REGS[2].getWordModbusAddr(), this::reactOnCoordinateUpdated);
        plc.setUpdateProc(REGS[0].getWordModbusAddr(), this::reactOnModeUpdated);

        setTitle(t("windowTitle"));
        grPnlMD = new PanelForGraphics(panFact, prm.prGrfMd.get(prm.currMDGrSet));
        pnGrfsMD.add(grPnlMD);
        prmMDEditPnl = (DeviceMD8UdpParamsPanel) blockMD.getParamsPanel();
        pnNastr.add(prmMDEditPnl);
        grPnlMD.redrawPanels();

        grPnlUS = new PanelForGraphics(panFact, prm.prGrfUSK.get(prm.currUSGrSet));
        pnGrfsUSK.add(grPnlUS);
        DeviceUSKUdpChanDef[] defs = new DeviceUSKUdpChanDef[16];
        for (int i = 0; i < 8; i++) {
            defs[i] = new DeviceUSKUdpChanDef(t("block1ChannelName") + (i + 1), blockUSK1, i);
        }
        for (int i = 0; i < 8; i++) {
            defs[i + 8] = new DeviceUSKUdpChanDef(t("block2ChannelName") + (i + 1), blockUSK2, i);
        }
        progressDialog.startProcessing(
                t("preparingUltrasonicPropetiesPanel"),
                new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                prmUSEditPnl = new DeviceUSKUdpParamPanel(defs);
                progressDialog.setVisible(false);
                return 0;
            }
        });

        pnNastrUSK.add(prmUSEditPnl);
        grPnlUS.redrawPanels();

        grPnlAll = new PanelForGraphics(panFact, prm.prGrfTube.get(prm.currAllGrSet));
        pnGrfsAll.add(grPnlAll);
        grPnlAll.redrawPanels();
        MDgrUpdater = new MDGrafsUpdater(blockMD, grPnlAll);//обновление графиков на вкладке "труба"
        USKgrUpdater = new USKGrafsUpdater(blockUSK1, blockUSK2, grPnlAll);
        grUpdaterMD = new GrafsUpdater(blockMD, grPnlMD);//обновление самописцев магнитки на вкладке "УМД"
        grUpdaterUSK1 = new GrafsUpdater(blockUSK1, grPnlUS);//обновление самописцев УЗК1 на вкладке "УЗК"
        grUpdaterUSK2 = new GrafsUpdater(blockUSK2, grPnlUS);//обновление самописцев УЗК2 на вкладке "УЗК"
        updaterAScanUSK1 = new AScanUpdater(blockUSK1, prmUSEditPnl);//обновление аСкана УЗК1 на вкладке "УЗК"
        updaterAScanUSK2 = new AScanUpdater(blockUSK2, prmUSEditPnl);//обновление аСкана УЗК2 на вкладке "УЗК"
        fillTubeTable();

        //Слушатель для драйвера магнитки
        blockMD.addListenerNewPoint(() -> {
            EventQueue.invokeLater(grUpdaterMD);
        });

        blockUSK1.addListenerNewPoint(() -> {
            if (prmUSEditPnl.drv.getDeviceId() == blockUSK1.getDeviceId()) {
                EventQueue.invokeLater(updaterAScanUSK1);
            }
            if (blockUSK1.isNewPointReady) {
                EventQueue.invokeLater(grUpdaterUSK1);
                blockUSK1.isNewPointReady = false;
            }
        });

        blockUSK2.addListenerNewPoint(() -> {
            if (prmUSEditPnl.drv.getDeviceId() == blockUSK2.getDeviceId()) {
                EventQueue.invokeLater(updaterAScanUSK2);
            }
            if (blockUSK2.isNewPointReady) {
                EventQueue.invokeLater(grUpdaterUSK2);
                blockUSK2.isNewPointReady = false;
            }
        });

        startDrivers();

        try {
            plc.setShortRegister("CalibrLen", prm.calibrLen);
            plc.setShortRegister("MDStart", prm.MDStart);
            plc.setShortRegister("SecLirStart", prm.secLirStart);
            plc.setShortRegister("startUSK_Coordinate", prm.USDStart);
            plc.setShortRegister("ThdLirStart", prm.thdLirStart);
            plc.setShortRegister("MDEnd", prm.MDEnd);
            plc.setShortRegister("stopUSK_Coordinate", prm.USDEnd);
            plc.setShortRegister("TailLen", prm.tailLen);
            plc.setShortRegister("FillTime", prm.fillTime);
            plc.setShortRegister("OnWatt", prm.onWatter);
            plc.setShortRegister("OffWatt", prm.offWatter);
            plc.setShortRegister("OnDry", prm.onDry);
            plc.setShortRegister("OffDry", prm.offDry);
            InetAddress adr = null;
            try {
                adr = InetAddress.getByName(prm.conTrLoc);
            } catch (UnknownHostException ex) {
                log.error("Can't get operator PC IP", ex);
            }
            if (adr != null) {
                plc.setShortRegister("IP_1", adr.getAddress()[0]);
                plc.setShortRegister("IP_2", adr.getAddress()[1]);
                plc.setShortRegister("IP_3", adr.getAddress()[2]);
                plc.setShortRegister("IP_4", adr.getAddress()[3]);
                plc.setShortRegister("Port", (short) 502);
                plc.setShortRegister("IntSave", prm.intSave);
            }
            plc.writeData();
            plc.readRegisters("Mode", 1);
            mode = plc.getShortRegister("Mode");
        } catch (Exception ex) {
            log.error("PLC communication error", ex);
            System.exit(-1);
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (prmUSEditPnl.isChangeParams()) {
                    int rc = JOptionPane.showConfirmDialog(null, t("saveUSKChangesMessage"),
                            t("saveUSKChangesMessageDialogCaption1"), JOptionPane.YES_NO_OPTION);
                    if (rc == JOptionPane.YES_OPTION) {
                        saveUSKParams(blockUSK1);
                        saveUSKParams(blockUSK2);
                    }
                    if (rc == JOptionPane.NO_OPTION) {
                        UEParams pr = (UEParams) tmn.getParam(Devicess.ID_R4);
                        blockUSK1.setParams(pr.currentTubeType.getParamsUSK1());
                        blockUSK2.setParams(pr.currentTubeType.getParamsUSK2());
                    }
                }
                if (blockMD.getChangeFlag()) {
                    int rc = JOptionPane.showConfirmDialog(null, t("saveMDChangesMessage"),
                            t("saveMDChangesMessageDialogCaption"), JOptionPane.YES_NO_OPTION);
                    if (rc == JOptionPane.YES_OPTION) {
                        saveMDParams();
                    }
                }
                if (server != null) {
                    try {
                        server.shutdown();
                    } catch (Exception ex) {
                    }
                }
                super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
            }
        });
        jTabbedPane1.getModel().addChangeListener((ChangeEvent e) -> {
            switch (jTabbedPane1.getSelectedIndex()) {
                case 2:
                    blockUSK1.startScan(0);
                    blockUSK2.startScan(0);
                    break;
            }
            if (prevTabInd == 2 && prmUSEditPnl.isChangeParams()) {
                int rc = JOptionPane.showConfirmDialog(null, t("saveUSKChangesMessage"),
                        t("saveUSKChangesMessageDialogCaption2"), JOptionPane.YES_NO_OPTION);
                if (rc == JOptionPane.YES_OPTION) {
                    saveUSKParams(blockUSK1);
                    saveUSKParams(blockUSK2);
                }
                if (rc == JOptionPane.NO_OPTION) {
                    UEParams pr = (UEParams) tmn.getParam(Devicess.ID_R4);
                    blockUSK1.setParams(pr.currentTubeType.getParamsUSK1());
                    blockUSK2.setParams(pr.currentTubeType.getParamsUSK2());

                }
                prmUSEditPnl.resetChanges();
            }
            if (prevTabInd == 1 && blockMD.getChangeFlag()) {
                int rc = JOptionPane.showConfirmDialog(null, "gui_text.getString(saveMDChangesMessage)",
                        t("saveMDChangesMessageDialogCaption"), JOptionPane.YES_NO_OPTION);
                if (rc == JOptionPane.YES_OPTION) {
                    saveMDParams();
                }
            }
            prevTabInd = jTabbedPane1.getSelectedIndex();
        });

        //Опрашиваем контроллер для определения состоняния OUTPUTS кнопок 
        //в наладочном режиме.
        Timer t = new Timer(500, null);
        t.addActionListener((evt) -> {
            try {
                plc.readInputs();
                plc.readOutputs();
            } catch (ModbusException ex) {
                log.error("PLC connection error", ex);
            }
            MainFrame.this.updateTunePanel(panel_Tunning);
        });
        t.start();

        //Инициализируем список результатов
        updateTubeOnDeviceResults(tmn
                .getDetail(Devicess.ID_R4, Device.DEFAULT_VALUE));
        //Оработка события изменения списка труб проверенных за смену
        ((DefaultTableModel) table_Shift_Tubes.getModel()).addTableModelListener((evt) -> {
            //Обновляем список результатов
            updateTubeOnDeviceResults(tmn
                    .getDetail(Devicess.ID_R4, Device.DEFAULT_VALUE));
        });

        updateCustomersList();
        shiftManager.addListener((Shift shift) -> {
            switch (shiftManager.getState()) {
                case SHIFT_IS_RUNNING:
                case SHIFT_IS_NOT_STARTED:
                    label_OperatorValue.setText(shift.getOperator().toString());
                    break;
                default:
                    label_OperatorValue.setText("");
            }
        });
        dialogCustomerSelection = new DialogCustomerSelection(mFrm, true, emf);
        dialogCustomerSelection.setLocationRelativeTo(MainFrame.this);
        customInitComponents();

        jTable_ArchiveResults.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (jDialog_ArchiveGraphs == null) {
                    jDialog_ArchiveGraphs = new Dialog_ArchGraphs(mFrm, false);
                }
                if (jTable_ArchiveResults.getSelectedRow() != -1) {
                    log.debug("Pipe for creating results preview: {}", archResults.get(jTable_ArchiveResults.getSelectedRow()).getId());
                    BasaTube selectedPipe = archResults.get(jTable_ArchiveResults.getSelectedRow());
                    jDialog_ArchiveGraphs.updateGraphs(selectedPipe,
                            ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes.get(toIntExact(selectedPipe.getTypeID()) - 1));
                } else {
                    jDialog_ArchiveGraphs.setVisible(false);
                }
            }
        });
    }

    /**
     * Заполняет список результатов результатами трубы находящейся на установке.
     *
     * @param tubeOnDeviceID ID трубы которая находится на установке. можно
     * получить от транспортного менеджера: <tt>tmn.getDetail(DEVICES.ID_R4,
     * Device.DEFAULT_VALUE);</tt>
     */
    public void updateTubeOnDeviceResults(long tubeOnDeviceID) {
        BasaTube tubeOnDevice = null;
        //Если на установке есть труба, 
        if (tubeOnDeviceID != 0) {
            EntityManager em = emf.createEntityManager();
            try {
                //то берем эту трубу из базы.
                tubeOnDevice = em.find(BasaTube.class, tubeOnDeviceID);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (em != null) {
                    em.close();
                }
            }
            //Если в базе есть труба соответствующая трубе на установке
            if (tubeOnDevice != null) {
                List<BazaDefectResults> defectDetectionResults = tubeOnDevice
                        .getTubeResults();
                //Если у трубы есть результаты контроля дефектов.
                if (defectDetectionResults != null
                        && !defectDetectionResults.isEmpty()) {
                    //Очищаем списко результатов
                    combobox_TubeOnDeviceResults.removeAllItems();
                    //Заполняем список результатов.
                    combobox_TubeOnDeviceResults.insertItemAt(t("selectDefectDetectionResults"), 0);
                    combobox_TubeOnDeviceResults.setSelectedIndex(0);
                    for (int i = defectDetectionResults.size(); i > 0; i--) {
                        combobox_TubeOnDeviceResults.insertItemAt(defectDetectionResults.get(i - 1), combobox_TubeOnDeviceResults.getItemCount());
                    }
                }
            }
        }
    }

    private SortoscopeDialog sortoscopeDialog;

    private void createGraphPerPageReport() {
        //Если есть данные для вывода отчета
        if (archResults != null && !archResults.isEmpty()) {
            //Имя файла скомпилированного отчёта, готового для заполнения.
            URL compiledReport = getClass().getResource("Resource/reports/AllGraphs.jasper");

            if (compiledReport == null) {
                log.warn("Шаблон отчета не найден. \nНеправильно откомпилировали программу.");
                JOptionPane.showMessageDialog(
                        null,
                        "Шаблон отчета не найден. \nНеправильно откомпилировали программу.",
                        "Ошибка",
                        ERROR_MESSAGE);
                return;
            }
            //Получаем индекс выделенной строчки
            int selectedRowIndex = jTable_ArchiveResults.getSelectedRow();
            if (!(selectedRowIndex < 0) && !(selectedRowIndex >= archResults.size())) {
                //Создаем генератор карты параметров для рисования графиков.
                UNTK_500DataSourceGenerator reportParamsGenerator = new UNTK_500DataSourceGenerator();

                //Получаем вы деленную трубы
                BasaTube selectedTube = archResults.get(selectedRowIndex);

                //Получаем тип трубы
                TubeType tubeType = ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes.get(toIntExact(selectedTube.getTypeID()) - 1);

                //Получаем последние результаты выбранной трубы которые необходимо вывести на график
                BazaDefectResults results = selectedTube.getTubeResults().get(0);

                //Заполняем координаты положения дефектов.
                for (int j = 0; j < 3; j++) {
                    String nameOfDefect = null;
                    switch (j) {
                        case 0:
                            nameOfDefect = "Магнитка";
                            break;
                        case 1:
                            nameOfDefect = "УЗК";
                            break;
                        case 2:
                            nameOfDefect = "Толщиномер";
                            break;
                    }

                    float[] defectsArray = results.tbRes.getDefects(j);
                    for (int i = 0; i < defectsArray.length; i++) {
                        reportParamsGenerator.addDefectPoint(
                                nameOfDefect,
                                j + 1,
                                (double) defectsArray[i]
                        );
                    }
                }

                //Добавляем значения порогов
                //TODO здесь получаем NullPointerException
                int[] magneticBorders = tubeType.getParamsMD().porog;
                for (int i = 0; i < magneticBorders.length; i++) {
                    reportParamsGenerator.setSignalBorder(
                            t("chanelName") + (i + 1),
                            SENSOR_TYPES.MAGNETIC,
                            (double) magneticBorders[i]);
                }
                //Добавляем значения сигналов Мд
                for (int i = 0; i < 8; i++) {
                    float[] signalsMDx = new float[results.mdRes.getGraficLength(i)];
                    float[] signalsMdy = new float[results.mdRes.getGraficLength(i)];
                    results.mdRes.getGrafic(i, signalsMDx, signalsMdy);
                    for (int j = 0; j < results.mdRes.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(
                                t("chanelName") + (i + 1),
                                SENSOR_TYPES.MAGNETIC,
                                (double) signalsMdy[j],
                                (double) signalsMDx[j]);
                    }
                }

                //Добавляем пороги для толщиномеров
                BazaDefectResults tubeResults = archResults.get(selectedRowIndex).getTubeResults().get(0);
                float[] thickBorders = tubeResults.tbRes.getTubeThicks();
                for (int i = 0; i < thickBorders.length; i++) {
                    //Если прого не равен 0
                    if (thickBorders[i] != 0) {
                        reportParamsGenerator.setSignalBorder(
                                t("chanelName") + (i + 1),
                                SENSOR_TYPES.THICKNESS,
                                (double) thickBorders[i]
                        );
                    }
                }
                reportParamsGenerator.setSignalBorder(
                        "Потеря акустического сигнала",
                        SENSOR_TYPES.THICKNESS,
                        3.0
                );

                //Добавляем значения толщиномеров
                BazaUSDResult thickRes = archResults.get(selectedRowIndex).getTubeResults().get(0).usk2Res;
                for (int i = 0; i < 4; i++) {
                    float[] y = new float[thickRes.getThickGrafLength(i)];
                    float[] x = new float[thickRes.getThickGrafLength(i)];
                    thickRes.getThickGrafic(i + 4, x, y);
                    for (int j = 0; j < thickRes.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(t("chanelName") + (i + 1), SENSOR_TYPES.THICKNESS, (double) y[j], (double) x[j]);
                    }
                }

                //Добавляем проги для продольников
                for (int i = 0; i < 6; i++) {
                    reportParamsGenerator.setSignalBorder(
                            t("chanelName") + (i + 1),
                            SENSOR_TYPES.LENGTHWISE,
                            (double) tubeResults.usk1Res.getThreshold(i)
                    );
                }

                //Добавляем значения продольников
                BazaUSDResult lengWiseRes = tubeResults.usk1Res;
                for (int i = 0; i < 6; i++) {
                    float[] y = new float[lengWiseRes.getGraficLength(i)];
                    float[] x = new float[lengWiseRes.getGraficLength(i)];
                    lengWiseRes.getGrafic(i, x, y);
                    for (int j = 0; j < lengWiseRes.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(t("chanelName") + (i + 1), SENSOR_TYPES.LENGTHWISE, (double) y[j], (double) x[j]);
                    }
                }

                //Добавляем попроги для поперечников
                for (int i = 0; i < 4; i++) {
                    reportParamsGenerator.setSignalBorder(
                            t("chanelName") + (i + 1),
                            SENSOR_TYPES.TRANSVERSAL,
                            (double) tubeResults.usk2Res.getThreshold(i)
                    );
                }
                for (int i = 6; i < 8; i++) {
                    reportParamsGenerator.setSignalBorder(
                            t("chanelName") + (i + 1),
                            SENSOR_TYPES.TRANSVERSAL,
                            (double) tubeResults.usk1Res.getThreshold(i)
                    );
                }

                //Добавляем сигналы от поперечников
                BazaUSDResult crossWiseRes1 = tubeResults.usk2Res;
                for (int i = 0; i < 4; i++) {
                    float[] y = new float[crossWiseRes1.getGraficLength(i)];
                    float[] x = new float[crossWiseRes1.getGraficLength(i)];
                    crossWiseRes1.getGrafic(i, x, y);
                    for (int j = 0; j < crossWiseRes1.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(t("chanelName") + (i + 1), SENSOR_TYPES.TRANSVERSAL, (double) y[j], (double) x[j]);
                    }
                }
                BazaUSDResult crossWiseRes2 = tubeResults.usk1Res;
                for (int i = 6; i < 8; i++) {
                    float[] y = new float[crossWiseRes2.getGraficLength(i)];
                    float[] x = new float[crossWiseRes2.getGraficLength(i)];
                    crossWiseRes2.getGrafic(i, x, y);
                    for (int j = 0; j < crossWiseRes2.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(t("chanelName") + (i + 1), SENSOR_TYPES.TRANSVERSAL, (double) y[j], (double) x[j]);
                    }
                }

                //Указываем номер трубы 
                reportParamsGenerator.setTubeNumber(selectedTube.getId());
                reportParamsGenerator
                        .setDefectDetectionTime(LocalDateTime.ofInstant(
                                tubeResults.dateRes.toInstant(),
                                ZoneId.systemDefault())
                        );
                try (InputStream is = compiledReport.openStream();) {
                    jasperPrint = JasperFillManager.fillReport(is, reportParamsGenerator.generate(), reportParamsGenerator.generateDataSource());
                    JRBeanCollectionDataSource debugOutput = reportParamsGenerator.generateDataSource();
                    Collection<XYChartBean> debugCollection = (Collection<XYChartBean>) debugOutput.getData();
                    //Debug output
                    debugCollection.forEach((xYChartBean) -> {
                        log.debug("Chart name is:{}", xYChartBean.getChartName());
                        xYChartBean.getGraphData().getData().forEach((xYChartPointBean) -> {
                            log.debug("Series:{} X:{}, Y:{}",
                                    ((ru.npptmk.bazaTest.defect.Util.jasper_report.XYChartPointBean) xYChartPointBean).getSeries(),
                                    ((ru.npptmk.bazaTest.defect.Util.jasper_report.XYChartPointBean) xYChartPointBean).getxValue(),
                                    ((ru.npptmk.bazaTest.defect.Util.jasper_report.XYChartPointBean) xYChartPointBean).getyValue());
                        });
                    });
                    JRViewer jasperViewer = new JRViewer(jasperPrint);
                    jasperViewer.setZoomRatio(0.5f);

                    JDialog reportDialog = new JDialog();
                    reportDialog.add(jasperViewer);
                    reportDialog.setBounds(0, 0, 800, 600);
                    reportDialog.setLocationRelativeTo(MainFrame.this);
                    reportDialog.setVisible(true);
                } catch (IOException ex) {
                    log.error("Report access error: ", ex);
                    JOptionPane.showMessageDialog(null, "Ошибка доступа к отчету: " + ex.getLocalizedMessage());
                } catch (JRException ex) {
                    log.error("Fill report error: ", ex);
                    JOptionPane.showMessageDialog(null, "Ошибка заполнения отчета: " + ex.getLocalizedMessage());
                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Выберите трубу по которой хотите получить детальный отчет.",
                        "Уведомление",
                        INFORMATION_MESSAGE
                );
            }
        } else {//Нет данных для вывода отчета
            log.warn("No data for report. "
                    + "Check that results table is filled. "
                    + "If the are no results, check correctness of "
                    + "filter parameters.");
            JOptionPane.showMessageDialog(
                    null,
                    "Нет данных для вывода отчета. \n "
                    + "Убедитесь, что таблица с результатами запонена. \n"
                    + "Если результаты отсутствуют, проверьте правильность "
                    + "задания критериев поиска в базе.",
                    "Уведомление",
                    INFORMATION_MESSAGE);
        }
    }

    private void createGraphsOnOnePage() {
        log.debug("Start printing the report...");
        //Если есть данные для вывода отчета
        if (archResults != null && !archResults.isEmpty()) {
            //Имя файла скомпилированного отчёта, готового для заполнения.
            URL compiledReport = getClass().getResource("Resource/reports/AllGraphsOnOnePage.jasper");

            if (compiledReport == null) {
                log.error("Report template [{}] has not been found...", "Resource/reports/AllGraphsOnOnePage.jasper");
                JOptionPane.showMessageDialog(
                        null,
                        "Шаблон отчета не найден. \nНеправильно откомпилировали программу.",
                        "Ошибка",
                        ERROR_MESSAGE);
                return;
            }
            log.debug("Number of pipes collected for report {}", archResults.size());
            log.debug("Report file has been found", compiledReport.toString());
            //Получаем индекс выделенной строчки
            int selectedRowIndex = jTable_ArchiveResults.getSelectedRow();
            log.debug("Number of selected pipe row {}", selectedRowIndex);
            if (!(selectedRowIndex < 0) && !(selectedRowIndex >= archResults.size())) {
                //Создаем генератор карты параметров для рисования графиков.
                UNTK_500DataSourceGenerator reportParamsGenerator = new UNTK_500DataSourceGenerator();

                //Получаем выделенную трубы
                BasaTube selectedTube = archResults.get(selectedRowIndex);
                log.debug("Id of pipe selected for report {}", selectedTube.getId());
                //Получаем тип трубы
                TubeType tubeType = ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes.get(toIntExact(selectedTube.getTypeID()) - 1);
                log.debug("Pipe type for report {}", tubeType.getName());

                //Получаем последние результаты выбранной трубы которые необходимо вывести на график
                BazaDefectResults results = selectedTube.getTubeResults().get(0);
                log.debug("Defect detection resluts have been collected.");
                //Заполняем координаты положения дефектов.
                for (int j = 0; j < 3; j++) {
                    String nameOfDefect = null;
                    switch (j) {
                        case 0:
                            nameOfDefect = "Магнитка";
                            break;
                        case 1:
                            nameOfDefect = "УЗК";
                            break;
                        case 2:
                            nameOfDefect = "Толщиномер";
                            break;
                    }

                    float[] defectsArray = results.tbRes.getDefects(j);
                    for (int i = 0; i < defectsArray.length; i++) {
                        reportParamsGenerator.addDefectPoint(
                                nameOfDefect,
                                j + 1,
                                (double) defectsArray[i]
                        );
                    }
                }

                //Добавляем значения порогов
                int[] magneticBorders = tubeType.getParamsMD().porog;
                for (int i = 0; i < magneticBorders.length; i++) {
                    reportParamsGenerator.setSignalBorder(
                            t("chanelName") + (i + 1),
                            SENSOR_TYPES.MAGNETIC,
                            (double) magneticBorders[i]);
                }
                //Добавляем значения сигналов Мд
                for (int i = 0; i < 8; i++) {
                    float[] signalsMDx = new float[results.mdRes.getGraficLength(i)];
                    float[] signalsMdy = new float[results.mdRes.getGraficLength(i)];
                    results.mdRes.getGrafic(i, signalsMDx, signalsMdy);
                    for (int j = 0; j < results.mdRes.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(
                                t("chanelName") + (i + 1),
                                SENSOR_TYPES.MAGNETIC,
                                (double) signalsMdy[j],
                                (double) signalsMDx[j]);
                    }
                }

                //Добавляем пороги для толщиномеров
                BazaDefectResults tubeResults = results;//archResults.get(selectedRowIndex).getTubeResults().get(0);
                float[] thickBorders = tubeResults.tbRes.getTubeThicks();
                for (int i = 0; i < thickBorders.length; i++) {
                    //Если прого не равен 0
                    if (thickBorders[i] != 0) {
                        reportParamsGenerator.setSignalBorder(
                                Integer.toString(i + 1),
                                SENSOR_TYPES.THICKNESS,
                                (double) thickBorders[i]
                        );
                    }
                }
                reportParamsGenerator.setSignalBorder(
                        "Потеря акустического сигнала",
                        SENSOR_TYPES.THICKNESS,
                        3.0
                );

                //Добавляем значения толщиномеров
                BazaUSDResult thickRes = tubeResults.usk2Res;
                for (int i = 0; i < 4; i++) {
                    float[] y = new float[thickRes.getThickGrafLength(i)];
                    float[] x = new float[thickRes.getThickGrafLength(i)];
                    thickRes.getThickGrafic(i + 4, x, y);
                    for (int j = 0; j < thickRes.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(t("chanelName") + (i + 1), SENSOR_TYPES.THICKNESS, (double) y[j], (double) x[j]);
                    }
                }

                //Добавляем проги для продольников
                for (int i = 0; i < 6; i++) {
                    reportParamsGenerator.setSignalBorder(
                            t("chanelName") + (i + 1),
                            SENSOR_TYPES.LENGTHWISE,
                            (double) tubeResults.usk1Res.getThreshold(mode)
                    );
                }

                //Добавляем значения продольников
                BazaUSDResult lengWiseRes = tubeResults.usk1Res;
                for (int i = 0; i < 6; i++) {
                    float[] y = new float[lengWiseRes.getGraficLength(i)];
                    float[] x = new float[lengWiseRes.getGraficLength(i)];
                    lengWiseRes.getGrafic(i, x, y);
                    for (int j = 0; j < lengWiseRes.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(t("chanelName") + (i + 1), SENSOR_TYPES.LENGTHWISE, (double) y[j], (double) x[j]);
                    }
                }

                //Добавляем попроги для поперечников
                for (int i = 0; i < 4; i++) {
                    reportParamsGenerator.setSignalBorder(
                            t("chanelName") + (i + 1),
                            SENSOR_TYPES.TRANSVERSAL,
                            (double) tubeResults.usk2Res.getThreshold(i)
                    );
                }
                for (int i = 6; i < 8; i++) {
                    reportParamsGenerator.setSignalBorder(
                            t("chanelName") + (i + 1),
                            SENSOR_TYPES.TRANSVERSAL,
                            (double) tubeResults.usk1Res.getThreshold(i)
                    );
                }

                //Добавляем сигналы от поперечников
                BazaUSDResult crossWiseRes2 = tubeResults.usk2Res;
                for (int i = 0; i < 4; i++) {
                    float[] y = new float[crossWiseRes2.getGraficLength(i)];
                    float[] x = new float[crossWiseRes2.getGraficLength(i)];
                    crossWiseRes2.getGrafic(i, x, y);
                    for (int j = 0; j < crossWiseRes2.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(t("chanelName") + (i + 1), SENSOR_TYPES.TRANSVERSAL, (double) y[j], (double) x[j]);
                    }
                }
                BazaUSDResult crossWiseRes1 = tubeResults.usk1Res;
                for (int i = 6; i < 8; i++) {
                    float[] y = new float[crossWiseRes1.getGraficLength(i)];
                    float[] x = new float[crossWiseRes1.getGraficLength(i)];
                    crossWiseRes1.getGrafic(i, x, y);
                    for (int j = 0; j < crossWiseRes1.getGraficLength(i); j++) {
                        reportParamsGenerator.addSignalPoint(t("chanelName") + (i + 1), SENSOR_TYPES.TRANSVERSAL, (double) y[j], (double) x[j]);
                    }
                }

                //Указываем номер трубы 
                reportParamsGenerator.setTubeNumber(selectedTube.getId());
                reportParamsGenerator
                        .setDefectDetectionTime(LocalDateTime.ofInstant(
                                tubeResults.dateRes.toInstant(),
                                ZoneId.systemDefault())
                        );
                try (InputStream is = compiledReport.openStream();) {
                    jasperPrint = JasperFillManager.fillReport(is, reportParamsGenerator.generate(), reportParamsGenerator.generateDataSource());
                    JRBeanCollectionDataSource debugOutput = (JRBeanCollectionDataSource) reportParamsGenerator.generate().get("LENGTHWISE");
                    Collection<XYChartPointBean> debugCollection = (Collection<XYChartPointBean>) debugOutput.getData();
                    //Debug output
                    debugCollection.forEach((xYChartPointBean) -> {

                        log.debug(
                                "Series: {} X:{}, Y:{}",
                                ((XYChartPointBean) xYChartPointBean).getSeries(),
                                ((XYChartPointBean) xYChartPointBean).getxValue(),
                                ((XYChartPointBean) xYChartPointBean).getyValue());

                    });
                    JRViewer jasperViewer = new JRViewer(jasperPrint);
                    jasperViewer.setZoomRatio(0.5f);

                    JDialog reportDialog = new JDialog();
                    reportDialog.add(jasperViewer);
                    reportDialog.setBounds(0, 0, 800, 600);
                    reportDialog.setLocationRelativeTo(MainFrame.this);
                    reportDialog.setVisible(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка доступа к отчету: " + ex.getLocalizedMessage());
                } catch (JRException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка заполнения отчета: " + ex.getLocalizedMessage());
                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Выберите трубу по которой хотите получить детальный отчет.",
                        "Уведомление",
                        INFORMATION_MESSAGE
                );
            }
        } else {//Нет данных для вывода отчета
            JOptionPane.showMessageDialog(
                    null,
                    "Нет данных для вывода отчета. \n "
                    + "Убедитесь, что таблица с результатами запонена. \n"
                    + "Если результаты отсутствуют, проверьте правильность "
                    + "задания критериев поиска в базе.",
                    "Уведомление",
                    INFORMATION_MESSAGE);
        }
    }

    /**
     * Обработчик обновления технологических этапов дефектоскопии.
     */
    private void reactOnUpdatedState() {
        try {
            //Обновляем Этап.
            plc.readRegisters("state", 1);
            //Обновляем длину трубы
            plc.readRegisters("TubeLen", 1);
            //Получаем номер этапа.
            state = plc.getShortRegister("state");
            //Этот логгер пишет в лог, только если отладка включена.

            switch (state) {
                case States.STATE_0: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_0_Name"));

                    //На этом этапе надо сбросить все, что необходимо
                    //Сбрасываем значение координаты
                    //Сбрасываем значение длины
                    //Сбрасываем графики
                    break;
                }
                case States.STATE_1: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_1_Name"));

                    //На этом этапе ничего не делаем,
                    //так как не понятно в каком состоянии линия
                    break;
                }
                case States.STATE_2: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_2_Name"));

                    //На этом этапе ничего не делаем,
                    //так как не понятно в каком состоянии линия
                    break;
                }
                case States.STATE_3: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_3_Name"));

                    //Получаем данные от транспортного менеджера о наличии трубы
                    long tubeOnTheDeviceID = tmn.getDetail(Devicess.ID_R4, Device.DEFAULT_VALUE);

                    if (tubeOnTheDeviceID == 0) {//Если по учету на установке ничего нет
                        sendCmdToPLC(Commands.NOTHING_IS_ON_THE_DEVICE);
                    } else {//Если по учету труба на установке есть
                        /*Вообще, по идее, здесь должна быть проверка,
                        //на то, что находится на установке, труба или образец.
                        //Но это долго, так как надо получать трубу из базы.
                        //Поэтому идеологически правльный код, возможно,
                        //придется закомментировать.*/
                        BasaTube tubeOnTheDevice;
                        EntityManager em = emf.createEntityManager();
                        try {
                            tubeOnTheDevice = em.find(BasaTube.class, tubeOnTheDeviceID);
                            //Если тип трубы образец
                            if (tubeOnTheDevice.isSample()) {
                                sendCmdToPLC(Commands.SAMPLE_IS_ON_THE_DEVICE);
                            } else {//На установке не образец
                                sendCmdToPLC(Commands.TUBE_IS_ON_THE_DEVICE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            if (em != null) {
                                em.close();
                            }
                        }
                    }
                    break;
                }
                case States.STATE_4: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_4_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_5: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_5_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_6: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_6_Name"));

                    //На этом этапе по учету транспортного менеджера
                    //трубы одназначно нет, так что ничего тут такого не
                    //проверяем.
                    //Регистрируем новую трубу
                    addTube(
                            Devicess.ID_R4, //ID Установки
                            (Customer) combobox_CustomerSelection.getSelectedItem(), //Заказчик
                            shiftManager.getShift(),//Смена
                            Device.DEFAULT_VALUE, //Позоция на установке
                            ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.getId(),//ID Типа трубы
                            false,//Является ли труба образцом
                            "Новая труба загружена на установку" //Комментарий
                    );

                    //Отправляем команду о том, что новая труба зарегистрирована
                    sendCmdToPLC(Commands.NEW_TUBE_IS_REGISTRED);

                    break;
                }
                case States.STATE_7: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_7_Name"));

                    //Сбрасываем координату
                    coord = 0;
                    //Сбрасываем длину
                    tubeLength = 0;
                    //Сбрасываем графики
                    blockMD.resetBufs();
                    blockUSK1.resetBufs();
                    blockUSK2.resetBufs();
                    grPnlAll.updateGrafs(mFrm);
                    break;
                }
                case States.STATE_8: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_8_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_9: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_9_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_10: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_10_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_11: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_11_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_12: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_12_Name"));

                    //Отправляем настройки в блок МД так как они переодически
                    //портяся.
                    blockMD.setParam(((UEParams) tmn.getParam(Devicess.ID_R4)).getParamMD());
                    break;
                }
                case States.STATE_13: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_13_Name"));

                    //Обновляем грфик дефектов
                    tubeLength = 0;
                    EventQueue.invokeLater(() -> {
                        //Обновляем график дефектов на трубе.
                        grPnlAll.updateGrafs(mFrm);
                    });

                    //Запускаем блок МД
                    blockMD.startScan(coord);
                    break;
                }
                case States.STATE_14: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_14_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_15: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_15_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_16: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_16_Name"));
                    mayScan = false;
                    //В методе обработчика изменния координаты, по этапам 
                    //запускать нельзя.

                    break;
                }
                case States.STATE_17: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_17_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_18: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_18_Name"));
                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_19: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_19_Name"));

                    //Останавливаем сканирование МД
                    blockMD.stopScan();
                    break;
                }
                case States.STATE_20: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_20_Name"));

                    //На этом этапе ничего не делаем
                    break;
                }
                case States.STATE_21: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_21_Name"));

                    //Останавливаем сканирование УЗК
                    blockUSK1.stopScan();
                    blockUSK2.stopScan();
                    //По координате в обработчике изменения координаты
                    break;
                }
                case States.STATE_22: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_22_Name"));

                    //Обновляем граыик дефектов
                    plc.readRegisters("TubeLen", 1);
                    tubeLength = plc.getShortRegister("TubeLen");
                    break;
                }
                case States.STATE_23: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_23_Name"));

                    //Получаем результаты от сортоскопа
                    progressDialog.startProcessing(
                            t("gettingSortoscopeResultsMessage"),
                            new SwingWorker<Integer, Integer>() {
                        @Override
                        protected Integer doInBackground() throws Exception {
                            try {
                                sortoscopeResult = null;
                                sortoscopeResult = sortoscopeDriver.getDurabilityGroup();

                            } catch (NoMeasurementsReadyException ex) {
                                log.warn("Got no ready measurements in sortoscope", ex);
                            }
                            progressDialog.setVisible(false);
                            return 0;
                        }
                    });
                    break;
                }
                case States.STATE_24: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_24_Name"));

                    //Сохраняем результаты в базу данных.
                    progressDialog.startProcessing(t("savingResultsToDB"), new SwingWorker<Integer, Integer>() {
                        @Override
                        protected Integer doInBackground() throws Exception {
                            progressDialog.setTitle(t("longProcessIsRunning"));

                            //Создаем результаты дефектоскопии
                            BazaDefectResults results = new BazaDefectResults(
                                    MainFrame.this,
                                    blockMD,
                                    blockUSK1,
                                    blockUSK2
                            );

                            //Получаем из базы данных трубу, которая находится на установке.
                            EntityManager em = emf.createEntityManager();
                            BasaTube tubeOnDevice = null;
                            try {
                                tubeOnDevice = em.find(
                                        BasaTube.class,
                                        tmn.getDetail(Devicess.ID_R4, Device.DEFAULT_VALUE)
                                );
                                //Если в базе есть труба которая находится на установке
                                if (tubeOnDevice != null) {
                                    //Проверяем наличие и записываем результаты измерений
                                    //от сортоскопа в трубу, что на установке
                                    tubeOnDevice.setGrTube(sortoscopeResult != null ? sortoscopeResult.toString() : "Нет");
                                    EntityTransaction trans = em.getTransaction();
                                    //Сохраянем трубу, так как группа прочности хранится в трубе
                                    try {
                                        trans.begin();
                                        tubeOnDevice = em.merge(tubeOnDevice);
                                        trans.commit();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    } finally {
                                        if (trans.isActive()) {
                                            trans.rollback();
                                        }
                                    }
                                    //Сохраняем результаты в БД
                                    saveTubeResultsToDB(
                                            tubeOnDevice.getId(),
                                            results,
                                            plc.getShortRegister("TubeLen"),
                                            "Сохранены результаты дефектоскопии."
                                    );
                                    //Обновляем спсок труб за смену.
                                    ((DefaultTableModel) table_Shift_Tubes.getModel()).setValueAt(
                                            (plc.getShortRegister("TubeLen") / 10) / 100F,
                                            0,
                                            2);
                                    ((DefaultTableModel) table_Shift_Tubes.getModel()).setValueAt(
                                            sortoscopeResult != null ? sortoscopeResult.toString() : "Нет",
                                            0,
                                            4);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            } finally {
                                if (em != null) {
                                    em.close();
                                }
                            }

                            //Прячем диалог
                            progressDialog.setVisible(false);

                            return 0;
                        }
                    });
                    //Отправляем сигнал о том, что результаты дефектоскопии сохранены
                    sendCmdToPLC(Commands.DEFECTS_DETECTION_RESULTS_ARE_SAVED);
                    break;
                }
                case States.STATE_25: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_25_Name"));
                    //Если включена галочка пропускать годные
                    if (checkBox_GoodAutohandle.isSelected()) {
                        log.debug("Good automaticaly go next checkbox is [true].");

                        //Получаем данные от транспортного менеджера о наличии трубы
                        long tubeOnTheDeviceID = tmn.getDetail(Devicess.ID_R4, Device.DEFAULT_VALUE);
                        log.debug("Got [{}] pipe on device.", tubeOnTheDeviceID);

                        if (tubeOnTheDeviceID == 0) {//Если по учету на установке ничего нет
                            sendCmdToPLC(Commands.NOTHING_IS_ON_THE_DEVICE);
                            log.debug("No pipe on device command has been sent.");
                        } else {//Если по учету труба на установке есть
                            /*Вообще, по идее, здесь должна быть проверка,
                        //на то, что находится на установке, труба или образец.
                        //Но это долго, так как надо получать трубу из базы.
                        //Поэтому идеологически правльный код, возможно,
                        //придется закомментировать.*/
                            BasaTube tubeOnTheDevice = null;
                            EntityManager em = emf.createEntityManager();
                            try {
                                tubeOnTheDevice = em.find(BasaTube.class, tubeOnTheDeviceID);
                            } catch (Exception ex) {
                                log.error("Can't find pipe with {} id.", tubeOnTheDeviceID, ex);
                            } finally {
                                if (em != null) {
                                    em.close();
                                }
                            }
                            log.debug("Got [{}] pipe on device.", tubeOnTheDeviceID);
                            //Получаем список дефектов
                            BazaTubeResult defects = tubeOnTheDevice.getTubeResults().get(0).tbRes;
                            log.debug("Got results for pipe with id [{}].", tubeOnTheDeviceID);
                            //Проверяем есть ли дефекты
                            boolean tubeIsBad = false;
                            for (int i = 0; i < 3; i++) {
                                //Если список дефектов для данного типа не null
                                if (defects.getDefects(i) != null && defects.getDefects(i).length != 0) {
                                    tubeIsBad = true;
                                    break;
                                }
                            }
                            //Если если установка нашла дефекты на трубе
                            if (tubeIsBad) {
                                log.debug("Got defects for pipe with id [{}].", tubeOnTheDeviceID);
                                //Делаем активными кнопки выбора
                                button_EnableVerdict.setEnabled(true);
                                button_RepeatDefectDetection.setEnabled(true);
                            } else {//Если установка дефектов не нашла
                                log.debug("No defects has been found for pipe with id [{}].", tubeOnTheDeviceID);
                                EntityManager em1 = emf.createEntityManager();
                                //Сначала получаем последние результаты проверки трбуы из базы
                                try {
                                    EntityTransaction trans = em1.getTransaction();
                                    try {
                                        //Помечаем трубу годной
                                        tubeOnTheDevice.setStatus(TubeConditions.GOOD);
                                        em1.getTransaction().begin();
                                        em1.merge(tubeOnTheDevice);
                                        em1.getTransaction().commit();
                                        log.debug("Pipe [{}] marked as good.", tubeOnTheDeviceID);
                                    } finally {
                                        if (trans.isActive()) {
                                            trans.rollback();
                                        }
                                    }
                                } catch (Exception ex) {
                                    log.error("Can't get trasaction.", ex);
                                } finally {
                                    if (em1 != null) {
                                        em1.close();
                                    }
                                }
                                //Обновляем список труб
                                ((DefaultTableModel) table_Shift_Tubes.getModel()).setValueAt("годная", 0, 3);
                                log.debug("Shift pipes list updated.");
                                //Отправляем комманду контроллеру, что труба годная.
                                sendCmdToPLC(Commands.MARK_AS_GOOD);
                                log.debug("[mark as good] commad has been sent to plc.");
                            }

                        }
                    } else {
                        //Делаем активными кнопки выбора
                        button_EnableVerdict.setEnabled(true);
                        button_RepeatDefectDetection.setEnabled(true);
                    }

                    //Деактивация кнопок и отправка команд происходит
                    //в обработчиках нажатия кнопок.
                    break;
                }
                case States.STATE_26: {
                    label_StateValue.setText(t("state_26_Name"));
                    break;
                }
                case States.STATE_27: {
                    label_StateValue.setText(t("state_27_Name"));
                    //Получаем трубу на установке
                    Long tubeOnDeviceID = tmn.getDetail(Devicess.ID_R4, Device.DEFAULT_VALUE);
                    BasaTube tubeOnDevice;
                    EntityManager em = emf.createEntityManager();
                    try {
                        tubeOnDevice = em.find(BasaTube.class, tubeOnDeviceID);
                        //Если на устройстве есть труба
                        if (tubeOnDevice != null) {
                            //Отправляем трубу с установки в архив
                            removeCurrentTubeFromDevice();
                            //Меняем статус 0 трубы в таблице на Архив
                            ((DefaultTableModel) table_Shift_Tubes.getModel()).setValueAt(t("archive"), 0, 5);
                            //Посылаем сигнал, что вся строка обновилась, чтобы убрать зеленую заливку
                            ((DefaultTableModel) table_Shift_Tubes.getModel()).fireTableRowsUpdated(0, 0);
                            //Отправляем коммаду контроллеру, что труба отправлена в архив
                            sendCmdToPLC(Commands.TUBE_IS_IN_ARCHIVE);

                            //Если труба годная, то увеличиваем количество годных 
                            if (tubeOnDevice.getStatusToString().equals("годная")) {
                                tubesCounter.addGoodTube();
                            } else {//Если труба брак, то увеличиваем количество бракованных
                                tubesCounter.addBadTube();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (em != null) {
                            em.close();
                        }
                    }
                    break;
                }
                case States.STATE_28: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_28_Name"));

                    //Получаем данные от транспортного менеджера о наличии трубы
                    long tubeOnTheDeviceID = tmn.getDetail(Devicess.ID_R4, Device.DEFAULT_VALUE);

                    if (tubeOnTheDeviceID == 0) {//Если по учету на установке ничего нет
                        sendCmdToPLC(Commands.NOTHING_IS_ON_THE_DEVICE);
                    } else {//Если по учету труба на установке есть
                        /*Вообще, по идее, здесь должна быть проверка,
                        //на то, что находится на установке, труба или образец.
                        //Но это долго, так как надо получать трубу из базы.
                        //Поэтому идеологически правльный код, возможно,
                        //придется закомментировать.*/
                        BasaTube tubeOnTheDevice;
                        EntityManager em = emf.createEntityManager();
                        try {
                            tubeOnTheDevice = em.find(BasaTube.class, tubeOnTheDeviceID);
                            //Если тип трубы образец
                            if (tubeOnTheDevice.isSample()) {
                                sendCmdToPLC(Commands.SAMPLE_IS_ON_THE_DEVICE);
                            } else {//На установке не образец
                                sendCmdToPLC(Commands.TUBE_IS_ON_THE_DEVICE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            if (em != null) {
                                em.close();
                            }
                        }
                    }
                    break;
                }
                case States.STATE_29: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_29_Name"));

                    //Создаем объект с названиями кнопок для принятия решения оператором
                    Object[] buttonsNames = new Object[]{
                        t("controlAsTube"),
                        t("controlAsSample")
                    };
                    //Выводи диалог и проверяем выбор оператора

                    switch (JOptionPane.showOptionDialog(
                            this,
                            t("whatToDoWithTubeQuestion"),
                            t("whatToDoWithTubeQuestionDialogCaption"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            buttonsNames,
                            JOptionPane.YES_OPTION)) {
                        case JOptionPane.YES_OPTION: {//Проконтролировать как трубу
                            //Отправляем команду контроллеру, что необходимо проверить трубу
                            sendCmdToPLC(Commands.CONTROL_AS_TUBE);
                            break;
                        }
                        case JOptionPane.NO_OPTION: {//Проконтролировать как образец
                            //Отправляем команду контроллеру, что необходимо проверить образец
                            sendCmdToPLC(Commands.CONTROL_AS_SAMPLE);
                            break;
                        }
                        default: {//Ничего не делаем

                        }
                    }
                    break;
                }
                case States.STATE_30: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_30_Name"));

                    //Регистрируем новый образец
                    BasaTube addedTube = addTube(
                            Devicess.ID_R4, //ID Установки
                            (Customer) combobox_CustomerSelection.getSelectedItem(), //Заказчик
                            shiftManager.getShift(),//Смена
                            Device.DEFAULT_VALUE, //Позоция на установке
                            ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.getId(),//ID Типа трубы
                            true,//Является ли труба образцом
                            "Новая труба загружена на установку" //Комментарий
                    );
                    //Отправляем комманду о том, что образец зарегистрирован
                    sendCmdToPLC(Commands.NEW_SAMPLE_IS_REGISTRED);
                    break;
                }
                case States.STATE_31: {
                    log.debug("Entered {} state", state);
                    label_StateValue.setText(t("state_31_Name"));
                    //Если не начата смена или закончена
                    switch (shiftManager.getState()) {
                        case SHIFT_IS_NOT_STARTED:
                        case SHIFT_IS_FINISHED: {
                            //Выводим диалог выбора оператора
                            try {
                                shiftManager.startShift();
                            } catch (Exception ex) {
                                log.error("Can not start new shift", ex);
                            }
                            button_Shift.setText(t("finishShift"));
                            break;
                        }
                    }
                    //Если не выбран производитель
                    if (!(combobox_CustomerSelection.getSelectedItem() instanceof Customer)) {
                        //Выводим диалог выбора заказчика
                        dialogCustomerSelection.setVisible(true);
                        combobox_CustomerSelection.removeAllItems();
                        List<Object> customers = dialogCustomerSelection.getCustomers();
                        customers.forEach((customer) -> {
                            combobox_CustomerSelection.addItem(customer);
                        });
                        combobox_CustomerSelection.setSelectedItem(dialogCustomerSelection.getSelectedCustomer());
                    }

                    //Начинаем смену
                    //Отправляем комманду контроллеру о том, что смена начата
                    sendCmdToPLC(Commands.SHIFT_IS_RUNNING_PROPERLY);
                    break;
                }
            }
        } catch (ModbusException ex) {
            log.error("PLC data exchange error:", ex);
        }
    }

    /**
     * Обновляет список заказчиков в раскладывающемся списке. Заказчик должен
     * быть выбран для начала смены.
     */
    private void updateCustomersList() {
        //Очищаем список элементов
        combobox_CustomerSelection.removeAllItems();

        //Добавляем элемент предлагающий выбрать заказчика
        combobox_CustomerSelection.addItem(t("chooseCustomer"));

        EntityManager em = emf.createEntityManager();
        try {
            //Получаем список заказчиков из базы
            List<Customer> customers = em.createQuery("SELECT a FROM Customer a")
                    .getResultList();

            //Заполняем список заказчиков
            for (int i = 0; i < customers.size(); i++) {
                combobox_CustomerSelection.insertItemAt(customers.get(i), i);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Обработчик изменения слова ошибок.
     */
    private void reactOnErrorUpdated() {
        short errors = 0;
        try {
            //Обновляем слово ошибок
            plc.readRegisters("Errors", 1);
            //Получаем слово ошибок
            errors = plc.getShortRegister("Errors");
        } catch (ModbusException ex) {
            log.error("Can't read errors from plc with IP [{}]", PLC_IP, ex);
        }
        //Если слово ошибок не пустое
        if (errors != 0) {
            //Если не выбрана закладка ошибки
            if (jTabbedPane1.getSelectedIndex() != 5) {
                //Активируем закладку ошибки
                jTabbedPane1.setSelectedIndex(5);
            }
        }
    }

    /**
     * Обработчик обновления координаты дефектоскопии.
     */
    private void reactOnCoordinateUpdated() {
        try {
            //Обновляем значение координаты
            plc.readRegisters("XCoord", 1);

            //Обновляем значение длины трубы
            plc.readRegisters("TubeLen", 1);

            //Получаем значение текущей координаты
            int localCoord = plc.getShortRegister("XCoord");

            //Выводим текущую координату
            label_xCoordinateValue.setText(String.format("%d", localCoord));

            //Получаем координату окончания УЗК
            int stopUSK_Coordinate = plc.getShortRegister("stopUSK_Coordinate");
            //Получаем координату начала УЗК
            int startUSK_Coordinate = plc.getShortRegister("startUSK_Coordinate");

            //Получаем длину трубы
            tubeLength = plc.getShortRegister("TubeLen");

            //Проверяем порали включать сканирование УЗК
            if ((startUSK_Coordinate <= (localCoord)) && state == 16 && !mayScan) {
                log.debug("Start ultrasonic defect detection from [{}] coordinatex.", localCoord);
                blockUSK1.startScan(localCoord);
                blockUSK2.startScan(localCoord);
                mayScan = true;
            }
            //Проверяем пора ли выключать сканирование УЗК
            if ((stopUSK_Coordinate <= (localCoord - tubeLength - 1) && state == 20 && mayScan)) {
                log.debug("Stop ultrasonic defect detection from [{}] coordinatex.", localCoord);
                blockUSK1.stopScan();
                blockUSK2.stopScan();
                mayScan = false;
            }
            {

                //Ведется лог координаты если включен
                log.debug(String.format("Current coordinate: {}", localCoord));

                if (state >= 16 && state <= 20 && mayScan) {
                    blockUSK1.setCurrenCoord(localCoord);
                    blockUSK2.setCurrenCoord(localCoord);
                    EventQueue.invokeLater(USKgrUpdater);
                }
                if (state >= 13 && state <= 18) {
                    blockMD.setCurrenCoord(localCoord);
                    EventQueue.invokeLater(MDgrUpdater);
                }

            }

            this.coord = localCoord;

        } catch (ModbusException ex) {
            log.error("Error reading data from PLC with IP [{}]", PLC_IP, ex);
        }
    }

    /**
     * <p>
     * Отправляет команду в контроллер. </P>
     * <p>
     * Если не удалось отправить команду, то не выбрасывает исключение, а делает
     * запись в журнал.</p>
     *
     * @param cmd номер команды.
     */
    private void sendCmdToPLC(int cmd) {
        try {
            plc.setShortRegister("Cmd", (short) cmd);
            plc.writeData();
        } catch (ModbusException ex) {
            log.error("Error writing data to PLC with IP [{}]", PLC_IP, ex);
        }
    }

    /**
     * Метод обновляет таблицу труб проверенных за смену. Также данная функция
     * управляет включение кнопок редактирования списка труб за смену: кнопка
     * "Положить образец" кнопка "Положить трубу" кнопка "Убрать"
     */
    public final void fillTubeTable() {
        DefaultTableModel tableModel = (DefaultTableModel) table_Shift_Tubes.getModel();

        //Удаляем все строки из таблицы
        while (tableModel.getRowCount() != 0) {
            tableModel.removeRow(0);
        }

        //Получаем список труб относящийся к текущей смене,
        //в зависимости от ёё состояния.
        List<BasaTube> smenaTubes = new ArrayList<>();
        switch (shiftManager.getState()) {
            //Указано только начало смены.
            //Получаем все трубы с начала смены до текущего времени.
            case SHIFT_IS_RUNNING:
                EntityManager em = emf.createEntityManager();
                try {
                    smenaTubes = em.createNamedQuery("getSmena")
                            .setParameter("beg", shiftManager.getShift().getBeginning())
                            .setParameter("end", new Date())
                            .getResultList();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (em != null) {
                        em.close();
                    }
                }
                break;
            //Даже в базу не смотрим.
            //Смены нет - значит и труб проконтроллированных за эту смену быть 
            //не должно.
            case SHIFT_IS_FINISHED:
            case SHIFT_IS_NOT_STARTED:
            default:
                smenaTubes = new ArrayList<>();

        }

        //Получаем ID трубы находящейся на установке
        long tubeOnDeviceID = tmn.getDetail(Devicess.ID_R4, Device.DEFAULT_VALUE);

        //Пполучаем трубу находящуюся на установке
        BasaTube tubeOnDevice = null;
        //Если на установке есть труба
        if (tubeOnDeviceID != 0) {
            EntityManager em = emf.createEntityManager();
            try {
                tubeOnDevice = em.find(BasaTube.class, tubeOnDeviceID);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        } else {//Если на установки трубы нет
            tubeOnDevice = null;
        }

        //Переменная для заполнения таблицы труб
        Object[][] data;
        //Если на установке есть труба
        if (tubeOnDevice != null) {
            //Если в базе нет труб проверенных за смену,
            if (smenaTubes.isEmpty()) {
                //то добавляем в список
                //трубу находящуюся на установке.
                data = new Object[1][6];

                data[0][0] = tubeOnDevice.getId();
                data[0][1] = ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes.get(toIntExact(tubeOnDevice.getTypeID() - 1)) + tubeOnDevice.isSampleToString();
                data[0][2] = tubeOnDevice.getLengthInMeters();
                data[0][3] = tubeOnDevice.getStatusToString();
                data[0][4] = tubeOnDevice.getDurabilityGroup() + "";
                data[0][5] = t("device");
            } else {//Если из базы получено не 0 труб проверенных за смену
                data = new Object[smenaTubes.size()][6];
                //Добавляем трубу на установке
                data[0][0] = tubeOnDevice.getId();
                data[0][1] = ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes.get(toIntExact(tubeOnDevice.getTypeID() - 1)) + tubeOnDevice.isSampleToString();
                data[0][2] = tubeOnDevice.getLengthInMeters();
                data[0][3] = tubeOnDevice.getStatusToString();
                data[0][4] = tubeOnDevice.getDurabilityGroup() + "";
                data[0][5] = t("device");

                //Добавляем трубы проверенные за смену
                for (int i = 1; i < smenaTubes.size(); i++) {
                    BasaTube aTube = smenaTubes.get(i);
                    data[i][0] = aTube.getId();
                    data[i][1] = ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes.get(toIntExact(aTube.getTypeID() - 1)) + aTube.isSampleToString();
                    data[i][2] = aTube.getLengthInMeters();
                    data[i][3] = aTube.getStatusToString();
                    data[i][4] = aTube.getDurabilityGroup() + "";
                    data[i][5] = t("archive");
                }
            }
        } else {//Если на станке нет трубы
            if (smenaTubes.isEmpty()) {//Если в базе нет труб проверенных за смену
                data = null;
            } else {
                //Если из базы получено не 0 труб проверенных за смену
                //Добавляем трубы проверенные за смену
                data = new Object[smenaTubes.size()][6];
                for (int i = 0; i < smenaTubes.size(); i++) {
                    BasaTube aTube = smenaTubes.get(i);
                    data[i][0] = aTube.getId();
                    data[i][1] = ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes.get(toIntExact(aTube.getTypeID() - 1)) + aTube.isSampleToString();
                    data[i][2] = aTube.getLengthInMeters();
                    data[i][3] = aTube.getStatusToString();
                    data[i][4] = aTube.getDurabilityGroup() + "";
                    data[i][5] = t("archive");
                }
            }
        }
        //Если есть трубы для вывода в таблицу,
        //то выводим список труб за смену в таблицу.
        if (data != null) {
            for (Object[] tubeRow : data) {
                tableModel.addRow(tubeRow);
            }
        }
    }

    /**
     * Обработчик изменения режима работы. Обновляет надпись текущего режима
     * работы на панели работы. Сбрасывает значение координаты и длины. Делает
     * активной закладку работа или наладка в зависимости от режима. Обновляет
     * текущую координату в режиме работы. Обновляет графики в
     * <tt>EventDispathcingThread</tt>.
     */
    public void reactOnModeUpdated() {
        try {
            plc.readRegisters();
            mode = plc.getShortRegister("Mode");
            //Пишем в лог если включена отладка
            log.debug("Work mode changed to [{}]", mode);

            if (mode != Modes.WORKING) {
                //Отключаем кнопки "Брак", "повторить",
                //"годная".
                button_EnableVerdict.setEnabled(false);
                button_RepeatDefectDetection.setEnabled(false);
            }
        } catch (ModbusException ex) {
            log.error("Error reading data from PLC with IP [{}]", PLC_IP, ex);
        }

        switch (mode) {
            case Modes.TURNING_ON: {
                label_xCoordinateValue.setText("");
                label_StateValue.setText(t("turningOnMode"));
                break;
            }
            case Modes.TUNING: {
                //Если закладка наладка не активна
                if (jTabbedPane1.getSelectedIndex() != 3) {
                    //Переключаемся на закладку наладка
                    jTabbedPane1.setSelectedIndex(3);
                }
                label_xCoordinateValue.setText("");
                label_StateValue.setText(t("tunningMode"));
                break;
            }
            case Modes.WORKING: {
                //Если закладка работа не активна
                if (jTabbedPane1.getSelectedIndex() != 0) {
                    //Переключаемся на закладку работа
                    jTabbedPane1.setSelectedIndex(0);
                }
                label_StateValue.setText(t("workingMode"));
                break;
            }
            case Modes.STOPPED: {
                label_StateValue.setText(t("stopMode"));
                break;
            }
        }

    }

    /**
     * Сохраняет результаты в базу данных.
     * <p>
     * <tt>EntityManager</tt> используется в блоке <tt>synchronized</tt>.</p>
     *
     * @param tubeID идентификатор трубы в базе
     * @param results результаты дефектоскопии
     * @param tubeLength измеренная длина трубы
     * @param comment комментарий об операции
     */
    private void saveTubeResultsToDB(
            long tubeID,
            BazaDefectResults results,
            short tubeLength,
            String comment) {
        //Ищем трубу для сохранения а базе данных
        BasaTube tubeToSave = null;
        EntityManager em = emf.createEntityManager();
        try {
            tubeToSave = (BasaTube) em.find(BasaTube.class, tubeID);
            //Если труба не найдена
            if (tubeToSave == null) {
                //выходим из метода
                return;
            }

            //Получаем список предыдущих проверок
            List<BazaDefectResults> defectsDetectionResults = tubeToSave.getTubeResults();
            //Если предыдущих проверок не было
            if (defectsDetectionResults == null) {
                //Создаем пустой список результатов проверок
                defectsDetectionResults = new ArrayList<>();
            }
            //Добавляем заданные результаты в список результов проверок
            defectsDetectionResults.add(results);

            //Присваивам пустой списко результаов контроля трубе для сохранения
            tubeToSave.setTubeResults(defectsDetectionResults);

            //Устанавливаем заданную длину трубы
            tubeToSave.setLenTube(tubeLength);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
        //Сохраняем трубу в базу
        em = emf.createEntityManager();

        synchronized (accessBd) {
            try {
                EntityTransaction trans = em.getTransaction();
                try {
                    trans.begin();
                    em.merge(tubeToSave);
                    em.persist(new Event(comment, tubeToSave.getId()));
                    trans.commit();
                } catch (Exception ex) {
                    log.error("Ошибка сохранения результатов {}", ex.getLocalizedMessage());
                } finally {
                    if (trans.isActive()) {
                        trans.rollback();
                    }
                }
            } finally {
                if (em != null) {
                    em.close();
                }
            }

        }
    }

    public void removeCurrentTubeFromDevice() {
        tmn.remDet(Device.DEFAULT_VALUE, Devicess.ID_R4, "Труба удалена с установки");
    }

    /**
     * Создает новую трубу, добавляет ее в базу данных и на заданную установку в
     * указанную позицию.
     *
     * @param deviceId идентификатор установки, на которую добавляется труба
     * @param position позиция, на которую добавляется труба,
     * {@code Device.DEFAULT_POSITION} для добавления трубы в позицию по
     * умолчанию.
     * @param tubeTypeId идентификатор типа создаваемой трубы
     * @param isSample признак того, что труба образец (СОП)
     * @param comment Комментарий добавления трубы.
     * @return Созданную трубу, в случае неудачи null.
     */
    synchronized public BasaTube addTube(
            Long deviceId,
            Customer customer,
            Shift shift,
            int position,
            Long tubeTypeId,
            boolean isSample,
            String comment) {
        BasaTube newTube = new BasaTube(
                customer,
                new Date(),
                shift,
                tubeTypeId,
                new Date()
        );
        //Помечаем трубу как образец
        newTube.setSample(isSample);
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction trans = em.getTransaction();
            try {
                //Сохраняем новую трубу
                trans.begin();
                em.persist(newTube);
                trans.commit();
            } finally {
                if (trans.isActive()) {
                    trans.rollback();
                }
            }
        } catch (Exception ex) {
            log.error("Can't get transaction.", ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        //Добавляем трубу в транспортный менеджер
        EventEnt ev = tmn.addDet(newTube.getId(), position, deviceId, comment);
        if (ev.codeErr != 0) {
            label_StateValue.setText("Ошибка добавления трубы: " + ev.getComment());
            log.error("Can't add pipe into transport manager, because of: {}", ev.getComment());
            return null;
        }

        em = emf.createEntityManager();
        try {
            EntityTransaction trans = em.getTransaction();

            try {
                trans.begin();
                newTube.setIdCreateEvt(ev.getId());
                newTube.setDateCreate(ev.getDate());
                em.merge(newTube);
                Event evv = new Event("Создание трубы", newTube.getId());
                em.persist(evv);
                trans.commit();
            } finally {
                if (trans.isActive()) {
                    trans.rollback();
                }
            }
        } catch (Exception e) {
            log.error("Can't get transaction.", e);
            label_StateValue.setText("Ошибка создания трубы " + e.getMessage());
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        //Добавляем трубу в список труб проконтроллированных за смену
        DefaultTableModel tableModel = ((DefaultTableModel) table_Shift_Tubes.getModel());
        //Устанавливаем
        tableModel.insertRow(0, new Object[]{
            newTube.getId(), //ID
            ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes.get(toIntExact(newTube.getTypeID()) - 1) + newTube.isSampleToString(),
            newTube.getLengthInMeters(),
            newTube.getStatusToString(),
            "",
            t("device")
        });
        return newTube;
    }

    @Override
    public long getDeviceId() {
        return Devicess.ID_R4;
    }

    private Operator isPassCheckOk() {
        if (restrictedAccessDialog == null) {
            restrictedAccessDialog = new Dialog_RestrictedAccess(mFrm, mayScan, emf);
        }
        restrictedAccessDialog.setModal(true);
        restrictedAccessDialog.setLocationRelativeTo(MainFrame.this);
        restrictedAccessDialog.setVisible(true);

        if (restrictedAccessDialog.getReturnStatus() == 1) {
            return restrictedAccessDialog.getOperator();
        }
        return null;
    }

    /**
     * Инициализируем дополнительные графические компоненты. Вызывать в
     * конструкторе MainFarme после initComponents().
     */
    private void customInitComponents() {

        label_TybeTypeValue.setText(((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.toString());

        tubeTypesDialog = new TubeTypesDialog(
                this,
                true,
                new TubeTypesTableModel()
        );

        //Устанавливаем исходное состояние системы в зависимости от состояния текущей смены.
        switch (shiftManager.getState()) {
            //Если смена не начата или завершена, то все что мы можем,
            //так это начать новую смену.
            case SHIFT_IS_NOT_STARTED:
            case SHIFT_IS_FINISHED:
                button_Shift.setText(t("startShift"));
                break;
            case SHIFT_IS_RUNNING:
                button_Shift.setText(t("endShift"));
                break;
            default:
        }
        //Добавляем подсветку трубы которая находится на установке.
        Enumeration<TableColumn> columns = table_Shift_Tubes.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            //Получаем следующую колонку
            TableColumn nextColumn = columns.nextElement();
            //Устанваливаем отрисовщик ячеек для каждой из колонок
            nextColumn.setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table,
                        Object value,
                        boolean isSelected,
                        boolean hasFocus,
                        int row,
                        int col) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            col);
                    if (table.getValueAt(row, PIPE_POSITION_COLUMN) == t("device")) {
                        label.setBackground(Color.GREEN);
                    }
                    if (table.getValueAt(row, PIPE_POSITION_COLUMN) != t("device")) {
                        label.setBackground(Color.WHITE);
                    }
                    if (table.getValueAt(row, PIPE_STATE_COLUMN).equals(t("googClass2"))) {
                        label.setBackground(Color.YELLOW);
                    }
                    if (table.getValueAt(row, PIPE_STATE_COLUMN).equals(t("googRepairClass2"))) {
                        label.setBackground(Color.ORANGE);
                    }
                    return label;
                }
            });

        }

        //Подсчитывается текущее количество брака годных в текущем списке
        tubesCounter.addListener(mFrm);
        tubesCounter.updateToTableModel((DefaultTableModel) table_Shift_Tubes.getModel());
        BasaTube.emf = this.emf;
        setUskSettingsEnabledState(false);
        setMdSettingsEnabledState(false);
    }

    private void setUskSettingsEnabledState(boolean isEnabled) {
        setEnabledRecurcively(USD, isEnabled);
        uskSettingsIsEnabled = isEnabled;
    }

    private void setMdSettingsEnabledState(boolean isEnabled) {
        setEnabledRecurcively(magDef, isEnabled);
        mdSettingsIsEnabled = isEnabled;
    }

    private void setEnabledRecurcively(Component comp, boolean isEnabled) {
        if (comp == null) {
            return;
        }
        if (comp instanceof JPanel) {
            Component[] comps = ((JPanel) comp).getComponents();
            for (Component subComp : comps) {
                setEnabledRecurcively(subComp, isEnabled);
            }
            return;
        }
        if (comp instanceof JTabbedPane) {
            Component[] comps = ((JTabbedPane) comp).getComponents();
            for (Component subComp : comps) {
                setEnabledRecurcively(subComp, isEnabled);
            }
            return;
        }
        if (comp instanceof JScrollPane) {
            Component[] comps = ((JScrollPane) comp).getComponents();
            for (Component subComp : comps) {
                setEnabledRecurcively(subComp, isEnabled);
            }
            return;
        }
        if (comp instanceof JViewport) {
            Component[] comps = ((JViewport) comp).getComponents();
            for (Component subComp : comps) {
                setEnabledRecurcively(subComp, isEnabled);
            }
            return;
        }
        comp.setEnabled(isEnabled);
    }

    @Override
    public void reactOnTubesCountUpdated(long goodTubesCount, long badTubesCount) {
        label_BadTubesCountValue.setText(String.valueOf(badTubesCount));
        label_GoodTubesCountValue.setText(String.valueOf(goodTubesCount));
        label_TotalTubesCountValue.setText(String.valueOf(badTubesCount + goodTubesCount));
    }

    class BOX extends JCheckBox {

        public BOX() {
            setIcon(new OvalIcon(15, 15, Color.gray, Color.white));
            setRolloverIcon(new OvalIcon(15, 15, Color.gray, Color.white));
            setSelectedIcon(new OvalIcon(15, 15, Color.red, Color.white));
            setRolloverSelectedIcon(new OvalIcon(15, 15, Color.red, Color.white));
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        tube = new javax.swing.JPanel();
        pnTubeTbl = new javax.swing.JPanel();
        label_StateValue = new javax.swing.JLabel();
        label_xCoordinateValue = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_Shift_Tubes = new javax.swing.JTable();
        combobox_TubeOnDeviceResults = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        label_xCoordinate = new javax.swing.JLabel();
        label_State = new javax.swing.JLabel();
        button_Shift = new javax.swing.JButton();
        combobox_CustomerSelection = new javax.swing.JComboBox();
        button_CreateNewCustomer = new javax.swing.JButton();
        label_Operator = new javax.swing.JLabel();
        label_OperatorValue = new javax.swing.JLabel();
        label_TubeType = new javax.swing.JLabel();
        label_TybeTypeValue = new javax.swing.JLabel();
        button_RepeatDefectDetection = new javax.swing.JButton();
        label_GoodTubesCount = new javax.swing.JLabel();
        label_GoodTubesCountValue = new javax.swing.JLabel();
        label_BadTubesCount = new javax.swing.JLabel();
        label_BadTubesCountValue = new javax.swing.JLabel();
        label_TotalTubesCount = new javax.swing.JLabel();
        label_TotalTubesCountValue = new javax.swing.JLabel();
        button_DropTubesCounter = new javax.swing.JButton();
        checkBox_GoodAutohandle = new javax.swing.JCheckBox();
        button_EnableVerdict = new javax.swing.JButton();
        comboBox_TubeConditions = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnGrfsAll = new javax.swing.JPanel();
        magDef = new javax.swing.JPanel();
        pnNastr = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnGrfsMD = new javax.swing.JPanel();
        frBtSave = new javax.swing.JButton();
        USD = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pnGrfsUSK = new javax.swing.JPanel();
        pnNastrUSK = new javax.swing.JPanel();
        jButton_saveUskSettings = new javax.swing.JButton();
        panel_Tunning = new javax.swing.JPanel();
        jPanel_Magnetic = new javax.swing.JPanel();
        button_Magnetic_Heads_Close = new javax.swing.JToggleButton();
        button_Demagnetization_Coil_On = new javax.swing.JToggleButton();
        button_Magnetic_Coil_On = new javax.swing.JToggleButton();
        jPanel_Ultrasonic = new javax.swing.JPanel();
        button_Wetter_Close = new javax.swing.JToggleButton();
        button_Pumping_Out_On = new javax.swing.JToggleButton();
        button_USD_Carriage_Up = new javax.swing.JToggleButton();
        button_Dryer_On = new javax.swing.JToggleButton();
        button_Carriage_Pump_on = new javax.swing.JToggleButton();
        jPanel_Tansport = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        button_Rollgang_REV = new javax.swing.JToggleButton();
        button_Rollgang_FWD = new javax.swing.JToggleButton();
        button_Stop_Rollgang = new javax.swing.JButton();
        button_Reloader_1_Up = new javax.swing.JToggleButton();
        button_Holder_1_Down = new javax.swing.JToggleButton();
        button_Holder_2_Down = new javax.swing.JToggleButton();
        button_Holder_3_Down = new javax.swing.JToggleButton();
        button_Reloader_2_Up = new javax.swing.JToggleButton();
        jPanel_UnitFigure = new javax.swing.JPanel();
        jCheckBox17 = new javax.swing.JCheckBox();
        jCheckBox16 = new javax.swing.JCheckBox();
        jCheckBox15 = new javax.swing.JCheckBox();
        jCheckBox14 = new javax.swing.JCheckBox();
        jCheckBox13 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        panel_Errors = new javax.swing.JPanel();
        scrollPane_ForErrorsTable = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        label_ErrorTabCaption = new javax.swing.JLabel();
        label_ErrorState = new javax.swing.JLabel();
        label_ErrorStateValue = new javax.swing.JLabel();
        panel_Archive = new javax.swing.JPanel();
        button_GetArchiveData = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable_ArchiveResults = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        button_GetTotalReport = new javax.swing.JButton();
        button_graphPerPageReport = new javax.swing.JButton();
        label_TubeTotall = new javax.swing.JLabel();
        label_TotalTubesValue = new javax.swing.JLabel();
        label_TotalLength = new javax.swing.JLabel();
        label_TotalTubesLengthValue = new javax.swing.JLabel();
        label_GoodTubes = new javax.swing.JLabel();
        label_GoodTubesValue = new javax.swing.JLabel();
        label_GoodTubesLength = new javax.swing.JLabel();
        label_GoodTubesLengthValue = new javax.swing.JLabel();
        label_BadTubes = new javax.swing.JLabel();
        label_BadTubesValue = new javax.swing.JLabel();
        label_BadTubesLength = new javax.swing.JLabel();
        label_BadTubesLengthValue = new javax.swing.JLabel();
        label_AverageTubeControllTime = new javax.swing.JLabel();
        label_AverageTubeControllTimeValue = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_DatePicker = new ru.npptmk.bazaTest.defect.TableDatePicker();
        jButton_AllGraphsOnOnePage = new javax.swing.JButton();
        jLabel_DatePickerTitle = new javax.swing.JLabel();
        jCheckBox_CalculateSideThickness = new javax.swing.JCheckBox();
        jLabel_ReportCreationDuration = new javax.swing.JLabel();
        jLabel_ReportCreationDurationValue = new javax.swing.JLabel();
        mainMenu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        tubeTypesMenu = new javax.swing.JMenuItem();
        menuItem_Settings = new javax.swing.JMenuItem();
        sortoscopeMenu = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        menuItem_EnableLogging = new javax.swing.JMenuItem();
        jMenuItem_createAdmin = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        menu_Drivers = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        menuItem_AddGraph = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("АРМ дефектоскописта");
        setMaximumSize(new java.awt.Dimension(1280, 1000));
        setMinimumSize(new java.awt.Dimension(797, 549));
        setSize(new java.awt.Dimension(1280, 1000));

        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTabbedPane1.setMaximumSize(new java.awt.Dimension(32767, 1000));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(750, 450));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        tube.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tube.setMaximumSize(new java.awt.Dimension(795, 494));
        tube.setMinimumSize(new java.awt.Dimension(795, 494));
        tube.setPreferredSize(new java.awt.Dimension(795, 494));

        pnTubeTbl.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        label_StateValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_StateValue.setText(" ");
        label_StateValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        label_StateValue.setMaximumSize(new java.awt.Dimension(10, 26));
        label_StateValue.setMinimumSize(new java.awt.Dimension(10, 26));
        label_StateValue.setPreferredSize(new java.awt.Dimension(10, 26));

        label_xCoordinateValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_xCoordinateValue.setText("0");
        label_xCoordinateValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        label_xCoordinateValue.setMaximumSize(new java.awt.Dimension(10, 26));
        label_xCoordinateValue.setMinimumSize(new java.awt.Dimension(10, 26));
        label_xCoordinateValue.setPreferredSize(new java.awt.Dimension(10, 26));

        table_Shift_Tubes.setFont(SEGOE_UI_18);
        table_Shift_Tubes.getTableHeader().setFont(SEGOE_UI_18);
        table_Shift_Tubes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "№", "<HTML>Тип трубы</HTML>", "Длина", "Статус", "Группа", "Место"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_Shift_Tubes.setFocusable(false);
        table_Shift_Tubes.setRowHeight(20);
        table_Shift_Tubes.setRowSelectionAllowed(false);
        table_Shift_Tubes.setSelectionBackground(new java.awt.Color(153, 255, 0));
        table_Shift_Tubes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_Shift_Tubes.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_Shift_Tubes);
        if (table_Shift_Tubes.getColumnModel().getColumnCount() > 0) {
            table_Shift_Tubes.getColumnModel().getColumn(0).setPreferredWidth(5);
            table_Shift_Tubes.getColumnModel().getColumn(2).setPreferredWidth(10);
            table_Shift_Tubes.getColumnModel().getColumn(3).setPreferredWidth(10);
            table_Shift_Tubes.getColumnModel().getColumn(4).setPreferredWidth(10);
            table_Shift_Tubes.getColumnModel().getColumn(5).setResizable(false);
            table_Shift_Tubes.getColumnModel().getColumn(5).setPreferredWidth(40);
        }

        combobox_TubeOnDeviceResults.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        combobox_TubeOnDeviceResults.setMaximumSize(new java.awt.Dimension(10, 26));
        combobox_TubeOnDeviceResults.setMinimumSize(new java.awt.Dimension(10, 26));
        combobox_TubeOnDeviceResults.setPreferredSize(new java.awt.Dimension(10, 26));
        combobox_TubeOnDeviceResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_TubeOnDeviceResultsActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Список проверенных труб за смену");
        jLabel2.setMaximumSize(new java.awt.Dimension(10, 26));
        jLabel2.setMinimumSize(new java.awt.Dimension(10, 26));
        jLabel2.setPreferredSize(new java.awt.Dimension(10, 26));

        label_xCoordinate.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_xCoordinate.setText("Координата переднего края трубы в мм:");
        label_xCoordinate.setMaximumSize(new java.awt.Dimension(10, 26));
        label_xCoordinate.setMinimumSize(new java.awt.Dimension(10, 26));
        label_xCoordinate.setPreferredSize(new java.awt.Dimension(10, 26));

        label_State.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_State.setText("Выполняется:");
        label_State.setMaximumSize(new java.awt.Dimension(10, 26));
        label_State.setMinimumSize(new java.awt.Dimension(10, 26));
        label_State.setPreferredSize(new java.awt.Dimension(10, 26));

        button_Shift.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Shift.setText("Кнопка начала/завершения смены");
        button_Shift.setMaximumSize(new java.awt.Dimension(10, 26));
        button_Shift.setMinimumSize(new java.awt.Dimension(10, 26));
        button_Shift.setPreferredSize(new java.awt.Dimension(10, 26));
        button_Shift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_ShiftActionPerformed(evt);
            }
        });

        combobox_CustomerSelection.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        combobox_CustomerSelection.setMaximumSize(new java.awt.Dimension(10, 26));
        combobox_CustomerSelection.setMinimumSize(new java.awt.Dimension(10, 26));
        combobox_CustomerSelection.setPreferredSize(new java.awt.Dimension(10, 26));

        button_CreateNewCustomer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_CreateNewCustomer.setText("Новый заказчик");
        button_CreateNewCustomer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        button_CreateNewCustomer.setMaximumSize(new java.awt.Dimension(10, 26));
        button_CreateNewCustomer.setMinimumSize(new java.awt.Dimension(10, 26));
        button_CreateNewCustomer.setPreferredSize(new java.awt.Dimension(10, 26));
        button_CreateNewCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_CreateNewCustomerActionPerformed(evt);
            }
        });

        label_Operator.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_Operator.setText("Оператор:");
        label_Operator.setMaximumSize(new java.awt.Dimension(10, 26));
        label_Operator.setMinimumSize(new java.awt.Dimension(10, 26));
        label_Operator.setPreferredSize(new java.awt.Dimension(10, 26));

        label_OperatorValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_OperatorValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        label_OperatorValue.setMaximumSize(new java.awt.Dimension(10, 26));
        label_OperatorValue.setMinimumSize(new java.awt.Dimension(10, 26));
        label_OperatorValue.setPreferredSize(new java.awt.Dimension(10, 26));

        label_TubeType.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_TubeType.setText("Тип трубы:");
        label_TubeType.setMaximumSize(new java.awt.Dimension(10, 26));
        label_TubeType.setMinimumSize(new java.awt.Dimension(10, 26));
        label_TubeType.setPreferredSize(new java.awt.Dimension(10, 26));

        label_TybeTypeValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_TybeTypeValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));
        label_TybeTypeValue.setMaximumSize(new java.awt.Dimension(10, 26));
        label_TybeTypeValue.setMinimumSize(new java.awt.Dimension(10, 26));
        label_TybeTypeValue.setPreferredSize(new java.awt.Dimension(10, 26));

        button_RepeatDefectDetection.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        button_RepeatDefectDetection.setText("Перепроверить");
        button_RepeatDefectDetection.setEnabled(false);
        button_RepeatDefectDetection.setMargin(new java.awt.Insets(2, 2, 2, 2));
        button_RepeatDefectDetection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_RepeatDefectDetectionActionPerformed(evt);
            }
        });

        label_GoodTubesCount.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        label_GoodTubesCount.setText("Годных:");

        label_GoodTubesCountValue.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        label_GoodTubesCountValue.setText("0");
        label_GoodTubesCountValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));

        label_BadTubesCount.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        label_BadTubesCount.setText("Брак:");

        label_BadTubesCountValue.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        label_BadTubesCountValue.setText("0");
        label_BadTubesCountValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));

        label_TotalTubesCount.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        label_TotalTubesCount.setText("Итого:");

        label_TotalTubesCountValue.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        label_TotalTubesCountValue.setText("0");
        label_TotalTubesCountValue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 255)));

        button_DropTubesCounter.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        button_DropTubesCounter.setText("Сброс");
        button_DropTubesCounter.setMargin(new java.awt.Insets(2, 2, 2, 2));
        button_DropTubesCounter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_DropTubesCounterActionPerformed(evt);
            }
        });

        button_EnableVerdict.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        button_EnableVerdict.setText("Пометить");
        button_EnableVerdict.setEnabled(false);
        button_EnableVerdict.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_EnableVerdictActionPerformed(evt);
            }
        });

        comboBox_TubeConditions.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        comboBox_TubeConditions.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Годная", "Брак", "Годная ксласс 2", "Рем. класс 2" }));

        javax.swing.GroupLayout pnTubeTblLayout = new javax.swing.GroupLayout(pnTubeTbl);
        pnTubeTbl.setLayout(pnTubeTblLayout);
        pnTubeTblLayout.setHorizontalGroup(
            pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTubeTblLayout.createSequentialGroup()
                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTubeTblLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnTubeTblLayout.createSequentialGroup()
                                .addComponent(label_xCoordinate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_xCoordinateValue, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnTubeTblLayout.createSequentialGroup()
                                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_Operator, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_TubeType, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_OperatorValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(label_TybeTypeValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(pnTubeTblLayout.createSequentialGroup()
                                .addComponent(label_State, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_StateValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(pnTubeTblLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button_Shift, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(combobox_TubeOnDeviceResults, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnTubeTblLayout.createSequentialGroup()
                                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(pnTubeTblLayout.createSequentialGroup()
                                        .addComponent(button_RepeatDefectDetection, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(button_EnableVerdict)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comboBox_TubeConditions, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(39, 39, 39)
                                        .addComponent(checkBox_GoodAutohandle))
                                    .addGroup(pnTubeTblLayout.createSequentialGroup()
                                        .addComponent(label_GoodTubesCount)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_GoodTubesCountValue, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_BadTubesCount)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_BadTubesCountValue, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_TotalTubesCount)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_TotalTubesCountValue, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(button_DropTubesCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pnTubeTblLayout.createSequentialGroup()
                                .addComponent(combobox_CustomerSelection, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_CreateNewCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnTubeTblLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4)))
                .addContainerGap())
        );
        pnTubeTblLayout.setVerticalGroup(
            pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTubeTblLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_State, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_StateValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_xCoordinate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_xCoordinateValue, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label_Operator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_OperatorValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_TubeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_TybeTypeValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combobox_CustomerSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_CreateNewCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(combobox_TubeOnDeviceResults, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button_Shift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_GoodTubesCount)
                    .addComponent(label_GoodTubesCountValue)
                    .addComponent(label_BadTubesCount)
                    .addComponent(label_BadTubesCountValue)
                    .addComponent(label_TotalTubesCount)
                    .addComponent(label_TotalTubesCountValue)
                    .addComponent(button_DropTubesCounter))
                .addGap(18, 18, 18)
                .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBox_GoodAutohandle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnTubeTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_RepeatDefectDetection)
                        .addComponent(button_EnableVerdict)
                        .addComponent(comboBox_TubeConditions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jScrollPane2.getVerticalScrollBar().setPreferredSize(new Dimension(32,70));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnGrfsAll.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnGrfsAll.setLayout(new java.awt.BorderLayout());
        jScrollPane2.setViewportView(pnGrfsAll);

        javax.swing.GroupLayout tubeLayout = new javax.swing.GroupLayout(tube);
        tube.setLayout(tubeLayout);
        tubeLayout.setHorizontalGroup(
            tubeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tubeLayout.createSequentialGroup()
                .addComponent(pnTubeTbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE))
        );
        tubeLayout.setVerticalGroup(
            tubeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tubeLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(tubeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnTubeTbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Работа", tube);

        pnNastr.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnNastr.setMaximumSize(new java.awt.Dimension(344, 409));
        pnNastr.setMinimumSize(new java.awt.Dimension(344, 409));
        pnNastr.setLayout(new javax.swing.BoxLayout(pnNastr, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnGrfsMD.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnGrfsMD.setLayout(new java.awt.BorderLayout());
        jScrollPane1.setViewportView(pnGrfsMD);

        frBtSave.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        frBtSave.setText("Сохранить");
        frBtSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frBtSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout magDefLayout = new javax.swing.GroupLayout(magDef);
        magDef.setLayout(magDefLayout);
        magDefLayout.setHorizontalGroup(
            magDefLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(magDefLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(magDefLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(frBtSave)
                    .addComponent(pnNastr, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE))
        );
        magDefLayout.setVerticalGroup(
            magDefLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(magDefLayout.createSequentialGroup()
                .addGroup(magDefLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(magDefLayout.createSequentialGroup()
                        .addComponent(pnNastr, javax.swing.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(frBtSave))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        jTabbedPane1.addTab("УМД", magDef);

        USD.setEnabled(false);

        jScrollPane3.setMaximumSize(new java.awt.Dimension(306, 470));
        jScrollPane3.setMinimumSize(new java.awt.Dimension(306, 470));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(306, 470));

        pnGrfsUSK.setLayout(new java.awt.BorderLayout());
        jScrollPane3.setViewportView(pnGrfsUSK);

        pnNastrUSK.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnNastrUSK.setMaximumSize(new java.awt.Dimension(273, 2147483647));
        pnNastrUSK.setMinimumSize(new java.awt.Dimension(273, 0));
        pnNastrUSK.setPreferredSize(new java.awt.Dimension(273, 100));
        pnNastrUSK.setLayout(new java.awt.BorderLayout(1, 1));

        jButton_saveUskSettings.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton_saveUskSettings.setText("Сохранить");
        jButton_saveUskSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_saveUskSettingsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout USDLayout = new javax.swing.GroupLayout(USD);
        USD.setLayout(USDLayout);
        USDLayout.setHorizontalGroup(
            USDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(USDLayout.createSequentialGroup()
                .addGroup(USDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnNastrUSK, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(USDLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton_saveUskSettings)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE))
        );
        USDLayout.setVerticalGroup(
            USDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
            .addGroup(USDLayout.createSequentialGroup()
                .addComponent(pnNastrUSK, javax.swing.GroupLayout.PREFERRED_SIZE, 881, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_saveUskSettings)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("УЗК", USD);

        panel_Tunning.setPreferredSize(new java.awt.Dimension(531, 450));

        jPanel_Magnetic.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "<HTML>ТРАНСПОРТ</HTML>", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N
        jPanel_Magnetic.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel_Magnetic.setName("Magnetic"); // NOI18N

        button_Magnetic_Heads_Close.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Magnetic_Heads_Close.setText("<HTML><CENTER>ДАТЧИКИ МД СВЕСТИ</CENTER></HTML>");
        button_Magnetic_Heads_Close.setName("Magnetic_Heads_Close"); // NOI18N
        button_Magnetic_Heads_Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Magnetic_Heads_CloseActionPerformed(evt);
            }
        });

        button_Demagnetization_Coil_On.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Demagnetization_Coil_On.setText("<HTML>РАЗМАГНИЧИВАНИЕ ВКЛЮЧИТЬ</HTML>");
        button_Demagnetization_Coil_On.setName("Demagnetization_Coil_On"); // NOI18N
        button_Demagnetization_Coil_On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Demagnetization_Coil_OnActionPerformed(evt);
            }
        });

        button_Magnetic_Coil_On.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Magnetic_Coil_On.setText("<HTML>НАМАГНИЧИВАНИЕ ВКЛЮЧИТЬ</HTML>");
        button_Magnetic_Coil_On.setName("Magnetic_Coil_On"); // NOI18N
        button_Magnetic_Coil_On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Magnetic_Coil_OnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_MagneticLayout = new javax.swing.GroupLayout(jPanel_Magnetic);
        jPanel_Magnetic.setLayout(jPanel_MagneticLayout);
        jPanel_MagneticLayout.setHorizontalGroup(
            jPanel_MagneticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(button_Magnetic_Heads_Close)
            .addComponent(button_Demagnetization_Coil_On, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
            .addComponent(button_Magnetic_Coil_On)
        );
        jPanel_MagneticLayout.setVerticalGroup(
            jPanel_MagneticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_MagneticLayout.createSequentialGroup()
                .addComponent(button_Magnetic_Heads_Close, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Magnetic_Coil_On, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Demagnetization_Coil_On, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel_Ultrasonic.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "<HTML>ТРАНСПОРТ</HTML>", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N
        jPanel_Ultrasonic.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel_Ultrasonic.setName("Ultrasonic"); // NOI18N

        button_Wetter_Close.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Wetter_Close.setText("<HTML>БЛОК СМАЧИВАНИЯ СЖАТЬ</HTML>");
        button_Wetter_Close.setName("Wetter_Close"); // NOI18N
        button_Wetter_Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Wetter_CloseActionPerformed(evt);
            }
        });

        button_Pumping_Out_On.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Pumping_Out_On.setText("<HTML>НАСОС ОТКАЧКИ ВКЛЮЧИТЬ</HTML>");
        button_Pumping_Out_On.setName("Pumping_Out_On"); // NOI18N
        button_Pumping_Out_On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Pumping_Out_OnActionPerformed(evt);
            }
        });

        button_USD_Carriage_Up.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_USD_Carriage_Up.setText("<HTML>КАРЕТКА УЗК ПОДНЯТЬ</HTML>");
        button_USD_Carriage_Up.setName("USD_Carriage_Up"); // NOI18N
        button_USD_Carriage_Up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_USD_Carriage_UpActionPerformed(evt);
            }
        });

        button_Dryer_On.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Dryer_On.setText("<HTML>БЛОК ОСУШКИ ВКЛЮЧИТЬ</HTML>");
        button_Dryer_On.setName("Dryer_On"); // NOI18N
        button_Dryer_On.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Dryer_OnActionPerformed(evt);
            }
        });

        button_Carriage_Pump_on.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Carriage_Pump_on.setText("<HTML>ПОДАЧА ВОДЫ ВКЛЮЧИТЬ</HTML>");
        button_Carriage_Pump_on.setName("Carriage_Pump_on"); // NOI18N
        button_Carriage_Pump_on.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Carriage_Pump_onActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_UltrasonicLayout = new javax.swing.GroupLayout(jPanel_Ultrasonic);
        jPanel_Ultrasonic.setLayout(jPanel_UltrasonicLayout);
        jPanel_UltrasonicLayout.setHorizontalGroup(
            jPanel_UltrasonicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(button_Wetter_Close)
            .addComponent(button_Pumping_Out_On, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
            .addComponent(button_USD_Carriage_Up)
            .addComponent(button_Dryer_On)
            .addComponent(button_Carriage_Pump_on)
        );
        jPanel_UltrasonicLayout.setVerticalGroup(
            jPanel_UltrasonicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_UltrasonicLayout.createSequentialGroup()
                .addComponent(button_Wetter_Close, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Pumping_Out_On, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Dryer_On, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Carriage_Pump_on, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_USD_Carriage_Up, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel_Tansport.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "<HTML>ТРАНСПОРТ</HTML>", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N
        jPanel_Tansport.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel_Tansport.setName("Transport"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "<HTML>ТРАНСПОРТ</HTML>", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        button_Rollgang_REV.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Rollgang_REV.setText("<HTML>НАЗАД</HTML>");
        button_Rollgang_REV.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button_Rollgang_REV.setName("Rollgang_REV"); // NOI18N
        button_Rollgang_REV.setPreferredSize(new java.awt.Dimension(76, 34));
        button_Rollgang_REV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Rollgang_REVActionPerformed(evt);
            }
        });

        button_Rollgang_FWD.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Rollgang_FWD.setText("<HTML>ВПЕРЕД</HTML>");
        button_Rollgang_FWD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button_Rollgang_FWD.setMaximumSize(new java.awt.Dimension(76, 24));
        button_Rollgang_FWD.setName("Rollgang_FWD"); // NOI18N
        button_Rollgang_FWD.setPreferredSize(new java.awt.Dimension(76, 34));
        button_Rollgang_FWD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Rollgang_FWDActionPerformed(evt);
            }
        });

        button_Stop_Rollgang.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Stop_Rollgang.setText("СТОП");
        button_Stop_Rollgang.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button_Stop_Rollgang.setMaximumSize(new java.awt.Dimension(67, 24));
        button_Stop_Rollgang.setMinimumSize(new java.awt.Dimension(67, 24));
        button_Stop_Rollgang.setPreferredSize(new java.awt.Dimension(76, 34));
        button_Stop_Rollgang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Stop_RollgangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(button_Rollgang_REV, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Stop_Rollgang, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Rollgang_FWD, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(button_Rollgang_FWD, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Stop_Rollgang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Rollgang_REV, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_Reloader_1_Up.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Reloader_1_Up.setText("<HTML>ПЕРЕКЛАДЧИК 1 ПОДНЯТЬ</HTML>");
        button_Reloader_1_Up.setName("Reloader_1_Up"); // NOI18N
        button_Reloader_1_Up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Reloader_1_UpActionPerformed(evt);
            }
        });

        button_Holder_1_Down.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Holder_1_Down.setText("<HTML>ПРИЖИМ 1 ОПУСТИТЬ</HTML>");
        button_Holder_1_Down.setName("Holder_1_Down"); // NOI18N
        button_Holder_1_Down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Holder_1_DownActionPerformed(evt);
            }
        });

        button_Holder_2_Down.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Holder_2_Down.setText("<HTML>ПРИЖИМ 2 ОПУСТИТЬ</HTML>");
        button_Holder_2_Down.setName("Holder_2_Down"); // NOI18N
        button_Holder_2_Down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Holder_2_DownActionPerformed(evt);
            }
        });

        button_Holder_3_Down.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Holder_3_Down.setText("<HTML>ПРИЖИМ 3 ОПУСТИТЬ</HTML>");
        button_Holder_3_Down.setName("Holder_3_Down"); // NOI18N
        button_Holder_3_Down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Holder_3_DownActionPerformed(evt);
            }
        });

        button_Reloader_2_Up.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_Reloader_2_Up.setText("<HTML>ПЕРЕКЛАДЧИК 2 ПОДНЯТЬ</HTML>");
        button_Reloader_2_Up.setName("Reloader_2_Up"); // NOI18N
        button_Reloader_2_Up.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Reloader_2_UpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_TansportLayout = new javax.swing.GroupLayout(jPanel_Tansport);
        jPanel_Tansport.setLayout(jPanel_TansportLayout);
        jPanel_TansportLayout.setHorizontalGroup(
            jPanel_TansportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(button_Reloader_1_Up)
            .addComponent(button_Holder_1_Down)
            .addComponent(button_Holder_2_Down)
            .addComponent(button_Holder_3_Down)
            .addComponent(button_Reloader_2_Up)
        );
        jPanel_TansportLayout.setVerticalGroup(
            jPanel_TansportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_TansportLayout.createSequentialGroup()
                .addComponent(button_Reloader_1_Up, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Holder_1_Down, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Holder_2_Down, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Holder_3_Down, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_Reloader_2_Up, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel_UnitFigure.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "СХЕМА УСТАНОВКИ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N
        jPanel_UnitFigure.setName("UnitFigure"); // NOI18N
        jPanel_UnitFigure.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jCheckBox17.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox17.setText("SQ17");
        jCheckBox17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox17.setName("SQ17_TUBE_ON_OUT_TABLE"); // NOI18N
        jCheckBox17.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox17, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, -1, -1));

        jCheckBox16.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox16.setText("SQ16");
        jCheckBox16.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCheckBox16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox16.setName("SQ16_IN_RELOADER_UP"); // NOI18N
        jCheckBox16.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox16, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, -1, -1));

        jCheckBox15.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox15.setText("SQ15");
        jCheckBox15.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCheckBox15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox15.setName("SQ15_IN_RELOADER_DOWN"); // NOI18N
        jCheckBox15.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox15, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, -1, -1));

        jCheckBox14.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox14.setText("SQ14");
        jCheckBox14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox14.setName("SQ14_TUBE_ON_IN_RELOADER"); // NOI18N
        jCheckBox14.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox14, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, -1, -1));

        jCheckBox13.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox13.setText("SQ13");
        jCheckBox13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox13.setName("SQ13_OUT_RELOADER_UP"); // NOI18N
        jCheckBox13.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox13, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 70, -1, -1));

        jCheckBox12.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox12.setText("SQ12");
        jCheckBox12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox12.setName("SQ12_OUT_RELOADER_DOWN"); // NOI18N
        jCheckBox12.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox12, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 110, -1, -1));

        jCheckBox11.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox11.setText("SQ11");
        jCheckBox11.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCheckBox11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox11.setName("SQ11_TUBE_ON_OUT_ROLLGANG"); // NOI18N
        jCheckBox11.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox11, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 70, -1, -1));

        jCheckBox10.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox10.setText("SQ10");
        jCheckBox10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox10.setName("SQ10_AFTER_SORTOSCOPE"); // NOI18N
        jCheckBox10.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox10, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 70, -1, -1));

        jCheckBox9.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox9.setText("SQ9");
        jCheckBox9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox9.setName("SQ9_HIGH_WATER_LEVEL"); // NOI18N
        jCheckBox9.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox9, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 100, -1, -1));

        jCheckBox8.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox8.setText("SQ8");
        jCheckBox8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox8.setName("SQ8_FIX3_UP"); // NOI18N
        jCheckBox8.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox8, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 30, -1, -1));

        jCheckBox7.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox7.setText("SQ7");
        jCheckBox7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox7.setName("SQ7_FIX2_UP"); // NOI18N
        jCheckBox7.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox7, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, -1, -1));

        jCheckBox6.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox6.setText("SQ6");
        jCheckBox6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox6.setName("SQ6_BEFORE_ULTRA_SOUND_BATH"); // NOI18N
        jCheckBox6.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox6, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 70, -1, -1));

        jCheckBox5.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox5.setText("SQ5");
        jCheckBox5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox5.setName("SQ5_CALIBRATION_SECTION_END"); // NOI18N
        jCheckBox5.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox5, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, -1, -1));

        jCheckBox4.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox4.setText("SQ4");
        jCheckBox4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox4.setName("SQ4_FIX1_UP"); // NOI18N
        jCheckBox4.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, -1, -1));

        jCheckBox3.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox3.setText("SQ3");
        jCheckBox3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox3.setName("SQ3_CALIBRATION_SECTION_START"); // NOI18N
        jCheckBox3.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 70, -1, -1));

        jCheckBox2.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox2.setText("SQ2");
        jCheckBox2.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCheckBox2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox2.setName("SQ2_FIX1_MUST_BE_DOWN"); // NOI18N
        jCheckBox2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, -1, -1));

        jCheckBox1.setForeground(new java.awt.Color(255, 51, 51));
        jCheckBox1.setText("SQ1");
        jCheckBox1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/Lamp.png"))); // NOI18N
        jCheckBox1.setName("SQ1_TUBE_ON_IN_ROLLGANG"); // NOI18N
        jCheckBox1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/LampON.png"))); // NOI18N
        jPanel_UnitFigure.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/npptmk/bazaTest/defect/Resource/images/UNKT500.png"))); // NOI18N
        jPanel_UnitFigure.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 1260, 160));

        javax.swing.GroupLayout panel_TunningLayout = new javax.swing.GroupLayout(panel_Tunning);
        panel_Tunning.setLayout(panel_TunningLayout);
        panel_TunningLayout.setHorizontalGroup(
            panel_TunningLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_TunningLayout.createSequentialGroup()
                .addComponent(jPanel_Tansport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Magnetic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_Ultrasonic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel_UnitFigure, javax.swing.GroupLayout.DEFAULT_SIZE, 1275, Short.MAX_VALUE)
        );
        panel_TunningLayout.setVerticalGroup(
            panel_TunningLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_TunningLayout.createSequentialGroup()
                .addGroup(panel_TunningLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_Tansport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_Magnetic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_Ultrasonic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel_UnitFigure, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Наладка", panel_Tunning);

        scrollPane_ForErrorsTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jTable1.getTableHeader().setFont(SEGOE_UI_18);
        jTable1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "№", "Ошибка"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(20);
        scrollPane_ForErrorsTable.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(25);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(743);
        }

        label_ErrorTabCaption.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_ErrorTabCaption.setText("СПИСОК ОШИБОК ВОЗНИКШИХ В РЕЖИМЕ РАБОТЫ");
        label_ErrorTabCaption.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        label_ErrorState.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_ErrorState.setText("Этап на котором возникла ошибка:");
        label_ErrorState.setPreferredSize(new java.awt.Dimension(20, 26));

        label_ErrorStateValue.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        label_ErrorStateValue.setPreferredSize(new java.awt.Dimension(20, 26));

        javax.swing.GroupLayout panel_ErrorsLayout = new javax.swing.GroupLayout(panel_Errors);
        panel_Errors.setLayout(panel_ErrorsLayout);
        panel_ErrorsLayout.setHorizontalGroup(
            panel_ErrorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_ErrorsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_ErrorState, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_ErrorStateValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(scrollPane_ForErrorsTable)
            .addGroup(panel_ErrorsLayout.createSequentialGroup()
                .addGap(366, 366, 366)
                .addComponent(label_ErrorTabCaption)
                .addContainerGap(471, Short.MAX_VALUE))
        );
        panel_ErrorsLayout.setVerticalGroup(
            panel_ErrorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_ErrorsLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(label_ErrorTabCaption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_ErrorsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label_ErrorState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_ErrorStateValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane_ForErrorsTable, javax.swing.GroupLayout.DEFAULT_SIZE, 855, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Ошибки", panel_Errors);

        panel_Archive.setMaximumSize(new java.awt.Dimension(1280, 991));
        panel_Archive.setName("Archive"); // NOI18N
        panel_Archive.setPreferredSize(new java.awt.Dimension(1280, 991));

        button_GetArchiveData.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_GetArchiveData.setText("<HTML><LEFT>Получить данные из архива</LEFT></HTML>");
        button_GetArchiveData.setMaximumSize(new java.awt.Dimension(186, 54));
        button_GetArchiveData.setMinimumSize(new java.awt.Dimension(186, 54));
        button_GetArchiveData.setPreferredSize(new java.awt.Dimension(186, 54));
        button_GetArchiveData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_GetArchiveDataActionPerformed(evt);
            }
        });

        jScrollPane5.getVerticalScrollBar().setPreferredSize(new Dimension(100,0));
        jScrollPane5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jTable_ArchiveResults.setFont(SEGOE_UI_18);
        jTable_ArchiveResults.getTableHeader().setFont(SEGOE_UI_18);
        jTable_ArchiveResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "№ трубы", "Время контроля", "Результат контроля", "Т. размер", "Мин. Толщина", "Длина", "ГП", "Оператор", "Заказчик"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_ArchiveResults.setRowHeight(20);
        jTable_ArchiveResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_ArchiveResultsMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTable_ArchiveResults);
        if (jTable_ArchiveResults.getColumnModel().getColumnCount() > 0) {
            jTable_ArchiveResults.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable_ArchiveResults.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_ArchiveResults.getColumnModel().getColumn(2).setPreferredWidth(50);
            jTable_ArchiveResults.getColumnModel().getColumn(3).setMinWidth(150);
            jTable_ArchiveResults.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable_ArchiveResults.getColumnModel().getColumn(3).setMaxWidth(150);
            jTable_ArchiveResults.getColumnModel().getColumn(4).setResizable(false);
            jTable_ArchiveResults.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTable_ArchiveResults.getColumnModel().getColumn(5).setMinWidth(20);
            jTable_ArchiveResults.getColumnModel().getColumn(5).setPreferredWidth(50);
            jTable_ArchiveResults.getColumnModel().getColumn(5).setMaxWidth(50);
            jTable_ArchiveResults.getColumnModel().getColumn(6).setMinWidth(20);
            jTable_ArchiveResults.getColumnModel().getColumn(6).setPreferredWidth(20);
            jTable_ArchiveResults.getColumnModel().getColumn(6).setMaxWidth(30);
        }

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setText("СПИСОК ПРОКОНТРОЛИРОВАННЫХ ТРУБ ЗА ВЫБРАННЫЙ ИНТЕРВАЛ ВРЕМЕНИ");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        button_GetTotalReport.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_GetTotalReport.setText("<HTML><LEFT>Вывести общий отчет</LEFT></HTML>");
        button_GetTotalReport.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        button_GetTotalReport.setMaximumSize(new java.awt.Dimension(149, 54));
        button_GetTotalReport.setMinimumSize(new java.awt.Dimension(149, 54));
        button_GetTotalReport.setPreferredSize(new java.awt.Dimension(149, 54));
        button_GetTotalReport.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        button_GetTotalReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_GetTotalReportActionPerformed(evt);
            }
        });

        button_graphPerPageReport.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        button_graphPerPageReport.setText("Вывести каждый график на отдельной странице");
        button_graphPerPageReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_graphPerPageReportActionPerformed(evt);
            }
        });

        label_TubeTotall.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_TubeTotall.setText("Всего труб проконтроллировано:");

        label_TotalTubesValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_TotalTubesValue.setPreferredSize(new java.awt.Dimension(0, 24));

        label_TotalLength.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_TotalLength.setText("Общей длиной:");

        label_TotalTubesLengthValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_TotalTubesLengthValue.setPreferredSize(new java.awt.Dimension(0, 24));

        label_GoodTubes.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_GoodTubes.setText("Всего труб годных:");

        label_GoodTubesValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_GoodTubesValue.setPreferredSize(new java.awt.Dimension(0, 24));

        label_GoodTubesLength.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_GoodTubesLength.setText("Общей длиной:");

        label_GoodTubesLengthValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_GoodTubesLengthValue.setPreferredSize(new java.awt.Dimension(0, 24));

        label_BadTubes.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_BadTubes.setText("Всего забраковано:");

        label_BadTubesValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_BadTubesValue.setPreferredSize(new java.awt.Dimension(0, 24));

        label_BadTubesLength.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_BadTubesLength.setText("Общей длиной:");

        label_BadTubesLengthValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_BadTubesLengthValue.setPreferredSize(new java.awt.Dimension(0, 24));

        label_AverageTubeControllTime.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_AverageTubeControllTime.setText("Среднее время контроля 1 трубы:");

        label_AverageTubeControllTimeValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        label_AverageTubeControllTimeValue.setPreferredSize(new java.awt.Dimension(0, 24));

        table_DatePicker.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        table_DatePicker.getTableHeader().setFont(new java.awt.Font("Dialog", 0, 18));
        table_DatePicker.setRowHeight(28);
        table_DatePicker.setRowSelectionAllowed(false);
        jScrollPane6.setViewportView(table_DatePicker);

        jButton_AllGraphsOnOnePage.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton_AllGraphsOnOnePage.setText("Вывести графики на одной странице");
        jButton_AllGraphsOnOnePage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_AllGraphsOnOnePageActionPerformed(evt);
            }
        });

        jLabel_DatePickerTitle.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel_DatePickerTitle.setText("Период за кторой необходим отчет:");

        jCheckBox_CalculateSideThickness.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jCheckBox_CalculateSideThickness.setText("Расчитывать толщины стенки");
        jCheckBox_CalculateSideThickness.setMargin(new java.awt.Insets(2, 0, 2, 2));

        jLabel_ReportCreationDuration.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel_ReportCreationDuration.setText("Время формирования отчета:");

        jLabel_ReportCreationDurationValue.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel_ReportCreationDurationValue.setPreferredSize(new java.awt.Dimension(0, 24));

        javax.swing.GroupLayout panel_ArchiveLayout = new javax.swing.GroupLayout(panel_Archive);
        panel_Archive.setLayout(panel_ArchiveLayout);
        panel_ArchiveLayout.setHorizontalGroup(
            panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_ArchiveLayout.createSequentialGroup()
                .addGap(168, 168, 168)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panel_ArchiveLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_DatePickerTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 1251, Short.MAX_VALUE)
                    .addGroup(panel_ArchiveLayout.createSequentialGroup()
                        .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_TubeTotall)
                            .addComponent(label_GoodTubes)
                            .addComponent(label_BadTubes))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_TotalTubesValue, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_GoodTubesValue, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_BadTubesValue, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_TotalLength)
                            .addComponent(label_GoodTubesLength)
                            .addComponent(label_BadTubesLength))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_TotalTubesLengthValue, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_GoodTubesLengthValue, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_BadTubesLengthValue, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox_CalculateSideThickness)
                            .addGroup(panel_ArchiveLayout.createSequentialGroup()
                                .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(label_AverageTubeControllTime)
                                    .addComponent(jLabel_ReportCreationDuration))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel_ReportCreationDurationValue, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                                    .addComponent(label_AverageTubeControllTimeValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_ArchiveLayout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_GetArchiveData, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton_AllGraphsOnOnePage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(button_GetTotalReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(button_graphPerPageReport, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panel_ArchiveLayout.setVerticalGroup(
            panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_ArchiveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(9, 9, 9)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_TubeTotall)
                    .addComponent(label_TotalTubesValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_TotalLength)
                    .addComponent(label_TotalTubesLengthValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_AverageTubeControllTime)
                    .addComponent(label_AverageTubeControllTimeValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_GoodTubes)
                    .addComponent(label_GoodTubesValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_GoodTubesLength)
                    .addComponent(label_GoodTubesLengthValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_ReportCreationDuration)
                    .addComponent(jLabel_ReportCreationDurationValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_BadTubes)
                    .addComponent(label_BadTubesValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_BadTubesLength)
                    .addComponent(label_BadTubesLengthValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox_CalculateSideThickness))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel_DatePickerTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_ArchiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(button_GetArchiveData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel_ArchiveLayout.createSequentialGroup()
                        .addComponent(jButton_AllGraphsOnOnePage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_GetTotalReport, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_graphPerPageReport))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(354, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Архив", panel_Archive);

        jMenu1.setText("Меню");
        jMenu1.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N

        tubeTypesMenu.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        tubeTypesMenu.setText("Типы труб");
        tubeTypesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tubeTypesMenuActionPerformed(evt);
            }
        });
        jMenu1.add(tubeTypesMenu);

        menuItem_Settings.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        menuItem_Settings.setText("Настройка");
        menuItem_Settings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_SettingsActionPerformed(evt);
            }
        });
        jMenu1.add(menuItem_Settings);

        sortoscopeMenu.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        sortoscopeMenu.setText("Настройка сортоскопа");
        sortoscopeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortoscopeMenuActionPerformed(evt);
            }
        });
        jMenu1.add(sortoscopeMenu);

        jMenuItem14.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jMenuItem14.setText("Сохранить настройки УЗК  и МД");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem14);

        jMenu6.setText("Администрирование");
        jMenu6.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N

        jMenuItem6.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jMenuItem6.setText("Сменить пароль администратора");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem6);

        menuItem_EnableLogging.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        menuItem_EnableLogging.setText("Включить запись журнала для отладки");
        menuItem_EnableLogging.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_EnableLoggingActionPerformed(evt);
            }
        });
        jMenu6.add(menuItem_EnableLogging);

        jMenuItem_createAdmin.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jMenuItem_createAdmin.setText("Создать администратора");
        jMenuItem_createAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_createAdminActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem_createAdmin);

        jMenu1.add(jMenu6);

        jMenuItem3.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jMenuItem3.setText("Выход");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        mainMenu.add(jMenu1);

        menu_Drivers.setText("Драйверы");
        menu_Drivers.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem1.setText("Запустить");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menu_Drivers.add(jMenuItem1);

        jMenuItem2.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem2.setText("Остановить");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        menu_Drivers.add(jMenuItem2);

        jMenu3.setText("МД");
        jMenu3.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N

        jMenuItem9.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem9.setText("Запустить");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem9);

        jMenuItem10.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem10.setText("Остановить");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem10);

        menu_Drivers.add(jMenu3);

        jMenu4.setText("УЗК");
        jMenu4.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N

        jMenuItem11.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem11.setText("Запустить");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem11);

        jMenuItem12.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem12.setText("Остановить");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem12);

        menu_Drivers.add(jMenu4);

        mainMenu.add(menu_Drivers);

        jMenu2.setText("Подключение");
        jMenu2.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N

        jMenuItem4.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem4.setText("Транспорт");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem5.setText("МД");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem7.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem7.setText("УЗК 1");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem8.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem8.setText("УЗК 2");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        mainMenu.add(jMenu2);

        jMenu5.setText("Графики");
        jMenu5.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        menuItem_AddGraph.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        menuItem_AddGraph.setText("Добавить график");
        menuItem_AddGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_AddGraphActionPerformed(evt);
            }
        });
        jMenu5.add(menuItem_AddGraph);

        jMenuItem16.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem16.setText("Сохранить набор");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem16);

        jMenuItem17.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem17.setText("Сменить набор");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem17);

        jMenuItem18.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jMenuItem18.setText("Удалить набор");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem18);

        mainMenu.add(jMenu5);

        setJMenuBar(mainMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1280, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 967, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startDrivers() {
        //Подключаемся к PLC
        progressDialog.startProcessing(
                t("connectingToPLC"),
                new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                try {
                    plc.connect();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            null,
                            t("canNotConnectToPLC"),
                            t("error"),
                            ERROR_MESSAGE
                    );
                    System.exit(1);
                } finally {
                    Thread.sleep(500);
                    progressDialog.setVisible(false);
                }
                return 0;
            }
        }
        );

        //Подключаемся к ДМ
        if (blockMD != null) {
            progressDialog.startProcessing(t("connectingToMDMessage"),
                    new SwingWorker<Integer, Integer>() {
                @Override
                protected Integer doInBackground() {
                    blockMD.start();
                    if (blockMD.getError() != GeneralUDPDevice.RC_OK) {
                        JOptionPane.showMessageDialog(
                                null,
                                t("canNotConnectToMD"),
                                t("error"),
                                ERROR_MESSAGE
                        );

                        log.error("Magnetic defect detection: {}", blockMD.getErrMessage());
                    } else {
                        blockMD.setParam(((UEParams) tmn.getParam(Devicess.ID_R4)).getParamMD());
                        blockMD.resetChangeFlag();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        log.warn("Interrupted while waiting after connecting to Magntic defect detection block", ex);
                    } finally {
                        progressDialog.setVisible(false);
                    }

                    return 0;
                }
            }
            );

        }

        //Подключение к УЗК1
        if (blockUSK1 != null) {
            progressDialog.startProcessing(t("connectingToUSK1"),
                    new SwingWorker<Integer, Integer>() {
                @Override
                protected Integer doInBackground() {
                    blockUSK1.start();
                    if (blockUSK1.getError() != GeneralUDPDevice.RC_OK) {
                        JOptionPane.showMessageDialog(
                                null,
                                t("canNotConnectToUSK1"),
                                t("error"),
                                ERROR_MESSAGE
                        );

                        log.error("Ultrasonic defect detection 1: {}", blockMD.getErrMessage());
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        log.warn("Interrupted while waiting after connecting to Ultrasonic defect detection block [1]", ex);
                    } finally {
                        progressDialog.setVisible(false);
                    }

                    return 0;
                }

            });
        }

        //Подключение к УЗК1
        if (blockUSK2 != null) {
            progressDialog.startProcessing("<HTML><LEFT>Подключаемся к блоку №2 <BR>ультразвукового контроля дефектов.<HTML><LEFT>",
                    new SwingWorker<Integer, Integer>() {
                @Override
                protected Integer doInBackground() {
                    blockUSK2.start();
                    if (blockUSK2.getError() != GeneralUDPDevice.RC_OK) {
                        JOptionPane.showMessageDialog(
                                null,
                                t("canNotConnectToUSK2"),
                                t("error"),
                                ERROR_MESSAGE
                        );

                        log.error("Ultrasonic defect detection 2: {}", blockMD.getErrMessage());
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        log.warn("Interrupted while waiting after connecting to Ultrasonic defect detection block [1]", ex);
                    }
                    progressDialog.setVisible(false);
                    return 0;
                }

            });
        }
    }

    private void stopDrivers() {
        if (blockMD != null) {
            blockMD.stop();
            if (blockMD.getError() != GeneralUDPDevice.RC_OK) {
                log.error("Magnetic defect detection: {}", blockMD.getErrMessage());
            }
        }
        if (blockUSK1 != null) {
            blockUSK1.stop();
            if (blockUSK1.getError() != GeneralUDPDevice.RC_OK) {
                log.error("Ultrasonic defect detection 1: {}", blockMD.getErrMessage());
            }
        }
        if (blockUSK2 != null) {
            blockUSK2.stop();
            if (blockUSK2.getError() != GeneralUDPDevice.RC_OK) {
                log.error("Ultrasonic defect detection 1: {}", blockMD.getErrMessage());
            }
        }
    }

    /**
     * Сохранение в базу данных текущих настроек МД.
     */
    private void saveMDParams() {
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        prm.currentTubeType.setParamsMD(blockMD.getParam());
        tmn.setParam(Devicess.ID_R4, prm);
        EntityManager em = emf.createEntityManager();
        synchronized (accessBd) {
            try {
                EntityTransaction trans = em.getTransaction();
                try {
                    trans.begin();
                    em.persist(new Event("Сохранены измененные параметры установки магнитной дефектоскопии.", 0));
                    trans.commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (trans.isActive()) {
                        trans.rollback();
                    }
                }
            } finally {
                if (em != null) {
                    em.close();
                }
            }

        }
    }

    /**
     * Сохранение в базу данных текущих настроек УЗК.
     */
    private void saveUSKParams(DeviceUSKUdp dev) {
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        if (dev.getDeviceId() == Devicess.ID_USK1) {
            prm.currentTubeType.setParamsUSK1(dev.getParams());
        }
        if (dev.getDeviceId() == Devicess.ID_USK2) {
            prm.currentTubeType.setParamsUSK2(dev.getParams());
        }
        tmn.setParam(Devicess.ID_R4, prm);
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction trans = em.getTransaction();
            try {
                synchronized (accessBd) {
                    trans.begin();
                    em.persist(new Event("Сохранены измененные параметры установки УЗК " + dev.driverName, 0));
                    trans.commit();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (trans.isActive()) {
                    trans.rollback();
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Возвращает панель со средствами отображения данных на активной вкладке.
     *
     * @return панель со средствами отображения данных, расположенная на текущей
     * вкладке.
     */
    private PanelForGraphics getCurrentPanelForGr() {
        int i = jTabbedPane1.getSelectedIndex();
        switch (i) {
            case 0:
                return grPnlAll;
            case 1:
                return grPnlMD;
            case 2:
                return grPnlUS;
        }
        return null;
    }

    /**
     * Возвращает коллекцию наборов графиков для панели средств отображения
     * данных на активной вкладке.
     *
     * @return Коллекция наборов графиков для текущей вкладки.
     */
    private HashMap<String, List<Serializable>> getCurretnGrSet() {
        int i = jTabbedPane1.getSelectedIndex();
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        switch (i) {
            case 0:
                return prm.prGrfTube;
            case 1:
                return prm.prGrfMd;
            case 2:
                return prm.prGrfUSK;
        }
        return null;
    }

    /**
     * Возвращает наименование текущего набора графиков для активной вкладки.
     *
     * @return имя текущего набора графиков для текущей вкладки.
     */
    private String getCurretnGrSetName() {
        int i = jTabbedPane1.getSelectedIndex();
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        switch (i) {
            case 0:
                return prm.currAllGrSet;
            case 1:
                return prm.currMDGrSet;
            case 2:
                return prm.currUSGrSet;
        }
        return null;
    }

    /**
     * Устанавливает наименование текущего набора графиков для активной вкладки.
     *
     * @param name имя текущего набора данных.
     */
    private void setCurretnGrSetName(String name) {
        int i = jTabbedPane1.getSelectedIndex();
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        switch (i) {
            case 0:
                prm.currAllGrSet = name;
                break;
            case 1:
                prm.currMDGrSet = name;
                break;
            case 2:
                prm.currUSGrSet = name;
        }
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        startDrivers();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        stopDrivers();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        saveMDParams();
        blockMD.resetChangeFlag();
        saveUSKParams(blockUSK1);
        blockUSK1.resetChangeFlag();
        saveUSKParams(blockUSK2);
        blockUSK2.resetChangeFlag();
        prmUSEditPnl.resetChanges();
        JOptionPane.showMessageDialog(null, t("allSettingsAreSaved"),
                t("allSettingsAreSavedDialog"), JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void menuItem_SettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_SettingsActionPerformed
        if (isPassCheckOk() == null) {
            return;
        }
        MainParametersDialog mainParametersDialog = new MainParametersDialog(
                ((UEParams) tmn.getParam(Devicess.ID_R4)),
                prmUSEditPnl.getDeltaGainPanel()
        );
        mainParametersDialog.setVisible(true);
        //Проеряем и сохраняем настройки
        progressDialog.startProcessing(t("savingSettings"), new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {

                if (mainParametersDialog.isChanged) {
                    //Коипируем выбранные настройки трубы
                    ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType = mainParametersDialog.getTubeType();

                    //Обновляем параметры в соостветствии с тем что введено на панели.
                    mainParametersDialog.updateParams((UEParams) tmn.getParam(Devicess.ID_R4));
                    //Отправляем обновленные параметры в PLC
                    try {
                        plc.setShortRegister("MDStart", ((UEParams) tmn.getParam(Devicess.ID_R4)).MDStart);
                        plc.setShortRegister("SecLirStart", ((UEParams) tmn.getParam(Devicess.ID_R4)).secLirStart);
                        plc.setShortRegister("startUSK_Coordinate", ((UEParams) tmn.getParam(Devicess.ID_R4)).USDStart);
                        plc.setShortRegister("ThdLirStart", ((UEParams) tmn.getParam(Devicess.ID_R4)).thdLirStart);
                        plc.setShortRegister("MDEnd", ((UEParams) tmn.getParam(Devicess.ID_R4)).MDEnd);
                        plc.setShortRegister("stopUSK_Coordinate", ((UEParams) tmn.getParam(Devicess.ID_R4)).USDEnd);
                        plc.setShortRegister("TailLen", ((UEParams) tmn.getParam(Devicess.ID_R4)).tailLen);
                        plc.setShortRegister("FillTime", ((UEParams) tmn.getParam(Devicess.ID_R4)).fillTime);
                        plc.setShortRegister("CalibrLen", ((UEParams) tmn.getParam(Devicess.ID_R4)).calibrLen);
                        plc.setShortRegister("IntSave", ((UEParams) tmn.getParam(Devicess.ID_R4)).intSave);
                        plc.setShortRegister("OnWatt", ((UEParams) tmn.getParam(Devicess.ID_R4)).onWatter);
                        plc.setShortRegister("OffWatt", ((UEParams) tmn.getParam(Devicess.ID_R4)).offWatter);
                        plc.setShortRegister("OnDry", ((UEParams) tmn.getParam(Devicess.ID_R4)).onDry);
                        plc.setShortRegister("OffDry", ((UEParams) tmn.getParam(Devicess.ID_R4)).offDry);
                        plc.writeData();
                    } catch (ModbusException ex) {
                        JOptionPane.showMessageDialog(null, t("canNotSendSettingsToPLC"));
                        log.error("Can't send setting to PLC on IP", PLC_IP, ex);
                    }

                    synchronized (accessBd) {
                        tmn.setParam(Devicess.ID_R4, ((UEParams) tmn.getParam(Devicess.ID_R4)));
                        EntityManager em = emf.createEntityManager();
                        try {
                            EntityTransaction trans = em.getTransaction();
                            try {
                                trans.begin();
                                em.persist(new Event("Обновлениы настройки линии", 0));
                                trans.commit();
                            } catch (Exception ex) {
                                log.error("Error updating line settings.", ex);
                            } finally {
                                if (trans.isActive()) {
                                    trans.rollback();
                                }
                            }
                        } finally {
                            if (em != null) {
                                em.close();
                            }
                        }
                    }
                }
                //Возможно следующая строчка ошибочная так текущий тип трубы надо оплучать их транспортного менеджера.
                //TubeType tubeType = mainParametersDialog.getTubeType();
                //Получаем текущий тип трбуы от транспортного менеджера
                TubeType tubeType = ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType;
                if (!tubeType.equals(((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType)) {
                    // Изменился тип трубы.
                    ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType = tubeType;
                    if (((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.getParamsMD() == null) {
                        ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.setParamsMD(new ParamsMD8Udp());
                    }
                    if (((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.getParamsUSK1() == null) {
                        ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.setParamsUSK1(new DeviceUSKUdpParams("УЗК 1"));
                    }
                    if (((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.getParamsUSK2() == null) {
                        ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.setParamsUSK2(new DeviceUSKUdpParams("УЗК 2"));
                    }
                    sendDriversSettingsForCurrentTubeType();
                    synchronized (accessBd) {
                        tmn.setParam(Devicess.ID_R4, ((UEParams) tmn.getParam(Devicess.ID_R4)));
                        EntityManager em = emf.createEntityManager();
                        try {
                            EntityTransaction trans = em.getTransaction();
                            try {
                                trans.begin();
                                em.persist(new Event("Изменен тип трубы", 0));
                                trans.commit();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            } finally {
                                if (trans.isActive()) {
                                    trans.rollback();
                                }
                            }
                        } finally {
                            if (em != null) {
                                em.close();
                            }
                        }

                    }
                    if (combobox_TubeOnDeviceResults.getSelectedIndex() == 0 && table_Shift_Tubes.getSelectedRow() < 1) {
                        grPnlAll.updateGrafs(MainFrame.this);
                    }
                }
                if (prmUSEditPnl.isChangeParams()) {
                    int rc = JOptionPane.showConfirmDialog(null, t("saveSettingInUSKBlock"),
                            "УЗК", JOptionPane.YES_NO_OPTION);
                    if (rc == JOptionPane.YES_OPTION) {
                        saveUSKParams(blockUSK1);
                        saveUSKParams(blockUSK2);
                    }
                    if (rc == JOptionPane.NO_OPTION) {
                        blockUSK1.setParams(((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.getParamsUSK1());
                        blockUSK2.setParams(((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.getParamsUSK2());
                    }
                    prmUSEditPnl.resetChanges();
                }
                progressDialog.setVisible(false);
                return 0;
            }
        });
        //Обновляем индикацию текущего типа трубы
        label_TybeTypeValue.setText(((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType.toString());
    }//GEN-LAST:event_menuItem_SettingsActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        blockMD.startScan(0);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        blockMD.stopScan();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        blockUSK1.startScan(0);
        blockUSK2.startScan(0);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        blockUSK1.stopScan();
        blockUSK2.stopScan();
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        if (blockMD.getChangeFlag()) {
            int rc = JOptionPane.showConfirmDialog(null, t("saveSettingInMDBlockQuestion"),
                    t("saveSettingInMDBlockQuestionCaption"), JOptionPane.YES_NO_OPTION);
            if (rc == JOptionPane.YES_OPTION) {
                saveMDParams();
            }
        }
        if (prmUSEditPnl.isChangeParams()) {
            int rc = JOptionPane.showConfirmDialog(null, t("saveSettingInUSKBlock"),
                    "УЗК", JOptionPane.YES_NO_OPTION);
            if (rc == JOptionPane.YES_OPTION) {
                saveUSKParams(blockUSK1);
                saveUSKParams(blockUSK2);
            }
            if (rc == JOptionPane.NO_OPTION) {
                UEParams pr = (UEParams) tmn.getParam(Devicess.ID_R4);
                blockUSK1.setParams(pr.currentTubeType.getParamsUSK1());
                blockUSK2.setParams(pr.currentTubeType.getParamsUSK2());
            }
            prmUSEditPnl.resetChanges();
        }
        System.exit(0);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if (isPassCheckOk() == null) {
            return;
        }
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        ControllerConnectionDialog ccd = new ControllerConnectionDialog(prm.getConTr());
        ccd.setIPLoc(prm.conTrLoc);
        ccd.setLocationRelativeTo(MainFrame.this);
        ccd.setVisible(true);
        if (ccd.isChanged) {
            InetAddress adr;
            try {
                adr = InetAddress.getByName(ccd.getIPLoc());
            } catch (UnknownHostException ex) {
                log.error("Wrong IP addres for PLC: {}", ex);
                JOptionPane.showMessageDialog(null, t("wrongAddress") + ccd.getIPLoc());
                return;
            }
            prm.setConTr(ccd.getIPRem());
            prm.conTrLoc = ccd.getIPLoc();
            synchronized (accessBd) {
                tmn.setParam(Devicess.ID_R4, prm);
            }
            try {
                plc.setShortRegister("IP_1", (short) (adr.getAddress()[0] & 0xff));
                plc.setShortRegister("IP_2", (short) (adr.getAddress()[1] & 0xff));
                plc.setShortRegister("IP_3", (short) (adr.getAddress()[2] & 0xff));
                plc.setShortRegister("IP_4", (short) (adr.getAddress()[3] & 0xff));
                plc.writeData();
            } catch (ModbusException ex) {
                log.error("Can't update devices IPs in PLC", ex);
            }
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        if (isPassCheckOk() == null) {
            return;
        }
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        UDPDeviceConnectionDialog udd = new UDPDeviceConnectionDialog(t("MDConnection"), prm.connMDL, prm.connMDR);
        udd.setVisible(true);
        if (udd.isChanged) {
            prm.connMDL = udd.local;
            prm.connMDR = udd.remote;
            synchronized (accessBd) {
                tmn.setParam(Devicess.ID_R4, prm);
            }
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        if (isPassCheckOk() == null) {
            return;
        }
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        UDPDeviceConnectionDialog udd = new UDPDeviceConnectionDialog(t("USK1Connection"), prm.connUSK1L, prm.connUSK1R);
        udd.setVisible(true);
        if (udd.isChanged) {
            prm.connUSK1L = udd.local;
            prm.connUSK1R = udd.remote;
            synchronized (accessBd) {
                tmn.setParam(Devicess.ID_R4, prm);
            }
        }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        if (isPassCheckOk() == null) {
            return;
        }
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        UDPDeviceConnectionDialog udd = new UDPDeviceConnectionDialog(t("USK2Connection"), prm.connUSK2L, prm.connUSK2R);
        udd.setVisible(true);
        if (udd.isChanged) {
            prm.connUSK2L = udd.local;
            prm.connUSK2R = udd.remote;
            synchronized (accessBd) {
                tmn.setParam(Devicess.ID_R4, prm);
            }
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void menuItem_AddGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_AddGraphActionPerformed
        Serializable ngr = panFact.getPanelParams(null);
        if (ngr != null) {
            PanelForGraphics pfg = getCurrentPanelForGr();
            if (pfg != null) {
                pfg.getParamCollection().add(ngr);
                pfg.redrawPanels();
                pfg.updateGrafs(this);
                pfg.invalidate();
            }
        }
    }//GEN-LAST:event_menuItem_AddGraphActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        String oldName = getCurretnGrSetName();
        PanelForGraphics pfg = getCurrentPanelForGr();
        String newName = JOptionPane.showInputDialog(null, t("setChartsSetName"), oldName);
        if (newName != null) {
            List<Serializable> ngc = new ArrayList<>();
            ngc.addAll(pfg.getParamCollection());
            getCurretnGrSet().put(newName, ngc);
            setCurretnGrSetName(newName);
            UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
            prm.prGrfTube.put(newName, ngc);
            synchronized (accessBd) {
                tmn.setParam(Devicess.ID_R4, prm);
            }
        }
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        HashMap<String, List<Serializable>> cgs = getCurretnGrSet();
        String selSet = (String) JOptionPane.showInputDialog(null, t("pickChartsSet"),
                t("chartsSet"), JOptionPane.QUESTION_MESSAGE,
                null, cgs.keySet().toArray(), getCurretnGrSetName());
        if (selSet != null) {
            List<Serializable> cs = cgs.get(selSet);
            setCurretnGrSetName(selSet);
            getCurrentPanelForGr().setParamCollection(cs);
            getCurrentPanelForGr().redrawPanels();
            getCurrentPanelForGr().updateGrafs(this);
        }
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        HashMap<String, List<Serializable>> cgs = getCurretnGrSet();
        String selSet = (String) JOptionPane.showInputDialog(null, t("pickChartsSet"),
                t("chartsSet"), JOptionPane.QUESTION_MESSAGE,
                null, cgs.keySet().toArray(), getCurretnGrSetName());
        if (selSet != null) {
            if (selSet.equals(getCurretnGrSetName())) {
                return;
            }
            cgs.remove(selSet);
        }
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    /**
     * Метод для вывода текстовых сообщений из файла свойств gui_text_ru_RU.
     *
     * @param key имя параметра из которого нужно взять строку.
     */
    private String t(String key) {
        return gui_text.getString(key);
    }
    private void frBtSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frBtSaveActionPerformed
        ParamsMD8Udp prmMD = prmMDEditPnl.getActualParam();
        blockMD.setParam(prmMD);
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        prm.currentTubeType.setParamsMD(prmMD);
        EntityManager em = emf.createEntityManager();
        synchronized (accessBd) {
            try {
                EntityTransaction trans = em.getTransaction();
                try {
                    trans.begin();
                    em.persist(new Event("Пользователь сохранил параметры МД8", 0));
                    trans.commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (trans.isActive()) {
                        trans.rollback();
                    }
                }
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
        setMdSettingsEnabledState(false);
    }//GEN-LAST:event_frBtSaveActionPerformed

    private void combobox_TubeOnDeviceResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_TubeOnDeviceResultsActionPerformed
        Object so = combobox_TubeOnDeviceResults.getSelectedItem();
        if (so instanceof String) {
            grPnlAll.updateGrafs(this);
            grPnlAll.updateGrafs(blockMD);
            grPnlAll.updateGrafs(blockUSK1);
            grPnlAll.updateGrafs(blockUSK2);
            return;
        }
        if (so instanceof BazaDefectResults) {
            BazaDefectResults res = (BazaDefectResults) so;
            grPnlAll.updateGrafs(new EmpyGraf((blockMD.getDeviceId())));
            grPnlAll.updateGrafs(new EmpyGraf((blockUSK1.getDeviceId())));
            grPnlAll.updateGrafs(new EmpyGraf((blockUSK2.getDeviceId())));
            grPnlAll.updateGrafs(res.tbRes);
            grPnlAll.updateGrafs(res.mdRes);
            grPnlAll.updateGrafs(res.usk1Res);
            grPnlAll.updateGrafs(res.usk2Res);
        }
    }//GEN-LAST:event_combobox_TubeOnDeviceResultsActionPerformed

    private void tubeTypesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tubeTypesMenuActionPerformed
        if (isPassCheckOk() == null) {
            return;
        }
        //Получаем параметры
        UEParams params = (UEParams) tmn.getParam(Devicess.ID_R4);
        tubeTypesDialog.setLocationRelativeTo(MainFrame.this);
        //Показываем диалог и передаем в него типы труб
        tubeTypesDialog.setVisible(params.tubeTypes, true);

        if (tubeTypesDialog.ShouldSaveChanges()) {
            //Копируем типы труб в параметры
            params.tubeTypes = tubeTypesDialog.getTubeTypesList();

            //Сохраняем параметры в базу данных
            tmn.setParam(Devicess.ID_R4, params);
            EntityManager em = emf.createEntityManager();
            try {
                EntityTransaction trans = em.getTransaction();
                try {
                    trans.begin();
                    em.persist(new Event("Сохранены измененные параметры типов труб.", 0));
                    trans.commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (trans.isActive()) {
                        trans.rollback();
                    }
                }
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
    }//GEN-LAST:event_tubeTypesMenuActionPerformed

    private void sortoscopeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortoscopeMenuActionPerformed
        if (isPassCheckOk() == null) {
            return;
        }
        //Получаем параметры
        UEParams params = (UEParams) tmn.getParam(Devicess.ID_R4);
        sortoscopeDialog.setVisible(true, params.currentTubeType);
        progressDialog.startProcessing(t("savingSortoscopeParametersToDb"), new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                tmn.setParam(Devicess.ID_R4, params);
                progressDialog.setVisible(false);
                return 0;
            }
        });
    }//GEN-LAST:event_sortoscopeMenuActionPerformed

    private void button_ShiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_ShiftActionPerformed
        Object[] YesNoButtons = new Object[]{t("yesButton"), t("noButton")};
        switch (shiftManager.getState()) {
            case SHIFT_IS_NOT_STARTED:
            case SHIFT_IS_FINISHED:
                if (JOptionPane.showOptionDialog(
                        null,
                        t("areYouSureAboutStartingShift"),
                        t("confirmation"),
                        YES_NO_OPTION,
                        QUESTION_MESSAGE,
                        null,
                        YesNoButtons,
                        YesNoButtons[0]
                ) == YES_OPTION) {
                    try {
                        shiftManager.startShift();
                        button_Shift.setText(t("finishShift"));
                        fillTubeTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                null,
                                t("canNotStartShift"),
                                t("error"),
                                ERROR_MESSAGE);
                    }
                }
                break;

            case SHIFT_IS_RUNNING:
                if (JOptionPane.showOptionDialog(
                        null,
                        t("areYouSureAboutEndingShift"),
                        t("confirmation"),
                        YES_NO_OPTION,
                        QUESTION_MESSAGE,
                        null,
                        YesNoButtons,
                        YesNoButtons[0]
                ) == YES_OPTION) {
                    try {
                        shiftManager.endShift();
                        button_Shift.setText(t("startShiftButton"));
                        fillTubeTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                null,
                                t("canNotFinishShift"),
                                t("error"),
                                ERROR_MESSAGE);
                    }
                }
                break;
        }
    }//GEN-LAST:event_button_ShiftActionPerformed

    private void switchToggleButton(java.awt.event.ActionEvent evt) {
        try {
            //Получаем имя кнопки. Должно совпадать с одним из OUTPUTS mFrm.
            String toggleButtonName = ((JToggleButton) evt.getSource()).getName();
            //Если имя кнопки задано пустой строкой или не задано
            if (toggleButtonName == null || toggleButtonName.equals("")) {
                throw new Exception("Не задано поле name у кнопки"
                        + ((JToggleButton) evt.getSource()).toString());
            } else {
                if (mFrm.plc.getOuputState(toggleButtonName)) {
                    mFrm.plc.setOuputState(toggleButtonName, false);
                } else {
                    mFrm.plc.setOuputState(toggleButtonName, true);
                }
            }
        } catch (ModbusException ex) {
            log.error("Can't write data to PLC with IP [{}] to toggle button [{}].", PLC_IP, evt.getSource().getClass().getName(), ex);
        } catch (Exception ex) {
            log.error("Button {} has no name set.", evt.getSource().getClass().getName(), ex);
        }
    }

    private void button_Magnetic_Heads_CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Magnetic_Heads_CloseActionPerformed
        simpleJToggleButtonHandler(
                evt,
                t("mdSensorsOpen"),
                t("mdSersorsClose")
        );
    }//GEN-LAST:event_button_Magnetic_Heads_CloseActionPerformed

    private void button_Holder_3_DownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Holder_3_DownActionPerformed
        simpleJToggleButtonHandler(
                evt,
                t("liftUpFix3"),
                t("liftDownFix3")
        );
    }//GEN-LAST:event_button_Holder_3_DownActionPerformed

    private void button_Magnetic_Coil_OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Magnetic_Coil_OnActionPerformed
        simpleJToggleButtonHandler(
                evt,
                t("turnOnMagnetizing"),
                t("turnOffMagnetizing")
        );
    }//GEN-LAST:event_button_Magnetic_Coil_OnActionPerformed

    private void button_Pumping_Out_OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Pumping_Out_OnActionPerformed
        simpleJToggleButtonHandler(
                evt,
                t("turnOnPumpOff"),
                t("turnOffPumpOff")
        );
    }//GEN-LAST:event_button_Pumping_Out_OnActionPerformed

    private void button_USD_Carriage_UpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_USD_Carriage_UpActionPerformed
        simpleJToggleButtonHandler(
                evt,
                t("dropUSKBathDown"),
                t("liftUSKBathUp")
        );
    }//GEN-LAST:event_button_USD_Carriage_UpActionPerformed

    private void button_Holder_2_DownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Holder_2_DownActionPerformed
        simpleJToggleButtonHandler(
                evt,
                "<HTML>ПРИЖИМ 2 ПОДНЯТЬ</HTML>",
                "<HTML>ПРИЖИМ 2 ОПУСТИТЬ</HTML>"
        );
    }//GEN-LAST:event_button_Holder_2_DownActionPerformed

    private void button_Rollgang_REVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Rollgang_REVActionPerformed
        try {
            //Имя источника события.
            String eventSourceName = ((JToggleButton) evt.getSource()).getName();
            //Обновляем значения входов из контроллера.
            plc.readOutputs(eventSourceName, 1);
            plc.readOutputs("Rollgang_FWD", 1);
            //Устанваливаем состояния выходя обратное текущему.
            plc.setOuputState(
                    eventSourceName,
                    !plc.getOuputState(eventSourceName)
            );
            //Если приходится включать движение назад и при этом
            //уже включено движение вперед, то выключаем движение вперед.
            if (plc.getOuputState(eventSourceName)
                    && plc.getOuputState("Rollgang_FWD")) {
                plc.setOuputState("Rollgang_FWD", false);
            }
            //Отправляем данные в контроллер.
            plc.writeData();
        } catch (ModbusException ex) {
            log.error("Can't write data to PLC with IP [{}] to send Rollgang_FWD", PLC_IP, ex);
        }
    }//GEN-LAST:event_button_Rollgang_REVActionPerformed

    private void button_Rollgang_FWDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Rollgang_FWDActionPerformed
        try {
            //Имя источника события.
            String eventSourceName = ((JToggleButton) evt.getSource()).getName();
            //Обновляем значения входов из контроллера.
            plc.readOutputs(eventSourceName, 1);
            plc.readOutputs("Rollgang_REV", 1);
            //Устанваливаем состояния выходя обратное текущему.
            plc.setOuputState(
                    eventSourceName,
                    !plc.getOuputState(eventSourceName)
            );
            //Если приходится включать движение вперед и при этом
            //уже включено движение назад, то выключаем движение вперед.
            if (plc.getOuputState(eventSourceName)
                    && plc.getOuputState("Rollgang_REV")) {
                plc.setOuputState("Rollgang_REV", false);
            }
            //Отправляем данные в контроллер.
            plc.writeData();
        } catch (ModbusException ex) {
            log.error("Can't write data to PLC with IP [{}] to send Rollgang_REV", PLC_IP, ex);
        }
    }//GEN-LAST:event_button_Rollgang_FWDActionPerformed

    private void button_Reloader_1_UpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Reloader_1_UpActionPerformed
        simpleJToggleButtonHandler(
                evt,
                "<HTML>ПЕРЕКЛАДЧИК 1 ОПУСТИТЬ</HTML>",
                "<HTML>ПЕРЕКЛАДЧИК 1 ПОДНЯТЬ</HTML>"
        );
    }//GEN-LAST:event_button_Reloader_1_UpActionPerformed

    private void button_Holder_1_DownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Holder_1_DownActionPerformed
        simpleJToggleButtonHandler(
                evt,
                "<HTML>ПРИЖИМ 1 ПОДНЯТЬ</HTML>",
                "<HTML>ПРИЖИМ 1 ОПУСТИТЬ</HTML>"
        );
    }//GEN-LAST:event_button_Holder_1_DownActionPerformed

    private void button_Reloader_2_UpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Reloader_2_UpActionPerformed
        simpleJToggleButtonHandler(evt,
                "<HTML>ПЕРЕКЛАДЧИК 2 ОПУСТИТЬ</HTML>",
                "<HTML>ПЕРЕКЛАДЧИК 2 ПОДНЯТЬ</HTML>"
        );
    }//GEN-LAST:event_button_Reloader_2_UpActionPerformed

    private void button_Stop_RollgangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Stop_RollgangActionPerformed
        try {
            //Готовим сигналы для остановки движения рольганга.
            plc.setOuputState("Rollgang_REV", false);
            plc.setOuputState("Rollgang_FWD", false);
            //Отправляем данные в контроллер.
            plc.writeData();
        } catch (ModbusException ex) {
            log.error("Can't write data to PLC with IP [{}] to send Rollgang_STOP", PLC_IP, ex);
        }
    }//GEN-LAST:event_button_Stop_RollgangActionPerformed

    private void button_Demagnetization_Coil_OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Demagnetization_Coil_OnActionPerformed
        simpleJToggleButtonHandler(
                evt,
                "<HTML>РАЗМАГНИЧИВАНИЕ ВЫКЛЮЧИТЬ</HTML>",
                "<HTML>РАЗМАГНИЧИВАНИЕ ВКЛЮЧИТЬ</HTML>"
        );
    }//GEN-LAST:event_button_Demagnetization_Coil_OnActionPerformed

    private void button_Wetter_CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Wetter_CloseActionPerformed
        simpleJToggleButtonHandler(
                evt,
                "<HTML>БЛОК СМАЧИВАНИЯ РАЗВЕСТИ</HTML>",
                "<HTML>БЛОК СМАЧИВАНИЯ СВЕСТИ</HTML>"
        );
    }//GEN-LAST:event_button_Wetter_CloseActionPerformed

    private void button_Dryer_OnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Dryer_OnActionPerformed
        simpleJToggleButtonHandler(
                evt,
                "<HTML>БЛОК ОСУШКИ ВЫКЛЮЧИТЬ</HTML>",
                "<HTML>БЛОК ОСУШКИ ВКЛЮЧИТЬ</HTML>"
        );
    }//GEN-LAST:event_button_Dryer_OnActionPerformed

    private void button_Carriage_Pump_onActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Carriage_Pump_onActionPerformed
        simpleJToggleButtonHandler(
                evt,
                "<HTML>ПОДАЧА ВОДЫ ВЫКЛЮЧИТЬ</HTML>",
                "<HTML>ПОДАЧА ВОДЫ ВКЛЮЧИТЬ</HTML>"
        );
    }//GEN-LAST:event_button_Carriage_Pump_onActionPerformed

    private void button_GetArchiveDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_GetArchiveDataActionPerformed
        if (archResults != null) {
            archResults.clear();
        }
        jTable_ArchiveResults.getSelectionModel().clearSelection();
        DateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        String query = "SELECT a FROM BasaTube a WHERE a.dateCreate BETWEEN :startDate AND :endDate";
        //Получаем список результатов из базы данных
        progressDialog.startProcessing("Получаем список труб из базы данных.", new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                EntityManager em = emf.createEntityManager();
                try {
                    log.debug("Starting loading results list...");
                    archResults = em
                            .createQuery(query)
                            .setParameter("startDate", Date.from(table_DatePicker.getStartLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()))
                            .setParameter("endDate", Date.from(table_DatePicker.getEndLocalDateTime().atZone(ZoneId.systemDefault()).toInstant()))
                            .getResultList();
                    log.debug("Results list has been loaded...");
                } catch (Exception ex) {
                    log.error("Can't load result list because: ", ex);
                } finally {
                    if (em != null) {
                        em.close();
                    }
                    progressDialog.setVisible(false);
                }
                return 0;
            }
        });
        //Если результаты котрые вернула база пусты или содержат 0 запией
        if (archResults == null || archResults.isEmpty()) {
            DefaultTableModel table = (DefaultTableModel) jTable_ArchiveResults.getModel();
            //Очищаем таблицу
            while (table.getRowCount() != 0) {
                table.removeRow(0);
            }
            JOptionPane.showMessageDialog(
                    null,
                    "По вышему запросу в базе данных труб не найдено.",
                    "Уведомление",
                    INFORMATION_MESSAGE
            );
            //Выходим из функции
            return;
        }
        //Создаем массив для заполнения таблицы результатов
        progressDialog.startProcessing("Готовим результаты", archResults.size(), new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                Date startDate = new Date();
                DefaultTableModel tableModel = (DefaultTableModel) jTable_ArchiveResults.getModel();
                //Удаляем предыдущий список
                log.debug("Start clearing pipes list.");
                while (tableModel.getRowCount() != 0) {
                    tableModel.removeRow(0);
                }
                log.debug("Pipes list has been cleared.");

                Object[][] data = new Object[archResults.size()][9];
                List<TubeType> tubeTypes = ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes;
                log.debug("Preparing pipe data for UI.");
                for (int i = 0; i < archResults.size(); i++) {
                    progressDialog.increment();
                    BasaTube currentArchResult = archResults.get(i);
                    data[i][0] = currentArchResult.getId();
                    data[i][1] = currentArchResult.getDateCreate() == null ? "Не указано" : format.format(currentArchResult.getDateCreate());
                    data[i][2] = currentArchResult.getStatusToString();
                    data[i][3] = tubeTypes.get(
                            toIntExact(currentArchResult.getTypeID()) - 1) == null
                            ? "Не указано"
                            : tubeTypes.get(toIntExact(currentArchResult.getTypeID()) - 1) + currentArchResult.isSampleToString();
                    float minThick = -1f;
                    //Если есть данные
                    if (jCheckBox_CalculateSideThickness.isSelected()) {
                        if (currentArchResult.getTubeResults() != null
                                && currentArchResult.getTubeResults().get(0) != null
                                && currentArchResult.getTubeResults().get(0).usk2Res != null
                                && currentArchResult.getTubeResults().get(0).usk2Res.ty.length == 8) {
                            //Получаем минимальное значение значение толщины

                            minThick = archResults
                                    .get(i)
                                    .getTubeResults()
                                    .get(0).usk2Res
                                    .getMinThick(4, 7, 3.0f);
                            //Округляем до 2 знаков
                            minThick = ((float) Math.round(minThick * 100)) / 100;

                        }
                    } else {
                        minThick = 0;
                    }

                    data[i][4] = minThick == -1 ? "Нет контакта" : minThick;
                    data[i][5] = currentArchResult.getLengthInMeters();
                    data[i][6] = currentArchResult.getDurabilityGroup() == null
                            ? "Не указано"
                            : currentArchResult.getDurabilityGroup();
                    if (currentArchResult.getShift() != null) {
                        data[i][7] = currentArchResult.getShift().getOperator() == null
                                ? "Не указано"
                                : currentArchResult.getShift().getOperator().toString();
                    }
                    data[i][8] = currentArchResult.getCustomer() == null
                            ? "Не указано"
                            : currentArchResult.getCustomer().toString();
                }
                log.debug("Preparing pipe data for UI has been finished.");

                //Если есть трубы для вывода в таблицу,
                //то выводим список труб за смену в таблицу.
                log.debug("Populating table with pipes data.");
                if (data != null && data.length > 0) {
                    for (Object[] tubeRow : data) {
                        tableModel.addRow(tubeRow);
                    }
                    log.debug("Populating table with pipes data has been finished.");
                    //Заполняем сводные данные по полученному из архива диапазону труб
                    //Считаем время нахождения всех труб на установке
                    Long totalTime = 0L;
                    //Считаем общую длину труб
                    Float totalTubesLength = 0.0F;
                    //Общее количество труб
                    Integer totalTubesCount = 0;
                    //Колличество годных
                    Integer goodTubesCount = 0;
                    //Длина годных труб
                    Float goodTubesLength = 0.0F;
                    //Количество забракованных
                    Integer badTubesCount = 0;
                    //Длина забракованных труб
                    Float badTubesLength = 0.0F;
                    for (BasaTube atube : archResults) {
                        if (!atube.isSample()) {
                            totalTime += atube.getControlDurationInSeconds();
                            switch (atube.getStatus()) {
                                //Бракованные
                                case 0:
                                    badTubesCount++;
                                    badTubesLength += atube.getLengthInMeters();
                                    break;
                                //Годные
                                case 1:
                                    goodTubesCount++;
                                    goodTubesLength += atube.getLengthInMeters();
                                    break;
                                //Непроконтроллированные
                                case 3:
                                    break;
                            }
                        }
                    }
                    totalTubesCount = goodTubesCount + badTubesCount;
                    totalTubesLength = goodTubesLength + badTubesLength;

                    label_BadTubesValue.setText(badTubesCount.toString());
                    label_BadTubesLengthValue.setText(Math.round(badTubesLength * 100.0) / 100.0 + "м");

                    label_GoodTubesValue.setText(goodTubesCount.toString());
                    label_GoodTubesLengthValue.setText(Math.round(goodTubesLength * 100.0) / 100.0 + "м");

                    label_TotalTubesValue.setText(totalTubesCount.toString());
                    label_TotalTubesLengthValue.setText(Math.round(totalTubesLength * 100.0) / 100.0 + "м");
                    if (totalTubesCount != 0) {
                        label_AverageTubeControllTimeValue.setText(totalTime / totalTubesCount + "с");
                    } else {
                        label_AverageTubeControllTimeValue.setText(0 + "с");
                    }
                }
                jLabel_ReportCreationDurationValue.setText(Long.toString((new Date().getTime() - startDate.getTime()) / 1000) + " c");
                progressDialog.setVisible(false);
                return 1;
            }
        });
    }//GEN-LAST:event_button_GetArchiveDataActionPerformed

    private void button_CreateNewCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_CreateNewCustomerActionPerformed
        DialogCustomerCreation dialogCustomerCreation = new DialogCustomerCreation(
                this,
                true,
                emf
        );
        dialogCustomerCreation.setVisible(true);
        updateCustomersList();
    }//GEN-LAST:event_button_CreateNewCustomerActionPerformed

    private void button_GetTotalReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_GetTotalReportActionPerformed
        //Если есть данные для вывода отчета
        if (archResults != null && !archResults.isEmpty()) {
            progressDialog.startProcessing("Подготавливаем отчет...", new SwingWorker<Integer, Integer>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    //Имя файла скомпилированного отчёта, готового для заполнения.
                    URL compiledReport = getClass().getResource("Resource/reports/TubesTable.jasper");

                    if (compiledReport == null) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Шаблон отчета не найден. \nНеправильно откомпилировали программу.",
                                "Ошибка",
                                ERROR_MESSAGE);
                        return 0;
                    }

                    //Оборачиваем TableModel для использования в отчёте
                    JRDataSourceTablesList jrDataSource = new JRDataSourceTablesList(
                            archResults,
                            ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes,
                            ((DefaultTableModel) jTable_ArchiveResults.getModel()).getDataVector().toArray());

                    //Сюда положим параметры для отчёта
                    Map<String, Object> parameters = new HashMap<>();

                    parameters.put("REPORT_START_TIME", new SimpleDateFormat("dd.MM.YY HH:mm:ss").format(table_DatePicker.getStartDate()));
                    parameters.put("REPORT_END_TIME", new SimpleDateFormat("dd.MM.YY HH:mm:ss").format(table_DatePicker.getEndDate()));
                    //Считаем время нахождения всех труб на установке
                    Long totalTime = 0L;
                    //Считаем общую длину труб
                    Float totalTubesLength = 0.0F;
                    //Общее количество труб
                    Integer totalTubesCount = 0;
                    //Колличество годных
                    Integer goodTubesCount = 0;
                    //Длина годных труб
                    Float goodTubesLength = 0.0F;
                    //Количество забракованных
                    Integer badTubesCount = 0;
                    //Длина забракованных труб
                    Float badTubesLength = 0.0F;
                    for (BasaTube atube : archResults) {
                        if (!atube.isSample()) {
                            totalTime += atube.getControlDurationInSeconds();
                            switch (atube.getStatus()) {
                                //Бракованные
                                case 0:
                                    badTubesCount++;
                                    badTubesLength += atube.getLengthInMeters();
                                    break;
                                //Годные
                                case 1:
                                    goodTubesCount++;
                                    goodTubesLength += atube.getLengthInMeters();
                                    break;
                                //Непроконтроллированные
                                case 3:
                                    break;
                            }
                        }
                    }
                    totalTubesCount = goodTubesCount + badTubesCount;
                    totalTubesLength = goodTubesLength + badTubesLength;
                    parameters.put("TOTAL_TUBES_COUNT", totalTubesCount);
                    parameters.put("TOTAL_TUBES_LENGTH", Float.valueOf(Math.round(totalTubesLength * 100)) / 100f);
                    parameters.put("GOOD_TUBES_COUNT", goodTubesCount);
                    parameters.put("GOOD_TUBES_LENGTH", Float.valueOf(Math.round(goodTubesLength * 100)) / 100f);
                    parameters.put("BAD_TUBES_COUNT", badTubesCount);
                    parameters.put("BAD_TUBES_LENGTH", Float.valueOf(Math.round(badTubesLength * 100)) / 100f);
                    if (totalTime != 0) {
                        parameters.put("OVERAGE_CONTROL_TIME", totalTime.floatValue() / totalTubesCount);
                    } else {
                        parameters.put("OVERAGE_CONTROL_TIME", 0F);
                    }
                    InputStream is = null;
                    JDialog reportDialog = new JDialog(mFrm, true);
                    JRViewer jRViewer = null;
                    try {
                        is = compiledReport.openStream();
                        jasperPrint = JasperFillManager.fillReport(is, parameters, jrDataSource);
                        jRViewer = new JRViewer(jasperPrint);
                        jRViewer.setZoomRatio(0.5f);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Ошибка доступа к отчету: " + ex.getLocalizedMessage());
                        progressDialog.setVisible(false);
                        reportDialog.dispose();
                    } catch (JRException ex) {
                        JOptionPane.showMessageDialog(null, "Ошибка заполнения отчета: " + ex.getLocalizedMessage());
                        progressDialog.setVisible(false);

                        reportDialog.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Ошибка заполнения отчета: " + ex.getLocalizedMessage());
                        ex.printStackTrace();
                        progressDialog.setVisible(false);
                        reportDialog.dispose();
                    }
                    reportDialog.add(jRViewer);
                    reportDialog.setBounds(0, 0, 800, 550);
                    reportDialog.setLocationRelativeTo(MainFrame.this);
                    progressDialog.setVisible(false);
                    reportDialog.setVisible(true);
                    reportDialog.dispose();
                    return 0;
                }
            });

        } else {//Нет данных для вывода отчета
            JOptionPane.showMessageDialog(
                    null,
                    "Нет данных для вывода отчета. \n "
                    + "Убедитесь, что таблица с результатами запонена. \n"
                    + "Если результаты отсутствуют, проверьте правильность "
                    + "задания критериев поиска в базе.",
                    "Уведомление",
                    INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_button_GetTotalReportActionPerformed

    private void button_RepeatDefectDetectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_RepeatDefectDetectionActionPerformed
        //Отключаем кнопки выбора.
        button_EnableVerdict.setEnabled(false);
        button_RepeatDefectDetection.setEnabled(false);
        //Отправляем комманду контроллеру, что необходимо повторить проверку.
        sendCmdToPLC(Commands.REPEAT_DEFECT_DETECTION);

    }//GEN-LAST:event_button_RepeatDefectDetectionActionPerformed

    private void button_graphPerPageReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_graphPerPageReportActionPerformed
        createGraphPerPageReport();
    }//GEN-LAST:event_button_graphPerPageReportActionPerformed

    private void jTable_ArchiveResultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_ArchiveResultsMouseClicked

    }//GEN-LAST:event_jTable_ArchiveResultsMouseClicked

    private void menuItem_EnableLoggingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_EnableLoggingActionPerformed
        if (log.isDebugEnabled()) {
            //TODO
        } else {
            //TODO
        }
    }//GEN-LAST:event_menuItem_EnableLoggingActionPerformed

    private void button_DropTubesCounterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_DropTubesCounterActionPerformed
        tubesCounter.dropCounter();
    }//GEN-LAST:event_button_DropTubesCounterActionPerformed

    private void jButton_AllGraphsOnOnePageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_AllGraphsOnOnePageActionPerformed
        createGraphsOnOnePage();
    }//GEN-LAST:event_jButton_AllGraphsOnOnePageActionPerformed

    private void button_EnableVerdictActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_EnableVerdictActionPerformed
        String selectedCondition = (String) comboBox_TubeConditions.getSelectedItem();
        int tubeCondition;
        String uiConditionLabel;
        int plcCommand;
        switch (selectedCondition) {
            case "Годная":
                tubeCondition = TubeConditions.GOOD;
                uiConditionLabel = t("good");
                plcCommand = Commands.MARK_AS_GOOD;
                break;
            case "Годная ксласс 2":
                tubeCondition = TubeConditions.GOOD_CLASS_2;
                uiConditionLabel = t("googClass2");
                plcCommand = Commands.MARK_AS_GOOD;
                break;
            case "Рем. класс 2":
                tubeCondition = TubeConditions.GOOD_REAPAIR_CLASS_2;
                uiConditionLabel = t("googRepairClass2");
                plcCommand = Commands.MARK_AS_GOOD;
                break;
            default://Брак
                tubeCondition = TubeConditions.BAD;
                uiConditionLabel = t("defect");
                plcCommand = Commands.MARK_AS_BAD;
        }
        updateTubesTable(uiConditionLabel);
        saveOperatorChoiceToDb(tubeCondition);
        notifyController(plcCommand);
    }//GEN-LAST:event_button_EnableVerdictActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        int selectedTabIndex = jTabbedPane1.getSelectedIndex();
        switch (selectedTabIndex) {
            case 1:
                if (!mdSettingsIsEnabled && isPassCheckOk() != null) {
                    setMdSettingsEnabledState(true);
                }
                break;
            case 2:
                if (!uskSettingsIsEnabled && isPassCheckOk() != null) {
                    setUskSettingsEnabledState(true);
                }
                break;
            default:
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jButton_saveUskSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_saveUskSettingsActionPerformed
        saveUSKParams(blockUSK1);
        blockUSK1.resetChangeFlag();
        saveUSKParams(blockUSK2);
        blockUSK2.resetChangeFlag();
        prmUSEditPnl.resetChanges();
        setUskSettingsEnabledState(false);
    }//GEN-LAST:event_jButton_saveUskSettingsActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        if (isPassCheckOk() != null) {
            if (newPass == null) {
                this.newPass = new Dialog_changePass(MainFrame.this, true, emf, restrictedAccessDialog.getOperator());
            }
            newPass.setLocationRelativeTo(MainFrame.this);
            newPass.setVisible(true);
            if (newPass.getReturnStatus() == 1) {
                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Пароль изменен успешно.",
                        "Уведомление",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem_createAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_createAdminActionPerformed
        if (isPassCheckOk() != null) {
            if (dialog_createAdmin == null) {
                dialog_createAdmin = new Dialog_createAdmin(MainFrame.this, true, emf);
            }
            dialog_createAdmin.setLocationRelativeTo(MainFrame.this);
            dialog_createAdmin.setVisible(true);
            switch (dialog_createAdmin.getReturnStatus()) {
                case Dialog_createAdmin.RET_OK:
                    JOptionPane.showMessageDialog(
                            this.getParent(),
                            "Новый администратор создан успешно.",
                            "Уведомление",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                case Dialog_createAdmin.RET_ERROR:
                    JOptionPane.showMessageDialog(
                            this.getParent(),
                            "Не получилось создать нового администратора.",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                default: //do nothing
            }
        }
    }//GEN-LAST:event_jMenuItem_createAdminActionPerformed

    private void saveOperatorChoiceToDb(int tubeCondition) {
        EntityManager em = emf.createEntityManager();
        //Сначала получаем последние результаты проверки по трбуе из базы
        BasaTube tubeOnDevice = null;
        try {
            tubeOnDevice = em.find(BasaTube.class, tmn.getDetail(Devicess.ID_R4, Device.DEFAULT_VALUE));
            EntityTransaction trans = em.getTransaction();
            //Задаем новый статус трубы
            tubeOnDevice.setStatus(tubeCondition);
            //Сохраянем данные базу
            try {
                trans.begin();
                em.merge(tubeOnDevice);
                trans.commit();
            } finally {
                if (trans.isActive()) {
                    trans.rollback();
                }
            }
        } catch (Exception ex) {
            log.error("Can't save operator choice for pipe {}.", tubeOnDevice != null ? tubeOnDevice.getId() : null, ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private void updateTubesTable(String uiConditionLabel) {
        //Отключаем кнопки выбора.
        button_EnableVerdict.setEnabled(false);
        button_RepeatDefectDetection.setEnabled(false);
        //Обновляем список труб
        ((DefaultTableModel) table_Shift_Tubes.getModel()).setValueAt(uiConditionLabel, 0, PIPE_STATE_COLUMN);
    }

    private void notifyController(int plcCommand) {
        //Отправляем комманду контроллеру о состоянии трубы.
        sendCmdToPLC(plcCommand);
    }

    private void createlengthWiseGraph() {
        //Если есть данные для вывода отчета
        if (archResults != null && !archResults.isEmpty()) {
            //Имя файла скомпилированного отчёта, готового для заполнения.
            URL compiledReport = getClass().getResource("Resource/LengthwiseReport.jasper");

            if (compiledReport == null) {
                JOptionPane.showMessageDialog(
                        null,
                        "Шаблон отчета не найден. \nНеправильно откомпилировали программу.",
                        "Ошибка",
                        ERROR_MESSAGE);
                return;
            }
            //Получаем индекс выделенной строчки
            int selectedRowIndex = jTable_ArchiveResults.getSelectedRow();
            if (!(selectedRowIndex < 0) && !(selectedRowIndex >= archResults.size())) {
                //Оборачиваем TableModel для использования в отчёте
                JRDataSource jrDataSource;
                //Получаем тип трубы
                TubeType tubeType = ((UEParams) tmn.getParam(Devicess.ID_R4)).tubeTypes.get(toIntExact(archResults.get(selectedRowIndex).getTypeID()) - 1);
                //Получаем границу
                jrDataSource = new JRDataSourceUSKLengthWiseResults(
                        archResults.get(selectedRowIndex).getTubeResults().get(0).usk1Res,
                        tubeType
                );

                //Сюда положим параметры для отчёта
                Map<String, Object> parameters = new HashMap<>();

                parameters.put("TUBE_NUMBER", archResults.get(selectedRowIndex).getId());
                parameters.put("CONTROL_TIME", new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(archResults.get(selectedRowIndex).getDateCreate()));
                InputStream is = null;
                try {
                    is = compiledReport.openStream();
                    jasperPrint = JasperFillManager.fillReport(is, parameters, jrDataSource);
                    JasperViewer.viewReport(jasperPrint, false, Locale.getDefault());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка доступа к отчету: " + ex.getLocalizedMessage());
                } catch (JRException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка заполнения отчета: " + ex.getLocalizedMessage());
                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Выберите трубу по которой хотите получить детальный отчет.",
                        "Уведомление",
                        INFORMATION_MESSAGE
                );
            }
        } else {//Нет данных для вывода отчета
            JOptionPane.showMessageDialog(
                    null,
                    "Нет данных для вывода отчета. \n "
                    + "Убедитесь, что таблица с результатами запонена. \n"
                    + "Если результаты отсутствуют, проверьте правильность "
                    + "задания критериев поиска в базе.",
                    "Уведомление",
                    INFORMATION_MESSAGE);
        }

    }

    /**
     * Изменяет состояние JToggleButton на противоположное тому кторое находится
     * в PLC. JToggleButton.getName() должно соостветствоовать имени выхода в
     * PLC/.
     *
     * @param evt JToggleButton у кторого необходимо изменить статус.
     */
    private void simpleJToggleButtonHandler(
            ActionEvent evt,
            String pressedButtonText,
            String freeButtonText) {
        if (evt.getSource() instanceof JToggleButton) {
            try {
                //Имя источника события.
                String eventSourceName = ((JToggleButton) evt.getSource()).getName();
                //Обновляем значения входов из контроллера.
                plc.readOutputs(eventSourceName, 1);
                //Устанваливаем состояния выходя обратное текущему.
                plc.setOuputState(
                        eventSourceName,
                        !plc.getOuputState(eventSourceName)
                );
                //Отправляем данные в контроллер.
                plc.writeData();
                //Меняем надпись на кнопке в зависимости от ее состояния.
                if (plc.getOuputState(eventSourceName)) {
                    ((JToggleButton) evt.getSource()).setText(pressedButtonText);
                } else {
                    ((JToggleButton) evt.getSource()).setText(freeButtonText);

                }
            } catch (ModbusException ex) {
                log.error("Can't toggle button state to PLC with IP [{}]", PLC_IP, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //Устанавливает старую тему при запуске для JFreeCharts,
        //так как JasperReports делает это при открытии и создается
        //эффект хаотичной смены оформления.
        //Если это вам ни о чем не говорит, то просто выполните эту строчку
        //перед созданием GUI в программах использующих JFreeCharts и JasperReports
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
            } catch (Throwable e) {
                System.exit(-1);
            }
        });
    }

    /**
     * Отправляет драйверам настройки для текущего типа трубы.
     */
    private void sendDriversSettingsForCurrentTubeType() {
//Получаем текущие параметры установки
        UEParams prm = (UEParams) tmn.getParam(Devicess.ID_R4);
        //Получаем текущие параметры магнитки
        ParamsMD8Udp mdp = prm.currentTubeType.getParamsMD();
        //Отправляем текущие параметры в драйвер магнитки
        blockMD.setParam(mdp);
        //Получаем текущие параметры УЗК1
        DeviceUSKUdpParams udp1 = prm.currentTubeType.getParamsUSK1();
        //Отправляем текущие параметрв в драйвер УЗК1
        blockUSK1.setParams(udp1);
        //Получаем текущие параметры УЗК2
        DeviceUSKUdpParams udp2 = prm.currentTubeType.getParamsUSK2();
        //ОТправляем текущие парамтеры в драйвер УЗК2
        blockUSK2.setParams(udp2);
        //Обновляем параметры на панеле Магнитки
        prmMDEditPnl.restoreParams(mdp);
        //Утснавливаем активный канал, чтобы это не значило
        prmUSEditPnl.setActiveChan();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel USD;
    private javax.swing.JToggleButton button_Carriage_Pump_on;
    private javax.swing.JButton button_CreateNewCustomer;
    private javax.swing.JToggleButton button_Demagnetization_Coil_On;
    private javax.swing.JButton button_DropTubesCounter;
    private javax.swing.JToggleButton button_Dryer_On;
    private javax.swing.JButton button_EnableVerdict;
    private javax.swing.JButton button_GetArchiveData;
    private javax.swing.JButton button_GetTotalReport;
    private javax.swing.JToggleButton button_Holder_1_Down;
    private javax.swing.JToggleButton button_Holder_2_Down;
    private javax.swing.JToggleButton button_Holder_3_Down;
    private javax.swing.JToggleButton button_Magnetic_Coil_On;
    private javax.swing.JToggleButton button_Magnetic_Heads_Close;
    private javax.swing.JToggleButton button_Pumping_Out_On;
    private javax.swing.JToggleButton button_Reloader_1_Up;
    private javax.swing.JToggleButton button_Reloader_2_Up;
    private javax.swing.JButton button_RepeatDefectDetection;
    private javax.swing.JToggleButton button_Rollgang_FWD;
    private javax.swing.JToggleButton button_Rollgang_REV;
    private javax.swing.JButton button_Shift;
    private javax.swing.JButton button_Stop_Rollgang;
    private javax.swing.JToggleButton button_USD_Carriage_Up;
    private javax.swing.JToggleButton button_Wetter_Close;
    private javax.swing.JButton button_graphPerPageReport;
    private javax.swing.JCheckBox checkBox_GoodAutohandle;
    private javax.swing.JComboBox<String> comboBox_TubeConditions;
    private javax.swing.JComboBox combobox_CustomerSelection;
    private javax.swing.JComboBox combobox_TubeOnDeviceResults;
    private javax.swing.JButton frBtSave;
    private javax.swing.JButton jButton_AllGraphsOnOnePage;
    private javax.swing.JButton jButton_saveUskSettings;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox13;
    private javax.swing.JCheckBox jCheckBox14;
    private javax.swing.JCheckBox jCheckBox15;
    private javax.swing.JCheckBox jCheckBox16;
    private javax.swing.JCheckBox jCheckBox17;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JCheckBox jCheckBox_CalculateSideThickness;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_DatePickerTitle;
    private javax.swing.JLabel jLabel_ReportCreationDuration;
    private javax.swing.JLabel jLabel_ReportCreationDurationValue;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jMenuItem_createAdmin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_Magnetic;
    private javax.swing.JPanel jPanel_Tansport;
    private javax.swing.JPanel jPanel_Ultrasonic;
    private javax.swing.JPanel jPanel_UnitFigure;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable_ArchiveResults;
    private javax.swing.JLabel label_AverageTubeControllTime;
    private javax.swing.JLabel label_AverageTubeControllTimeValue;
    private javax.swing.JLabel label_BadTubes;
    private javax.swing.JLabel label_BadTubesCount;
    private javax.swing.JLabel label_BadTubesCountValue;
    private javax.swing.JLabel label_BadTubesLength;
    private javax.swing.JLabel label_BadTubesLengthValue;
    private javax.swing.JLabel label_BadTubesValue;
    private javax.swing.JLabel label_ErrorState;
    private javax.swing.JLabel label_ErrorStateValue;
    private javax.swing.JLabel label_ErrorTabCaption;
    private javax.swing.JLabel label_GoodTubes;
    private javax.swing.JLabel label_GoodTubesCount;
    private javax.swing.JLabel label_GoodTubesCountValue;
    private javax.swing.JLabel label_GoodTubesLength;
    private javax.swing.JLabel label_GoodTubesLengthValue;
    private javax.swing.JLabel label_GoodTubesValue;
    private javax.swing.JLabel label_Operator;
    private javax.swing.JLabel label_OperatorValue;
    private javax.swing.JLabel label_State;
    private javax.swing.JLabel label_StateValue;
    private javax.swing.JLabel label_TotalLength;
    private javax.swing.JLabel label_TotalTubesCount;
    private javax.swing.JLabel label_TotalTubesCountValue;
    private javax.swing.JLabel label_TotalTubesLengthValue;
    private javax.swing.JLabel label_TotalTubesValue;
    private javax.swing.JLabel label_TubeTotall;
    private javax.swing.JLabel label_TubeType;
    private javax.swing.JLabel label_TybeTypeValue;
    private javax.swing.JLabel label_xCoordinate;
    private javax.swing.JLabel label_xCoordinateValue;
    private javax.swing.JPanel magDef;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JMenuItem menuItem_AddGraph;
    private javax.swing.JMenuItem menuItem_EnableLogging;
    private javax.swing.JMenuItem menuItem_Settings;
    private javax.swing.JMenu menu_Drivers;
    private javax.swing.JPanel panel_Archive;
    private javax.swing.JPanel panel_Errors;
    private javax.swing.JPanel panel_Tunning;
    private javax.swing.JPanel pnGrfsAll;
    private javax.swing.JPanel pnGrfsMD;
    private javax.swing.JPanel pnGrfsUSK;
    private javax.swing.JPanel pnNastr;
    private javax.swing.JPanel pnNastrUSK;
    private javax.swing.JPanel pnTubeTbl;
    private javax.swing.JScrollPane scrollPane_ForErrorsTable;
    private javax.swing.JMenuItem sortoscopeMenu;
    private ru.npptmk.bazaTest.defect.TableDatePicker table_DatePicker;
    private javax.swing.JTable table_Shift_Tubes;
    private javax.swing.JPanel tube;
    private javax.swing.JMenuItem tubeTypesMenu;
    // End of variables declaration//GEN-END:variables

    @Override
    public int getTubeLength() {
        return tubeLength;
    }

    @Override
    public String getTubeType() {
        TubeType tt = ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType;
        if (tt != null) {
            return tt.toString();
        }
        return null;
    }

    @Override
    public float[] getTubeThicks() {
        float[] res = new float[3];
        TubeType tt = ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType;
        for (int i = 0; i < res.length; i++) {
            res[i] = tt.getThickClassBorderValue(ThickClasses.values()[i]);
        }
        return res;
    }

    @Override
    public float[] getDefects(int ch) {
        TubeType ctt = ((UEParams) tmn.getParam(Devicess.ID_R4)).currentTubeType;
        ArrayList<Float> cdf = new ArrayList<>();
        float[] x = new float[1500];
        float[] y = new float[1500];
        switch (ch) {
            case 0:
                // Магнитные дефекты.
                ParamsMD8Udp prmd = blockMD.getParam();
                for (int i = 0; i < 8; i++) {
                    int lg = blockMD.getGrafic(i, x, y);
                    for (int j = 0; j < lg; j++) {
                        if (y[j] > prmd.porog[i]) {
                            cdf.add(x[j]);
                        }
                    }
                }
                break;
            case 1:
                // Узк дефекты.
                DeviceUSKUdpParams prmus = blockUSK1.getParams();
                for (int i = 0; i < 8; i++) {
                    if (prmus.prms[i].getHardware_type() == DeviceUSKUdpParam.FLAW_CONTROL) {
                        short porog = prmus.prms[i].getA_thresh();
                        int lg = blockUSK1.getGrafic(i, x, y);
                        for (int j = 0; j < lg; j++) {
                            if (y[j] > porog) {
                                cdf.add(x[j]);
                            }
                        }
                    }
                }
                prmus = blockUSK2.getParams();
                for (int i = 0; i < 8; i++) {
                    if (prmus.prms[i].getHardware_type() == DeviceUSKUdpParam.FLAW_CONTROL) {
                        short porog = prmus.prms[i].getA_thresh();
                        int lg = blockUSK2.getGrafic(i, x, y);
                        for (int j = 0; j < lg; j++) {
                            if (y[j] > porog) {
                                cdf.add(x[j]);
                            }
                        }
                    }
                }
                break;
            case 2:
                // Дефекты толщины.
                prmus = blockUSK1.getParams();
                for (int i = 0; i < 8; i++) {
                    if (prmus.prms[i].getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
                        int lg = blockUSK1.getGrafic(i, x, y);
                        for (int j = 0; j < lg; j++) {
                            if ((y[j] > 1f) && ctt.getThickClassByThickValue(y[j]) == ThickClasses.GARBAGE) {
                                cdf.add(x[j]);
                            }
                        }
                    }
                }
                prmus = blockUSK2.getParams();
                for (int i = 0; i < 8; i++) {
                    if (prmus.prms[i].getHardware_type() == DeviceUSKUdpParam.TIME_CONTROL) {
                        int lg = blockUSK2.getGrafic(i, x, y);
                        for (int j = 0; j < lg; j++) {
                            if ((y[j] > 1f) && ctt.getThickClassByThickValue(y[j]) == ThickClasses.GARBAGE) {
                                cdf.add(x[j]);
                            }
                        }
                    }
                }
                break;
        }
        float[] res = new float[cdf.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = cdf.get(i);
        }
        return res;
    }

    /**
     * Создает новую трубу и помещает ее на выбранное устройство.
     *
     * @param deviceName имя устройства на которое необходимо поместить трубу.
     */
    public void newTube(String deviceName) {

    }

    /**
     * Обновляет состояние кнопок на панели. Нажатия кнопок реализованы
     * слушателями нажатий на кнопки. Здесь нажатия на кнопки не обрабатываются.
     *
     * @param panel
     */
    public void updateTunePanel(JPanel panel) {
        try {
            //Перебераем кнопки на панели.
            for (Component component : panel.getComponents()) {
                //Если компонент панель, то вызываем метод рекурсивно.
                if (component instanceof JPanel) {

                    updateTunePanel((JPanel) component);
                    continue;
                }

                //Если компонент это кнопка переключатель (JToggleButton).
                if (component instanceof JToggleButton
                        && !(component instanceof JCheckBox)) {
                    ((JToggleButton) component).setSelected(plc.getOuputState(component.getName()));
                    if (mode == 2) {
                        component.setEnabled(true);
                    } else {
                        component.setEnabled(false);
                    }
                    continue;
                }

                if (component instanceof JButton) {
                    if (mode == 2) {
                        component.setEnabled(true);
                    } else {
                        component.setEnabled(false);
                    }
                    continue;
                }

                //Если компонент это (JCheckBox)
                if (component instanceof JCheckBox) {
                    ((JCheckBox) component).setSelected(plc.getInputState(component.getName()));
                }
            }
        } catch (ModbusException ex) {
            log.error("Can't update pipe panel from PLC with IP [{}]", PLC_IP, ex);
        }
    }

    static public String getChanName(int idDev, int i) {
        if (idDev == Devicess.ID_USK1.intValue()) {
            if (i <= 5) {
                return "Прод." + (i + 1) + " - Канал 1." + (i + 1);
            } else {
                return "Попер." + (i - 5) + " - Канал 1." + (i + 1);
            }
        }
        if (idDev == Devicess.ID_USK2.intValue()) {
            if (i <= 3) {
                return "Попер." + (i + 3) + " - Канал 2." + (i + 1);
            } else {
                return "Толщ." + (i - 3) + " - Канал 2." + (i + 1);
            }
        }
        return "";

    }

    /**
     * Объект, используемый для выполнения процесса обновления стредств
     * отображения в фоновом потоке.
     */
    private class GrafsUpdater implements Runnable {

        private final IScanDataProvider driver;
        private final PanelForGraphics pn;

        public GrafsUpdater(IScanDataProvider driver, PanelForGraphics pn) {
            this.driver = driver;
            this.pn = pn;
        }

        @Override
        public void run() {
            pn.updateGrafs(driver);
        }
    }

    /**
     * Объект, используемый для выполнения процесса обновления стредств
     * отображения в фоновом потоке.
     */
    private class MDGrafsUpdater implements Runnable {

        private final IScanDataProvider dr3;
        private final PanelForGraphics pn;

        public MDGrafsUpdater(IScanDataProvider dr3, PanelForGraphics pn) {

            this.dr3 = dr3;
            this.pn = pn;
        }

        @Override
        public void run() {
            pn.updateGrafs(dr3);
        }
    }

    /**
     * Объект, используемый для выполнения процесса обновления стредств
     * отображения в фоновом потоке.
     */
    private class USKGrafsUpdater implements Runnable {

        private final IScanDataProvider dr1;
        private final IScanDataProvider dr2;
        private final PanelForGraphics pn;

        public USKGrafsUpdater(IScanDataProvider dr1, IScanDataProvider dr2, PanelForGraphics pn) {
            this.dr1 = dr1;
            this.dr2 = dr2;
            this.pn = pn;
        }

        @Override
        public void run() {
            pn.updateGrafs(dr1);
            pn.updateGrafs(dr2);
        }
    }

    /**
     * Объект, используемый для выполнения процесса обновления стредств
     * отображения в фоновом потоке.
     */
    private class AScanUpdater implements Runnable {

        private final DeviceUSKUdp driver;
        private final DeviceUSKUdpParamPanel pn;

        public AScanUpdater(DeviceUSKUdp driver, DeviceUSKUdpParamPanel pn) {
            this.driver = driver;
            this.pn = pn;
        }

        @Override
        public void run() {
            pn.updateAScan(driver);
        }
    }

}
