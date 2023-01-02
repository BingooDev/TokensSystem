package nu.granskogen.spela.TokensSystem.commands.tokenCommand;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.MessageUtil;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeDoesntExist;
import nu.granskogen.spela.TokenSystem.token.Token;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import nu.granskogen.spela.TokensSystem.TestUtilities;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AmountTokenCommandTest {

	private static HikariDataSource dataSource = TestUtilities.getDataSource();
	private ServerMock server;
	private Main plugin;

	private TokenTypeRepository tokenTypeRepository;

	@BeforeEach
	void setup() throws InvalidDescriptionException, IOException, SQLException {
		TestUtilities.resetDatabase(dataSource);
		server = MockBukkit.mock();
		plugin = TestUtilities.getPlugin(server);
		tokenTypeRepository = plugin.getTokenTypeRepository();
	}

	@AfterEach
	void tearDown()
	{
		MockBukkit.unmock();
	}

	@Test
	void accessDeniedTest() {
		PlayerMock player = server.addPlayer();
		player.performCommand("tokens amount abc");
		player.assertSaid(MessageUtil.getErrMessage("accessDenied"));
		player.assertNoMoreSaid();

		player.performCommand("tokens amount abc player");
		player.assertSaid(MessageUtil.getErrMessage("accessDenied"));
		player.assertNoMoreSaid();
	}

	@Test
	void cantGetAmountForInvalidTokenType() throws InterruptedException {
		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens amount abc");
		player.assertSaid(MessageUtil.getErrMessage("tokenTypeDoesntExist", Map.of("tokenType", "abc")));
		player.assertNoMoreSaid();

		player.performCommand("tokens amount abc player");
		player.assertSaid(MessageUtil.getErrMessage("tokenTypeDoesntExist", Map.of("tokenType", "abc")));
		player.assertNoMoreSaid();
	}

	@Test
	void canGetAmountSelf() throws TokenTypeDoesntExist, SQLException {
		TokenType tokenType = createTokenType("something", "SomeThing");

		PlayerMock player = server.addPlayer();
		player.setOp(true);

		plugin.getTokenRepository().updateToken(player.getUniqueId(), new Token(tokenType, 42));

		player.performCommand("tokens amount " + tokenType.getName());
		player.assertSaid(MessageUtil.getMessage("amountSelf", Map.of("amount", "42", "tokenType", tokenType.getDisplayName())));
		player.assertNoMoreSaid();
	}

	@Test
	void canGetAmountTarget() throws TokenTypeDoesntExist, SQLException {
		TokenType tokenType = createTokenType("something", "SomeThing");

		PlayerMock player = server.addPlayer();
		player.setOp(true);
		OfflinePlayer offlinePlayer = server.getOfflinePlayer("BingooDev");

		plugin.getTokenRepository().updateToken(offlinePlayer.getUniqueId(), new Token(tokenType, 42));

		player.performCommand("tokens amount " + tokenType.getName() + " " + offlinePlayer.getName());
		player.assertSaid(MessageUtil.getMessage("amountTarget", Map.of("player", offlinePlayer.getName(), "amount", "42", "tokenType", tokenType.getDisplayName())));
		player.assertNoMoreSaid();

		// check console can get target amount
		ConsoleCommandSenderMock consoleSender = server.getConsoleSender();
		server.execute("tokens", consoleSender, "amount", tokenType.getName(), offlinePlayer.getName());
		// using plain text because command sender changes how MiniMessage tags are stored, will look the same on screen, but internal storage is changed.
		String actualPlainText = PlainTextComponentSerializer.plainText().serialize(consoleSender.nextComponentMessage());
		String expectedPlainText = PlainTextComponentSerializer.plainText().serialize(
				MessageUtil.getMessage("amountTarget",
						Map.of("player", offlinePlayer.getName(), "amount", "42", "tokenType", tokenType.getDisplayName())));
		assertEquals(expectedPlainText, actualPlainText);
		consoleSender.assertNoMoreSaid();
	}

	@Test
	void consoleCantRunAmountSelf() {
		TokenType tokenType = createTokenType("something", "SomeThing");
		ConsoleCommandSenderMock consoleSender = server.getConsoleSender();
		server.execute("tokens", consoleSender, "amount", tokenType.getName());
		consoleSender.assertSaid(MessageUtil.getErrMessage("onlyPlayers"));
		consoleSender.assertNoMoreSaid();
	}

	@Test
	void canGetAmountTokenTypeCaseIgnore() throws TokenTypeDoesntExist, SQLException {
		TokenType tokenType = createTokenType("something", "SomeThing");

		PlayerMock player = server.addPlayer();
		player.setOp(true);
		OfflinePlayer offlinePlayer = server.getOfflinePlayer("BingooDev");

		plugin.getTokenRepository().updateToken(offlinePlayer.getUniqueId(), new Token(tokenType, 42));

		player.performCommand("tokens amount " + tokenType.getName().toUpperCase() + " " + offlinePlayer.getName());
		player.assertSaid(MessageUtil.getMessage("amountTarget", Map.of("player", offlinePlayer.getName(), "amount", "42", "tokenType", tokenType.getDisplayName())));
		player.assertNoMoreSaid();
	}

	private TokenType createTokenType(String name, String displayName) {
		// Creating token type, same as /tokens create something
		TokenType tokenType = null;
		try {
			int id = tokenTypeRepository.createTokenType(name, displayName);
			tokenType = tokenTypeRepository.getTokenTypeById(id);
		} catch (SQLException | FailedCratingTokenType | TokenTypeAlreadyExists ignored) {}
		return tokenType;
	}
}
