package com.spookybox.camera;

import org.openkinect.freenect.FrameMode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class KinectFrame implements Cloneable {

    private final FrameMode mMode;
    private final ByteBuffer mBuffer;

    private final int mTimestamp;
    private final boolean mIsDepthFrame;

    public KinectFrame(
            final boolean isDepthFrame,
            final FrameMode mode,
            final ByteBuffer frame,
            final int timestamp){

        mIsDepthFrame = isDepthFrame;
        mMode = mode;
        mBuffer = frame;
        mTimestamp = timestamp;
    }

    public static List<Byte> kinectFrameToByteList(KinectFrame f) {
        List<Byte> isDepthFrame = Serialization.booleanToByteList(f.isDepthFrame());
        List<Byte> mode = FrameMode.frameModeToByteList(f.getMode());
        List<Byte> timeStamp = Serialization.intToByteList(f.getTimestamp());
        List<Byte> buffer = Serialization.byteBufferToByteList(f.getBuffer());

        ArrayList<Byte> resultList = new ArrayList<>();
        resultList.addAll(isDepthFrame);
        resultList.addAll(mode);
        resultList.addAll(timeStamp);
        resultList.addAll(buffer);

        return resultList;
    }

    public static KinectFrame byteListToKinectFrame(List<Byte> in){
        int index = 0;

        List<Byte> isDepthFrameList = in.subList(index, index + Serialization.BOOLEAN_BYTE_LENGTH);
        index += Serialization.BOOLEAN_BYTE_LENGTH;

        List<Byte> modeList = in.subList(index, index + FrameMode.FRAME_MODE_BYTE_LENGTH);
        index += FrameMode.FRAME_MODE_BYTE_LENGTH;

        List<Byte> timeStampList = in.subList(index, index + Serialization.INT_BYTE_LENGTH);
        index += Serialization.INT_BYTE_LENGTH;

        List<Byte> bufferList = Serialization.extractBufferList(in, index);
        boolean isDepthFrame = Serialization.byteListToBoolean(isDepthFrameList);
        FrameMode frameMode = FrameMode.byteListToFrameMode(modeList);
        int timeStamp = Serialization.byteListToInt(timeStampList);
        ByteBuffer byteBuffer = Serialization.byteListToByteBuffer(bufferList);
        return new KinectFrame(isDepthFrame, frameMode, byteBuffer, timeStamp);
    }

    public FrameMode getMode() {
        return mMode;
    }

    public ByteBuffer getBuffer() {
        return mBuffer;
    }

    public int getTimestamp() {
        return mTimestamp;
    }

    public boolean isDepthFrame() {
        return mIsDepthFrame;
    }


    @Override
    public KinectFrame clone(){
        FrameMode mode = Cloning.clone(mMode);
        ByteBuffer frame = Cloning.clone(mBuffer);
        return new KinectFrame(mIsDepthFrame,mode,frame,mTimestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KinectFrame that = (KinectFrame) o;

        if (mTimestamp != that.mTimestamp) return false;
        if (mIsDepthFrame != that.mIsDepthFrame) return false;
        if (mMode != null ? !mMode.equals(that.mMode) : that.mMode != null) return false;
        return mBuffer != null ? Cloning.isEqual(mBuffer, that.mBuffer) : that.mBuffer == null;

    }

    @Override
    public int hashCode() {
        int result = mMode != null ? mMode.hashCode() : 0;
        result = 31 * result + (mBuffer != null ? mBuffer.hashCode() : 0);
        result = 31 * result + mTimestamp;
        result = 31 * result + (mIsDepthFrame ? 1 : 0);
        return result;
    }
}
