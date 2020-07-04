package me.border.sqltest;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
                CompletableFuture<Boolean> tableExistsFuture = database.tableExists("PlayerData");
                tableExistsFuture.whenComplete((b, err) -> {
                    try {
                        if (!tableExistsFuture.get()){
                            database.createTable("PlayerData", "Name", "UUID", 16, 36);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });

                String uuid = "d1152d23-501a-4907-a0ce-44c4de2c3990";
                String setRowQuery = "Name FROM PlayerData WHERE UUID='" + uuid + "'";
                CompletableFuture<Boolean> rowExistsFuture = database.rowExists(setRowQuery);
                rowExistsFuture.whenComplete((b, err) -> {
                    try {
                        if (!rowExistsFuture.get()){
                            database.setData("PlayerData(Name, UUID) VALUES('MrBorder','" + uuid + "')");
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });

                CompletableFuture<ObjectsWrapper<UUID, String, ?, ?>> profileFuture = database.getPlayerProfile("MrBorder");
                profileFuture.whenComplete((b, err) ->{
                    try {
                        System.out.println("UUID = " + profileFuture.get().getT());
                        System.out.println("Name = " + profileFuture.get().getV());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            }
        }.runTaskLater(this, 40L);
    }

    @Override
    public void onDisable() {
        database.closeConnection();
    }
}
