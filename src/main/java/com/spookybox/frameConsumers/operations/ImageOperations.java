package com.spookybox.frameConsumers.operations;

import java.awt.image.*;

public class ImageOperations {
    public static BufferedImage blur(BufferedImage bufferedImage){
        Kernel kernel = new Kernel(3, 3, new float[] { 1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f });
        BufferedImageOp op = new ConvolveOp(kernel);
        return op.filter(bufferedImage, null);
    }

    public static BufferedImageOp createThresholdOp(int threshold,
                                              int minimum, int maximum) {
        short[] thresholdArray = new short[256];
        for (int i = 0; i < 256; i++) {
            if (i < threshold)
                thresholdArray[i] = (short)minimum;
            else
                thresholdArray[i] = (short)maximum;
        }
        return new LookupOp(new ShortLookupTable(0, thresholdArray), null);
    }
}
