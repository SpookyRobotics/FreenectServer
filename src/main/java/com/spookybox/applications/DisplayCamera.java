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
            byte[] bytes = new byte[rgbResult.capacity()];
            for(int index = 0; index < bytes.length; index++){
                bytes[index] = rgbResult.get(index);
            }
            BufferedImage image = ByteBufferToImage.byteArrayToImage(bytes);
            ArffData arffData = new ArffData(kinectFrame.getTimestamp(), true, image);
            for(KinectFrameConsumer c : mKinectFrameConsumers){
                c.acceptDepth(arffData);
            }
            mDepthCanvas.setImage(image);
            mDepthCanvas.repaint();
        });
    }

    private KinectFrame rgbFirstFrame = null;
    private Consumer<KinectFrame> displayRgbImage() {
        return (kinectFrame -> {
            if(rgbFirstFrame == null){
                rgbFirstFrame = kinectFrame;
            } else {
                byte[] bytes = new byte[rgbFirstFrame.getBuffer().capacity() + kinectFrame.getBuffer().capacity()];
                int resultIndex = 0;
                for(int frameIndex = 0; frameIndex < rgbFirstFrame.getBuffer().capacity(); frameIndex++){
                    bytes[resultIndex] = rgbFirstFrame.getBuffer().get(frameIndex);
                    resultIndex++;
                }
                for(int frameIndex = 0; frameIndex < kinectFrame.getBuffer().capacity(); frameIndex++){
                    bytes[resultIndex] = kinectFrame.getBuffer().get(frameIndex);
                    resultIndex++;
                }
                BufferedImage image = ByteBufferToImage.byteArrayToImage(bytes);
                ArffData arffData = new ArffData(kinectFrame.getTimestamp(), false, image);
                for(KinectFrameConsumer c : mKinectFrameConsumers){
                    c.acceptRgb(arffData);
                }
                rgbFirstFrame = null;
                mRgbCanvas.setImage(image);
                mRgbCanvas.repaint();
            }
        });
    }
}
