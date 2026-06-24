package dev.hoard.plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;
public class HoardGUI {
    public static final String MAIN_TITLE = "§8Your Hoard";
    public static final String JOB_TITLE_PREFIX = "§8Hoard: ";
    public static final String TOP_TITLE_PREFIX = "§8Top: ";
    public static NamespacedKey GUI_KEY;
    public static final int[] MAIN_SLOTS = {11, 12, 13, 14, 15, 22};

    public static void init() {
        GUI_KEY = new NamespacedKey(HoardPlugin.getInstance(), "hoard_gui");
    }

    public static Inventory buildMain(Player player) {
        Inventory inv = Bukkit.createInventory(new HoardInventoryHolder("main", null, null), 27, MAIN_TITLE);
        HoardJob[] jobs = HoardJob.values();
        for (int i = 0; i < jobs.length; i++) {
            HoardJob job = jobs[i];
            ItemStack item = new ItemStack(job.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(job.getColor() + "" + ChatColor.BOLD + job.getDisplayName());
                meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, i);
                HoardData data = HoardPlugin.getInstance().getHoardData();
                long total = 0;
                for (Material m : job.getItems()) total += data.getCount(player.getUniqueId(), m);
                String totalLabel = job == HoardJob.HUNTER ? "Total Killed" : job == HoardJob.FISHERMAN ? "Total Fished" : job == HoardJob.MINER ? "Total Mined" : job == HoardJob.FARMER ? "Total Farmed" : "Total Broken";
                java.util.List<String> lore = new java.util.ArrayList<>();
                lore.add(ChatColor.GRAY + totalLabel + ": " + job.getColor() + ChatColor.BOLD + String.format("%,d", total));
                lore.add("");
                lore.add(ChatColor.GRAY + "Click to view collection");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(MAIN_SLOTS[i], item);
        }
        fillBorders(inv);
        return inv;
    }

