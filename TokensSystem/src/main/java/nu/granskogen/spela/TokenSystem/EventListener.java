package nu.granskogen.spela.TokenSystem;

import org.black_ixx.bossshop.events.BSRegisterTypesEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EventListener implements Listener {

	@EventHandler
	public void onRegisterType(BSRegisterTypesEvent event) {
		new BSVoteTokenPriceType().register();
		new BSJobsTokenPriceType().register();
	}
}
