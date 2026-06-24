package dev.hoard.plugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import java.util.*;
public enum HoardJob {
    MINER("Miner", ChatColor.GRAY, Material.DIAMOND_PICKAXE, new Material[]{
        Material.COAL_ORE,
        Material.IRON_ORE,
        Material.GOLD_ORE,
        Material.DIAMOND_ORE,
        Material.EMERALD_ORE,
        Material.LAPIS_ORE,
        Material.REDSTONE_ORE,
        Material.COPPER_ORE,
        Material.NETHER_QUARTZ_ORE, Material.ANCIENT_DEBRIS,
        Material.STONE, Material.DEEPSLATE, Material.GRANITE, Material.DIORITE,
        Material.ANDESITE, Material.OBSIDIAN, Material.NETHERRACK,
        Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE
    }),
    DIGGER("Digger", ChatColor.GOLD, Material.IRON_SHOVEL, new Material[]{
        Material.DIRT, Material.GRASS_BLOCK, Material.COARSE_DIRT,
        Material.ROOTED_DIRT, Material.PODZOL, Material.MYCELIUM,
        Material.SAND, Material.RED_SAND, Material.GRAVEL,
        Material.SOUL_SAND, Material.SOUL_SOIL, Material.CLAY,
        Material.MUD, Material.SNOW_BLOCK
    }),
    WOODCUTTER("Woodcutter", ChatColor.GREEN, Material.IRON_AXE, new Material[]{
        Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
        Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
        Material.MANGROVE_LOG, Material.CHERRY_LOG,
    }),
    HUNTER("Hunter", ChatColor.RED, Material.IRON_SWORD, new Material[]{
        Material.ROTTEN_FLESH, Material.GUNPOWDER,
        Material.STRING, Material.SPIDER_EYE, Material.ENDER_PEARL,
        Material.BLAZE_ROD, Material.WITHER_SKELETON_SKULL, Material.GOLD_NUGGET,
        Material.GHAST_TEAR, Material.MAGMA_CREAM, Material.SLIME_BALL,
        Material.PHANTOM_MEMBRANE, Material.RABBIT_FOOT, Material.FEATHER,
        Material.LEATHER, Material.PORKCHOP, Material.MUTTON,
        Material.TURTLE_SCUTE, Material.INK_SAC, Material.GLOW_INK_SAC,
        Material.SHULKER_SHELL, Material.IRON_NUGGET, Material.NETHER_STAR
    }),
    FISHERMAN("Fisherman", ChatColor.AQUA, Material.FISHING_ROD, new Material[]{
        Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH,
        Material.NAUTILUS_SHELL, Material.HEART_OF_THE_SEA,
        Material.SADDLE
    }),
    FARMER("Farmer", ChatColor.YELLOW, Material.IRON_HOE, new Material[]{
        Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS,
        Material.PUMPKIN, Material.MELON, Material.SUGAR_CANE,
        Material.CACTUS, Material.BAMBOO, Material.NETHER_WART,
        Material.COCOA, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
        Material.CHORUS_PLANT, Material.KELP_PLANT,
        Material.SWEET_BERRY_BUSH, Material.CAVE_VINES_PLANT
    });

    private final String displayName;
    private final ChatColor color;
    private final Material icon;
    private final List<Material> items;

