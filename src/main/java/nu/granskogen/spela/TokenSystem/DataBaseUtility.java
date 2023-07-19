package nu.granskogen.spela.TokenSystem;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBaseUtility {

	public static void setupDatabase(HikariDataSource dataSource) {
		try (Connection con = dataSource.getConnection()
		) {
			PreparedStatement pst = con.prepareStatement(SQLQuery.CREATE_TABLE_TOKEN_TYPES.toString());
			pst.execute();
			pst = con.prepareStatement(SQLQuery.CREATE_TABLE_USER_TOKENS.toString());
			pst.execute();
		} catch (SQLException e) {
			System.err.println("Failed creating database table for TokensSystem!");
			e.printStackTrace();
		}
	}

	public static HikariDataSource createHikariDataSource(FileConfiguration config) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/"
				+ config.getString("database.database")
				+ "?useSSL=" + config.getString("database.useSSL"));
		hikariConfig.setUsername(config.getString("database.username"));
		hikariConfig.setPassword(config.getString("database.password"));
		hikariConfig.addDataSourceProperty("cachePrepStmts" , "true" );
		hikariConfig.addDataSourceProperty("prepStmtCacheSize" , config.getString("database.prepStmtCacheSize"));
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit" , config.getString("database.prepStmtCacheSqlLimit"));
		hikariConfig.addDataSourceProperty("connectionTimeout" , config.getString("database.connectionTimeout"));
		hikariConfig.addDataSourceProperty("maximumPoolSize" , config.getString("database.maximumPoolSize"));
		return new HikariDataSource(hikariConfig);
	}
}
