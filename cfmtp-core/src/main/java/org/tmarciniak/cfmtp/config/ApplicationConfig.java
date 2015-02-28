package org.tmarciniak.cfmtp.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import javax.jms.ConnectionFactory;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableJms
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "org.tmarciniak.cfmtp")
public class ApplicationConfig {

	public static final String CFMTP_QUEUE_NAME = "CFMTP.QUEUE";
	public static final String CFMTP_JMS_LISTENER_CONTAINER_FACTORY = "cfmtpJmsListenerContainerFactory";
	public static final String TRANSFORMED_RESULTS_CHANNEL = "transformedResults";
	private static final int NUMBER_OF_TRADE_MESSAGES = 10;

	@Value("${cfmtp.brokerURL}")
	private String brokerURL;

	@Value("${cfmtp.dateFormat}")
	private String dateFormat;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DefaultJmsListenerContainerFactory cfmtpJmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(getConnectionFactory());
		factory.setDestinationResolver(getDestinationResolver());
		factory.setConcurrency("1");
		return factory;
	}

	@Bean
	public CountDownLatch latch() {
		return new CountDownLatch(NUMBER_OF_TRADE_MESSAGES);
	}

	@Bean
	public Environment env() {
		return new Environment();
	}

	@Bean
	public Reactor reactor(Environment env) {
		return Reactors.reactor().env(env).dispatcher(Environment.THREAD_POOL)
				.get();
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat(getDateFormat(), Locale.ENGLISH);
		objectMapper.setDateFormat(df);
		return objectMapper;
	}

	private DestinationResolver getDestinationResolver() {
		return new DynamicDestinationResolver();
	}

	public ConnectionFactory getConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		activeMQConnectionFactory.setBrokerURL(getBrokerURL());
		return activeMQConnectionFactory;
	}

	public String getBrokerURL() {
		return brokerURL;
	}

	public void setBrokerURL(String brokerURL) {
		this.brokerURL = brokerURL;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

}
