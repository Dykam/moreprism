package nl.dykam.dev.moreprism.parameters;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.parameters.SimplePrismParameterHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegionParameter extends SimplePrismParameterHandler {

    public RegionParameter(String... aliases) {
        super("Region", Pattern.compile("(?:([\\w]+):)?([\\w]+)"), aliases);
    }

    @Override
    protected void process(QueryParameters queryParameters, String alias, String input, CommandSender commandSender) {
        Matcher matcher = Pattern.compile("(?:([\\w]+):)?([\\w]+)").matcher(input);
        matcher.find();
        String worldName = matcher.group(1);
        String regionName = matcher.group(2);
        if (worldName != null) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                throw new IllegalArgumentException("World " + worldName + " not found.");
            }
            ProtectedRegion region = WorldGuardPlugin.inst().getRegionManager(world).getRegion(regionName);
            setRegionParameters(queryParameters, regionName, region);
        } else {
            ProtectedRegion region;
            region = getRegion(commandSender, regionName);
            setRegionParameters(queryParameters, regionName, region);
        }
    }

    private void setRegionParameters(QueryParameters queryParameters, String regionName, ProtectedRegion region) {
        if (region == null) {
            throw new IllegalArgumentException("Region " + regionName + " not found.");
        }
        BlockVector minimumPoint = region.getMinimumPoint();
        Vector minimumVector = new Vector(minimumPoint.getX(), minimumPoint.getY(), minimumPoint.getZ());
        queryParameters.setMinLocation(minimumVector);
        BlockVector maximumPoint = region.getMaximumPoint();
        Vector maximumVector = new Vector(maximumPoint.getX(), maximumPoint.getY(), maximumPoint.getZ());
        queryParameters.setMaxLocation(maximumVector);
        Bukkit.broadcastMessage(minimumVector.toString());
        Bukkit.broadcastMessage(maximumVector.toString());
    }

    private ProtectedRegion getRegion(CommandSender commandSender, String regionName) {
//        if (commandSender instanceof Player) {
//            World world = ((Player) commandSender).getWorld();
//            ProtectedRegion region = WorldGuardPlugin.inst().getRegionManager(world).getRegion(regionName);
//            if (region != null) {
//                return region;
//            }
//        }
        for (World world : Bukkit.getWorlds()) {
            ProtectedRegion region = WorldGuardPlugin.inst().getRegionManager(world).getRegion(regionName);
            if (region != null) {
                return region;
            }
        }
        return null;
    }
}
