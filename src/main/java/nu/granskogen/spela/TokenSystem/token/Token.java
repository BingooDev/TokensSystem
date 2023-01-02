package nu.granskogen.spela.TokenSystem.token;

import nu.granskogen.spela.TokenSystem.tokenType.TokenType;

import java.util.Objects;

public class Token {
	private TokenType tokenType;
	private int amount;

	public Token(TokenType tokenType, int amount) {
		this.tokenType = tokenType;
		this.amount = amount;
	}

	// Constructor

	/**
	 * Adds amount, use {@link TokenRepository#updateToken} to store in database
	 * @param value Value to add
	 */
	public void addAmount(int value) {
		this.amount += value;
	}

	/**
	 * Removes amount, use {@link TokenRepository#updateToken} to store in database
	 * @param value Value to remove
	 */
	public void removeAmount(int value) {
		this.amount -= value;
	}

	/**
	 * Sets amount to specified value, use {@link TokenRepository#updateToken} to store in database
	 * @param value new value
	 */
	public void setAmount(int value) {
		this.amount = value;
	}

	public TokenType getTokenType() {
		return tokenType;
	}
	
	public int getAmount() {
		return this.amount;
	}

	@Override
	public boolean equals(Object o) {
//		System.out.println(this);
//		System.out.println(o);
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Token token = (Token) o;

		if (amount != token.amount) return false;
		return token.tokenType.equals(this.tokenType);
	}

	@Override
	public String toString() {
		return "Token{" +
				"tokenType=" + tokenType +
				", amount=" + amount +
				'}';
	}
}
