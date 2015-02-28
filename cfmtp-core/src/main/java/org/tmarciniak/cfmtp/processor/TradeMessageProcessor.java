package org.tmarciniak.cfmtp.processor;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmarciniak.cfmtp.model.TradeMessage;
import org.tmarciniak.cfmtp.publisher.TradeMessageTrasformedResultPublisher;

/**
 * The TradeMessageProcessor is responsible for retrieving results from incoming data.
 *         
 * @author tomasz.marciniak
 */
@Named
public class TradeMessageProcessor {

	private static Log logger = LogFactory.getLog(TradeMessageProcessor.class);

	@Inject
	TradeMessageTrasformedResultPublisher tradeMessageTrasformedResultPublisher;

	public void process(TradeMessage tradeMessage) {
		logger.trace("Processor received: " + tradeMessage);
		try {
			String result = applyAlgorithm(tradeMessage);
			tradeMessageTrasformedResultPublisher.publishResults(result);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	private String applyAlgorithm(TradeMessage tradeMessage) {
		return "";
	}
}