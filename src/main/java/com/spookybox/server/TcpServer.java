package com.spookybox.server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class TcpServer {
    private static final int BACKLOG_QUEUE = 2;
    private static final int SHUDOWN_TASK_COMPLETION_DELAY_SECONDS = 0;
    private final HttpServer server;

    public TcpServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(getPort()), BACKLOG_QUEUE);
        } catch (IOException e) {
            throw new IllegalAccessError(e.toString());
        }
        server.setExecutor(Executors.newSingleThreadExecutor());
        for(ContextHandler c : getHandlers()){
            server.createContext(c.getContext(), c.getHandler());
        }
    }

    protected abstract int getPort();
    protected abstract List<ContextHandler> getHandlers();

    public void start(){
        System.out.println("Starting server " + getName());
        server.start();
    }

    protected String getName(){
        return getClass().getName();
    }

    public void stop() {
        server.stop(SHUDOWN_TASK_COMPLETION_DELAY_SECONDS);
    }
}
