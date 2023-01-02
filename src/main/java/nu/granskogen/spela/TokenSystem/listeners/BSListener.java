package nu.granskogen.spela.TokenSystem.listeners;

import nu.granskogen.spela.TokenSystem.bossShopPro.BSTokenPriceType;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import org.black_ixx.bossshop.events.BSRegisterTypesEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BSListener implements Listener {

	private TokenTypeRepository tokenTypeRepository;

	public BSListener(TokenTypeRepository tokenTypeRepository) {
		this.tokenTypeRepository = tokenTypeRepository;
	}

	@EventHandler
	public void onRegisterType(BSRegisterTypesEvent event) {
		tokenTypeRepository.getTokenTypes().forEach(tokenType -> new BSTokenPriceType(tokenType).register());
	}

}
