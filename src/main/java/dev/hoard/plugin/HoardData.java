package dev.hoard.plugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.*;
import java.util.*;
import java.util.Set;
public class HoardData {
    private final HoardPlugin plugin;
    private File dataFile;
    private FileConfiguration data;
    // playerUUID -> material -> count
    private final Map<UUID, Map<Material, Long>> counts = new HashMap<>();
    private final Map<UUID, String> playerNames = new HashMap<>();

    public HoardData(HoardPlugin plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "hoard.yml");
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        loadAll();
    }

    public long getCount(UUID uuid, Material mat) {
        return counts.computeIfAbsent(uuid, k -> new HashMap<>()).getOrDefault(mat, 0L);
    }

    public void addCount(UUID uuid, Material mat, long amount) {
        counts.computeIfAbsent(uuid, k -> new HashMap<>())
              .merge(mat, amount, Long::sum);
    }

    public void setPlayerName(UUID uuid, String name) { playerNames.put(uuid, name); }
    public String getPlayerName(UUID uuid) { return playerNames.getOrDefault(uuid, "Unknown"); }

    public List<Map.Entry<UUID, Long>> getTop10(Material mat) {
        List<Map.Entry<UUID, Long>> list = new ArrayList<>();
        for (Map.Entry<UUID, Map<Material, Long>> entry : counts.entrySet()) {
            long count = entry.getValue().getOrDefault(mat, 0L);
            if (count > 0) list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), count));
        }
        list.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        return list.subList(0, Math.min(10, list.size()));
    }

    public Set<UUID> getAllPlayerIds() {
        return counts.keySet();
    }

    public void save(UUID uuid) {
        String name = playerNames.get(uuid);
        if (name != null) data.set("names." + uuid, name);
        Map<Material, Long> playerCounts = counts.get(uuid);
        if (playerCounts != null) {
            for (Map.Entry<Material, Long> entry : playerCounts.entrySet()) {
                data.set("counts." + uuid + "." + entry.getKey().name(), entry.getValue());
            }
        }
        try { data.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadAll() {
        if (data.contains("names")) {
            for (String uuidStr : data.getConfigurationSection("names").getKeys(false)) {
                try { playerNames.put(UUID.fromString(uuidStr), data.getString("names." + uuidStr)); } catch (Exception ignored) {}
            }
        }
        if (data.contains("counts")) {
            for (String uuidStr : data.getConfigurationSection("counts").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    for (String matName : data.getConfigurationSection("counts." + uuidStr).getKeys(false)) {
                        try {
                            Material mat = Material.valueOf(matName);
                            long count = data.getLong("counts." + uuidStr + "." + matName);
                            if (count > 0) counts.computeIfAbsent(uuid, k -> new HashMap<>()).put(mat, count);
                        } catch (Exception ignored) {}
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}
