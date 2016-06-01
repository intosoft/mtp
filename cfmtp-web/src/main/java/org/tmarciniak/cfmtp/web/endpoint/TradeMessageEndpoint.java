package org.tmarciniak.cfmtp.web.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.tmarciniak.cfmtp.config.MessagingConfig;
import org.tmarciniak.cfmtp.mapping.DozerMapper;
import org.tmarciniak.cfmtp.model.TradeMessage;
import org.tmarciniak.cfmtp.model.TradeMessageDTO;
import org.tmarciniak.cfmtp.model.TradeMessageFilter;
import org.tmarciniak.cfmtp.model.TradeMessagesDTO;
import org.tmarciniak.cfmtp.model.TradeMessagesDTO.MessageType;
import org.tmarciniak.cfmtp.service.TradeMessageService;
import org.tmarciniak.cfmtp.web.websocket.TradeMessageWebSocketService;

@Controller
public class TradeMessageEndpoint {

	private static Log logger = LogFactory.getLog(TradeMessageEndpoint.class);

	@Inject
	private TradeMessageService tradeMessageService;

	@Inject
	private TradeMessageWebSocketService tradeMessageWebSocketService;

	@Inject
	private MessagingConfig messagingConfig;

	@Inject
	private DozerMapper dozerMapper;

	private Queue queue;
	
	private JmsTemplate jmsTemplate;

	@PostConstruct
	private void initJms() {
		this.queue = new ActiveMQQueue(MessagingConfig.CFMTP_QUEUE_NAME);
		this.jmsTemplate = new JmsTemplate(
				messagingConfig.getConnectionFactory());
	}

	@RequestMapping(value = "/", method = RequestMethod.POST, headers = "Accept=application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void consumeTradeMessage(@RequestBody String tradeMessage) {
		addToQueue(tradeMessage);
	}

	public void addToQueue(final String tradeMessage) {
		this.jmsTemplate.send(this.queue, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				TextMessage createTextMessage = session.createTextMessage(tradeMessage);
				return createTextMessage;
			}
		});

		logger.trace("Message has been sent.");
	}

	@MessageMapping("/trade.messages")
	public void getTradeMessages(TradeMessageFilter tradeMessageFilter)
			throws Exception {
		List<TradeMessageDTO> tradeMessageDTOs = new ArrayList<TradeMessageDTO>();
		List<TradeMessage> tradeMessages = tradeMessageService
				.getTradeMessages(tradeMessageFilter);

		for (TradeMessage tradeMessage : tradeMessages) {
			TradeMessageDTO tradeMessageDTO = dozerMapper.map(tradeMessage,
					TradeMessageDTO.class);
			tradeMessageDTOs.add(tradeMessageDTO);
		}

		TradeMessagesDTO tradeMessagesDTO = new TradeMessagesDTO();
		tradeMessagesDTO.setMessageType(MessageType.LOAD);
		tradeMessagesDTO.setTradeMessages(tradeMessageDTOs);
		tradeMessagesDTO.setCurrencyFrom(tradeMessageFilter.getCurrencyFrom());
		tradeMessagesDTO.setCurrencyTo(tradeMessageFilter.getCurrencyTo());
		tradeMessageWebSocketService
				.sendTradeMessageTransformationResult(tradeMessagesDTO,null);
	}

	@MessageExceptionHandler
	@SendToUser("/queue/errors")
	public String handleException(Throwable exception) {
		return exception.getMessage();
	}
}
