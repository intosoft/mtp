package org.tmarciniak.cfmtp.components;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.tmarciniak.cfmtp.model.TradeMessage;

@Controller
public class TradeMessageConsumer {
	
	@RequestMapping(value = "/", method = RequestMethod.POST, headers = "Accept=application/json")
	@ResponseStatus(value=HttpStatus.OK)
	public void consumeTradeMessage(@RequestBody TradeMessage tradeMessage) {
		System.out.println("MessageConsumer.logs() "+ tradeMessage.toString());
	}
	
}
