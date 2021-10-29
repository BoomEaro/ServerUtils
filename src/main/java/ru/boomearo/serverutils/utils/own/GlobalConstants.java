package ru.boomearo.serverutils.utils.own;

import java.io.File;

import ru.boomearo.serverutils.ServerUtils;

public class GlobalConstants {

    public static File getServerUtilsFolder() {
        return ServerUtils.getInstance().getDataFolder();
    }

    public static File getPluginsFolder() {
        return getServerUtilsFolder().getParentFile();
    }

}
