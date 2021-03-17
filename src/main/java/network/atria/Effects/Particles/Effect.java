package network.atria.Effects.Particles;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Effect {

  private final String Name;
  private final ItemStack icon;
  private final int slot;
  private final int point;
  private final boolean donor;
  private final boolean donor_only;

  public Effect(
      String Name, ItemStack icon, int slot, int point, boolean donor, boolean donor_only) {
    this.Name = Name;
    this.icon = icon;
    this.slot = slot;
    this.point = point;
    this.donor = donor;
    this.donor_only = donor_only;
  }

  public String getName() {
    return this.Name;
  }

  public String getUncoloredName() {
    return ChatColor.stripColor(this.Name);
  }

  public ItemStack getIcon() {
    return icon;
  }

  public Integer getSlot() {
    return slot;
  }

  public Integer getPoint() {
    return this.point;
  }

  public boolean canUseDonor() {
    return this.donor;
  }

  public boolean isDonorOnly(Player player) {
    return donor_only && player.hasPermission("pgm.group.donor");
  }

  public static Effect of(
      String Name, ItemStack icon, int slot, int point, boolean donor, boolean donor_only) {
    return new Effect(Name, icon, slot, point, donor, donor_only);
  }
}
