package com.spookybox.frameConsumers;

import com.spookybox.applications.KinectFrameConsumer;
import com.spookybox.camera.KinectFrame;
import com.spookybox.graphics.ByteBufferToImage;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
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
        InputPanelImage inputPanelImage = InputPanel.splitIntoPanels(image, mPanelsPerRow, mNumberOfRows);
        for(InputPanel p : inputPanelImage.panels){
            averageRgbColor(p);
        }
        return InputPanel.stichPanelsToImage(inputPanelImage);
    }

    private void averageRgbColor(final InputPanel panel) {

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
