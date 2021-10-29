package ru.boomearo.serverutils.utils.other;

import org.bukkit.Location;

public class DistanceUtils {

    public static double distance2DCircle(Location a, Location b) {
        double aVec = Math.pow(a.getX() - b.getX(), 2);
        double bVec = Math.pow(a.getZ() - b.getZ(), 2);
        return Math.sqrt(aVec + bVec);
    }

}
