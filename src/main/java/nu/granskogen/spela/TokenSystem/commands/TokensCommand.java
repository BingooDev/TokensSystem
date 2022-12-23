package nu.granskogen.spela.TokenSystem.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.PlayerTokens;

public class TokensCommand implements CommandExecutor, TabCompleter {
	Main pl = Main.getInstance();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 1) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("You need to be a player!");
				return false;
			}
			Player player = (Player) sender;
			
			if(!sender.hasPermission("TokensSystem.see.self")) {
				sender.sendMessage("§cÅtkomst nekad!");
				return false;
			}
			
			PlayerTokens tokens;
			switch (args[0].toLowerCase()) {
				case "jobs":
					tokens = pl.getJobsToken(player.getUniqueId());
					break;
				case "vote":
					tokens = pl.getVoteToken(player.getUniqueId());
					break;
	
				default:
					sender.sendMessage("§cOgiltig token typ.");
					return false;
			}
			
			sender.sendMessage("§aDu har §2" + tokens.getAmount() + " " + tokens.getName() + "s" + "§a.");
			return true;
		}
		
		if(args.length == 3) {
			if(!sender.hasPermission("TokensSystem.see.other")) {
				sender.sendMessage("§cÅtkomst nekad!");
				return false;
			}
			
			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
			
			if(player == null) {
				sender.sendMessage("§cSpelaren finns inte.");
				return false;
			}
			
			if(!args[1].equalsIgnoreCase("amount")) {
				sendUsageText(sender);
				return false;
			}
			
			PlayerTokens tokens;
			switch (args[0].toLowerCase()) {
				case "jobs":
					tokens = pl.getJobsToken(player.getUniqueId());
					break;
				case "vote":
					tokens = pl.getVoteToken(player.getUniqueId());
					break;
	
				default:
					sender.sendMessage("§cOgiltig token typ.");
					return false;
			}
			
			sender.sendMessage("§2" + player.getName() + "§a har §2" + tokens.getAmount() + " " + tokens.getName() + "§a.");
			return true;
		}
		
		if(args.length < 4) {
			sendUsageText(sender);
			return false;
		}
		
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
		
		if(player == null) {
			sender.sendMessage("§cSpelaren finns inte.");
			return false;
		}
		
		PlayerTokens tokens;
		switch (args[0].toLowerCase()) {
			case "jobs":
				tokens = pl.getJobsToken(player.getUniqueId());
				break;
			case "vote":
				tokens = pl.getVoteToken(player.getUniqueId());
				break;

			default:
				sender.sendMessage("§cOgiltig token typ.");
				return false;
		}
		
		if(args[1].equalsIgnoreCase("add")) {
			if(!sender.hasPermission("TokensSystem.add")) {
				sender.sendMessage("§cÅtkomst nekad!");
				return false;
			}
			
			try {
				tokens.add(Integer.parseInt(args[3]));
			} catch (NumberFormatException e) {
				sender.sendMessage("§cOgiltig summa");
				return false;
			}
			
			pl.dbm.updateUserTokens(player.getUniqueId());
			
			sender.sendMessage("§2" + player.getName() + "§a har fått §2" + args[3] + " " + tokens.getName()+ "s§a. Ny totalsumma: §2" + tokens.getAmount() + " " + tokens.getName() + "s§a.");
			
		} else if(args[1].equalsIgnoreCase("remove")) {
			if(!sender.hasPermission("TokensSystem.remove")) {
				sender.sendMessage("§cÅtkomst nekad!");
				return false;
			}
			
			
			try {
				int removeAmount = Integer.parseInt(args[3]);
				if(tokens.getAmount() - removeAmount < 0) {
					sender.sendMessage("§cKan inte ha ett negativt antal tokens.");
					return false;
				}
				tokens.remove(removeAmount);
			} catch (NumberFormatException e) {
				sender.sendMessage("§cOgiltig summa");
				return false;
			}
			
			pl.dbm.updateUserTokens(player.getUniqueId());
			
			sender.sendMessage("§2" + player.getName() + "§a har blivit av med §2" + args[3] + " " + tokens.getName() + "s§a. Ny totalsumma: §2" + tokens.getAmount() + " " + tokens.getName() + "s§a.");
			
		} else if(args[1].equalsIgnoreCase("set")) {
			if(!sender.hasPermission("TokensSystem.remove")) {
				sender.sendMessage("§cÅtkomst nekad!");
				return false;
			}
			
			try {
				tokens.setAmount(Integer.parseInt(args[3]));
			} catch (NumberFormatException e) {
				sender.sendMessage("§cOgiltig summa");
				return false;
			}
			
			pl.dbm.updateUserTokens(player.getUniqueId());
			
			sender.sendMessage("§2" + player.getName() + "§a har nu: §2" + tokens.getAmount() + " " + tokens.getName() + "s§a.");
			
		} else {
			sendUsageText(sender);
		}
		
		return false;
	}
	
	private void sendUsageText(CommandSender sender) {
		sender.sendMessage("§7Syntax: /tokens jobs/vote add/remove/set <player> <summa>");
		sender.sendMessage("§7Syntax: /tokens jobs/vote amount <player>");
		sender.sendMessage("§7Syntax: /tokens jobs/vote");
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> s1 = Arrays.asList(new String[] { "jobs", "vote" });
		List<String> s2 = Arrays.asList(new String[] { "add", "remove", "set", "amount" });
		List<String> fList = Lists.newArrayList();
		if (args.length == 1) {
			for (String s : s1) {
				if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					fList.add(s);
			}
			return fList;
		}
		if (args.length == 2) {
			for (String s : s2) {
				if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					fList.add(s);
			}
			return fList;
		}
		return null;
	}
}
