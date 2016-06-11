package com.spookybox.applications;

import com.spookybox.camera.CameraManager;
import com.spookybox.camera.KinectFrame;
import com.spookybox.server.*;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class KinectFrameServer extends KinectFrameConsumer<List<Byte>> {
    private final CameraManager mCameraManager;
    private ServerMain mServer;

    private final Consumer<List<Byte>> mRgbConsumer;
    private final Consumer<List<Byte>> mDepthConsumer;
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
        mServer = new ServerMain(mCameraManager);

    }

    @Override
    public void stop() {

    }

    @Override
    public List<Byte> transformDepth(KinectFrame frame) {
        return null;
    }

    @Override
    protected List<Byte> transformRgb(KinectFrame frame) {
        return null;
    }

    private boolean shutdownServers() {
        return mShutdownServers;
    }

    @Override
    protected Consumer<List<Byte>> getRgbConsumer() {
        return mRgbConsumer;
    }

    @Override
    protected Consumer<List<Byte>> getDepthConsumer() {
        return mDepthConsumer;
    }
}
