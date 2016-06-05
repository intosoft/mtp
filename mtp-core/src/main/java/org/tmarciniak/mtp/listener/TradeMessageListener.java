package org.tmarciniak.mtp.listener;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.tmarciniak.mtp.config.ApplicationConfig;
import org.tmarciniak.mtp.config.MessagingConfig;
import org.tmarciniak.mtp.model.TradeMessage;
import org.tmarciniak.mtp.processor.TradeMessageProcessor;

@Component
public class TradeMessageListener {

	private static Log logger = LogFactory.getLog(TradeMessageListener.class);

	@Inject
	ApplicationConfig applicationConfig;

	@Inject
	TradeMessageProcessor tradeMessageProcessor;

	@JmsListener(destination = MessagingConfig.MTP_QUEUE_NAME, containerFactory = MessagingConfig.MTP_JMS_LISTENER_CONTAINER_FACTORY)
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
