package com.bank.dto;

import java.math.BigDecimal;

public class CurrentAccountTransactionRequest {

	private BigDecimal amount;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
