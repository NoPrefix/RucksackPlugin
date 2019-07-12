package de.horstblocks.rucksack.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.horstblocks.rucksack.RucksackPlugin;

public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (event.getCurrentItem() == null) {
			return;
		}

		Player player = (Player) event.getWhoClicked();

		if (!RucksackPlugin.getPlugin().usingBackpack.contains(player)) {
			return;
		}

		try {
			if (event.getView().getTitle().startsWith("§aDeine Rucksäcke")) {
				event.setCancelled(true);
				if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§cRucksack")) {
					player.closeInventory();
					player.sendMessage(
							"§cDu besitzt diesen Rucksack nicht! §e10.000 Mark §foder§a store.horstblocks.de");
					RucksackPlugin.getPlugin().usingBackpack.remove(event.getWhoClicked());
					return;
				} else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§aRucksack")) {
					int index = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]);
					player.openInventory(RucksackPlugin.getPlugin().getCache().get(player).get(index - 1).getInhalt());
					player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
					RucksackPlugin.getPlugin().usingBackpack.remove(event.getWhoClicked());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
