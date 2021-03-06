package ru.boomearo.serverutils.utils.other;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import ru.boomearo.serverutils.ServerUtils;

public class TpsRunnable extends BukkitRunnable {

    //only main thread
    private long mills = 0;
    private final double[] tpsArr = new double[10];
    private int index = 0;

    //caches info
    private volatile double tps = 0;
    private volatile int entities = 0;
    private volatile int chunks = 0;

    public TpsRunnable() {
        runnable();
    }

    public void runnable() {
        this.runTaskTimer(ServerUtils.getInstance(), 20, 20);
    }

    public double getTps() {
        return this.tps;
    }

    public int getEntities() {
        return this.entities;
    }

    public int getChunks() {
        return this.chunks;
    }

    @Override
    public void run() {
        handleTps();
        handleOther();
    }

    private void handleTps() {
        //Обработка тпс
        if (this.mills > 0L) {
            double tps, diff = (System.currentTimeMillis() - this.mills) - 1000.0D;

            if (diff < 0.0D) {
                diff = Math.abs(diff);
            }
            if (diff == 0.0D) {
                tps = 20.0D;
            }
            else {
                tps = 20.0D - diff / 50.0D;
            }

            if (tps < 0.0D) {
                tps = 0.0D;
            }
            this.tpsArr[this.index++] = tps;

            if (this.index >= this.tpsArr.length) {
                this.index = 0;
            }
        }
        this.mills = System.currentTimeMillis();

        //Присвоение значение
        double tpsSum = 0.0D;

        for (double d : this.tpsArr) {
            tpsSum += d;
        }

        this.tps = Math.round(tpsSum / 10.0D * 100.0D) / 100.0D;
    }

    private void handleOther() {
        int entities = 0;
        int chunks = 0;

        for (World w : Bukkit.getWorlds()) {
            entities = entities + w.getEntities().size();
            chunks = chunks + w.getLoadedChunks().length;
        }

        this.entities = entities;
        this.chunks = chunks;
    }
}
