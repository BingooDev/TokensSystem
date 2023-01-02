package nu.granskogen.spela.TokensSystem;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.DataBaseUtility;
import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.SQLQuery;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestUtilities {
	private static FileConfiguration config;

	static {
		String path = Paths.get("").toAbsolutePath().toString();
		config = YamlConfiguration.loadConfiguration(new File(path+"/src/test/config.yml"));
	}
	public static HikariDataSource getDataSource() {
		HikariDataSource dataSource = DataBaseUtility.createHikariDataSource(config);
		return dataSource;
	}

	public static void resetDatabase(HikariDataSource dataSource) throws SQLException {
		String database = config.getString("database.database");
		Connection connection = dataSource.getConnection();
		PreparedStatement pst = connection.prepareStatement("DROP DATABASE IF EXISTS "+database+";");
		pst.execute();
		pst = connection.prepareStatement("CREATE DATABASE "+database+";");
		pst.execute();

		pst.execute("USE " + database + ";");
		pst = connection.prepareStatement(SQLQuery.CREATE_TABLE_TOKEN_TYPES.toString());
		pst.execute();
		pst = connection.prepareStatement(SQLQuery.CREATE_TABLE_USER_TOKENS.toString());
		pst.execute();
	}

	public static Main getPlugin(ServerMock server) throws InvalidDescriptionException, IOException {
//		PluginDescriptionFile description = new PluginDescriptionFile(server.getClass().getResourceAsStream("/plugin.yml"));
//		Main plugin = (Main) server.getPluginManager().loadPlugin(Main.class, description);
//		server.getPluginManager().enablePlugin(plugin);
//		return plugin;
		return MockBukkit.load(Main.class);
	}
}
