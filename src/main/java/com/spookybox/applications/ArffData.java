package com.spookybox.applications;

import com.spookybox.camera.KinectFrame;

import java.awt.image.BufferedImage;

public class ArffData {
    public final int timestamp;
    public final boolean isDepth;
    public final BufferedImage image;

    public ArffData(int timestamp, boolean isDepth, BufferedImage image) {
        this.timestamp = timestamp;
        this.isDepth = isDepth;
        this.image = image;
    }
}
