package org.openkinect.freenect;

import com.spookybox.camera.KinectFrame;
import com.spookybox.frameConsumers.CameraSnapShot;
import com.spookybox.util.FileUtils;
import com.spookybox.util.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileWriteTest {
    private final String SAVE_FILE = "fileWriteTest.out";

    private KinectFrame mKinectFrame1;
    private KinectFrame mKinectFrame2;
    private CameraSnapShot mSnapShot;

    @Before
    public void setUp(){
        mKinectFrame1 = new KinectFrame(true, buildFrameMode(), buildByteBuffer(), buildTimestamp());
        mKinectFrame2 = new KinectFrame(false, buildAlternateFrameMode(), buildByteBuffer(), buildTimestamp());
        List<KinectFrame> rgbFrames = new ArrayList<>();
        List<KinectFrame> depthFrames = new ArrayList<>();
        depthFrames.add(mKinectFrame1);
        rgbFrames.add(mKinectFrame2);
        mSnapShot = new CameraSnapShot(rgbFrames, depthFrames);
    }

    @Test
    public void testWriteToFile() throws IOException, ClassNotFoundException {
        File saveFile = new File(SAVE_FILE);
        saveFile.delete();
        byte[] buffer = SerializationUtils.toByteArray(CameraSnapShot.cameraSnapShotToByteList(mSnapShot));
        FileUtils.write(saveFile, buffer);
        List<Byte> byteList = FileUtils.readInputFile(SAVE_FILE);
        for(int index = 0; index < buffer.length; index++){
            assertTrue("Index: "+index + " doesn't match" ,buffer[index] == byteList.get(index));
        }
        CameraSnapShot returnedValue = CameraSnapShot.byteListToCameraSnapShot(byteList);
        assertEquals(mSnapShot, returnedValue);
    }

    private int buildTimestamp() {
        return -1;
    }

    private ByteBuffer buildByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put((byte) 0);
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);
        buffer.put((byte) 4);
        return buffer;
    }

    private FrameMode buildFrameMode() {
        FrameMode frameMode = new FrameMode();
        frameMode.reserved = 1;
        frameMode.resolution = 2;
        frameMode.format = 3;
        frameMode.bytes = 4;
        frameMode.width = 5;
        frameMode.height = 6;
        frameMode.dataBitsPerPixel = 7;
        frameMode.paddingBitsPerPixel = 8;
        frameMode.framerate = 9;
        frameMode.valid = 10;
        return frameMode;
    }

    private FrameMode buildAlternateFrameMode() {
        FrameMode frameMode = new FrameMode();
        frameMode.reserved = 11;
        frameMode.resolution = 12;
        frameMode.format = 13;
        frameMode.bytes = 14;
        frameMode.width = 15;
        frameMode.height = 16;
        frameMode.dataBitsPerPixel = 17;
        frameMode.paddingBitsPerPixel = 18;
        frameMode.framerate = 19;
        frameMode.valid = 20;
        return frameMode;
    }
}
