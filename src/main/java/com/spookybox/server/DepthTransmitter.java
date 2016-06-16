package com.spookybox.server;

import com.spookybox.frameConsumers.DownscaledImage;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class DepthTransmitter extends TcpServer{
    private static final String PAGE = "PAGE";
    private static final String DATA = "DATA";
    private final int MIN_TRANSMIT_SIZE = 60;
    private int mTransmitNumber = 0;
    private ConcurrentLinkedQueue<int[][]> mFramesToTransmit = new ConcurrentLinkedQueue<>();
    private String mStringToTransmit;

    public DepthTransmitter(){
        mStringToTransmit = getTransmitData();
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
                    String transmit = mStringToTransmit;
                    httpExchange.sendResponseHeaders(200, transmit.getBytes().length);
                    OutputStream outputStream = httpExchange.getResponseBody();
                    outputStream.write(transmit.getBytes());
                    outputStream.close();
                };
            }
        };
    }

    private synchronized String getTransmitData() {
        if(mFramesToTransmit.isEmpty() || mFramesToTransmit.size() < MIN_TRANSMIT_SIZE){
            return new JSONObject().toString();
        }
        return encodeTransmitData();
    }

    private String encodeTransmitData() {
        int[][][] framesToTransmit = new int[0][0][0];
        framesToTransmit = mFramesToTransmit.toArray(framesToTransmit);
        String [][][] hexResult = new String[framesToTransmit.length][][];
        for(int frameIndex = 0; frameIndex < framesToTransmit.length; frameIndex++){
            int[][] rgbMatrix = framesToTransmit[frameIndex];
            String[][] outputMatrix = new String[rgbMatrix.length][];
            hexResult[frameIndex] = outputMatrix;
            for(int yIndex = 0; yIndex < rgbMatrix.length; yIndex++){
                int[] rgbRow = rgbMatrix[yIndex];
                String[] hexRow = new String[rgbRow.length];
                outputMatrix[yIndex] = hexRow;
                for( int xIndex = 0; xIndex < rgbRow.length; xIndex++){
                    hexRow[xIndex] = Integer.toHexString(rgbRow[xIndex]);
                }
            }
        }

        JSONObject out = new JSONObject();
        out.put(PAGE, mTransmitNumber);
        out.put(DATA, new JSONArray(hexResult));
        return out.toString();
    }

    private void removeLowerTransmitSize(){
        for(int index = 0; index < MIN_TRANSMIT_SIZE; index++){
            mFramesToTransmit.remove();
        }
        mTransmitNumber++;

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
            if(mFramesToTransmit.size() > MIN_TRANSMIT_SIZE *2){
                removeLowerTransmitSize();
                mStringToTransmit = getTransmitData();
            }
            mFramesToTransmit.add(rgbMatrix);
        };
    }
}
