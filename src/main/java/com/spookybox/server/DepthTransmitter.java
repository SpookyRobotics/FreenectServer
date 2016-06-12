package com.spookybox.server;

import com.spookybox.camera.CameraManager;
import com.spookybox.camera.KinectFrame;
import com.spookybox.frameConsumers.DownscaledImage;
import com.sun.net.httpserver.HttpHandler;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class DepthTransmitter extends TcpServer{
    private final int MAX_QUEUE_SIZE = 60;
    private ConcurrentLinkedQueue<int[][]> mFramesToTransmit = new ConcurrentLinkedQueue<>();

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
        int[][][] framesToTransmit = new int[mFramesToTransmit.size()][][];
        framesToTransmit = mFramesToTransmit.toArray(framesToTransmit);
        mFramesToTransmit.clear();
        StringBuilder builder = new StringBuilder();
        for(int index = 0; index < framesToTransmit.length; index++){
            int[][] rgbMatrix = framesToTransmit[index];
            builder.append("testDEPTH:Height")
                    .append(rgbMatrix.length)
                    .append(":Width")
                    .append(rgbMatrix[0].length)
                    .append(":");
            for(int yIndex = 0; yIndex < rgbMatrix.length; yIndex++){
                if(yIndex != 0){
                    builder.append(":");
                }
                for(int xIndex=0; xIndex < rgbMatrix[yIndex].length; xIndex++){
                    builder.append(Integer.toHexString(rgbMatrix[yIndex][xIndex]));
                    if(!(xIndex +1 == rgbMatrix[yIndex].length)){
                        builder.append(":");
                    }
                }
            }
            builder.append("/END");
        }
        return builder.toString();
    }

    public Consumer<DownscaledImage> getDownScaleConsumer() {
        return downscaledImage-> {
            int[][] rgbMatrix = new int[downscaledImage.numberOfRows][downscaledImage.panelsPerRow];
            BufferedImage image = downscaledImage.image;
            int xOffset = (image.getWidth() - 1)/ downscaledImage.panelsPerRow;
            int yOffset = (image.getHeight() -1)/ downscaledImage.numberOfRows;
            for(int yIndex = 0; yIndex < downscaledImage.numberOfRows; yIndex++){
                for(int xIndex = 0; xIndex < downscaledImage.panelsPerRow; xIndex++){
                    rgbMatrix[yIndex][xIndex] = image.getRGB(xIndex * xOffset, yIndex * yOffset);
                }
            }
            if(mFramesToTransmit.size() > MAX_QUEUE_SIZE){
                mFramesToTransmit.clear();
            }
            mFramesToTransmit.add(rgbMatrix);
        };
    }
}
