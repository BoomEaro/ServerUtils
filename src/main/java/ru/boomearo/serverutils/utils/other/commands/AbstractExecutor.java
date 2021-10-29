package ru.boomearo.serverutils.utils.other.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class AbstractExecutor implements CommandExecutor {

    private final Map<String, Cmd> commands = new HashMap<>();

    private static final String sep = "=============================================";

    //Мне нужно получить объект класса с которого я беру методы
    public AbstractExecutor(Commands commands) {
        registerCmd(commands);
    }

    private void registerCmd(Commands commands) {
        for (Method method : commands.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(CmdInfo.class)) {
                continue;
            }
            Cmd cmd = new Cmd(commands, method, method.getAnnotation(CmdInfo.class));
            this.commands.put(cmd.getName(), cmd);
            //Пытаемся зарегать алиасы.
            for (String alias : cmd.getAliases()) {
                if (alias != null && !alias.isEmpty()) {
                    this.commands.put(alias, cmd);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!hasPermissionToExecute(sender)) {
            String permMsg = getPermissionMessage();
            if (permMsg != null) {
                sender.sendMessage(permMsg);
            }
            return true;
        }
        int len = args.length;
        if (len == 0) {
            return zeroArgument(sender);
        }
        String[] argsCopy = new String[len - 1];
        System.arraycopy(args, 1, argsCopy, 0, len - 1);
        if (!executeCommand(args[0].toLowerCase(), sender, argsCopy)) {
            sendUsageCommands(sender);
        }
        return true;
    }

    private boolean executeCommand(String name, CommandSender sender, String[] args) {
        Cmd cmd = this.commands.get(name);
        if (cmd == null) {
            return false;
        }

        String permission = cmd.getPermission();

        if (permission != null && !permission.isEmpty()) {
            if (!sender.hasPermission(permission)) {
                String permMsg = getPermissionMessage();
                if (permMsg != null) {
                    sender.sendMessage(permMsg);
                }
                return true;
            }
        }
        if (!cmd.execute(sender, args)) {
            String usage = cmd.getUsage();
            if (usage != null && !usage.isEmpty()) {
                sender.sendMessage(usage);
            }
        }
        return true;
    }

    //Если у игрока есть доступ хотя бы к одной команде, значит исполняем
    protected boolean hasPermissionToExecute(CommandSender sender) {
        for (Cmd cmd : getCmds()) {
            String permission = cmd.getPermission();
            if (permission != null && !permission.isEmpty()) {
                if (sender.hasPermission(permission)) {
                    return true;
                }
                continue;
            }
            return true;
        }
        return false;
    }

    private Set<Cmd> getCmds() {
        return new TreeSet<>(this.commands.values());
    }

    protected void sendUsageCommands(CommandSender cs) {
        cs.sendMessage(getPrefix() + sep);
        for (Cmd cmd : getCmds()) {
            if (cmd.getPermission().length() != 0) {
                if (cs.hasPermission(cmd.getPermission())) {
                    cs.sendMessage(getUsage(cmd));
                }
                continue;
            }
            cs.sendMessage(getUsage(cmd));
        }
        cs.sendMessage(getPrefix() + sep);
    }

    private String getUsage(Cmd res) {
        return getPrefix() + res.getUsage() + getSuffix() + res.getDescription();
    }

    protected String getPermissionMessage() {
        return "§cУ вас не достаточно прав.";
    }

    protected abstract boolean zeroArgument(CommandSender sender);

    protected abstract String getPrefix();

    protected abstract String getSuffix();


}
