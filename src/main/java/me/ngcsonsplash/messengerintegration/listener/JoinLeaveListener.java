package me.ngcsonsplash.messengerintegration.listener;

import me.ngcsonsplash.messengerintegration.bridge.BridgeClient;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class JoinLeaveListener {

    private final BridgeClient bridgeClient;

    public JoinLeaveListener(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    public void register() {
        // Join event
        ServerPlayerEvents.AFTER_PLAYER_SPAWN.register((player, server) -> {
            String playerName = player.getName().getString();
            bridgeClient.send("join", playerName + " joined the server");
        });

        // Leave event
        ServerPlayerEvents.DISCONNECT.register((player, server) -> {
            String playerName = player.getName().getString();
            bridgeClient.send("leave", playerName + " left the server");
        });
    }
}
