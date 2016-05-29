package com.spookybox.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.function.Consumer;

public class TcpServer {
    private final int mPortNumber;
    private final ServerSocket mSocket;
    private final String mServerName;
    private final Consumer<TcpServer> mListener;
    private Optional<Socket> mClientSocket = Optional.empty();

    private final Thread serverThread = new Thread(new java.lang.Runnable() {
        @Override
        public void run() {
            try {
                while(true) {
                    System.out.println(mServerName + " Waiting for client");
                    Socket clientSocket = mSocket.accept();
                    System.out.println(mServerName + " Client connected");
                    mClientSocket = Optional.of(clientSocket);
                    mListener.accept(TcpServer.this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    public TcpServer(final String serverName,
                     final int port,
                     final Consumer<TcpServer> listener) throws IOException {
        mPortNumber = port;
        mSocket = new ServerSocket(mPortNumber);
        mListener = listener;
        mServerName = serverName;
    }

    public boolean isConnected(){
        return mClientSocket.isPresent();
    }

    public void start(){
        serverThread.start();
    }

    public void transmit(int[] buffer,int offset,int bytesToWrite){
        if(isConnected()){
            mClientSocket.ifPresent((client) -> {
                try {
                    BufferedOutputStream output = new BufferedOutputStream(client.getOutputStream(), 25344);
                    for (int index = offset; index < offset + bytesToWrite; index++) {
                        output.write(buffer[index]);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            });
        }
    }
}
