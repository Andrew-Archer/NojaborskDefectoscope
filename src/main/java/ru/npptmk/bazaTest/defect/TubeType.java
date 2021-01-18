/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import ru.npptmk.devices.USKUdp.DeviceUSKUdpParams;
import ru.npptmk.devices.md8Udp.ParamsMD8Udp;
import ru.npptmk.sortoscope.model.Diameter;

/**
 * Типоразмер трубы.
 *
 * @author MalginAS
 */
public class TubeType implements Serializable {
    private static final long serialVersionUID = -6271685085379088090L;

    /**
     * @return the paramsMD
     */
    public ParamsMD8Udp getParamsMD() {
        return paramsMD;
    }

    /**
     * @param paramsMD the paramsMD to set
     */
    public void setParamsMD(ParamsMD8Udp paramsMD) {
        this.paramsMD = paramsMD;
    }
    

    private Long idTubeType;
    
    protected String name;
    protected float diameter;
    protected float thick;
    private ParamsMD8Udp paramsMD;//Параметры электромагнитной дефектоскопии
    protected DeviceUSKUdpParams paramsUSK1;//Параметры ультразвуковой дефектоскопии первого блока
    protected DeviceUSKUdpParams paramsUSK2;//Параметры ультразвуковой дефектоскопии второго блока
    private Diameter paramsSort;//Параметры для сортоскопа.

    /**
     * Для хранения границ классов толщин. Setter должен обеспечивать хранение
     * толщин в порядке убывания. Например: 3мм - 1 класс 2мм - 2 класс 1мм - 3
     * класс
     */
    private final List<Float> thickClassesBorders;
    
    
    
    public DeviceUSKUdpParams getParamsUSK1(){
        return this.paramsUSK1;
    }
    
    public void setParamsUSK1(DeviceUSKUdpParams params){
        this.paramsUSK1 = params;
    }
    
        public DeviceUSKUdpParams getParamsUSK2(){
        return this.paramsUSK2;
    }
    
    public void setParamsUSK2(DeviceUSKUdpParams params){
        this.paramsUSK2 = params;
    }
    
    /**
     * Для создания типа трубы с параметрами для дефектоскопии
     *
     * @param id идентификатор типа трубы
     * @param nm Наименование типа трубы
     * @param dm Номинальный диаметр, мм
     * @param th толщина номиральная, мм
     */
    public TubeType(Long id, String nm, float dm, float th) {
        idTubeType = id;
        name = nm;
        diameter = dm;
        thick = th;
        thickClassesBorders = new ArrayList<>();
        
        for (ThickClasses thickClasses : ThickClasses.values()){
            thickClassesBorders.add(0f);
        }
        
    }

    public void setThickClassBorder(
            ThickClasses thickClass,
            Float thickValue) throws Exception {
        //Если уже есть более выскоий класс с меньшей толщиной.
        for (int i = 0; i < 3; i++) {
            if ((i < thickClass.ordinal()) && (thickClassesBorders.get(i) <= thickValue)) {
                throw new Exception("Не возможна ситуация когда "
                        + "у класса " + ThickClasses.values()[i] + " толщина " + thickClassesBorders.get(i) + "мм,"
                        + "а у класса " + thickClass + " толщина " + thickValue + "мм.");
            }
        }

        //Если все хорошо
        //Обновляем значения класса
        thickClassesBorders.set(thickClass.ordinal(), thickValue);
    }

    /**
     * Получить настройки сортоскопа.
     * 
     * @return 
     */
    public Diameter getSortoscopeSettings(){
        return this.paramsSort;
    }
    /**
     * Возвращает толщину в мм соотвтествующую заданному классу толщины.
     *
     * @param thickClass номер класса толщины от 1 - 3.
     * @return толщина соответствующая заданному классу толщины в мм.
     */
    public Float getThickClassBorderValue(ThickClasses thickClass) {
        return thickClassesBorders.get(thickClass.ordinal());
    }

    /**
     * На основании заданной толщины стенки трубы в мм, возвращает класс
     * толщиный трубы.
     *
     * @param thick толщина стенки в мм, заданная для определения класа
     * толщины.
     * @return элемент объединения {@code ThickClass}.
     */
    public ThickClasses getThickClassByThickValue(Float thick) {
        for (Float classesThick : thickClassesBorders) {
            if (classesThick <= thick && classesThick > 0) {
                return ThickClasses.values()[thickClassesBorders.indexOf(classesThick)];
            }

        }
        //В случае если толщина меньше всех трех
        //пороговых значений заданных в thickClassesBorders.
        return ThickClasses.GARBAGE;
    }


    /**
     * Идентификатор типа трубы
     *
     * @return идентификатор типа трубы
     */
    public Long getId() {
        return idTubeType;
    }

    /**
     * Идентификатор типа трубы
     *
     * @param id идентификатор типа трубы
     */
    public void setIdTubeType(Long id) {
        this.idTubeType = id;
    }

    /**
     * Получить наименование типоразмера (НКТ,БТ)
     *
     * @return Наименование типоразмера (НКТ,БТ)
     */
    public String getName() {
        return name;
    }

    /**
     * Задать наименование типоразмера.
     *
     * @param name наименование типоразмера
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получить диаметр трубы.
     *
     * @return Диаметр трубы.
     */
    public float getDiameter() {
        return diameter;
    }

    /**
     * Задать диаметр трубы.
     *
     * @param diameter диаметр трубы
     */
    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }

    /**
     * Получить номинальную толщину стенки трубы.
     *
     * @return Толщина стенки трубы.
     */
    public float getThick() {
        return thick;
    }

    /**
     * Задать номинальную толщину стенки трубы
     *
     * @param thick номинальная толщина стенки трубы
     */
    public void setThick(float thick) {
        this.thick = thick;
    }

    @Override
    public String toString() {
        return String.format("%s Ø%d*%.1f", name, (int)diameter, thick);
    }
    public static enum ThickClasses {
        CLASS_1, CLASS_2, CLASS_3, GARBAGE
    }

    /**
     * @return the paramsSort
     */
    public Diameter getParamsSort() {
        return paramsSort;
    }

    /**
     * @param paramsSort the paramsSort to set
     */
    public void setParamsSort(Diameter paramsSort) {
        this.paramsSort = paramsSort;
    }
    
}
