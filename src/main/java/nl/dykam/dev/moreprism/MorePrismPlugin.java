package nl.dykam.dev.moreprism;

import me.botsko.prism.Prism;
import org.bukkit.plugin.java.JavaPlugin;

public class MorePrismPlugin extends JavaPlugin {
    @Override
    public void onEnable() {

    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if(getConfig().getBoolean("worldguard.region", false)) {
            Prism.registerParameter(new RegionParameter());
        }
        if(getConfig().getBoolean("vault.group", false)) {
            Prism.registerParameter(new GroupParameter());
        }
    }
}
