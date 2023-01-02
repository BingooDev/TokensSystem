package nu.granskogen.spela.TokenSystem.tokenType;

import com.zaxxer.hikari.HikariDataSource;
import nu.granskogen.spela.TokenSystem.exceptions.FailedCratingTokenType;
import nu.granskogen.spela.TokenSystem.exceptions.TokenTypeAlreadyExists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TokenTypeRepository {
	private HikariDataSource dataSource;
	private HashMap<Integer, TokenType> tokenTypes = new HashMap<>();

	public TokenTypeRepository(HikariDataSource dataSource) throws SQLException {
		this.dataSource = dataSource;
		loadTokenTypes();
	}

	private void loadTokenTypes() throws SQLException {
		Connection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = dataSource.getConnection();
			pst = connection.prepareStatement("SELECT * FROM token_types;");
			rs = pst.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				tokenTypes.put(id, new TokenType(
						id,
					rs.getString("name")
				));
			}
			connection.close();
		} finally {
			if (connection != null) try {connection.close();} catch (Exception ignored) {}
			if (pst != null) try {pst.close();} catch (Exception ignored) {}
			if (rs != null) try {rs.close();} catch (Exception ignored) {}
		}
	}

	/**
	 * Creates a new tokenType
	 * @param name The name of the tokenType
	 * @return The id of the tokenType
	 * @throws SQLException
	 */
	public int createTokenType(String name) throws SQLException, FailedCratingTokenType, IllegalArgumentException, TokenTypeAlreadyExists {
		TokenType.validateName(name);

		name = name.toLowerCase();
		String query = "INSERT INTO token_types (name) VALUES (?);";
		Connection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			connection = dataSource.getConnection();
			pst = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			pst.setString(1, name);
			pst.executeUpdate();

			rs = pst.getGeneratedKeys();
			if(rs.next())
			{
				int id = rs.getInt(1);

				// Add to memory
				tokenTypes.put(id, new TokenType(id, name));

				connection.close();
				return id;
			}
		} catch (SQLException e) {
			if(e.getMessage().toLowerCase().contains("duplicate entry")) {
				throw new TokenTypeAlreadyExists();
			}
			throw e;
		} finally {
			if (connection != null) try {connection.close();} catch (Exception ignored) {}
			if (pst != null) try {pst.close();} catch (Exception ignored) {}
			if (rs != null) try {rs.close();} catch (Exception ignored) {}
		}

		throw new FailedCratingTokenType();
	}

	public TokenType getTokenTypeById(int id) {
		return tokenTypes.get(id);
	}

	public TokenType getTokenTypeByName(String name) {
		for (TokenType tokenType : tokenTypes.values()) {
			if(tokenType.getName().equals(name.toLowerCase()))
					return tokenType;
		}
		return null;
	}

	/**
	 * Updates tokenType data in database
	 * @param tokenType The update tokenType, with same id as the old one
	 * @throws SQLException
	 * @throws TokenTypeAlreadyExists Thrown if the new name already exists
	 */
	public void updateTokenType(TokenType tokenType) throws SQLException, TokenTypeAlreadyExists {
		TokenType.validateName(tokenType.getName());

		Connection connection = null;
		PreparedStatement pst = null;
		try {
			String query = "UPDATE token_types SET name=? WHERE id=?;";
			connection = dataSource.getConnection();

			pst = connection.prepareStatement(query);
			pst.setString(1, tokenType.getName());
			pst.setInt(2, tokenType.getId());

			pst.executeUpdate();
		} catch (SQLException e) {
			if(e.getMessage().toLowerCase().contains("duplicate entry"))
				throw new TokenTypeAlreadyExists();
			throw e;
		} finally {
			if (connection != null) try {connection.close();} catch (Exception ignored) {}
			if (pst != null) try {pst.close();} catch (Exception ignored) {}
		}
		tokenTypes.put(tokenType.getId(), tokenType);
	}

	public void delete(TokenType tokenType) throws SQLException {
		Connection connection = null;
		PreparedStatement pst = null;
		try {
			String query = "DELETE FROM token_types WHERE id=?;";
			connection = dataSource.getConnection();
			pst = connection.prepareStatement(query);
			pst.setInt(1, tokenType.getId());
			pst.execute();
		} finally {
			if (connection != null) try {connection.close();} catch (Exception ignored) {}
			if (pst != null) try {pst.close();} catch (Exception ignored) {}
		}

		tokenTypes.remove(tokenType.getId());
	}

	public List<String> getTokenTypeNames() {
		ArrayList<String> names = new ArrayList();
		tokenTypes.values().forEach(tokenType -> names.add(tokenType.getName()));
		return names;
	}


	public boolean exists(int id) {
		return tokenTypes.get(id) != null;
	}

	public boolean exists(String name) {
		return getTokenTypeByName(name) != null;
	}
}
