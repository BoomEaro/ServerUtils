package ru.boomearo.serverutils.listeners;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.spigotmc.SpigotConfig;

import java.util.LinkedHashSet;

public class CommandsListener implements Listener {

    //Запрещаем командному блоку команды с двоеточием.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommandEvent(ServerCommandEvent e) {
        CommandSender sender = e.getSender();

        if (!(sender instanceof BlockCommandSender)) {
            return;
        }

        String msg = e.getCommand().toLowerCase();

        if (msg.split(" ")[0].contains(":")) {
            e.setCancelled(true);
            sender.sendMessage("Данный синтаксис запрещен.");
        }
    }

    //Запрет использования команд с двоеточиями.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocessEventSy(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getMessage().split(" ")[0].contains(":")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(SpigotConfig.unknownCommandMessage);
        }
    }

    //Скрываем предложенные команды, имеющие двоеточие.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandSendEvent(PlayerCommandSendEvent e) {
        //TODO я не буду ставить проверку на каст.
        LinkedHashSet<String> current = (LinkedHashSet<String>) e.getCommands();

        LinkedHashSet<String> tmp = new LinkedHashSet<>();
        for (String cmd : current) {
            if (!cmd.contains(":")) {
                tmp.add(cmd);
            }
        }

        current.clear();
        current.addAll(tmp);
    }


}
