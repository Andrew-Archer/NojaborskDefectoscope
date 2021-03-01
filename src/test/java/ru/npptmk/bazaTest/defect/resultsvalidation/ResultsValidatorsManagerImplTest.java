/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.resultsvalidation;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Test;
import ru.npptmk.bazaTest.defect.BazaTubeResult;
import ru.npptmk.bazaTest.defect.ResultsValidatorsManagerImpl;
import ru.npptmk.bazaTest.defect.ValidationException;

import static org.junit.Assert.*;

/**
 * @author razumnov
 */
public class ResultsValidatorsManagerImplTest {
    
    private static final ResultsValidator alwaysTrueValidator = new ResultsValidator("Always true validator", "Always valid."){
        @Override
        public boolean test(BazaTubeResult result) {
           return true;
        }
        
    };
    
    private static final ResultsValidator alwaysFalseValidator = new ResultsValidator("Always false validator", "Always invalid."){
        @Override
        public boolean test(BazaTubeResult result) {
           return false;
        }
        
    };

    public ResultsValidatorsManagerImplTest() {
    }

    /**
     * Test of addResultsValidators method, of class
     * ResultsValidatorsManagerImpl.
     */
    @Test(expected = IllegalStateException.class)
    public void testAddResultsValidators() {
        System.out.println("addResultsValidators");
        List<ResultsValidator> validators = Collections.singletonList(alwaysTrueValidator);
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();
        instance.addResultsValidators(validators);

        assertEquals("Always true validator", instance.getValidatorsNamesSet().iterator().next());
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
        List<ResultsValidator> validators = Collections.singletonList(alwaysTrueValidator);
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
        ResultsValidator validator = alwaysTrueValidator;
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();
        instance.addResultsValidator(validator);

        assertEquals("Always true validator", instance.getValidatorsNamesSet().iterator().next());
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
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();

        instance.removeResultsValidator(alwaysTrueValidator.getName());
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
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();

        instance.addResultsValidator(alwaysTrueValidator);
        try {
            instance.validateResults(null);
        } catch (ValidationException ex) {
            numberOfExceptions++;
        }
        assertEquals(expectedNumberOfExceptions, numberOfExceptions);

        instance.addResultsValidator(alwaysFalseValidator);

        instance.validateResults(null);
    }

    /**
     * Test of getValidatorsNamesSet method, of class
     * ResultsValidatorsManagerImpl.
     */
    @Test
    public void testGetValidatorsNamesSet() {
        System.out.println("getValidatorsNamesSet");
        ResultsValidator alwaysPassValidator = alwaysTrueValidator;
        ResultsValidator alwaysFailValidator = alwaysFalseValidator;
        ResultsValidatorsManagerImpl instance = new ResultsValidatorsManagerImpl();
        List<ResultsValidator> validators = Arrays.asList(alwaysPassValidator, alwaysFailValidator);
        instance.addResultsValidators(validators);

        Set<String> expected = validators.stream().map(ResultsValidator::getName).collect(Collectors.toSet());
        assertEquals(expected, instance.getValidatorsNamesSet());
    }

}
