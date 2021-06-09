package ru.boomearo.serverutils.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import ru.boomearo.serverutils.ServerUtils;

public class CommandBlockProtectListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerCommandEvent(ServerCommandEvent e) {
		CommandSender sender = e.getSender();
		String msg = e.getCommand().toLowerCase();
		if (sender instanceof BlockCommandSender) {
			BlockCommandSender cobl = (BlockCommandSender) sender;
			Location loc = cobl.getBlock().getLocation();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			if (msg.split(" ")[0].contains(":")) {
				cobl.sendMessage("Данный синтаксис запрещен.");
				e.setCancelled(true);
			}
			String[] args = msg.split(" ");
			if (isProtectCommand(args[0])) {
				loc.getBlock().setType(Material.AIR);
				ServerUtils.getInstance().getLogger().info("(Debug) Попытка командного блока выполнить запрещенную команду: '" + e.getCommand() + "'. Координаты: X: " + x + " Y: " + y + " Z: " + z + ". Мир: " + loc.getWorld().getName() + " (Тип блока: " + cobl.getBlock().getType().toString() + ")");
				e.setCancelled(true);
			}
		}
	}

	//TODO Сделать по нормальному
	private boolean isProtectCommand(String cmd) {
		String c = cmd.replace("/", "");
		if (c.equalsIgnoreCase("sudo") ||
				c.equalsIgnoreCase("esudo") || 
				c.equalsIgnoreCase("holograms") || 
				c.equalsIgnoreCase("hd") || 
				c.equalsIgnoreCase("holo") ||
				c.equalsIgnoreCase("hologram") || 
				c.equalsIgnoreCase("stop") || 
				c.equalsIgnoreCase("reload") || 
				c.equalsIgnoreCase("restart") || 
				c.equalsIgnoreCase("execute") || 
				c.equalsIgnoreCase("wl")) {
			return true;
		}
		return false;
	}
	
}
