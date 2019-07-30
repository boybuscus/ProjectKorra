package com.projectkorra.projectkorra.earthbending.sand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.ElementalAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.util.DamageHandler;

public class SandSpout extends SandAbility {
	
	private static final double HEIGHT = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.SandSpout.Height");
	private static final int BTIME = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.SandSpout.BlindnessTime");
	private static final int SPOUTDAMAGE = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.SandSpout.SpoutDamage");
	private static final boolean SPIRAL = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Earth.SandSpout.Spiral");
	private static final double SPEED = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.SandSpout.SpoutSpeed");
	private static final long COOLDOWN = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.SandSpout.Cooldown");
	private static final long interval = 100;
	
	private long time;
	private int angle = 0;
	private double speed = SPEED;
	private double height = HEIGHT;
	private int bTime = BTIME;
	private long cooldown = COOLDOWN;
	private double spoutDamage = SPOUTDAMAGE;
	private double y = 0D;
	
	
	public SandSpout(Player player) {
		super(player);
		
		if (!this.bPlayer.canBend(this)) {
			return;
		}
		
		final SandSpout spout = getAbility(player, SandSpout.class);
		if (spout != null) {
			spout.remove();
			return;
		}
		
		final double heightRemoveThreshold = 2;
		if (!this.isWithinMaxSpoutHeight(heightRemoveThreshold)) {
			return;
		}
		
		time = System.currentTimeMillis();
		Block topBlock = GeneralMethods.getTopBlock(player.getLocation(), 0, -50);
		if (topBlock == null) {
			topBlock = player.getLocation().getBlock();
		}
		Material mat = topBlock.getType();
		if (mat != Material.SAND && mat != Material.SANDSTONE && mat != Material.RED_SANDSTONE && mat != Material.RED_SAND) {
			return;
		}
		
		if (this.bPlayer.isAvatarState()) {
			this.height = getConfig().getDouble("Abilities.Avatar.AvatarState.Earth.Sand.SandSpout.Height");
		}
		
		flightHandler.createInstance(player, this.getName());
		
		bPlayer.addCooldown(this);
		this.start();
		
	}
	
	@Deprecated
	public static boolean removeSpouts(Location loc0, final double radius, final Player sourceplayer) {
		boolean removed = false;
		for (final SandSpout spout : getAbilities(SandSpout.class)) {
			if (!spout.getPlayer().equals(sourceplayer)) {
				final Location loc1 = spout.getPlayer().getLocation().getBlock().getLocation();
				loc0 = loc0.getBlock().getLocation();
				final double dx = loc1.getX() - loc0.getX();
				final double dy = loc1.getY() - loc0.getY();
				final double dz = loc1.getZ() - loc0.getZ();

				final double distance = Math.sqrt(dx * dx + dz * dz);

				if (distance <= radius && dy > 0 && dy < spout.height) {
					spout.remove();
					removed = true;
				}
			}
		}
		return removed;
	}
	
	private void allowFlight() {
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setFlySpeed((float) speed);
	}

	private void removeFlight() {
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setFlySpeed(.1f);
	}

