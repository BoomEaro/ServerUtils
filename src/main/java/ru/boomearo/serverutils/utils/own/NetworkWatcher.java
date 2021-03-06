package ru.boomearo.serverutils.utils.own;

import java.net.ProxySelector;

public class NetworkWatcher {

    public void register() {
        ProxySelector.setDefault(new NetworkWatcherProxySelector(ProxySelector.getDefault()));
    }

    public void unregister() {
        ProxySelector cur = ProxySelector.getDefault();
        if (cur instanceof NetworkWatcherProxySelector) {
            ProxySelector.setDefault(((NetworkWatcherProxySelector) cur).getDefaultSelector());
        }
    }

}
