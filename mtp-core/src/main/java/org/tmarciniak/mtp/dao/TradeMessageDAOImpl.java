package org.tmarciniak.mtp.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.tmarciniak.mtp.model.TradeMessage;
import org.tmarciniak.mtp.model.TradeMessageFilter;
import org.tmarciniak.mtp.model.TradeMessage_;

@Repository
public class TradeMessageDAOImpl implements TradeMessageDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public void add(TradeMessage tradeMessage) {
		entityManager.persist(tradeMessage);
	}

	public void update(TradeMessage tradeMessage) {
		entityManager.merge(tradeMessage);
	}

	public TradeMessage get(int id) {
		return entityManager.find(TradeMessage.class, id);
	}

	public void delete(int id) {
		TradeMessage tradeMessage = get(id);

		if (tradeMessage != null) {
			entityManager.remove(tradeMessage);
		}
	}

	@SuppressWarnings("unchecked")
	public List<TradeMessage> getTradeMessages() {
		Query query = entityManager
				.createQuery("SELECT tm FROM TradeMessage tm");
		return query.getResultList();
	}

	@Override
	public List<String[]> getCurrenciesPairs() {
		@SuppressWarnings("unchecked")
		List<String[]> resultList = entityManager.createQuery(
				"select distinct currencyFrom, currencyTo from TradeMessage")
				.getResultList();
		return resultList;
	}

	@Override
	public List<TradeMessage> getTradeMessages(
			TradeMessageFilter tradeMessageFilter) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TradeMessage> criteriaQuery = criteriaBuilder
				.createQuery(TradeMessage.class);
		Root<TradeMessage> tradeMessageRecord = criteriaQuery
				.from(TradeMessage.class);

		Predicate currencyFromFilter = criteriaBuilder.equal(
				tradeMessageRecord.get(TradeMessage_.currencyFrom),
				tradeMessageFilter.getCurrencyFrom());
		Predicate currencyToFilter = criteriaBuilder.equal(
				tradeMessageRecord.get(TradeMessage_.currencyTo),
				tradeMessageFilter.getCurrencyTo());
		criteriaQuery.where(criteriaBuilder.and(currencyFromFilter,
				currencyToFilter));

		criteriaQuery.orderBy(criteriaBuilder.asc(tradeMessageRecord
				.get(TradeMessage_.timePlaced)));
		CriteriaQuery<TradeMessage> select = criteriaQuery
				.select(tradeMessageRecord);
		return entityManager.createQuery(select).getResultList();
	}
}
