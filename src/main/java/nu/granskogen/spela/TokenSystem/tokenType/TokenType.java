package nu.granskogen.spela.TokenSystem.tokenType;

import java.util.Objects;

public class TokenType {
	private int id;
	private String name;
	private String displayName;

	public TokenType(int id, String name) {
		this(id, name.toLowerCase(), name.toLowerCase());
	}

	public TokenType(int id, String name, String displayName) {
		this.id = id;
		setName(name);
		setDisplayName(displayName);
	}

	// Getters and setters

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		validateName(name);
		this.name = name.toLowerCase();
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) throws IllegalArgumentException {
		validateDisplayName(displayName);
		this.displayName = displayName;
	}

	/**
	 * Validates name, may contain a-z, A-Z, 0-9, _ and -
	 * @param name The display name to validate
	 */
	public static void validateName(String name) {
		if(!name.matches("[a-zA-Z0-9_\\-]+"))
			throw new IllegalArgumentException("Invalid name, man only contain a-z, A-Z, 0-9, _ and -");
	}

	/**
	 * Validates display name, may contain a-z, A-Z, 0-9, _, - and space
	 * @param displayName The display name to validate
	 */
	public static void validateDisplayName(String displayName) {
		if(!displayName.matches("[a-zA-Z0-9_\\- ]+"))
			throw new IllegalArgumentException("Invalid display name, man only contain a-z, A-Z, 0-9, _, - and space");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TokenType tokenType = (TokenType) o;
		return id == tokenType.id && tokenType.name.equals(this.name) && tokenType.getDisplayName().equals(this.displayName);
	}

	@Override
	public String toString() {
		return "TokenType{" +
				"id=" + id +
				", name='" + name + '\'' +
				", displayName='" + displayName + '\'' +
				'}';
	}
}
