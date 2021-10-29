package ru.boomearo.serverutils.commands.serverutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import ru.boomearo.serverutils.utils.other.commands.AbstractExecutor;
import ru.boomearo.serverutils.utils.other.commands.CmdList;

public class ServerUtilsExecutor extends AbstractExecutor {

    private static final List<String> empty = new ArrayList<>();

    public ServerUtilsExecutor() {
        super(new ServerUtilsCmds());
    }

    @Override
    public boolean zeroArgument(CommandSender sender, CmdList cmds) {
        cmds.sendUsageCmds(sender);
        return true;
    }

    @Override
    public String getPrefix() {
        return "§f";
    }

    @Override
    public String getSuffix() {
        return " §8-§a ";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 1) {
                List<String> matches = new ArrayList<>();
                String search = args[0].toLowerCase();
                for (String se : Arrays.asList("pmanager")) {
                    if (se.toLowerCase().startsWith(search)) {
                        matches.add(se);
                    }
                }
                return matches;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("pmanager")) {
                    List<String> matches = new ArrayList<>();
                    String search = args[1].toLowerCase();
                    for (String se : Arrays.asList("load", "unload", "funload", "reload", "freload")) {
                        if (se.toLowerCase().startsWith(search)) {
                            matches.add(se);
                        }
                    }
                    return matches;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("pmanager")) {
                    if (args[1].equalsIgnoreCase("unload") || args[1].equalsIgnoreCase("funload") || args[1].equalsIgnoreCase("reload") || args[1].equalsIgnoreCase("freload")) {
                        List<String> matches = new ArrayList<>();
                        String search = args[2].toLowerCase();
                        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                            if (plugin.isEnabled()) {
                                if (plugin.getName().toLowerCase().startsWith(search)) {
                                    matches.add(plugin.getName());
                                }
                            }
                        }
                        return matches;
                    }
                }
            }
        }
        return empty;
    }
}

