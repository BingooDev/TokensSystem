package nu.granskogen.spela.TokenSystem.events;

import org.black_ixx.bossshop.events.BSRegisterTypesEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nu.granskogen.spela.TokenSystem.bossShopPro.BSJobsTokenPriceType;
import nu.granskogen.spela.TokenSystem.bossShopPro.BSVoteTokenPriceType;

public class BSListener implements Listener {
	
	@EventHandler
	public void onRegisterType(BSRegisterTypesEvent event) {
		new BSVoteTokenPriceType().register();
		new BSJobsTokenPriceType().register();
	}

}
