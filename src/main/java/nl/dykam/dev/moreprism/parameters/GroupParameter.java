package nl.dykam.dev.moreprism.parameters;

import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.parameters.SimplePrismParameterHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;


public class GroupParameter extends SimplePrismParameterHandler {
    private Permission provider;

    public GroupParameter(Permission provider, String... aliases) {
        super("Group", Pattern.compile("[\\w]+"), aliases);
        this.provider = provider;
    }

    @Override
    protected void process(QueryParameters queryParameters, String alias, String group, CommandSender commandSender) {
        World world;
        HashSet<String> groups = new HashSet<>(Arrays.asList(provider.getGroups()));
        if(!groups.contains(group))
            throw new IllegalArgumentException("Group " + group + " not found");
        if (commandSender instanceof Player) {
            world = ((Player) commandSender).getWorld();
        } else {
            world = Bukkit.getWorlds().get(0);
        }
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (provider.playerInGroup(world, offlinePlayer.getName(), group))
                queryParameters.addPlayerName(offlinePlayer.getName());
        }
        queryParameters.addPlayerName("DUMMY_PLAYER_____");
    }
}
