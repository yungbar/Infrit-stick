package minebird.bardak;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class IfritStickPlugin extends JavaPlugin implements Listener, CommandExecutor {

    private Map<Player, Boolean> canShootMap = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("Ifrit Stick Plugin has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("stick").setExecutor(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Ifrit Stick Plugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("stick") && sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("ifritstick.use")) {
                giveIfritStick(player);
                player.sendMessage("You received an Ifrit Stick!");
            } else {
                player.sendMessage("You don't have permission to use this command.");
            }

            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item != null && item.getType() == Material.STICK && item.hasItemMeta() &&
                item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals("Ifrit Stick")) {
            // Проверка, что это Ifrit Stick
            event.setCancelled(true); // Отмена обычного воздействия палкой

            if (canShoot(player)) {
                shootIfrit(player);
                canShootMap.put(player, false);

                // Задержка перед удалением палки из инвентаря (в данном случае 20 тиков = 1 секунда)
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getInventory().removeItem(item);
                    }
                }.runTaskLater(this, 20L);
            }
        }
    }

    private void giveIfritStick(Player player) {
        ItemStack ifritStick = new ItemStack(Material.STICK);
        ifritStick.setAmount(1);
        ifritStick.getItemMeta().setDisplayName("Ifrit Stick");
        player.getInventory().addItem(ifritStick);
        canShootMap.put(player, true);
    }

    private boolean canShoot(Player player) {
        return canShootMap.getOrDefault(player, false);
    }

    private void shootIfrit(Player player) {
        // Создание вектора для направления выстрела
        Vector direction = player.getLocation().getDirection().multiply(2);

        // Создание "огненного шара" (можно заменить на другой эффект)

        player.getWorld().spawnFallingBlock(player.getLocation(), Material.FIRE, (byte) 0);
        player.getWorld().spawnFallingBlock(player.getLocation().add(direction), Material.FIRE, (byte) 0);
    }
}

