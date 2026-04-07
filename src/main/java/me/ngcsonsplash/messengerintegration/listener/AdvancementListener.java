package me.ngcsonsplash.messengerintegration.listener;

import me.ngcsonsplash.messengerintegration.bridge.BridgeClient;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;

public class AdvancementListener {

    private final BridgeClient bridgeClient;

    public AdvancementListener(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    public void register() {
        // Fabric doesn't have PlayerAdvancementDoneEvent in the same way as Bukkit
        // In a real implementation, you'd hook into AdvancementPayload from Fabric API
        // For now, this is a placeholder
    }

    // Helper method for advancement notification
    public void sendAdvancement(String playerName, String advancementTitle) {
        bridgeClient.send("advancement", playerName + " achieved " + advancementTitle);
    }
}
