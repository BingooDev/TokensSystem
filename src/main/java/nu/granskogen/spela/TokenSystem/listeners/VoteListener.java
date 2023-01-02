package nu.granskogen.spela.TokenSystem.listeners;

import nu.granskogen.spela.TokenSystem.MessageUtil;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeDoesntExist;
import nu.granskogen.spela.TokenSystem.token.Token;
import nu.granskogen.spela.TokenSystem.token.TokenRepository;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

import nu.granskogen.spela.TokenSystem.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

public class VoteListener implements Listener {

	private TokenType voteTokenType;
	private TokenRepository tokenRepository;
	private Main plugin;
	private Logger logger;

	public VoteListener(TokenType voteTokenType, TokenRepository tokenRepository, Main plugin, Logger logger) {
		this.voteTokenType = voteTokenType;
		this.tokenRepository = tokenRepository;
		this.plugin = plugin;
		this.logger = logger;
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getVote().getUsername());
		logger.info(voteTokenType.getDisplayName()+" added to " + player.getName() + " ("+player.getUniqueId()+")");
		Main.getInstance().dbm.updateUserTokens(player.getUniqueId());

		Token voteToken = tokenRepository.getToken(voteTokenType, player.getUniqueId());
		voteToken.addAmount(1);
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					tokenRepository.updateToken(player.getUniqueId(), voteToken);
					if(player.isOnline()) {
						MessageUtil.sendMessage((Player) player, "addTokensTarget",
								Map.of("tokenType", voteTokenType.getDisplayName(),
										"amount", "1",
										"sum", String.valueOf(voteToken.getAmount())
								));
					}
				} catch (SQLException | TokenTypeDoesntExist e) {
					throw new RuntimeException(e);
				}
			}
		}.runTaskAsynchronously(plugin);
	}

}
