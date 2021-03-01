package ru.npptmk.bazaTest.defect.resultsvalidation;

import ru.npptmk.bazaTest.defect.BazaTubeResult;

public class ThicknessResultsValidator extends ResultsValidator {

    private int minimumThicknessMm;

    public ThicknessResultsValidator(int minimumThicknessMm) {
        super("Minimum thickness validator.", "If got even one place with"
                + "thickness less than minimal then return false on test"
                + "method execution.");
        this.minimumThicknessMm = minimumThicknessMm;
    }

    /**
     * @return the minimumThicknessMm
     */
    public int getMinimumThicknessMm() {
        return minimumThicknessMm;
    }

    /**
     * @param minimumThicknessMm the minimumThicknessMm to set
     */
    public void setMinimumThicknessMm(int minimumThicknessMm) {
        this.minimumThicknessMm = minimumThicknessMm;
    }

    @Override
    public boolean test(BazaTubeResult result) {
        final float[] thicknesses = result.getTubeThicks();
        for (float thickness : thicknesses) {
            if (thickness < minimumThicknessMm){
                return false;
            }
        }
        return true;
    }

}
