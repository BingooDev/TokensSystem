package nu.granskogen.spela.TokenSystem.events;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

import nu.granskogen.spela.TokenSystem.Main;

public class VoteListener implements Listener {
	
	@EventHandler
	public void onVote(VotifierEvent event) {
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getVote().getUsername());
		Main.getInstance().getVoteToken(player.getUniqueId()).add(1);
		System.out.println("VoteToken added to " + event.getVote().getUsername());
		
		if(player.isOnline()) {
			Player onlinePlayer = (Player) player;
			onlinePlayer.sendMessage("§aDu har fått en VoteToken!");
		}
		
		Main.getInstance().dbm.updateUserTokens(player.getUniqueId());
	}

}
