package com.ddatkins.tgt.products.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.basho.riak.client.api.commands.datatypes.MapUpdate;
import com.basho.riak.client.api.commands.datatypes.RegisterUpdate;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.crdt.types.RiakMap;
import com.ddatkins.tgt.products.domain.CurrentPrice;
import com.ddatkins.tgt.products.exception.PriceException;

import static com.ddatkins.tgt.products.config.ApplicationConstants.*;

@Service
public class PriceService {
	final static Logger logger = LoggerFactory.getLogger(PriceService.class);
	
	@Autowired RiakClientService riakClientService;
	
	public Map<String, Object> getPriceCurrentValue(Long id) throws PriceException {
		Map<String, Object> currentValue = new HashMap<String, Object>();
		
		RiakMap persistedPrice = getPersistedPriceData(id);
		
		if (persistedPrice != null) {
			try {
				if (persistedPrice.getRegister(PRICE_VALUE) != null && persistedPrice.getRegister(CURRENCY_CODE)!=null) {
					String value = persistedPrice.getRegister(PRICE_VALUE).getValue().toStringUtf8();
					logger.info("got price = {} for id {}",value, id);
					currentValue.put(PRICE_VALUE, value);
					String currencyValue = persistedPrice.getRegister(CURRENCY_CODE).getValue().toStringUtf8();
					currentValue.put(CURRENCY_CODE, currencyValue);
				}
			} catch (NullPointerException npe) {
				logger.error("No Riak map for price with key = {}" , id);
				throw new PriceException();
			}
		} else {
			logger.error("persistedPrice null for id {}", id);
			throw new PriceException();
		}

		return currentValue;
	}
	
	
	RiakMap getPersistedPriceData(Long id) {
		Namespace ns = new Namespace(BUCKET_TYPE, BUCKET_NAME);
		return riakClientService.fetchMap(ns, id.toString());
	}

	public boolean updateCurrentPrice(Long id, CurrentPrice currentPrice) {
		if (!validCurrentPrice(currentPrice)) {
			return false;
		}
		
		Namespace ns = new Namespace(BUCKET_TYPE, BUCKET_NAME);
		Location mapLocation = new Location(ns, id.toString());

        RegisterUpdate ru1 = new RegisterUpdate(currentPrice.value);
        RegisterUpdate ru2 = new RegisterUpdate(currentPrice.currency_code);
        MapUpdate mu = new MapUpdate()
                .update(PRICE_VALUE,ru1)
                .update(CURRENCY_CODE, ru2);
		return riakClientService.mapUpdate(mapLocation, mu);
		
	}
	
	public  boolean validCurrentPrice(CurrentPrice curr) {
		if (ObjectUtils.isEmpty(curr) || ObjectUtils.isEmpty(curr.currency_code) || ObjectUtils.isEmpty(curr.value)) {
			logger.error("supplied price is not valid");
			return false;
		}

		try {
			Float.parseFloat(curr.value);
		} catch (NumberFormatException nfe) {
			logger.error("price value is not a number");
			return false;
		}

		return true;
	}
	

}

