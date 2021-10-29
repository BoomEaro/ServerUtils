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
import ru.boomearo.serverutils.utils.own.FileUtils;
import ru.boomearo.serverutils.utils.own.GlobalConstants;
import ru.boomearo.serverutils.utils.own.InternalUtils;
import ru.boomearo.serverutils.utils.own.StringUtils;

public class ServerUtilsCmds implements Commands {

    private final InternalUtils iutils = new InternalUtils();

    @CmdInfo(name = "pmanager", description = "Менеджер плагинов. Разгрузить/загрузить указанный плагин.", usage = "/su pmanager load/unload/reload/funload/freload <плагин>", permission = "serverutils.command.pmanager")
    public boolean pmanager(CommandSender cs, String[] args) {
        if (cs instanceof ConsoleCommandSender) {
            if (args.length != 2) {
                return false;
            }

            if (args[1].equalsIgnoreCase("ServerUtils")) {
                cs.sendMessage("What are you doing?!");
                return true;
            }

            if (args[0].equalsIgnoreCase("load")) {
                loadPlugin(cs, args[1]);
            }
            else if (args[0].equalsIgnoreCase("unload")) {
                unloadPlugin(cs, args[1], false);
            }
            else if (args[0].equalsIgnoreCase("funload")) {
                unloadPlugin(cs, args[1], true);
            }
            else if (args[0].equalsIgnoreCase("reload")) {
                reloadPlugin(cs, args[1], false);
            }
            else if (args[0].equalsIgnoreCase("freload")) {
                reloadPlugin(cs, args[1], true);
            }
            else {
                return false;
            }
        }
        return true;
    }

    private void reloadPlugin(CommandSender sender, String pluginname, boolean force) {
        // find plugin
        Plugin pmplugin = findPlugin(pluginname);
        // ignore if plugin is not loaded
        if (pmplugin == null) {
            sender.sendMessage("Plugin with this name not found");
            return;
        }
        // check if plugin has other active depending plugins
        if (!force) {
            List<String> depending = getOtherDependingPlugins(pmplugin);
            if (!depending.isEmpty()) {
                sender.sendMessage("Found other plugins that depend on this one, disable them first: " + StringUtils.join(depending.toArray(new String[depending.size()]), ", "));
                return;
            }
        }
        // find plugin file
        File pmpluginfile = findPluginFile(pluginname);
        // ignore if we can't find plugin file
        if (!pmpluginfile.exists()) {
            sender.sendMessage("File with this plugin name not found");
            return;
        }
        // now reload plugin
        try {
            this.iutils.unloadPlugin(pmplugin);
            this.iutils.loadPlugin(pmpluginfile);
            sender.sendMessage("Plugin reloaded");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unloadPlugin(CommandSender sender, String pluginname, boolean force) {
        // find plugin
        Plugin pmplugin = findPlugin(pluginname);
        // ignore if plugin is not loaded
        if (pmplugin == null) {
            sender.sendMessage("Plugin with this name not found");
            return;
        }
        // check if plugin has other active depending plugins
        if (!force) {
            List<String> depending = getOtherDependingPlugins(pmplugin);
            if (!depending.isEmpty()) {
                sender.sendMessage("Found other plugins that depend on this one, disable them first: " + StringUtils.join(depending.toArray(new String[depending.size()]), ", "));
                return;
            }
        }
        // now unload plugin
        try {
            this.iutils.unloadPlugin(pmplugin);
            sender.sendMessage("Plugin unloaded");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlugin(CommandSender sender, String pluginname) {
        // ignore if plugin is already loaded
        if (isPluginAlreadyLoaded(pluginname)) {
            sender.sendMessage("Plugin is already loaded");
            return;
        }
        // find plugin file
        File pmpluginfile = findPluginFile(pluginname);
        // ignore if we can't find plugin file
        if (!pmpluginfile.exists()) {
            sender.sendMessage("File with this plugin name not found");
            return;
        }
        // now load plugin
        try {
            this.iutils.loadPlugin(pmpluginfile);
            sender.sendMessage("Plugin loaded");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getOtherDependingPlugins(Plugin plugin) {
        ArrayList<String> others = new ArrayList<String>();
        for (Plugin otherplugin : Bukkit.getPluginManager().getPlugins()) {
            PluginDescriptionFile descfile = otherplugin.getDescription();
            if (
                    (descfile.getDepend() != null) && (descfile.getDepend().contains(plugin.getName())) ||
                            (descfile.getSoftDepend() != null) && (descfile.getSoftDepend().contains(plugin.getName()))
            ) {
                others.add(otherplugin.getName());
            }
        }
        return others;
    }

    private boolean isPluginAlreadyLoaded(String pluginname) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(pluginname)) {
                return true;
            }
        }
        return false;
    }

    private Plugin findPlugin(String pluginname) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(pluginname)) {
                return plugin;
            }
        }
        return Bukkit.getPluginManager().getPlugin(pluginname);
    }

    private File findPluginFile(String pluginname) {
        for (File pluginfile : FileUtils.safeListFiles(GlobalConstants.getPluginsFolder())) {
            String pluginName = getPluginName(pluginfile);
            if ((pluginName != null) && (pluginname.equalsIgnoreCase(pluginName) || pluginname.equalsIgnoreCase(pluginName.replace(" ", "_")))) {
                return pluginfile;
            }
        }
        return new File(GlobalConstants.getPluginsFolder(), pluginname + ".jar");
    }

    private String getPluginName(File pluginfile) {
        if (pluginfile.getName().endsWith(".jar")) {
            try (final JarFile jarFile = new JarFile(pluginfile)) {
                JarEntry je = jarFile.getJarEntry("plugin.yml");
                if (je != null) {
                    PluginDescriptionFile plugininfo = new PluginDescriptionFile(jarFile.getInputStream(je));
                    String jarpluginName = plugininfo.getName();
                    jarFile.close();
                    return jarpluginName;
                }
            }
            catch (Exception ignored) {

            }
        }
        return null;
    }

}
