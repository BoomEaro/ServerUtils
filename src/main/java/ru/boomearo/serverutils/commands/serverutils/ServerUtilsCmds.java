package ru.boomearo.serverutils.commands.serverutils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import ru.boomearo.serverutils.utils.other.commands.CmdInfo;
import ru.boomearo.serverutils.utils.other.commands.Commands;
import ru.boomearo.serverutils.utils.own.GlobalConstants;
import ru.boomearo.serverutils.utils.own.InternalUtils;

public class ServerUtilsCmds implements Commands {

    private final InternalUtils internalUtils = new InternalUtils();

    @CmdInfo(name = "pmanager", description = "Менеджер плагинов. Разгрузить/загрузить указанный плагин.", usage = "/su pmanager load/unload/reload/funload/freload <плагин>", permission = "serverutils.command.pmanager")
    public boolean pmanager(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return false;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            return true;
        }

        if (args[1].equalsIgnoreCase("ServerUtils")) {
            sender.sendMessage("You can't load/unload ServerUtils!");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "load": {
                loadPlugin(sender, args[1]);
                return true;
            }
            case "unload": {
                unloadPlugin(sender, args[1], false);
                return true;
            }
            case "funload": {
                unloadPlugin(sender, args[1], true);
                return true;
            }
            case "reload": {
                reloadPlugin(sender, args[1], false);
                return true;
            }
            case "freload": {
                reloadPlugin(sender, args[1], true);
                return true;
            }
            default: {
                return false;
            }
        }
    }

    private void reloadPlugin(CommandSender sender, String pluginName, boolean force) {
        // find plugin
        Plugin pmPlugin = findPlugin(pluginName);
        // ignore if plugin is not loaded
        if (pmPlugin == null) {
            sender.sendMessage("Plugin with this name not found");
            return;
        }
        List<String> depending = getOtherDependingPlugins(pmPlugin);
        if (!depending.isEmpty()) {
            if (force) {
                for (String plugin : depending) {
                    unloadPlugin(sender, plugin, true);
                }
            }
            else {
                sender.sendMessage("Found other plugins that depend on this one, disable them first: " + String.join(",", depending.toArray(new String[0])));
                return;
            }
        }
        // find plugin file
        File pmPluginFile = findPluginFile(pmPlugin.getName());
        // ignore if we can't find plugin file
        if (pmPluginFile == null) {
            sender.sendMessage("File with this plugin name not found");
            return;
        }
        // now reload plugin
        try {
            this.internalUtils.unloadPlugin(pmPlugin);
            this.internalUtils.loadPlugin(pmPluginFile);
            sender.sendMessage("Plugin reloaded");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unloadPlugin(CommandSender sender, String pluginName, boolean force) {
        // find plugin
        Plugin pmPlugin = findPlugin(pluginName);
        // ignore if plugin is not loaded
        if (pmPlugin == null) {
            sender.sendMessage("Plugin with this name not found");
            return;
        }
        // check if plugin has other active depending plugins
        List<String> depending = getOtherDependingPlugins(pmPlugin);
        if (!depending.isEmpty()) {
            if (force) {
                for (String plugin : depending) {
                    unloadPlugin(sender, plugin, true);
                }
            }
            else {
                sender.sendMessage("Found other plugins that depend on this one, disable them first: " + String.join(",", depending.toArray(new String[0])));
                return;
            }
        }
        // now unload plugin
        try {
            this.internalUtils.unloadPlugin(pmPlugin);
            sender.sendMessage("Plugin unloaded");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlugin(CommandSender sender, String pluginName) {
        // ignore if plugin is already loaded
        if (isPluginAlreadyLoaded(pluginName)) {
            sender.sendMessage("Plugin is already loaded");
            return;
        }
        // find plugin file
        File pmPluginFile = findPluginFile(pluginName);
        // ignore if we can't find plugin file
        if (pmPluginFile == null) {
            sender.sendMessage("File with this plugin name not found");
            return;
        }
        // now load plugin
        try {
            this.internalUtils.loadPlugin(pmPluginFile);
            sender.sendMessage("Plugin loaded");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getOtherDependingPlugins(Plugin plugin) {
        ArrayList<String> others = new ArrayList<>();
        for (Plugin otherPlugin : Bukkit.getPluginManager().getPlugins()) {
            PluginDescriptionFile descFile = otherPlugin.getDescription();
            if ((descFile.getDepend() != null) && (descFile.getDepend().contains(plugin.getName())) || (descFile.getSoftDepend() != null) && (descFile.getSoftDepend().contains(plugin.getName()))) {
                others.add(otherPlugin.getName());
            }
        }
        return others;
    }

    private boolean isPluginAlreadyLoaded(String pluginName) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(pluginName)) {
                return true;
            }
        }
        return false;
    }

    private Plugin findPlugin(String pluginName) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(pluginName)) {
                return plugin;
            }
        }
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }

    private File findPluginFile(String plugin) {
        File pluginFile = new File(GlobalConstants.getPluginsFolder(), plugin + ".jar");
        if (!pluginFile.exists()) {
            return null;
        }
        String pluginName = getPluginName(pluginFile);
        if (pluginName == null) {
            return null;
        }
        return pluginFile;
    }

    private String getPluginName(File pluginFile) {
        if (pluginFile.getName().endsWith(".jar")) {
            try (final JarFile jarFile = new JarFile(pluginFile)) {
                JarEntry je = jarFile.getJarEntry("plugin.yml");
                if (je != null) {
                    PluginDescriptionFile pluginInfo = new PluginDescriptionFile(jarFile.getInputStream(je));
                    String jarPluginName = pluginInfo.getName();
                    jarFile.close();
                    return jarPluginName;
                }
            }
            catch (Exception ignored) {
            }
        }
        return null;
    }

}
