package com.spookybox.server;

import com.spookybox.camera.CameraManager;
import com.spookybox.camera.KinectFrame;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class RgbTransmitter extends TcpServer{
    private final int MAX_QUEUE_SIZE = 60;
    private final ConcurrentLinkedQueue<KinectFrame> mFramesToTransmit = new ConcurrentLinkedQueue<>();

    public Consumer<KinectFrame> getRgbConsumer(){
        return kinectFrame -> {
            synchronized (mFramesToTransmit) {
                if (mFramesToTransmit.size() > MAX_QUEUE_SIZE) {
                    //System.out.println("Dropping rgb queue buffer");
                    mFramesToTransmit.clear();
                }
                mFramesToTransmit.add(kinectFrame);
            }
        };
    }
    @Override
    protected int getPort() {
        return 8001;
    }

    @Override
    protected List<ContextHandler> getHandlers() {
        return Arrays.asList(
            getDefaultHandler()
        );
    }

    private ContextHandler getDefaultHandler() {
        return new ContextHandler() {
            @Override
            String getContext() {
                return "/";
            }

            @Override
            HttpHandler getHandler() {
                return httpExchange -> {
                    String transmit = getTransmitData();
                    httpExchange.sendResponseHeaders(200, transmit.getBytes().length);
                    OutputStream outputStream = httpExchange.getResponseBody();
                    outputStream.write(transmit.getBytes());
                    outputStream.close();
                };
            }
        };
    }

    private synchronized String getTransmitData() {
        if(mFramesToTransmit.isEmpty()){
            return "none";
        }
        return encodeAndEmptyRgb();
    }

    private String encodeAndEmptyRgb() {
        KinectFrame[] framesToTransmit;
        synchronized (mFramesToTransmit) {
            framesToTransmit = (KinectFrame[]) mFramesToTransmit.toArray();
            mFramesToTransmit.clear();
        }
        StringBuilder builder = new StringBuilder();
        for(int index = 0; index < framesToTransmit.length; index++){
            builder.append("testRGB").append(index).append("/END");
        }
        return builder.toString();
    }
}
