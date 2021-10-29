package ru.boomearo.serverutils.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

public class CmdExecutor implements CommandExecutor, TabCompleter {
    private CmdList cmds;

    private CmdServerUtils cmdMain = null;

    public CmdExecutor() {
        this.cmdMain = new CmdServerUtils();
        registerCmd();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            int len = args.length;
            if (len == 0) {
                this.cmds.sendUsageCmds(sender);
                return true;
            }
            String[] argsCopy = new String[len - 1];
            System.arraycopy(args, 1, argsCopy, 0, len - 1);
            if (!this.cmds.execute(args[0].toLowerCase(), sender, argsCopy)) {
                this.cmds.sendUsageCmds(sender);
            }
        }
        return true;
    }

    private void registerCmd() {
        this.cmds = CmdList.getInstance();
        for (Method method : this.cmdMain.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(CmdInfo.class)) {
                continue;
            }
            Cmd cmd = new Cmd(this, method, method.getAnnotation(CmdInfo.class));
            this.cmds.put(cmd);
        }
    }

    public CmdServerUtils getCmdBuffer() {
        return this.cmdMain;
    }

    private static final List<String> empty = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (arg0 instanceof ConsoleCommandSender) {
            if (arg3.length == 1) {
                List<String> matches = new ArrayList<>();
                String search = arg3[0].toLowerCase();
                for (String se : Arrays.asList("pmanager")) {
                    if (se.toLowerCase().startsWith(search)) {
                        matches.add(se);
                    }
                }
                return matches;
            }
            if (arg3.length == 2) {
                if (arg3[0].equalsIgnoreCase("pmanager")) {
                    List<String> matches = new ArrayList<>();
                    String search = arg3[1].toLowerCase();
                    for (String se : Arrays.asList("load", "unload", "funload", "reload", "freload")) {
                        if (se.toLowerCase().startsWith(search)) {
                            matches.add(se);
                        }
                    }
                    return matches;
                }
                else {
                    return empty;
                }
            }
            if (arg3.length == 3) {
                if (arg3[0].equalsIgnoreCase("pmanager")) {
                    if (arg3[1].equalsIgnoreCase("unload") || arg3[1].equalsIgnoreCase("funload") || arg3[1].equalsIgnoreCase("reload") || arg3[1].equalsIgnoreCase("freload")) {
                        List<String> matches = new ArrayList<>();
                        String search = arg3[2].toLowerCase();
                        for (String se : getLoadedPluginListString()) {
                            if (se.toLowerCase().startsWith(search)) {
                                matches.add(se);
                            }
                        }
                        return matches;
                    }
                    else {
                        return empty;
                    }
                }
                else {
                    return empty;
                }
            }
            else {
                return empty;
            }
        }
        else {
            return empty;
        }
    }

    private List<String> getLoadedPluginListString() {
        List<String> tmp = new ArrayList<String>();
        for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
            if (pl.isEnabled()) {
                tmp.add(pl.getName());
            }
        }
        return tmp;
    }

}

