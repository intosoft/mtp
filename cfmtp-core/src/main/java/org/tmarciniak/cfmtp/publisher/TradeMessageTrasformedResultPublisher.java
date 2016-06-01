package org.tmarciniak.cfmtp.publisher;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.tmarciniak.cfmtp.config.WebSocketConfig;
import org.tmarciniak.cfmtp.model.TradeMessagesDTO;

import reactor.core.Reactor;
import reactor.event.Event;

@Service
public class TradeMessageTrasformedResultPublisher {

	@Inject
	Reactor reactor;

	@Inject
	WebSocketConfig webSocketConfig;

	public void publishResults(TradeMessagesDTO tradeMessagesDTO)
			throws InterruptedException {
		reactor.notify(webSocketConfig.getTransformedResultsChannel(),
				Event.wrap(tradeMessagesDTO));
	}
}
