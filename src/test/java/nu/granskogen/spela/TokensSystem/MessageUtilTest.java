package nu.granskogen.spela.TokensSystem;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.minimessage.MiniMessage;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.MessageUtil;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MessageUtilTest {
	private ServerMock server;
	private Main plugin;

	@BeforeEach
	void setup() throws InvalidDescriptionException, IOException {
		server = MockBukkit.mock();
		plugin = TestUtilities.getPlugin(server);
	}

	@AfterEach
	void tearDown()
	{
		MockBukkit.unmock();
	}

	@Test
	void canGetErrMessage() {
		assertEquals(MiniMessage.miniMessage().deserialize("<red>Åtkomst nekad!"), MessageUtil.getErrMessage("accessDenied"));
	}

	@Test
	void canReplaceMessage() {
		String player = "Bingoo";
		String amount = "10";
		String tokenType = "VoteToken";

		Component expected = MiniMessage.miniMessage().deserialize("<green><dark_green>"+player+"</dark_green> har <dark_green>"+amount+" "+tokenType+"s</dark_green> kvar.");
		Component actual = MessageUtil.getMessage("tokensLeft", Map.of("player", player, "amount", amount, "tokenType", tokenType));

		assertEquals(expected, actual);
	}

	@Test
	void canSendErrMessage() {
		PlayerMock player = server.addPlayer();
		MessageUtil.sendErrMessage(player, "accessDenied");
		player.assertSaid(MiniMessage.miniMessage().deserialize("<red>Åtkomst nekad!"));
	}

	@Test
	void treatsRegexCharactersAsLiterals() {
		String player = "Bingoo$";
		String amount = "^10";
		String tokenType = "VoteToken\"";

		Component expected = MiniMessage.miniMessage().deserialize("<green><dark_green>"+player+"</dark_green> har <dark_green>"+amount+" "+tokenType+"s</dark_green> kvar.");
		Component actual = MessageUtil.getMessage("tokensLeft", Map.of("player", player, "amount", amount, "tokenType", tokenType));

		Component expected2 = MiniMessage.miniMessage().deserialize("<red>TokenTypen <dark_red>"+tokenType+"</dark_red> finns redan.");
		Component actual2 = MessageUtil.getErrMessage("tokenTypeExists", Map.of( "tokenType", tokenType));

		assertEquals(expected, actual);
		assertEquals(expected2, actual2);
	}

	@Test
	void canAddCommasAndAnds() {
		String expected = "a, b, c "+ plugin.cfgm.getLanguage().getString("and")+" something";
		String actual = MessageUtil.addCommasAndAnds(List.of("a","b","c","something"));
		assertEquals(expected, actual);
	}

	@Test
	void canAddCommasAndAndsWithColor() {
		String expected = "<dark_green>a</dark_green>, <dark_green>b</dark_green>, <dark_green>c</dark_green> "+ plugin.cfgm.getLanguage().getString("and")+" <dark_green>something</dark_green>";
		String actual = MessageUtil.addCommasAndAnds(List.of("a","b","c","something"), "dark_green");
		assertEquals(expected, actual);

		String expected2 = "<dark_green>a</dark_green>";
		String actual2 = MessageUtil.addCommasAndAnds(List.of("a"), "dark_green");
		assertEquals(expected2, actual2);
	}

	@Test
	void canGetRawMessage() {
		String tokenType = "VoteToken";

		String expected1 = "<red>Åtkomst nekad!";
		String actual1 = MessageUtil.getRawErrMessage("accessDenied");
		assertEquals(expected1, actual1);

		String expected2 = "<red>TokenTypen <dark_red>"+tokenType+"</dark_red> finns redan.";
		String actual2 = MessageUtil.getRawErrMessage("tokenTypeExists", Map.of("tokenType", tokenType));
		assertEquals(expected2, actual2);
	}
}
