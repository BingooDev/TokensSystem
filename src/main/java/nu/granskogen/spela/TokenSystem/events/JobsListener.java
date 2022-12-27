package nu.granskogen.spela.TokenSystem.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.JobsPlayer;

import nu.granskogen.spela.TokenSystem.Main;

public class JobsListener implements Listener {

	@EventHandler
	public void onLevelUp(JobsLevelUpEvent event) {
		JobsPlayer jobsPlayer = event.getPlayer();
		Main.getInstance().getJobsToken(jobsPlayer.getUniqueId()).addAmount(1);
		
		Player player = Bukkit.getPlayer(jobsPlayer.getUniqueId());
		if(player != null) 
			player.sendMessage("§aDu har fått en JobsToken!");
		
		Main.getInstance().dbm.updateUserTokens(player.getUniqueId());
	}
}
