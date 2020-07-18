package me.KarimKaylani.Race;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.scheduler.BukkitRunnable;

public class Play implements CommandExecutor {
	
	static Main plugin;
	
	public Play (Main instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			
			//runs /race
			if (label.equalsIgnoreCase("race")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("Sorry, console can't play!");
					return true;
				}
				Player player = (Player) sender;
				boolean isUsable = false;
				
				// if not given argument - set default time
				if (args.length == 0) {
					plugin.seconds = 180;
				}
				
				//given argument
				else {
					try {
						plugin.seconds = Integer.parseInt(args[0]);
						
					} catch (Exception e) {
						player.sendMessage(ChatColor.RED + "Usage: /race [prep time in seconds] (defaults to 3min)");
						return true;
					}
					
				}		
				
				//check for permissions
				if (!(player.hasPermission("race.use"))) {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Sorry, you don't have permission!");
					return true;
				}
				
				//disable pvp
				plugin.world = player.getWorld();
				plugin.world.setPVP(false);
				
				String totalTime;
				
				//sending prep title
				if (plugin.seconds < 60) {
					totalTime = Integer.toString(plugin.seconds) + " seconds";
				}
				
				else {
					double minutes = (double) plugin.seconds/60;
					totalTime = String.valueOf(minutes) + " minutes";
				}
				
				for (Player p : Bukkit.getOnlinePlayers() ) {
					p.sendTitle("GET READY FOR THE RACE", "You have " + totalTime + "!", 10, 100 , 20);
				}
				
				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "PVP DISABLED");
				Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Keep an eye out! There are " + Integer.toString(Bukkit.getOnlinePlayers().size()) + " loot chests nearby");
				
				
				//generate teleport spot
				
				while (!isUsable) {
					
					plugin.spawnX = generateRandomInt(-5000, 5000);
					plugin.spawnZ = generateRandomInt(-5000, 5000);
					plugin.spawnY = plugin.world.getHighestBlockAt(plugin.spawnX, plugin.spawnZ).getY() + 2;
					
					plugin.spawnLoc = new Location(plugin.world, plugin.spawnX, plugin.spawnY, plugin.spawnZ);
					
					if (!(Arrays.asList(plugin.barredBiomes).contains(plugin.spawnLoc.getBlock().getBiome().toString())) && !(plugin.spawnLoc.getBlock().isLiquid())) {
						isUsable = true;
					}
					
				}
				
				 //generate blacksmith chest for each player on server
			    for (int i = 1; i <= Bukkit.getOnlinePlayers().size(); i++) {
			    	
				    int chestX = generateRandomInt(plugin.spawnX-100, plugin.spawnX+100);
					int chestZ = generateRandomInt(plugin.spawnZ-100,plugin.spawnZ+100);
					int chestY = plugin.world.getHighestBlockAt(chestX, chestZ).getY() + 1;
									
					plugin.world.getBlockAt(chestX, chestY, chestZ).setType(Material.CHEST);
					
					Chest chest = (Chest) plugin.world.getBlockAt(chestX, chestY, chestZ).getState();
					chest.setLootTable(Bukkit.getLootTable(LootTables.VILLAGE_WEAPONSMITH.getKey()));
					chest.update();
					
			    }
				
				//teleport all players and set spawn point
				for (Player p : Bukkit.getOnlinePlayers() ) {
					p.teleport(plugin.spawnLoc);
					plugin.world.setSpawnLocation(plugin.spawnLoc);
				}
				
				//countdown for prep phase
				new BukkitRunnable() {
					
					private int secondsRemaining = plugin.seconds;
					
					public void run() {
											
						if (secondsRemaining >= 0 && secondsRemaining <= 10) {
							Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "RACE BEGINS IN: " + Integer.toString(secondsRemaining));
						}
						
						if (secondsRemaining >= 0) {
							secondsRemaining--;
						}
						
						else {
							playGame();
							plugin.isPlaying = true;
							this.cancel();
						}
					}
					
				}.runTaskTimer(plugin, 0, 20);
				
				return true;
			}
			
		return false;	
	}
		
		//generate random, inclusive int
		public static int generateRandomInt(int min, int max) {
			Random r = new Random();
			return r.nextInt(max - min) + min;
		}
		
		//activates after countdown ends
		public void playGame() {
			
			for (Player p : Bukkit.getOnlinePlayers() ) {
				p.sendTitle("RACE TO THE BEACON", "Punch the block first to secure your ranking", 10, 100, 20);
			}
	
			plugin.world.setPVP(true);
			Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "PVP ENABLED");

					
			for (Player p : Bukkit.getOnlinePlayers() ) {
				p.teleport(plugin.spawnLoc);
			}
			
			//randomize spot for beacon
			int x = generateRandomInt(plugin.spawnX-150, plugin.spawnX+150);
			int z = generateRandomInt(plugin.spawnZ-150,plugin.spawnZ+ 150);
			int y = plugin.world.getHighestBlockAt(x, z).getY();	
			
			//spawn beacon + pyramid
			plugin.world.getBlockAt(x, y, z).setType(Material.BEACON);
			
		    for (int xPoint = x-1; xPoint <= x+1 ; xPoint++) { 
		        for (int zPoint = z-1 ; zPoint <= z+1; zPoint++) {            
		            plugin.world.getBlockAt(xPoint, y-1, zPoint).setType(Material.IRON_BLOCK);
		        }
		    	
		    }
		    
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "Any damage taken enacts slowness, first to click beacon wins and earns random enchantment on item it is clicked with");
			Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "YOUR COORDINATES ARE:");
			Bukkit.broadcastMessage(ChatColor.BOLD + Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(z));
			plugin.world.playSound(plugin.spawnLoc, Sound.ITEM_TOTEM_USE, 50, 1);
		}
		
		//generate random enchant + level given item
		public static ItemStack randomEnchant(ItemStack item) {
			
			List<Enchantment> allowed = new ArrayList<Enchantment>();
			
			for (Enchantment e : Enchantment.values()) {
				if (e.canEnchantItem(item)) {
					allowed.add(e);
				}
			}
			
			if (allowed.size() >= 1) {
				Collections.shuffle(allowed);
				Enchantment chosenEnchant = allowed.get(0);
				
				int randLevel = generateRandomInt(chosenEnchant.getStartLevel(), chosenEnchant.getMaxLevel());
				item.addEnchantment(chosenEnchant, randLevel);	
				
			}
			
			return item;
		}
}
	
