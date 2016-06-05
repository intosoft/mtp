package org.tmarciniak.mtp.model;

import java.util.List;

public class TradeMessagesDTO {

	public enum MessageType {
		UPDATE, LOAD
	}

	MessageType messageType;

	List<TradeMessageDTO> tradeMessages;

	String currencyFrom;

	String currencyTo;

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public List<TradeMessageDTO> getTradeMessages() {
		return tradeMessages;
	}

	public void setTradeMessages(List<TradeMessageDTO> tradeMessages) {
		this.tradeMessages = tradeMessages;
	}

	public String getCurrencyFrom() {
		return currencyFrom;
	}

	public void setCurrencyFrom(String currencyFrom) {
		this.currencyFrom = currencyFrom;
	}

	public String getCurrencyTo() {
		return currencyTo;
	}

	public void setCurrencyTo(String currencyTo) {
		this.currencyTo = currencyTo;
	}

}
