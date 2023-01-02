package nu.granskogen.spela.TokensSystem.commands.tokenCommand;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.MessageUtil;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import nu.granskogen.spela.TokensSystem.TestUtilities;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ListTokensCommandTest {

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
		player.performCommand("tokens list");
		player.assertSaid(MessageUtil.getErrMessage("accessDenied"));
		player.assertNoMoreSaid();
	}

	@Test
	void canCreateTokensTest() throws InterruptedException, TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		plugin.getTokenTypeRepository().createTokenType("one");
		plugin.getTokenTypeRepository().createTokenType("two");
		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens list");
		player.assertSaid(MessageUtil.getMessage("listTokenTypes",
				Map.of("tokenTypesList", MessageUtil.addCommasAndAnds(List.of("one (one)", "two (two)"), "dark_green"))));
		player.assertNoMoreSaid();

		plugin.getTokenTypeRepository().createTokenType("three", "ThreeToken");
		player.performCommand("tokens list");

		player.assertSaid(MessageUtil.getMessage("listTokenTypes",
				Map.of("tokenTypesList", MessageUtil.addCommasAndAnds(List.of("one (one)", "two (two)", "three (ThreeToken)"), "dark_green"))));
		player.assertNoMoreSaid();
	}

	@Test
	void showsMessageWhenNoTokensExist() {
		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens list");
		player.assertSaid(MessageUtil.getMessage("listTokenTypes", Map.of("tokenTypesList", "<dark_green>" + plugin.cfgm.getLanguage().getString("nothing") + "</dark_green>")));
	}
}
