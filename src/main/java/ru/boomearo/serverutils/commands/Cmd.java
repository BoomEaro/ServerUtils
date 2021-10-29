package ru.boomearo.serverutils.commands;

import java.lang.reflect.Method;

import org.bukkit.command.CommandSender;

public class Cmd {
    private CmdExecutor cmdexec;
    private Method method;
    private String
            name,
            description,
            usage,
            permission;

    public Cmd(CmdExecutor cmdexec, Method method, CmdInfo cmdinfo) {
        this.cmdexec = cmdexec;
        this.method = method;
        this.name = cmdinfo.name();
        this.description = cmdinfo.description();
        this.usage = cmdinfo.usage();
        this.permission = cmdinfo.permission();

    }

    public boolean execute(CommandSender cs, String[] args) {
        try {
            return (boolean) this.method.invoke(this.cmdexec.getCmdBuffer(), cs, args);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getPermission() {
        return this.permission;
    }

}