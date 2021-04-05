/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.resultsvalidation;

import org.junit.Test;
import static org.junit.Assert.*;
import ru.npptmk.bazaTest.defect.BazaTubeResult;
import ru.npptmk.guiObjects.ITubeDataProvider;

/**
 *
 * @author razumnov
 */
public class ThicknessResultsValidatorTest {
    private static final ITubeDataProvider BAD_DATA_PROVIDER = new ITubeDataProvider() {
        private final float[][] defects = new float[][]{{0.1f, 0.2f}, {0.93f, 0.4f}};

        @Override
        public int getTubeLength() {
            return 6_000;
        }

        @Override
        public String getTubeType() {
            return "Test pipe";
        }

        @Override
        public float[] getTubeThicks() {
            return new float[]{1f, 2f};
        }

        @Override
        public float[] getDefects(int i) {
            return defects[i];
        }

        @Override
        public long getDeviceId() {
            return 0;
        }
    };
    
    private static final ITubeDataProvider GOOD_DATA_PROVIDER = new ITubeDataProvider() {
        private final float[][] defects = new float[][]{{1f, 2f}, {93f, 4f}};

        @Override
        public int getTubeLength() {
            return 6_000;
        }

        @Override
        public String getTubeType() {
            return "Test pipe";
        }

        @Override
        public float[] getTubeThicks() {
            return new float[]{5f, 5f};
        }

        @Override
        public float[] getDefects(int i) {
            return defects[i];
        }

        @Override
        public long getDeviceId() {
            return 0;
        }
    };

    private static final BazaTubeResult BAD_BAZA_TUBE_RESULT = new BazaTubeResult(
            BAD_DATA_PROVIDER,
            2);
    
    private static final BazaTubeResult GOOD_BAZA_TUBE_RESULT = new BazaTubeResult(
            GOOD_DATA_PROVIDER,
            2);
    public ThicknessResultsValidatorTest() {
    }


    @Test
    public void testTest() {
        ResultsValidator instance= new ThicknessResultsValidator(3);
        assertFalse(instance.test(BAD_BAZA_TUBE_RESULT));
        assertTrue(instance.test(GOOD_BAZA_TUBE_RESULT));
    }
    
}
