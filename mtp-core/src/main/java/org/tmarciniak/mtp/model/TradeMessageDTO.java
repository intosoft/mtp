package org.tmarciniak.mtp.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author tomasz.marciniak
 * 
 *         Immutable class representing trade message
 */

public final class TradeMessageDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal rate;
	private long timePlaced;

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public long getTimePlaced() {
		return timePlaced;
	}

	public void setTimePlaced(long timePlaced) {
		this.timePlaced = timePlaced;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
