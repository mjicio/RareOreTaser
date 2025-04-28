package it.mjicio.rareoretaser;

import it.mjicio.rareoretaser.commands.RicaricaCommand;
import it.mjicio.rareoretaser.commands.TaserCommands;
import it.mjicio.rareoretaser.events.RicaricaEvent;
import it.mjicio.rareoretaser.events.TaseratoEvent;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class RareOreTaser extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("[Taser] Abilitato");

        getServer().getPluginManager().registerEvents(new TaseratoEvent(this), this);
        getCommand("taser").setExecutor(new TaserCommands(this));



        getServer().getPluginManager().registerEvents(new RicaricaEvent(), this);
        getCommand("ricarica").setExecutor(new RicaricaCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("[Taser] Disabilitato");
    }
}
