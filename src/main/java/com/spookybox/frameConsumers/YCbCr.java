package com.spookybox.frameConsumers;

public class YCbCr {
    public final double Y;
    public final double Cb;
    public final double Cr;

    public YCbCr(double y, double cB, double cR) {
        Y = y;
        Cb = cB;
        Cr = cR;
    }

    public static YCbCr fromRgb(byte red, byte green, byte blue){
        double y = 0.2126 *red+0.7152*green+0.0722*blue;
        double cB = 0.5389*(blue - y);
        double cR  = 0.6350*(red - y);
        return new YCbCr(y, cB, cR);
    }
}
