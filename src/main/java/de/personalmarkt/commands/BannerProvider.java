package de.personalmarkt.commands;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 06.07.17
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BannerProvider extends DefaultBannerProvider {

	public String getBanner() {
		StringBuffer buf = new StringBuffer();
		buf.append("");
		buf.append("\n");
		buf.append("========================================================================================" + OsUtils.LINE_SEPARATOR);
		buf.append("*                                                                  					*" + OsUtils.LINE_SEPARATOR);
		buf.append("*                        OccupationImporter                        					*" + OsUtils.LINE_SEPARATOR);
		buf.append("* oi simple --pn jobrobot --pi 245 --f jobrobot.csv --path /tmp    					*" + OsUtils.LINE_SEPARATOR);
		buf.append("* 		 		 		 	or	 		 		 		 					*" + OsUtils.LINE_SEPARATOR);
		buf.append("* oi simple --pn jobrobor --pi 1234 --f jobrobot.xlsx --path /Users/kemal/tmp      	*" + OsUtils.LINE_SEPARATOR);
		buf.append("*                          IndustryImporter   		                           	  *" + OsUtils.LINE_SEPARATOR);
		buf.append("* ii simple --pn jobrobot --pi 1234 --f jobrobot_ind.xlsx --path /Users/kemal/tmp  	*" + OsUtils.LINE_SEPARATOR);
		buf.append("========================================================================================" + OsUtils.LINE_SEPARATOR);
		buf.append("Version:" + this.getVersion());
		return buf.toString();
	}

	public String getVersion() {
		return "1.2.3";
	}

	public String getWelcomeMessage() {
		return "Welcome to OccupationImporter CLI";
	}

	@Override
	public String getProviderName() {
		return "OccupationImporter Banner";
	}

}
