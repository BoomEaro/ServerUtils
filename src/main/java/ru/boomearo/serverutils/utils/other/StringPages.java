package ru.boomearo.serverutils.utils.other;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;

import java.util.List;

public class StringPages {

    public static void sendPageInfoComponent(CommandSender sender, List<TextComponent> data, int page, int pageLimit, PageInfo info) {
        String prefix = info.getPrefix();
        if (prefix == null) {
            prefix = "";
        }

        if (page <= 0) {
            sender.sendMessage(prefix + "Указанная страница должна быть больше нуля.");
            return;
        }
        int offSet = (page - 1) * pageLimit;
        if (offSet >= data.size()) {
            sender.sendMessage(prefix + "Указанная страница не найдена.");
            return;
        }
        int maxPage = data.size() / pageLimit + (data.size() % pageLimit > 0 ? 1 : 0);

        final String sep = prefix + "§8==========================";
        sender.sendMessage(sep);
        sender.sendMessage(prefix + info.getCurrentPageInfo(page, maxPage));
        for (int i = 0; i < pageLimit; i++) {
            int newO = offSet + i;
            if (newO >= data.size()) {
                break;
            }
            TextComponent tt = new TextComponent(prefix + info.getDataFormat((newO + 1)));
            tt.addExtra(data.get(newO));
            sender.spigot().sendMessage(tt);
        }
        sender.sendMessage(sep);
    }

    public static void sendPageInfo(CommandSender sender, List<String> data, int page, int pageLimit, PageInfo info) {
        String prefix = info.getPrefix();
        if (prefix == null) {
            prefix = "";
        }

        if (page <= 0) {
            sender.sendMessage(prefix + "Указанная страница должна быть больше нуля.");
            return;
        }
        int offSet = (page - 1) * pageLimit;
        if (offSet >= data.size()) {
            sender.sendMessage(prefix + "Указанная страница не найдена.");
            return;
        }

        int maxPage = data.size() / pageLimit + (data.size() % pageLimit > 0 ? 1 : 0);

        final String sep = prefix + "§8==========================";

        sender.sendMessage(sep);
        sender.sendMessage(prefix + info.getCurrentPageInfo(page, maxPage));
        for (int i = 0; i < pageLimit; i++) {
            int newO = offSet + i;
            if (newO >= data.size()) {
                break;
            }
            sender.sendMessage(prefix + info.getDataFormat((newO + 1)) + data.get(newO));
        }
        sender.sendMessage(sep);
    }

    public static interface PageInfo {

        public String getPrefix();

        public default String getCurrentPageInfo(int currentPage, int maxPage) {
            return "Страница: §b" + currentPage + "§f/§b" + maxPage;
        }

        public default String getDataFormat(int index) {
            return "§b" + index + ". §f";
        }
    }
}
