package me.KarimKaylani.Race;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RaceEvents implements Listener {
	
	static Main plugin;
	
	public RaceEvents (Main instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block b = event.getClickedBlock();
				
		if (b == null) {
			return;
		}
		
		//if interacted block is beacon
		if (b.getType() == Material.BEACON) {
			
			int x = (int) b.getLocation().getX();
			int y = (int) b.getLocation().getY();
			int z = (int) b.getLocation().getZ();
			
			//if first player to click beacon - winner
			if (plugin.winners.size() == 0) {
				plugin.winners.add(event.getPlayer().getName());
				Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + ChatColor.ITALIC + event.getPlayer().getName() + " WINS");
				ItemStack itemToEnchant = event.getPlayer().getInventory().getItemInMainHand();
				
				itemToEnchant.setItemMeta(Play.randomEnchant(itemToEnchant).getItemMeta());
			} 
			
			//everyone else to click beacon
			else {
				if (!(plugin.winners.contains(event.getPlayer().getName()))) {
					plugin.winners.add(event.getPlayer().getName());
					Bukkit.broadcastMessage(ChatColor.AQUA + "" + "#" + (plugin.winners.indexOf(event.getPlayer().getName()) + 1) + ": " + event.getPlayer().getName());
				}
			}
			
			//if everyone in server clicks beacon - reset and delete beacon
			if (plugin.winners.size() >= Bukkit.getOnlinePlayers().size()) {
				b.setType(Material.AIR);
				
				for (Player p : Bukkit.getOnlinePlayers() ) {
					p.sendTitle("ROUND OVER", "Winner: " + plugin.winners.get(0), 10, 100, 20);
				}
				
				for (int xPoint = x-1; xPoint <= x+1 ; xPoint++) { 
			        for (int zPoint = z-1 ; zPoint <= z+1; zPoint++) {            
			            plugin.world.getBlockAt(xPoint, y-1, zPoint).setType(Material.AIR);
			        }
				
				}
				
				plugin.world.playSound(b.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50, 1);
				plugin.winners.clear();
				plugin.isPlaying = false;
			}
			
		
		}
		
	}
	
	//cancel breaking of beacon + pyramid during game
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (plugin.isPlaying) {
			if (event.getBlock().getType() == Material.BEACON || event.getBlock().getType() == Material.IRON_BLOCK) {
				event.setCancelled(true);
				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "CANNOT BREAK " + event.getBlock().toString() + " DURING GAME");
			}
		}
	}
	
	//enact 3 seconds of slowness each time damage is taken during game
	@EventHandler
	public void onHit(EntityDamageEvent event) {
		if (plugin.isPlaying) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
			}
		}
	}

}
