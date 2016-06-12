package com.spookybox.frameConsumers;

import java.awt.image.BufferedImage;

public class DownscaledImage {
    public final BufferedImage image;
    public final int panelsPerRow;
    public final int numberOfRows;

    public DownscaledImage(BufferedImage image, int panelsPerRow, int numberOfRows) {
        this.image = image;
        this.panelsPerRow = panelsPerRow;
        this.numberOfRows = numberOfRows;
    }
}
