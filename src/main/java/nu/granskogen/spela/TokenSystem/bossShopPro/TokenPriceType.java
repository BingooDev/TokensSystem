package nu.granskogen.spela.TokenSystem.bossShopPro;

import nu.granskogen.spela.TokenSystem.PlayerToken;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.prices.BSPriceTypeNumber;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class TokenPriceType extends BSPriceTypeNumber {
	private final String name;
	private final Class<PlayerToken> tokenType;

	// TODO: implement
	public TokenPriceType(String name, Class<PlayerToken> tokenType) {
		this.name = name;
		this.tokenType = tokenType;
	}

	@Override
	public boolean isIntegerValue() {
		return true;
	}

	// TODO: implement
	@Override
	public String takePrice(Player player, BSBuy bsBuy, Object o, ClickType clickType, int i) {
		return null;
	}

	// TODO: implement
	@Override
	public boolean hasPrice(Player player, BSBuy bsBuy, Object o, ClickType clickType, int i, boolean b) {
		return false;
	}

	// TODO: implement
	@Override
	public String getDisplayBalance(Player player, BSBuy bsBuy, Object o, ClickType clickType) {
		return null;
	}

	@Override
	public Object createObject(Object o, boolean b) {
		// receives the price object from the config and transforms it into the type of object needed later
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

	// TODO: implement
	@Override
	public String getDisplayPrice(Player player, BSBuy bsBuy, Object o, ClickType clickType) {
		return "" + price;
	}

	@Override
	public String[] createNames() {
		return new String[]{ name };
	}

	// TODO: comment
	@Override
	public boolean mightNeedShopUpdate() {
		// Set to true,
		return true;
	}
}
