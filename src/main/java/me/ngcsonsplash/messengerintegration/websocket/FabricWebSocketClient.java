package me.ngcsonsplash.messengerintegration.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import me.ngcsonsplash.messengerintegration.MessengerIntegration;
import net.minecraft.server.MinecraftServer;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FabricWebSocketClient extends WebSocketListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FabricWebSocketClient.class);

    private final MessengerIntegration plugin;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private OkHttpClient client;
    private WebSocket webSocket;
    private final AtomicBoolean manuallyClosed = new AtomicBoolean(false);
    private final String websocketUrl;
    private Request request;

    public FabricWebSocketClient(String url, MessengerIntegration plugin) {
        this.websocketUrl = url;
        this.plugin = plugin;

        // Configure OkHttpClient with WebSocket support
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // No timeout for WebSocket
                .build();

        this.request = new Request.Builder()
                .url(websocketUrl)
                .build();
    }

    public void connect() {
        LOGGER.info("Connecting to WebSocket: {}", websocketUrl);
        client.newWebSocket(request, this);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        LOGGER.info("WebSocket opened");
        this.webSocket = webSocket;
        startKeepAlive();
    }

    private void startKeepAlive() {
        scheduler.scheduleAtFixedRate(() -> {
            if (webSocket != null) {
                JsonObject ping = new JsonObject();
                ping.addProperty("type", "ping");
                send(ping.toString());
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onText(WebSocket webSocket, String message) {
        MinecraftServer server = plugin.getServer();
        if (server != null) {
            server.execute(() -> {
                try {
                    JsonObject json = JsonParser.parseString(message).getAsJsonObject();
                    if (json.has("type")) {
                        String type = json.get("type").getAsString();

                        if ("messenger_message".equals(type)) {
                            if (json.has("sender") && json.has("message")) {
                                String sender = json.get("sender").getAsString();
                                String msg = json.get("message").getAsString();

                                if ("/status".equalsIgnoreCase(msg.trim())) {
                                    server.getCommandManager().execute(
                                        server.getCommandSource(),
                                        "msstatus"
                                    );
                                    return;
                                }

                                String broadcastMsg = "[Bridge] " + sender + ": " + msg;
                                // Broadcast to players
                                server.getPlayers().forEach(player ->
                                    player.sendMessage(
                                        net.minecraft.text.Text.literal(broadcastMsg)
                                    )
                                );
                                // Log to console
                                LOGGER.info("Received from Messenger: {} -> {}", sender, msg);
                            }
                        } else if ("command_response".equals(type)) {
                            if (json.has("message")) {
                                String response = json.get("message").getAsString();
                                String msg = "[Bridge Bot]: " + response;
                                server.getPlayers().forEach(player ->
                                    player.sendMessage(net.minecraft.text.Text.literal(msg))
                                );
                                LOGGER.info("Command response: {}", response);
                            }
                        } else if ("status_request".equals(type)) {
                            server.getCommandManager().execute(
                                server.getCommandSource(),
                                "msstatus"
                            );
                            LOGGER.info("Status request received from Messenger");
                        } else {
                            String msg = "[Bridge Bot Info]: " + message;
                            server.getPlayers().forEach(player ->
                                player.sendMessage(net.minecraft.text.Text.literal(msg))
                            );
                            LOGGER.info("Bridge Info: {}", message);
                        }
                    } else {
                        String msg = "[Bridge Bot Raw]: " + message;
                        server.getPlayers().forEach(player ->
                            player.sendMessage(net.minecraft.text.Text.literal(msg))
                        );
                        LOGGER.info("Raw message from Messenger: {}", message);
                    }
                } catch (JsonSyntaxException | IllegalStateException e) {
                    LOGGER.error("Invalid message from bot: {}", message, e);
                    String errorMsg = "[Bridge Bot Error]: Invalid message from bot: " + message;
                    server.getPlayers().forEach(player ->
                        player.sendMessage(net.minecraft.text.Text.literal(errorMsg))
                    );
                }
            });
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        LOGGER.info("WebSocket closing: {}", reason);
        webSocket.sendClose(code, reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        LOGGER.warn("WebSocket closed: {}", reason);
        if (!manuallyClosed.get()) {
            scheduler.schedule(() -> {
                LOGGER.info("Attempting reconnect...");
                connect();
            }, 5, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        LOGGER.warn("WebSocket error: {}", t.getMessage());
    }

    public void send(String message) {
        if (webSocket != null) {
            webSocket.send(message, null);
        }
    }

    public void close() {
        manuallyClosed.set(true);
        if (webSocket != null) {
            webSocket.sendClose(1000, "Closing");
        }
        scheduler.shutdownNow();
    }

    public boolean isOpen() {
        return webSocket != null;
    }
}
