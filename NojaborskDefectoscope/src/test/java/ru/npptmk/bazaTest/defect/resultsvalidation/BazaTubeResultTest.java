/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.resultsvalidation;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import ru.npptmk.bazaTest.defect.BazaTubeResult;
import static org.junit.Assert.*;
import ru.npptmk.guiObjects.ITubeDataProvider;

/**
 *
 * @author razumnov
 */
public class BazaTubeResultTest {

    List<Integer> expectedDefects = Arrays.asList(100, 200, 300, 400);

    @Test
    public void testGetAllDefectsPositions() {
        ITubeDataProvider dataProvider = new ITubeDataProvider() {
            private final float[][] defects = {{0.1f, 0.2f}, {0.3f, 0.4f}};

            @Override
            public int getTubeLength() {
                return 1000;
            }

            @Override
            public String getTubeType() {
                return "tubeType";
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
                return 0L;
            }
        };
        BazaTubeResult bazaTubeResult = new BazaTubeResult(dataProvider, 2);
        assertEquals(expectedDefects, bazaTubeResult.getAllDefectsPositions());
    }

}
