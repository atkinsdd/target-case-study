package com.ddatkins.tgt.products.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.basho.riak.client.api.commands.datatypes.MapUpdate;
import com.basho.riak.client.api.commands.datatypes.RegisterUpdate;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.crdt.types.RiakMap;
import com.ddatkins.tgt.products.domain.CurrentPrice;
import static com.ddatkins.tgt.products.config.ApplicationConstants.*;

@Service
public class PriceService {
	final static Logger logger = LoggerFactory.getLogger(PriceService.class);
	
	@Autowired RiakClientService riakClientService;
	
	public Map<String, Object> getPriceCurrentValue(Long id) {
		Map<String, Object> currentValue = new HashMap<String, Object>();
		
		RiakMap persistedPrice = getPersistedPriceData(id);
		
		if (persistedPrice != null) {
			try {
				if (persistedPrice.getRegister(PRICE_VALUE) != null && persistedPrice.getRegister(PRICE_CURRENCY_CODE)!=null) {
					String value = persistedPrice.getRegister(PRICE_VALUE).getValue().toStringUtf8();
					logger.info("got price = {} for id {}",value, id);
					currentValue.put(PRICE_VALUE, value);
					String currencyValue = persistedPrice.getRegister(PRICE_CURRENCY_CODE).getValue().toStringUtf8();
					currentValue.put(PRICE_CURRENCY_CODE, currencyValue);
				}
			} catch (NullPointerException npe) {
				logger.error("No Riak map for product_name with key = {}" , id);
				return getErrorCurrentValue();
			}
		} else {
			logger.error("persistedPrice null for id {}", id);
			return getErrorPriceMap(id);
		}

		return currentValue;
	}
	
	
	RiakMap getPersistedPriceData(Long id) {
		Namespace ns = new Namespace(BUCKET_TYPE, BUCKET_NAME);
		return riakClientService.fetchMap(ns, id.toString());
	}
	
	private Map<String, Object> getErrorCurrentValue() {
		Map<String, Object> currentValue = new HashMap<String, Object>();
		currentValue.put("value", "N/A");
		currentValue.put("currency_code", "N/A");
		
		return currentValue;
	}
	
	private Map<String, Object> getErrorPriceMap(Long id) {
		Map<String, Object> priceMap = new HashMap<String, Object>();
		Map<String, Object> currentValue = new HashMap<String, Object>();
		priceMap.put("id", id);
		currentValue.put("value", "N/A");
		currentValue.put("currency_code", "N/A");
		priceMap.put("current_price", currentValue);
		
		return priceMap;
	}


	public boolean updateCurrentPrice(Long id, CurrentPrice currentPrice) {
		Namespace ns = new Namespace(BUCKET_TYPE, BUCKET_NAME);
		Location mapLocation = new Location(ns, id.toString());

        RegisterUpdate ru1 = new RegisterUpdate(currentPrice.value);
        RegisterUpdate ru2 = new RegisterUpdate(currentPrice.currency_code);
        MapUpdate mu = new MapUpdate()
                .update(PRICE_VALUE,ru1)
                .update(PRICE_CURRENCY_CODE, ru2);
		return riakClientService.mapUpdate(mapLocation, mu);
		
	}
	
	

}

