package nu.granskogen.spela.TokensSystem.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokensSystem.TestUtilities;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TokenCommandTest {
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
	void shows_usage_message_if_no_args_specified() {
		PlayerMock player = server.addPlayer();
		player.setOp(true);
		player.performCommand("tokens");
		player.assertSaid("§7Syntax: /tokens list");
		player.assertSaid("§7Syntax: /tokens create <namn>");
		player.assertSaid("§7Syntax: /tokens delete <namn>");
		player.assertSaid("§7Syntax: /tokens amount <tokentyp> <spelare>");
		player.assertSaid("§7Syntax: /tokens add <tokentyp> <spelare> <summa>");
		player.assertSaid("§7Syntax: /tokens remove <tokentyp> <spelare> <summa>");
		player.assertSaid("§7Syntax: /tokens set <tokentyp> <spelare> <summa>");
		player.assertNoMoreSaid();
	}
}
