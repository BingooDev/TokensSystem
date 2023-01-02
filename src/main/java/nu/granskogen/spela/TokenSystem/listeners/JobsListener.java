package nu.granskogen.spela.TokenSystem.listeners;

import nu.granskogen.spela.TokenSystem.MessageUtil;
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
import java.util.Map;
import java.util.logging.Logger;

public class JobsListener implements Listener {

	private TokenType jobsTokenType;
	private TokenRepository tokenRepository;
	private Main plugin;
	private Logger logger;

	public JobsListener(TokenType jobsTokenType, TokenRepository tokenRepository, Main plugin, Logger logger) {
		this.jobsTokenType = jobsTokenType;
		this.tokenRepository = tokenRepository;
		this.plugin = plugin;
		this.logger = logger;
	}

	@EventHandler
	public void onLevelUp(JobsLevelUpEvent event) {
		JobsPlayer jobsPlayer = event.getPlayer();
		logger.info(jobsTokenType.getDisplayName()+" added to " + jobsPlayer.getName() + " ("+jobsPlayer.getUniqueId()+")");
		Player player = Bukkit.getPlayer(jobsPlayer.getUniqueId());

		Token jobsToken = tokenRepository.getToken(jobsTokenType, jobsPlayer.getUniqueId());
		jobsToken.addAmount(1);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					tokenRepository.updateToken(jobsPlayer.getUniqueId(), jobsToken);
					if(player.isOnline()) {
						MessageUtil.sendMessage(player, "addTokensTarget",
								Map.of("tokenType", jobsTokenType.getDisplayName(),
										"amount", "1",
										"sum", String.valueOf(jobsToken.getAmount())
								));
					}
				} catch (SQLException | TokenTypeDoesntExist e) {
					throw new RuntimeException(e);
				}
			}
		}.runTaskAsynchronously(plugin);
	}
}
