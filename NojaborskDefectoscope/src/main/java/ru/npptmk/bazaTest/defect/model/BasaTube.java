/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.eclipse.persistence.annotations.Index;
import ru.npptmk.bazaTest.defect.BazaDefectResults;
import ru.npptmk.bazaTest.defect.Shift;
import ru.npptmk.bazaTest.defect.TubeConditions;

/**
 * Сущность описывает всю информацию о трубе в г.Урай
 *
 * @author SmorkalovAV
 */
@Entity
@NamedQueries({
    //Возвращает трубы проконтроллированные за смену
    @NamedQuery(name = "getSmena", query = "SELECT t FROM BasaTube t WHERE "
            + "t.uploadDate BETWEEN :beg AND :end ORDER BY t.uploadDate DESC")
    ,
    //Сумма длин труб, переданных ид. Коллекция не должна быть пустой.
    @NamedQuery(name = "getLenTubes", query = "select sum(t.lenTube) from BasaTube t where t.idTube in :tbs")
    ,
    //Максимальный индекс трубы в базе.
    @NamedQuery(name = "getMaxIdTube", query = "select max(t.idTube) from BasaTube t")
    ,
    //Возвращает трубы вышедшие из цеха за заданный период времени отсортировано по дате выгрузки из кармана.
    @NamedQuery(name = "getTubes", query = "select t from BasaTube t where t.dateRemove "
            + "between :beg and :end  order by t.dateRemove")
    ,
    //Возвращает трубы вышедшие из цеха за заданный период времени отсортировано по дате загрузки в карман.
    @NamedQuery(name = "getTubesToKrm", query = "select t from BasaTube t where t.dateToKrm "
            + "between :beg and :end  order by t.dateToKrm")
})
public class BasaTube implements Serializable {
    private static final ResourceBundle RB = ResourceBundle.getBundle("gui_text", new Locale("ru", "RU"));

    public static EntityManagerFactory emf;

    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    public BasaTube(
            Customer customer,
            Date dateCreate,
            Shift shift,
            Long tubeType,
            Date uploadDate
    ) {
        this.status = TubeConditions.NOT_CHECKED;
        this.route = 0;
        this.customer = customer;
        this.dateCreate = dateCreate;
        this.shift = shift;
        this.tubeType = tubeType;
        this.uploadDate = uploadDate;
    }

    public BasaTube() {
        this.status = TubeConditions.NOT_CHECKED;
        this.route = 0;

    }

    @Index
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateCreate;
    @Transient
    private String dateFormatted;

    @Index
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateRemove;

    @Index
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateToKrm;
    private String grTube;
    @Index
    private long idCreateEvt;
    @Index
    private long idEvtToKrm;

    @Index
    private long idRemoveEvt;
    @Id
    @TableGenerator(name = "TYBE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TYBE_SEQ")
    private long idTube;

    private String klTube;

    private float lenTube = 0;
    private String mark = null;

