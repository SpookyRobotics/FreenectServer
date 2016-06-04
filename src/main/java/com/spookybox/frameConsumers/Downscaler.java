package com.spookybox.frameConsumers;

import com.spookybox.applications.KinectFrameConsumer;
import com.spookybox.camera.KinectFrame;
import com.spookybox.graphics.ByteBufferToImage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Downscaler extends KinectFrameConsumer<BufferedImage> {
    private final Consumer<BufferedImage> mFrameConsumer;
    private final Consumer<BufferedImage> mDisplay;
    private final int mPanelsPerRow;
    private final int mNumberOfRows;

    public Downscaler(Consumer<BufferedImage> display,
                      int panelsPerRow,
                      int numberOfRows) {
        mDisplay = display;
        mPanelsPerRow = panelsPerRow;
        mNumberOfRows = numberOfRows;
        mFrameConsumer = image -> {
            mDisplay.accept(downScale(image));
        };
    }

    private synchronized BufferedImage downScale(final BufferedImage image) {
        InputPanelImage testImage = InputPanel.splitIntoPanels(image, mPanelsPerRow, mNumberOfRows);
        return InputPanel.stichPanelsToImage(testImage);
    }

    private Pixel getAverageRgbColor(final InputPanel panel) {

        int averageRed = 0;
        int averageGreen = 0;
        int averageBlue = 0;
        int[] image = panel.image;
        for( int index = 0; index < image.length; index++){
            averageRed += Pixel.getRed(image[index]);
            averageGreen += Pixel.getGreen(image[index]);
            averageBlue += Pixel.getBlue(image[index]);
        }

        byte red = (byte) (averageRed / image.length);
        byte green = (byte) (averageGreen / image.length);
        byte blue = (byte) (averageBlue / image.length);
        return new Pixel(red, green, blue);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    protected BufferedImage transformDepth(final KinectFrame frame) {
        return ByteBufferToImage.byteArrayToImage(frame.getBuffer().array());
    }

    @Override
    protected BufferedImage transformRgb(final KinectFrame frame) {
        return ByteBufferToImage.byteArrayToImage(frame.getBuffer().array());
    }

    @Override
    protected Consumer<BufferedImage> getRgbConsumer() {
        return image -> {};
    }

    @Override
    protected Consumer<BufferedImage> getDepthConsumer() {
        return mFrameConsumer;
    }
}
