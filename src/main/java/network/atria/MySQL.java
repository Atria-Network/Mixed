package network.atria;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;

public class MySQL {

  private static MySQL mySQL;
  private HikariDataSource ds;

  public void connect() {
    HikariConfig hikari = new HikariConfig();
    FileConfiguration config = Mixed.get().getConfig();
    String ROOT = "MySQL.";
    hikari.setJdbcUrl(
        "jdbc:mysql://"
            + config.getString(ROOT + "Host")
            + ":"
            + config.getInt(ROOT + "Port")
            + "/"
            + config.getString(ROOT + "Database")
            + "?useSSL=false");
    hikari.addDataSourceProperty("user", config.getString(ROOT + "User"));
    hikari.addDataSourceProperty("password", config.getString(ROOT + "Password"));

    ds = new HikariDataSource(hikari);
    mySQL = this;
  }

  public void createTables() {
    Connection connection = null;
    try {
      connection = ds.getConnection();
      Statement statement = connection.createStatement();

      statement.executeUpdate(
          "CREATE TABLE IF NOT EXISTS STATS(UUID varchar(36) NOT NULL PRIMARY KEY, NAME varchar(20), KILLS int, DEATHS int, FLAGS int, CORES int, WOOLS int, MONUMENTS int, PLAYTIME int, POINTS int, WINS int, LOSES int);");
      statement.executeUpdate(
          "CREATE TABLE IF NOT EXISTS WEEK_STATS(UUID varchar(36) NOT NULL PRIMARY KEY, NAME varchar(20), KILLS int, DEATHS int, FLAGS int, CORES int, WOOLS int, MONUMENTS int, PLAYTIME int, POINTS int, WINS int, LOSES int);");
      statement.executeUpdate(
          "CREATE TABLE IF NOT EXISTS RANKS(UUID varchar(36) NOT NULL PRIMARY KEY, NAME varchar(20), GAMERANK varchar(20), EFFECT varchar(20), SOUND varchar(20), PROJECTILE varchar(20));");

      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static MySQL get() {
    return mySQL;
  }

  public HikariDataSource getHikari() {
    return ds;
  }

  public static class SQLQuery {

    public static void update(String table, String column, String value, UUID uuid) {
      PreparedStatement statement = null;
      Connection connection = null;
      StringBuilder sql = new StringBuilder();
      sql.append("UPDATE ")
          .append(table)
          .append(" SET ")
          .append(column)
          .append(" = ? WHERE UUID = ?");
      try {
        connection = MySQL.get().getHikari().getConnection();
        statement = connection.prepareStatement(sql.toString());
        statement.setString(1, value);
        statement.setString(2, uuid.toString());
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        closeConnection(connection);
        closeStatement(statement);
      }
    }

    public static String getAsString(String table, String column, UUID uuid) {
      String result = null;
      PreparedStatement statement = null;
      Connection connection = null;
      ResultSet rs = null;
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT ").append(column).append(" FROM ").append(table).append(" WHERE UUID = ?");
      try {
        connection = MySQL.get().getHikari().getConnection();
        statement = connection.prepareStatement(sql.toString());
        statement.setString(1, uuid.toString());
        rs = statement.executeQuery();
        if (rs.next()) result = rs.getString(column);
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        closeConnection(connection);
        closeResultSet(rs);
        closeStatement(statement);
      }
      return result;
    }

    public static Integer getAsInteger(String table, String column, UUID uuid) {
      int result = 0;
      PreparedStatement statement = null;
      Connection connection = null;
      ResultSet rs = null;
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT ").append(column).append(" FROM ").append(table).append(" WHERE UUID = ?");
      try {
        connection = MySQL.get().getHikari().getConnection();
        statement = connection.prepareStatement(sql.toString());
        statement.setString(1, uuid.toString());
        rs = statement.executeQuery();
        if (rs.next()) result = rs.getInt(column);
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        closeConnection(connection);
        closeResultSet(rs);
        closeStatement(statement);
      }
      return result;
    }

    public static boolean playerExists(UUID uuid) {
      ResultSet rs = null;
      PreparedStatement statement = null;
      Connection connection = null;
      String sql = "SELECT UUID FROM STATS WHERE UUID = ? LIMIT 1";
      try {
        connection = MySQL.get().getHikari().getConnection();
        statement = connection.prepareStatement(sql);
        statement.setString(1, uuid.toString());
        statement.setString(2, uuid.toString());

        rs = statement.executeQuery();
        return (rs.next() && rs.getString("UUID") != null);
      } catch (SQLException e) {
        e.printStackTrace();
        return false;
      } finally {
        closeStatement(statement);
        closeResultSet(rs);
        closeConnection(connection);
      }
    }

    public static void createPlayer(String name, UUID uuid) {
      Statement statement = null;
      Connection connection = null;
      String query =
          "INSERT INTO STATS(UUID, NAME, KILLS, DEATHS, FLAGS, CORES, WOOLS, MONUMENTS, PLAYTIME, POINTS, WINS, LOSES) VALUES ('"
              + uuid
              + "', '"
              + name
              + "', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');";
      String query2 =
          "INSERT INTO WEEK_STATS(UUID, NAME, KILLS, DEATHS, FLAGS, CORES, WOOLS, MONUMENTS, PLAYTIME, POINTS, WINS, LOSES) VALUES ('"
              + uuid
              + "', '"
              + name
              + "', '0', '0', '0', '0', '0', '0', '0', '0', '0');";
      String query3 =
          "INSERT INTO RANKS(UUID, NAME, GAMERANK, EFFECT, SOUND, PROJECTILE) VALUES ('"
              + uuid
              + "', '"
              + name
              + "', 'wood_iii', 'NONE', 'DEFAULT', 'NONE');";
      try {
        connection = MySQL.get().getHikari().getConnection();
        statement = connection.createStatement();
        statement.addBatch(query);
        statement.addBatch(query2);
        statement.addBatch(query3);
        statement.executeBatch();

      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        try {
          if (statement != null) {
            statement.close();
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
        closeConnection(connection);
      }
    }

    public static void create_weekly_table(UUID uuid, String name) {
      PreparedStatement statement = null;
      Connection connection = null;
      try {
        connection = MySQL.get().getHikari().getConnection();
        statement =
            connection.prepareStatement(
                "INSERT INTO WEEK_STATS(UUID, NAME, KILLS, DEATHS, FLAGS, CORES, WOOLS, MONUMENTS, PLAYTIME, WINS, LOSES) VALUES (?, ?, '0', '0', '0', '0', '0', '0', '0', '0', '0');");
        statement.setString(1, uuid.toString());
        statement.setString(2, name);
        statement.execute();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        closeStatement(statement);
        closeConnection(connection);
      }
    }

    public static boolean playerExist_in_weekly_table(UUID uuid) {
      Connection connection = null;
      PreparedStatement statement = null;
      ResultSet rs = null;
      try {
        connection = MySQL.get().getHikari().getConnection();
        statement =
            connection.prepareStatement("SELECT UUID FROM WEEK_STATS WHERE UUID = ? LIMIT 1");
        statement.setString(1, uuid.toString());
        rs = statement.executeQuery();
        return (rs.next() && rs.getString("UUID") != null);
      } catch (SQLException e) {
        e.printStackTrace();
        return false;
      } finally {
        closeStatement(statement);
        closeResultSet(rs);
        closeConnection(connection);
      }
    }

    public static void closeConnection(Connection connection) {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    public static void closeStatement(PreparedStatement statement) {
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    public static void closeResultSet(ResultSet rs) {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
