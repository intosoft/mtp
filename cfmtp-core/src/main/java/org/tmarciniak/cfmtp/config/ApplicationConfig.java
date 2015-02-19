package org.tmarciniak.cfmtp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.tmarciniak.cfmtp")
public class ApplicationConfig {

	public static final String DATE_FORMAT = "dd-MMM-yy HH:mm:ss";
	
}
