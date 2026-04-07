package me.ngcsonsplash.messengerintegration;

import me.ngcsonsplash.messengerintegration.bridge.BridgeClient;
import me.ngcsonsplash.messengerintegration.command.StatusCommand;
import me.ngcsonsplash.messengerintegration.listener.*;
import me.ngcsonsplash.messengerintegration.status.StatusTask;
import me.ngcsonsplash.messengerintegration.websocket.FabricWebSocketClient;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessengerIntegration implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessengerIntegration.class);

    public static final String MOD_ID = "messengerintegration";

    private FabricWebSocketClient wsClient;
    private BridgeClient bridgeClient;
    private MinecraftServer server;

    @Override
    public void onInitialize() {
        LOGGER.info("MessengerIntegration initializing...");

        String websocketUrl = loadConfig();

        wsClient = new FabricWebSocketClient(websocketUrl, this);
        wsClient.connect();

        bridgeClient = new BridgeClient(wsClient);

        new ChatListener(bridgeClient).register();
        new JoinLeaveListener(bridgeClient).register();
        new DeathListener(bridgeClient).register();
        new AdvancementListener(bridgeClient).register();
        new CommandListener(bridgeClient).register();

        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new StatusCommand(this, bridgeClient).register(dispatcher);
        });

        LOGGER.info("MessengerIntegration enabled!");
    }

    private String loadConfig() {
        try {
            Path configPath = Paths.get("config/config.yml");
            if (Files.exists(configPath)) {
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Handle "url: ..." or "websocket.url: ..."
                        if (line.trim().startsWith("url:")) {
                            return line.split(":", 2)[1].trim();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load config, using default: ws://100.x.x.x:3000");
        }
        return "ws://100.x.x.x:3000";
    }

    public FabricWebSocketClient getWsClient() {
        return wsClient;
    }

    public BridgeClient getBridgeClient() {
        return bridgeClient;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }
}
