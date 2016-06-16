package com.spookybox.frameConsumers;

import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class TestBufferedImagePanel {
    private static final int TEST_WIDTH = 2;
    private static final int TEST_HEIGHT = 3;
    private BufferedImage mTestImage;
    private ColorModel testColorModel;

    @Before
    public void setUp() {
        mTestImage = new BufferedImage(TEST_WIDTH, TEST_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        /* set rgb */
        mTestImage.setRGB(0, 0, TEST_WIDTH, TEST_HEIGHT, getRgbArray(), 0, TEST_WIDTH);
    }

    @Test
    public void testSplitIntoPanels(){
        List<InputPanel> panels = InputPanel.splitIntoPanels(mTestImage, TEST_WIDTH, 1).panels;
        int[] testArray = getRgbArray();
        int testArrayIndex = 0;
        for(InputPanel p : panels){
            for(int index = 0; index < p.image.length; index++){
                assertEquals(testArray[testArrayIndex], p.image[index]);
                testArrayIndex++;
            }
        }
    }

    @Test
    public void testStichPanels(){
        InputPanelImage testImage = InputPanel.splitIntoPanels(mTestImage, TEST_WIDTH, 1);
        BufferedImage result = InputPanel.stichPanelsToImage(testImage);
        int[] testData = result.getRGB(0, 0, result.getWidth(), result.getHeight(), null, 0, result.getWidth());
        for(int index = 0; index < testData.length; index++){
            assertEquals(getRgbArray()[index], testData[index]);
        }

    }

    public int[] getRgbArray() {
        /* Buffered image is drawn (0,0) to (0,Y) then (1,0) to (1,Y).
            So this array fills in Y values before incrementing X */
        return new int []{
            0xAAAAAAAA,
            0xBBBBBBBB,
            0xCCCCCCCC,
            0xDDDDDDDD,
            0xEEEEEEEE,
            0xFFFFFFFF
        };
    }
}
