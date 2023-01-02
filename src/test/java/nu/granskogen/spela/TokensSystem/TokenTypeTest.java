package nu.granskogen.spela.TokensSystem;

import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTypeTest {
	@Test
	void checkEqualsMethodTest() {
		TokenType tokenType1 = new TokenType(10, "something");
		TokenType tokenType2 = new TokenType(10, "something");
		TokenType tokenType3 = new TokenType(10, "SoMEThing");
		TokenType tokenType4 = new TokenType(1, "something");

		assertEquals(tokenType1, tokenType2);
		assertEquals(tokenType1, tokenType3);
		assertNotEquals(tokenType1, tokenType4);
	}

	@Test
	void canOnlyIncludeLegalCharactersInName() {
		assertThrows(IllegalArgumentException.class, () -> new TokenType(1, "a name"));
		assertThrows(IllegalArgumentException.class, () -> {
			TokenType tokenType = new TokenType(1, "valid_name");
			tokenType.setName("a not valid name");
		});
	}
}
