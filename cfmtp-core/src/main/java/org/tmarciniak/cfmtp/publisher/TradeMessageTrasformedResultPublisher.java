package org.tmarciniak.cfmtp.publisher;

import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.tmarciniak.cfmtp.config.ApplicationConfig;

import reactor.core.Reactor;
import reactor.event.Event;

@Service
public class TradeMessageTrasformedResultPublisher {

	@Inject
	Reactor reactor;

	@Inject
	CountDownLatch latch;

	public void publishResults(String transformedResult)
			throws InterruptedException {
		reactor.notify(ApplicationConfig.TRANSFORMED_RESULTS_CHANNEL, Event.wrap(transformedResult));
		latch.await();
	}
}
