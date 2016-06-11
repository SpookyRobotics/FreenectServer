package com.spookybox.server;


import com.sun.net.httpserver.HttpHandler;

public abstract class ContextHandler {
    abstract String getContext();
    abstract HttpHandler getHandler();
}
