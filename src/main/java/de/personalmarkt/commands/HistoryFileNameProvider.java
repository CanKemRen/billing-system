package de.personalmarkt.commands;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 06.07.17
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HistoryFileNameProvider extends DefaultHistoryFileNameProvider {

	public String getHistoryFileName() {
		return "occupation-importer.log";
	}

	@Override
	public String getProviderName() {
		return "occupation-importer history file name provider";

	}
}
