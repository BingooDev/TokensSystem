package nu.granskogen.spela.TokenSystem;

import org.black_ixx.bossshop.events.BSRegisterTypesEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

public class EventListener implements Listener {

	@EventHandler
	public void onRegisterType(BSRegisterTypesEvent event) {
		new BSVoteTokenPriceType().register();
		new BSJobsTokenPriceType().register();
	}
	
	@EventHandler
	public void onVote(VotifierEvent event) {
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getVote().getUsername());
		Main.getInstance().getVoteToken(player.getUniqueId()).add(1);
		System.out.println("VoteToken added to " + event.getVote().getUsername());
	}
}
