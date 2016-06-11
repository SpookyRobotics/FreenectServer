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

    public static byte getRed(int rgb) {
        return (byte) ((rgb & RED_MASK) >> 16);
    }

    public static byte getGreen(int rgb) {
        return (byte) ((rgb & GREEN_MASK) >> 8);
    }

    public static byte getBlue(int rgb) {
        return (byte) (rgb & BLUE_MASK);
    }
}
