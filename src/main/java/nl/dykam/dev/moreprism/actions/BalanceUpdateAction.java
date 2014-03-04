package nl.dykam.dev.moreprism.actions;

import com.earth2me.essentials.User;
import me.botsko.prism.actionlibs.ActionType;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actions.GenericAction;
import me.botsko.prism.appliers.ChangeResult;
import me.botsko.prism.appliers.ChangeResultType;
import me.botsko.prism.appliers.PrismProcessType;
import net.ess3.api.MaxMoneyException;
import nl.dykam.dev.moreprism.MorePrismPlugin;
import nl.dykam.dev.moreprism.Util;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * Represents a before/after of balance when a transaction occurs.
 */
public class BalanceUpdateAction extends GenericAction {
    protected BalanceData actionData;

    public BalanceUpdateAction() {
        type = Type.get();
        actionData = new BalanceData();
    }

    public void setBalance(BigDecimal newBalance, BigDecimal oldBalance) {
        actionData = new BalanceData();
        actionData.newBalance = newBalance;
        actionData.oldBalance = oldBalance;
    }

    public void setData( String data ){
        this.data = data;
        if(this.data != null && !this.data.isEmpty()){
            actionData = gson.fromJson(this.data, BalanceData.class);
        }
    }

    @Override
    public void save() {
        data = gson.toJson(actionData);
    }

    public BalanceData getActionData(){
        return this.actionData;
    }

    public BigDecimal getNewBalance(){
        return actionData.newBalance;
    }
    public BigDecimal getOldBalance(){
        return actionData.oldBalance;
    }
    public BigDecimal getChange() { return getNewBalance().subtract(getOldBalance()); }
    public String getNiceName() {
        String oldDisplay = Util.displayCurrency(getOldBalance());
        String newDisplay = Util.displayCurrency(getNewBalance());
        return oldDisplay + " -> " + newDisplay;
    }

    @Override
    public ChangeResult applyRollback(Player player, QueryParameters parameters, boolean is_preview) {
        return applyBalance(player, parameters, is_preview);
    }

    @Override
    public ChangeResult applyRestore(Player player, QueryParameters parameters, boolean is_preview) {
        return applyBalance(player, parameters, is_preview);
    }

    /**
     * Tries to rollback or restore the change in balance
     * @param player
     * @param parameters
     * @param is_preview
     * @return Whether or not it succeeded. SKIPPED if the player's balance is maxed out.
     */
    protected ChangeResult applyBalance(Player player, QueryParameters parameters, boolean is_preview) {
        if(is_preview)
            return new ChangeResult(ChangeResultType.PLANNED);

        PrismProcessType pt = parameters.getProcessType();
        BigDecimal diff = null;
        switch (pt) {
            case ROLLBACK:
                diff = getChange().negate();
                break;
            case RESTORE:
                diff = getChange();
                break;
        }

        if(diff == null)
            return new ChangeResult(ChangeResultType.SKIPPED);

        try {
            User user = MorePrismPlugin.getEssentials().getUser(player);
            user.setMoney(user.getMoney().add(diff));
        } catch (MaxMoneyException e) {
            new ChangeResult(ChangeResultType.SKIPPED);
        }
        return new ChangeResult(ChangeResultType.APPLIED);
    }

    public static class Type extends ActionType {
        static final Type instance = new Type();

        private Type() {
            super("more-player-balanceupdate", false, true, true, "BalanceUpdateAction", "changed");
        }

        public static Type get() {
            return instance;
        }
    }

    public static class BalanceData {
        public BigDecimal newBalance;
        public BigDecimal oldBalance;
    }
}

