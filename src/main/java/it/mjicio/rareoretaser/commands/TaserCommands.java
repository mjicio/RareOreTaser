package it.mjicio.rareoretaser.commands;

import it.mjicio.rareoretaser.RareOreTaser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataType;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaserCommands implements CommandExecutor, TabCompleter {

    private final RareOreTaser plugin;

    public TaserCommands(RareOreTaser plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("taser")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("get") && sender instanceof Player) {
                    Player player = (Player) sender;
                    giveTaser(player);
                    sender.sendMessage(ChatColor.GREEN + "Hai ricevuto un taser");
                    return true;
                } else if (args[0].equalsIgnoreCase("give") && args.length > 1) {
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target != null) {
                        giveTaser(target);
                        sender.sendMessage(ChatColor.GREEN + "Hai dato un taser a " + args[1]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void giveTaser(Player player) {
        ItemStack taser = new ItemStack(Material.STICK);
        ItemMeta meta = taser.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Taser"));
        meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&7Cariche: 0")));
        meta.setCustomModelData(163);
        UUID uuid = UUID.randomUUID();
        NamespacedKey key = new NamespacedKey(plugin, "uuid");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, uuid.toString());
        taser.setItemMeta(meta);
        player.getInventory().addItem(taser);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("get");
            completions.add("give");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> playerNames = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            completions.addAll(playerNames);
        }
        return completions;
    }
}
