package nl.dykam.dev.moreprism;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.exceptions.InvalidActionException;
import net.milkbowl.vault.permission.Permission;
import nl.dykam.dev.moreprism.actions.BalanceUpdateAction;
import nl.dykam.dev.moreprism.parameters.GroupParameter;
import nl.dykam.dev.moreprism.parameters.RegionParameter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MorePrismPlugin extends JavaPlugin {
    private static Essentials essentials;
    private static Permission permission;
    private static WorldGuardPlugin worldGuard;

    public static final String[] ZERO_STRINGS = new String[0];
    private static MorePrismPlugin instance;
    private static Set<String> enabledActions;


    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadConfig();
        Bukkit.getPluginManager().registerEvents(new MorePrismListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0)
            return false;
        switch (args[0]) {
            case "reload":
                reloadConfig();
                sender.sendMessage(ChatColor.DARK_PURPLE + "[MorePrism] " + ChatColor.GREEN + "Reloaded config");
                return true;
        }
        return false;
    }

    @Override
    public void reloadConfig() {
        // Clean up
        ActionType actionType = BalanceUpdateAction.Type.get();
        Prism.getActionRegistry().getRegisteredAction().remove(actionType.getName());

        super.reloadConfig();

        // Load and register
        permission = getRegistration(Permission.class);
        essentials = getPlugin(Essentials.class, "Essentials");
        worldGuard = getPlugin(WorldGuardPlugin.class, "WorldGuard");

        if (worldGuard != null && getConfig().getBoolean("parameters.region.enabled", false)) {
            String[] aliases = getAliases("parameters.region.aliases");
            Prism.registerParameter(new RegionParameter(aliases));
        }
        if (permission != null && getConfig().getBoolean("parameters.group.enabled", false)) {
            Prism.registerParameter(new GroupParameter(permission, getAliases("parameters.group.aliases")));
        }

        enabledActions = new HashSet<String>(getConfig().getStringList("actions"));
        if(essentials != null && enabledActions.contains("player-balanceupdate")) {
            try {
                Prism.getActionRegistry().registerCustomAction(this, actionType);
                Prism.getHandlerRegistry().registerCustomHandler(this, BalanceUpdateAction.class);
            } catch (InvalidActionException e) {
                getLogger().severe(e.getMessage());
                Prism.getActionRegistry().getRegisteredAction().remove(actionType.getName());
            }
        }
    }

    private <TPlugin> TPlugin getPlugin(Class<TPlugin> clazz, String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if(plugin == null)
            return null;
        if(!clazz.isInstance(plugin))
            return null;
        return clazz.cast(plugin);
    }

    private <TService> TService getRegistration(Class<TService> clazz) {
        RegisteredServiceProvider<TService> registration = Bukkit.getServicesManager().getRegistration(clazz);
        return registration != null ? registration.getProvider() : null;
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    public static WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    public static Permission getPermission() {
        return permission;
    }

    public static Set<String> getEnabledActions() {
        return Collections.unmodifiableSet(enabledActions);
    }

    private String[] getAliases(String configPath) {
        List<String> aliases = getConfig().getStringList(configPath);
        if (aliases == null)
            return ZERO_STRINGS;
        return aliases.toArray(new String[aliases.size()]);
    }

    public static MorePrismPlugin instance() {
        return instance;
    }
}
