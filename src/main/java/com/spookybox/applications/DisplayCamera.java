package com.spookybox.applications;

import com.spookybox.frameConsumers.ArffCreator;
import com.spookybox.frameConsumers.ArffData;
import com.spookybox.freenect.DepthStreamCallback;
import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;
import com.spookybox.camera.KinectFrame;
import com.spookybox.inputManager.ConsoleInput;

import java.awt.image.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Consumer;

import static com.spookybox.graphics.ByteBufferToImage.SCREEN_RESOLUTION;

public class DisplayCamera extends DefaultInstance {
    private DisplayCanvas mRgbCanvas;
    private DisplayCanvas mDepthCanvas;
    private DepthStreamCallback mDepthStreamCallback;
    private final ArrayList<KinectFrameConsumer<ArffData>> mKinectFrameConsumers;

    public DisplayCamera(){
        mDepthStreamCallback = new DepthStreamCallback();
        mKinectFrameConsumers = new ArrayList<>();
    }

    @Override
    public void run() {
        DisplayCanvas[] canvases = DisplayCanvas.initWindow();
        mRgbCanvas = canvases[0];
        mDepthCanvas = canvases[1];
        addArrfCreator();
        mCameraManager.startCapture(displayRgbImage(), displayDepthImage());
        ConsoleInput input = new ConsoleInput();
        input.setOnButtonA(() -> mCameraManager.setTilt(mCameraManager.getTilt() + 10));
        input.setOnButtonB(() -> mCameraManager.setTilt(mCameraManager.getTilt() - 10));
        input.startInputLoop();
    }

    private void addArrfCreator() {
        mKinectFrameConsumers.add(new ArffCreator());
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
            BufferedImage image = ByteBufferToImage.byteArrayToImage(bytes);
            for(KinectFrameConsumer c : mKinectFrameConsumers){
                c.acceptDepth(rgbFrame);
            }
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
                for(KinectFrameConsumer c : mKinectFrameConsumers){
                    c.acceptRgb(rgbFrame);
                }
                rgbFirstFrame = null;
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
