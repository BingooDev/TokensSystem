package nu.granskogen.spela.TokenSystem.bossShopPro;

import nu.granskogen.spela.TokenSystem.Main;
import nu.granskogen.spela.TokenSystem.PlayerToken;
import nu.granskogen.spela.TokenSystem.VoteToken;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.prices.BSPriceTypeNumber;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BSVoteTokenPriceType extends BSTokenPriceType {

	public BSVoteTokenPriceType() {
		super("VoteToken", VoteToken.class);
	}
}
