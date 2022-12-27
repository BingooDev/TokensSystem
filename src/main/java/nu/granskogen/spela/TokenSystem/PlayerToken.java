package nu.granskogen.spela.TokenSystem;

public class PlayerToken {
	private String name;
	private int amount = 0;
	
	// Constructor
	public PlayerToken(String name) {
		this.name = name;
	}
	

	public void addAmount(int value) {
		this.amount += value;
	}
	
	public void removeAmount(int value) {
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
