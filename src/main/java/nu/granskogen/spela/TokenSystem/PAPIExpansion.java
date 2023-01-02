package nu.granskogen.spela.TokenSystem;

import nu.granskogen.spela.TokenSystem.token.TokenRepository;
import nu.granskogen.spela.TokenSystem.tokenType.TokenType;
import nu.granskogen.spela.TokenSystem.tokenType.TokenTypeRepository;
import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {
	private Main plugin;
	private ConfigManager cfgm;
	private TokenTypeRepository tokenTypeRepository;
	private TokenRepository tokenRepository;

    /**
	 * Since we register the expansion inside our own plugin, we
	 * can simply use this method here to get an instance of our
	 * plugin.
	 *  @param plugin The instance of our plugin.
	 * @param cfgm ConfigManager
	 * @param tokenTypeRepository
	 * @param tokenRepository
	 */
    public PAPIExpansion(Main plugin, ConfigManager cfgm, TokenTypeRepository tokenTypeRepository, TokenRepository tokenRepository) {
        this.plugin = plugin;
		this.cfgm = cfgm;
		this.tokenTypeRepository = tokenTypeRepository;
		this.tokenRepository = tokenRepository;
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
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier){

        if(player == null){
            return "";
        }

		TokenType tokenType = tokenTypeRepository.getTokenTypeByName(identifier);
		if(tokenType == null) {
			// We return null if an invalid placeholder (e.g. %tokenssystem_noneExistentType%) was provided
			return null;
		}

		return String.valueOf(tokenRepository.getToken(tokenType, player.getUniqueId()).getAmount());
    }

}
