package de.horstblocks.rucksack.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Rucksack {

	private Player holder;
	private int id;
	private Inventory inhalt;
	
	public Rucksack(Player holder, int id, Inventory inhalt) {
		this.holder = holder;
		this.id = id;
		this.inhalt = inhalt;
	}

	public Player getHolder() {
		return holder;
	}

	public void setHolder(Player holder) {
		this.holder = holder;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Inventory getInhalt() {
		return inhalt;
	}

	public void setInhalt(Inventory inhalt) {
		this.inhalt = inhalt;
	}
	
}
