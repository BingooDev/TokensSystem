package nu.granskogen.spela.TokensSystem;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeDoesntExist;
import nu.granskogen.spela.TokenSystem.token.Token;
import nu.granskogen.spela.TokenSystem.token.TokenRepository;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenRepositoryTest {
	private static HikariDataSource dataSource = TestUtilities.getDataSource();
	private TokenTypeRepository tokenTypeRepository;
	private TokenRepository tokenRepository;

	@BeforeEach
	void setUp() throws SQLException {
		TestUtilities.resetDatabase(dataSource);
		tokenTypeRepository = new TokenTypeRepository(dataSource);
		tokenRepository = new TokenRepository(dataSource, tokenTypeRepository);
	}

	@AfterEach
	public void tearDown()
	{
		MockBukkit.unmock();
	}

	@Test
	void canCreateTokenTest() {
		TokenType tokenType = null;
		try {
			int id = tokenTypeRepository.createTokenType("one");
			tokenType = tokenTypeRepository.getTokenTypeById(id);
		} catch (SQLException | FailedCratingTokenType | TokenTypeAlreadyExists ignored) {}

		// Update token is used both for updating and creating new tokens
		TokenType finalTokenType = tokenType;
		UUID uuid = UUID.randomUUID();
		assertDoesNotThrow(() -> tokenRepository.updateToken(uuid, new Token(finalTokenType, 0)));
	}

	@Test
	void canGetTokenByTokenTypeAndUUID() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		TokenType tokenType1 = null;
		TokenType tokenType2 = null;
		try {
			int id1 = tokenTypeRepository.createTokenType("one");
			int id2 = tokenTypeRepository.createTokenType("two");
			tokenType1 = tokenTypeRepository.getTokenTypeById(id1);
			tokenType2 = tokenTypeRepository.getTokenTypeById(id2);
			tokenRepository.updateToken(uuid1, new Token(tokenType1, 10));
			tokenRepository.updateToken(uuid2, new Token(tokenType1, 50));
			tokenRepository.updateToken(uuid2, new Token(tokenType2, 10));
		} catch (SQLException | FailedCratingTokenType | TokenTypeAlreadyExists | TokenTypeDoesntExist ignored) {}

		// Update token is used both for updating and creating new tokens
		assertEquals(new Token(tokenType1, 10), tokenRepository.getToken(tokenType1, uuid1));
		assertNotEquals(tokenRepository.getToken(tokenType1, uuid1), tokenRepository.getToken(tokenType1, uuid2));
		assertNotEquals(tokenRepository.getToken(tokenType1, uuid1), tokenRepository.getToken(tokenType2, uuid1));
	}

	@Test
	void cantGetNonExistentToken() {
		TokenType tokenType = null;
		UUID uuid = UUID.randomUUID();
		try {
			int id = tokenTypeRepository.createTokenType("one");
			tokenType = tokenTypeRepository.getTokenTypeById(id);
		} catch (SQLException | FailedCratingTokenType | TokenTypeAlreadyExists ignored) {}

		assertNull(tokenRepository.getToken(new TokenType(2, "non_existent"), uuid));
	}

	@Test
	void getsTokenObjectEvenIfPlayerDoesntExistInDatabase() {
		TokenType tokenType = null;
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		try {
			int id = tokenTypeRepository.createTokenType("one");
			tokenType = tokenTypeRepository.getTokenTypeById(id);
		} catch (SQLException | FailedCratingTokenType | TokenTypeAlreadyExists ignored) {}

		assertEquals(new Token(tokenType, 0), tokenRepository.getToken(tokenType, uuid1));
		assertEquals(new Token(tokenType, 0), tokenRepository.getToken(tokenType, uuid2));
	}

	@Test
	void canUpdateToken() {
		UUID uuid = UUID.randomUUID();
		TokenType tokenType = null;
		try {
			int id = tokenTypeRepository.createTokenType("one");
			tokenType = tokenTypeRepository.getTokenTypeById(id);
			tokenRepository.updateToken(uuid, new Token(tokenType, 10));
		} catch (SQLException | FailedCratingTokenType | TokenTypeAlreadyExists | TokenTypeDoesntExist ignored) {}

		// Update token
		Token token = tokenRepository.getToken(tokenType, uuid);
		token.addAmount(10);
		assertDoesNotThrow(() -> tokenRepository.updateToken(uuid, token));

		// check token
		assertEquals(new Token(tokenType, 20), tokenRepository.getToken(tokenType, uuid));

		// reload from database be creating new repository
		assertDoesNotThrow(() -> {
			tokenRepository = new TokenRepository(dataSource, tokenTypeRepository);
		});

		// check after database reload
		assertEquals(new Token(tokenType, 20), tokenRepository.getToken(tokenType, uuid));
	}

	@Test
	void canUpdateNonExistentTokenType() {
		TokenType nonExistentTokenType = new TokenType(1, "non_existent");
		Token token = new Token(nonExistentTokenType, 10);
		UUID uuid = UUID.randomUUID();

		assertThrows(TokenTypeDoesntExist.class, () -> tokenRepository.updateToken(uuid, token));
	}

	@Test
	void canLoadTokensFromDatabase() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		TokenType tokenType1 = null;
		TokenType tokenType2 = null;
		try {
			int id1 = tokenTypeRepository.createTokenType("one");
			int id2 = tokenTypeRepository.createTokenType("two");
			tokenType1 = tokenTypeRepository.getTokenTypeById(id1);
			tokenType2 = tokenTypeRepository.getTokenTypeById(id2);
			tokenRepository.updateToken(uuid1, new Token(tokenType1, 10));
			tokenRepository.updateToken(uuid2, new Token(tokenType1, 30));
			tokenRepository.updateToken(uuid1, new Token(tokenType2, 10));
		} catch (SQLException | FailedCratingTokenType | TokenTypeAlreadyExists | TokenTypeDoesntExist ignored) {}

		// reload from database be creating new repository
		assertDoesNotThrow(() -> {
			tokenRepository = new TokenRepository(dataSource, tokenTypeRepository);
		});
		assertEquals(new Token(tokenType1, 10), tokenRepository.getToken(tokenType1, uuid1));
		assertEquals(new Token(tokenType1, 30), tokenRepository.getToken(tokenType1, uuid2));
		assertEquals(new Token(tokenType2, 10), tokenRepository.getToken(tokenType2, uuid1));
	}

	@Test
	void loadsTokensFromDatabaseOnPluginEnable() {
		// Add data to database
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		TokenType tokenType1 = null;
		TokenType tokenType2 = null;
		try {
			int id1 = tokenTypeRepository.createTokenType("one");
			int id2 = tokenTypeRepository.createTokenType("two");
			tokenType1 = tokenTypeRepository.getTokenTypeById(id1);
			tokenType2 = tokenTypeRepository.getTokenTypeById(id2);
			tokenRepository.updateToken(uuid1, new Token(tokenType1, 10));
			tokenRepository.updateToken(uuid2, new Token(tokenType1, 30));
			tokenRepository.updateToken(uuid1, new Token(tokenType2, 10));
		} catch (SQLException | FailedCratingTokenType | TokenTypeAlreadyExists | TokenTypeDoesntExist ignored) {}

		ServerMock server = MockBukkit.mock();
		TokenType finalTokenType1 = tokenType1;
		TokenType finalTokenType2 = tokenType2;

		Main plugin = MockBukkit.load(Main.class);
		assertEquals(new Token(finalTokenType1, 10), plugin.getTokenRepository().getToken(finalTokenType1, uuid1));
		assertEquals(new Token(finalTokenType1, 30), plugin.getTokenRepository().getToken(finalTokenType1, uuid2));
		assertEquals(new Token(finalTokenType2, 10), plugin.getTokenRepository().getToken(finalTokenType2, uuid1));
	}
}
