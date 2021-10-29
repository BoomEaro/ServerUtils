package ru.boomearo.serverutils;

import org.bukkit.plugin.java.JavaPlugin;

import ru.boomearo.serverutils.commands.serverutils.ServerUtilsExecutor;
import ru.boomearo.serverutils.listeners.CommandsListener;
import ru.boomearo.serverutils.utils.own.NetworkWatcher;

public class ServerUtils extends JavaPlugin {

    private final NetworkWatcher watcher = new NetworkWatcher();

    private static ServerUtils instance = null;

    @Override
    public void onEnable() {
        instance = this;

        this.watcher.register();

        getServer().getPluginManager().registerEvents(new CommandsListener(), this);

        getCommand("serverutils").setExecutor(new ServerUtilsExecutor());

        getLogger().info("Плагин успешно включен");
    }

    @Override
    public void onDisable() {
        this.watcher.unregister();

        getLogger().info("Плагин успешно выключен");
    }

    public static ServerUtils getInstance() {
        return instance;
    }

}
