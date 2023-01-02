package nu.granskogen.spela.TokenSystem;

public enum SQLQuery {
	CREATE_TABLE_TOKEN_TYPES("CREATE TABLE IF NOT EXISTS token_types (" +
			"id int(11) PRIMARY KEY auto_increment," +
			"name varchar(255) NOT NULL UNIQUE" +
			");"),
	CREATE_TABLE_USER_TOKENS("CREATE TABLE IF NOT EXISTS user_token_type (" +
			"id int(11) PRIMARY KEY auto_increment," +
			"uuid varchar(255) NOT NULL, " +
			"token_type_id int(255) NOT NULL, " +
			"amount int(11) NOT NULL, " +
			"FOREIGN KEY (token_type_id) REFERENCES token_types(id)," +
			"UNIQUE(uuid, token_type_id)" +
			");"),
	SELECT_ALL_TOKEN_TYPES("SELECT * FROM token_types;"),
	SELECT_TOKENS_BY_USER("SELECT * FROM user_tokens WHERE uuid=?;"),
	UPDATE_TOKEN("INSERT INTO user_tokens (uuid, token_type_id, amount) VALUES (?,?,?) "
			+ "ON DUPLICATE KEY UPDATE uuid=VALUES(uuid), token_type_id=VALUES(token_type_id), "
			+ "amount=VALUES(amount);");
	private String mysql;
	
	SQLQuery(String mysql) {
		this.mysql = mysql;
	}
	
	@Override
    public String toString() {
        return mysql;
    }
}
