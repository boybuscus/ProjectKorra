package com.projectkorra.ProjectKorra.firebending;

import com.projectkorra.ProjectKorra.GeneralMethods;
import com.projectkorra.ProjectKorra.Ability.AddonAbility;
import com.projectkorra.ProjectKorra.Utilities.ParticleEffect;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Used in {@link HeatControl}.
 */
public class Cook extends AddonAbility {

	private static final long COOK_TIME = 2000;
	private static final Material[] cookables = { Material.RAW_BEEF,
		Material.RAW_CHICKEN, Material.RAW_FISH, Material.PORK,
		Material.POTATO_ITEM, Material.RABBIT, Material.MUTTON };

	private Player player;
	private ItemStack items;
	private long time;
	private long cooktime = COOK_TIME;

	public Cook(Player player) {
		reloadVariables();
		this.player = player;
		items = player.getItemInHand();
		time = System.currentTimeMillis();
		if (isCookable(items.getType())) {
			//instances.put(player, this);
			putInstance(player, this);
		}
	}

	private static boolean isCookable(Material material) {
		return Arrays.asList(cookables).contains(material);
	}

	private void cook() {
		ItemStack cooked = getCooked(items);
		HashMap<Integer, ItemStack> cantfit = player.getInventory().addItem(cooked);
		for (int id : cantfit.keySet()) {
			player.getWorld().dropItem(player.getEyeLocation(), cantfit.get(id));
		}
		int amount = items.getAmount();
		if (amount == 1) {
			player.getInventory().clear(player.getInventory().getHeldItemSlot());
			// items.setType(Material.AIR);
		} else {
			items.setAmount(amount - 1);
		}
	}

	private ItemStack getCooked(ItemStack is) {
		ItemStack cooked = new ItemStack(Material.AIR);
		Material material = is.getType();
		switch (material) {
		case RAW_BEEF:
			cooked = new ItemStack(Material.COOKED_BEEF, 1);
			break;
		case RAW_FISH:
			ItemStack salmon = new ItemStack(Material.RAW_FISH, 1, (short) 1);
			if (is.getDurability() == salmon.getDurability()) {
				cooked = new ItemStack(Material.COOKED_FISH, 1, (short) 1);
			} else {
				cooked = new ItemStack(Material.COOKED_FISH, 1);
			}
			break;
		case RAW_CHICKEN:
			cooked = new ItemStack(Material.COOKED_CHICKEN, 1);
			break;
		case PORK:
			cooked = new ItemStack(Material.GRILLED_PORK, 1);
			break;
		case POTATO_ITEM:
			cooked = new ItemStack(Material.BAKED_POTATO, 1);
			break;
		case MUTTON:
			cooked = new ItemStack(Material.COOKED_MUTTON);
			break;
		case RABBIT:
			cooked = new ItemStack(Material.COOKED_RABBIT);
			break;
		default:
			break; //Shouldn't happen
		}
		return cooked;
	}

	public long getCooktime() {
		return cooktime;
	}

	public Player getPlayer() {
		return player;
	}

	public long getTime() {
		return time;
	}

	@Override
	public boolean progress() {
		if (player.isDead() || !player.isOnline()) {
			remove();
			return false;
		}

		if (GeneralMethods.getBoundAbility(player) == null) {
			remove();
			return false;
		}
		if (!player.isSneaking() || !GeneralMethods.getBoundAbility(player).equalsIgnoreCase("HeatControl")) {
			remove();
			return false;
		}

		if (!items.equals(player.getItemInHand())) {
			time = System.currentTimeMillis();
			items = player.getItemInHand();
		}

		if (!isCookable(items.getType())) {
			remove();
			return false;
		}

		if (System.currentTimeMillis() > time + cooktime) {
			cook();
			time = System.currentTimeMillis();
		}
		ParticleEffect.FLAME.display(player.getEyeLocation(), 0.6F, 0.6F, 0.6F, 0, 6);
		ParticleEffect.SMOKE.display(player.getEyeLocation(), 0.6F, 0.6F, 0.6F, 0, 6);
		return true;
	}

	@Override
	public void reloadVariables() {}

	public void setCooktime(long cooktime) {
		this.cooktime = cooktime;
	}

	public void setTime(long time) {
		this.time = time;
	}

}