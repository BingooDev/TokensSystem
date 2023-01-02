package nu.granskogen.spela.TokensSystem;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import nu.granskogen.spela.TokenSystem.ConfigManager;
import nu.granskogen.spela.TokenSystem.Main;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class ConfigManagerTest {
	private ServerMock server;
	private Main plugin;

	private ConfigManager cfgm;

	@BeforeEach
	void setup() throws InvalidDescriptionException, IOException {
		server = MockBukkit.mock();
		plugin = TestUtilities.getPlugin(server);
		cfgm = new ConfigManager(plugin.getDataFolder(), true);
	}

	@AfterEach
	void tearDown()
	{
		MockBukkit.unmock();
	}

	@Test
	void canGetConfig() {
		assertNotNull(cfgm.getConfig());
	}

	@Test
	void canGetLanguage() {
		assertNotNull(cfgm.getLanguage());
	}

	@Test
	void canGetDataFromConfig() {
		assertEquals("localhost", cfgm.getConfig().getString("database.host"));
	}

	@Test
	void canGetDataFromLanguage() {
		assertEquals("<red>Åtkomst nekad!", cfgm.getLanguage().getString("errors.accessDenied"));
	}
}
