package org.tmarciniak.cfmtp.web.service;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Service;

@Service
public class TradeMessageService implements
		ApplicationListener<BrokerAvailabilityEvent> {

	@Inject
	private final MessageSendingOperations<String> messagingTemplate;

	private AtomicBoolean brokerAvailable = new AtomicBoolean();

	@Inject
	public TradeMessageService(
			MessageSendingOperations<String> messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public void sendTradeMessageTransformationResult(String result) {
		this.messagingTemplate.convertAndSend("/topic/tradeMessages", result);
	}

	@Override
	public void onApplicationEvent(BrokerAvailabilityEvent event) {
		this.brokerAvailable.set(event.isBrokerAvailable());
	}

}
