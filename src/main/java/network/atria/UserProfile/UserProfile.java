package network.atria.UserProfile;

import java.util.Objects;
import java.util.UUID;
import network.atria.Effects.Particles.Effect;
import network.atria.Ranks.Rank;

public class UserProfile {

  private final String name;
  private final UUID uuid;

  private Rank rank;
  private Effect killeffect;
  private Effect projectile;
  private Effect killsound;
  private Integer points;

  public UserProfile(
      String name,
      UUID uuid,
      Rank rank,
      Effect killeffect,
      Effect projectile,
      Effect killsound,
      Integer points) {
    this.name = name;
    this.uuid = uuid;
    this.rank = rank;

    this.killeffect = killeffect;
    this.projectile = projectile;
    this.killsound = killsound;
    this.points = points;
  }

  public String getName() {
    return name;
  }

  public UUID getUUID() {
    return uuid;
  }

  public Rank getRank() {
    return rank;
  }

  public void setRank(Rank rank) {
    this.rank = rank;
  }

  public Effect getKilleffect() {
    return killeffect;
  }

  public void setKilleffect(Effect killeffect) {
    this.killeffect = killeffect;
  }

  public Effect getProjectile() {
    return projectile;
  }

  public void setProjectile(Effect projectile) {
    this.projectile = projectile;
  }

  public Effect getKillsound() {
    return killsound;
  }

  public void setKillsound(Effect killsound) {
    this.killsound = killsound;
  }

  public Integer getPoints() {
    return points;
  }

  public void setPoints(Integer points) {
    this.points = points;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserProfile profile = (UserProfile) o;
    return uuid.equals(profile.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "UserProfile{"
        + "name='"
        + name
        + '\''
        + ", uuid="
        + uuid
        + ", rank="
        + rank
        + ", killeffect="
        + killeffect
        + ", projectile="
        + projectile
        + ", killsound="
        + killsound
        + ", points="
        + points
        + '}';
  }

  public static UserProfile of(
      String name,
      UUID uuid,
      Rank rank,
      Effect killeffect,
      Effect projectile,
      Effect killsound,
      Integer points) {
    return new UserProfile(name, uuid, rank, killeffect, projectile, killsound, points);
  }
}
