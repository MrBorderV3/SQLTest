package me.border.sqltest;

import me.border.sqltest.storage.IDatabase;
import org.bukkit.scheduler.BukkitRunnable;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Database extends IDatabase {
    protected Database(String host, String database, String username, String password, int port) {
        super(host, database, username, password, port);
    }

    public CompletableFuture<ObjectsWrapper<UUID, String, ?, ?>> getPlayerProfile(UUID uuid) {
        CompletableFuture<ObjectsWrapper<UUID, String, ?, ?>> future = new CompletableFuture<>();
        CompletableFuture<ResultSet> resultFuture = getData("* FROM PlayerData WHERE UUID='" + uuid.toString() + "'");
        resultFuture.whenComplete((b, err) -> {
                try {
                ResultSet result = resultFuture.get();
                if (result.next()) {
                    ObjectsWrapper<UUID, String, ? , ?> profileWrapper = new ObjectsWrapper<>();
                    profileWrapper.setT(UUID.fromString(result.getString("UUID")));
                    profileWrapper.setV(result.getString("Name"));
                    future.complete(profileWrapper);
                }
            } catch (SQLException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public CompletableFuture<ObjectsWrapper<UUID, String, ?, ?>> getPlayerProfile(String name) {
        CompletableFuture<ObjectsWrapper<UUID, String, ?, ?>> future = new CompletableFuture<>();
        CompletableFuture<ResultSet> resultFuture = getData("* FROM PlayerData WHERE Name='" + name + "'");
        resultFuture.whenComplete((b, err) -> {
            try {
                ResultSet result = resultFuture.get();
                if (result.next()) {
                    ObjectsWrapper<UUID, String, ? , ?> profileWrapper = new ObjectsWrapper<>();
                    profileWrapper.setT(UUID.fromString(result.getString("UUID")));
                    profileWrapper.setV(result.getString("Name"));
                    future.complete(profileWrapper);
                }
            } catch (SQLException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        return future;
    }
}
