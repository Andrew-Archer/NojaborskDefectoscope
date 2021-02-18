/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author razumnov
 */
public class ResultsValidatorsManagerImplTest {

    public ResultsValidatorsManagerImplTest() {
    }

    /**
     * Test of addResultsValidators method, of class
     * ResultsValidatorsManagerImpl.
     */
    @Test(expected = IllegalStateException.class)
    public void testAddResultsValidators() {
        System.out.println("addResultsValidators");
        String failConditionDescription = "desc";
        ResultsValidator resultsValidator = new ResultsValidator("testValidator", results -> true, failConditionDescription);
        List<ResultsValidator> validators = Collections.singletonList(resultsValidator);
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();
        instance.addResultsValidators(validators);

        assertEquals("testValidator", instance.getValidatorsNamesSet().iterator().next());
        assertEquals(1, instance.getValidatorsNamesSet().size());
        instance.addResultsValidators(validators);
    }

    /**
     * Test of removeResultsValidators method, of class
     * ResultsValidatorsManagerImpl.
     */
    @Test
    public void testRemoveResultsValidators() {
        System.out.println("removeResultsValidators");
        String failConditionDescription = "desc";
        ResultsValidator resultsValidator = new ResultsValidator("testValidator", results -> true, failConditionDescription);
        List<ResultsValidator> validators = Collections.singletonList(resultsValidator);
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();
        instance.addResultsValidators(validators);


        instance.removeResultsValidators(validators.stream().map(ResultsValidator::getName).collect(Collectors.toList()));
        assertEquals(0, instance.getValidatorsNamesSet().size());
    }

    /**
     * Test of addResultsValidator method, of class
     * ResultsValidatorsManagerImpl.
     */
    @Test(expected = IllegalStateException.class)
    public void testAddResultsValidator() {
        System.out.println("addResultsValidator");
        String failConditionDescription = "desc";
        ResultsValidator validator = new ResultsValidator("testValidator", results -> true, failConditionDescription);
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();
        instance.addResultsValidator(validator);

        assertEquals("testValidator", instance.getValidatorsNamesSet().iterator().next());
        assertEquals(1, instance.getValidatorsNamesSet().size());
        instance.addResultsValidator(validator);
    }

    /**
     * Test of removeResultsValidator method, of class
     * ResultsValidatorsManagerImpl.
     */
    @Test
    public void testRemoveResultsValidator() {
        System.out.println("removeResultsValidator");
        String failConditionDescription = "desc";
        ResultsValidator resultsValidator = new ResultsValidator("testValidator", results -> true, failConditionDescription);
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();

        instance.removeResultsValidator(resultsValidator.getName());
        assertEquals(0, instance.getValidatorsNamesSet().size());
    }

    /**
     * Test of validateResults method, of class ResultsValidatorsManagerImpl.
     */
    @Test(expected = ValidationException.class)
    public void testValidateResults() throws Exception {
        System.out.println("validateResults");
        int numberOfExceptions = 0;
        final int expectedNumberOfExceptions = 0;
        String failConditionDescription = "desc";
        ResultsValidator alwaysPassValidator = new ResultsValidator("alwaysPassValidator", results -> true, failConditionDescription);
        ResultsValidator alwaysFailValidator = new ResultsValidator("alwaysFailValidator", results -> false, failConditionDescription);
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();

        instance.addResultsValidator(alwaysPassValidator);
        try {
            instance.validateResults(null);
        } catch (ValidationException ex) {
            numberOfExceptions++;
        }
        assertEquals(expectedNumberOfExceptions, numberOfExceptions);

        instance.addResultsValidator(alwaysFailValidator);

        instance.validateResults(null);
    }

    /**
     * Test of getValidatorsNamesSet method, of class
     * ResultsValidatorsManagerImpl.
     */
    @Test
    public void testGetValidatorsNamesSet() {
        System.out.println("getValidatorsNamesSet");
        String failConditionDescription = "desc";
        ResultsValidator alwaysPassValidator = new ResultsValidator("alwaysPassValidator", results -> true, failConditionDescription);
        ResultsValidator alwaysFailValidator = new ResultsValidator("alwaysFailValidator", results -> false, failConditionDescription);
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();
        List<ResultsValidator> validators = Arrays.asList(alwaysPassValidator, alwaysFailValidator);
        instance.addResultsValidators(validators);

        Set<String> expected = validators.stream().map(ResultsValidator::getName).collect(Collectors.toSet());
        assertEquals(expected, instance.getValidatorsNamesSet());
    }

}
