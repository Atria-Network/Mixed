package network.atria;

import static net.kyori.adventure.text.Component.text;

import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import net.kyori.adventure.text.format.NamedTextColor;
import network.atria.Effects.Particles.Effect;
import network.atria.Manager.EffectManager;
import network.atria.Ranks.Rank;
import network.atria.UserProfile.UserProfile;
import org.bukkit.configuration.file.FileConfiguration;

public class MySQL {

  private static SQLQuery query;
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
    query = new SQLQuery();
  }

  public static MySQL get() {
    return mySQL;
  }

  public HikariDataSource getHikari() {
    return ds;
  }

  public static SQLQuery query() {
    return query;
  }

  private interface queries {
    void createTables();

    UserProfile registerPlayer(String name, UUID uuid);

    boolean playerExists(UUID uuid);

    void updateProfile(UserProfile profile);

    void insertStats(
        UUID uuid,
        int kill,
        int death,
        int flag,
        int core,
        int wool,
        int monument,
        int playtime,
        int point,
        int win,
        int lose);

    Map<String, Object> getStats(UUID uuid);
  }

  public static class SQLQuery implements queries {

    @Override
    public void createTables() {
      execute(
          "CREATE TABLE IF NOT EXISTS stats(id varchar(36) NOT NULL PRIMARY KEY, `kill` int, death int, flag int, core int, wool int, monument int, win int, lose int, playtime int, point int)");
      execute(
          "CREATE TABLE IF NOT EXISTS weekly_stats(id varchar(36) NOT NULL, date DATE NOT NULL UNIQUE KEY, `kill` int, death int, flag int, core int, wool int, monument int, win int, lose int, playtime int)");
      execute(
          "CREATE TABLE IF NOT EXISTS effect(id varchar(36) NOT NULL PRIMARY KEY, effect varchar(16), sound varchar(16), projectile varchar(16), `rank` varchar(16))");
    }

    @Override
    public UserProfile registerPlayer(String name, UUID uuid) {
      execute(
          "INSERT INTO stats(id, `kill`, death, flag, core, wool, monument, playtime, point, win, lose) VALUES (?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) ON DUPLICATE KEY UPDATE id = VALUES(id)",
          uuid.toString());
      execute(
          "INSERT IGNORE INTO effect(id, `rank`, effect, sound, projectile) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id = VALUES(id)",
          uuid.toString(),
          "wood_iii",
          "NONE",
          "DEFAULT",
          "NONE");
      return registerUserProfile(name, uuid);
    }

    @Override
    public boolean playerExists(UUID uuid) {
      Map<String, Object> result =
          get("SELECT id FROM stats WHERE id = ? LIMIT 1", uuid.toString());
      if (result == null) {
        return false;
      }
      return result.get("id") != null;
    }

    @Override
    public void updateProfile(UserProfile profile) {
      execute(
          "UPDATE effect SET `rank` = ?, effect = ?, sound = ?, projectile = ? WHERE id = ?",
          profile.getRank().getName(),
          profile.getKilleffect().getUncoloredName(),
          profile.getKillsound().getUncoloredName(),
          profile.getProjectile().getUncoloredName(),
          profile.getUUID().toString());
    }

    @Override
    public void insertStats(
        UUID uuid,
        int kill,
        int death,
        int flag,
        int core,
        int wool,
        int monument,
        int playtime,
        int point,
        int win,
        int lose) {
      execute(
          "INSERT INTO stats(id, `kill`, death, flag, core, wool, monument, playtime, point, win, lose) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `kill` = `kill` + VALUES(`kill`), death = death + VALUES(death), flag = flag + VALUES(flag), wool = wool + VALUES(wool), monument = monument + VALUES(monument), playtime = playtime + VALUES(playtime), point = point + VALUES(point), win = win + VALUES(win), lose = lose + VALUES(lose)",
          uuid.toString(),
          kill,
          death,
          flag,
          core,
          wool,
          monument,
          playtime,
          point,
          win,
          lose);
      execute(
          "INSERT INTO weekly_stats(id, date, `kill`, death, flag, core, wool, monument, win, lose, playtime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `kill` = `kill` + VALUES(`kill`), death = death + VALUES(death), flag = flag + VALUES(flag), wool = wool + VALUES(wool), monument = monument + VALUES(monument), win = win + VALUES(win), lose = lose + VALUES(lose), playtime = playtime + VALUES(playtime)",
          uuid.toString(),
          new Date(System.currentTimeMillis()),
          kill,
          death,
          flag,
          core,
          wool,
          monument,
          win,
          lose,
          playtime);
    }

    @Override
    public Map<String, Object> getStats(UUID uuid) {
      return get(
          "SELECT `kill`, death, flag, core, wool, monument, win, lose FROM stats WHERE id = ?",
          uuid.toString());
    }

    private UserProfile registerUserProfile(String name, UUID uuid) {
      Map<String, Object> result =
          get(
              "SELECT `rank`, effect, sound, projectile, point FROM effect, stats WHERE stats.id = ? LIMIT 1",
              uuid.toString());

      Effect effect =
          EffectManager.getKillEffects().stream()
              .filter(x -> x.getName().equalsIgnoreCase(result.get("effect").toString()))
              .findFirst()
              .orElse(new Effect("NONE", null, 0, 0, false, false));
      Effect sound =
          EffectManager.getSounds().stream()
              .filter(x -> x.getName().equalsIgnoreCase(result.get("sound").toString()))
              .findFirst()
              .orElse(new Effect("DEFAULT", null, 0, 0, false, false));
      Effect projectile =
          EffectManager.getProjectiles().stream()
              .filter(x -> x.getName().equalsIgnoreCase(result.get("projectile").toString()))
              .findFirst()
              .orElse(new Effect("NONE", null, 0, 0, false, false));

      if (result != null) {
        Rank rank = Mixed.get().getRankManager().getRank(result.get("rank").toString());
        int point = (Integer) result.get("point");

        return UserProfile.of(name, uuid, rank, effect, projectile, sound, point);
      } else {
        return UserProfile.of(
            name,
            uuid,
            new Rank("wood_iii", text("WOOD III", NamedTextColor.GOLD), 0),
            effect,
            projectile,
            sound,
            0);
      }
    }

    private void execute(String sql) {
      Mixed.get()
          .getServer()
          .getScheduler()
          .runTaskAsynchronously(
              Mixed.get(),
              () -> {
                Connection connection = null;
                PreparedStatement statement = null;
                try {
                  connection = MySQL.get().getHikari().getConnection();
                  statement = connection.prepareStatement(sql);
                  statement.executeUpdate();
                } catch (SQLException e) {
                  e.printStackTrace();
                } finally {
                  close(connection);
                  close(statement);
                }
              });
    }

    private void execute(String sql, Object... values) {
      Mixed.get()
          .getServer()
          .getScheduler()
          .runTaskAsynchronously(
              Mixed.get(),
              () -> {
                Connection connection = null;
                PreparedStatement statement = null;
                try {
                  connection = MySQL.get().getHikari().getConnection();
                  statement = connection.prepareStatement(sql);
                  setValues(statement, values);
                  statement.executeUpdate();
                } catch (SQLException e) {
                  e.printStackTrace();
                } finally {
                  close(connection);
                  close(statement);
                }
              });
    }

    private Map<String, Object> get(String sql) {
      AtomicReference<Map<String, Object>> result = new AtomicReference<>();

      Mixed.get()
          .getServer()
          .getScheduler()
          .runTaskAsynchronously(
              Mixed.get(),
              () -> {
                Connection connection = null;
                PreparedStatement statement = null;
                ResultSet rs = null;
                ResultSetMetaData metaData;
                try {
                  connection = MySQL.get().getHikari().getConnection();
                  statement = connection.prepareStatement(sql);
                  rs = statement.executeQuery();
                  metaData = rs.getMetaData();

                  if (rs.next()) {
                    result.set(new HashMap<>());
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                      result.get().put(metaData.getColumnName(i), rs.getObject(i));
                    }
                  }
                } catch (SQLException e) {
                  e.printStackTrace();
                } finally {
                  close(connection);
                  close(statement);
                  close(rs);
                }
              });
      return result.get();
    }

    private Map<String, Object> get(String sql, Object... values) {
      Connection connection = null;
      PreparedStatement statement = null;
      ResultSet rs = null;
      ResultSetMetaData metaData;
      Map<String, Object> result;
      try {
        connection = MySQL.get().getHikari().getConnection();
        statement = connection.prepareStatement(sql);
        setValues(statement, values);
        rs = statement.executeQuery();
        metaData = rs.getMetaData();

        if (rs.next()) {
          result = Maps.newHashMap();
          for (int i = 1; i <= metaData.getColumnCount(); i++) {
            result.put(metaData.getColumnName(i), rs.getObject(i));
          }
          return result;
        }
      } catch (SQLException e) {
        e.printStackTrace();
        return null;
      } finally {
        close(connection);
        close(statement);
        close(rs);
      }
      return null;
    }

    private static void setValues(PreparedStatement statement, Object... values)
        throws SQLException {
      for (int i = 0; i < values.length; i++) {
        statement.setObject(i + 1, values[i]);
      }
    }

    private void close(ResultSet rs) {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    private void close(PreparedStatement statement) {
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    private void close(Connection connection) {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
