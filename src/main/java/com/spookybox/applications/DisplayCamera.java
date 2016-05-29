package com.spookybox.applications;

import com.spookybox.freenect.DepthStreamCallback;
import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;
import com.spookybox.camera.KinectFrame;
import com.spookybox.server.DepthTransmitter;
import com.spookybox.server.RgbTransmitter;
import com.spookybox.server.ServerDescription;
import com.spookybox.server.ServerMain;

import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static com.spookybox.graphics.ByteBufferToImage.SCREEN_RESOLUTION;

public class DisplayCamera extends DefaultInstance {
    private DisplayCanvas mRgbCanvas;
    private ServerMain mServer;
    private DisplayCanvas mDepthCanvas;
    private DepthStreamCallback mDepthStreamCallback;
    private Optional<ConcurrentLinkedQueue<BufferedImage>> mRecentDepthFrames;
    private Optional<ConcurrentLinkedQueue<BufferedImage>> mRecentRgbFrames;
    private boolean mShutdownServers = false;

    public DisplayCamera(){
        mDepthStreamCallback = new DepthStreamCallback();
        mRecentDepthFrames = Optional.empty();
        mRecentRgbFrames = Optional.empty();
    }

    @Override
    public void run() {
        DisplayCanvas[] canvases = DisplayCanvas.initWindow();
        mRgbCanvas = canvases[0];
        mDepthCanvas = canvases[1];
        //startServerThread();
        mCameraManager.startCapture(getRgbReceiver(), getDepthReceiver());
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServerThread() {
        Thread t = new Thread(() -> {
            mServer = new ServerMain(mCameraManager, getRgbServerDescription(), getDepthServerDescription());
            ConcurrentLinkedQueue<BufferedImage> rgbFrames = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<BufferedImage> depthFrames = new ConcurrentLinkedQueue<>();

            mRecentDepthFrames = Optional.of(depthFrames);
            mRecentRgbFrames = Optional.of(rgbFrames);

            while(true){
                if(!rgbFrames.isEmpty()){
                    BufferedImage frame = rgbFrames.remove();
                    mServer.transmitRgb(frame);
                    System.out.println("Transmit rgb frame");
                }
                if(!depthFrames.isEmpty()){
                    depthFrames.remove();
                    System.out.println("Transmit depth frame");
                }
            }
        });
        t.start();
    }

    private ServerDescription getDepthServerDescription() {
        return new ServerDescription("depthServer", 4041, tcpServer -> {
            while(!shutdownServers()){
                mRecentDepthFrames.ifPresent(queue -> {
                    while (!queue.isEmpty()){
                        BufferedImage rgb = queue.remove();
                        DepthTransmitter.transmit(rgb,tcpServer);
                    }
                });
            }
        });
    }

    private ServerDescription getRgbServerDescription() {
        return new ServerDescription("rgbServer", 4040, tcpServer -> {
            while(!shutdownServers()){
                mRecentRgbFrames.ifPresent(queue -> {
                    while (!queue.isEmpty()){
                        BufferedImage depth = queue.remove();
                        RgbTransmitter.transmit(depth, tcpServer);
                    }
                });
            }
        });
    }

    private boolean shutdownServers() {
        return mShutdownServers;
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
            mRecentDepthFrames.ifPresent(queue -> queue.add(image));
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
                mRecentRgbFrames.ifPresent(queue -> queue.add(image));
                rgbFirstFrame = null;
                mRgbCanvas.setImage(image);
                mRgbCanvas.repaint();
            }
        });
    }
}
