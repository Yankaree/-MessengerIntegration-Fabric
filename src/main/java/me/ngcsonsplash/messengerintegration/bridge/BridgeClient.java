package me.ngcsonsplash.messengerintegration.bridge;

import com.google.gson.JsonObject;
import me.ngcsonsplash.messengerintegration.websocket.FabricWebSocketClient;

public class BridgeClient {

    private final FabricWebSocketClient wsClient;

    public BridgeClient(FabricWebSocketClient wsClient) {
        this.wsClient = wsClient;
    }

    /**
     * Gửi tin nhắn dạng JSON qua WebSocket
     */
    public void send(String type, String content) {
        if (wsClient == null || !wsClient.isOpen()) {
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.addProperty("content", content);

        wsClient.send(json.toString());
    }

    /**
     * Gửi yêu cầu status đến Messenger
     */
    public void requestStatus() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "status_request");
        send(json.toString());
    }

    /**
     * Gửi command response đến Messenger
     */
    public void sendCommandResponse(String message) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "command_response");
        json.addProperty("message", message);
        send(json.toString());
    }

    /**
     * Gửi tin nhắn trực tiếp (không qua type/content wrapper)
     */
    public void sendDirect(String message) {
        if (wsClient != null && wsClient.isOpen()) {
            wsClient.send(message);
        }
    }
}
