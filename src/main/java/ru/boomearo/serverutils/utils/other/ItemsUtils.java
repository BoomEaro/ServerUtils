package ru.boomearo.serverutils.utils.other;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemsUtils {

    public static void removeInventoryItems(Player player, ItemStack itemRemove, int amount) {
        int iter = 0;
        for (ItemStack is : player.getInventory().getStorageContents()) {
            if (is != null && is.isSimilar(itemRemove)) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                }
                else {
                    player.getInventory().setItem(iter, null);
                    amount = -newAmount;
                    if (amount == 0) {
                        break;
                    }
                }
            }
            iter++;
        }
    }

    public static int getItemsAmount(Player player, ItemStack item) {
        Inventory inv = player.getInventory();
        int tmp = 0;
        for (ItemStack i : inv.getStorageContents()) {
            if (i != null) {
                if (i.isSimilar(item)) {
                    tmp = tmp + i.getAmount();
                }
            }
        }
        return tmp;
    }

    //TODO Возможно стоит переписать этот метод
    public static int giveItems(Player player, ItemStack item, int amount) {
        Inventory inv = player.getInventory();
        int remainder = 0;
        if (item.getMaxStackSize() == 1) {
            item.setAmount(1);
            for (int i = 0; i < amount; i++) {
                Map<Integer, ItemStack> res = inv.addItem(item);
                if (!res.isEmpty()) remainder++;
            }
        }
        else if (item.getMaxStackSize() == 16) {
            item.setAmount(1);
            for (int i = 0; i < amount; i++) {
                Map<Integer, ItemStack> res = inv.addItem(item);
                for (ItemStack getRes : res.values())
                    if (!res.isEmpty()) remainder += getRes.getAmount();
            }
        }
        else {
            item.setAmount(amount);
            amount = 0;
            Map<Integer, ItemStack> res = inv.addItem(item);
            for (ItemStack getRes : res.values()) {
                remainder += getRes.getAmount();
            }
        }
        return remainder;
    }

    public static int getFillAmount(Player player, ItemStack item) {
        Inventory inv = player.getInventory();
        int tmp = 0;
        for (ItemStack i : inv.getStorageContents()) {
            if (i != null) {
                if (i.isSimilar(item)) {
                    tmp = tmp + (i.getMaxStackSize() - i.getAmount());
                }
            }
            else {
                tmp = tmp + item.getMaxStackSize();
            }
        }
        return tmp;
    }

}
