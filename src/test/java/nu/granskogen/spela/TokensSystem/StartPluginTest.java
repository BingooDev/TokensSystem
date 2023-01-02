package nu.granskogen.spela.TokensSystem;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class StartPluginTest {

	private HikariDataSource dataSource;

	@BeforeEach
	void setUp() throws SQLException {
		dataSource = TestUtilities.getDataSource();
		TestUtilities.resetDatabase(dataSource);
	}

	@AfterEach
	public void tearDown()
	{
		MockBukkit.unmock();
	}

	@Test
	void createsTokenTypeRepositoryWhenEnabled() throws InvalidDescriptionException, IOException {
		ServerMock server = MockBukkit.mock();
		Main plugin = MockBukkit.load(Main.class);

		assertNotNull(plugin.getTokenTypeRepository());
	}

	@Test
	void createsTokenRepositoryWhenEnabled() throws InvalidDescriptionException, IOException {
		ServerMock server = MockBukkit.mock();
		Main plugin = MockBukkit.load(Main.class);

		assertNotNull(plugin.getTokenRepository());
	}

	@Test
	void tokenRepositoryLoadsFromDatabaseWhenPluginEnables() throws InvalidDescriptionException, IOException, TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		// populate database
		TokenTypeRepository tokenTypeRepository = new TokenTypeRepository(dataSource);
		tokenTypeRepository.createTokenType("something");

		// Load & Enable plugin
		ServerMock server = MockBukkit.mock();
		Main plugin = TestUtilities.getPlugin(server);

		// Check TokenRepository from plugin
		assertEquals(new TokenType(1, "something"), plugin.getTokenTypeRepository().getTokenTypeByName("something"));
	}
}
