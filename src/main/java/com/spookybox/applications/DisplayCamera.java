package com.spookybox.applications;

import com.spookybox.frameConsumers.ArffCreator;
import com.spookybox.frameConsumers.ArffData;
import com.spookybox.frameConsumers.DownscaledImage;
import com.spookybox.frameConsumers.Downscaler;
import com.spookybox.frameConsumers.operations.ImageOperations;
import com.spookybox.frameConsumers.operations.PanelOperations;
import com.spookybox.freenect.DepthStreamCallback;
import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;
import com.spookybox.camera.KinectFrame;
import com.spookybox.inputManager.ConsoleInput;
import com.spookybox.server.ServerMain;

import java.awt.image.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.spookybox.graphics.ByteBufferToImage.SCREEN_RESOLUTION;

public class DisplayCamera extends DefaultInstance {
    private final KinectFrameConsumerThread mConsumerThread;
    private DisplayCanvas mRgbCanvas;
    private DisplayCanvas mDepthCanvas;
    private DepthStreamCallback mDepthStreamCallback;
    private DisplayCanvas mAuxCanvas;
    private ServerMain mServer;
    private List<Consumer<DownscaledImage>> mDownScaleConsumers = new ArrayList<>();

    public DisplayCamera(){
        mDepthStreamCallback = new DepthStreamCallback();
        mConsumerThread = new KinectFrameConsumerThread();
    }

    @Override
    public void run() {
        DisplayCanvas[] canvases = DisplayCanvas.initWindow();
        mRgbCanvas = canvases[0];
        mDepthCanvas = canvases[1];
        mAuxCanvas = canvases[2];
        addDownscaleConsumer();
        mCameraManager.addDepthConsumer(displayDepthImage());
        mCameraManager.addRgbConsumer(displayRgbImage());
        mServer = new ServerMain(mCameraManager);
        mDownScaleConsumers.add(mServer.getDownScaleConsumer());
        mCameraManager.startCapture();
        ConsoleInput input = new ConsoleInput();
        input.setOnButtonA(() -> mCameraManager.setTilt(mCameraManager.getTilt() + 10));
        input.setOnButtonB(() -> mCameraManager.setTilt(mCameraManager.getTilt() - 10));
        input.startInputLoop();
    }

    private void addDownscaleConsumer(){
        int panelsPerRow = 10;
        int numberOfRows = 10;
        Downscaler downscaler = new Downscaler(
                image -> {
                    mAuxCanvas.setImage(image);
                    mAuxCanvas.repaint();
                    for(Consumer<DownscaledImage> c : mDownScaleConsumers){
                        c.accept(new DownscaledImage(image, panelsPerRow, numberOfRows));
                    }
                },
                panelsPerRow,
                numberOfRows
        );
        downscaler.setInputPanelOperation(PanelOperations.averageRgbColor());
        mConsumerThread.add(downscaler);
    }
    
    private void addArrfCreator() {
        mConsumerThread.add(new ArffCreator());
    }


    private Consumer<KinectFrame> displayDepthImage() {
        return (kinectFrame -> {
            int bytesPerPixel = 3;
            ByteBuffer rgbResult = ByteBuffer.allocateDirect(bytesPerPixel * SCREEN_RESOLUTION);
            mDepthStreamCallback.depthCallback(kinectFrame.getBuffer(), rgbResult);

            KinectFrame rgbFrame = new KinectFrame(
                    kinectFrame.isDepthFrame(),
                    kinectFrame.getMode(),
                    buildArrayBackedBuffer(rgbResult),
                    kinectFrame.getTimestamp()
            );

            byte[] bytes = new byte[rgbResult.capacity()];
            for(int index = 0; index < bytes.length; index++){
                bytes[index] = rgbResult.get(index);
            }
            mConsumerThread.queueDepth(rgbFrame);
            BufferedImage image = ByteBufferToImage.byteArrayToImage(bytes);
            mDepthCanvas.setImage(image);
            mDepthCanvas.repaint();
        });
    }

    private static ByteBuffer buildArrayBackedBuffer(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.capacity()];
        for(int index = 0; index < bytes.length; index++){
            bytes[index] = buffer.get(index);
        }
        return ByteBuffer.wrap(bytes);
    }

    private KinectFrame rgbFirstFrame = null;
    private Consumer<KinectFrame> displayRgbImage() {
        return (kinectFrame -> {
            if(rgbFirstFrame == null){
                rgbFirstFrame = kinectFrame;
            } else {
                KinectFrame rgbFrame = buildRgbFrame(rgbFirstFrame, kinectFrame);
                rgbFirstFrame = null;
                mConsumerThread.queueRgb(rgbFrame);
                BufferedImage image = ByteBufferToImage.byteArrayToImage(rgbFrame.getBuffer().array());
                mRgbCanvas.setImage(image);
                mRgbCanvas.repaint();
            }
        });
    }

    private ByteBuffer zipBuffers(ByteBuffer first, ByteBuffer second){
        byte[] bytes = new byte[first.capacity() + second.capacity()];
        int resultIndex = 0;
        for(int frameIndex = 0; frameIndex < first.capacity(); frameIndex++){
            bytes[resultIndex] = first.get(frameIndex);
            resultIndex++;
        }
        for(int frameIndex = 0; frameIndex < second.capacity(); frameIndex++){
            bytes[resultIndex] = second.get(frameIndex);
            resultIndex++;
        }
        return ByteBuffer.wrap(bytes);
    }
    private KinectFrame buildRgbFrame(KinectFrame firstFrame, KinectFrame secondFrame) {
        if(firstFrame.isDepthFrame() || secondFrame.isDepthFrame()){
            throw new IllegalArgumentException("Must have two rgb frames");
        }
        ByteBuffer buffer = zipBuffers(firstFrame.getBuffer(), secondFrame.getBuffer());
        return new KinectFrame(false, firstFrame.getMode(), buffer, firstFrame.getTimestamp());
    }
}
