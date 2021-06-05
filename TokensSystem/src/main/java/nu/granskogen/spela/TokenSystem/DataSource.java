package nu.granskogen.spela.TokenSystem;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {
	private static Main pl = Main.getInstance();

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:mysql://" + pl.getConfig().getString("database.host") + ":" + pl.getConfig().getString("database.port") + "/" + pl.getConfig().getString("database.database") + "?useSSL=false");
        config.setUsername(pl.getConfig().getString("database.username"));
        config.setPassword(pl.getConfig().getString("database.password"));
        config.addDataSourceProperty("cachePrepStmts" , "true" );
        config.addDataSourceProperty("prepStmtCacheSize" , pl.getConfig().getString("database.prepStmtCacheSize"));
        config.addDataSourceProperty("prepStmtCacheSqlLimit" , pl.getConfig().getString("database.prepStmtCacheSqlLimit"));
        config.addDataSourceProperty("connectionTimeout" , pl.getConfig().getString("database.connectionTimeout"));
        config.addDataSourceProperty("maximumPoolSize" , pl.getConfig().getString("database.maximumPoolSize"));
        ds = new HikariDataSource(config);
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
