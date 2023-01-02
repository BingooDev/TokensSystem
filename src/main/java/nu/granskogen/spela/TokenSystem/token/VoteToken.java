package nu.granskogen.spela.TokenSystem.token;

import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;

public class VoteToken extends Token {

	public VoteToken(TokenTypeRepository tokenTypeRepository, int amount) {
		super(tokenTypeRepository.getTokenTypeByName("VoteToken"), amount);
	}

}
