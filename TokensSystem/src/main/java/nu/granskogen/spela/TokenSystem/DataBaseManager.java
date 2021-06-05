package nu.granskogen.spela.TokenSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;

public class DataBaseManager {
	
	public void createDatabase() {
		try (Connection con = DataSource.getConnection();
			PreparedStatement pst = con.prepareStatement(SQLQuery.CREATE_TABLE_USERS.toString());
		) {
			pst.execute();
			System.err.println("Created!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createUserIfNotExists(final UUID uuid) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				try (Connection con = DataSource.getConnection();
						PreparedStatement pst = con.prepareStatement(SQLQuery.INSERT_USER.toString());
					) {
					pst.setString(1, uuid.toString());
					pst.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	public void updateUserTokens(final UUID uuid) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				final JobsToken jobsToken = Main.getInstance().getJobsToken(uuid);
				final VoteToken voteToken = Main.getInstance().getVoteToken(uuid);
				try (Connection con = DataSource.getConnection();
						PreparedStatement pst = con.prepareStatement(SQLQuery.UPDATE_USER.toString());
					) {
					pst.setString(1, uuid.toString());
					pst.setInt(2, jobsToken.getAmount());
					pst.setInt(3, voteToken.getAmount());
					pst.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
}
