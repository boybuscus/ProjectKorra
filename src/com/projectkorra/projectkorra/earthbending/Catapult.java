package com.projectkorra.projectkorra.earthbending;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.attribute.Attribute;

public class Catapult extends EarthAbility {

	private double stageTimeMult;
	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	private Location origin;
	private Location target;

	private int stage;
	private long stageStart;

	private boolean activationHandled;
	private Vector up;
	private double angle;
	private boolean cancelWithAngle;
	private BlockData bentBlockData;

	public Catapult(final Player player) {
		super(player);
		this.setFields();
		final Block b = player.getLocation().getBlock().getRelative(BlockFace.DOWN, 1);
		if (!(isEarth(b) || isSand(b) || isMetal(b))) {
			return;
		}
		
		this.bentBlockData = b.getBlockData();
		
		if (!this.bPlayer.canBend(this)) {
			return;
		}
		
		if (this.bPlayer.isAvatarState()) {
			this.cooldown = getConfig().getLong("Abilities.Avatar.AvatarState.Earth.Catapult.Cooldown");
		}

		this.start();
	}

	private void setFields() {
		this.stageTimeMult = getConfig().getDouble("Abilities.Earth.Catapult.StageTimeMult");
		this.cooldown = getConfig().getLong("Abilities.Earth.Catapult.Cooldown");
		this.angle = 45;
		this.cancelWithAngle = getConfig().getBoolean("Abilities.Earth.Catapult.CancelWithAngle");
		this.activationHandled = false;
		this.stage = 1;
		this.stageStart = System.currentTimeMillis();
		this.up = new Vector(0, 1, 0);
	}

	private void moveEarth(final Vector apply, final Vector direction) {
		for (final Entity entity : GeneralMethods.getEntitiesAroundPoint(this.origin, 2)) {
			if (entity.getEntityId() != this.player.getEntityId()) {
				entity.setVelocity(apply);
			}
		}
		this.moveEarth(this.origin.clone().subtract(direction), direction, 3, false);
	}

	@Override
	public void progress() {
		if (!this.bPlayer.canBendIgnoreBindsCooldowns(this)) {
			this.remove();
			return;
		}
		
		final Block b = this.player.getLocation().getBlock().getRelative(BlockFace.DOWN, 1);
		if (!(isEarth(b) || isSand(b) || isMetal(b))) {
			this.remove();
			return;
		}
		
		this.bentBlockData = b.getBlockData();



		Vector direction = null;
		if (!this.activationHandled) {
			this.origin = this.player.getLocation().clone();
			direction = this.player.getEyeLocation().getDirection().clone().normalize();

			if (!this.bPlayer.canBend(this)) {
				this.activationHandled = true;
				this.remove();
				return;
			}
			this.activationHandled = true;
			this.bPlayer.addCooldown(this);
		}

		if (this.up.angle(this.player.getEyeLocation().getDirection()) > this.angle) {
			if (this.cancelWithAngle) {
				this.remove();
				return;
			}
			
			Location targetloc = GeneralMethods.getTargetedLocation(player, stage*3.5, getTransparentMaterials());
	        Vector dir = GeneralMethods.getDirection(player.getLocation(), targetloc).setY(stage);
	        player.setVelocity(dir);
			EarthAbility.playEarthbendingSound(player.getLocation());
		}
		//was done by boybuscuss, not dreamerboy.
		Location targetloc = GeneralMethods.getTargetedLocation(player, stage*2, getTransparentMaterials());
        Vector dir = GeneralMethods.getDirection(player.getLocation(), targetloc).setY(stage);
        player.setVelocity(dir);
		EarthAbility.playEarthbendingSound(player.getLocation());

		this.moveEarth(dir, direction);
		this.remove();
	}

	@Override
	public String getName() {
		return "Catapult";
	}

	@Override
	public Location getLocation() {
		if (this.player != null) {
			return this.player.getLocation();
		}
		return null;
	}

	@Override
	public long getCooldown() {
		return this.cooldown;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	public Location getOrigin() {
		return this.origin;
	}

	public void setOrigin(final Location origin) {
		this.origin = origin;
	}

	public Location getTarget() {
		return this.target;
	}

	public void setTarget(final Location target) {
		this.target = target;
	}

	public void setCooldown(final long cooldown) {
		this.cooldown = cooldown;
	}
}
