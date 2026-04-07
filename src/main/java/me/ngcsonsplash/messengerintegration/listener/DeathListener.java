package me.ngcsonsplash.messengerintegration.listener;

import me.ngcsonsplash.messengerintegration.bridge.BridgeClient;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class DeathListener {

    private final BridgeClient bridgeClient;

    public DeathListener(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    public void register() {
        ServerPlayerEvents.AFTER_DEATH.register((player, server) -> {
            String playerName = player.getName().getString();
            bridgeClient.send("death", playerName + " died");
        });
    }
}
