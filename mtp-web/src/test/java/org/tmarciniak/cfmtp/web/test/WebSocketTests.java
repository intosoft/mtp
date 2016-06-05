package org.tmarciniak.mtp.web.test;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.SocketUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.tmarciniak.mtp.config.MessagingConfig;
import org.tmarciniak.mtp.model.TradeMessageDTO;
import org.tmarciniak.mtp.model.TradeMessagesDTO;
import org.tmarciniak.mtp.web.config.ApplicationInitializer;
import org.tmarciniak.mtp.web.config.WebConfig;
import org.tmarciniak.mtp.web.websocket.TradeMessageWebSocketService;
import org.tmarciniak.mtp.web.websocket.support.client.StompMessageHandler;
import org.tmarciniak.mtp.web.websocket.support.client.StompSession;
import org.tmarciniak.mtp.web.websocket.support.client.WebSocketStompClient;
import org.tmarciniak.mtp.web.websocket.support.server.DispatcherServletInitializer;
import org.tmarciniak.mtp.web.websocket.support.server.TomcatWebSocketTestServer;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class })
public class WebSocketTests extends TestCase {
	private static Log logger = LogFactory.getLog(WebSocketTests.class);
	private CountDownLatch lock = new CountDownLatch(1);

	private BrokerService broker;

	@Inject
	MessagingConfig messagingConfig;

	@Inject
	TradeMessageWebSocketService tradeMessageWebSocketService;

	private SockJsClient sockJsClient;

	private int port;

	private TomcatWebSocketTestServer tomcatWebSocketTestServer;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		this.broker = new BrokerService();
		TransportConnector addConnector = broker.addConnector(messagingConfig
				.getBrokerURL());
		if (addConnector == null) {
			logger.error("Cannot create TransportConnector");
			throw new AssertionError("TransportConnector is null");
		}

		this.broker.start();

		this.port = SocketUtils.findAvailableTcpPort();
		this.tomcatWebSocketTestServer = new TomcatWebSocketTestServer(port);
		this.tomcatWebSocketTestServer.deployConfig(
				TestDispatcherServletInitializer.class,
				ApplicationInitializer.class);
		this.tomcatWebSocketTestServer.start();

		List<Transport> transports = new ArrayList<Transport>();
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		this.sockJsClient = new SockJsClient(transports);
	}

	@Test
	public void testMessageQueue() throws Exception {
		System.setProperty("spring.profiles.active", "test.tomcat");

		TradeMessageDTO tradeMessageDTO = new TradeMessageDTO();
		tradeMessageDTO.setRate(BigDecimal.valueOf(1.0));

		final TradeMessagesDTO tradeMessagesDTO = new TradeMessagesDTO();
		tradeMessagesDTO.setTradeMessages(Arrays.asList(tradeMessageDTO));

		final String currencyFrom = "USD";
		final String currencyTo = "GBP";

		tradeMessagesDTO.setCurrencyFrom(currencyFrom);
		tradeMessagesDTO.setCurrencyTo(currencyTo);

		URI uri = new URI("ws://localhost:" + this.port + "/mtpws");

		WebSocketStompClient webSocketStompClient = new WebSocketStompClient(
				uri, null, sockJsClient);
		webSocketStompClient
				.setMessageConverter(new MappingJackson2MessageConverter());

		final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();

		StompMessageHandler stompMessageHandler = new StompMessageHandler() {

			@Override
			public void handleReceipt(String receiptId) {
			}

			@Override
			public void handleMessage(Message<byte[]> message) {
				StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
				if (!(TradeMessageWebSocketService.TOPIC_TRADE_MESSAGES
						+ currencyFrom + "." + currencyTo).equals(headers
						.getDestination())) {
					failure.set(new IllegalStateException(
							"Unexpected message: " + message));
				}
				try {
					String json = new String((byte[]) message.getPayload(),
							Charset.forName("UTF-8"));

					assertEquals(
							"\"{\\\"messageType\\\":null,\\\"tradeMessages\\\":[{\\\"rate\\\":1.0,\\\"timePlaced\\\":0}],\\\"currencyFrom\\\":\\\"USD\\\",\\\"currencyTo\\\":\\\"GBP\\\"}\"",
							json);

					lock.countDown();
				} catch (Throwable t) {
					logger.error(t);
				}
			}

			@Override
			public void handleError(Message<byte[]> message) {
				logger.error("Web socket test error on message handling: "
						+ new String(message.getPayload()));
			}

			@Override
			public void afterDisconnected() {
			}

			@Override
			public void afterConnected(StompSession session,
					StompHeaderAccessor headers) {
				String destination = TradeMessageWebSocketService.TOPIC_TRADE_MESSAGES
						+ currencyFrom + "." + currencyTo;
				session.subscribe(destination, null);

				tradeMessageWebSocketService
						.sendTradeMessageTransformationResult(tradeMessagesDTO,
								session);
			}
		};
		webSocketStompClient.connect(stompMessageHandler);

		lock.await();
	}

	@Override
	protected void tearDown() throws Exception {
		broker.stop();
		tomcatWebSocketTestServer.stop();
	}

	public static class TestDispatcherServletInitializer extends
			DispatcherServletInitializer {

		@Override
		protected Class<?>[] getServletConfigClasses() {
			return new Class[] { WebConfig.class, TestWebSocketConfig.class };
		}
	}

	@Configuration
	@EnableScheduling
	@ComponentScan(basePackages = "org.tmarciniak.mtp")
	@EnableWebSocketMessageBroker
	static class TestWebSocketConfig extends
			AbstractWebSocketMessageBrokerConfigurer {

		@Autowired
		Environment env;

		@Override
		public void registerStompEndpoints(StompEndpointRegistry registry) {
			DefaultHandshakeHandler handler = new DefaultHandshakeHandler(
					new TomcatRequestUpgradeStrategy());
			registry.addEndpoint("/mtpws").setHandshakeHandler(handler)
					.withSockJS();
		}

		@Override
		public void configureMessageBroker(MessageBrokerRegistry registry) {
			registry.enableSimpleBroker("/queue/", "/topic/");
			registry.setApplicationDestinationPrefixes("/app");
		}
	}

}