    @Index
    private int route;
    private boolean sample;
    @ManyToOne(fetch = FetchType.EAGER)
    private Shift shift;
    private int status;
    private Long tubeType;
    /**
     * Дата выгрузки трубы с дефектоскопа.
     */
    @Index
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "UPLOADDATE")
    private Date uploadDate;

    /**
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Время созания трубы
     *
     * @return
     */
    public Date getDateCreate() {
        return dateCreate;
    }

    /**
     * Время созания трубы
     *
     * @param dateCreate
     */
    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    /**
     * Возволяет длительность нахождения трубы на установке в секундах
     *
     * @return разница между врменем создания трубы и временем выгрузки с
     * установки в секундах.
     */
    public Long getControlDurationInSeconds() {
        if (dateCreate == null && uploadDate == null) {
            return 0L;
        } else {
            if (uploadDate.getTime() < dateCreate.getTime()) {
                return 0L;
            }
            return ((uploadDate.getTime() - dateCreate.getTime()) / 1000);
        }
    }

    public String getDateFormatted() {
        return String.format("%1$td.%1$tm.%1$ty %1$tH:%1$tM:%1$tS", dateRemove);
    }

    /**
     * Возвращает пометку того, что труба СОП.
     *
     * @return СОП если труба образец и пусту строку в противном случае.
     */
    public String isSampleToString() {
        return (isSample() ? " СОП" : "");
    }

    /**
     * Дата вывода трубы из цеха
     *
     * @return
     */
    public Date getDateRemove() {
        return dateRemove;
    }

    /**
     * Дата вывода трубы из цеха
     *
     * @param dateRemove
     */
    public void setDateRemove(Date dateRemove) {
        this.dateRemove = dateRemove;
    }

    public Date getDateToKrm() {
        return dateToKrm;
    }

    public void setDateToKrm(Date dateToKrm) {
        this.dateToKrm = dateToKrm;
    }

    /**
     * Группа прочности трубы или пометка, что труба бракованная.
     *
     * @return Группа прочности трубы или пометка, что труба бракованная.
     */
    public String getDurabilityGroup() {
        if (grTube == null) {
            return "нет";
        } else {
            return grTube;
        }
    }

    /**
     * Группа прочности трубы или пометка, что труба бракованная.
     *
     * @param grTube Группа прочночти трубы или пометка, что труба бракованная.
     */
    public void setGrTube(String grTube) {
        this.grTube = grTube;
    }

    /**
     * Идентификатор события созания трубы
     *
     * @return
     */
    public long getIdCreateEvt() {
        return idCreateEvt;
    }

    /**
     * Идентификатор события созания трубы
     *
     * @param idCreateEvt
     */
    public void setIdCreateEvt(long idCreateEvt) {
        this.idCreateEvt = idCreateEvt;
    }

    public long getIdEvtToKrm() {
        return idEvtToKrm;
    }

    public void setIdEvtToKrm(long idEvtToKrm) {
        this.idEvtToKrm = idEvtToKrm;
    }

    /**
     * Идентификатор события вывода трубы из цеха
     *
     * @return
     */
    public long getIdRemoveEvt() {
        return idRemoveEvt;
    }

    /**
     * Идентификатор события вывода трубы из цеха
     *
     * @param idRemoveEvt
     */
    public void setIdRemoveEvt(long idRemoveEvt) {
        this.idRemoveEvt = idRemoveEvt;
    }

    /**
     * Идентификатор сущности.
     *
     * @return Идентификатор сущности.
     */
    public long getId() {
        return idTube;
    }

    /**
     * Идентификатор сущности.
     *
     * @param idTube Идентификатор сущности.
     */
    public void setIdTube(long idTube) {
        this.idTube = idTube;
    }

    /**
     * Класс по толщине стенки трубы.
     *
     * @return Класс по толщине стенки трубы.
     */
    public String getKlTube() {
        return klTube;
    }

    /**
     * Класс по толщине стенки трубы.
     *
     * @param klTube Класс по толщине стенки трубы.
     */
    public void setKlTube(String klTube) {
        this.klTube = klTube;
    }

    /**
     * Позволяет получить длину трубы.
     *
     * @return длина трубы в метрах.
     */
    public float getLengthInMeters() {
        return ((int) (lenTube / 10)) / 100F;
    }

    /**
     * Длина трубы, мм
     *
     * @param lenTube
     */
    public void setLenTube(float lenTube) {
        this.lenTube = lenTube;
    }

    /**
     * Строка маркировки.
     *
     * @return Строка маркировки.
     */
    public String getMark() {
        return mark;
    }

    /**
     * Строка маркировки.
     *
     * @param mark Строка маркировки.
     */
    public void setMark(String mark) {
        this.mark = mark;
    }

    /**
     * Маршрут движения трубы (номер кармана)
     *
     * @return Маршрут движения трубы.
     */
    public int getRoute() {
        return route;
    }

    public void setSample(boolean smpl) {
        this.sample = smpl;
    }

    /**
     * @return the shift
     */
    public Shift getShift() {
        return shift;
    }

    /**
     * @param shift the shift to set
     */
    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public int getStatus() {
        return status;
    }

    /**
     * Возвращает результаты испытаний хранящиеся в базе данных.
     *
     * @return список результатов испытаний соответствующий данной трубе.
     */
    public List<BazaDefectResults> getTubeResults() {
        if (emf != null) {
            List<BazaDefectResults> result = null;
            EntityManager em = emf.createEntityManager();
            EntityTransaction trans = null;
            try {
                trans = em.getTransaction();
                trans.begin();
                em.createNamedQuery("Tuberesult.findById").setParameter("id", idTube);
                Tuberesult tr = em.find(Tuberesult.class, idTube);
                if (tr != null) {
                    result = (List<BazaDefectResults>) tr.getResult();
                } else {
                    result = new ArrayList<>();
                }
                trans.commit();
            } catch (Exception ex) {
                if (trans != null && trans.isActive()) {
                    trans.rollback();
                }
                ex.printStackTrace();
            } finally {
                if (em != null) {
                    em.close();
                }
            }
            return result;
        } else {
            System.out.println("Попытка сохранить резултаты трубы при не ининициализивованном"
                    + "статическом поле EntityManagerFactory в BasaTube");
            return null;
        }
    }

    /**
     * Устанавливает список результатов проверок трубы.
     *
     * @param defectDetectionResults список результатов проверок для установки.
     */
    public void setTubeResults(List<BazaDefectResults> defectDetectionResults) {
        if (emf != null) {
            EntityManager em = emf.createEntityManager();
            EntityTransaction trans = null;
            try {
                trans = em.getTransaction();
                trans.begin();
                Tuberesult resultToSave = new Tuberesult(defectDetectionResults, idTube);
                em.merge(resultToSave);
                trans.commit();
            } catch (Exception ex) {
                if (trans != null && trans.isActive()) {
                    trans.rollback();
                }
                ex.printStackTrace();
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        } else {
            System.out.println("Попытка сохранить резултаты трубы при не ининициализивованном"
                    + "статическом поле EntityManagerFactory в BasaTube");
        }
    }

    public String getStatusToString() {
        switch (status) {
            case 0:
                return "брак";
            case 1:
                return "годная";
            case 3:
                return RB.getString("googClass2");
            case 4:
                return RB.getString("googRepairClass2");
            default:
                return "";
        }
    }

    public Long getTypeID() {
        return tubeType;
    }

    public void setType(Long idTT) {
        this.tubeType = idTT;
    }

    /**
     * Возвращает дату выгрузки трубы с дефектоскопа.
     *
     * @return дата выгрузки трубы с дефектоскопа.
     */
    public Date getUploadDate() {
        return uploadDate;
    }

    /**
     * Задает дату выгрузки трубы с дефектоскопа.
     *
     * @param UploadDate дата выгрузки трубы с дефектоскопа.
     */
    public void setUploadDate(Date UploadDate) {
        this.uploadDate = UploadDate;
    }

    /**
     * <p>
     * Позволяет понять является ли труба образцом.</p>
     *
     * @return <tt>true</tt> если труба образец, <tt>false</tt> в противном
     * случае.
     */
    public boolean isSample() {
        return sample;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
