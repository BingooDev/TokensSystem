package nu.granskogen.spela.TokenSystem.listeners;

import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeDoesntExist;
import nu.granskogen.spela.TokenSystem.token.Token;
import nu.granskogen.spela.TokenSystem.token.TokenRepository;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.JobsPlayer;

import nu.granskogen.spela.TokenSystem.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class JobsListener implements Listener {

	private TokenType jobsTokenType;
	private TokenRepository tokenRepository;
	private Main plugin;

	public JobsListener(TokenType jobsTokenType, TokenRepository tokenRepository, Main plugin) {
		this.jobsTokenType = jobsTokenType;
		this.tokenRepository = tokenRepository;
		this.plugin = plugin;
	}

	@EventHandler
	public void onLevelUp(JobsLevelUpEvent event) {
		JobsPlayer jobsPlayer = event.getPlayer();
		Player player = Bukkit.getPlayer(jobsPlayer.getUniqueId());

		Token jobsToken = tokenRepository.getToken(jobsTokenType, jobsPlayer.getUniqueId());
		jobsToken.addAmount(1);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					tokenRepository.updateToken(jobsPlayer.getUniqueId(), jobsToken);
					if(player != null)
						player.sendMessage("§aDu har fått en JobsToken!");
				} catch (SQLException | TokenTypeDoesntExist e) {
					throw new RuntimeException(e);
				}
			}
		}.runTaskAsynchronously(plugin);
	}
}
