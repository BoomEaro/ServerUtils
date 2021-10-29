package ru.boomearo.serverutils.utils.other;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;

import java.util.List;

public class StringPages {

    public static void sendPageInfoComponent(CommandSender sender, List<TextComponent> info, int page, int pageLimit, String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        if (page <= 0) {
            sender.sendMessage(prefix + "Указанная страница должна быть больше нуля.");
            return;
        }
        int offSet = (page - 1) * pageLimit;
        if (offSet >= info.size()) {
            sender.sendMessage(prefix + "Указанная страница не найдена.");
            return;
        }
        int maxPage = info.size() / pageLimit + (info.size() % pageLimit > 0 ? 1 : 0);

        final String sep = prefix + "§8==========================";
        sender.sendMessage(sep);
        sender.sendMessage(prefix + "Страница: §c" + page + "§f/§c" + maxPage);
        for (int i = 0; i < pageLimit; i++) {
            int newO = offSet + i;
            if (newO >= info.size()) {
                break;
            }
            TextComponent tt = new TextComponent(prefix + "§4" + (newO + 1) + ". §f");
            tt.addExtra(info.get(newO));
            sender.spigot().sendMessage(tt);
        }
        sender.sendMessage(sep);
    }

}
