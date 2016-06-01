package org.tmarciniak.cfmtp.dao;

import java.util.List;

import org.tmarciniak.cfmtp.model.TradeMessage;
import org.tmarciniak.cfmtp.model.TradeMessageFilter;

public interface TradeMessageDAO {

	public void add(TradeMessage tradeMessage);

	public void update(TradeMessage tradeMessage);

	public TradeMessage get(int id);

	public void delete(int id);

	public List<TradeMessage> getTradeMessages();

	public List<String[]> getCurrenciesPairs();

	public List<TradeMessage> getTradeMessages(
			TradeMessageFilter tradeMessageFilter);


}
