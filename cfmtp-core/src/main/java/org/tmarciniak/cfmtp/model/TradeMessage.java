package org.tmarciniak.cfmtp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tomasz.marciniak
 * 
 *         Immutable class representing trade message
 */
public final class TradeMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	String userId;
	String currencyFrom;
	String currencyTo;
	BigDecimal amountBuy;
	BigDecimal amountSell;
	BigDecimal rate;
	Date timePlaced;
	String originatingCountry;

	@JsonCreator
	public TradeMessage(@JsonProperty("name") String userId,
			@JsonProperty("currencyFrom") String currencyFrom,
			@JsonProperty("currencyTo") String currencyTo,
			@JsonProperty("amountSell") BigDecimal amountSell,
			@JsonProperty("amountBuy") BigDecimal amountBuy,
			@JsonProperty("rate") BigDecimal rate,
			@JsonProperty("timePlaced") Date timePlaced,
			@JsonProperty("originatingCountry") String originatingCountry) {
		super();
		this.userId = userId;
		this.currencyFrom = currencyFrom;
		this.currencyTo = currencyTo;
		this.amountBuy = amountBuy;
		this.rate = rate;
		this.timePlaced = timePlaced;
		this.originatingCountry = originatingCountry;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public BigDecimal getAmountBuy() {
		return amountBuy;
	}

	public void setAmountBuy(BigDecimal amountBuy) {
		this.amountBuy = amountBuy;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Date getTimePlaced() {
		return new Date(timePlaced.getTime());
	}

	public void setTimePlaced(Date timePlaced) {
		this.timePlaced = timePlaced;
	}

	public String getOriginatingCountry() {
		return originatingCountry;
	}

	public void setOriginatingCountry(String originatingCountry) {
		this.originatingCountry = originatingCountry;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
