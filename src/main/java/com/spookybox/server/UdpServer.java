package com.spookybox.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpServer {

    private final int mSocketPort;
    public UdpServer(int socket){
        mSocketPort = socket;
        serverThread.start();
    }

    private DatagramSocket mSocket;
    private Thread serverThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                mSocket = new DatagramSocket(mSocketPort);
                InetAddress ipAddress = mSocket.getInetAddress();
                int serverPort = mSocket.getPort();
                System.out.println("Server started");
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
                mSocket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                buffer = "Received Hello".getBytes();
                packet = new DatagramPacket(buffer, buffer.length, address, port);
                mSocket.send(packet);
                mSocket.close();
                System.out.println("Transmission complete");
            } catch (SocketException e) {
                e.printStackTrace();
                System.err.println("Failed to create socket");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to handle packet received");
            }
        }
    });


}

