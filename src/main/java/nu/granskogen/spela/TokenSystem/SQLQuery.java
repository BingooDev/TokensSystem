package nu.granskogen.spela.TokenSystem;

public enum SQLQuery {
	CREATE_TABLE_USERS("CREATE TABLE IF NOT EXISTS users (" + 
			"uuid varchar(255) PRIMARY KEY," + 
			"vote_tokens int NOT NULL," +
			"jobs_tokens int NOT NULL" +
			");"),
	SELECT_ALL_USERS("SELECT * FROM users;"),
	SELECT_USER("SELECT * FROM users WHERE uuid=?;"),
	INSERT_USER("INSERT IGNORE INTO users (`uuid`, `jobs_tokens`, `vote_tokens`) VALUES (?, 0, 0);"),
	UPDATE_USER("INSERT INTO users (uuid, jobs_tokens, vote_tokens) VALUES (?,?,?) "
			+ "ON DUPLICATE KEY UPDATE uuid=VALUES(uuid), jobs_tokens=VALUES(jobs_tokens), "
			+ "vote_tokens=VALUES(vote_tokens);");
	private String mysql;
	
	SQLQuery(String mysql) {
		this.mysql = mysql;
	}
	
	@Override
    public String toString() {
        return mysql;
    }
}
