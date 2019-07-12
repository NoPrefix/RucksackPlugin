package de.horstblocks.rucksack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

	public String mySqlHost, mySqlUsername, mySqlPassword, mySqlDatabase, mySqlPort;
	
	public int maxBackpacks;

	private static RucksackPlugin rucksackPlugin;

	private HashMap<Player, ArrayList<Rucksack>> cache = new HashMap<>();
	public ArrayList<Player> usingBackpack = new ArrayList<>();
	
	public ExecutorService executorService;

	private MySQL mysql;

	@Override
	public void onEnable() {
		setup();
	}

	@Override
	public void onDisable() {
		Bukkit.getOnlinePlayers().forEach((player) -> {
			for (int i = 1; i <= 5; i++) {
				try {
					RucksackPlugin.getPlugin().getMysql().setRucksackInhalt(player.getUniqueId().toString(), i,
							RucksackPlugin.getPlugin().getCache().get(player).get(i - 1));
					Bukkit.getLogger().info(player.getName() + " wurde in die Rucksack-Datenbank eingetragen.");
				} catch (Exception e) {}
			}
		});
	}

	private void setup() {
		rucksackPlugin = this;
		mysql = new MySQL();
		
		executorService = Executors.newFixedThreadPool(5);

		new ConfigManager().loadConfig();
		if (!mysql.connect(mySqlHost, mySqlPort, mySqlDatabase, mySqlUsername, mySqlPassword)) {
			System.err.println(
					"[RucksackPlugin] Die MySQL-Daten konnten nicht geladen werden, das Plugin wird deaktiviert.");
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
