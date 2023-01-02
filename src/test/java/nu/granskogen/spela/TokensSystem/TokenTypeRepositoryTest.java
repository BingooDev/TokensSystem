package nu.granskogen.spela.TokensSystem;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeDoesntExist;
import nu.granskogen.spela.TokenSystem.token.Token;
import nu.granskogen.spela.TokenSystem.token.TokenRepository;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTypeRepositoryTest {
	private static HikariDataSource dataSource = TestUtilities.getDataSource();
	private TokenTypeRepository tokenTypeRepository;

	@BeforeEach
	void setUp() throws SQLException {
		TestUtilities.resetDatabase(dataSource);
		tokenTypeRepository = new TokenTypeRepository(dataSource);
	}

	@AfterEach
	public void tearDown()
	{
		MockBukkit.unmock();
	}

	@Test
	void canCreateTokenTypeTest() {
		assertDoesNotThrow(() -> tokenTypeRepository.createTokenType("aName", "a name"));
	}

	@Test
	void cantCreateSameNameMultipleTimes() {
		assertThrows(TokenTypeAlreadyExists.class, () -> {
			tokenTypeRepository.createTokenType("aName");
			tokenTypeRepository.createTokenType("aName");
		});
		assertThrows(TokenTypeAlreadyExists.class, () -> {
			tokenTypeRepository.createTokenType("aName", "a name");
		});
	}

	@Test
	void canGetTokenTypeById() throws SQLException, FailedCratingTokenType, TokenTypeAlreadyExists {
		int id = tokenTypeRepository.createTokenType("aName");
		TokenType tokenType = tokenTypeRepository.getTokenTypeById(id);

		assertEquals(tokenType, new TokenType(id, "aName"));

		int id2 = tokenTypeRepository.createTokenType("aName2", "a name 2");
		TokenType tokenType2 = tokenTypeRepository.getTokenTypeById(id2);

		assertEquals(tokenType2, new TokenType(id2, "aName2", "a name 2"));
	}

	@Test
	void canGetTokenTypeByName() throws SQLException, FailedCratingTokenType, TokenTypeAlreadyExists {
		int id1 = tokenTypeRepository.createTokenType("something");
		int id2 = tokenTypeRepository.createTokenType("something2", "Something 2");
		TokenType tokenType1 = tokenTypeRepository.getTokenTypeByName("something");
		TokenType tokenType2 = tokenTypeRepository.getTokenTypeByName("something2");

		assertEquals(tokenType1, new TokenType(id1, "something"));
		assertEquals(tokenType1, new TokenType(id1, "SOMETHING"));
		assertEquals(tokenType2, new TokenType(id2, "something2", "Something 2"));
	}

	@Test
	void cantGetNonexistentToken() {
		assertNull(tokenTypeRepository.getTokenTypeById(0));
	}

	@Test
	void tokenTypeNameCantIncludeSpace() {
		assertThrows(IllegalArgumentException.class, () -> tokenTypeRepository.createTokenType("a name"));
	}

	@Test
	void tokenTypeDisplayNameCantIncludeIllegalCharacters() {
		assertThrows(IllegalArgumentException.class, () -> tokenTypeRepository.createTokenType("valid", "a name§"));
		System.out.println(tokenTypeRepository.getTokenTypeByName("valid"));
		assertNull(tokenTypeRepository.getTokenTypeByName("valid"));
		// check not throwing TokenTypeAlreadyExists
		assertDoesNotThrow(() -> tokenTypeRepository.createTokenType("valid"));

		assertThrows(IllegalArgumentException.class, () -> tokenTypeRepository.createTokenType("valid", "nam%e"));
	}

	@Test
	void canLoadTokenTypesFromDatabase() throws SQLException, FailedCratingTokenType, TokenTypeAlreadyExists {
		// create tokenTypes
		tokenTypeRepository.createTokenType("one");
		tokenTypeRepository.createTokenType("two", "TWO TWO");

		// create new repository, should load from databasen when constructed
		tokenTypeRepository = new TokenTypeRepository(dataSource);
		assertEquals(new TokenType(1, "one"), tokenTypeRepository.getTokenTypeByName("one"));
		assertEquals(new TokenType(2, "two", "TWO TWO"), tokenTypeRepository.getTokenTypeByName("two"));
	}

	@Test
	void canUpdateTokenType() throws TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		int id = tokenTypeRepository.createTokenType("one");
		TokenType tokenType = tokenTypeRepository.getTokenTypeById(id);
		tokenType.setName("new");
		assertDoesNotThrow(() -> {
			tokenTypeRepository.updateTokenType(tokenType);
		});
		assertEquals(new TokenType(1, "new", "one"), tokenTypeRepository.getTokenTypeById(tokenType.getId()));

		// check updated after reload from database
		tokenTypeRepository = new TokenTypeRepository(dataSource);
		assertEquals(new TokenType(1, "new", "one"), tokenTypeRepository.getTokenTypeById(tokenType.getId()));

		// check update of name and display name
		tokenType.setName("new2");
		tokenType.setDisplayName("a new display name");
		assertDoesNotThrow(() -> {
			tokenTypeRepository.updateTokenType(tokenType);
		});
		assertEquals(new TokenType(1, "new2", "a new display name"), tokenTypeRepository.getTokenTypeById(tokenType.getId()));

		// check updated after reload from database
		tokenTypeRepository = new TokenTypeRepository(dataSource);
		assertEquals(new TokenType(1, "new2", "a new display name"), tokenTypeRepository.getTokenTypeById(tokenType.getId()));
	}

	@Test
	void canDeleteTokenType() throws TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		int id = tokenTypeRepository.createTokenType("one");
		TokenType tokenType = tokenTypeRepository.getTokenTypeById(id);

		assertNotNull(tokenTypeRepository.getTokenTypeById(tokenType.getId()));
		tokenTypeRepository.delete(tokenType);
		assertNull(tokenTypeRepository.getTokenTypeById(tokenType.getId()));

		// check deleted after reload from database
		tokenTypeRepository = new TokenTypeRepository(dataSource);
		assertNull(tokenTypeRepository.getTokenTypeById(tokenType.getId()));
	}

	@Test
	void checkTokenTypeExists() throws TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		int id = tokenTypeRepository.createTokenType("test");
		assertTrue(tokenTypeRepository.exists(id));
		assertTrue(tokenTypeRepository.exists("test"));
		assertTrue(tokenTypeRepository.exists("TEST"));

		assertFalse(tokenTypeRepository.exists(id+1));
		assertFalse(tokenTypeRepository.exists("test2"));
	}

	@Test
	void canDeleteTokenTypeIfUsersHaveTokenType() {
		TokenType tokenType = null;
		try {
			int id = tokenTypeRepository.createTokenType("one");
			tokenType = tokenTypeRepository.getTokenTypeById(id);
			TokenRepository tokenRepository = new TokenRepository(dataSource, tokenTypeRepository);

			// Add user data associated with token type
			tokenRepository.updateToken(UUID.randomUUID(), new Token(tokenType, 42));
		} catch (SQLException | FailedCratingTokenType | TokenTypeAlreadyExists | TokenTypeDoesntExist ignored) {}


		TokenType finalTokenType = tokenType;
		assertDoesNotThrow(() -> tokenTypeRepository.delete(finalTokenType));
	}
}
