package org.tmarciniak.cfmtp.processor;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.tmarciniak.cfmtp.config.ApplicationConfig;
import org.tmarciniak.cfmtp.mapping.DozerMapper;
import org.tmarciniak.cfmtp.model.TradeMessage;
import org.tmarciniak.cfmtp.model.TradeMessageDTO;
import org.tmarciniak.cfmtp.model.TradeMessagesDTO;
import org.tmarciniak.cfmtp.model.TradeMessagesDTO.MessageType;
import org.tmarciniak.cfmtp.publisher.TradeMessageTrasformedResultPublisher;
import org.tmarciniak.cfmtp.service.TradeMessageService;

/**
 * The TradeMessageProcessor is responsible for retrieving results from incoming
 * data.
 * 
 * @author tomasz.marciniak
 */
@Component
public class TradeMessageProcessor {

	private static Log logger = LogFactory.getLog(TradeMessageProcessor.class);

	@Inject
	TradeMessageService tradeMessageService;

	@Inject
	ApplicationConfig applicationConfig;

	@Inject
	DozerMapper dozerMapper;

	@Inject
	TradeMessageTrasformedResultPublisher tradeMessageTrasformedResultPublisher;

	public void process(TradeMessage tradeMessage) {
		logger.trace("Processor received: " + tradeMessage);
		try {
			save(tradeMessage);
			
			TradeMessagesDTO tradeMessagesDTO = new TradeMessagesDTO();
			TradeMessageDTO tradeMessageDTO = dozerMapper.map(tradeMessage, TradeMessageDTO.class);
			tradeMessagesDTO.setTradeMessages(Arrays.asList(tradeMessageDTO));
			tradeMessagesDTO.setCurrencyFrom(tradeMessage.getCurrencyFrom());
			tradeMessagesDTO.setCurrencyTo(tradeMessage.getCurrencyTo());
			tradeMessagesDTO.setMessageType(MessageType.UPDATE);
			tradeMessageTrasformedResultPublisher.publishResults(tradeMessagesDTO);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}

	private void save(TradeMessage tradeMessage) {
		tradeMessageService.add(tradeMessage);
	}
}