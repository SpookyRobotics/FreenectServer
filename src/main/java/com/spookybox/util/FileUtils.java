package com.spookybox.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    public static List<Byte> readInputFile(String input) {
        File in = new File(input);
        if(!in.exists()){
            System.err.println(input + " does not exist");
            return Collections.emptyList();
        }
        FileInputStream fileInputStream;
        BufferedInputStream inputStream;
        try {
            fileInputStream = new FileInputStream(input);
            inputStream = new BufferedInputStream(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create input streams");
            return null;
        }
        List<Byte> read = new ArrayList<>();
        try {
            int bytesRead = -1;
            byte[] buffer = new byte[600];
            bytesRead = inputStream.read(buffer);
            while(bytesRead != -1){
                for(int i = 0; i < bytesRead; i++){
                    read.add(buffer[i]);
                }
                bytesRead = inputStream.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return read;
    }

    public static void write(File saveFile, byte[] buffer) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveFile));
            outputStream.write(buffer);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
