package nu.granskogen.spela.TokenSystem.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import nu.granskogen.spela.TokenSystem.Main;

public class EventListener implements Listener {
	Main pl = Main.getInstance();
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		pl.dbm.createUserIfNotExists(event.getPlayer().getUniqueId());
	}
}
