package com.spookybox.applications;

import com.spookybox.camera.KinectFrame;
import com.spookybox.frameConsumers.ArffCreator;
import com.spookybox.frameConsumers.ArffData;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KinectFrameConsumerThread {

    private final ExecutorService mRgbExecutor;
    private final ExecutorService mDepthExecutor;
    private final ArrayList<KinectFrameConsumer<ArffData>> mKinectFrameConsumers;


    public KinectFrameConsumerThread(){
        mRgbExecutor = Executors.newSingleThreadExecutor();
        mDepthExecutor = Executors.newSingleThreadExecutor();
        mKinectFrameConsumers = new ArrayList<>();
    }

    public void queueDepth(final KinectFrame depth){
        dispatchDepthFrame(depth);
        //mDepthExecutor.submit(() -> dispatchDepthFrame(depth));
    }

    public void queueRgb(final KinectFrame rgb){
        dispatchRgbFrame(rgb);
        //mRgbExecutor.submit(() -> dispatchRgbFrame(rgb));
    }

    private void dispatchDepthFrame(final KinectFrame depthFrame){
        for(KinectFrameConsumer c : mKinectFrameConsumers){
            c.acceptDepth(depthFrame);
        }
    }

    private void dispatchRgbFrame(final KinectFrame rgbFrame){
        for(KinectFrameConsumer c : mKinectFrameConsumers){
            c.acceptRgb(rgbFrame);
        }
    }
    public void add(KinectFrameConsumer consumer) {
        mKinectFrameConsumers.add(consumer);
    }
}
