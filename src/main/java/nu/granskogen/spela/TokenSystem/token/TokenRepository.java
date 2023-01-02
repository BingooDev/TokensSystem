package nu.granskogen.spela.TokenSystem.token;

import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeDoesntExist;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.UUID;

public class TokenRepository {
	private HikariDataSource dataSource;
	private TokenTypeRepository tokenTypeRepository;

	// <token_type_id, <player_uuid, token>>
	private HashMap<Integer, HashMap<UUID, Token>> tokens = new HashMap<>();

	public TokenRepository(HikariDataSource dataSource, TokenTypeRepository tokenTypeRepository) throws SQLException {
		this.dataSource = dataSource;
		this.tokenTypeRepository = tokenTypeRepository;
		loadTokensFromDatabase();
	}

	private void loadTokensFromDatabase() throws SQLException {
		Connection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = dataSource.getConnection();
			pst = connection.prepareStatement("SELECT * FROM user_token_type;");
			rs = pst.executeQuery();
			while (rs.next()) {
				int token_type_id = rs.getInt("token_type_id");
				TokenType tokenType = tokenTypeRepository.getTokenTypeById(token_type_id);
				// setup inner hashmap if nonexistent
				if(!tokens.containsKey(token_type_id)) {
					tokens.put(token_type_id, new HashMap<>());
				}

				tokens.get(token_type_id).put(createUUIDFromUUIDWithoutDashes(rs.getString("uuid")), new Token(
						tokenType,
						rs.getInt("amount")
				));
			}
			connection.close();
		} finally {
			if (connection != null) try {connection.close();} catch (Exception ignored) {}
			if (pst != null) try {pst.close();} catch (Exception ignored) {}
			if (rs != null) try {rs.close();} catch (Exception ignored) {}
		}
	}

	private UUID createUUIDFromUUIDWithoutDashes(String uuidWithoutDashes) {
		String uuid = uuidWithoutDashes.replaceAll(
				"(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
				"$1-$2-$3-$4-$5");
		return UUID.fromString(uuid);
	}

	public void updateToken(UUID playerUUID, Token token) throws SQLException, TokenTypeDoesntExist {
		Connection connection = null;
		PreparedStatement pst = null;
		int tokenTypeId = token.getTokenType().getId();
		try {
			connection = dataSource.getConnection();
			pst = connection.prepareStatement("INSERT INTO user_token_type (uuid, token_type_id, amount) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE amount=?;");
			pst.setString(1, playerUUID.toString().replace("-", ""));
			pst.setInt(2, tokenTypeId);
			pst.setInt(3, token.getAmount());
			pst.setInt(4, token.getAmount());
			pst.execute();
		} catch (SQLIntegrityConstraintViolationException e) {
			if(e.getMessage().toLowerCase().contains("a foreign key constraint fails"))
				throw new TokenTypeDoesntExist();
			throw e;
		} finally {
			if (connection != null) try {connection.close();} catch (Exception ignored) {}
			if (pst != null) try {pst.close();} catch (Exception ignored) {}
		}

		// When using token objects, they should always be from this class, and thereby already linked to the map.
		try {
			if(!tokens.get(tokenTypeId).containsKey(playerUUID))
				tokens.get(tokenTypeId).put(playerUUID, token);
		} catch (NullPointerException e) {
			tokens.put(tokenTypeId, new HashMap<>());
			tokens.get(tokenTypeId).put(playerUUID, token);
		}
	}

	public Token getToken(TokenType tokenType, UUID playerUUID) {
		if(tokenType == null) {
			return null;
		}
		int tokenTypeId = tokenType.getId();
		try {
			Token token = tokens.get(tokenTypeId).get(playerUUID);
			if(token == null) {
				token = new Token(tokenType, 0);
				tokens.get(tokenTypeId).put(playerUUID, token);
			}
			return token;
		} catch (NullPointerException e) {
			// Add token and TokenType to tokens if TokenType exists in database
			// Add token and TokenType to tokens if TokenType exists in database
			if(tokenTypeRepository.exists(tokenTypeId)) {
				tokens.put(tokenTypeId, new HashMap<>());
				Token token = new Token(tokenType, 0);
				tokens.get(tokenTypeId).put(playerUUID, token);
				return token;
			}
			// return null in case tokenType doesn't exist.
			return null;
		}
	}
}
