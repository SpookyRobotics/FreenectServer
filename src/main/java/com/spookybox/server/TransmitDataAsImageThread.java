package com.spookybox.server;

import com.spookybox.util.ThreadUtils;

import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public class TransmitDataAsImageThread<T> extends Thread{
    private static final int MILLISECONDS_BETWEEN_TRANSMIT = 500;
    private static final int QUEUE_SIZE = 2;

    private final TcpServer mServer;
    private final BlockingQueue<T> mQueuedData;
    private final Function<T, BufferedImage> mConverter;
    private int[] transmitBuffer;
    private final TransmitController mTransmitController;
    private boolean mIsStopping = true;
    private final int mThreadNumber;

    public TransmitDataAsImageThread(final TcpServer server,
                                     final TransmitController controller,
                                     final int threadNumber,
                                     final Function<T, BufferedImage> converter) {
        mServer = server;
        mQueuedData = new ArrayBlockingQueue<T>(QUEUE_SIZE);
        mTransmitController = controller;
        mThreadNumber = threadNumber;
        mConverter = converter;
    }

    public void queueData(T data){
        mQueuedData.add(data);
    }
    public void run(){
        if(!mServer.isConnected()){
            System.err.println("Can't start " + getName() + " without having server connected");
            return;
        }
        mIsStopping = false;
        T data;
        while(!isStopping()){
            if(!mQueuedData.isEmpty()){
                try {
                    data = mQueuedData.take();
                    BufferedImage image = mConverter.apply(data);
                    int width = image.getWidth();
                    int height = image.getHeight();
                    transmitBuffer = new int[width * height];
                    image.getRGB(0, 0, image.getWidth(), image.getHeight(), transmitBuffer, 0, image.getWidth());
                    mTransmitController.transmit(mServer, mThreadNumber, transmitBuffer,0,width*height);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ThreadUtils.sleep(MILLISECONDS_BETWEEN_TRANSMIT);
        }
    }

    public void stopTransmit(){
        mIsStopping = true;
    }

    private boolean isStopping() {
        return mIsStopping;
    }
}
