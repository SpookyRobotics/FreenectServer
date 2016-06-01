package com.spookybox.camera;

import org.openkinect.freenect.FrameMode;

import java.nio.ByteBuffer;

public class Cloning {

    public static FrameMode clone(FrameMode original) {
        FrameMode clone = new FrameMode();
        clone.reserved = original.reserved;
        clone.resolution = original.resolution;
        clone.format = original.format;
        clone.bytes = original.bytes;
        clone.width = original.width;
        clone.height = original.height;
        clone.dataBitsPerPixel = original.dataBitsPerPixel;
        clone.paddingBitsPerPixel = original.paddingBitsPerPixel;
        clone.framerate = original.framerate;
        clone.valid = original.valid;
        return clone;
    }

    public static ByteBuffer clone(ByteBuffer original) {
        // ByteBuffer clone = ByteBuffer.allocate(original.capacity());
        byte[] buffer = new byte[original.capacity()];
        int start = original.position();
        original.rewind();
        original.get(buffer);
        original.position(start);
        ByteBuffer clone = ByteBuffer.wrap(buffer);
        return clone;
    }

    public static boolean isEqual(ByteBuffer buffer, ByteBuffer buffer1) {
        for(int index = 0; index < buffer.capacity(); index++){
            if(buffer1.get(index) != buffer.get(index)){
                return false;
            }
        }
        return true;
    }
}
