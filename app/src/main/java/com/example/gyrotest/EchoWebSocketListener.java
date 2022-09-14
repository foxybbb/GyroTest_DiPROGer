package com.example.gyrotest;

import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class EchoWebSocketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        webSocket.send("Hello, it's me");
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
       Log.d("onMessage", text);

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        Log.d("onMessage", bytes.toString());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        webSocket.close(NORMAL_CLOSURE_STATUS, null);

    }
}
