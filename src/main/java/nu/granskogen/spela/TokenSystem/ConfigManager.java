package nu.granskogen.spela.TokenSystem;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;

public class ConfigManager {

	// Files and file configs here
	private FileConfiguration languagecfg;
	private File languagefile;
	private FileConfiguration configcfg;
	private File configfile;

	/**
	 * Instantiates a ConfigManager
	 * @param dataFolder The folder where the plugins data is stored
	 * @param isTest Should be true if an automated test is using ConfigManager, will use test version of config.yml
	 */
	public ConfigManager(File dataFolder, boolean isTest) {
		setup(dataFolder, isTest);
	}
	// ---------------------------

	private void setup(File pluginFolder, boolean isTest) {
		if (!pluginFolder.exists()) {
			pluginFolder.mkdir();
		}

		languagefile = new File(pluginFolder, "language.yml");
		configfile = new File(pluginFolder, "config.yml");
		if(isTest) {
			String path = Paths.get("").toAbsolutePath().toString();
			configfile = new File(path+"/src/test/config.yml");
		}

		if (!languagefile.exists()) {
			InputStream stream = this.getClass().getResourceAsStream("/language.yml");
			File dest = new File(pluginFolder, "language.yml");
			copy(stream, dest);
		}
		if (!configfile.exists()) {
			InputStream stream = this.getClass().getResourceAsStream("/config.yml");
			File dest = new File(pluginFolder, "config.yml");
			copy(stream, dest);
		}

		configcfg = YamlConfiguration.loadConfiguration(configfile);
		languagecfg = YamlConfiguration.loadConfiguration(languagefile);
	}

	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getLanguage() {
		return languagecfg;
	}

	public void saveLanguage() {
		try {
			languagecfg.save(languagefile);
		} catch (IOException e) {
			System.err.println("§4Could not save the language.yml file!");
		}
	}

	public void reloadLanguage() {
		languagecfg = YamlConfiguration.loadConfiguration(languagefile);
	}

	public FileConfiguration getConfig() {
		return configcfg;
	}

	public void saveConfig() {
		try {
			configcfg.save(configfile);
		} catch (IOException e) {
			System.err.println("§4Could not save the config.yml file!");
		}
	}

	public void reloadConfig() {
		configcfg = YamlConfiguration.loadConfiguration(configfile);
	}
}