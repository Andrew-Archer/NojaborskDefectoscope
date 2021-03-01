/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.resultsvalidation;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.npptmk.bazaTest.defect.BazaTubeResult;

/**
 *
 * @author razumnov
 */
public class MinimumLengthResultsValidator extends ResultsValidator {

    private static final Logger log = LoggerFactory.getLogger(MinimumLengthResultsValidator.class);
    private int minimumGoodAreaLenght;
    

    public MinimumLengthResultsValidator(int minimumGoodAreaLenght) {
        super("Minimum length validator",
                "If there is an area on pipe with no defects and which length equal or"
                + "greater than minimum allowed then metod test return true.");
        this.minimumGoodAreaLenght = minimumGoodAreaLenght; 
    }
    /**
     * @return the minimumGoodAreaLenght
     */
    public int getMinimumGoodAreaLenght() {
        return minimumGoodAreaLenght;
    }
    /**
     * @param minimumGoodAreaLenght the minimumGoodAreaLenght to set
     */
    public void setMinimumGoodAreaLenght(int minimumGoodAreaLenght) {
        this.minimumGoodAreaLenght = minimumGoodAreaLenght;
    }

    @Override
    public boolean test(BazaTubeResult bazaTubeResults) {
        
                    log.debug("Start testing pipe.");
                    if(bazaTubeResults == null){
                        log.error("Pipe results is null.");
                        return false;
                    }
                    int pipeLengthInMm = bazaTubeResults.getTubeLength();
                    List<Integer> defectsPositions = bazaTubeResults.getAllDefectsPositions();
                    
                    if(defectsPositions.isEmpty()){
                        log.debug("Pipe has no deffects");
                        log.debug("Minimum good range length is [{}mm] and pipe length is [{}mm]", 
                                getMinimumGoodAreaLenght(), 
                                pipeLengthInMm);
                        return getMinimumGoodAreaLenght() <= pipeLengthInMm;
                    }
                    
                    int maxGoodRangeLength = 0;
                    int rangeStart = 0;
                    for (Integer rangeEnd : defectsPositions){
                        int rangeLength = rangeEnd - rangeStart;
                        if(maxGoodRangeLength < rangeLength){
                            maxGoodRangeLength = rangeLength;
                        }
                    }
                    log.debug("Minimum good range length is [{}mm] and actual maximun good range length is [{}mm]", 
                            getMinimumGoodAreaLenght(), 
                            maxGoodRangeLength);
                    return getMinimumGoodAreaLenght() <= maxGoodRangeLength;
    }
}
