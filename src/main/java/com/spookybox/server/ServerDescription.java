package com.spookybox.server;

import java.util.function.Consumer;

public class ServerDescription {
    public final String mServerName;
    public final int mPort;
    public final Consumer<TcpServer> mListener;

    public ServerDescription(final String serverName,
                             final int port,
                             final Consumer<TcpServer> listener) {
        mServerName = serverName;
        mPort = port;
        mListener = listener;
    }
}
