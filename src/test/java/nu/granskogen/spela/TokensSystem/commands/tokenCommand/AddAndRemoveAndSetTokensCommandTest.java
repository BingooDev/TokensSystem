package nu.granskogen.spela.TokensSystem.commands.tokenCommand;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.MessageUtil;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
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

public class AddAndRemoveAndSetTokensCommandTest {
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
	void accessDeniedAddTest() {
		accessDeniedTest("add");
	}

	@Test
	void accessDeniedRemoveTest() {
		accessDeniedTest("remove");
	}

	@Test
	void accessDeniedSetTest() {
		accessDeniedTest("set");
	}

	void accessDeniedTest(String cmd) {
		PlayerMock player = server.addPlayer();
		player.performCommand("tokens "+cmd+" something Bingoo 10");
		player.assertSaid(MessageUtil.getErrMessage("accessDenied"));
		player.assertNoMoreSaid();
	}

	@Test
	void canCreateTokensOnAddTest() throws InterruptedException {
		canCreateTokensTest("add", 10, 10);
	}

	@Test
	void canCreateTokensOnRemoveTest() throws InterruptedException {
		canCreateTokensTest("remove", 10, -10);
	}

	@Test
	void canCreateTokensOnSetTest() throws InterruptedException {
		canCreateTokensTest("set", 10, 10);
	}

