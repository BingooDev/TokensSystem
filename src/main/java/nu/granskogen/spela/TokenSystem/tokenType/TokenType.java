package nu.granskogen.spela.TokenSystem.tokenType;

import java.util.Objects;

public class TokenType {
	private int id;
	private String name;

	public TokenType(int id, String name) {
		this.id = id;
		setName(name);
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

	public static void validateName(String name) {
		if(!name.matches("[a-zA-Z0-9_-]+"))
			throw new IllegalArgumentException("Invalid name, man only contain a-z, A-Z, 0-9, _ and -");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TokenType tokenType = (TokenType) o;
		return id == tokenType.id && tokenType.name.equals(this.name);
	}

	@Override
	public String toString() {
		return "TokenType{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
