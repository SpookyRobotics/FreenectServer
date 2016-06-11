package com.spookybox.applications;

import com.spookybox.camera.CameraManager;
import com.spookybox.tilt.TiltManager;
import org.openkinect.freenect.*;

public abstract class DefaultInstance implements ApplicationInstance {

    protected final CameraManager mCameraManager;
    protected final TiltManager mTiltManager;
    protected final Context mContext;
    protected final Device mKinect;

    public DefaultInstance(){
        if(!initCamera()){
            mCameraManager = null;
            mTiltManager = null;
            mContext = null;
            mKinect = null;
            return;
        }
        mContext = Freenect.createContext();
        if(mContext == null){
            throw new IllegalStateException("mContext is null");
        }
        if (mContext.numDevices() > 0) {
            //mContext.setLogHandler((dev, level, msg) -> System.out.println("--> "+msg+" <--"));
            //mContext.setLogLevel(LogLevel.INFO);
            mKinect = mContext.openDevice(0);
        } else {
            throw new IllegalAccessError("No kinect detected.");
        }
        mTiltManager = new TiltManager(mKinect);
        mCameraManager = new CameraManager(mKinect);

    }

    protected boolean initCamera(){
        return true;
    }

    public final void shutdownKinect() {
        if(!initCamera()){
            return;
        }
        mKinect.close();
        mContext.shutdown();
    }

    protected void printTiltAngle() {
        System.out.println(" Current Tilt angle: " + mTiltManager.getTiltAngle());
    }

    @Override
    public void haltCameraAndTilt(){
        if(initCamera()){
            mCameraManager.stop();
        }
    }
}
