package nu.granskogen.spela.TokenSystem.bossShopPro;

import org.black_ixx.bossshop.BossShop;
import org.black_ixx.bossshop.api.BossShopAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;


public class BSPHook {
	private BossShop bs; //BossShopPro Plugin Instance

    public BSPHook() {
        Plugin plugin = (Plugin) Bukkit.getPluginManager().getPlugin("BossShopPro"); // Get BossShopPro instance
 
        if (plugin == null) { // Not installed?
            System.out.print("[BSP Hook] BossShopPro was not found... you can download it here: https://www.spigotmc.org/resources/25699/");
            return;
        }
 
        bs = (BossShop) plugin; // Success :)
 
    }
    
    public BossShopAPI getBSPAPI() {
        return bs.getAPI(); // Returns BossShopPro API class instance
    }

}
