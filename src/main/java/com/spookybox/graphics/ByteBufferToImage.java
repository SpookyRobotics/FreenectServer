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
