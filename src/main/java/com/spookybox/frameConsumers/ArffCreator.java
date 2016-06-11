package com.spookybox.frameConsumers;

import com.spookybox.applications.KinectFrameConsumer;
import com.spookybox.camera.KinectFrame;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ArffCreator extends KinectFrameConsumer<ArffData> {
    private final String mFileName;
    private PrintWriter mOutputStream;
    private Consumer<ArffData> mDepthConsumer;
    private Consumer<ArffData> mRgbConsumer;
    private int mFileIndex = 0;
    private int mWroteRgb = 0;
    private final Object mWriteLock = new Object();
    private ExecutorService mExecutor;

    public ArffCreator(){
        mFileName = getDefaultSaveFile();
        init();
    }

    public ArffCreator(String filename){
        mFileName = filename;
        init();
    }

    private void init(){
        mOutputStream = getOutputStream(mFileName);
        queueExecution(() -> mOutputStream.print(getArffDataHeader()));
        mDepthConsumer = arffData -> {
            queueExecution(() -> writeArffData(arffData));
        };
        mRgbConsumer = arffData -> {
            queueExecution(() -> writeArffData(arffData));
        };
    }

    private void queueExecution(Runnable runnable){
        if(mExecutor == null){
            mExecutor = Executors.newFixedThreadPool(1);
        }
        mExecutor.submit(runnable);
    }
    private void writeArffData(ArffData arffData) {
        synchronized (mWriteLock){
            String data = toArffData(arffData);
            mOutputStream.print(data);
            if(!arffData.mIsDepth){
                mWroteRgb += 1;
                if(mWroteRgb >= 30){
                    mWroteRgb = 0;
                    mOutputStream = getOutputStream(mFileName);
                }
            }
        }
    }

    private String toArffData(ArffData arffData) {
        StringBuilder out = new StringBuilder();
        if(arffData.mIsDepth){
            out.append("depth_frame");
        } else {
            out.append("rgb_frame");
        }
        out.append(",");
        out.append(arffData.mTimestamp).append(",");
        for(int heightIndex = 0; heightIndex < arffData.mInputPanels.length; heightIndex++){
            for(int widthIndex = 0; widthIndex < arffData.mInputPanels[0].length; widthIndex++){
                out.append(arffData.mInputPanels[heightIndex][widthIndex]);
                if(widthIndex + 1 != arffData.mInputPanels[0].length){
                    out.append(",");
                }
            }
        }
        out.append("\n");
        return out.toString();
    }

    private PrintWriter getOutputStream(String fileName) {
        try {
            return new PrintWriter(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Invalid state");
    }

    public String getDefaultSaveFile() {
        StringBuilder name = new StringBuilder();
        String fileName = new SimpleDateFormat("MM_dd_HH_mm").format(new Date());
        name.append(fileName);
        name.append("_index").append(mFileIndex);
        mFileIndex += 1;
        name.append(".kinect.arff");
        return name.toString();
    }

    public String getArffDataHeader() {
        StringBuilder out = new StringBuilder();
        out.append(getRelation());
        out.append(getAttributes());
        out.append("@DATA\n");
        return out.toString();
    }

    private String imageToString(BufferedImage image) {
        int[] array = new int[640*480];
        image.getRGB(0,0,640,480,array,0,640);
        StringBuilder result = new StringBuilder();
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
    public ArffData transformDepth(KinectFrame frame) {
        return new ArffData(frame);
    }

    @Override
    protected ArffData transformRgb(KinectFrame frame) {
        return new ArffData(frame);
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
        StringBuilder out = new StringBuilder();
        out.append("@RELATION");
        out.append(" hadukan");
        out.append("\n");
        return out.toString();
    }

    private String getWekaFrame(ByteBuffer buffer, boolean isDepthFrame, int timestamp) {
        StringBuilder data = new StringBuilder();
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

    private static String getAttributes() {
        StringBuilder attributeList = new StringBuilder();
        attributeList.append("@ATTRIBUTE class  {rgb_frame,depth_frame}\n");
        attributeList.append("@ATTRIBUTE mTimestamp   NUMERIC\n");
        for(int index = 0; index < 640*480; index++){
            attributeList.append("@ATTRIBUTE red"+index+" NUMERIC\n");
            attributeList.append("@ATTRIBUTE green"+index+" NUMERIC\n");
            attributeList.append("@ATTRIBUTE blue"+index+" NUMERIC\n");
        }
        return  attributeList.toString();
    }
}
