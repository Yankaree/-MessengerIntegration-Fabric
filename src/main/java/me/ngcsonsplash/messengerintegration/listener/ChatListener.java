package me.ngcsonsplash.messengerintegration.listener;

import me.ngcsonsplash.messengerintegration.bridge.BridgeClient;
import net.fabricmc.fabric.api.chat.v2.ChatCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ChatListener {

    private final BridgeClient bridgeClient;

    public ChatListener(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    public void register() {
        // Listen for server chat events
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // We'll hook into the chat event via Fabric API
        });
    }

    // Helper method to send chat message to bridge
    public void sendChatMessage(String playerName, String message) {
        bridgeClient.send("chat", playerName + ": " + message);
    }
}
