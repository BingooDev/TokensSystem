package nu.granskogen.spela.TokenSystem;

import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.commands.TokensCommand;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;
import nu.granskogen.spela.TokenSystem.listeners.BSListener;
import nu.granskogen.spela.TokenSystem.listeners.JobsListener;
import nu.granskogen.spela.TokenSystem.listeners.VoteListener;
import nu.granskogen.spela.TokenSystem.token.TokenRepository;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class Main extends JavaPlugin {
	
	private static Main instance;

	public ConfigManager cfgm;

	private static HikariDataSource dataSource;

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
			getLogger().info("Enabling BossShopPro support");
			getServer().getPluginManager().registerEvents(new BSListener(tokenTypeRepository), this);
        }

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			getLogger().info("Enabling PlaceholderAPI support");
			new PAPIExpansion(Main.getInstance(), cfgm, tokenTypeRepository, tokenRepository).register();
        }

		if (Bukkit.getPluginManager().getPlugin("Votifier") != null && cfgm.getConfig().getBoolean("enableVoteToken")) {
			getLogger().info("Enabling Votifier support");
			getServer().getPluginManager().registerEvents(new VoteListener(tokenTypeRepository.getTokenTypeByName("vote"), tokenRepository, this, getLogger()), this);
        }

		if (Bukkit.getPluginManager().getPlugin("Jobs") != null && cfgm.getConfig().getBoolean("enableJobsToken")) {
			getLogger().info("Enabling Jobs support");
			getServer().getPluginManager().registerEvents(new JobsListener(tokenTypeRepository.getTokenTypeByName("jobs"), tokenRepository, this, getLogger()), this);
        }
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

	public static Main getInstance() {
		return instance;
	}


	public TokenTypeRepository getTokenTypeRepository() {
		return tokenTypeRepository;
	}

	public TokenRepository getTokenRepository() {
		return tokenRepository;
	}
}
