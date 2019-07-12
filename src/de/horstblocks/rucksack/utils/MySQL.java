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

	public boolean connect(String host, String port, String database, String user, String password) {
		if (!isConnected()) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(
						"jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", user,
						password);
				Bukkit.getLogger().info("Die Datenbankverbindung wurde erfolgreich hergestellt!");
				ic = true;
				createTable();
				return true;
			} catch (SQLException | ClassNotFoundException e) {
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
		RucksackPlugin.getPlugin().executorService.execute(() -> {
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
			Statement st;
			st = con.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException e) {
			System.err.println(e);
		}
		return rs;
	}

	public void createTable() {
		String start = "CREATE TABLE IF NOT EXISTS backpacks(Name VARCHAR(16), UUID VARCHAR(64)";
		String end = ", PRIMARY KEY (UUID));";

		String query = start;

		for (int i = 1; i <= RucksackPlugin.getPlugin().maxBackpacks; i++) {
			query += ", Rucksack" + i + " TEXT";
		}

		query += end;

		System.out.println("[" + query + "]");

		update(query);
	}

	public boolean existPlayer(String uuid) {
		try {
			ResultSet rs = getResult("SELECT Name FROM backpacks WHERE UUID = '" + uuid + "'");
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
		String clearInventory = InventoryConverter.inventoryToString(newInventory);
		if (!existPlayer(uuid)) {

			String start = "INSERT INTO backpacks(Name, UUID";

			String query = start;

			for (int i = 1; i <= RucksackPlugin.getPlugin().maxBackpacks; i++) {
				query += ", Rucksack" + i;
			}

			query += ") VALUES ('" + name + "', '" + uuid;

			for (int i = 1; i <= RucksackPlugin.getPlugin().maxBackpacks; i++) {
				query += "', '" + clearInventory;
			}

			query += "');";

			/*
			 * update("INSERT INTO backpacks(Name, UUID, Rucksack1, Rucksack2, Rucksack3, Rucksack4, Rucksack5) VALUES ('"
			 * + name + "', '" + uuid + "', '" + clearInventory + "', '" + clearInventory +
			 * "', '" + clearInventory + "', '" + clearInventory + "', '" + clearInventory +
			 * "');");
			 */

			update(query);
		}
	}

	public String getNameByUUID(String uuid) {
		String i = null;
		try {
			ResultSet rs = getResult("SELECT * FROM backpacks WHERE UUID = '" + uuid + "'");
			if (!rs.next() || String.valueOf(rs.getString("Name")) == null) {
				rs.close();
				return "0";
			}
			i = rs.getString("NAME");
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public Rucksack getRucksackInhalt(String uuid, int rucksack) {
		String inhalt = null;
		UUID id = UUID.fromString(uuid);
		String name = Bukkit.getOfflinePlayer(id).getName();
		if (existPlayer(uuid)) {
			try {
				ResultSet rs = getResult("SELECT * FROM backpacks WHERE UUID = '" + uuid + "'");
				if (!rs.next() || rs.getString("Rucksack" + rucksack) == null) {
					return null;
				}
				inhalt = rs.getString("Rucksack" + rucksack);
				rs.close();
				return new Rucksack(Bukkit.getPlayer(id), rucksack, InventoryConverter.stringToInventory(inhalt));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			createPlayer(name, uuid);
		}
		return getRucksackInhalt(uuid, rucksack);
	}

	public void setRucksackInhalt(String uuid, int rucksackID, Rucksack rucksack) {
		String inhalt = InventoryConverter.inventoryToString(rucksack.getInhalt());
		UUID id = UUID.fromString(uuid);
		String name = Bukkit.getOfflinePlayer(id).getName();
		if (existPlayer(uuid)) {
			update("UPDATE backpacks SET Rucksack" + rucksackID + "= '" + inhalt + "' WHERE UUID= '" + uuid + "';");
		} else {
			createPlayer(name, uuid);
			setRucksackInhalt(uuid, rucksackID, rucksack);
		}
	}
}