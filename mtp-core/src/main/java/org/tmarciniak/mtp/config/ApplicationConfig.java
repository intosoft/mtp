package org.tmarciniak.mtp.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@PropertySource("classpath:config/application.properties")
@ComponentScan(basePackages = "org.tmarciniak.mtp")
public class ApplicationConfig {
	
	@Value("${mtp.dateFormat}")
	private String dateFormat;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		DateFormat df = new SimpleDateFormat(getDateFormat(), Locale.ENGLISH);
		objectMapper.setDateFormat(df);
		return objectMapper;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
}
