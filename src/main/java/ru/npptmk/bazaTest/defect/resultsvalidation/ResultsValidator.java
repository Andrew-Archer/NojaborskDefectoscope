package ru.npptmk.bazaTest.defect.resultsvalidation;

import java.util.function.Predicate;
import ru.npptmk.bazaTest.defect.BazaTubeResult;

public abstract class ResultsValidator implements Predicate<BazaTubeResult> {
    private String name;
    private final String failConditionDescription;

    public ResultsValidator(String name, String failConditionDescription) {
        this.name = name;
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
    abstract public boolean test(BazaTubeResult result);

    public String getFailConditionDescription() {
        return "DefaultString";
    }
}
