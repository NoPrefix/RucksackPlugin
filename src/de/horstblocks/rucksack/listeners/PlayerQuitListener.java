package de.horstblocks.rucksack.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.horstblocks.rucksack.RucksackPlugin;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (int i = 1; i <= 5; i++) {
			RucksackPlugin.getPlugin().getMysql().setRucksackInhalt(event.getPlayer().getUniqueId().toString(), i,
					RucksackPlugin.getPlugin().getCache().get(event.getPlayer()).get(i-1));
		}
		RucksackPlugin.getPlugin().getCache().remove(event.getPlayer());
	}

}