    public static Inventory buildJob(Player player, HoardJob job) {
        Inventory inv = Bukkit.createInventory(new HoardInventoryHolder("job", job, null), 54, JOB_TITLE_PREFIX + job.getColor() + job.getDisplayName());
        HoardData data = HoardPlugin.getInstance().getHoardData();
        java.util.List<Material> items = job.getItems();
        for (int i = 0; i < items.size() && i < 45; i++) {
            Material mat = items.get(i);
            long count = data.getCount(player.getUniqueId(), mat);
            ItemStack display = new ItemStack(toDisplayMaterial(mat));
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                String name = job == HoardJob.HUNTER ? HoardJob.mobKeyName(mat) : formatName(mat.name());
                meta.setDisplayName(ChatColor.YELLOW + name);
                meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, i);
                java.util.List<String> lore = new java.util.ArrayList<>();
                String label = job == HoardJob.HUNTER ? "Killed" : job == HoardJob.FISHERMAN ? "Fished" : job == HoardJob.MINER ? "Mined" : job == HoardJob.FARMER ? "Farmed" : mat == Material.CAVE_VINES_PLANT ? "Collected" : "Broken";
                lore.add(ChatColor.GRAY + label + ": " + ChatColor.WHITE + String.format("%,d", count));
                List<Map.Entry<UUID, Long>> top = HoardPlugin.getInstance().getHoardData().getTop10(mat);
                int rank = -1;
                for (int r = 0; r < top.size(); r++) {
                    if (top.get(r).getKey().equals(player.getUniqueId())) { rank = r + 1; break; }
                }
                if (rank > 0) {
                    String rankColor = rank == 1 ? "§6" : rank == 2 ? "§7" : rank == 3 ? "§c" : "§f";
                    lore.add(ChatColor.GRAY + "Rank: " + rankColor + "#" + rank);
                } else if (count > 0) {
                    lore.add(ChatColor.GRAY + "Rank: §f#" + (top.size() + 1) + "+");
                } else {
                    lore.add(ChatColor.GRAY + "Rank: §7Unranked");
                }
                lore.add("");
                lore.add(ChatColor.DARK_GRAY + "Click to view leaderboard");
                meta.setLore(lore);
                display.setItemMeta(meta);
            }
            inv.setItem(i, display);
        }
        inv.setItem(49, makeBack());
        fillBorders(inv);
        return inv;
    }

    public static Inventory buildTop(Material mat, HoardJob job) {
        String name = job == HoardJob.HUNTER ? HoardJob.mobKeyName(mat) : formatName(mat.name());
        Inventory inv = Bukkit.createInventory(new HoardInventoryHolder("top", job, mat), 54, TOP_TITLE_PREFIX + name);
        HoardData data = HoardPlugin.getInstance().getHoardData();
        java.util.List<java.util.Map.Entry<java.util.UUID, Long>> top = data.getTop10(mat);
        String[] medals = {"§6#1", "§7#2", "§c#3", "§f#4", "§f#5", "§f#6", "§f#7", "§f#8", "§f#9", "§f#10"};
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 21, 22, 23};
        for (int i = 0; i < top.size(); i++) {
            java.util.Map.Entry<java.util.UUID, Long> entry = top.get(i);
            String pName = data.getPlayerName(entry.getKey());
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                try {
                    com.destroystokyo.paper.profile.PlayerProfile profile =
                        Bukkit.createProfile(entry.getKey(), pName);
                    profile.complete(true); // fetch skin from Mojang
                    meta.setPlayerProfile(profile);
                } catch (Exception ignored) {
                    try { meta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getKey())); } catch (Exception ignored2) {}
                }
                meta.setDisplayName(medals[i] + " " + ChatColor.WHITE + pName);
                meta.setItemName(pName); // override "Dynamic"
                meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -1);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                java.util.List<String> lore = new java.util.ArrayList<>();
                String label = job == HoardJob.HUNTER ? "Kills" : job == HoardJob.FISHERMAN ? "Fished" : "Broken";
                lore.add(ChatColor.GRAY + label + ": " + ChatColor.WHITE + String.format("%,d", entry.getValue()));
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            inv.setItem(slots[i], head);
        }
        if (top.isEmpty()) {
            ItemStack empty = new ItemStack(Material.BARRIER);
            ItemMeta meta = empty.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RED + "No data yet!");
                meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -1);
                empty.setItemMeta(meta);
            }
            inv.setItem(22, empty);
        }
        inv.setItem(49, makeBack());
        fillBorders(inv);
        return inv;
    }

    private static ItemStack makeBack() {
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta meta = back.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "← Back");
            meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -2);
            back.setItemMeta(meta);
        }
        return back;
    }

    public static int getGuiTag(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Integer.MIN_VALUE;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Integer.MIN_VALUE;
        Integer val = meta.getPersistentDataContainer().get(GUI_KEY, PersistentDataType.INTEGER);
        return val != null ? val : Integer.MIN_VALUE;
    }

    private static void fillBorders(Inventory inv) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -99);
            filler.setItemMeta(meta);
        }
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }
    }


    private static Material toDisplayMaterial(Material mat) {
        return switch (mat) {
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT;
            case COCOA -> Material.COCOA_BEANS;
            case KELP_PLANT -> Material.KELP;
            case CAVE_VINES_PLANT -> Material.GLOW_BERRIES;
            case SWEET_BERRY_BUSH -> Material.SWEET_BERRIES;
            case CHORUS_PLANT -> Material.CHORUS_FRUIT;
            case MELON -> Material.MELON_SLICE;
            case BAMBOO -> Material.BAMBOO;
            case SUGAR_CANE -> Material.SUGAR_CANE;
            case CACTUS -> Material.CACTUS;
            case PUMPKIN -> Material.PUMPKIN;
            case NETHER_WART -> Material.NETHER_WART;
            default -> mat;
        };
    }

    public static String formatName(String name) {
        if (name == null || name.isEmpty()) return "";
        String[] words = name.toLowerCase().replace("_", " ").split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
