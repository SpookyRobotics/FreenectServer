package com.spookybox.frameConsumers;

import com.spookybox.applications.ArffData;
import com.spookybox.applications.KinectFrameConsumer;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class ArffCreator extends KinectFrameConsumer<ArffData> {
    private PrintWriter mOutputStream;
    private Consumer<ArffData> mDepthConsumer;
    private Consumer<ArffData> mRgbConsumer;
    private int mFileIndex = 0;
    private int mWroteRgb = 0;
    private final Object mWriteLock = new Object();
    public ArffCreator(){
        mOutputStream = getOutputStream();
        mOutputStream.print(getArffDataHeader());
        mDepthConsumer = arffData -> {
            writeArffData(arffData);
        };
        mRgbConsumer = arffData -> {
            writeArffData(arffData);
        };
    }

    private void writeArffData(ArffData arffData) {
        if(true) return;
        synchronized (mWriteLock){
            String data = toArffData(arffData);
            mOutputStream.print(data);
            if(!arffData.isDepth){
                mWroteRgb += 1;
                if(mWroteRgb >= 30){
                    mWroteRgb = 0;
                    mOutputStream = getOutputStream();
                }
            }
        }
    }

    private String toArffData(ArffData arffData) {
        StringBuffer out = new StringBuffer();
        if(arffData.isDepth){
            out.append("depth_frame");
        } else {
            out.append("rgb_frame");
        }
        out.append(",");
        out.append(arffData.timestamp).append(",");
        out.append(imageToString(arffData.image));
        return out.toString();
    }

    private PrintWriter getOutputStream() {
        try {
            return new PrintWriter(new FileOutputStream(getSaveFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Invalid state");
    }

    public String getSaveFile() {
        StringBuffer name = new StringBuffer();
        String fileName = new SimpleDateFormat("MM_dd_HH_mm").format(new Date());
        name.append(fileName);
        name.append("_index").append(mFileIndex);
        mFileIndex += 1;
        name.append(".kinect_arff");
        return name.toString();
    }

    private String getArffDataHeader() {
        StringBuffer out = new StringBuffer();
        out.append(getRelation());
        out.append(getAttributes());
        out.append("@DATA\n");
        return out.toString();
    }

    private String imageToString(BufferedImage image) {
        int[] array = new int[640*480];
        image.getRGB(0,0,640,480,array,0,640);
        StringBuffer result = new StringBuffer();
        for(int index = 0; index < array.length; index++){
            result.append(array[index]).append(",");
        }
        return result.toString();
    }

    private String getFileStart(String nameSeed, CameraSnapShot snapShot) {
        return ""+nameSeed+"_"+snapShot.mDepthFrames.size()+"_"+snapShot.mRgbFrames.size();
    }

    public static long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    protected Consumer<ArffData> getRgbConsumer() {
        return mRgbConsumer;
    }

    @Override
    protected Consumer<ArffData> getDepthConsumer() {
        return mDepthConsumer;
    }

    public String getRelation() {
        return "@RELATION hadukan\n\n";
    }

    private String getWekaFrame(ByteBuffer buffer, boolean isDepthFrame, int timestamp) {
        StringBuffer data = new StringBuffer();
        for(int index = 0; index < buffer.capacity(); index++) {
            data.append(buffer.get(index)).append(",");
        }
        data.append(timestamp).append(",");
        if(isDepthFrame){
            data.append("depth_frame");
        } else {
            data.append("rgb_frame");
        }
        data.append("\n");
        return data.toString();
    }

    public String getAttributes() {
        StringBuffer attributeList = new StringBuffer();
        attributeList.append("@ATTRIBUTE class  {rgb_frame,depth_frame}\n");
        attributeList.append("@ATTRIBUTE timestamp   NUMERIC\n");
        for(int index = 0; index < 640*480; index++){
            attributeList.append("@ATTRIBUTE red"+index+" NUMERIC\n");
            attributeList.append("@ATTRIBUTE green"+index+" NUMERIC\n");
            attributeList.append("@ATTRIBUTE blue"+index+" NUMERIC\n");
        }
        return  attributeList.toString();
    }
}
