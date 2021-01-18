/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Элемент коллекции результатов исследования,трубы.
 * @author SmorkalovAV
 */
public class TubeResults {

    private static final long serialVersionUID = 259237582891602384L;
    private float begLength;
    private float endLength;
    /**Коллекция ид результатов исследования по установкам*/
    private HashMap<Long, Long> hsRes = new HashMap<>();
    /**Идентификатор последнего результата трубы*/
    public Long lastEvtId = null;
    /**Время возникновение последнего результата трубы*/
    public Date lastEvtTime = null;
    private String matGroup;
    /**два реза на трубе, ограничивают годную часть трубы
     * 0 элемент ближайший рез, 1 - дальний*/
    public List<Integer> resi;

    private float thickAll;
    private float thickFit;
    private int tubeClass;
    /**
     * На основе существующих результатов.
     * @param result существующие результаты.
     */
    public TubeResults(TubeResults result) {
        if(result instanceof TubeResults){
            TubeResults pr = result;
            begLength = pr.begLength;
            endLength = pr.endLength;
            hsRes = pr.hsRes;
            matGroup = pr.matGroup;
            resi = pr.resi;
            tubeClass = pr.tubeClass;
            thickAll = pr.thickAll;
            thickFit = pr.thickFit;
        }
    }
    /**
     * @return the begLength
     */
    public float getBegLength() {
        return begLength;
    }
    /**
     * @param begLength the begLength to set
     */
    public void setBegLength(float begLength) {
        this.begLength = begLength;
    }
    /**
     * @return the endLength
     */
    public float getEndLength() {
        return endLength;
    }
    /**
     * @param endLength the endLength to set
     */
    public void setEndLength(float endLength) {
        this.endLength = endLength;
    }
    /**
     * коллекция ид. результатов контроля 
     * @return коллекция
     */
    public HashMap<Long, Long> getHsRes() {
        return hsRes;
    }
    /**
     * @return the matGroup
     */
    public String getMatGroup() {
        return matGroup;
    }
    /**
     * @param matGroup the matGroup to set
     */
    public void setMatGroup(String matGroup) {
        this.matGroup = matGroup;
    }
    
    /**Возвращает ид. параметров события, 
     * отображающие результаты исследо вания трубы
     * @param idUe ил установки, на которой проводилось исследование
     * @return ид. результаты исследования
     */
    public Long getResultTube(Long idUe){
        return hsRes.get(idUe);
    }

    /**
     * @return the thickAll
     */
    public float getThickAll() {
        return thickAll;
    }
    /**
     * @param thickAll the thickAll to set
     */
    public void setThickAll(float thickAll) {
        this.thickAll = thickAll;
    }
    /**
     * @return the thickFit
     */
    public float getThickFit() {
        return thickFit;
    }
    /**
     * @param thickFit the thickFit to set
     */
    public void setThickFit(float thickFit) {
        this.thickFit = thickFit;
    }
    /**
     * @return the tubeClass
     */
    public int getTubeClass() {
        return tubeClass;
    }
    /**
     * @param tubeClass the tubeClass to set
     */
    public void setTubeClass(int tubeClass) {
        this.tubeClass = tubeClass;
    }
}
