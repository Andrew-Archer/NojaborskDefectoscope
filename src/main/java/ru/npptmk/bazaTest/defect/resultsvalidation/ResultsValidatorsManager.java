package ru.npptmk.bazaTest.defect.resultsvalidation;

import java.util.List;
import java.util.Set;
import ru.npptmk.bazaTest.defect.BazaTubeResult;
import ru.npptmk.bazaTest.defect.ValidationException;

/**
 * Used to decide is pipe bad or good. Contains a number of validators that
 * applied to defect detection results.
 *
 * @author razumnov
 *
 */
public interface ResultsValidatorsManager {

    /**
     * Return set of all added validators names.
     *
     * @return all validators names. Nullsafe. If no vaidator exist return enpty
     * map.
     */
    Set<String> getValidatorsNamesSet();

    /**
     * Add new results validator to be applied.
     *
     * @param validator validator to add.
     * @throws IllegalStateException if validator with given name already exist.
     */
    void addResultsValidator(ResultsValidator validator) throws IllegalStateException;

    /**
     * Add new results validators to be applied.
     *
     * @param validator validators to add.
     * @throws IllegalStateException if validator with given name already exist
     * and add not even one validators.
     */
    void addResultsValidators(List<ResultsValidator> validator) throws IllegalStateException;

    /**
     * Tries to delete validators by given names list. Skips not found
     * validators.
     *
     * @param names names of validators to delete.
     */
    void removeResultsValidators(List<String> names);

    /**
     * Tries to delete validator by given name. Skip not found validator.
     *
     * @param name name of validator to delete.
     */
    void removeResultsValidator(String name);

    /**
     * Check if results valid for whole validators set.
     *
     * @param result defect detection result to be validated.
     * @throws ValidationException on first validator fail.
     */
    void validateResults(BazaTubeResult result) throws ValidationException;

}
