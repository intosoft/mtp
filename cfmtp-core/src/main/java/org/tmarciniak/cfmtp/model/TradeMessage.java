package org.tmarciniak.cfmtp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author tomasz.marciniak
 * 
 *         Immutable class representing trade message
 */

@Entity
@Table
public final class TradeMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trade_message_id_seq")
	@SequenceGenerator(name = "trade_message_id_seq", sequenceName = "trade_message_id_seq", allocationSize = 1)
	private long id;
	private String userId;
	private String currencyFrom;
	private String currencyTo;
	private BigDecimal amountBuy;
	private BigDecimal amountSell;
	private BigDecimal rate;
	private Date timePlaced;
	private String originatingCountry;

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

	public BigDecimal getAmountSell() {
		return amountSell;
	}

	public void setAmountSell(BigDecimal amountSell) {
		this.amountSell = amountSell;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((amountBuy == null) ? 0 : amountBuy.hashCode());
		result = prime * result
				+ ((amountSell == null) ? 0 : amountSell.hashCode());
		result = prime * result
				+ ((currencyFrom == null) ? 0 : currencyFrom.hashCode());
		result = prime * result
				+ ((currencyTo == null) ? 0 : currencyTo.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime
				* result
				+ ((originatingCountry == null) ? 0 : originatingCountry
						.hashCode());
		result = prime * result + ((rate == null) ? 0 : rate.hashCode());
		result = prime * result
				+ ((timePlaced == null) ? 0 : timePlaced.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradeMessage other = (TradeMessage) obj;
		if (amountBuy == null) {
			if (other.amountBuy != null)
				return false;
		} else if (!amountBuy.equals(other.amountBuy))
			return false;
		if (amountSell == null) {
			if (other.amountSell != null)
				return false;
		} else if (!amountSell.equals(other.amountSell))
			return false;
		if (currencyFrom == null) {
			if (other.currencyFrom != null)
				return false;
		} else if (!currencyFrom.equals(other.currencyFrom))
			return false;
		if (currencyTo == null) {
			if (other.currencyTo != null)
				return false;
		} else if (!currencyTo.equals(other.currencyTo))
			return false;
		if (id != other.id)
			return false;
		if (originatingCountry == null) {
			if (other.originatingCountry != null)
				return false;
		} else if (!originatingCountry.equals(other.originatingCountry))
			return false;
		if (rate == null) {
			if (other.rate != null)
				return false;
		} else if (!rate.equals(other.rate))
			return false;
		if (timePlaced == null) {
			if (other.timePlaced != null)
				return false;
		} else if (!timePlaced.equals(other.timePlaced))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
