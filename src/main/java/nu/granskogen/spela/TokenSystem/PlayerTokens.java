package nu.granskogen.spela.TokenSystem;

public class PlayerTokens {
	private String name;
	private int amount = 0;
	
	// Constructor
	public PlayerTokens(String name) {
		this.name = name;
	}
	

	public void add(int value) {
		this.amount += value;
	}
	
	public void remove(int value) {
		this.amount -= value;
	}
	
	public void setAmount(int value) {
		this.amount = value;
	}
	
	
	// Getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getAmount() {
		return this.amount;
	}
}
