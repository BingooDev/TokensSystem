package nu.granskogen.spela.TokenSystem;

import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.prices.BSPriceTypeNumber;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BSVoteTokenPriceType extends BSPriceTypeNumber {
	Main pl = Main.getInstance();
	
	public BSVoteTokenPriceType() {
		updateNames();
	}

	// receives the price object from the config and transforms it into the type of object needed later
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

    public void enableType(){ // executed once when BSP starts in case the PriceType is used in a shop
    // can be used to register dependencies like Vault
    }


    @Override
    public boolean hasPrice(Player p, BSBuy buy, Object price, ClickType clickType, int multiplier, boolean messageOnFailure) {
        double points = ClassManager.manager.getMultiplierHandler().calculatePriceWithMultiplier(p, buy, clickType, (Double) price) * multiplier;
        if (pl.getVoteToken(p.getUniqueId()).getAmount() < points) {
            String message = "§cDu har inte tillräckligt många VoteTokens.";
            if (message != null && messageOnFailure) {
                p.sendMessage(ClassManager.manager.getStringManager().transform(message, buy, buy.getShop(), null, p));
            }
            return false;
        }
        return true;
    }

    @Override
    public String takePrice(Player p, BSBuy buy, Object price, ClickType clickType, int multiplier) {
        double points = ClassManager.manager.getMultiplierHandler().calculatePriceWithMultiplier(p, buy, clickType, (Double) price) * multiplier;
 
        pl.getVoteToken(p.getUniqueId()).remove((int) points);
        return getDisplayBalance(p, buy, price, clickType);
    }

    @Override
    public String getDisplayBalance(Player p, BSBuy buy, Object price, ClickType clickType) {
    	VoteToken voteToken = pl.getVoteToken(p.getUniqueId());
        double balance_points = voteToken.getAmount();
        return "" + balance_points;
    }
 
    @Override
    public String getDisplayPrice(Player p, BSBuy buy, Object price, ClickType clickType) {
        return "" + price;
    }


    @Override
    public String[] createNames() { // the names of the PriceType users can enter in their shop configs
        return new String[]{ "VoteToken" };
    }

    public boolean supportsMultipliers() {
        return false; // makes RewardTypes like BuyAll possible
    }

    @Override
    public boolean mightNeedShopUpdate() {
        return true; // whether the execution of this PriceType requires the shop to be updated (for example because placeholders might have changed)
    }

    @Override
    public boolean isIntegerValue() {
        return true; // needs to be defined because the class extends BSPriceTypeNumber
    }
}
