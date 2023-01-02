package nu.granskogen.spela.TokenSystem.token;

import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;

public class JobsToken extends Token {
	public JobsToken(TokenTypeRepository tokenTypeRepository, int amount) {
		super(tokenTypeRepository.getTokenTypeByName("JobsToken"), amount);
	}

}
