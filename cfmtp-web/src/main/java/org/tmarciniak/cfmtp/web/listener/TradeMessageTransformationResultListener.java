package org.tmarciniak.cfmtp.web.listener;

import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.tmarciniak.cfmtp.config.ApplicationConfig;
import org.tmarciniak.cfmtp.web.service.TradeMessageService;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.event.selector.Selectors;
import reactor.function.Consumer;

@Service
public class TradeMessageTransformationResultListener implements
		Consumer<Event<String>> {

	@Inject
	CountDownLatch latch;

	@Inject
	Reactor reactor;

	@Inject
	TradeMessageService tradeMessageService;

	@Inject
	ApplicationConfig applicationConfig;

	@PostConstruct
	public void init() {
		reactor.on(Selectors.$(ApplicationConfig.TRANSFORMED_RESULTS_CHANNEL),
				this);
	}

	@Override
	public void accept(Event<String> t) {
		tradeMessageService.sendTradeMessageTransformationResult(t.getData());
		latch.countDown();
	}
}
