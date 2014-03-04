package nl.dykam.dev.moreprism;

import me.botsko.prism.actionlibs.RecordingQueue;
import net.ess3.api.events.UserBalanceUpdateEvent;
import nl.dykam.dev.moreprism.actions.BalanceUpdateAction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MorePrismListener implements Listener {
    @EventHandler
    private void onUserBalanceUpdate(UserBalanceUpdateEvent event) {
        if(MorePrismPlugin.getEssentials() == null || !MorePrismPlugin.getEnabledActions().contains("player-balanceupdate"))
            return;
        BalanceUpdateAction action = new BalanceUpdateAction();
        action.setBalance(event.getNewBalance(), event.getOldBalance());
        action.setPlayerName(event.getPlayer());
        action.setLoc(event.getPlayer().getLocation());
        RecordingQueue.addToQueue(action);
    }
}
