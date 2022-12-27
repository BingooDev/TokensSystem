package nu.granskogen.spela.TokenSystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
	Main pl = Main.getInstance();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You need to be a player!");
			return false;
		}
		
		Player player = (Player) sender;
		
		VoteToken voteToken = pl.getVoteToken(player.getUniqueId());
		
		if(args[0].equalsIgnoreCase("add")) {
			voteToken.addAmount(Integer.parseInt(args[1]));
		}
		
		if(args[0].equalsIgnoreCase("remove")) {
			voteToken.removeAmount(Integer.parseInt(args[1]));
		}
		
		player.sendMessage("You have " + voteToken.getAmount() + " tokens");
		
		return false;
	}
	
}
