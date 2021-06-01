package nu.granskogen.spela.TokenSystem;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static Main instance;
	
	public HashMap<UUID, VoteToken> voteTokens = new HashMap<UUID, VoteToken>();
	public HashMap<UUID, JobsToken> jobsTokens = new HashMap<UUID, JobsToken>();
	
	public void onEnable() {
		instance = this;
		
		getCommand("test").setExecutor(new TestCommand());
	}
	
	public VoteToken getVoteToken(UUID uuid) {
		VoteToken voteToken = voteTokens.get(uuid);
		if(voteToken == null) {
			voteToken = new VoteToken();
			voteTokens.put(uuid, voteToken);
		}
		
		return voteToken;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
}
