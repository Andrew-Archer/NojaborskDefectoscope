package ru.npptmk.bazaTest.defect;

import java.util.function.Predicate;
import ru.npptmk.bazaTest.defect.model.DefectTestResult;

public class ResultsValidator implements Predicate<DefectTestResult> {
    private String name;
    private final Predicate<DefectTestResult> predicate;
    private final String failConditionDescription;

    public ResultsValidator(String name, Predicate<DefectTestResult> predicate, String failConditionDescription) {
        this.name = name;
        this.predicate = predicate;
        this.failConditionDescription = failConditionDescription;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean test(DefectTestResult result) {
        return predicate.test(result);
    }

    public String getFailConditionDescription() {
        return "DefaultString";
    }
}
