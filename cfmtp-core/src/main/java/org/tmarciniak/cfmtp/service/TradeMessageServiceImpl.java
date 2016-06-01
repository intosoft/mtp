package org.tmarciniak.cfmtp.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmarciniak.cfmtp.dao.TradeMessageDAO;
import org.tmarciniak.cfmtp.model.TradeMessage;
import org.tmarciniak.cfmtp.model.TradeMessageFilter;

@Service
@Transactional
public class TradeMessageServiceImpl implements TradeMessageService {

	@Inject
	private TradeMessageDAO tradeMessageDAO;

	public void add(TradeMessage tradeMessage) {
		tradeMessageDAO.add(tradeMessage);
	}

	@Override
	public void update(TradeMessage tradeMessage) {
		tradeMessageDAO.update(tradeMessage);
	}

	@Override
	public TradeMessage get(int id) {
		return tradeMessageDAO.get(id);
	}

	@Override
	public void delete(int id) {
		tradeMessageDAO.delete(id);
	}

	@Override
	public List<TradeMessage> getTradeMessages() {
		return tradeMessageDAO.getTradeMessages();
	}

	@Override
	public List<String[]> getCurrenciesPairs() {
		return tradeMessageDAO.getCurrenciesPairs();
	}

	@Override
	public List<TradeMessage> getTradeMessages(
			TradeMessageFilter tradeMessageFilter) {
		return tradeMessageDAO.getTradeMessages(tradeMessageFilter);
	}

}
