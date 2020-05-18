package com.projectkorra.projectkorra.command;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Executor for /bending version. Extends {@link PKCommand}.
 */
public class VersionCommand extends PKCommand {

	public VersionCommand() {
		super("version", "/bending version", "Displays the installed version of ProjectKorra.", new String[] { "version", "v" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !correctLength(sender, args.size(), 0, 0)) {
			return;
		}

		sender.sendMessage(ChatColor.GREEN + "ModifiedCore Version: " + ChatColor.RED + "1.8.8.B9");
		if (GeneralMethods.hasRPG()) {
			sender.sendMessage(ChatColor.GREEN + "RPG Version: " + ChatColor.RED + GeneralMethods.getRPG().getDescription().getVersion());
		}
		if (GeneralMethods.hasItems()) {
			sender.sendMessage(ChatColor.GREEN + "Items Version: " + ChatColor.RED + GeneralMethods.getItems().getDescription().getVersion());
		}
		sender.sendMessage(ChatColor.GREEN + "Founded by: " + ChatColor.RED + "MistPhizzle");
		sender.sendMessage(ChatColor.DARK_RED + "Modified by: Boybuscus and DreamerB0y_");
	}

}
