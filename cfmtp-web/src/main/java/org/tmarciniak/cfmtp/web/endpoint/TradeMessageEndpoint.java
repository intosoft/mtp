package org.tmarciniak.cfmtp.web.endpoint;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.tmarciniak.cfmtp.config.ApplicationConfig;

@Controller
public class TradeMessageEndpoint {

	private static Log logger = LogFactory.getLog(TradeMessageEndpoint.class);

	@Inject
	private ApplicationConfig applicationConfig;

	private Queue queue;
	private JmsTemplate jmsTemplate;

	@PostConstruct
	private void initJms() {
		this.queue = new ActiveMQQueue(ApplicationConfig.CFMTP_QUEUE_NAME);
		this.jmsTemplate = new JmsTemplate(
				applicationConfig.getConnectionFactory());
	}

	@RequestMapping(value = "/", method = RequestMethod.POST, headers = "Accept=application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void consumeTradeMessage(@RequestBody String tradeMessage) {
		addToQueue(tradeMessage);
	}

	public void addToQueue(final String tradeMessage) {
		this.jmsTemplate.send(this.queue, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(tradeMessage);
			}
		});

		logger.trace("Message has been sent.");
	}

}
