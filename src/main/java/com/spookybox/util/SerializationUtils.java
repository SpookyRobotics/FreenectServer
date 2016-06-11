package com.spookybox.util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SerializationUtils {

    public static byte[] toByteArray(List<Byte> input) {
        byte[] bytes = new byte[input.size()];
        for(int index = 0; index< input.size(); index++){
            bytes[index] = input.get(index);
        }
        return bytes;
    }

    public static List<Byte> toByteList(byte[] serialized) {
        ArrayList<Byte> result = new ArrayList<>();
        for(int index = 0; index < serialized.length; index++){
            result.add(serialized[index]);
        }
        return result;
    }

    public static List<Integer> toIntegerList(int[] serialized) {
        ArrayList<Integer> result = new ArrayList<>();
        for(int index = 0; index < serialized.length; index++){
            result.add(Integer.valueOf(serialized[index]));
        }
        return result;
    }

    public static int[][] toMatrix(List<Integer> bytes, int width, int height){
        if(bytes.size() % width != 0){
            System.out.println("Non square transformation -> " + bytes.size() % width + " ignored");
        }
        int[][] result = new int[height][width];
        for(int widthIndex = 0; widthIndex < width; widthIndex++){
            for(int heightIndex = 0; heightIndex < height; heightIndex++){
                result[heightIndex][widthIndex] = bytes.get(width * heightIndex + widthIndex);
            }
        }
        return result;
    }


}
