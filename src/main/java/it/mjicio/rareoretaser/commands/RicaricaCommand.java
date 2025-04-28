package it.mjicio.rareoretaser.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RicaricaCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ricarica")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("get") && sender instanceof Player) {
                    if (args.length > 1) {
                        try {
                            int amount = Integer.parseInt(args[1]);
                            Player player = (Player) sender;
                            giveRicarica(player, amount);
                            return true;
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Devi specificare un numero valido di cariche.");
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Devi specificare il numero di cariche da ricevere.");
                        return false;
                    }
                } else if (args[0].equalsIgnoreCase("give") && args.length > 2) {
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target != null) {
                        try {
                            int amount = Integer.parseInt(args[2]);
                            giveRicarica(target, amount);
                            return true;
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Devi specificare un numero valido di cariche.");
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Giocatore non trovato.");
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private void giveRicarica(Player player, int amount) {
        ItemStack ricarica = new ItemStack(Material.PHANTOM_MEMBRANE, amount);
        ItemMeta meta = ricarica.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Ricarica Taser"));
        meta.setCustomModelData(10);
        ricarica.setItemMeta(meta);
        player.getInventory().addItem(ricarica);
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
