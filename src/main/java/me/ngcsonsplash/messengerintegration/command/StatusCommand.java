package me.ngcsonsplash.messengerintegration.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.ngcsonsplash.messengerintegration.MessengerIntegration;
import me.ngcsonsplash.messengerintegration.bridge.BridgeClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class StatusCommand {

    private final MessengerIntegration plugin;
    private final BridgeClient bridgeClient;

    public StatusCommand(MessengerIntegration plugin, BridgeClient bridgeClient) {
        this.plugin = plugin;
        this.bridgeClient = bridgeClient;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            LiteralArgumentBuilder.<ServerCommandSource>literal("msstatus")
                .executes(context -> execute(context))
        );
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (plugin.getWsClient() != null) {
            source.sendFeedback(
                () -> Text.literal("MessengerIntegration status: " +
                    (plugin.getWsClient().isOpen() ? "Connected" : "Disconnected")),
                false
            );
        } else {
            source.sendFeedback(() -> Text.literal("MessengerIntegration status: Not initialized"), false);
        }

        return 1;
    }
}
