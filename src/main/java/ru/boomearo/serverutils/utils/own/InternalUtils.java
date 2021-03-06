package ru.boomearo.serverutils.utils.own;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;

/**
 * Работает на чистой магии. Логика была взята из плагина AutoSaveWorld.
 */
public class InternalUtils {

    public void unloadPlugin(Plugin plugin) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException, InterruptedException, NoSuchMethodException, InvocationTargetException {
        PluginManager pluginmanager = Bukkit.getPluginManager();
        Class<? extends PluginManager> managerClass = pluginmanager.getClass();
        ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
        // disable plugin
        pluginmanager.disablePlugin(plugin);
        // kill threads if any
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getClass().getClassLoader() == pluginClassLoader) {
                thread.interrupt();
                thread.join(2000);
                if (thread.isAlive()) {
                    thread.stop();
                }
            }
        }
        // remove from plugins field
        ((List<Plugin>) ReflectionUtils.getField(managerClass, "plugins").get(pluginmanager)).remove(plugin);
        // remove from lookupnames field
        ((Map<String, Plugin>) ReflectionUtils.getField(managerClass, "lookupNames").get(pluginmanager)).values().remove(plugin);
        // remove from commands field
        CommandMap commandMap = (CommandMap) ReflectionUtils.getField(managerClass, "commandMap").get(pluginmanager);
        Collection<Command> commands = (Collection<Command>) ReflectionUtils.getMethod(commandMap.getClass(), "getCommands", 0).invoke(commandMap);
        for (Command cmd : new LinkedList<>(commands)) {
            if (cmd instanceof PluginIdentifiableCommand pluginCommand) {
                if (pluginCommand.getPlugin().getName().equalsIgnoreCase(plugin.getName())) {
                    removeCommand(commandMap, commands, cmd);
                }
            }
            else if (cmd.getClass().getClassLoader() == pluginClassLoader) {
                removeCommand(commandMap, commands, cmd);
            }
        }
        // close file in url classloader
        if (pluginClassLoader instanceof URLClassLoader urlLoader) {
            urlLoader.close();
        }
    }

    private void removeCommand(CommandMap commandMap, Collection<Command> commands, Command cmd) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        cmd.unregister(commandMap);
        if (commands.getClass().getSimpleName().equals("UnmodifiableCollection")) {
            Field originalField = commands.getClass().getDeclaredField("c");
            originalField.setAccessible(true);
            Collection<Command> original = (Collection<Command>) originalField.get(commands);
            original.remove(cmd);
        }
        else {
            commands.remove(cmd);
        }
    }

    public void loadPlugin(File pluginFile) throws UnknownDependencyException, InvalidPluginException, InvalidDescriptionException, IllegalArgumentException, IllegalAccessException {
        PluginManager pluginmanager = Bukkit.getPluginManager();
        // load plugin
        Plugin plugin = pluginmanager.loadPlugin(pluginFile);
        // enable plugin
        plugin.onLoad();
        pluginmanager.enablePlugin(plugin);
    }

}
