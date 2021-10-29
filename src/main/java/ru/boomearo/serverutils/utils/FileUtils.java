package ru.boomearo.serverutils.utils;

import java.io.File;

public class FileUtils {

    public static File[] safeListFiles(File file) {
        File[] files = file.listFiles();
        return files != null ? files : new File[0];
    }

    public static String[] safeList(File file) {
        String[] files = file.list();
        return files != null ? files : new String[0];
    }

}
