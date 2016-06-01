package com.spookybox.frameConsumers;

import java.awt.image.BufferedImage;

public class ArffData {
    private static final int PANEL_INPUT_HEIGHT = 2;
    private static final int PANEL_INPUT_WIDTH = 3;
    public final int timestamp;
    public final boolean isDepth;
    public final Pixel[][] inputPanels = new Pixel[PANEL_INPUT_HEIGHT][PANEL_INPUT_WIDTH];

    public ArffData(int timestamp, boolean isDepth, BufferedImage image) {
        this.timestamp = timestamp;
        this.isDepth = isDepth;
        int panelPixelWidth = image.getWidth() / PANEL_INPUT_WIDTH;
        int panelPixelHeight = image.getHeight() / PANEL_INPUT_HEIGHT;
        for(int heightIndex = 0; heightIndex < PANEL_INPUT_HEIGHT; heightIndex++){
            for(int widthIndex = 0; widthIndex < PANEL_INPUT_WIDTH; widthIndex++){
                BufferedImage subImage = image.getSubimage(
                        widthIndex * panelPixelWidth,
                        heightIndex * panelPixelHeight,
                        panelPixelWidth,
                        panelPixelHeight);
                inputPanels[heightIndex][widthIndex] = imageToPixel(subImage);
            }
        }

    }

    private Pixel imageToPixel(BufferedImage subImage) {
        long blueAverage = 0;
        long greenAverage = 0;
        long redAverage = 0;


        for(int width = 0; width < subImage.getWidth(); width++){
            for(int height = 0; height < subImage.getHeight(); height++){
                int pixel = subImage.getRGB(width, height);
                redAverage += getRed(pixel);
                blueAverage += getBlue(pixel);
                greenAverage += getGreen(pixel);
            }
        }
        long denominator = subImage.getHeight() * subImage.getWidth();
        return new Pixel(redAverage/denominator, greenAverage/denominator, blueAverage/denominator);
    }

    private long getRed(int rgb) {
        int redChannel = rgb & 0x00FF0000;
        return redChannel >> 16;
    }

    private long getGreen(int green) {
        int greenChannel = green & 0x0000FF00;
        return greenChannel >> 8;
    }

    private long getBlue(int blue) {
        int blueChannel = blue & 0x000000FF;
        return blueChannel;
    }

    public static class Pixel{
        long red;
        long green;
        long blue;

        public Pixel(long r, long g, long b){
            red = r;
            green = g;
            blue = b;
        }

    }
}
