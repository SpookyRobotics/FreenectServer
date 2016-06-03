package com.spookybox.frameConsumers;

import com.spookybox.applications.KinectFrameConsumer;
import com.spookybox.camera.KinectFrame;
import com.spookybox.graphics.ByteBufferToImage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Consumer;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

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

    private BufferedImage downScale(BufferedImage image) {
        int panelWidth = image.getWidth() / mPanelsPerRow;
        int panelHeight = image.getHeight() / mNumberOfRows;
        BufferedImage downScaledImage = new BufferedImage(
                panelWidth * mPanelsPerRow,
                panelHeight * mNumberOfRows,
                TYPE_INT_RGB
        );
        ArrayList<Pixel> panelColors = new ArrayList<>();
        for(int widthIndex = 0; widthIndex < mPanelsPerRow; widthIndex++ ){
            for(int heightIndex = 0; heightIndex < mNumberOfRows; heightIndex++ ){
                int panelNumber = widthIndex * mPanelsPerRow + mNumberOfRows;
                panelColors.add(getAverageRgbColor(image, panelWidth, panelHeight, panelNumber));
            }
        }

        for(int panelIndex = 0; panelIndex < panelColors.size(); panelIndex++ ){
            int startWidth = panelIndex * panelWidth;
            int endWidth = startWidth + panelWidth;
            for( int widthIndex = startWidth; widthIndex < endWidth; widthIndex++){
                int startHeight = (panelIndex % mPanelsPerRow) * panelHeight;
                int endHeight = startHeight + panelHeight;
                for( int heightIndex = startHeight; heightIndex < endHeight; heightIndex++){
                    downScaledImage.setRGB(widthIndex, heightIndex, panelColors.get(panelIndex).toInt());
                }
            }
        }
        return image;
    }

    private Pixel getAverageRgbColor(BufferedImage image,
                                     int panelWidth,
                                     int panelHeight,
                                     int panelNumber) {

        int averageRed = 0;
        int averageGreen = 0;
        int averageBlue = 0;
        int startWidth = panelNumber * panelWidth;
        int endWidth = startWidth + panelWidth;
        int startHeight = panelNumber * panelHeight;
        int endHeight = startHeight + panelHeight;

        for(int widthIndex = startWidth; widthIndex < endWidth; widthIndex++ ){
            for(int heightIndex = startHeight; heightIndex < endHeight; heightIndex++ ){
                int rgb = image.getRGB(widthIndex, heightIndex);
                averageRed += Pixel.getRed(rgb);
                averageGreen += Pixel.getGreen(rgb);
                averageBlue += Pixel.getBlue(rgb);
            }
        }

        int totalPixels = panelWidth * panelHeight;
        byte red = (byte) (averageRed / totalPixels);
        byte green = (byte) (averageGreen / totalPixels);
        byte blue = (byte) (averageBlue / totalPixels);
        return new Pixel(red, green, blue);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    protected BufferedImage transformDepth(KinectFrame frame) {
        return ByteBufferToImage.byteArrayToImage(frame.getBuffer().array());
    }

    @Override
    protected BufferedImage transformRgb(KinectFrame frame) {
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
