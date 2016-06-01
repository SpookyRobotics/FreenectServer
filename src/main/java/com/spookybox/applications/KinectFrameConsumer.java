package com.spookybox.applications;

import java.util.function.Consumer;

public abstract class KinectFrameConsumer<T> {
    public abstract void start();
    public abstract void stop();


    public final void acceptRgb(T rgb){
        getRgbConsumer().accept(rgb);
    }

    public final void acceptDepth(T depth){
        getDepthConsumer().accept(depth);
    }

    protected abstract Consumer<T> getRgbConsumer();

    protected abstract Consumer<T> getDepthConsumer();
}
