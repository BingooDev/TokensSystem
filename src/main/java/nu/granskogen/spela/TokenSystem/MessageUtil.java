package nu.granskogen.spela.TokenSystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class MessageUtil {
	private static Main pl = Main.getInstance();

	public static ConfigManager test(){
		return pl.cfgm;
	}


	public static void sendMessage(CommandSender receiver, String language_key) {
		sendMessage(receiver, language_key, "messages");
	}

	public static void sendErrMessage(CommandSender receiver, String language_key) {
		sendMessage(receiver, language_key, "errors");
	}

	private static void sendMessage(CommandSender receiver, String language_key, String lang_key_prefix) {
		receiver.sendMessage(
				MiniMessage.miniMessage().deserialize(
						pl.cfgm.getLanguage().getString(lang_key_prefix+"."+language_key)
				)
		);
	}

	public static void sendMessage(CommandSender receiver, String language_key, Map<String, String> replace) {
		sendMessage(receiver, language_key, replace, "messages");
	}

	public static void sendErrMessage(CommandSender receiver, String language_key, Map<String, String> replace) {
		sendMessage(receiver, language_key, replace, "errors");
	}

	private static void sendMessage(CommandSender receiver, String language_key, Map<String, String> replace, String lang_key_prefix) {
		receiver.sendMessage(getMessage(language_key, replace, lang_key_prefix));
	}

	public static void broadcastRawMessage(String message) {
		Component messageComponent = MiniMessage.miniMessage().deserialize(message);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(messageComponent);
		}
		Bukkit.getConsoleSender().sendMessage(messageComponent);
	}

	public static void broadcastRawMessage(String message, Map<String, String> replace) {
		for (Map.Entry<String, String> replaceEntry : replace.entrySet()) {
			message = message.replace("{"+replaceEntry.getKey()+"}", replaceEntry.getValue());
		}
		broadcastRawMessage(message);
	}

	public static void broadcastMessage(String language_key) {
		broadcastMessage(pl.cfgm.getLanguage().getString(language_key), "messages");
	}

	public static void broadcastErrMessage(String language_key) {
		broadcastMessage(pl.cfgm.getLanguage().getString(language_key), "errors");
	}

	private static void broadcastMessage(String language_key, String lang_key_prefix) {
		broadcastRawMessage(pl.cfgm.getLanguage().getString(lang_key_prefix+"."+language_key));
	}

	public static void broadcastMessage(String language_key, Map<String, String> replace) {
		broadcastMessage(pl.cfgm.getLanguage().getString(language_key), replace, "messages");
	}

	public static void broadcastErrMessage(String language_key, Map<String, String> replace) {
		broadcastMessage(pl.cfgm.getLanguage().getString(language_key), replace, "errors");
	}

	private static void broadcastMessage(String language_key, Map<String, String> replace, String lang_key_prefix) {
		broadcastRawMessage(lang_key_prefix+"."+pl.cfgm.getLanguage().getString(language_key), replace);
	}

	public static Component getMessage(String language_key) {
		return getMessage(language_key, "messages");
	}

	public static Component getErrMessage(String language_key) {
		return getMessage(language_key, "errors");
	}

	public static Component getMessage(String language_key, String lang_key_prefix) {
		return MiniMessage.miniMessage().deserialize(pl.cfgm.getLanguage().getString(lang_key_prefix+"."+language_key));
	}

	public static Component getMessage(String language_key, Map<String, String> replace) {
		return getMessage(language_key, replace, "messages");
	}

	public static Component getErrMessage(String language_key, Map<String, String> replace) {
		return getMessage(language_key, replace, "errors");
	}

	public static Component getMessage(String language_key, Map<String, String> replace, String lang_key_prefix) {
		String message = pl.cfgm.getLanguage().getString(lang_key_prefix+"."+language_key);
		for (Map.Entry<String, String> replaceEntry : replace.entrySet()) {
			message = message.replace("{"+replaceEntry.getKey()+"}", replaceEntry.getValue());
		}
		return MiniMessage.miniMessage().deserialize(message);
	}

	public static String getRawMessage(String language_key) {
		return getRawMessage(language_key, Map.of(), "messages");
	}

	public static String getRawErrMessage(String language_key) {
		return getRawMessage(language_key, Map.of(), "errors");
	}

	public static String getRawMessage(String language_key, Map<String, String> replace) {
		return getRawMessage(language_key, replace, "messages");
	}

	public static String getRawErrMessage(String language_key, Map<String, String> replace) {
		return getRawMessage(language_key, replace, "errors");
	}

	public static String getRawMessage(String language_key, Map<String, String> replace, String lang_key_prefix) {
		String message = pl.cfgm.getLanguage().getString(lang_key_prefix+"."+language_key);
		for (Map.Entry<String, String> replaceEntry : replace.entrySet()) {
			message = message.replace("{"+replaceEntry.getKey()+"}", replaceEntry.getValue());
		}
		return message;
	}

	/**
	 * Adds "," and "and" between words
	 * @param words List of words that should be formatted.
	 * @return A string with "," between the words and "and" between the last 2 words. Returns no "and" or comma if one word is given.
	 * */
	public static String addCommasAndAnds(List<String> words) {
		return addCommasAndAnds(words, null);
	}

	/**
	 * Adds "," and "and" between words
	 * @param words List of words that should be formatted.
	 * @param color The color words should be
	 * @return A string with "," between the words and "and" between the last 2 words. Returns no "and" or comma if one word is given.
	 */
	public static String addCommasAndAnds(List<String> words, String color) {
		if(words.size() == 0)
			return "";
		if(words.size() == 1) {
			if(color == null)
				return words.get(0);
			return "<"+color+">"+words.get(0)+"</"+color+">";
		}

		StringBuilder formattedString = new StringBuilder();
		int numOfTokens = words.size();
		for (int i = 0; i < numOfTokens -2; i++) {
			if(color != null) {
				formattedString.append("<").append(color).append(">").append(words.get(i)).append("</").append(color).append(">").append(", ");
			} else {
				formattedString.append(words.get(i)).append(", ");
			}
		}

		if(color != null) {
			formattedString
					.append("<").append(color).append(">").append(words.get(numOfTokens - 2)).append("</").append(color).append(">")
					.append(" ").append(pl.cfgm.getLanguage().getString("and")).append(" ")
					.append("<").append(color).append(">").append(words.get(numOfTokens - 1)).append("</").append(color).append(">");
		} else {
			formattedString.append(words.get(numOfTokens - 2))
					.append(" ").append(pl.cfgm.getLanguage().getString("and")).append(" ")
					.append(words.get(numOfTokens - 1));
		}
		return formattedString.toString();
	}
}
