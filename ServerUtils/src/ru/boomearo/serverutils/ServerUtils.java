package ru.boomearo.serverutils;

import org.bukkit.plugin.java.JavaPlugin;

import ru.boomearo.serverutils.commands.CmdExecutor;
import ru.boomearo.serverutils.listeners.CommandBlockProtectListener;
import ru.boomearo.serverutils.utils.NetworkWatcher;

public class ServerUtils extends JavaPlugin {

	private final NetworkWatcher watcher = new NetworkWatcher();
	
	public void onEnable() {
		instance = this;
		
		this.watcher.register();
		
	    getServer().getPluginManager().registerEvents(new CommandBlockProtectListener(), this);
		
	    getCommand("serverutils").setExecutor(new CmdExecutor());
		
	    getLogger().info("Плагин успешно включен");
	}
	
	public void onDisable() {
		this.watcher.unregister();
		
	    getLogger().info("Плагин успешно выключен");
	}
	
	private static ServerUtils instance = null;
	public static ServerUtils getContext() { 
		if (instance != null) return instance; return null; 
	}
	
}