	// Should create token if player has no tokens of specified type
	void canCreateTokensTest(String cmd, int change, int newAmount) throws InterruptedException {
		// Creating token type, same as /tokens create something
		TokenType tokenType = createTokenType("something", "SomeThing");

		PlayerMock player = server.addPlayer("Bingoo");
		player.setOp(true);
		player.performCommand("tokens "+cmd+" something Bingoo "+change);

		// Sleeping because command is performed async
		Thread.sleep(500);
		assertEquals(new Token(tokenType, newAmount), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));
		player.assertSaid(MessageUtil.getMessage(cmd+"Tokens", Map.of(
				"player", "Bingoo",
				"tokenType", tokenType.getDisplayName(),
				"amount", ""+change,
				"sum", ""+newAmount)
		));
		player.assertSaid(MessageUtil.getMessage(cmd+"TokensTarget", Map.of(
				"tokenType", tokenType.getDisplayName(),
				"amount", ""+change,
				"sum", ""+newAmount)
		));
		player.assertNoMoreSaid();
	}

	@Test
	void canAddTokensToOfflinePlayers() throws InterruptedException {
		canAddOrRemoveTokensToOfflinePlayers("add", 10, 10);
	}

	@Test
	void canRemoveTokensToOfflinePlayers() throws InterruptedException {
		canAddOrRemoveTokensToOfflinePlayers("remove", 10, -10);
	}

	@Test
	void canSetTokensForOfflinePlayers() throws InterruptedException {
		canAddOrRemoveTokensToOfflinePlayers("set", 10, 10);
	}

	void canAddOrRemoveTokensToOfflinePlayers(String cmd, int change, int newAmount) throws InterruptedException {
		TokenType tokenType = createTokenType("something", "SomeThing");

		PlayerMock player = server.addPlayer("Bingoo");
		player.setOp(true);

		player.performCommand("tokens "+cmd+" something BingooDev "+change);

		// Sleeping because command is performed async
		Thread.sleep(500);
		OfflinePlayer offlinePlayer = server.getOfflinePlayer("BingooDev");

		assertEquals(new Token(tokenType, newAmount), plugin.getTokenRepository().getToken(tokenType, offlinePlayer.getUniqueId()));
	}

	@Test
	void canUpdateTokenThroughAddTest() throws InterruptedException {
		// Creating token type, same as /tokens create something
		TokenType tokenType = createTokenType("something", "SomeThing");

		PlayerMock player = server.addPlayer("Bingoo");
		player.setOp(true);
		player.performCommand("tokens add something Bingoo 10");

		Thread.sleep(500);
		assertEquals(new Token(tokenType, 10), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));

		player.performCommand("tokens add something Bingoo 20");

		Thread.sleep(500);
		assertEquals(new Token(tokenType, 30), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));

		player.performCommand("tokens add something Bingoo 20");
		player.performCommand("tokens add something Bingoo 20");

		Thread.sleep(500);
		assertEquals(new Token(tokenType, 70), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));
	}

	@Test
	void canUpdateTokenThroughRemoveTest() throws InterruptedException {
		// Creating token type, same as /tokens create something
		TokenType tokenType = createTokenType("something", "SomeThing");

		PlayerMock player = server.addPlayer("Bingoo");
		player.setOp(true);
		player.performCommand("tokens remove something Bingoo 10");

		Thread.sleep(500);
		assertEquals(new Token(tokenType, -10), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));

		player.performCommand("tokens remove something Bingoo 20");

		Thread.sleep(500);
		assertEquals(new Token(tokenType, -30), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));

		player.performCommand("tokens remove something Bingoo 20");
		player.performCommand("tokens remove something Bingoo 20");

		Thread.sleep(500);
		assertEquals(new Token(tokenType, -70), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));
	}

	@Test
	void canUpdateTokenThroughSetTest() throws InterruptedException {
		// Creating token type, same as /tokens create something
		TokenType tokenType = createTokenType("something", "SomeThing");

		PlayerMock player = server.addPlayer("Bingoo");
		player.setOp(true);
		player.performCommand("tokens set something Bingoo 10");

		Thread.sleep(500);
		assertEquals(new Token(tokenType, 10), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));

		player.performCommand("tokens set something Bingoo 20");

		Thread.sleep(500);
		assertEquals(new Token(tokenType, 20), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));

		player.performCommand("tokens set something Bingoo 20");
		player.performCommand("tokens set something Bingoo 20");

		Thread.sleep(500);
		assertEquals(new Token(tokenType, 20), plugin.getTokenRepository().getToken(tokenType, player.getUniqueId()));
	}

	@Test
	void cantAddToNonExistentTokenType() throws InterruptedException {
		cantAddOrRemoveToNonExistentTokenType("add");
	}

	@Test
	void cantRemoveFromNonExistentTokenType() throws InterruptedException {
		cantAddOrRemoveToNonExistentTokenType("remove");
	}

	@Test
	void cantSetNonExistentTokenType() throws InterruptedException {
		cantAddOrRemoveToNonExistentTokenType("set");
	}

	void cantAddOrRemoveToNonExistentTokenType(String cmd) throws InterruptedException {
		PlayerMock player = server.addPlayer("Bingoo");
		player.setOp(true);
		player.performCommand("tokens "+cmd+" something Bingoo 10");

		// Sleeping because command is performed async
		Thread.sleep(500);
		player.assertSaid(MessageUtil.getErrMessage("tokenTypeDoesntExist", Map.of("tokenType", "something")));
		player.assertNoMoreSaid();
	}

	@Test
	void cantAddInvalidAmount() throws InterruptedException, TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		cantUpdateWithInvalidAmount("add");
	}

	@Test
	void cantRemoveInvalidAmount() throws InterruptedException, TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		cantUpdateWithInvalidAmount("remove");
	}

	@Test
	void cantSetInvalidAmount() throws InterruptedException, TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		cantUpdateWithInvalidAmount("set");
	}

	void cantUpdateWithInvalidAmount(String cmd) throws InterruptedException, TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		// need to create tokentype in order to not get error when executing command
		tokenTypeRepository.createTokenType("something");

		PlayerMock player = server.addPlayer("Bingoo");
		player.setOp(true);
		player.performCommand("tokens "+cmd+" something Bingoo abc");

		// Sleeping because command is performed async
		Thread.sleep(500);
		player.assertSaid(MessageUtil.getErrMessage("invalidAmount"));
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
