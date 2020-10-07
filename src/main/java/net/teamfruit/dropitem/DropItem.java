package net.teamfruit.dropitem;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class DropItem extends JavaPlugin implements Listener {
    private ConfigurationSection playerConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        playerConfig = getConfig().getConfigurationSection("players");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (!(event.getDamager() instanceof Player))
            return;
        Player damagee = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (!event.getDamager().hasPermission("dropitem"))
            return;
        ConfigurationSection playerConfig = getConfig().getConfigurationSection("players").getConfigurationSection(damagee.getName());
        if (playerConfig == null)
            return;
        double chance = playerConfig.getDouble("chance", 1);
        if (Math.random() < chance) {
            String sound = playerConfig.getString("sound");
            if (sound != null)
                damagee.getWorld().playSound(damagee.getLocation(), sound, 1, 1);
            ItemStack item = playerConfig.getItemStack("item");
/*
            String name = playerConfig.getString("name");
            if (name != null) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(name);
                item.setItemMeta(meta);
            }
*/
            if (item != null) {
                damagee.getWorld().dropItem(damagee.getLocation(), item);
            }
            int exp = playerConfig.getInt("exp", 0);
            if (exp > 0)
                damager.getWorld().spawn(damager.getLocation(), ExperienceOrb.class).setExperience(exp);
        }
    }

/*
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Must be player");
            return true;
        }
        Player player = (Player) sender;
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("item", player.getInventory().getItem(EquipmentSlot.HAND));
        Arrays.stream(cfg.saveToString().split("\n")).forEach(sender::sendMessage);
        return true;
    }
*/
}
