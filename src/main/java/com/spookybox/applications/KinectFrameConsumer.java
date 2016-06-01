package com.spookybox.applications;

import com.spookybox.camera.KinectFrame;

import java.util.function.Consumer;

public abstract class KinectFrameConsumer<T> {
    public abstract void start();
    public abstract void stop();


    public final void acceptRgb(KinectFrame rgb){
        getRgbConsumer().accept(transformRgb(rgb));
    }

    public final void transformAndAccept(KinectFrame frame){

    }

    protected abstract T transformDepth(KinectFrame frame);
    protected abstract T transformRgb(KinectFrame frame);

    public final void acceptDepth(KinectFrame depth){
        getDepthConsumer().accept(transformDepth(depth));
    }

    protected abstract Consumer<T> getRgbConsumer();

    protected abstract Consumer<T> getDepthConsumer();
}
