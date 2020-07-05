package me.border.sqltest.storage;

import me.border.sqltest.SQLTest;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public abstract class IDatabase {

    protected JavaPlugin plugin = SQLTest.getInstance();
    protected Connection connection;
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

    public CompletableFuture<Boolean> createTableIfNotExists(String tableName, String sql) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ResultSet result = metaData.getTables(null, null, tableName, null);
                    PreparedStatement ps = connection.prepareStatement("CREATE TABLE " + tableName + "(" + sql + ");");
                    boolean complete = ps.execute();
                    ps.close();

                    future.complete(complete);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }

    public CompletableFuture<Boolean> createTableIfNotExists(String tableName, String column1, String column2, int column1length, int column2length) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String idString = "id" + " INT NOT NULl AUTO_INCREMENT, ";
                    String column1Full = column1 + " VARCHAR(" + column1length + "), ";
                    String column2Full = column2 + " VARCHAR(" + column2length + "), ";
                    String primaryKey = "PRIMARY KEY(id)";
                    String query = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + idString + column1Full + column2Full + primaryKey + ");";
                    PreparedStatement ps = connection.prepareStatement(query);
                    boolean complete = ps.execute();
                    ps.close();

                    future.complete(complete);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return future;
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

    public CompletableFuture<Boolean> rowExists(String sql) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    PreparedStatement ps = connection.prepareStatement("SELECT " + sql + ";");
                    ResultSet result = ps.executeQuery();
                    future.complete(result.next());
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }

    public CompletableFuture<Boolean> createRowIfDoesntExist(String sql, String rowQuery){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    PreparedStatement ps = connection.prepareStatement("SELECT " + rowQuery + ";");
                    ResultSet result = ps.executeQuery();
                    boolean complete = result.next();
                    ps.close();
                    if (!complete){
                        PreparedStatement psUpdate = connection.prepareStatement("INSERT INTO " + sql + ";");
                        psUpdate.executeUpdate("INSERT INTO " + sql + ";");
                        psUpdate.close();
                    }
                    future.complete(complete);
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);

        return future;
    }

    public CompletableFuture<ResultSet> getData(String sql) {
        CompletableFuture<ResultSet> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String query = "SELECT " + sql + ";";
                    PreparedStatement ps = connection.prepareStatement(query);
                    future.complete(ps.executeQuery());
                    ps.close();
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
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO " + sql + ";");
                    ps.executeUpdate("INSERT INTO " + sql + ";");
                    ps.close();
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
                    metaData = connection.getMetaData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
