package it.mjicio.rareoretaser.events;

import me.zombie_striker.qg.api.QAWeaponPrepareShootEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TaseratoEvent implements Listener {

    private final Set<UUID> cooldown = new HashSet<>();

    private final Set<UUID> playertaserati = new HashSet<>();
    private final JavaPlugin plugin;

    public TaseratoEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (cooldown.contains(player.getUniqueId())) {
            return; // Ignore if the player is in cooldown
        }

        if (item.getType() == Material.STICK && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&6Taser")) && meta.hasLore()) {
                List<String> lore = meta.getLore();
                String firstLine = lore.get(0);
                if (firstLine.startsWith(ChatColor.translateAlternateColorCodes('&', "&7Cariche: "))) {
                    String chargesStr = firstLine.split(": ")[1]; // Split to extract the charge part
                    int charges = Integer.parseInt(chargesStr);

                    if (charges > 0) {
                        cooldown.add(player.getUniqueId());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                cooldown.remove(player.getUniqueId()); // Remove the player from cooldown after 1 second
                            }
                        }.runTaskLater(plugin, 100); // 20 ticks = 1 second

                        if (event.getRightClicked() instanceof Player) {
                            Player target = (Player) event.getRightClicked();

                            if (!target.isBlocking()) {

                                if (!target.hasPermission("taser.bypass")) {

                                    playertaserati.add(target.getUniqueId());

                                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 10));
                                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));

                                    // Schedule task to remove player from playertaserati after effects end
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            playertaserati.remove(target.getUniqueId());
                                        }
                                    }.runTaskLater(plugin, 100); // This should match the duration of the effects

                                    if (target.getVehicle() != null) {
                                        target.getVehicle().eject();
                                    }

                                    charges--;
                                    lore.set(0, ChatColor.translateAlternateColorCodes('&', "&7Cariche: ") + charges);
                                    meta.setLore(lore);
                                    item.setItemMeta(meta);

                                    player.getInventory().setItemInMainHand(item); // Update the item in the player's inventory

                                    // Play the sound for all entities within a 5-block radius
                                    for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                                        if (entity instanceof Player) {
                                            entity.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.0f, 2.0f);
                                        }
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Non puoi taserare questo utente!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPlayerRightClickWhileTazed(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (playertaserati.contains(player.getUniqueId())) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerShoot(QAWeaponPrepareShootEvent e) {
        Player player = e.getPlayer();
        if (playertaserati.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Non puoi da taserato!!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player p = event.getPlayer();

        if(playertaserati.contains(p.getUniqueId())) {

            event.setCancelled(true);

        }

    }


}
