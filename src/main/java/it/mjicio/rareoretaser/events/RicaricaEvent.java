package it.mjicio.rareoretaser.events;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RicaricaEvent implements Listener {

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType() == Material.STICK && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();

                if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&6Taser")) && meta.hasLore()) {
                    List<String> lore = meta.getLore();
                    if (lore.get(0).startsWith(ChatColor.translateAlternateColorCodes('&', "&7Cariche:"))) {
                        String chargesStr = lore.get(0).split(": ")[1];
                        int charges = Integer.parseInt(chargesStr);
                        if (charges < 3) {
                            ItemStack chargeItem = getChargeItem(player);
                            if (chargeItem != null) {
                                charges++;
                                lore.set(0, ChatColor.translateAlternateColorCodes('&', "&7Cariche: ") + charges);
                                meta.setLore(lore);
                                item.setItemMeta(meta);
                                chargeItem.setAmount(chargeItem.getAmount() - 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private ItemStack getChargeItem(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PHANTOM_MEMBRANE && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                meta.setCustomModelData(10);
                if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',"&6Ricarica Taser"))) {
                    return item;
                }
            }
        }
        return null;
    }
}
