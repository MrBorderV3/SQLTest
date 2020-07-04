package me.border.sqltest;

import me.border.sqltest.storage.IDatabase;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Database extends IDatabase {
    protected Database(String host, String database, String username, String password, int port) {
        super(host, database, username, password, port);
    }

    public String getPlayerName(UUID uuid){
        CompletableFuture<String> future = new CompletableFuture<>();
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    ResultSet result = getData("Name FROM PlayerData WHERE UUID='" + uuid.toString() + "'");
                    if (result.next())
                        future.complete(result.getString("Name"));
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return translateFuture(future);
    }

    public UUID getPlayerUUID(String name){
        CompletableFuture<UUID> future = new CompletableFuture<>();
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    ResultSet result = getData("UUID FROM PlayerData WHERE Name='" + name + "'");
                    if (result.next())
                        future.complete(UUID.fromString(result.getString("UUID")));
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return translateFuture(future);
    }

}
