package com.spookybox.frameConsumers;

import com.spookybox.camera.KinectFrame;
import com.spookybox.camera.Serialization;

import java.util.ArrayList;
import java.util.List;

public class CameraSnapShot {
    public final List<KinectFrame> mRgbFrames = new ArrayList<>();
    public final List<KinectFrame> mDepthFrames = new ArrayList<>();

    public CameraSnapShot(List<KinectFrame> rgbFrames, List<KinectFrame> depthFrames){
        if(rgbFrames == null || depthFrames == null){
            throw new IllegalArgumentException("null buffer not allowed");
        }

        for (KinectFrame f : rgbFrames){
            mRgbFrames.add(f.clone());
        }

        for(KinectFrame f : depthFrames){
            mDepthFrames.add(f.clone());
        }
    }

    public static List<Byte> cameraSnapShotToByteList(CameraSnapShot snapShot) {
        int depthFrames = snapShot.mDepthFrames.size();
        int rgbFrames = snapShot.mRgbFrames.size();
        List<Byte> numDepthFrames = Serialization.intToByteList(depthFrames);
        List<Byte> numRgbFrames = Serialization.intToByteList(rgbFrames);
        List<Byte> kinectFrames = new ArrayList<>();
        for(KinectFrame f : snapShot.mDepthFrames){
            kinectFrames.addAll(KinectFrame.kinectFrameToByteList(f));
        }

        for(KinectFrame f : snapShot.mRgbFrames){
            kinectFrames.addAll(KinectFrame.kinectFrameToByteList(f));
        }
        ArrayList<Byte> resultList = new ArrayList<>();
        resultList.addAll(numDepthFrames);
        resultList.addAll(numRgbFrames);
        resultList.addAll(kinectFrames);
        return resultList;
    }

    public static CameraSnapShot byteListToCameraSnapShot(List<Byte> bytes) {
        int index = 0;
        List<Byte> numDepthFramesList = bytes.subList(0, index+ Serialization.INT_BYTE_LENGTH);
        index += Serialization.INT_BYTE_LENGTH;
        List<Byte> numRgbFramesList = bytes.subList(index, index+ Serialization.INT_BYTE_LENGTH);
        index += Serialization.INT_BYTE_LENGTH;

        int numDepthFrames = Serialization.byteListToInt(numDepthFramesList);
        int numRgbFrames = Serialization.byteListToInt(numRgbFramesList);
        List<KinectFrame> depthFrames = new ArrayList<>();
        List<KinectFrame> rgbFrames = new ArrayList<>();
        Serialization.extractKinectFrames(bytes, index, numDepthFrames, numRgbFrames, depthFrames, rgbFrames);
        return new CameraSnapShot(rgbFrames, depthFrames);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CameraSnapShot that = (CameraSnapShot) o;

        for(KinectFrame f : mRgbFrames){
            if(!that.mRgbFrames.contains(f)){
                return false;
            }
        }

        for(KinectFrame f : mDepthFrames){
            if(!that.mDepthFrames.contains(f)){
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mRgbFrames != null ? mRgbFrames.hashCode() : 0;
        result = 31 * result + (mDepthFrames != null ? mDepthFrames.hashCode() : 0);
        return result;
    }
}
