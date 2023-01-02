package nu.granskogen.spela.TokensSystem.commands.tokenCommand;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.MessageUtil;
import nu.granskogen.spela.TokensSystem.TestUtilities;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class CreateTokenCommandTest {

	private static HikariDataSource dataSource = TestUtilities.getDataSource();
	private ServerMock server;
	private Main plugin;

	@BeforeEach
	void setup() throws InvalidDescriptionException, IOException, SQLException {
		TestUtilities.resetDatabase(dataSource);
		server = MockBukkit.mock();
		plugin = TestUtilities.getPlugin(server);
	}

	@AfterEach
	void tearDown()
	{
		MockBukkit.unmock();
	}

	@Test
	void accessDeniedTest() {
		PlayerMock player = server.addPlayer();
		player.performCommand("tokens create abc");
		player.assertSaid(MessageUtil.getErrMessage("accessDenied"));
		player.assertNoMoreSaid();
	}

	@Test
	void canCreateTokensTest() throws InterruptedException {
		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens create abc");

		// Sleeping because command is performed async
		Thread.sleep(500);
		assertNotNull(plugin.getTokenTypeRepository().getTokenTypeByName("abc"));
		player.assertSaid(MessageUtil.getMessage("createdTokenType", Map.of("tokenType", "abc", "displayName", "abc")));
		player.assertNoMoreSaid();
	}

	@Test
	void canCreateTokenWithDisplayNameTest() throws InterruptedException {
		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens create abc AbcToken");

		// Sleeping because command is performed async
		Thread.sleep(500);
		assertNotNull(plugin.getTokenTypeRepository().getTokenTypeByName("abc"));
		player.assertSaid(MessageUtil.getMessage("createdTokenType", Map.of("tokenType", "abc", "displayName", "AbcToken")));
		player.assertNoMoreSaid();
	}

	@Test
	void cantCreateSameTokenTypeMultipleTimes() throws InterruptedException {
		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens create abc");

		// Sleeping because command is performed async
		Thread.sleep(500);
		assertNotNull(plugin.getTokenTypeRepository().getTokenTypeByName("abc"));
		player.assertSaid(MessageUtil.getMessage("createdTokenType", Map.of("tokenType", "abc", "displayName", "abc")));

		player.performCommand("tokens create ABC");
		// Sleeping because command is performed async
		Thread.sleep(500);
		player.assertSaid(MessageUtil.getErrMessage("tokenTypeExists", Map.of("tokenType", "abc")));
		player.assertNoMoreSaid();

		// Testing with display name
		player.performCommand("tokens create abc NEW");
		// Sleeping because command is performed async
		Thread.sleep(500);
		player.assertSaid(MessageUtil.getErrMessage("tokenTypeExists", Map.of("tokenType", "abc")));
		player.assertNoMoreSaid();
	}

	@Test
	void cantUseIllegalCharacters() throws InterruptedException {
		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens create abc$");

		// Sleeping because command is performed async
		Thread.sleep(500);
		assertNull(plugin.getTokenTypeRepository().getTokenTypeByName("abc$"));
		player.assertSaid(MessageUtil.getErrMessage("tokenTypeIllegalCharacters"));
		player.assertNoMoreSaid();

		// Testing display name
		player.performCommand("tokens create abc A%BC");

		// Sleeping because command is performed async
		Thread.sleep(500);
		assertNull(plugin.getTokenTypeRepository().getTokenTypeByName("abc"));
		player.assertSaid(MessageUtil.getErrMessage("tokenTypeIllegalCharacters"));
		player.assertNoMoreSaid();
	}
}
