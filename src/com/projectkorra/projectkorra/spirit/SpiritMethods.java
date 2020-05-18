package com.projectkorra.projectkorra.spirit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.util.ParticleEffect;

public class SpiritMethods {
	public static void createPolygon(final Location location, final int points, final int radius, final double height, final ParticleEffect particleEffect) {
        for (int i = 0; i < points; ++i) {
            double angle = 360.0 / points * i;
            angle = Math.toRadians(angle);
            final double x = radius * Math.cos(angle);
            final double z = radius * Math.sin(angle);
            location.add(x, height, z);
            particleEffect.display(location, 0.0f, 0.0f, 0.0f, 0.0f, 1);
            location.subtract(x, height, z);
        }
    }
    
    public static void createRotatingCircle(final Location location, final int speed, final int points, final int radius, final double height, final ParticleEffect particleEffect) {
        for (int i = 0; i < speed; ++i) {
            int currPoint = 0;
            currPoint += 360 / points;
            if (currPoint > 360) {
                currPoint = 0;
            }
            final double angle = currPoint * 3.141592653589793 / 180.0;
            final double x = radius * Math.cos(angle);
            final double z = radius * Math.sin(angle);
            location.add(x, height, z);
            particleEffect.display(location, 0.0f, 0.0f, 0.0f, 0.0f, 1);
            location.subtract(x, height, z);
        }
    }
    

    
    public static SpiritType getSpiritType(final Player player) {
        final BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        final Element ls = Element.LightSpirit;
        final Element ds = Element.DarkSpirit;
        final Element s = Element.Spirit;
        
        if ((bPlayer.hasElement(ls) && bPlayer.hasElement(ds)) || (!bPlayer.hasElement(ls) && !bPlayer.hasElement(ds)) || bPlayer.hasElement(s)) {
            return SpiritType.NEUTRAL;
        }
        if (bPlayer.hasElement(ls)) {
            return SpiritType.LIGHT;
        }
        if (bPlayer.hasElement(ds)) {
            return SpiritType.DARK;
        }
        return null;
    }
    
    public static void playSpiritParticles(final BendingPlayer bPlayer, final Location location, final float X, final float Y, final float Z, final float speed, final int amount) {
        final Element ls = Element.LightSpirit;
        final Element ds = Element.DarkSpirit;
        final Element s = Element.Spirit;
        if (bPlayer.hasElement(ls) && bPlayer.hasElement(ds)) {
            ParticleEffect.MAGIC_CRIT.display(location, X, Y, Z, speed, amount);
        }
        else if (!bPlayer.hasElement(ls) && !bPlayer.hasElement(ds) && bPlayer.hasElement(s)) {
            ParticleEffect.MAGIC_CRIT.display(location, X, Y, Z, speed, amount);
        }
        else if (bPlayer.hasElement(ds)) {
            ParticleEffect.WITCH_MAGIC.display(location, X, Y, Z, speed, amount);
        }
        else if (bPlayer.hasElement(ls)) {
            ParticleEffect.INSTANT_SPELL.display(location, X, Y, Z, speed, amount);
        }
    }
    
    public static void playSpiritParticles(final SpiritType spiritType, final Location location, final float X, final float Y, final float Z, final float speed, final int amount) {
        if (spiritType == SpiritType.NEUTRAL) {
            ParticleEffect.MAGIC_CRIT.display(location, X, Y, Z, speed, amount);
        }
        else if (spiritType == SpiritType.DARK) {
            ParticleEffect.WITCH_MAGIC.display(location, X, Y, Z, speed, amount);
        }
        else if (spiritType == SpiritType.LIGHT) {
            ParticleEffect.INSTANT_SPELL.display(location, X, Y, Z, speed, amount);
        }
    }
    
    public static void setPlayerVelocity(final Player player, final boolean isForward, final float speed, final double height) {
        final Location location = player.getLocation();
        Vector direction;
        if (isForward) {
            direction = location.getDirection().normalize().multiply(speed);
        }
        else {
            direction = location.getDirection().normalize().multiply(-speed);
        }
        direction.setY(height);
        player.setVelocity(direction);
    }
    
    public static String setSpiritDescriptionColor(final SpiritType spiritType) {
        ChatColor chatColor = null;
        if (spiritType == SpiritType.NEUTRAL) {
            chatColor = ChatColor.BLUE;
        }
        else if (spiritType == SpiritType.LIGHT) {
            chatColor = ChatColor.AQUA;
        }
        else if (spiritType == SpiritType.DARK) {
            chatColor = ChatColor.DARK_GRAY;
        }
        return new StringBuilder().append(chatColor).toString();
    }
    
    public static String setSpiritDescription(final SpiritType spiritType, final String abilityType) {
        ChatColor titleColor = null;
        ChatColor descColor = null;
        if (spiritType == SpiritType.NEUTRAL) {
            titleColor = ChatColor.BLUE;
            descColor = ChatColor.DARK_AQUA;
        }
        else if (spiritType == SpiritType.LIGHT) {
            titleColor = ChatColor.AQUA;
            descColor = ChatColor.WHITE;
        }
        else if (spiritType == SpiritType.DARK) {
            titleColor = ChatColor.DARK_GRAY;
            descColor = ChatColor.DARK_RED;
        }
        return new StringBuilder().append(titleColor).append(ChatColor.BOLD).append(abilityType).append(": ").append(descColor).toString();
    }
    
    public enum SpiritType
    {
        DARK("DARK", 0), 
        LIGHT("LIGHT", 1), 
        NEUTRAL("NEUTRAL", 2);
        
        private SpiritType(final String s, final int n) {
        }
    }
}
