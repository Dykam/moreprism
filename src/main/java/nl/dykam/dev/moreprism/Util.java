package nl.dykam.dev.moreprism;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.utils.NumberUtil;

import java.math.BigDecimal;

public class Util {
    public static String displayCurrency(BigDecimal balance) {
        Essentials essentials = MorePrismPlugin.getEssentials();
        if(essentials == null) {
            return "$" + balance.toPlainString();
        } else {
            return NumberUtil.displayCurrency(balance, essentials);
        }
    }
}
