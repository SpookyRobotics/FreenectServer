package com.spookybox.server;

import com.spookybox.camera.CameraManager;
import com.spookybox.camera.KinectFrame;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class DepthTransmitter extends TcpServer{
    private final int MAX_QUEUE_SIZE = 60;
    private ConcurrentLinkedQueue<KinectFrame> mFramesToTransmit = new ConcurrentLinkedQueue<>();

    public Consumer<KinectFrame> getDepthConsumer(){
        return kinectFrame -> {
            if(mFramesToTransmit.size() > MAX_QUEUE_SIZE){
                //System.out.println("Dropping depth transmit buffer");
                mFramesToTransmit.clear();
            }
            mFramesToTransmit.add(kinectFrame);
        };
    }
    @Override
    protected int getPort() {
        return 8000;
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
        return encodeAndEmptyDepth();
    }

    private String encodeAndEmptyDepth() {
        KinectFrame[] framesToTransmit = (KinectFrame[]) mFramesToTransmit.toArray();
        mFramesToTransmit.clear();
        StringBuilder builder = new StringBuilder();
        for(int index = 0; index < framesToTransmit.length; index++){
            builder.append("testDEPTH").append(index).append("/END");
        }
        return builder.toString();
    }
}
