package com.spookybox.graphics;

import com.spookybox.camera.KinectFrame;
import com.spookybox.camera.Serialization;
import org.openkinect.freenect.FrameMode;

import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ByteBufferToImage {
    public static final int SCREEN_RESOLUTION = 640 * 480;

    private static BufferedImage rgbArrayToImage(FrameMode mode, int[] rgbArray){
        int width = mode.width;
        int height = mode.height;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,0,width,height,rgbArray,0,width);
        return image;
    }

    public static BufferedImage rgbFramesToImage(KinectFrame kinectFrame, KinectFrame kinectFrame1){
        ByteBuffer buffer1 = kinectFrame.clone().getBuffer();
        ByteBuffer buffer2 = kinectFrame1.clone().getBuffer();
        byte[] input = new byte[buffer1.capacity() + buffer2.capacity()];
        buffer1.get(input, 0, buffer1.capacity());
        buffer2.get(input, buffer1.capacity(), buffer2.capacity());
        int length = kinectFrame.getMode().height * kinectFrame.getMode().width;
        int[] rgbArray = new int[length];
        for(int index = 0; index < rgbArray.length; index++){
            int base = index*3;
            rgbArray[index] = 0xFF000000 | input[base] <<  16 | input[base+1] << 8 | input[base+2];
        }
        return rgbArrayToImage(kinectFrame.getMode(), rgbArray);
    }

    public static BufferedImage byteArrayToImage(byte[] bytes) {
        DataBuffer rgbData = new DataBufferByte(bytes, bytes.length);

        int width = 640;
        int height = 480;
        WritableRaster raster = Raster.createInterleavedRaster(
                rgbData, width, height,
                width * 3, // scanlineStride
                3, // pixelStride
                new int[]{0, 1, 2}, // bandOffsets
                null);

        ColorModel colorModel = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[]{8, 8, 8}, // bits
                false, // hasAlpha
                false, // isPreMultiplied
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);

        return new BufferedImage(colorModel, raster, false, null);
    }
}
