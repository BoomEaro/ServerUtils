package ru.boomearo.serverutils.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;

public class CmdList {

    private static CmdList instance = null;
    private Map<String, Cmd> cmd = new HashMap<String, Cmd>();

    public static CmdList getInstance() {
        if (instance == null) {
            instance = new CmdList();
        }
        return instance;
    }

    private CmdList() {

    }

    public void put(Cmd cmd) {
        this.cmd.put(cmd.getName(), cmd);
    }

    public boolean execute(String name, CommandSender cs, String[] args) {
        Cmd res = this.cmd.get(name);
        if (res == null) {
            return false;
        }
        if (res.getPermission().length() != 0) {
            if (!cs.hasPermission(res.getPermission())) {
                cs.sendMessage("§cУ вас нет прав");
                return true;
            }
        }
        if (!res.execute(cs, args)) {
            if (res.getUsage().length() != 0) {
                cs.sendMessage(res.getUsage());
            }
        }
        return true;
    }

    public Set<String> getListName() {
        return cmd.keySet();
    }

    public Map<String, Cmd> getListCmd() {
        return cmd;
    }

    public void sendUsageCmds(CommandSender cs) {
        for (Cmd cmd : this.cmd.values()) {
            cs.sendMessage("§f" + cmd.getUsage() + " §8- §a" + cmd.getDescription());
        }
    }
}
