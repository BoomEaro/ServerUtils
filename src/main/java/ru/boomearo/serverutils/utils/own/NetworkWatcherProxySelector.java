package ru.boomearo.serverutils.utils.own;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import ru.boomearo.serverutils.ServerUtils;

public class NetworkWatcherProxySelector extends ProxySelector {

    private final ProxySelector defaultSelector;

    public ProxySelector getDefaultSelector() {
        return this.defaultSelector;
    }

    public NetworkWatcherProxySelector(ProxySelector defaultSelector) {
        this.defaultSelector = defaultSelector;
    }

    @Override
    public List<Proxy> select(URI uri) {
        if (Bukkit.isPrimaryThread()) {
            Plugin plugin = getRequestingPlugin();
            if (plugin != null) {
                ServerUtils.getInstance().getLogger().warning("Plugin " + plugin.getName() + " attempted to establish connection " + uri + " in main server thread");
            }
            else {
                ServerUtils.getInstance().getLogger().warning("Something attempted to access " + uri + " in main server thread, printing stack trace");
                Thread.dumpStack();
            }
        }
        return this.defaultSelector.select(uri);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        this.defaultSelector.connectFailed(uri, sa, ioe);
    }

    private Plugin getRequestingPlugin() {
        HashMap<ClassLoader, Plugin> map = getClassloaderToPluginMap();
        StackTraceElement[] stacktrace = new Exception().getStackTrace();
        for (StackTraceElement element : stacktrace) {
            try {
                ClassLoader loader = Class.forName(element.getClassName(), false, getClass().getClassLoader()).getClassLoader();
                if (map.containsKey(loader)) {
                    return map.get(loader);
                }
            }
            catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    private HashMap<ClassLoader, Plugin> getClassloaderToPluginMap() {
        HashMap<ClassLoader, Plugin> map = new HashMap<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            map.put(plugin.getClass().getClassLoader(), plugin);
        }
        map.remove(getClass().getClassLoader());
        return map;
    }

}
