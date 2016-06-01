package org.tmarciniak.cfmtp.listener;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.tmarciniak.cfmtp.config.ApplicationConfig;
import org.tmarciniak.cfmtp.config.MessagingConfig;
import org.tmarciniak.cfmtp.model.TradeMessage;
import org.tmarciniak.cfmtp.processor.TradeMessageProcessor;

@Component
public class TradeMessageListenerRaw {

	private static Log logger = LogFactory.getLog(TradeMessageListenerRaw.class);

	@Inject
	ApplicationConfig applicationConfig;

	@Inject
	TradeMessageProcessor tradeMessageProcessor;

	public String processTradeMessage(String tradeMessage) {
		try {
			logger.trace("Listener received: " + tradeMessage);
			tradeMessageProcessor.process(applicationConfig.objectMapper()
					.readValue(tradeMessage, TradeMessage.class));
		} catch (IOException e) {
			logger.error(e);
		}
		return null;
	}

}
