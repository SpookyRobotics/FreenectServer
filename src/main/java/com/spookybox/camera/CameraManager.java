package com.spookybox.camera;

import com.spookybox.tilt.TiltManager;
import com.spookybox.util.SelectiveReceiver;
import com.spookybox.util.ThreadUtils;
import org.openkinect.freenect.*;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.spookybox.util.ThreadUtils.sleep;

public class CameraManager {
    private final Device mKinect;
    private final TiltManager mTiltManager;
    private boolean isTerminating = true;
    private Optional<Thread> mDepthThread = Optional.empty();
    private Optional<Thread> mRgbThread = Optional.empty();
    private Optional<Thread> mConsumerThread = Optional.empty();
    private List<Runnable> mOnStartListeners = new ArrayList<>();
    private final List<Consumer<KinectFrame>> mDepthConsumers = new ArrayList<>();
    private final List<Consumer<KinectFrame>> mRgbConsumers = new ArrayList<>();

    public CameraManager(Device kinect){
        if(kinect == null){
            throw new IllegalArgumentException("Kinect is null");
        }
        mKinect = kinect;
        mTiltManager = new TiltManager(mKinect);
        stop();
    }

    public void setTilt(int degrees){
        mTiltManager.moveAndWait(degrees);
    }

    public void addDepthConsumer(Consumer<KinectFrame> consumer){
        mDepthConsumers.add(consumer);
    }

    public void addRgbConsumer(Consumer<KinectFrame> consumer){
        mRgbConsumers.add(consumer);
    }

    public void startCapture(){
        isTerminating = false;
        startDepthCapture();
        startRgbCapture();
        mOnStartListeners.forEach(Runnable::run);
        mOnStartListeners.clear();
    }

    private void startRgbCapture() {
        Object awaitStart = new Object();
        mKinect.setVideoFormat(VideoFormat.RGB, Resolution.MEDIUM);
        mRgbThread = Optional.of(new Thread(() -> {
            VideoHandler receiver = (mode, frame, timestamp) -> {
                if(isTerminating || frame == null){
                    return;
                }
                for (Consumer<KinectFrame> rgbConsumer : mRgbConsumers) {
                    rgbConsumer.accept(new KinectFrame(false, mode, frame, timestamp));
                }
            };
            while(!(mKinect.startVideo(receiver) == 0)){
                System.out.println("Restarting depth");
                sleep(50);
            }
            notifyOnObject(awaitStart);
        }));
        mRgbThread.get().start();
        waitOnObject(awaitStart);
    }

    private void startDepthCapture() {
        Object awaitStart = new Object();
        mKinect.setDepthFormat(DepthFormat.D11BIT);
        mDepthThread = Optional.of(new Thread(() -> {
            DepthHandler receiver = (mode, frame, timestamp) -> {
                if(isTerminating || frame == null){
                    return;
                }
                for (Consumer<KinectFrame> depthConsumer : mDepthConsumers) {
                    depthConsumer.accept(new KinectFrame(true, mode, frame, timestamp));
                }

            };
            while(!(mKinect.startDepth(receiver) == 0)){
                System.out.println("Restarting depth");
                sleep(50);
            };
            notifyOnObject(awaitStart);
        }));
        mDepthThread.get().start();
        waitOnObject(awaitStart);
    }

    private void notifyOnObject(Object lock){
        synchronized (lock){
            lock.notify();
        }
    }

    private void waitOnObject(Object lock){
        synchronized (lock){
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void stop(){
        isTerminating = true;
        mKinect.stopVideo();
        mKinect.stopDepth();
        ThreadUtils.joinThread(mRgbThread);
        ThreadUtils.joinThread(mDepthThread);
        ThreadUtils.joinThread(mConsumerThread);
        mRgbThread = Optional.empty();
        mDepthThread = Optional.empty();
        mConsumerThread = Optional.empty();
    }

    public boolean isStopping() {
        return isTerminating;
    }

    public int getAttachedKinects() {
        return mKinect != null ? 1 : 0;
    }

    public void addOnStartListener(Runnable r) {
        if(isTerminating){
            mOnStartListeners.add(r);
        }
        else{
            r.run();
        }
    }

    public int getTilt() {
        return (int) mTiltManager.getTiltAngle();
    }
}
