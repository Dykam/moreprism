package nl.dykam.dev.moreprism;

import me.botsko.prism.Prism;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MorePrismPlugin extends JavaPlugin {

    public static final String[] ZERO_STRINGS = new String[0];

    @Override
    public void onEnable() {

    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (getConfig().getBoolean("region.enabled", false)) {
            Prism.registerParameter(new RegionParameter(getAliases("region.aliases")));
        }
        if (getConfig().getBoolean("group.enabled", false)) {
            RegisteredServiceProvider<Permission> registration = Bukkit.getServicesManager().getRegistration(Permission.class);
            Permission provider = registration.getProvider();
            Prism.registerParameter(new GroupParameter(provider, getAliases("group.aliases")));
        }
    }

    private String[] getAliases(String configPath) {
        List<String> aliases = getConfig().getStringList(configPath);
        if (aliases == null)
            return ZERO_STRINGS;
        return aliases.toArray(new String[aliases.size()]);
    }
}
