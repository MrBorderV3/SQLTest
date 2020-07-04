package me.border.sqltest;

import me.border.sqltest.storage.IDatabase;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SQLTest extends JavaPlugin {

    private static SQLTest instance;
    private Database database;

    public static SQLTest getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.database = new Database("localhost", "spigot", "root", "Lmao777gyre1", 3306);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!database.tableExists("PlayerData"))
                    database.createTable("PlayerData", "Name", "UUID", 16, 36);
                String uuid = "d1152d23-501a-4907-a0ce-44c4de2c3990";
                String setRowQuery = "Name FROM PlayerData WHERE UUID='" + uuid + "'";
                if (!database.rowExists(setRowQuery))
                    database.setData("PlayerData(Name, UUID) VALUES('MrBorder','" + uuid + "')");

                UUID uuidObj = database.getPlayerUUID("MrBorder");
                String name = database.getPlayerName(uuidObj);

                System.out.println(uuidObj);
                System.out.println(name);
            }
        }.runTaskLater(this, 40L);

        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        database.closeConnection();
    }
}
