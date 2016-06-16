package com.spookybox.tilt;

import org.openkinect.freenect.Device;
import org.openkinect.freenect.TiltStatus;

public class TiltManager {

    private final Device mKinect;

    public TiltManager(Device kinect){
        if(kinect == null){
            throw new IllegalArgumentException("null kinect not allowed");
        }
        mKinect = kinect;
    }
    public double getTiltAngle(){
        mKinect.refreshTiltState();
        return mKinect.getTiltAngle();
    }

    public boolean moveAndWait(int degrees){
        int result = mKinect.setTiltAngle(degrees);
        if(result != 0) {
            System.err.println("Error Code: "+ result + " received from Device.setTiltAngle");
            return false;
        }
        waitWhileMoving(mKinect);
        return true;
    }

    private void waitWhileMoving(Device device) {
        while (device.getTiltStatus() == TiltStatus.STOPPED) {
            device.refreshTiltState();
        }

        if (device.getTiltStatus() == TiltStatus.MOVING) {
            while (device.getTiltStatus() == TiltStatus.MOVING) {
                device.refreshTiltState();
            }
        }

        if (device.getTiltStatus() == TiltStatus.STOPPED) {
            while (device.getTiltAngle() < -32) {
                device.refreshTiltState();
            }
        }
    }
}
