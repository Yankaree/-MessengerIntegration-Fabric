package me.ngcsonsplash.messengerintegration.status;

import com.google.gson.JsonObject;
import me.ngcsonsplash.messengerintegration.MessengerIntegration;
import net.minecraft.server.MinecraftServer;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class StatusTask implements Runnable {

    private final MessengerIntegration plugin;
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    private final MinecraftServer server;

    public StatusTask(MessengerIntegration plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override
    public void run() {
        if (plugin.getWsClient() == null || !plugin.getWsClient().isOpen()) {
            return;
        }

        JsonObject status = new JsonObject();
        status.addProperty("type", "status");
        status.addProperty("tps", getRecentTps());
        status.addProperty("cpu", osBean.getSystemLoadAverage());

        JsonObject memory = new JsonObject();
        memory.addProperty("total", Runtime.getRuntime().totalMemory());
        memory.addProperty("free", Runtime.getRuntime().freeMemory());
        memory.addProperty("max", Runtime.getRuntime().maxMemory());
        status.add("memory", memory);

        plugin.getWsClient().send(status.toString());
    }

    private double getRecentTps() {
        return server.getTps()[0];
    }
}
