package com.spookybox.frameConsumers;

public class Pixel {
    private static final int RED_MASK =   0x00FF0000;
    private static final int GREEN_MASK = 0x0000FF00;
    private static final int BLUE_MASK =  0x000000FF;

    byte red;
    byte green;
    byte blue;

    public Pixel(byte r, byte g, byte b){
        red = r;
        green = g;
        blue = b;
    }

    public int toInt() {
        return 0xFF000000 |  (red << 16 | green << 8 | blue);
    }

    public static int getRed(int rgb) {
        return (rgb & RED_MASK) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & GREEN_MASK) >> 8;
    }

    public static int getBlue(int rgb) {
        return  (rgb & BLUE_MASK);
    }
}
