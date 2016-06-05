package org.tmarciniak.mtp.config;

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

@Configuration
@EnableJms
@PropertySource("classpath:config/messaging.properties")
@ComponentScan(basePackages = "org.tmarciniak.mtp")
public class MessagingConfig {

	private static final String CONCURRENCY_LEVEL = "1";
	public static final String MTP_QUEUE_NAME = "MTP.QUEUE";
	public static final String MTP_JMS_LISTENER_CONTAINER_FACTORY = "mtpJmsListenerContainerFactory";

	@Value("${mtp.brokerURL}")
	private String brokerURL;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DefaultJmsListenerContainerFactory mtpJmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(getConnectionFactory());
		factory.setDestinationResolver(getDestinationResolver());
		factory.setConcurrency(CONCURRENCY_LEVEL);
		return factory;
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

}
