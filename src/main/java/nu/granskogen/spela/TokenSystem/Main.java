package nu.granskogen.spela.TokenSystem;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import nu.granskogen.spela.TokenSystem.token.JobsToken;
import nu.granskogen.spela.TokenSystem.token.Token;
import nu.granskogen.spela.TokenSystem.token.TokenRepository;
import nu.granskogen.spela.TokenSystem.token.VoteToken;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import nu.granskogen.spela.TokenSystem.commands.TokensCommand;
import nu.granskogen.spela.TokenSystem.listeners.BSListener;
import nu.granskogen.spela.TokenSystem.listeners.JobsListener;
import nu.granskogen.spela.TokenSystem.listeners.VoteListener;

public class Main extends JavaPlugin {
	
	private static Main instance;

	public ConfigManager cfgm;

	private static HikariDataSource dataSource;
	
	public DataBaseUtility dbm;

	// token_type_id, name
	private HashMap<Integer, String> tokenTypes = new HashMap<>();
	// token_type_id, <uuid, token>
	private HashMap<Integer, HashMap<UUID, Token>> tokens = new HashMap<>();

	private TokenTypeRepository tokenTypeRepository;

	private TokenRepository tokenRepository;

	// True if, unit or integration test
	private boolean isTest = false;
	
	public void onEnable() {
		instance = this;

		cfgm = new ConfigManager(getDataFolder(), isTest);

		// Will only be instantiated beforehand if running automated test multiple times
		if(dataSource == null) {
			dataSource = DataBaseUtility.createHikariDataSource(cfgm.getConfig());
			DataBaseUtility.setupDatabase(dataSource);
		}

		try {
			tokenTypeRepository = new TokenTypeRepository(dataSource);
			tokenRepository = new TokenRepository(dataSource, tokenTypeRepository);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		setupDefaultTokenTypes();

		getCommand("tokens").setExecutor(new TokensCommand(this, tokenTypeRepository, tokenRepository));
		getCommand("tokens").setTabCompleter(new TokensCommand(this, tokenTypeRepository, tokenRepository));

		// Small check to make sure that BossShopPro is installed
		if (Bukkit.getPluginManager().getPlugin("BossShopPro") != null) {
			getServer().getPluginManager().registerEvents(new BSListener(), this);
        }

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PAPIExpansion(Main.getInstance(), cfgm, tokenTypeRepository, tokenRepository).register();
        }

		if (Bukkit.getPluginManager().getPlugin("Votifier") != null) {
			getServer().getPluginManager().registerEvents(new VoteListener(), this);
        }

		if (Bukkit.getPluginManager().getPlugin("Jobs") != null) {
			getServer().getPluginManager().registerEvents(new JobsListener(), this);
        }

//		if(!this.loadUsers()) {
//			//Could not read from database
//			this.getPluginLoader().disablePlugin(this);
//		}
	}

	private void setupDefaultTokenTypes() {
		try {
			tokenTypeRepository.createTokenType("vote", "VoteToken");
		} catch (SQLException | FailedCratingTokenType e) {
			e.printStackTrace();
		} catch (TokenTypeAlreadyExists ignored) {}
		try {
			tokenTypeRepository.createTokenType("jobs", "JobsToken");
		} catch (SQLException | FailedCratingTokenType e) {
			e.printStackTrace();
		} catch (TokenTypeAlreadyExists ignored) {}
	}

	// Used for mocking
	public Main()
	{
		super();
	}

	// Used for mocking
	protected Main(@SuppressWarnings("removal") org.bukkit.plugin.java.JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
	{
		super(loader, description, dataFolder, file);
		isTest = true;
	}

	//	public Token getToken(Class<? extends Token> tokenType, UUID uuid) {
//		System.out.println(tokenType);
//		if(VoteToken.class.isAssignableFrom(tokenType)) {
//			return getVoteToken(uuid);
//		} else if(JobsToken.class.isAssignableFrom(tokenType)) {
//			return getJobsToken(uuid);
//		}
//		return null;
//	}
//
//	public VoteToken getVoteToken(UUID uuid) {
//		VoteToken voteToken = voteTokens.get(uuid);
//		if(voteToken == null) {
//			voteToken = new VoteToken();
//			voteTokens.put(uuid, voteToken);
//		}
//
//		return voteToken;
//	}
//
//	public JobsToken getJobsToken(UUID uuid) {
//		JobsToken jobsToken = jobsTokens.get(uuid);
//		if(jobsToken == null) {
//			jobsToken = new JobsToken();
//			jobsTokens.put(uuid, jobsToken);
//		}
//
//		return jobsToken;
//	}
//
	public static Main getInstance() {
		return instance;
	}
//
//	private boolean loadUsers() {
//		try (Connection con = DataSource.getConnection();
//				PreparedStatement pst = con.prepareStatement(SQLQuery.SELECT_ALL_USERS.toString());
//				ResultSet rs = pst.executeQuery();
//			) {
//			while (rs.next()) {
//				VoteToken vt = new VoteToken();
//				vt.setAmount(rs.getInt("vote_tokens"));
//				voteTokens.put(UUID.fromString(rs.getString("uuid")), vt);
//
//				JobsToken jt = new JobsToken();
//				jt.setAmount(rs.getInt("jobs_tokens"));
//				jobsTokens.put(UUID.fromString(rs.getString("uuid")), jt);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}


	/**
	 * TEMP
	 */

	public Token getToken(Class<? extends Token> tokenType, UUID uuid) {
		return null;
	}

	public VoteToken getVoteToken(UUID uuid) {
		return null;
	}

	public JobsToken getJobsToken(UUID uuid) {
		return null;
	}


	public TokenTypeRepository getTokenTypeRepository() {
		return tokenTypeRepository;
	}

	public TokenRepository getTokenRepository() {
		return tokenRepository;
	}
}
