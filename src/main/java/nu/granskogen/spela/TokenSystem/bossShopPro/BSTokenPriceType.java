package nu.granskogen.spela.TokenSystem.bossShopPro;

import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.token.Token;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.prices.BSPriceTypeNumber;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

public class BSTokenPriceType extends BSPriceTypeNumber {
	private Main pl = Main.getInstance();
	private final String name;
	private final Class<? extends Token> tokenType;

	public BSTokenPriceType(String name, Class<? extends Token> tokenType) {
		this.name = name;
		this.tokenType = tokenType;
		updateNames();
	}

	@Override
	public boolean isIntegerValue() {
		return true;
	}

	@Override
	public String takePrice(Player p, BSBuy buy, Object price, ClickType clickType, int multiplier) {
		double points = ClassManager.manager.getMultiplierHandler().calculatePriceWithMultiplier(p, buy, clickType, (Double) price) * multiplier;
		Token tokens = pl.getToken(tokenType, p.getUniqueId());
		tokens.removeAmount((int) points);
		return getDisplayBalance(p, buy, price, clickType);
	}

	@Override
	public boolean hasPrice(Player p, BSBuy buy, Object price, ClickType clickType, int multiplier, boolean messageOnFailure) {
		double points = ClassManager.manager.getMultiplierHandler().calculatePriceWithMultiplier(p, buy, clickType, (Double) price) * multiplier;
		Token tokens = pl.getToken(tokenType, p.getUniqueId());
		if (tokens.getAmount() < points) {
			String message = "§cDu har inte tillräckligt många " + tokens.getTokenType().getName() + "s.";
			if (messageOnFailure) {
				// Send the message to BossShopPro
				p.sendMessage(ClassManager.manager.getStringManager().transform(message, buy, buy.getShop(), null, p));
			}
			return false;
		}
		return true;
	}

	@Override
	public String getDisplayBalance(Player p, BSBuy buy, Object price, ClickType clickType) {
		Token tokens = pl.getToken(tokenType, p.getUniqueId());
		return "" + tokens.getAmount();
	}

	/**
	 * receives the price object from the config and transforms it into the type of object needed later
	 * @param o price object, wrong type
	 * @param force_final_state
	 * @return Price object with right type
	 */
	@Override
	public Object createObject(Object o, boolean force_final_state) {
		return InputReader.getDouble(o, -1);
	}

	public boolean validityCheck(String item_name, Object o){ // validates the price object
		if ((Double)o!=-1) {
			return true;
		}
		ClassManager.manager.getBugFinder().severe("Was not able to create ShopItem "+item_name+"! The price object needs to be a valid number. Example: '7' or '12'.");
		return false;
	}

	/**
	 * executed once when BSP starts in case the PriceType is used in a shop
	 */
	@Override
	public void enableType() {}

	@Override
	public String getDisplayPrice(Player p, BSBuy buy, Object price, ClickType clickType) {
		return "" + price;
	}

	/**
	 * Specifies the names of the PriceType users can enter in their shop configs
	 * @return Array of valid names
	 */
	@Override
	public String[] createNames() {
		return new String[]{ name };
	}

	@Override
	public boolean mightNeedShopUpdate() {
		// Set to true, in case placeholders have been updated.
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pl, name, tokenType);
	}
}
