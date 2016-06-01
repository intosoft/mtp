package org.tmarciniak.cfmtp.web.websocket;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tmarciniak.cfmtp.config.ApplicationConfig;
import org.tmarciniak.cfmtp.model.TradeMessagesDTO;
import org.tmarciniak.cfmtp.service.TradeMessageService;
import org.tmarciniak.cfmtp.web.websocket.support.client.StompSession;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class TradeMessageWebSocketService implements
		ApplicationListener<BrokerAvailabilityEvent> {


	private static Log logger = LogFactory
			.getLog(TradeMessageWebSocketService.class);

	public static final String TOPIC_TRADE_MESSAGES = "/topic/trade.messages.";
	public static final String TOPIC_CURRENCIES_PAIRS = "/topic/currencies";

	private AtomicBoolean brokerAvailable = new AtomicBoolean();

	@Inject
	private final MessageSendingOperations<String> messagingTemplate;

	@Inject
	private final TradeMessageService tradeMessageService;

	@Inject
	private ApplicationConfig applicationConfig;

	@Inject
	public TradeMessageWebSocketService(
			MessageSendingOperations<String> messagingTemplate,
			TradeMessageService tradeMessageService,
			ApplicationConfig applicationConfig) {
		this.messagingTemplate = messagingTemplate;
		this.tradeMessageService = tradeMessageService;
		this.applicationConfig = applicationConfig;
	}

	public void sendTradeMessageTransformationResult(
			TradeMessagesDTO tradeMessagesDTO, StompSession session) {
		String jsonTradeMessage = null;

		try {
			jsonTradeMessage = applicationConfig.objectMapper()
					.writeValueAsString(tradeMessagesDTO);
		} catch (JsonProcessingException e) {
			logger.error(e);
		}

		if (jsonTradeMessage != null) {
			if(session!=null){
				session.send(createQueueName(tradeMessagesDTO), jsonTradeMessage);
			}else{
				this.messagingTemplate.convertAndSend(
						createQueueName(tradeMessagesDTO), jsonTradeMessage);
			}
		}
	}

	private String createQueueName(TradeMessagesDTO tradeMessagesDTO) {
		return TOPIC_TRADE_MESSAGES + tradeMessagesDTO.getCurrencyFrom()
				+ "." + tradeMessagesDTO.getCurrencyTo();
	}

	@Scheduled(fixedDelay = 2000)
	public void sendAvaliableCurrenciesPairs() {
		this.messagingTemplate.convertAndSend(TOPIC_CURRENCIES_PAIRS,
				tradeMessageService.getCurrenciesPairs());
	}

	@Override
	public void onApplicationEvent(BrokerAvailabilityEvent event) {
		this.brokerAvailable.set(event.isBrokerAvailable());
	}

}
