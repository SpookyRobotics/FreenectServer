package com.spookybox.client;

import com.spookybox.camera.KinectFrame;
import com.spookybox.graphics.ByteBufferToImage;
import com.spookybox.graphics.DisplayCanvas;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TcpClientMain {
    private final int port;
    private String IP_ADDRESS = "192.168.1.6";
    private DisplayCanvas mRgbCanvas;

    public static void main(String[] args) {
        new TcpClientMain(4445).start();
    }
    public TcpClientMain(int port){
        this.port = port;
    }


    public  void start(){
        DisplayCanvas[] canvases = DisplayCanvas.initWindow();
        mRgbCanvas = canvases[0];
        Runnable r = () -> startClient(port);
        Thread t = new Thread(r);
        t.start();
    }
    private  void startClient(int port){
        try{
            System.out.println("Connecting to -> " + IP_ADDRESS);
            InetAddress address = InetAddress.getByName(IP_ADDRESS);
            Socket socket = new Socket(address,port);
            InputStream inputStream = socket.getInputStream();
            List<Byte> bufferList = new ArrayList<>();
            int bufferSize = 25344*4;
            byte[] buffer = new byte[bufferSize];
            while (true){
                int read = inputStream.read(buffer);
                while(read != -1){
                    for(int index = 0; index < read; index++){
                        bufferList.add(buffer[index]);
                    }
                    read = inputStream.read(buffer);
                }
                System.out.println("Read image "+System.currentTimeMillis());
                bufferList.clear();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
