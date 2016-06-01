package com.spookybox.server;

import com.spookybox.camera.CameraManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ServerMain {
    public ServerMain(final CameraManager cameraManager,
                      final ServerDescription rgb,
                      final ServerDescription depth){
        cameraManager.addOnStartListener(() -> {
            try {
                start(rgb, depth);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void start(ServerDescription rgb, ServerDescription depth) throws IOException {
        TcpServer rgbServer = new TcpServer(rgb.mServerName, rgb.mPort, rgb.mListener);
        rgbServer.start();

        TcpServer depthServer = new TcpServer(depth.mServerName, depth.mPort, depth.mListener);
        depthServer.start();
    }

    public void transmitRgb(BufferedImage frame) {
    }
}
