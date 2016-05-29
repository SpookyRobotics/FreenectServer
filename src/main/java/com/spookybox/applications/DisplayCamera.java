package com.spookybox.applications;

import com.spookybox.freenect.DepthStreamCallback;
import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;
import com.spookybox.camera.KinectFrame;
import com.spookybox.server.ServerMain;

import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static com.spookybox.graphics.ByteBufferToImage.SCREEN_RESOLUTION;

public class DisplayCamera extends DefaultInstance {
    private DisplayCanvas mRgbCanvas;
    private ServerMain mServer;
    private DisplayCanvas mDepthCanvas;
    private DepthStreamCallback mDepthStreamCallback;

    public DisplayCamera(){
        mDepthStreamCallback = new DepthStreamCallback();
    }

    @Override
    public void run() {
        DisplayCanvas[] canvases = DisplayCanvas.initWindow();
        mRgbCanvas = canvases[0];
        mDepthCanvas = canvases[1];
        //mServer = new ServerMain(mCameraManager);
        mCameraManager.startCapture(getRgbReceiver(), getDepthReceiver());
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Consumer<KinectFrame> getDepthReceiver() {
        return (kinectFrame -> {
            int bytesPerPixel = 3;
            ByteBuffer rgbResult = ByteBuffer.allocateDirect(bytesPerPixel * SCREEN_RESOLUTION);
            mDepthStreamCallback.depthCallback(kinectFrame.getBuffer(), rgbResult);
            byte[] bytes = new byte[rgbResult.capacity()];
            for(int index = 0; index < bytes.length; index++){
                bytes[index] = rgbResult.get(index);
            }
            BufferedImage image = ByteBufferToImage.byteArrayToImage(bytes);
            mDepthCanvas.setImage(image);
            mDepthCanvas.repaint();
        });
    }

    private KinectFrame rgbFirstFrame = null;
    private Consumer<KinectFrame> getRgbReceiver() {
        return (kinectFrame -> {
            if(rgbFirstFrame == null){
                rgbFirstFrame = kinectFrame;
            } else {
                byte[] bytes = new byte[rgbFirstFrame.getBuffer().capacity() + kinectFrame.getBuffer().capacity()];
                int index = 0;
                for(int s = 0; s < rgbFirstFrame.getBuffer().capacity(); s++){
                    bytes[index] = rgbFirstFrame.getBuffer().get(s);
                    index++;
                }
                for(int s = 0; s < kinectFrame.getBuffer().capacity(); s++){
                    bytes[index] = kinectFrame.getBuffer().get(s);
                    index++;
                }
                BufferedImage image = ByteBufferToImage.byteArrayToImage(bytes);
                rgbFirstFrame = null;
                mRgbCanvas.setImage(image);
                mRgbCanvas.repaint();
            }
        });
    }
}
