package org.tmarciniak.cfmtp.web.test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.ServiceStopper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tmarciniak.cfmtp.config.ApplicationConfig;
import org.tmarciniak.cfmtp.config.MessagingConfig;
import org.tmarciniak.cfmtp.listener.TradeMessageListener;
import org.tmarciniak.cfmtp.model.TradeMessage;
import org.tmarciniak.cfmtp.processor.TradeMessageProcessor;
import org.tmarciniak.cfmtp.web.config.WebConfig;
import org.tmarciniak.cfmtp.web.endpoint.TradeMessageEndpoint;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MessagingConfig.class,
		ApplicationConfig.class })
public class MessageQueueTests extends TestCase {

	private CountDownLatch lock = new CountDownLatch(1);

	private BrokerService broker;

	@Inject
	ApplicationConfig applicationConfig;

	@Inject
	MessagingConfig messagingConfig;

	@Inject
	TradeMessageEndpoint tradeMessageEndpoint;

	@Mock
	TradeMessageProcessor tradeMessageProcessor;

	@Inject
	@InjectMocks
	TradeMessageListener tradeMessageListener;

	@Before
	@Override
	public void setUp() throws Exception {
		broker = new BrokerService();
		broker.addConnector(messagingConfig.getBrokerURL());
		broker.start();
		broker.waitUntilStarted();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMessageQueue() throws Exception {
		final String tradeMessage = "{\"userId\": \"134256\", \"currencyFrom\": \"EUR\", \"currencyTo\": \"GBP\", \"amountSell\": 20.0, \"amountBuy\": 10.00,\"rate\": 0.5, \"timePlaced\" : \"14-JAN-15 10:27:44\", \"originatingCountry\" : \"FR\"}";

		
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				TradeMessage readValue;
				try {
					readValue = applicationConfig.objectMapper().readValue(tradeMessage,
							TradeMessage.class);
					assertEquals(readValue, args[0]);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				lock.countDown();
				return "called with arguments: " + args;
			}
		}).when(tradeMessageProcessor).process(Mockito.any(TradeMessage.class));
		
		tradeMessageEndpoint.addToQueue(tradeMessage);
		
		lock.await();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		broker.stopAllConnectors(new ServiceStopper());
		broker.stop();
		broker.waitUntilStopped();
	}
}
