package me.border.sqltest.storage;

import me.border.sqltest.SQLTest;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class IDatabase {

    protected JavaPlugin plugin = SQLTest.getInstance();
    private Connection connection;
    protected Statement statement;
    protected DatabaseMetaData metaData;
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    protected IDatabase(String host, String database, String username, String password, int port) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
        startDb();
    }

    public void createTable(String tableName, String sql) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    statement.execute("CREATE TABLE " + tableName + "(" + sql + ");");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void createTable(String tableName, String column1, String column2, int column1length, int column2length) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String idString = "id" + " INT NOT NULl AUTO_INCREMENT, ";
                    String column1Full = column1 + " VARCHAR(" + column1length + "), ";
                    String column2Full = column2 + " VARCHAR(" + column2length + "), ";
                    String primaryKey = "PRIMARY KEY(id)";
                    String query = "CREATE TABLE " + tableName + "(" + idString + column1Full + column2Full + primaryKey + ");";
                    statement.execute(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public CompletableFuture<Boolean> tableExists(String table) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ResultSet result = metaData.getTables(null, null, table, null);
                    future.complete(result.next());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }

    public CompletableFuture<Boolean> rowExists(String sql){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                CompletableFuture<ResultSet> resultFuture = getData(sql);
                    resultFuture.whenComplete((b, err) ->{
                        try {
                            ResultSet result = resultFuture.get();
                            future.complete(result.next());
                        } catch (InterruptedException | ExecutionException | SQLException e) {
                            e.printStackTrace();
                        }
                    });
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }

    public CompletableFuture<ResultSet> getData(String sql) {
        CompletableFuture<ResultSet> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                String query = "SELECT " + sql + ";";
                try {
                    future.complete(statement.executeQuery(query));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }

    public void setData(String sql) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    statement.executeUpdate("INSERT INTO " + sql + ";");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void openConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            if (statement != null && !statement.isClosed())
                statement.close();
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void startDb() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();
                    metaData = connection.getMetaData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
