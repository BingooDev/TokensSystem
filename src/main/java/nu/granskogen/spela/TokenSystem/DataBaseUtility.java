package nu.granskogen.spela.TokenSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

public class DataBaseUtility {

	public static void setupDatabase(HikariDataSource dataSource) {
		try (Connection con = dataSource.getConnection();
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
	
//	public void createUserIfNotExists(final UUID uuid) {
//		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
//			@Override
//			public void run() {
//				try (Connection con = dataSource.getConnection();
//						PreparedStatement pst = con.prepareStatement(SQLQuery.INSERT_USER.toString());
//					) {
//					pst.setString(1, uuid.toString());
//					pst.execute();
//				} catch (SQLException e) {
//					e.printStackTrace();
//					System.err.println("Failed inserting user in TokensSystem database!");
//				}
//
//			}
//		});
//	}
//
//	public void createTokenType(String name) {
//		try (Connection con = DataSource.getConnection();
//			 PreparedStatement pst = con.prepareStatement(SQLQuery.INSERT_INTO_TOKEN_TYPES.toString());
//		) {
//			pst.setString(1, name);
//			pst.execute();
//			System.out.println(pst.getResultSet());
//		} catch (SQLException e) {
//			e.printStackTrace();
//			System.err.println("Failed inserting user in TokensSystem database!");
//		}
//	}
//
//	public void updateUserTokens(final UUID uuid) {
//		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
//			@Override
//			public void run() {
//				final JobsToken jobsToken = Main.getInstance().getJobsToken(uuid);
//				final VoteToken voteToken = Main.getInstance().getVoteToken(uuid);
//				try (Connection con = DataSource.getConnection();
//						PreparedStatement pst = con.prepareStatement(SQLQuery.UPDATE_USER.toString());
//					) {
//					pst.setString(1, uuid.toString());
//					pst.setInt(2, jobsToken.getAmount());
//					pst.setInt(3, voteToken.getAmount());
//					pst.execute();
//				} catch (SQLException e) {
//					e.printStackTrace();
//					System.err.println("Failed updating user ("+uuid.toString()+") in TokensSystem database!");
//				}
//
//			}
//		});
//	}

	/**
	 * TEMP
	 */

	public void createUserIfNotExists(final UUID uuid) {}

	public void createTokenType(String name) {}

	public void updateUserTokens(final UUID uuid) {}
}
