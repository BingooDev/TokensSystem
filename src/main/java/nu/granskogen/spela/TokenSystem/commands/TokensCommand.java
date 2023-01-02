package nu.granskogen.spela.TokenSystem.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import nu.granskogen.spela.TokenSystem.MessageUtil;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeDoesntExist;
import nu.granskogen.spela.TokenSystem.token.Token;
import nu.granskogen.spela.TokenSystem.token.TokenRepository;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import nu.granskogen.spela.TokenSystem.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TokensCommand implements CommandExecutor, TabCompleter {

	private Main plugin;
	private TokenTypeRepository tokenTypeRepository;
	private TokenRepository tokenRepository;

	public TokensCommand(Main plugin, TokenTypeRepository tokenTypeRepository, TokenRepository tokenRepository) {
		this.plugin = plugin;
		this.tokenTypeRepository = tokenTypeRepository;
		this.tokenRepository = tokenRepository;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		/*
		Possible usages:
		/tokens list
		/tokens create <name> [displayName]
		/tokens delete <name>
		/tokens amount <tokentype> [player]
		/tokens add <tokentype> <player> <amount>
		/tokens remove <tokentype> <player> <amount>
		/tokens set <tokentype> <player> <amount>
		 */

		if(args.length == 0) {
			showUsageMessage(sender);
			return false;
		}

		if(args[0].toLowerCase().equals("list")) {
			runListCommand(sender);
			return true;
		}

		if(args.length >= 4) {
			switch (args[0].toLowerCase()) {
				case "add":
					runAddCommand(sender, args[1], args[2], args[3]);
					return true;
				case "remove":
					runRemoveCommand(sender, args[1], args[2], args[3]);
					return true;
				case "set":
					runSetCommand(sender, args[1], args[2], args[3]);
					return true;
			}
		}

		if(args.length >= 3) {
			if(args[0].equalsIgnoreCase("amount")) {
				runAmountCommand(sender, args[1], args[2]);
				return true;
			} else if(args[0].equalsIgnoreCase("create")) {
				runCreateCommand(sender, args[1], args[2]);
				return true;
			}
		}

		if(args.length >= 2) {
			switch (args[0].toLowerCase()) {
				case "create":
					runCreateCommand(sender, args[1], args[1]);
					return true;
				case "delete":
					runDeleteCommand(sender, args[1]);
					return true;
				case "amount":
					runAmountCommand(sender, args[1], null);
					return true;
			}
		}

		showUsageMessage(sender);
		return false;
	}

	private void runAmountCommand(CommandSender sender, String tokenTypeName, String playerName) {
		if(!sender.hasPermission("TokensSystem.amount.self")) {
			MessageUtil.sendErrMessage(sender, "accessDenied");
			return;
		}

		TokenType tokenType = tokenTypeRepository.getTokenTypeByName(tokenTypeName);
		if(tokenType == null) {
			MessageUtil.sendErrMessage(sender, "tokenTypeDoesntExist", Map.of("tokenType", tokenTypeName));
			return;
		}

		OfflinePlayer target;
		boolean self;
		// If null, get amount for executor
		if(playerName == null) {
			if(!(sender instanceof OfflinePlayer)) {
				MessageUtil.sendErrMessage(sender, "onlyPlayers");
				return;
			}
			target = (OfflinePlayer) sender;
			self = true;
		} else {
			target = Bukkit.getOfflinePlayer(playerName);
			self = false;
		}

		Token token = tokenRepository.getToken(tokenType, target.getUniqueId());
		if(self) {
			MessageUtil.sendMessage(sender, "amountSelf", Map.of(
					"amount", String.valueOf(token.getAmount()),
					"tokenType", tokenType.getDisplayName()));
		} else {
			MessageUtil.sendMessage(sender, "amountTarget", Map.of(
					"player", target.getName(),
					"amount", String.valueOf(token.getAmount()),
					"tokenType", tokenType.getDisplayName()));
		}
	}

	private void runListCommand(CommandSender sender) {
		if(!sender.hasPermission("TokensSystem.list")) {
			MessageUtil.sendErrMessage(sender, "accessDenied");
			return;
		}

		List<String> tokenTypesList = tokenTypeRepository.getTokenTypes().stream()
				.map(tokenType -> tokenType.getName() + " (" + tokenType.getDisplayName() + ")").toList();

		if(tokenTypesList.size() == 0) {
			tokenTypesList = List.of(plugin.cfgm.getLanguage().getString("nothing"));
		}
		String tokenTypesListString = MessageUtil.addCommasAndAnds(tokenTypesList, "dark_green");

		MessageUtil.sendMessage(sender, "listTokenTypes", Map.of("tokenTypesList", tokenTypesListString));
	}

	private void runCreateCommand(CommandSender sender, String name, String displayName) {
		if(!sender.hasPermission("TokensSystem.create")) {
			MessageUtil.sendErrMessage(sender, "accessDenied");
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				String nameLowerCase = name.toLowerCase();
				try {
					tokenTypeRepository.createTokenType(nameLowerCase, displayName);
				} catch (SQLException | FailedCratingTokenType e) {
					MessageUtil.sendErrMessage(sender, "error");
					return;
				} catch (TokenTypeAlreadyExists e) {
					MessageUtil.sendErrMessage(sender, "tokenTypeExists", Map.of("tokenType", nameLowerCase));
					return;
				} catch (IllegalArgumentException ignored) {
					MessageUtil.sendErrMessage(sender, "tokenTypeIllegalCharacters");
					return;
				}
				MessageUtil.sendMessage(sender, "createdTokenType", Map.of("tokenType", nameLowerCase, "displayName", displayName));
			}
		}.runTaskAsynchronously(plugin);
	}

	private void runDeleteCommand(CommandSender sender, String name) {
		if(!sender.hasPermission("TokensSystem.delete")) {
			MessageUtil.sendErrMessage(sender, "accessDenied");
			return;
		}

		TokenType tokenType = tokenTypeRepository.getTokenTypeByName(name);
		if(tokenType == null) {
			MessageUtil.sendErrMessage(sender, "tokenTypeDoesntExist", Map.of("tokenType", name));
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					tokenTypeRepository.delete(tokenType);
				} catch (SQLException e) {
					MessageUtil.sendErrMessage(sender, "error");
					return;
				}
				MessageUtil.sendMessage(sender, "deletedTokenType", Map.of("tokenType", tokenType.getDisplayName()));
			}
		}.runTaskAsynchronously(plugin);
	}

	private void runAddCommand(CommandSender sender, String tokenTypeName, String targetName, String amountText) {
		if(!sender.hasPermission("TokensSystem.add")) {
			MessageUtil.sendErrMessage(sender, "accessDenied");
			return;
		}

		updateTokens(sender, tokenTypeName, targetName, amountText, "add");
	}

	private void runRemoveCommand(CommandSender sender, String tokenTypeName, String targetName, String amountText) {
		if(!sender.hasPermission("TokensSystem.remove")) {
			MessageUtil.sendErrMessage(sender, "accessDenied");
			return;
		}

		updateTokens(sender, tokenTypeName, targetName, amountText, "remove");
	}

	private void runSetCommand(CommandSender sender, String tokenTypeName, String targetName, String amountText) {
		if(!sender.hasPermission("TokensSystem.set")) {
			MessageUtil.sendErrMessage(sender, "accessDenied");
			return;
		}

		updateTokens(sender, tokenTypeName, targetName, amountText, "set");
	}

	private void updateTokens(CommandSender sender, String tokenTypeName, String targetName, String amountText, String method) {
		OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

		new BukkitRunnable() {
			@Override
			public void run() {
				TokenType tokenType = tokenTypeRepository.getTokenTypeByName(tokenTypeName);
				if(tokenType == null) {
					MessageUtil.sendErrMessage(sender, "tokenTypeDoesntExist", Map.of("tokenType", tokenTypeName));
					return;
				}
				Token token = tokenRepository.getToken(tokenType, target.getUniqueId());

				int amount;
				try {
					amount = Integer.parseInt(amountText);
				} catch (NumberFormatException e) {
					MessageUtil.sendErrMessage(sender, "invalidAmount");
					return;
				}

				switch (method) {
					case "add":
						token.addAmount(amount);
						break;
					case "remove":
						token.removeAmount(amount);
						break;
					case "set":
						token.setAmount(amount);
						break;
					default:
						throw new IllegalArgumentException("method must be one of: add, remove or set");
				}

				try {
					tokenRepository.updateToken(target.getUniqueId(), token);
				} catch (SQLException e) {
					MessageUtil.sendErrMessage(sender, "error");
					return;
				} catch (TokenTypeDoesntExist e) {
					MessageUtil.sendErrMessage(sender, "tokenTypeDoesntExist", Map.of("tokenType", tokenTypeName));
					return;
				}

				// successfully updated tokens
				// sending message to target and executor
				MessageUtil.sendMessage(sender, method+"Tokens",
						Map.of("player", targetName,
								"tokenType", tokenType.getDisplayName(),
								"amount", amountText,
								"sum", String.valueOf(token.getAmount())
						));

				if(target.isOnline()) {
					MessageUtil.sendMessage((Player) target, method+"TokensTarget",
							Map.of("tokenType", tokenType.getDisplayName(),
									"amount", amountText,
									"sum", String.valueOf(token.getAmount())
							));
				}

			}
		}.runTaskAsynchronously(plugin);
	}

	private void showUsageMessage(CommandSender sender) {
		sender.sendMessage("§7Syntax: /tokens list");
		sender.sendMessage("§7Syntax: /tokens create <namn> [visningsnamn]");
		sender.sendMessage("§7Syntax: /tokens delete <namn>");
		sender.sendMessage("§7Syntax: /tokens amount <tokentyp> [spelare]");
		sender.sendMessage("§7Syntax: /tokens add <tokentyp> <spelare> <summa>");
		sender.sendMessage("§7Syntax: /tokens remove <tokentyp> <spelare> <summa>");
		sender.sendMessage("§7Syntax: /tokens set <tokentyp> <spelare> <summa>");
	}

	private static List<String> tab_complete_s1 = List.of("list", "create", "delete", "amount", "add", "remove", "set");
	private static List<String> tab_complete_should_show_token_type = List.of("amount", "add", "remove", "set", "delete");
	private static List<String> tab_complete_should_show_numbers = List.of("add", "remove", "set");
	private static List<String> tab_complete_numbers = List.of("10", "100", "1000");
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> fList = new ArrayList<>();
		if (args.length == 1) {
			for (String s : tab_complete_s1) {
				if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					fList.add(s);
			}
			return fList;
		}
		if(args.length == 2 && tab_complete_should_show_token_type.contains(args[0].toLowerCase())) {
			for (String s : tokenTypeRepository.getTokenTypeNames()) {
				if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
					fList.add(s);
				}
			}
			return fList;
		}
		if(args.length == 4 && tab_complete_should_show_numbers.contains(args[0].toLowerCase())) {
			for (String s : tab_complete_numbers) {
				if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
					fList.add(s);
				}
			}
			return fList;
		}
		return null;
	}
}
