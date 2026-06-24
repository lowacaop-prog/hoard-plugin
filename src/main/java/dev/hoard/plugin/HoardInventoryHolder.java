package dev.hoard.plugin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
public class HoardInventoryHolder implements InventoryHolder {
    private final String type; // "main", "job", "top"
    private final HoardJob job;
    private final org.bukkit.Material mat;

    public HoardInventoryHolder(String type, HoardJob job, org.bukkit.Material mat) {
        this.type = type;
        this.job = job;
        this.mat = mat;
    }

    public String getType() { return type; }
    public HoardJob getJob() { return job; }
    public org.bukkit.Material getMat() { return mat; }

    @Override
    public Inventory getInventory() { return null; }
}
