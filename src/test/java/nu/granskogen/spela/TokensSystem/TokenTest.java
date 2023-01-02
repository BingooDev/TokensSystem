package nu.granskogen.spela.TokensSystem;

import nu.granskogen.spela.TokenSystem.token.Token;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTest {
	@Test
	void checkEqualsMethodTest() {
		TokenType tokenType1 = new TokenType(1, "something");
		TokenType tokenType2 = new TokenType(2, "something2");
		Token token1 = new Token(tokenType1, 10);
		Token token2 = new Token(tokenType1, 10);
		Token token3 = new Token(tokenType1, 20);
		Token token4 = new Token(tokenType2, 10);

		assertEquals(token1, token2);
		assertNotEquals(token1, token3);
		assertNotEquals(token1, token4);
	}

	@Test
	void canAddToAmount() {
		TokenType tokenType = new TokenType(1, "something");
		Token token = new Token(tokenType, 10);

		token.addAmount(5);
		assertEquals(15, token.getAmount());

		token.addAmount(5);
		assertEquals(20, token.getAmount());

		token.addAmount(-3);
		assertEquals(17, token.getAmount());
	}

	@Test
	void canRemoveFromAmount() {
		TokenType tokenType = new TokenType(1, "something");
		Token token = new Token(tokenType, 10);

		token.removeAmount(5);
		assertEquals(5, token.getAmount());

		token.removeAmount(10);
		assertEquals(-5, token.getAmount());

		token.removeAmount(-8);
		assertEquals(3, token.getAmount());
	}
}
