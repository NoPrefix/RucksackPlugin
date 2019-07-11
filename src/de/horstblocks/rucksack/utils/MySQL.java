package de.horstblocks.rucksack.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import de.horstblocks.rucksack.RucksackPlugin;

public class MySQL {
	private static Connection con;
	private boolean ic;

	public boolean isConnected() {
		return this.ic;
	}

	public boolean connect(String HOST, String PORT, String DATABASE, String USER, String PASSWORD) {
		if (!isConnected()) {
			try {
				con = DriverManager.getConnection(
						"jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true&useSSL=false", USER,
						PASSWORD);
				Bukkit.getLogger().info("Die Datenbankverbindung wurde erfolgreich hergestellt!");
				ic = true;
				createTable();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				Bukkit.getLogger().info("Die Datenbankverbindung konnte nicht hergestellt werden!");
				Bukkit.getLogger().info("Fehler: ");
				Bukkit.getLogger().info(e.getMessage());
				ic = false;
				return false;
			}
		}
		return false;
	}

	public void close() {
		try {
			if (con != null) {
				con.close();
				Bukkit.getLogger().info("Die Datenkbankverbindung wurde erfolgreich beendet.");
				this.ic = false;
			} else {
				Bukkit.getLogger().info("Derzeit ist keine Datenbankverbindung aktiv!");
			}
		} catch (SQLException e) {
			Bukkit.getLogger()
					.info("Die Datenbankverbindung konnte nicht geschlossen werden!\nFehler:\n" + e.getMessage());
		}
	}

	public void update(String qry) {
		Bukkit.getScheduler().runTaskAsynchronously(RucksackPlugin.getPlugin(), () -> {
			try {
				Statement st = con.createStatement();
				st.executeUpdate(qry);
				st.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		});
	}

	public ResultSet getResult(String qry) {
		
		ResultSet rs = null;
		try {
			Statement st = con.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException e) {
			System.err.println(e);
		}
		return rs;
	}

	public void createTable() {
		update("CREATE TABLE IF NOT EXISTS backpacks(Name VARCHAR(16), UUID VARCHAR(64), "
				+ "Rucksack1 TEXT, Rucksack2 TEXT, Rucksack3 TEXT, Rucksack4 TEXT, Rucksack5 TEXT);");
	}

	public boolean existPlayerName(String uuid) {
		try {
			ResultSet rs = getResult("SELECT Name FROM backpacks WHERE UUID = '" + uuid + "'");
			System.out.print("" + rs);
			if (rs.next()) {
				rs.close();
				return true;
			}
			rs.close();
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void createPlayer(String name, String uuid) {
		Inventory newInventory = Bukkit.createInventory(null, 9 * 3);
		String clearInventory = InventoryConverter.InventoryToString(newInventory);
		if (!existPlayerName(uuid)) {
			update("INSERT INTO backpacks(Name, UUID, Rucksack1, Rucksack2, Rucksack3, Rucksack4, Rucksack5) VALUES ('"
					+ name + "', '" + uuid + "', '" + clearInventory + "', '" + clearInventory + "', '" + clearInventory
					+ "', '" + clearInventory + "', '" + clearInventory + "');");
		}
	}

	public String getNameByUUID(String uuid) {
		String i = null;
		try {
			ResultSet rs = getResult("SELECT * FROM backpacks WHERE UUID = '" + uuid + "'");
			if (!rs.next() || String.valueOf(rs.getString("Name")) == null) {
				return "0";
			}
			i = rs.getString("NAME");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public Rucksack getRucksackInhalt(String uuid, int rucksack) {
		String inhalt = null;
		UUID id = UUID.fromString(uuid);
		String name = Bukkit.getOfflinePlayer(id).getName();
		if (existPlayerName(uuid)) {
			try {
				ResultSet rs = getResult("SELECT * FROM backpacks WHERE UUID = '" + uuid + "'");
				if (!rs.next() || rs.getString("Rucksack" + rucksack) == null) {
					return null;
				}
				inhalt = rs.getString("Rucksack" + rucksack);
				return new Rucksack(Bukkit.getPlayer(id), rucksack, InventoryConverter.StringToInventory(inhalt));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			createPlayer(name, uuid);
		}
		return getRucksackInhalt(uuid, rucksack);
	}

	public void setRucksackInhalt(String uuid, int rucksackID, Rucksack rucksack) {
		String inhalt = InventoryConverter.InventoryToString(rucksack.getInhalt());
		UUID id = UUID.fromString(uuid);
		String name = Bukkit.getOfflinePlayer(id).getName();
		if (existPlayerName(uuid)) {
			update("UPDATE backpacks SET Rucksack" + rucksackID + "= '" + inhalt + "' WHERE UUID= '" + uuid + "';");
		} else {
			createPlayer(name, uuid);
			setRucksackInhalt(uuid, rucksackID, rucksack);
		}
	}
}