package com.spookybox.frameConsumers.operations;

import com.spookybox.frameConsumers.InputPanel;
import com.spookybox.frameConsumers.YCbCr;

import java.awt.image.*;
import java.util.function.Function;

public class PanelOperations {
    public static Function<InputPanel, InputPanel> averageRgbColor() {

        return panel -> {
            DirectColorModel colorModel = new DirectColorModel(24, 0x00FF0000, 0x0000FF00, 0x000000FF);
            double averageRed = 0;
            double averageGreen = 0;
            double averageBlue = 0;
            int[] image = panel.image;
            for( int index = 0; index < image.length; index++){
                averageRed += colorModel.getRed(image[index]);
                averageGreen += colorModel.getGreen(image[index]);
                averageBlue += colorModel.getBlue(image[index]);
            }

            byte red = (byte) ((averageRed / image.length) / 3);
            byte green = (byte) ((averageGreen / image.length) / 3);
            byte blue = (byte) ((averageBlue / image.length) / 3);
            for(int index = 0; index < image.length; index++){
                image[index] = 0xFF << 24 | red << 16 | green << 8 | blue;
            }
            return panel;
        };
    }

    public static Function<InputPanel, InputPanel> yCbCrThreshold(){
        return panel -> {
            DirectColorModel colorModel = new DirectColorModel(24, 0x00FF0000, 0x0000FF00, 0x000000FF);
            double averageRed = 0;
            double averageGreen = 0;
            double averageBlue = 0;
            int[] image = panel.image;
            for( int index = 0; index < image.length; index++){
                averageRed += colorModel.getRed(image[index]);
                averageGreen += colorModel.getGreen(image[index]);
                averageBlue += colorModel.getBlue(image[index]);
            }

            byte red = (byte) ((averageRed / image.length) / 3);
            byte green = (byte) ((averageGreen / image.length) / 3);
            byte blue = (byte) ((averageBlue / image.length) / 3);
            YCbCr yCbCr = YCbCr.fromRgb(red,green,blue);
            for(int index = 0; index < image.length; index++){
                if(yCbCr.Cb < yCbCr.Cr && yCbCr.Cb < -5){
                    //System.out.println("Displaying: cb - "+yCbCr.Cb+"\t cr - "+yCbCr.Cr);
                    image[index] = 0xFF << 24 | red << 16 | green << 8 | blue;
                } else {
                    image[index] = 0;
                }
            }
            return panel;
        };
    }

}
