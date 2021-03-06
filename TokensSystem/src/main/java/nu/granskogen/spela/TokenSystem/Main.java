package nu.granskogen.spela.TokenSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nu.granskogen.spela.TokenSystem.commands.TokensCommand;
import nu.granskogen.spela.TokenSystem.events.BSListener;
import nu.granskogen.spela.TokenSystem.events.EventListener;
import nu.granskogen.spela.TokenSystem.events.JobsListener;
import nu.granskogen.spela.TokenSystem.events.VoteListener;

public class Main extends JavaPlugin {
	
	private static Main instance;
	
	public DataBaseManager dbm = new DataBaseManager();
	
	public HashMap<UUID, VoteToken> voteTokens = new HashMap<UUID, VoteToken>();
	public HashMap<UUID, JobsToken> jobsTokens = new HashMap<UUID, JobsToken>();
	
	public void onEnable() {
		instance = this;
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		getCommand("tokens").setExecutor(new TokensCommand());
		getCommand("tokens").setTabCompleter(new TokensCommand());
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		// Small check to make sure that BossShopPro is installed
		if (Bukkit.getPluginManager().getPlugin("BossShopPro") != null) {
			getServer().getPluginManager().registerEvents(new BSListener(), this);
        }
		
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new TokensPAPIExpansion(Main.getInstance()).register();
        }
		
		if (Bukkit.getPluginManager().getPlugin("Votifier") != null) {
			getServer().getPluginManager().registerEvents(new VoteListener(), this);
        }
		
		if (Bukkit.getPluginManager().getPlugin("Jobs") != null) {
			getServer().getPluginManager().registerEvents(new JobsListener(), this);
        }
		
		dbm.createDatabase();
		
		if(!this.loadUsers()) {
			//Could not read from database
			this.getPluginLoader().disablePlugin(this);
		}
	}
	
	public VoteToken getVoteToken(UUID uuid) {
		VoteToken voteToken = voteTokens.get(uuid);
		if(voteToken == null) {
			voteToken = new VoteToken();
			voteTokens.put(uuid, voteToken);
		}
		
		return voteToken;
	}
	
	public JobsToken getJobsToken(UUID uuid) {
		JobsToken jobsToken = jobsTokens.get(uuid);
		if(jobsToken == null) {
			jobsToken = new JobsToken();
			jobsTokens.put(uuid, jobsToken);
		}
		
		return jobsToken;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	private boolean loadUsers() {
		try (Connection con = DataSource.getConnection();
				PreparedStatement pst = con.prepareStatement(SQLQuery.SELECT_ALL_USERS.toString());
				ResultSet rs = pst.executeQuery();
			) {
			while (rs.next()) {
				VoteToken vt = new VoteToken();
				vt.setAmount(rs.getInt("vote_tokens"));
				voteTokens.put(UUID.fromString(rs.getString("uuid")), vt);
				
				JobsToken jt = new JobsToken();
				jt.setAmount(rs.getInt("jobs_tokens"));
				jobsTokens.put(UUID.fromString(rs.getString("uuid")), jt);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
