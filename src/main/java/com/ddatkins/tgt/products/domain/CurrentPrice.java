package com.ddatkins.tgt.products.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentPrice {
	final static Logger logger = LoggerFactory.getLogger(CurrentPrice.class);
	public String value;
	
	public String currency_code;
	
	public CurrentPrice() {}
	
	CurrentPrice(String value, String currency) {
		this.value = value;
		this.currency_code = currency;
	}

	
}
