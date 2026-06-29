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
    public static NamespacedKey MAT_KEY;
    public static final int[] MAIN_SLOTS = {11, 12, 13, 14, 15, 22};

    public static void init() {
        GUI_KEY = new NamespacedKey(HoardPlugin.getInstance(), "hoard_gui");
        MAT_KEY = new NamespacedKey(HoardPlugin.getInstance(), "hoard_mat");
    }

    // Job colors as glass panes
    private static Material getJobGlass(HoardJob job) {
        return switch (job) {
            case MINER -> Material.GRAY_STAINED_GLASS_PANE;
            case DIGGER -> Material.ORANGE_STAINED_GLASS_PANE;
            case WOODCUTTER -> Material.GREEN_STAINED_GLASS_PANE;
            case HUNTER -> Material.RED_STAINED_GLASS_PANE;
            case FISHERMAN -> Material.CYAN_STAINED_GLASS_PANE;
            case FARMER -> Material.YELLOW_STAINED_GLASS_PANE;
        };
    }

    // Group separator glass
    private static Material getGroupGlass(HoardJob job) {
        return switch (job) {
            case MINER -> Material.LIGHT_GRAY_STAINED_GLASS_PANE;
            case DIGGER -> Material.BROWN_STAINED_GLASS_PANE;
            case WOODCUTTER -> Material.LIME_STAINED_GLASS_PANE;
            case HUNTER -> Material.PINK_STAINED_GLASS_PANE;
            case FISHERMAN -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            case FARMER -> Material.LIME_STAINED_GLASS_PANE;
        };
    }

    // Groups per job — list of (groupName, materials)
    private static List<GroupData> getGroups(HoardJob job) {
        List<GroupData> groups = new ArrayList<>();
        switch (job) {
            case MINER -> {
                groups.add(new GroupData("§f✦ Common Ores", new Material[]{
                    Material.COAL_ORE, Material.IRON_ORE, Material.COPPER_ORE,
                    Material.GOLD_ORE, Material.REDSTONE_ORE, Material.LAPIS_ORE
                }));
                groups.add(new GroupData("§f✦ Rare Ores", new Material[]{
                    Material.DIAMOND_ORE, Material.EMERALD_ORE,
                    Material.NETHER_QUARTZ_ORE, Material.ANCIENT_DEBRIS
                }));
                groups.add(new GroupData("§f✦ Stone & Deepslate", new Material[]{
                    Material.STONE, Material.GRANITE, Material.DIORITE,
                    Material.ANDESITE, Material.DEEPSLATE, Material.OBSIDIAN,
                    Material.NETHERRACK
                }));
                groups.add(new GroupData("§f✦ Ice", new Material[]{
                    Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE
                }));
            }
            case DIGGER -> {
                groups.add(new GroupData("§a✦ Dirt & Grass", new Material[]{
                    Material.DIRT, Material.GRASS_BLOCK, Material.COARSE_DIRT,
                    Material.ROOTED_DIRT, Material.PODZOL, Material.MYCELIUM
                }));
                groups.add(new GroupData("§e✦ Sand & Gravel", new Material[]{
                    Material.SAND, Material.RED_SAND, Material.GRAVEL
                }));
                groups.add(new GroupData("§9✦ Clay & Mud", new Material[]{
                    Material.CLAY, Material.MUD, Material.SOUL_SAND, Material.SOUL_SOIL
                }));
                groups.add(new GroupData("§f✦ Snow", new Material[]{
                    Material.SNOW_BLOCK
                }));
            }
            case WOODCUTTER -> {
                groups.add(new GroupData("§a✦ Overworld Logs", new Material[]{
                    Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
                    Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG
                }));
                groups.add(new GroupData("§d✦ Exotic Logs", new Material[]{
                    Material.MANGROVE_LOG, Material.CHERRY_LOG
                }));
            }
            case HUNTER -> {
                groups.add(new GroupData("§c✦ Overworld Hostile", new Material[]{
                    Material.ROTTEN_FLESH, Material.BONE, Material.GUNPOWDER,
                    Material.STRING, Material.SPIDER_EYE, Material.ENDER_PEARL,
                    Material.PHANTOM_MEMBRANE, Material.SLIME_BALL
                }));
                groups.add(new GroupData("§6✦ Nether", new Material[]{
                    Material.BLAZE_ROD, Material.WITHER_SKELETON_SKULL, Material.GOLD_NUGGET,
                    Material.GHAST_TEAR, Material.MAGMA_CREAM
                }));
                groups.add(new GroupData("§a✦ Passive & Ocean", new Material[]{
                    Material.FEATHER, Material.LEATHER, Material.PORKCHOP,
                    Material.MUTTON, Material.TURTLE_SCUTE, Material.RABBIT_FOOT,
                    Material.INK_SAC, Material.GLOW_INK_SAC, Material.SHULKER_SHELL
                }));
                groups.add(new GroupData("§4✦ Boss", new Material[]{
                    Material.IRON_NUGGET, Material.NETHER_STAR
                }));
            }
            case FISHERMAN -> {
                groups.add(new GroupData("§b✦ Fish", new Material[]{
                    Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH
                }));
                groups.add(new GroupData("§6✦ Treasure", new Material[]{
                    Material.NAUTILUS_SHELL, Material.HEART_OF_THE_SEA, Material.SADDLE
                }));
            }
            case FARMER -> {
                groups.add(new GroupData("§e✦ Crops", new Material[]{
                    Material.WHEAT, Material.CARROTS, Material.POTATOES,
                    Material.BEETROOTS, Material.NETHER_WART
                }));
                groups.add(new GroupData("§a✦ Plants", new Material[]{
                    Material.SUGAR_CANE, Material.CACTUS, Material.BAMBOO,
                    Material.PUMPKIN, Material.MELON
                }));
                groups.add(new GroupData("§5✦ Fungi", new Material[]{
                    Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
                    Material.COCOA, Material.CHORUS_PLANT
                }));
                groups.add(new GroupData("§2✦ Vines", new Material[]{
                    Material.KELP_PLANT, Material.SWEET_BERRY_BUSH, Material.CAVE_VINES_PLANT
                }));
            }
        }
        return groups;
    }

    public static Inventory buildMain(Player player) {
        Inventory inv = Bukkit.createInventory(new HoardInventoryHolder("main", null, null), 27, MAIN_TITLE);
        HoardJob[] jobs = HoardJob.values();
        HoardData data = HoardPlugin.getInstance().getHoardData();
        for (int i = 0; i < jobs.length; i++) {
            HoardJob job = jobs[i];
            ItemStack item = new ItemStack(job.getIcon());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(job.getColor() + "" + ChatColor.BOLD + job.getDisplayName());
            meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, i);
            long total = 0;
            for (Material mat : job.getItems()) total += data.getCount(player.getUniqueId(), mat);
            String totalLabel = job == HoardJob.HUNTER ? "Total Killed" : job == HoardJob.FISHERMAN ? "Total Fished" : job == HoardJob.MINER ? "Total Mined" : job == HoardJob.FARMER ? "Total Farmed" : "Total Broken";
            // Calculate rank
            Map<UUID, Long> jobTotals = new HashMap<>();
            for (UUID pid : HoardPlugin.getInstance().getHoardData().getAllPlayerIds()) {
                long t = 0;
                for (Material m : job.getItems()) t += HoardPlugin.getInstance().getHoardData().getCount(pid, m);
                if (t > 0) jobTotals.put(pid, t);
            }
            List<Map.Entry<UUID, Long>> sorted = new ArrayList<>(jobTotals.entrySet());
            sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
            int jobRank = -1;
            for (int r = 0; r < sorted.size(); r++) {
                if (sorted.get(r).getKey().equals(player.getUniqueId())) { jobRank = r + 1; break; }
            }
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + totalLabel + ": " + job.getColor() + ChatColor.BOLD + String.format("%,d", total));
            if (jobRank > 0) {
                String rc = jobRank == 1 ? "§6" : jobRank == 2 ? "§7" : jobRank == 3 ? "§c" : "§f";
                lore.add(ChatColor.GRAY + "Rank: " + rc + "#" + jobRank);
            } else if (total > 0) {
                lore.add(ChatColor.GRAY + "Rank: §f#" + (sorted.size() + 1) + "+");
            } else {
                lore.add(ChatColor.GRAY + "Rank: §7Unranked");
            }
            lore.add("");
            lore.add(ChatColor.GRAY + "Click to view collection");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(MAIN_SLOTS[i], item);
        }
        fillBorders(inv, Material.PURPLE_STAINED_GLASS_PANE);
        return inv;
    }

    public static Inventory buildJob(Player player, HoardJob job) {
        List<GroupData> groups = getGroups(job);
        HoardData data = HoardPlugin.getInstance().getHoardData();
        Material jobGlass = getJobGlass(job);
        Material groupGlass = getGroupGlass(job);
        String label = job == HoardJob.HUNTER ? "Killed" : job == HoardJob.FISHERMAN ? "Fished"
            : job == HoardJob.MINER ? "Mined" : job == HoardJob.FARMER ? "Farmed" : "Broken";

        // Each group gets its own row
        // Row layout: [border][separator][item][item]...[border]
        // Size = 2 (top+bottom border) + groups.size() rows, max 6 rows
        int numRows = groups.size() + 2;
        // Round up to valid inventory size (multiple of 9, max 54)
        int size = Math.min(54, numRows * 9);

        Inventory inv = Bukkit.createInventory(new HoardInventoryHolder("job", job, null), size,
            JOB_TITLE_PREFIX + job.getColor() + job.getDisplayName());

        int itemIndex = 0;
        for (int g = 0; g < groups.size() && (g + 2) * 9 < size; g++) {
            GroupData group = groups.get(g);
            int rowStart = (g + 1) * 9; // skip top border row

            // Separator in col 0
            ItemStack sep = new ItemStack(groupGlass);
            ItemMeta sepMeta = sep.getItemMeta();
            sepMeta.setDisplayName(group.name);
            sepMeta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -99);
            sep.setItemMeta(sepMeta);
            inv.setItem(rowStart, sep);

            // Items in cols 1-7
            for (int i = 0; i < group.items.length && i < 7; i++) {
                Material mat = group.items[i];
                long count = data.getCount(player.getUniqueId(), mat);
                ItemStack display = new ItemStack(toDisplayMaterial(mat));
                ItemMeta meta = display.getItemMeta();
                String name = job == HoardJob.HUNTER ? HoardJob.mobKeyName(mat) : formatName(mat.name());
                meta.setDisplayName(job.getColor() + name);
                meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, itemIndex);
                meta.getPersistentDataContainer().set(MAT_KEY, PersistentDataType.STRING, mat.name());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + label + ": " + ChatColor.WHITE + String.format("%,d", count));
                List<Map.Entry<UUID, Long>> top = HoardPlugin.getInstance().getHoardData().getTop10(mat);
                int rank = -1;
                for (int r = 0; r < top.size(); r++) {
                    if (top.get(r).getKey().equals(player.getUniqueId())) { rank = r + 1; break; }
                }
                if (rank > 0) {
                    String rc = rank == 1 ? "§6" : rank == 2 ? "§7" : rank == 3 ? "§c" : "§f";
                    lore.add(ChatColor.GRAY + "Rank: " + rc + "#" + rank);
                } else if (count > 0) {
                    lore.add(ChatColor.GRAY + "Rank: §f#" + (top.size() + 1) + "+");
                } else {
                    lore.add(ChatColor.GRAY + "Rank: §7Unranked");
                }
                lore.add("");
                lore.add(ChatColor.DARK_GRAY + "Click to view leaderboard");
                meta.setLore(lore);
                display.setItemMeta(meta);
                inv.setItem(rowStart + 1 + i, display);
                itemIndex++;
            }

            // Fill remaining cols with job glass
            int itemsPlaced = Math.min(group.items.length, 7);
            for (int i = itemsPlaced; i < 7; i++) {
                ItemStack filler = new ItemStack(jobGlass);
                ItemMeta fm = filler.getItemMeta();
                fm.setDisplayName(" ");
                fm.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -99);
                filler.setItemMeta(fm);
                inv.setItem(rowStart + 1 + i, filler);
            }
            // Right border
            ItemStack rb = new ItemStack(jobGlass);
            ItemMeta rbm = rb.getItemMeta();
            rbm.setDisplayName(" ");
            rbm.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -99);
            rb.setItemMeta(rbm);
            inv.setItem(rowStart + 8, rb);
        }

        // Top and bottom borders
        fillBorders(inv, jobGlass);
        inv.setItem(size - 5, makeBack(jobGlass));
        return inv;
    }

    public static Inventory buildTop(Material mat, HoardJob job) {
        String name = job == HoardJob.HUNTER ? HoardJob.mobKeyName(mat) : formatName(mat.name());
        Inventory inv = Bukkit.createInventory(new HoardInventoryHolder("top", job, mat), 54, TOP_TITLE_PREFIX + name);
        HoardData data = HoardPlugin.getInstance().getHoardData();
        List<Map.Entry<UUID, Long>> top = data.getTop10(mat);
        String[] medals = {"§6#1", "§7#2", "§c#3", "§f#4", "§f#5", "§f#6", "§f#7", "§f#8", "§f#9", "§f#10"};
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 21, 22, 23};
        Material jobGlass = getJobGlass(job);
        for (int i = 0; i < top.size(); i++) {
            Map.Entry<UUID, Long> entry = top.get(i);
            String pName = data.getPlayerName(entry.getKey());
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            try {
                com.destroystokyo.paper.profile.PlayerProfile profile = Bukkit.createProfile(entry.getKey(), pName);
                profile.complete(true);
                meta.setPlayerProfile(profile);
            } catch (Exception ignored) {}
            meta.setDisplayName(medals[i] + " " + ChatColor.WHITE + pName);
            meta.setItemName(pName);
            meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -1);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
            List<String> lore = new ArrayList<>();
            String label = job == HoardJob.HUNTER ? "Kills" : job == HoardJob.FISHERMAN ? "Fished" : job == HoardJob.MINER ? "Mined" : job == HoardJob.FARMER ? "Farmed" : "Broken";
            lore.add(ChatColor.GRAY + label + ": " + ChatColor.WHITE + String.format("%,d", entry.getValue()));
            meta.setLore(lore);
            head.setItemMeta(meta);
            inv.setItem(slots[i], head);
        }
        if (top.isEmpty()) {
            ItemStack empty = new ItemStack(Material.BARRIER);
            ItemMeta meta = empty.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "No data yet!");
            meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -1);
            empty.setItemMeta(meta);
            inv.setItem(22, empty);
        }
        inv.setItem(49, makeBack(jobGlass));
        fillBorders(inv, jobGlass);
        return inv;
    }

    private static ItemStack makeBack(Material glass) {
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "← Back");
        meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -2);
        back.setItemMeta(meta);
        return back;
    }

    private static void fillBorders(Inventory inv, Material glass) {
        ItemStack filler = new ItemStack(glass);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        meta.getPersistentDataContainer().set(GUI_KEY, PersistentDataType.INTEGER, -99);
        filler.setItemMeta(meta);
        int size = inv.getSize();
        for (int i = 0; i < size; i++) {
            if (inv.getItem(i) == null) {
                // Top and bottom rows
                if (i < 9 || i >= size - 9) { inv.setItem(i, filler); continue; }
                // Left and right columns
                if (i % 9 == 0 || i % 9 == 8) inv.setItem(i, filler);
            }
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
            case WHEAT -> Material.WHEAT;
            case NETHER_WART -> Material.NETHER_WART;
            default -> mat;
        };
    }

    public static String formatName(String name) {
        String[] words = name.toLowerCase().replace("_", " ").split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        return sb.toString().trim();
    }

    public static int getGuiTag(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Integer.MIN_VALUE;
        Integer val = item.getItemMeta().getPersistentDataContainer().get(GUI_KEY, PersistentDataType.INTEGER);
        return val != null ? val : Integer.MIN_VALUE;
    }

    static class GroupData {
        String name;
        Material[] items;
        GroupData(String name, Material[] items) { this.name = name; this.items = items; }
    }
}
