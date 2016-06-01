package com.spookybox.applications;

import com.spookybox.camera.CameraManager;
import com.spookybox.camera.KinectFrame;
import com.spookybox.server.*;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Consumer;

public class KinectFrameServer extends KinectFrameConsumer {
    private final CameraManager mCameraManager;
    private ServerMain mServer;

    private final Consumer<KinectFrame> mRgbConsumer;
    private final Consumer<KinectFrame> mDepthConsumer;
    private boolean mShutdownServers = false;
    private Optional<TcpServer> mDepthServer = Optional.empty();
    private Optional<TcpServer> mRgbServer = Optional.empty();


    public KinectFrameServer(CameraManager cameraManager) {
        mCameraManager = cameraManager;
        mDepthConsumer = kinectFrame -> {
            mDepthServer.ifPresent(tcpServer -> {

            });
        };
        mRgbConsumer = kinectFrame -> {
            mRgbServer.ifPresent(tcpServer -> {

            });
        };

    }

    @Override
    public void start() {
        mServer = new ServerMain(mCameraManager, getRgbServerDescription(), getDepthServerDescription());

    }

    @Override
    public void stop() {

    }

    private boolean shutdownServers() {
        return mShutdownServers;
    }


    private ServerDescription getDepthServerDescription() {
        return new ServerDescription("depthServer", 4041, tcpServer -> {
            mDepthServer = Optional.of(tcpServer);
        });
    }

    private ServerDescription getRgbServerDescription() {
        return new ServerDescription("rgbServer", 4040, tcpServer -> {
            mRgbServer = Optional.of(tcpServer);
        });
    }

    @Override
    protected Consumer<KinectFrame> getRgbConsumer() {
        return mRgbConsumer;
    }

    @Override
    protected Consumer<KinectFrame> getDepthConsumer() {
        return mDepthConsumer;
    }
}
