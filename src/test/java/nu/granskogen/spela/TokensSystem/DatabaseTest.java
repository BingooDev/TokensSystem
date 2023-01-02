package nu.granskogen.spela.TokensSystem;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DatabaseTest {

	@Test
	void canCreateDataSource() {
		assertDoesNotThrow(() -> {
			TestUtilities.getDataSource();
		});
	}

	@Test
	void canClearDatabase() {
		assertDoesNotThrow(() -> {
			HikariDataSource dataSource = TestUtilities.getDataSource();
			TestUtilities.resetDatabase(dataSource);
		});
	}
}
