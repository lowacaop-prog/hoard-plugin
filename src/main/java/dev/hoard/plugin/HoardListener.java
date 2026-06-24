package dev.hoard.plugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import java.util.*;
public class HoardListener implements Listener {
    private final HoardPlugin plugin;
    private final Set<UUID> inMainGUI = new HashSet<>();
    private final Map<UUID, HoardJob> inJobGUI = new HashMap<>();
    private final Map<UUID, Material> inTopGUI = new HashMap<>();

    // Track blocks placed by players so we don't count them when broken
    private final Set<org.bukkit.Location> playerPlacedBlocks = new HashSet<>();

    public HoardListener(HoardPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getHoardData().setPlayerName(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
        playerPlacedBlocks.add(event.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material mat = event.getBlock().getType();
        HoardJob job = HoardJob.jobFor(mat);
        if (job == null || job == HoardJob.HUNTER || job == HoardJob.FISHERMAN) return;
        // Planted crops are always placed by players so exempt them from anti-cheat
        boolean isPlantedCrop = mat == Material.WHEAT || mat == Material.CARROTS ||
            mat == Material.POTATOES || mat == Material.BEETROOTS ||
            mat == Material.NETHER_WART || mat == Material.COCOA;
        if (!isPlantedCrop && playerPlacedBlocks.remove(event.getBlock().getLocation())) return;
        if (isPlantedCrop) {
            playerPlacedBlocks.remove(event.getBlock().getLocation());
            // Only count fully grown crops
            org.bukkit.block.data.BlockData data = event.getBlock().getBlockData();
            if (data instanceof org.bukkit.block.data.Ageable ageable) {
                if (ageable.getAge() < ageable.getMaximumAge()) return;
            }
        }
        Material key = HoardJob.displayItem(mat);
        plugin.getHoardData().addCount(player.getUniqueId(), key, 1);
        long total = plugin.getHoardData().getCount(player.getUniqueId(), key);
        if (total % 100 == 0) plugin.getHoardData().save(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        Material key = HoardJob.mobToKey(event.getEntity().getType());
        if (key == null) return;
        plugin.getHoardData().addCount(killer.getUniqueId(), key, 1);
        plugin.getHoardData().save(killer.getUniqueId());
    }

    // Track glow berries picked by right-clicking cave vines
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.CAVE_VINES_PLANT &&
            event.getClickedBlock().getType() != Material.CAVE_VINES) return;
        org.bukkit.block.data.BlockData blockData = event.getClickedBlock().getBlockData();
        boolean hasBerries = false;
        if (blockData instanceof org.bukkit.block.data.type.CaveVines vines) {
            hasBerries = vines.isBerries();
        } else if (blockData instanceof org.bukkit.block.data.type.CaveVinesPlant plant) {
            hasBerries = plant.isBerries();
        }
        if (!hasBerries) return;
        plugin.getHoardData().addCount(event.getPlayer().getUniqueId(), Material.CAVE_VINES_PLANT, 1);
        plugin.getHoardData().save(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof org.bukkit.entity.Item caughtItem)) return;
        Player player = event.getPlayer();
        Material mat = caughtItem.getItemStack().getType();
        if (HoardJob.FISHERMAN.getItems().contains(mat)) {
            plugin.getHoardData().addCount(player.getUniqueId(), mat, 1);
            plugin.getHoardData().save(player.getUniqueId());
        }
    }

    public void openMain(Player player) {
        clearGUIState(player.getUniqueId());
        inMainGUI.add(player.getUniqueId());
        player.openInventory(HoardGUI.buildMain(player));
    }

    public void openJob(Player player, HoardJob job) {
        clearGUIState(player.getUniqueId());
        inJobGUI.put(player.getUniqueId(), job);
        player.openInventory(HoardGUI.buildJob(player, job));
    }

    public void openTop(Player player, Material mat, HoardJob job) {
        clearGUIState(player.getUniqueId());
        inTopGUI.put(player.getUniqueId(), mat);
        player.openInventory(HoardGUI.buildTop(mat, job));
    }

    private void clearGUIState(UUID uuid) {
        inMainGUI.remove(uuid);
        inJobGUI.remove(uuid);
        inTopGUI.remove(uuid);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        clearGUIState(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof HoardInventoryHolder)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGUIClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        // Check if top inventory has our holder
        if (!(event.getView().getTopInventory().getHolder() instanceof HoardInventoryHolder holder)) return;

        // Cancel EVERYTHING no matter what
        event.setCancelled(true);

        // Only handle left click on top inventory
        if (event.getClick() != ClickType.LEFT) return;
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int tag = HoardGUI.getGuiTag(clicked);
        if (tag == Integer.MIN_VALUE) return;
        if (tag == -99) return;

        switch (holder.getType()) {
            case "main" -> {
                if (tag >= 0 && tag < HoardJob.values().length) {
                    final HoardJob job = HoardJob.values()[tag];
                    plugin.getServer().getScheduler().runTask(plugin, () -> openJob(player, job));
                }
            }
            case "job" -> {
                HoardJob job = holder.getJob();
                if (tag == -2) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> openMain(player));
                } else if (tag >= 0 && tag < job.getItems().size()) {
                    Material mat = job.getItems().get(tag);
                    plugin.getServer().getScheduler().runTask(plugin, () -> openTop(player, mat, job));
                }
            }
            case "top" -> {
                if (tag == -2) {
                    HoardJob job = HoardJob.jobFor(holder.getMat());
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (job != null) openJob(player, job);
                        else openMain(player);
                    });
                }
            }
        }
    }
}
