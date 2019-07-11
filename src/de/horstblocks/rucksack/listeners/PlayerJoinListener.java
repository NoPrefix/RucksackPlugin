package de.horstblocks.rucksack.listeners;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.horstblocks.rucksack.RucksackPlugin;
import de.horstblocks.rucksack.utils.Rucksack;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		ArrayList<Rucksack> backpacks = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			backpacks.add(RucksackPlugin.getPlugin().getMysql().getRucksackInhalt(event.getPlayer().getUniqueId().toString(), i));
		}
		RucksackPlugin.getPlugin().getCache().put(event.getPlayer(), backpacks);
	}

}
