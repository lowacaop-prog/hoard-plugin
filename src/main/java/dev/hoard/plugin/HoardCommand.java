package dev.hoard.plugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class HoardCommand implements CommandExecutor {
    private final HoardPlugin plugin;
    public HoardCommand(HoardPlugin plugin) { this.plugin = plugin; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(ChatColor.RED + "Only players can use this."); return true; }
        if (!player.hasPermission("hoard.use")) { player.sendMessage(ChatColor.RED + "No permission."); return true; }
        plugin.getHoardListener().openMain(player);
        return true;
    }
}
