package dev.hoard.plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.UUID;
public class HoardWipeCommand implements CommandExecutor {
    private final HoardPlugin plugin;
    public HoardWipeCommand(HoardPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hoard.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /hoardwipe <player>");
            return true;
        }
        String targetName = args[0];
        // Find UUID by name
        UUID targetUUID = null;
        for (UUID uuid : plugin.getHoardData().getAllPlayerIds()) {
            if (plugin.getHoardData().getPlayerName(uuid).equalsIgnoreCase(targetName)) {
                targetUUID = uuid;
                break;
            }
        }
        // Also check online players
        if (targetUUID == null && Bukkit.getPlayerExact(targetName) != null) {
            targetUUID = Bukkit.getPlayerExact(targetName).getUniqueId();
        }
        if (targetUUID == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + targetName + "' not found in hoard data!");
            return true;
        }
        plugin.getHoardData().wipePlayer(targetUUID);
        sender.sendMessage(ChatColor.GREEN + "✔ Wiped all hoard data for " + ChatColor.YELLOW + targetName + ChatColor.GREEN + "!");
        return true;
    }
}
