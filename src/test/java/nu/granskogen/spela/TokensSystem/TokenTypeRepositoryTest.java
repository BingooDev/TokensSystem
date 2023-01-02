package nu.granskogen.spela.TokensSystem;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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
		assertDoesNotThrow(() -> tokenTypeRepository.createTokenType("aName"));
	}

	@Test
	void cantCreateSameNameMultipleTimes() {
		assertThrows(TokenTypeAlreadyExists.class, () -> {
			tokenTypeRepository.createTokenType("aName");
			tokenTypeRepository.createTokenType("aName");
		});
	}

	@Test
	void canGetTokenTypeById() throws SQLException, FailedCratingTokenType, TokenTypeAlreadyExists {
		int id = tokenTypeRepository.createTokenType("aName");
		TokenType tokenType = tokenTypeRepository.getTokenTypeById(id);

		assertEquals(tokenType, new TokenType(1, "aName"));
	}

	@Test
	void canGetTokenTypeByName() throws SQLException, FailedCratingTokenType, TokenTypeAlreadyExists {
		tokenTypeRepository.createTokenType("something");
		TokenType tokenType = tokenTypeRepository.getTokenTypeByName("something");

		assertEquals(tokenType, new TokenType(1, "something"));
		assertEquals(tokenType, new TokenType(1, "Something"));
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
	void canLoadTokenTypesFromDatabase() throws SQLException, FailedCratingTokenType, TokenTypeAlreadyExists {
		// create tokenTypes
		tokenTypeRepository.createTokenType("one");
		tokenTypeRepository.createTokenType("two");

		// create new repository, should load from databasen when constructed
		tokenTypeRepository = new TokenTypeRepository(dataSource);
		assertEquals(new TokenType(1, "one"), tokenTypeRepository.getTokenTypeByName("one"));
		assertEquals(new TokenType(2, "two"), tokenTypeRepository.getTokenTypeByName("two"));
	}

	@Test
	void canUpdateTokenType() throws TokenTypeAlreadyExists, SQLException, FailedCratingTokenType {
		int id = tokenTypeRepository.createTokenType("one");
		TokenType tokenType = tokenTypeRepository.getTokenTypeById(id);
		tokenType.setName("new");
		assertDoesNotThrow(() -> {
			tokenTypeRepository.updateTokenType(tokenType);
			assertEquals(new TokenType(1, "new"), tokenTypeRepository.getTokenTypeById(tokenType.getId()));
		});

		// check updated after reload from database
		tokenTypeRepository = new TokenTypeRepository(dataSource);
		assertEquals(new TokenType(1, "new"), tokenTypeRepository.getTokenTypeById(tokenType.getId()));
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
}
