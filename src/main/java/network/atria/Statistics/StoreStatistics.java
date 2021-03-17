package network.atria.Statistics;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreStatistics {

  private final Map<UUID, AtomicInteger> kills;
  private final Map<UUID, AtomicInteger> deaths;
  private final Map<UUID, AtomicInteger> wools;
  private final Map<UUID, AtomicInteger> monuments;
  private final Map<UUID, AtomicInteger> cores;
  private final Map<UUID, AtomicInteger> flags;
  private final Map<UUID, AtomicInteger> points;
  private final Map<UUID, AtomicInteger> wins;
  private final Map<UUID, AtomicInteger> losses;
  private final Map<UUID, AtomicInteger> playtime;

  public StoreStatistics() {
    this.kills = Maps.newHashMap();
    this.deaths = Maps.newHashMap();
    this.wools = Maps.newHashMap();
    this.monuments = Maps.newHashMap();
    this.cores = Maps.newHashMap();
    this.flags = Maps.newHashMap();
    this.points = Maps.newHashMap();
    this.wins = Maps.newHashMap();
    this.losses = Maps.newHashMap();
    this.playtime = Maps.newConcurrentMap();
  }

  public Map<UUID, AtomicInteger> getKills() {
    return kills;
  }

  public int getKills(UUID uuid) {
    return kills.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }

  public Map<UUID, AtomicInteger> getDeaths() {
    return deaths;
  }

  public int getDeaths(UUID uuid) {
    return deaths.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }

  public Map<UUID, AtomicInteger> getWools() {
    return wools;
  }

  public int getWools(UUID uuid) {
    return wools.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }

  public Map<UUID, AtomicInteger> getMonuments() {
    return monuments;
  }

  public int getMonuments(UUID uuid) {
    return monuments.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }

  public Map<UUID, AtomicInteger> getCores() {
    return cores;
  }

  public int getCores(UUID uuid) {
    return cores.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }

  public Map<UUID, AtomicInteger> getFlags() {
    return flags;
  }

  public int getFlags(UUID uuid) {
    return flags.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }

  public Map<UUID, AtomicInteger> getPoints() {
    return points;
  }

  public int getPoints(UUID uuid) {
    return points.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }

  public Map<UUID, AtomicInteger> getWins() {
    return wins;
  }

  public int getWins(UUID uuid) {
    return wins.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }

  public Map<UUID, AtomicInteger> getLosses() {
    return losses;
  }

  public int getLosses(UUID uuid) {
    return losses.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }

  public Map<UUID, AtomicInteger> getPlaytime() {
    return playtime;
  }

  public int getPlaytime(UUID uuid) {
    return playtime.getOrDefault(uuid, new AtomicInteger(0)).intValue();
  }
}
