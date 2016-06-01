package org.tmarciniak.cfmtp.config;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;

@Configuration
@PropertySource("classpath:config/websocket.properties")
@ComponentScan(basePackages = "org.tmarciniak.cfmtp")
public class WebSocketConfig {

	@Value(value = "cfmtp.transformedResultsChannel")
	private String transformedResultsChannel;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public Environment webSocketEnv() {
		return new Environment();
	}

	@Bean
	@Inject
	public Reactor reactor(Environment webSocketEnv) {
		return Reactors.reactor().env(webSocketEnv)
				.dispatcher(Environment.THREAD_POOL).get();
	}

	public String getTransformedResultsChannel() {
		return transformedResultsChannel;
	}

	public void setTransformedResultsChannel(String transformedResultsChannel) {
		this.transformedResultsChannel = transformedResultsChannel;
	}
}
