package de.horstblocks.rucksack.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryConverter {

//	static String sep = "§";
//	static String blockSep = "§";

	@SuppressWarnings("deprecation")
	public static String InventoryToString(Inventory invInventory) {
		String serialization = invInventory.getSize() + ";";
		for (int i = 0; i < invInventory.getSize(); i++) {
			ItemStack is = invInventory.getItem(i);
			if (is != null) {
				String serializedItemStack = new String();

				String isType = String.valueOf(is.getType().getId());
				serializedItemStack += "t@" + isType;

				if (is.getDurability() != 0) {
					String isDurability = String.valueOf(is.getDurability());
					serializedItemStack += ":d@" + isDurability;
				}

				if (is.getAmount() != 1) {
					String isAmount = String.valueOf(is.getAmount());
					serializedItemStack += ":a@" + isAmount;
				}

				Map<Enchantment, Integer> isEnch = is.getEnchantments();
				if (isEnch.size() > 0) {
					for (Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
						serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
					}
				}

				serialization += i + "#" + serializedItemStack + ";";
			}
		}
		return serialization;
	}

	@SuppressWarnings("deprecation")
	public static Inventory StringToInventory(String invString) {
		String[] serializedBlocks = invString.split(";");
		String invInfo = serializedBlocks[0];
		Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));

		for (int i = 1; i < serializedBlocks.length; i++) {
			String[] serializedBlock = serializedBlocks[i].split("#");
			int stackPosition = Integer.valueOf(serializedBlock[0]);

			if (stackPosition >= deserializedInventory.getSize()) {
				continue;
			}

			ItemStack is = null;
			Boolean createdItemStack = false;

			String[] serializedItemStack = serializedBlock[1].split(":");
			for (String itemInfo : serializedItemStack) {
				String[] itemAttribute = itemInfo.split("@");
				if (itemAttribute[0].equals("t")) {
					is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
					createdItemStack = true;
				} else if (itemAttribute[0].equals("d") && createdItemStack) {
					is.setDurability(Short.valueOf(itemAttribute[1]));
				} else if (itemAttribute[0].equals("a") && createdItemStack) {
					is.setAmount(Integer.valueOf(itemAttribute[1]));
				} else if (itemAttribute[0].equals("e") && createdItemStack) {
					is.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])),
							Integer.valueOf(itemAttribute[2]));
				}
			}
			deserializedInventory.setItem(stackPosition, is);
		}

		return deserializedInventory;
	}

//	public static String invToString(Inventory inventory) {
//		String serInv = Math.round(inventory.getSize() / 9) + blockSep;
//		serInv += inventory.getName().replaceAll("§", "&") + blockSep;
//		ItemStack[] items = inventory.getContents();
//		for (int i = 0; i < Math.round(inventory.getSize() / 9) * 9; i++) {
//			ItemStack item = items[i];
//			if (item != null) {
//				if (item.getType() != Material.AIR) {
//					serInv += "@w" + sep + i;
//					for (int k = 0; k < Material.values().length; k++)
//						if (Material.values()[k].equals(item.getType()))
//							serInv += "@m" + sep + k;
//					serInv += "@a" + sep + item.getAmount();
//					if (item.getDurability() != 0)
//						serInv += "@d" + sep + item.getDurability();
//					if (item.hasItemMeta()) {
//						if (item.getItemMeta().hasDisplayName())
//							serInv += "@dn" + sep + item.getItemMeta().getDisplayName().replaceAll("§", "&");
//						if (item.getItemMeta().hasLore()) {
//							serInv += "@l" + sep + item.getItemMeta().getLore().get(0).replaceAll("§", "&");
//							for (int k = 1; k < item.getItemMeta().getLore().size(); k++) {
//								serInv += sep + item.getItemMeta().getLore().get(k).replaceAll("§", "&");
//							}
//						}
//						if (item.getItemMeta().hasEnchants()) {
//							serInv += "@e" + sep;
//							for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
//								for (int k = 0; k < Enchantment.values().length; k++) {
//									if (Enchantment.values()[k].equals(ench.getKey())) {
//										if (Enchantment.values().length - k > 1) {
//											serInv += k + "<>" + ench.getValue() + sep;
//										} else {
//											serInv += k + "<>" + ench.getValue();
//										}
//									}
//								}
//							}
//						}
//					}
//					serInv += blockSep;
//				}
//			}
//		}
//		return serInv;
//	}
//
//	public static Inventory stringToInv(String string) {
//		String[] blocks = string.split(blockSep);
//		Inventory ser = Bukkit.createInventory(null, Integer.valueOf(blocks[0]) * 9, blocks[1].replaceAll("&", "§"));
//		for (int j = 2; j < blocks.length; j++) {
//			String[] itemAttributes = blocks[j].split("@");
//			ItemStack item = null;
//			int where = 0;
//			for (int i = 0; i < itemAttributes.length; i++) {
//				String[] attribute = itemAttributes[i].split(sep);
//				if (attribute[0].equals("w")) {
//					where = Integer.valueOf(attribute[1]);
//				} else if (attribute[0].equals("m")) {
//					item = new ItemStack(Material.values()[Integer.valueOf(attribute[1])], 1);
//				} else if (attribute[0].equals("a")) {
//					item.setAmount(Integer.valueOf(attribute[1]));
//				} else if (attribute[0].equals("d")) {
//					item.setDurability(Short.valueOf(attribute[1]));
//				} else if (attribute[0].equals("dn")) {
//					ItemMeta itemMeta = item.getItemMeta();
//					itemMeta.setDisplayName(attribute[1].replaceAll("&", "§"));
//					item.setItemMeta(itemMeta);
//				} else if (attribute[0].equals("l")) {
//					ItemMeta itemMeta = item.getItemMeta();
//					ArrayList<String> lores = new ArrayList<String>();
//					for (int k = 1; k < attribute.length; k++) {
//						lores.add(attribute[k].replaceAll("&", "§"));
//					}
//					itemMeta.setLore(lores);
//					item.setItemMeta(itemMeta);
//				} else if (attribute[0].equals("e")) {
//					ItemMeta itemMeta = item.getItemMeta();
//					for (int k = 1; k < attribute.length; k++) {
//						String[] spe = attribute[k].split("<>");
//						itemMeta.addEnchant(Enchantment.values()[Integer.valueOf(spe[0])], Integer.valueOf(spe[1]),
//								true);
//					}
//					item.setItemMeta(itemMeta);
//				}
//			}
//			ser.setItem(where, item);
//		}
//		return ser;
//	}
}