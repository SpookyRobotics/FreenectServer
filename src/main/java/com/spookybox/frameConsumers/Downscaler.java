package com.spookybox.frameConsumers;

import com.spookybox.applications.KinectFrameConsumer;
import com.spookybox.camera.KinectFrame;
import com.spookybox.frameConsumers.operations.PanelOperations;
import com.spookybox.graphics.ByteBufferToImage;

import java.awt.image.*;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Downscaler extends KinectFrameConsumer<BufferedImage> {
    private final Consumer<BufferedImage> mFrameConsumer;
    private final Consumer<BufferedImage> mDisplay;
    private final int mPanelsPerRow;
    private final int mNumberOfRows;
    private Optional<Function<InputPanel, InputPanel>> mPanelOperation = Optional.empty();

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

    public void setInputPanelOperation(Function<InputPanel, InputPanel> panelOperation){
        mPanelOperation = Optional.ofNullable(panelOperation);
    }

    private synchronized BufferedImage downScale(final BufferedImage image) {
        InputPanelImage inputPanelImage = InputPanel.splitIntoPanels(image, mPanelsPerRow, mNumberOfRows);
        if(mPanelOperation.isPresent()){
            for(InputPanel p : inputPanelImage.panels){
                mPanelOperation.get().apply(p);
            }
        }
        return InputPanel.stichPanelsToImage(inputPanelImage);
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
