package me.ngcsonsplash.messengerintegration.listener;

import me.ngcsonsplash.messengerintegration.bridge.BridgeClient;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

public class CommandListener {

    private final BridgeClient bridgeClient;

    public CommandListener(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<ServerCommandSource> sayBuilder =
                LiteralArgumentBuilder.<ServerCommandSource>literal("say")
                    .then(
                        RequiredArgumentBuilder.<ServerCommandSource, String>argument("message", StringArgumentType.greedyString())
                            .executes(context -> {
                                String message = StringArgumentType.getString(context, "message");
                                bridgeClient.send("say", message);
                                return 1;
                            })
                    );
            dispatcher.register(sayBuilder);
        });
    }
}
