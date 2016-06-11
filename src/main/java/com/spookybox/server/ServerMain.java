package com.spookybox.server;

import com.spookybox.camera.CameraManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ServerMain {
    private RgbTransmitter mRgbServer;
    private DepthTransmitter mDepthServer;
    private final CameraManager mCameraManager;

    public ServerMain(final CameraManager cameraManager){
        mCameraManager = cameraManager;
        mRgbServer = new RgbTransmitter();
        mDepthServer = new DepthTransmitter();
        mCameraManager.addRgbConsumer(mRgbServer.getRgbConsumer());
        mCameraManager.addDepthConsumer(mDepthServer.getDepthConsumer());
        mCameraManager.addOnStartListener(() -> {
            try {
              start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void start() throws IOException {
        mRgbServer.start();
        mDepthServer.start();
    }
}