    HoardJob(String displayName, ChatColor color, Material icon, Material[] items) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
        this.items = new ArrayList<>(Arrays.asList(items));
    }

    public String getDisplayName() { return displayName; }
    public ChatColor getColor() { return color; }
    public Material getIcon() { return icon; }
    public List<Material> getItems() { return items; }

    public static HoardJob jobFor(Material mat) {
        for (HoardJob job : values()) {
            if (job.getItems().contains(mat)) return job;
        }
        return null;
    }

    public static Material displayItem(Material broken) {
        return switch (broken) {
            case COAL_ORE, DEEPSLATE_COAL_ORE -> Material.COAL_ORE;
            case IRON_ORE, DEEPSLATE_IRON_ORE -> Material.IRON_ORE;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE, NETHER_GOLD_ORE -> Material.GOLD_ORE;
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> Material.DIAMOND_ORE;
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> Material.EMERALD_ORE;
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> Material.LAPIS_ORE;
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> Material.REDSTONE_ORE;
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.COPPER_ORE;
            case WHEAT -> Material.WHEAT;
            case CARROTS -> Material.CARROTS;
            case POTATOES -> Material.POTATOES;
            case BEETROOTS -> Material.BEETROOTS;
            case COCOA -> Material.COCOA;
            case KELP_PLANT -> Material.KELP_PLANT;
            case CAVE_VINES_PLANT -> Material.CAVE_VINES_PLANT;
            case CHORUS_PLANT -> Material.CHORUS_PLANT;
            case SWEET_BERRY_BUSH -> Material.SWEET_BERRY_BUSH;
            default -> broken;
        };
    }

    public static Material mobToKey(org.bukkit.entity.EntityType type) {
        return switch (type) {
            case ZOMBIE, ZOMBIE_VILLAGER, DROWNED, HUSK -> Material.ROTTEN_FLESH;
            case SKELETON, STRAY -> Material.BONE;
            case CREEPER -> Material.GUNPOWDER;
            case SPIDER -> Material.STRING;
            case CAVE_SPIDER -> Material.SPIDER_EYE;
            case ENDERMAN -> Material.ENDER_PEARL;
            case BLAZE -> Material.BLAZE_ROD;
            case WITHER_SKELETON -> Material.WITHER_SKELETON_SKULL;
            case PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN -> Material.GOLD_NUGGET;
            case GHAST -> Material.GHAST_TEAR;
            case MAGMA_CUBE -> Material.MAGMA_CREAM;
            case SLIME -> Material.SLIME_BALL;
            case PHANTOM -> Material.PHANTOM_MEMBRANE;
            case RABBIT -> Material.RABBIT_FOOT;
            case CHICKEN -> Material.FEATHER;
            case COW, MOOSHROOM, HORSE, DONKEY, MULE -> Material.LEATHER;
            case PIG -> Material.PORKCHOP;
            case SHEEP -> Material.MUTTON;
            case TURTLE -> Material.TURTLE_SCUTE;
            case SQUID -> Material.INK_SAC;
            case GLOW_SQUID -> Material.GLOW_INK_SAC;
            case SHULKER -> Material.SHULKER_SHELL;
            case IRON_GOLEM -> Material.IRON_NUGGET;
            case WITHER -> Material.NETHER_STAR;
            default -> null;
        };
    }

    public static String mobKeyName(Material key) {
        return switch (key) {
            case ROTTEN_FLESH -> "Zombie";
            case BONE -> "Skeleton";
            case GUNPOWDER -> "Creeper";
            case STRING -> "Spider";
            case SPIDER_EYE -> "Cave Spider";
            case ENDER_PEARL -> "Enderman";
            case BLAZE_ROD -> "Blaze";
            case WITHER_SKELETON_SKULL -> "Wither Skeleton";
            case GOLD_NUGGET -> "Piglin";
            case GHAST_TEAR -> "Ghast";
            case MAGMA_CREAM -> "Magma Cube";
            case SLIME_BALL -> "Slime";
            case PHANTOM_MEMBRANE -> "Phantom";
            case RABBIT_FOOT -> "Rabbit";
            case FEATHER -> "Chicken";
            case LEATHER -> "Cow";
            case PORKCHOP -> "Pig";
            case MUTTON -> "Sheep";
            case TURTLE_SCUTE -> "Turtle";
            case INK_SAC -> "Squid";
            case GLOW_INK_SAC -> "Glow Squid";
            case SHULKER_SHELL -> "Shulker";
            case IRON_NUGGET -> "Iron Golem";
            case NETHER_STAR -> "Wither";
            default -> HoardGUI.formatName(key.name());
        };
    }
}
