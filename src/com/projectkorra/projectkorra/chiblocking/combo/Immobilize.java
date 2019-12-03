package com.projectkorra.projectkorra.chiblocking.combo;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.ChiAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.MovementHandler;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Immobilize extends ChiAbility implements ComboAbility {

	@Attribute(Attribute.DURATION)
	private long duration;
	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	private double range;
	private Entity target;

	public Immobilize(final Player player) {
		super(player);

		this.cooldown = getConfig().getLong("Abilities.Chi.Immobilize.Cooldown");
		this.duration = getConfig().getLong("Abilities.Chi.Immobilize.ParalyzeDuration");
		this.range = getConfig().getDouble("Abilities.Chi.Immobilize.Range");
		//TODO: add config options for Immobilize to config package for Range
		this.target = GeneralMethods.getTargetedEntity(player, range);
		if (!this.bPlayer.canBendIgnoreBinds(this)) {
			return;
		}
		if (this.target == null) {
			this.remove();
			return;
		} else {
			if (GeneralMethods.isRegionProtectedFromBuild(this, target.getLocation()) || ((target instanceof Player) && Commands.invincible.contains(((Player) target).getName())) || !(target instanceof LivingEntity)){
				return;
			}
			paralyze(this.target, this.duration);
			this.bPlayer.addCooldown(this);
		}
	}

	/**
	 * Paralyzes the target for the given duration. The player will be unable to
	 * move or interact for the duration.
	 *
	 * @param target The Entity to be paralyzed
	 * @param duration The time in milliseconds the target will be paralyzed
	 */
	private static void paralyze(final Entity target, final Long duration) {
		final MovementHandler mh = new MovementHandler((LivingEntity) target, CoreAbility.getAbility(Immobilize.class));
		mh.stopWithDuration(duration / 1000 * 20, Element.CHI.getColor() + "* Immobilized *");
	}

	@Override
	public String getName() {
		return "Immobilize";
	}

	@Override
	public void progress() {
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public long getCooldown() {
		return this.cooldown;
	}

	@Override
	public Location getLocation() {
		return this.target != null ? this.target.getLocation() : null;
	}

	@Override
	public Object createNewComboInstance(final Player player) {
		return new Immobilize(player);
	}

	@Override
	public ArrayList<AbilityInformation> getCombination() {
		final ArrayList<AbilityInformation> immobilize = new ArrayList<>();
		immobilize.add(new AbilityInformation("QuickStrike", ClickType.LEFT_CLICK));
		immobilize.add(new AbilityInformation("SwiftKick", ClickType.LEFT_CLICK));
		immobilize.add(new AbilityInformation("QuickStrike", ClickType.LEFT_CLICK));
		immobilize.add(new AbilityInformation("QuickStrike", ClickType.LEFT_CLICK));
		return immobilize;
	}

	public long getDuration() {
		return this.duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}

	public Entity getTarget() {
		return this.target;
	}

	public void setTarget(final Entity target) {
		this.target = target;
	}

	public void setCooldown(final long cooldown) {
		this.cooldown = cooldown;
	}
}
