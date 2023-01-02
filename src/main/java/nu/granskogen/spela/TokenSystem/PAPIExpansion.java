package nu.granskogen.spela.TokenSystem;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIExpansion extends PlaceholderExpansion {
	private Main plugin;
	private ConfigManager cfgm;

    /**
	 * Since we register the expansion inside our own plugin, we
	 * can simply use this method here to get an instance of our
	 * plugin.
	 *
	 * @param plugin The instance of our plugin.
	 * @param cfgm ConfigManager
	 */
    public PAPIExpansion(Main plugin, ConfigManager cfgm) {
        this.plugin = plugin;
		this.cfgm = cfgm;
	}

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "tokenssystem";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.entity.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        // %tokenssystem_votetokens%
        if(identifier.equals("votetokens")){
            return cfgm.getConfig().getString("votetokens", String.valueOf(plugin.getVoteToken(player.getUniqueId()).getAmount()));
        }

        // %tokenssystem_jobstokens%
        if(identifier.equals("jobstokens")){
            return cfgm.getConfig().getString("jobstokens", String.valueOf(plugin.getJobsToken(player.getUniqueId()).getAmount()));
        }
 
        // We return null if an invalid placeholder (f.e. %tokenssystem_placeholder3%) was provided
        return null;
    }

}
