package nl.dykam.dev.moreprism;

import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.parameters.PrismParameterHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GroupParameter implements PrismParameterHandler {
    @Override
    public String getName() {
        return "Group";
    }

    @Override
    public String[] getHelp() {
        return new String[0];
    }

    @Override
    public Pattern getArgumentPattern() {
        return Pattern.compile("(gr|group):([\\w]+)");
    }

    @Override
    public void process(QueryParameters query, Matcher input, CommandSender sender) {
        String group = input.group(2);
        RegisteredServiceProvider<Permission> registration = Bukkit.getServicesManager().getRegistration(Permission.class);
        Permission provider = registration.getProvider();
        World world;
        if(sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            world = Bukkit.getWorlds().get(0);
        }
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if(provider.playerInGroup(world, offlinePlayer.getName(),   group))
                query.addPlayerName(offlinePlayer.getName());
        }
    }

    @Override
    public void defaultTo(QueryParameters query, CommandSender sender) {

    }
}
