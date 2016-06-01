package com.spookybox.camera;

import org.openkinect.freenect.FrameMode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Serialization {

    public static final int INT_BYTE_LENGTH = 4;
    public static final int SHORT_BYTE_LENGTH = 2;
    public static final int BOOLEAN_BYTE_LENGTH = 1;

    public static List<Byte> shortToByteList(short value) {
        byte[] array = ByteBuffer.allocate(2).putShort(value).array();
        ArrayList<Byte> result = new ArrayList<>(2);
        for(byte b : array){
            result.add(b);
        }
        return result;
    }

    public static short byteListToShort(List<Byte> in) {
        if(in.size() != SHORT_BYTE_LENGTH){
            throw new IllegalArgumentException("in must have length "+ SHORT_BYTE_LENGTH);
        }
        byte[] array = new byte[in.size()];
        for(int index = 0; index < in.size(); index++){
            array[index] = in.get(index);
        }
        return ByteBuffer.wrap(array).getShort() ;
    }

    public static List<Byte> intToByteList(int value) {
        byte[] array = ByteBuffer.allocate(INT_BYTE_LENGTH).putInt(value).array();
        ArrayList<Byte> result = new ArrayList<>(INT_BYTE_LENGTH);
        for(byte b : array){
            result.add(b);
        }
        return result;
    }

    public static int byteListToInt(List<Byte> in) {
        if(in.size() != INT_BYTE_LENGTH){
            throw new IllegalArgumentException("in must have length "+INT_BYTE_LENGTH);
        }
        byte[] array = new byte[in.size()];
        for(int index = 0; index < in.size(); index++){
            array[index] = in.get(index);
        }
        return ByteBuffer.wrap(array).getInt() ;
    }

    public static List<Byte> booleanToByteList(boolean value) {
        byte byteValue = 0;
        if(value){
            byteValue = 1;
        }
        return Arrays.asList(byteValue);
    }

    public static boolean byteListToBoolean(List<Byte> in){
        if(in.size() != BOOLEAN_BYTE_LENGTH){
            throw new IllegalArgumentException("in must have length "+ BOOLEAN_BYTE_LENGTH);
        }
        if(in.get(0) == (byte) 0){
            return false;
        }
        return true;
    }


    public static List<Byte> byteBufferToByteList(ByteBuffer buffer) {
        byte[] array = buffer.array();
        int size = array.length;
        List<Byte> sizeList = intToByteList(array.length);

        ArrayList<Byte> resultList = new ArrayList<>();
        resultList.addAll(sizeList);
        for(int index =0; index < size; index++){
            resultList.add(array[index]);
        }
        return resultList;
    }

    public static ByteBuffer byteListToByteBuffer(List<Byte> in){
        ByteBuffer result = ByteBuffer.allocate(in.size());
        for(byte b : in){
            result.put(b);
        }
        return result;
    }

    public static int extractBufferSize(final List<Byte> in, final int start){
        List<Byte> sizeList = in.subList(start, start + INT_BYTE_LENGTH);
        return byteListToInt(sizeList);
    }


    public static List<Byte> extractBufferList(final List<Byte> in, final int start) {
        int index = start;
        int size = extractBufferSize(in, start);
        if(size < 0 || size + INT_BYTE_LENGTH > in.size()){
            throw new IllegalArgumentException("invalid size received");
        }
        index += INT_BYTE_LENGTH;

        List<Byte> bufferData = in.subList(index, index + size);
        if(size != bufferData.size()){
            throw new IllegalArgumentException("size not expected value: Expected "+size+" received "+bufferData.size());
        }
        return bufferData;
    }

    public static int extractKinectFrames(List<Byte> bytes,
                                            int depthFramesStart,
                                            int numDepthFrames,
                                            int numRgbFrames,
                                            List<KinectFrame> depthFramesResult,
                                            List<KinectFrame> rgbFramesResult) {
        if(depthFramesResult == null || rgbFramesResult == null){
            throw new IllegalArgumentException("Result lists must not be null");
        }
        int read = extractKinectFrame(bytes, depthFramesStart, depthFramesResult, numDepthFrames);
        int rgbFramesStart = depthFramesStart + read;
        read += extractKinectFrame(bytes, rgbFramesStart, rgbFramesResult, numRgbFrames);
        return read;
    }

    public static int extractKinectFrame(List<Byte> bytes,
                                          int framesStart,
                                          final List<KinectFrame> result,
                                          int framesToRead) {
        if(result == null || !result.isEmpty()){
            throw new IllegalArgumentException("Result must not be null");
        }
        int framesRead = 0;
        int bytesRead = 0;
        int index = framesStart;
        while(index < bytes.size() && framesRead != framesToRead){
            int startIndex = index;
            List<Byte> isDepthFrameList = bytes.subList(index, index + BOOLEAN_BYTE_LENGTH);
            boolean isDepthFrame = byteListToBoolean(isDepthFrameList);
            index += BOOLEAN_BYTE_LENGTH;

            List<Byte> modeList = bytes.subList(index, index + FrameMode.FRAME_MODE_BYTE_LENGTH);
            FrameMode mode = FrameMode.byteListToFrameMode(modeList);
            index += FrameMode.FRAME_MODE_BYTE_LENGTH;

            List<Byte> timeStampList = bytes.subList(index, index + INT_BYTE_LENGTH);
            int timeStamp = byteListToInt(timeStampList);
            index += INT_BYTE_LENGTH;

            List<Byte> bufferList = extractBufferList(bytes, index);
            index += INT_BYTE_LENGTH;
            ByteBuffer frame = byteListToByteBuffer(bufferList);
            index += bufferList.size();

            KinectFrame kinectFrame = new KinectFrame(isDepthFrame, mode, frame, timeStamp);
            framesRead += 1;
            result.add(kinectFrame);
            bytesRead += index - startIndex;

        }
        return bytesRead;
    }

    public static List<Short> byteListToShortList(List<Byte> byteStream) {
        if(byteStream.size() %2 != 0){
            throw new IllegalArgumentException("Byte stream size must be multiple of 2");
        }
        List<Short> result = new ArrayList<>();
        for(int index = 0; index < byteStream.size(); index+=2){
            result.add(byteListToShort(byteStream.subList(index,index+2)));
        }
        return result;
    }
}
