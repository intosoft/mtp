package org.tmarciniak.mtp.web.listener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.tmarciniak.mtp.config.WebSocketConfig;
import org.tmarciniak.mtp.model.TradeMessagesDTO;
import org.tmarciniak.mtp.web.websocket.TradeMessageWebSocketService;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.event.selector.Selectors;
import reactor.function.Consumer;

@Service
public class TradeMessageTransformationResultListener implements
		Consumer<Event<TradeMessagesDTO>> {

	@Inject
	Reactor reactor;

	@Inject
	TradeMessageWebSocketService tradeMessageWebSocketService;

	@Inject
	WebSocketConfig webSocketConfig;

	@PostConstruct
	public void init() {
		reactor.on(Selectors.$(webSocketConfig.getTransformedResultsChannel()),
				this);
	}

	@Override
	public void accept(Event<TradeMessagesDTO> event) {
		tradeMessageWebSocketService
				.sendTradeMessageTransformationResult(event.getData(),null);
	}
}
