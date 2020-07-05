package me.border.sqltest;

import me.border.sqltest.storage.IDatabase;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Database extends IDatabase {
    protected Database(String host, String database, String username, String password, int port) {
        super(host, database, username, password, port);
    }

    public CompletableFuture<ObjectsWrapper<UUID, String, ?, ?>> getPlayerProfile(UUID uuid) {
        CompletableFuture<ObjectsWrapper<UUID, String, ?, ?>> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement ps = connection.prepareStatement("SELECT * FROM PlayerData WHERE UUID='" + uuid.toString() + "';");
                    ResultSet result = ps.executeQuery();
                    ObjectsWrapper<UUID, String, ? , ?> profileWrapper = new ObjectsWrapper<>();
                    if (result.next()){
                        profileWrapper.setT(UUID.fromString(result.getString("UUID")));
                        profileWrapper.setV(result.getString("Name"));
                    }
                    future.complete(profileWrapper);
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }

    public CompletableFuture<ObjectsWrapper<UUID, String, ?, ?>> getPlayerProfile(String name) {
        CompletableFuture<ObjectsWrapper<UUID, String, ?, ?>> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement ps = connection.prepareStatement("SELECT * FROM PlayerData WHERE Name='" + name + "';");
                    ResultSet result = ps.executeQuery();
                    ObjectsWrapper<UUID, String, ? , ?> profileWrapper = new ObjectsWrapper<>();
                    if (result.next()){
                        profileWrapper.setT(UUID.fromString(result.getString("UUID")));
                        profileWrapper.setV(result.getString("Name"));
                    }
                    future.complete(profileWrapper);
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }
}
