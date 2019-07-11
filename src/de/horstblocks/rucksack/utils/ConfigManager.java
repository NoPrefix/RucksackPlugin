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
		
		plugin.MySQL_HOST = config.getString("MySQL.Host");
		plugin.MySQL_PORT = config.getString("MySQL.Port");
		plugin.MySQL_USERNAME = config.getString("MySQL.Username");
		plugin.MySQL_PASSWORD = config.getString("MySQL.Password");
		plugin.MySQL_DATABASE = config.getString("MySQL.Database");
		
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
