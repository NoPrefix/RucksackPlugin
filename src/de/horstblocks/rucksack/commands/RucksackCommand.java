package de.horstblocks.rucksack.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.horstblocks.rucksack.RucksackPlugin;

public class RucksackCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {

		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage("Du musst ein Spieler sein!");
			return true;
		}

		Player player = (Player) commandSender;

		if (arguments.length == 0) {
			Inventory backpacks = Bukkit.createInventory(null, 9 * ((RucksackPlugin.getPlugin().maxBackpacks/9)+1), "§aDeine Rucksäcke");

			ItemStack noPermission = new ItemStack(Material.REDSTONE);
			ItemMeta noPermissionMeta = noPermission.getItemMeta();
			noPermissionMeta.setDisplayName("§cRucksack");
			ArrayList<String> lore = new ArrayList<>();
			lore.add("§cKeine Rechte!");
			noPermissionMeta.setLore(lore);
			noPermission.setItemMeta(noPermissionMeta);

			for (int i = 0; i < RucksackPlugin.getPlugin().maxBackpacks; i++) {
				backpacks.setItem(i, noPermission);
			}

			ItemStack usableBackpack = new ItemStack(Material.CHEST);
			ItemMeta usableBackpackMeta = usableBackpack.getItemMeta();
			usableBackpackMeta.setDisplayName("§aRucksack %");
			ArrayList<String> usableLore = new ArrayList<>();
			usableLore.add("§aBenutzbar!");
			usableLore.add("§7Linksklick §8= §aöffnen");
			usableBackpackMeta.setLore(usableLore);
			usableBackpack.setItemMeta(usableBackpackMeta);

			for(int i = 1; i <= RucksackPlugin.getPlugin().maxBackpacks; i++) {
				if(player.hasPermission("horstblocks.rucksack." + i)) {
					ItemStack usable = usableBackpack.clone();
					ItemMeta usableMetaNew = usable.getItemMeta();
					usableMetaNew.setDisplayName(usableMetaNew.getDisplayName().replace("%", String.valueOf(i)));
					usable.setItemMeta(usableMetaNew);
					backpacks.setItem(i - 1, usable);
				}
			}
			
			/*if (player.hasPermission("horstblock.rucksack.1")) {
				ItemMeta backbackMeta = usableBackpack.getItemMeta();
				backbackMeta.setDisplayName(backbackMeta.getDisplayName().replace('%', '1'));
				usableBackpack.setItemMeta(backbackMeta);
				backpacks.setItem(11, usableBackpack);
			}

			if (player.hasPermission("horstblock.rucksack.2")) {
				ItemMeta backbackMeta = usableBackpack.getItemMeta();
				backbackMeta.setDisplayName(backbackMeta.getDisplayName().replace('1', '2'));
				usableBackpack.setItemMeta(backbackMeta);
				backpacks.setItem(12, usableBackpack);
			}

			if (player.hasPermission("horstblock.rucksack.3")) {
				ItemMeta backbackMeta = usableBackpack.getItemMeta();
				backbackMeta.setDisplayName(backbackMeta.getDisplayName().replace('2', '3'));
				usableBackpack.setItemMeta(backbackMeta);
				backpacks.setItem(13, usableBackpack);
			}

			if (player.hasPermission("horstblock.rucksack.4")) {
				ItemMeta backbackMeta = usableBackpack.getItemMeta();
				backbackMeta.setDisplayName(backbackMeta.getDisplayName().replace('3', '4'));
				usableBackpack.setItemMeta(backbackMeta);
				backpacks.setItem(14, usableBackpack);
			}

			if (player.hasPermission("horstblock.rucksack.5")) {
				ItemMeta backbackMeta = usableBackpack.getItemMeta();
				backbackMeta.setDisplayName(backbackMeta.getDisplayName().replace('4', '5'));
				usableBackpack.setItemMeta(backbackMeta);
				backpacks.setItem(15, usableBackpack);
			}*/

			player.openInventory(backpacks);
			player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
			RucksackPlugin.getPlugin().usingBackpack.add(player);
			return true;
		}
		return false;
	}

}
