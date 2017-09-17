package com.ddatkins.tgt.products.service;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ddatkins.tgt.products.exception.PriceException;
import com.ddatkins.tgt.products.exception.ProductException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest {
	@Autowired
	ProductService productService;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetCombinedProductPrice() throws PriceException, ProductException {
		Map<String,Object>prodPrice = null;
		prodPrice = productService.getCombinedProductPrice(52147760L);
		assertNotNull(prodPrice);
		assertNotNull(prodPrice.get("current_price"));
		
	}

	@Test
	public void testGetProductName() {
		String prodName = null;
		try {
			prodName = productService.getProductName(52147760L);
			assertTrue(prodName.contains("Xbox"));
		} catch (ProductException e) {
			fail("productException "+e);
		}
	}
	
	@Test
	public void testGetProductNameException() {
		String prodName = null;
		
		try {
			prodName = productService.getProductName(-1L);
		} catch (ProductException e) {
			System.out.println("exception expected!");
		}
		assertNull(prodName);
	}

	@Test
	public void testGetProductJson() throws PriceException, ProductException {
		Map<String,Object>prodPrice = productService.getCombinedProductPrice(52147760L);
		String prodJson = productService.getProductJson(prodPrice);
		assertNotNull(prodJson);
		Gson gson = new Gson();
		JsonElement json = gson.fromJson(prodJson,JsonElement.class);
		assertNotNull(json);
	}

}
