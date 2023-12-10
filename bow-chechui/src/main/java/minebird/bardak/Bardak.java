package minebird.bardak;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Bardak extends JavaPlugin implements Listener {

    private Map<UUID, Boolean> specialBowOwners = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("Bardak plugin has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("bow").setExecutor(this);
        getCommand("stick").setExecutor(this);
        IfritStickPlugin ifritStickPlugin = new IfritStickPlugin();
    }

    @Override
    public void onDisable() {
        getLogger().info("Bardak plugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bow")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1 && args[0].equalsIgnoreCase("chechui")) {
                    giveSpecialBow(player);
                    sender.sendMessage(ChatColor.GREEN + "You have received the special bow!");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /bow chechui");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
                return true;
            }
        }
        if (cmd.getName().equalsIgnoreCase("bow")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadPlugin(sender);
                return true;
            }
        }
        return false;
    }
    private void reloadPlugin(CommandSender sender) {
        reloadConfig(); // Перезагрузка конфигурации, если она используется


        sender.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
    }
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();

            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();

                if (isSpecialBow(shooter)) {
                    // Игрок использует специальный лук
                    if (new Random().nextDouble() <= 0.30) {
                        spawnExplodingCheshuinka(shooter, arrow.getLocation());
                    }
                }
            }
        }
    }

    private void giveSpecialBow(Player player) {
        ItemStack specialBow = createSpecialBow();
        player.getInventory().addItem(specialBow);
        specialBowOwners.put(player.getUniqueId(), true);
    }

    private boolean isSpecialBow(Player player) {
        return specialBowOwners.getOrDefault(player.getUniqueId(), false);
    }

    private ItemStack createSpecialBow() {
        ItemStack specialBow = new ItemStack(Material.BOW);
        ItemMeta meta = specialBow.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Чешуйный лук");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "Шанс на появление чешуйницы 30 процентов"));
        specialBow.setItemMeta(meta);

        return specialBow;
    }

    private void spawnExplodingCheshuinka(Player player, Location location) {
        LivingEntity cheshuinka = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.SILVERFISH);
        cheshuinka.setCustomName(ChatColor.RED + "Exploding Cheshuinka");
        cheshuinka.setCustomNameVisible(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (cheshuinka.isValid() && !cheshuinka.isDead()) {
                    explodeCheshuinka(cheshuinka.getLocation());
                }
            }
        }.runTaskLater(this, 100L); // 5 seconds delay (20 ticks per second)
    }

    private void explodeCheshuinka(Location location) {
        TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        tnt.setFuseTicks(0); // Explode immediately
    }
}











