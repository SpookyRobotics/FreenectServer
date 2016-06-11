package com.spookybox.freenect;

import java.nio.ByteBuffer;

public class DepthStreamCallback {
    private static final int SIZE_OF_SHORT = 2;
    private static final ByteBuffer mGammaBuffer;

    static {
        System.loadLibrary("freenectDepth");
        mGammaBuffer = ByteBuffer.allocateDirect(2048 * SIZE_OF_SHORT);
        initGammaArray(mGammaBuffer);
    }

    public void depthCallback(ByteBuffer depthBuffer, ByteBuffer rgbResult){
        depthCallback(mGammaBuffer, depthBuffer, rgbResult);
    }

    private static native void depthCallback(ByteBuffer gammaArray, ByteBuffer frame, ByteBuffer resultBuffer);
    private static native void initGammaArray(ByteBuffer arrayBuffer);


}
