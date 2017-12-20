package de.personalmarkt.commands;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 06.07.17
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PromptProvider extends DefaultPromptProvider {

	@Override
	public String getPrompt() {
		return "oi-shell>";
	}

	@Override
	public String getProviderName() {
		return "Occupation-Importer prompt provider";
	}

}
