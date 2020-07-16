package me.KarimKaylani.Race;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener {
	
	public World world;
	
	//biomes to be avoided for teleporting
	public String[] barredBiomes = {"COLD_OCEAN", "DEEP_COLD_OCEAN", "DEEP_FROZEN_OCEAN",
			"DEEP_LUKEWARM_OCEAN", "DEEP_OCEAN", "DEEP_WARM_OCEAN", "LUKEWARM_OCEAN",
			"OCEAN", "WARM_OCEAN"};
	
	public ArrayList<String> winners = new ArrayList<String>();
	
	public int spawnX;
	public int spawnY;
	public int spawnZ;
	public Location spawnLoc;
	public int seconds;
	
	public boolean isPlaying = false;

	
	@Override
	public void onEnable() {
		
		this.getCommand("Race").setExecutor(new Play(this));
		this.getServer().getPluginManager().registerEvents(new RaceEvents(this), this);

	}

	@Override
	public void onDisable() {

	}
	
}