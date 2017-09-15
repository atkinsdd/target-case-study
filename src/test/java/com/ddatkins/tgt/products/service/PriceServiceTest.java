package com.ddatkins.tgt.products.service;

import static com.ddatkins.tgt.products.config.ApplicationConstants.PRICE_VALUE;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.basho.riak.client.core.query.crdt.types.RiakMap;
import com.ddatkins.tgt.products.domain.CurrentPrice;
import com.ddatkins.tgt.products.exception.PriceException;
import com.ddatkins.tgt.products.service.PriceService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PriceServiceTest {
	@Autowired
	PriceService priceService;

	@Before
	public void setUp() throws Exception {
		CurrentPrice currentPrice = createCurrentPrice("USD", "123.99");
		priceService.updateCurrentPrice(1L, currentPrice);
	}

	@Test
	public void testGetPriceCurrentValue() throws PriceException {
		Map<String,Object>priceMap = priceService.getPriceCurrentValue(1L);
		assertNotNull(priceMap);
		assertTrue(priceMap.get("value").toString().equalsIgnoreCase("123.99"));
	}

	@Test
	public void testGetPersistedPriceData() {
		RiakMap riakMap = null;
		riakMap = priceService.getPersistedPriceData(1L);
		
		assertNotNull(riakMap);
		assertNotNull(riakMap.getRegister(PRICE_VALUE));
	}

	@Test
	public void testUpdateCurrentPrice() throws PriceException {
		CurrentPrice currentPrice = new CurrentPrice();
		currentPrice.currency_code="CAD";
		currentPrice.value="199.99";
		assertTrue(priceService.updateCurrentPrice(1L, currentPrice));
		assertTrue(priceService.getPriceCurrentValue(1L).get("value").toString().equalsIgnoreCase("199.99"));
		
	}
	
	@Test
	public void testGetPriceFail() {
		Map<String,Object> priceMap = null;
		try {
		 priceMap = priceService.getPriceCurrentValue(-1L);
		} catch (PriceException pe) {
			System.out.println("PriceException expected!");
		}
		assertNull(priceMap);
	}
	
	@Test
	public void testValidCurrentPrice() {
		CurrentPrice curr = createCurrentPrice("USD","11.99");
		assertTrue(priceService.validCurrentPrice(curr));
		
		curr.value = "ABCD";
		assertFalse(priceService.validCurrentPrice(curr));
	}
	
	private CurrentPrice createCurrentPrice(String currency_code, String value) {
		CurrentPrice curr = new CurrentPrice();
		curr.currency_code=currency_code;
		curr.value=value;
		
		return curr;
	}

}
