package de.personalmarkt.config;

import java.io.IOException;

import org.springframework.shell.Bootstrap;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 05.07.17
 */

public class ApplicationConfig {

	/**
	 * Main class that delegates to Spring Shell's Bootstrap class in order to simplify debugging inside an IDE
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Bootstrap.main(args);

	}
}
