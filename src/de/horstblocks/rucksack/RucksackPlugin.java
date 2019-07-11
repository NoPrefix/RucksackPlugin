package de.horstblocks.rucksack;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.horstblocks.rucksack.commands.RucksackCommand;
import de.horstblocks.rucksack.listeners.InventoryClickListener;
import de.horstblocks.rucksack.listeners.PlayerJoinListener;
import de.horstblocks.rucksack.listeners.PlayerQuitListener;
import de.horstblocks.rucksack.utils.ConfigManager;
import de.horstblocks.rucksack.utils.MySQL;
import de.horstblocks.rucksack.utils.Rucksack;

public class RucksackPlugin extends JavaPlugin {
	
	public String MySQL_HOST, MySQL_USERNAME, MySQL_PASSWORD, MySQL_DATABASE, MySQL_PORT;
	
	private static RucksackPlugin rucksackPlugin;
	
	private HashMap<Player, ArrayList<Rucksack>> cache = new HashMap<>();
	public ArrayList<Player> usingBackpack = new ArrayList<>();
	
	private MySQL mysql;

	@Override
	public void onEnable() {
		setup();
	}
	
	@Override
	public void onDisable() {
		Bukkit.getOnlinePlayers().forEach((player) -> {
			for(int i = 1; i <= 5; i++) {
				try {
					player.kickPlayer("§cDer Server startet neu!");
					RucksackPlugin.getPlugin().getMysql().setRucksackInhalt(player.getUniqueId().toString(), i,
							RucksackPlugin.getPlugin().getCache().get(player).get(i-1));
				} catch (Exception e) {}
				Bukkit.getLogger().info(player.getName() + " wurde in die Rucksack-Datenbank eingetragen.");
			}
		});
	}
	
	private void setup() {
		rucksackPlugin = this;
		mysql = new MySQL();
				
		new ConfigManager().loadConfig();
		if(!mysql.connect(MySQL_HOST, MySQL_PORT, MySQL_DATABASE, MySQL_USERNAME, MySQL_PASSWORD)) {
			System.err.println("[RucksackPlugin] Die MySQL-Daten konnten nicht geladen werden, das Plugin wird deaktiviert.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		getCommand("rucksack").setExecutor(new RucksackCommand());
		
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
		
	}

	public static RucksackPlugin getPlugin() {
		return rucksackPlugin;
	}
	
	public HashMap<Player, ArrayList<Rucksack>> getCache() {
		return cache;
	}

	public MySQL getMysql() {
		return mysql;
	}

}
