package de.horstblocks.rucksack.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import de.horstblocks.rucksack.RucksackPlugin;

public class ConfigManager {

	public void loadConfig() {
		File file = new File("plugins/Rucksack/mysql.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		if(!file.exists()) {
			setupConfig();
			loadConfig();
			return;
		}
		
		RucksackPlugin plugin = RucksackPlugin.getPlugin();
		
		plugin.mySqlHost = config.getString("MySQL.Host");
		plugin.mySqlPort = config.getString("MySQL.Port");
		plugin.mySqlUsername = config.getString("MySQL.Username");
		plugin.mySqlPassword = config.getString("MySQL.Password");
		plugin.mySqlDatabase = config.getString("MySQL.Database");
		
	}
	
	private void setupConfig() {
		File file = new File("plugins/Rucksack/mysql.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.set("MySQL.Host", "localhost");
		config.set("MySQL.Port", "3306");
		config.set("MySQL.Username", "user");
		config.set("MySQL.Password", "password");
		config.set("MySQL.Database", "rucksack");
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
