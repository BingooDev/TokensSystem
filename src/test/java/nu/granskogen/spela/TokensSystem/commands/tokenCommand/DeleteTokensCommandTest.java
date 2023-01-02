package nu.granskogen.spela.TokensSystem.commands.tokenCommand;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.MessageUtil;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import nu.granskogen.spela.TokensSystem.TestUtilities;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DeleteTokensCommandTest {

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
		player.performCommand("tokens delete abc");
		player.assertSaid(MessageUtil.getErrMessage("accessDenied"));
		player.assertNoMoreSaid();
	}

	@Test
	void canDeleteTokensTest() throws InterruptedException, TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		plugin.getTokenTypeRepository().createTokenType("one", "ONE");
		plugin.getTokenTypeRepository().createTokenType("two", "TWO");

		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens delete one");

		// Sleeping because command is performed async
		Thread.sleep(500);
		assertNull(plugin.getTokenTypeRepository().getTokenTypeByName("one"));
		player.assertSaid(MessageUtil.getMessage("deletedTokenType", Map.of("tokenType", "ONE")));
		player.assertNoMoreSaid();

		// Assert removed from list when deleted
		player.performCommand("tokens list");
		player.assertSaid(MessageUtil.getMessage("listTokenTypes",
				Map.of("tokenTypesList", MessageUtil.addCommasAndAnds(List.of("two (TWO)"), "dark_green"))));

		// Check one deleted from list when adding something new
		plugin.getTokenTypeRepository().createTokenType("three");
		player.performCommand("tokens list");
		player.assertSaid(MessageUtil.getMessage("listTokenTypes",
				Map.of("tokenTypesList", MessageUtil.addCommasAndAnds(List.of("two (TWO)", "three (three)"), "dark_green"))));
		player.assertNoMoreSaid();
	}

	@Test
	void cantDeleteNonExistentTokenType() throws InterruptedException {
		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens delete one");

		// Sleeping because command is performed async
		Thread.sleep(500);
		player.assertSaid(MessageUtil.getErrMessage("tokenTypeDoesntExist", Map.of("tokenType", "one")));
	}
}
