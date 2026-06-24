package dev.hoard.plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
public class HoardPlugin extends JavaPlugin {
    private static HoardPlugin instance;
    private HoardData hoardData;
    private HoardListener hoardListener;
    @Override
    public void onEnable() {
        instance = this;
        hoardData = new HoardData(this);
        HoardGUI.init();
        hoardListener = new HoardListener(this);
        getServer().getPluginManager().registerEvents(hoardListener, this);
        getCommand("hoard").setExecutor(new HoardCommand(this));
        // Auto-save every 5 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                for (org.bukkit.entity.Player p : getServer().getOnlinePlayers()) {
                    hoardData.save(p.getUniqueId());
                }
            }
        }.runTaskTimer(this, 6000L, 6000L);
        getLogger().info("Hoard enabled!");
    }
    @Override
    public void onDisable() {
        for (org.bukkit.entity.Player p : getServer().getOnlinePlayers()) {
            hoardData.save(p.getUniqueId());
        }
        getLogger().info("Hoard disabled.");
    }
    public static HoardPlugin getInstance() { return instance; }
    public HoardData getHoardData() { return hoardData; }
    public HoardListener getHoardListener() { return hoardListener; }
}