	private Block getGround() {
		Block standingblock = player.getLocation().getBlock();
		for (int i = 0; i <= height + 5; i++) {
			Block block = standingblock.getRelative(BlockFace.DOWN, i);
			if (GeneralMethods.isSolid(block) || block.isLiquid()) {
				return block;
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private void rotateSandColumn(Block block) {


		if (System.currentTimeMillis() >= time + interval) {
			time = System.currentTimeMillis();

			Location location = block.getLocation();
			Location playerloc = player.getLocation();
			location = new Location(location.getWorld(), playerloc.getX(), location.getY(), playerloc.getZ());

			double dy = playerloc.getY() - block.getY();
			if (dy > height)
				dy = height;
			Integer[] directions = { 0, 1, 2, 3, 5, 6, 7, 8 };
			int index = angle;

			angle++;
			if (angle >= directions.length)
				angle = 0;
			for (int i = 1; i <= dy; i++) {

				index += 1;
				if (index >= directions.length)
					index = 0;

				Location effectloc2 = new Location(location.getWorld(), location.getX(), block.getY() + i, location.getZ());

				if (SPIRAL) {
					displayHelix(block.getLocation(), this.player.getLocation(), block);
				}
				if (block != null && ((block.getType() == Material.SAND && block.getData() == (byte) 0) || block.getType() == Material.SANDSTONE)) {
					displaySandParticle(effectloc2, 20, 1f, 3f, 1f, .2f, false);
				} else if (block != null && ((block.getType() == Material.SAND && block.getData() == (byte) 1) || block.getType() == Material.RED_SANDSTONE)) {
					displaySandParticle(effectloc2, 20, 1f, 3f, 1f, .2f, true);
				}

				Collection<Player> players = GeneralMethods.getPlayersAroundPoint(effectloc2, 1.5f);
				if (!players.isEmpty())
					for (Player sPlayer : players) {
						if (!sPlayer.equals(player)) {
							sPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, bTime * 20, 1));
							DamageHandler.damageEntity(sPlayer, spoutDamage, this);
						}
					}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void displayHelix(Location location, Location player, Block block) {
		this.y += 0.1;
		if (this.y >= player.getY() - location.getY()) {
			this.y = 0D;
		}
		for (int points = 0; points <= 5; points++) {
			double x = Math.cos(y);
			double z = Math.sin(y);
			double nx = x * -1;
			double nz = z * -1;
			Location newloc = new Location(player.getWorld(), location.getX() + x, location.getY() + y, location.getZ() + z);
			Location secondloc = new Location(player.getWorld(), location.getX() + nx, location.getY() + y, location.getZ() + nz);
			if (block != null && ((block.getType() == Material.SAND && block.getData() == (byte) 0) || block.getType() == Material.SANDSTONE)) {
				displaySandParticle(newloc.add(0.5, 0.5, 0.5), 2, 0.1F, 0.1F, 0.1F, 1, false);
				displaySandParticle(secondloc.add(0.5, 0.5, 0.5), 2, 0.1F, 0.1F, 0.1F, 1, false);
			} else if (block != null && ((block.getType() == Material.SAND && block.getData() == (byte) 1) || block.getType() == Material.RED_SANDSTONE)) {
				displaySandParticle(newloc.add(0.5, 0.5, 0.5), 2, 0.1F, 0.1F, 0.1F, 1, true);
				displaySandParticle(secondloc.add(0.5, 0.5, 0.5), 2, 0.1F, 0.1F, 0.1F, 1, true);
			}
		}
	}
	
	@Override
	public List<Location> getLocations() {
		final ArrayList<Location> locations = new ArrayList<>();
		final Location topLoc = this.player.getLocation().getBlock().getLocation();
		final double ySpacing = 3;
		for (double i = 0; i <= this.height; i += ySpacing) {
			locations.add(topLoc.clone().add(0, -i, 0));
		}
		return locations;
	}
	
	

	@Override
	public void progress() {
		if(player.isDead() || !player.isOnline() || !this.bPlayer.canBendIgnoreBinds(this) || !this.bPlayer.canBind(this)) {
			remove();
			return;
		}
		
		final Block eyeBlock = this.player.getEyeLocation().getBlock();
		if (ElementalAbility.isWater(eyeBlock) || GeneralMethods.isSolid(eyeBlock)) {
			this.remove();
			return;
		}
		
		player.setFallDistance(0);
		player.setSprinting(false);
		Random rand = new Random();
		if (rand.nextInt(2) == 0) {
			playSandbendingSound(player.getLocation());
		}
		Block block = getGround();
		if (block != null && (block.getType() == Material.SAND || block.getType() == Material.SANDSTONE || block.getType() == Material.RED_SANDSTONE)) {
			double dy = player.getLocation().getY() - block.getY();
			if (dy > height) {
				if (player.getGameMode() != GameMode.CREATIVE) {
					player.setAllowFlight(false);
					player.setFlying(false);
				}
				removeFlight();
			} else {
				allowFlight();
			}
			rotateSandColumn(block);
		} else {
			if (player.getGameMode() != GameMode.CREATIVE) {
				player.setAllowFlight(false);
				player.setFlying(false);
			}
			remove();
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		flightHandler.removeInstance(this.player, this.getName());
	}
	
	public Player getPlayer() {
		return player;
	}

	public double getHeight() {
		return height;
	}
	
	public double getSpeed() {
		return speed;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}
	
	private boolean isWithinMaxSpoutHeight(final double threshold) {
		final Block ground = this.getGround();
		if (ground == null) {
			return false;
		}
		final double playerHeight = this.player.getLocation().getY();
		if (playerHeight > ground.getLocation().getY() + this.height + threshold) {
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return "SandSpout";
	}

	@Override
	public Location getLocation() {
		return this.player != null ? this.player.getLocation() : null;
	}

}
