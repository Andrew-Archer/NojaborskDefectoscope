package ru.npptmk.bazaTest.defect;


import ru.npptmk.bazaTest.defect.resultsvalidation.ResultsValidator;
import ru.npptmk.bazaTest.defect.resultsvalidation.ResultsValidatorsManager;
import java.util.*;
import java.util.stream.Collectors;

public class ResultsValidatorsManagerImpl implements ResultsValidatorsManager {

    private final Map<String, ResultsValidator> validators;

    public ResultsValidatorsManagerImpl() {
        validators = new HashMap<>();
    }

    @Override
    public synchronized void addResultsValidators(List<ResultsValidator> validators) throws IllegalStateException {
        validators.stream().map(ResultsValidator::getName).forEach((name) -> {
            if (this.validators.containsKey(name)) {
                throw new IllegalStateException(String.format("Manager already contains %s validator", name));
            }
        });

        this.validators.putAll(validators.stream()
                .collect(Collectors.toMap(ResultsValidator::getName, validator -> validator)));
    }


    @Override
    public synchronized void removeResultsValidators(List<String> names) {
        names.forEach(validators::remove);
    }

    @Override
    public synchronized void addResultsValidator(ResultsValidator validator) throws IllegalStateException {
        addResultsValidators(Collections.singletonList(validator));
    }

    @Override
    public void removeResultsValidator(String name) {
        removeResultsValidators(Collections.singletonList(name));
    }

    @Override
    public void validateResults(BazaTubeResult result) throws ValidationException {
        for (ResultsValidator resultsValidator : validators.values()) {
            if (!resultsValidator.test(result)) {
                throw new ValidationException(String.format("Results validation failed by %s validator, because: %s",
                        resultsValidator.getName(), resultsValidator.getFailConditionDescription()));
            }
        }
    }

    @Override
    public Set<String> getValidatorsNamesSet() {
        return validators.keySet();
    }

}
