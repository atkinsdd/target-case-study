package com.ddatkins.tgt.controller;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ddatkins.tgt.products.controller.ProductsController;
import com.ddatkins.tgt.products.domain.CurrentPrice;
import com.ddatkins.tgt.products.exception.PriceException;
import com.ddatkins.tgt.products.exception.ProductException;
import com.ddatkins.tgt.products.service.PriceService;
import com.ddatkins.tgt.products.service.ProductService;
import com.ddatkins.tgt.products.service.RiakClientService;
import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class ProductsControllerTest {

	@Mock
	private ProductService mockProductService;
	@Mock 
	private PriceService mockPriceService;
	@Mock
	private RiakClientService riakClientService;
	
	@InjectMocks
	ProductsController productsController;
	
	Map<String, Object> combinedProductInfo;

	@Before
	public void setUp() throws Exception {
		
	}

	/*
	 * combined product info looks like
	 * 
	 * {
    "name": "Xbox One S 1TB Halo Wars 2 Bundle",
    "id": 52147760,
    "current_price": {
        "value": "391.99",
        "currency_code": "USD"
    }
}
	 */
	
	private CurrentPrice getCurrentPrice() {
		CurrentPrice curr = new CurrentPrice();
		curr.currency_code="ABC";
		curr.value="12345";
		
		return curr;
//		currentPrice.put("name")
	}
	private Map<String,Object> getSampleCombinedProduct() {
		Map<String, Object> combinedProductInfo = new HashMap<String,Object>();
		combinedProductInfo.put("name", "xbox");
		combinedProductInfo.put("id", 5214);
		
		return combinedProductInfo;
	}
	
	private CombinedProduct getCombinedProduct() {
		String sampleJson = "{\r\n    \"name\": \"Xbox One S 1TB Halo Wars 2 Bundle\",\r\n    \"id\": 52147760,\r\n    \"current_price\": {\r\n        \"value\": \"391.99\",\r\n        \"currency_code\": \"USD\"\r\n    }\r\n}";
		Gson gson = new Gson();
		CombinedProduct prod = gson.fromJson(sampleJson,CombinedProduct.class);
		return prod;
	}

	@Test
	public void testGetProductDataHappyPath() throws PriceException, ProductException {
		Map<String,Object> sample = getSampleCombinedProduct();
		when(mockProductService.getCombinedProductPrice(anyLong())).thenReturn(sample);
		when(mockProductService.getProductJson(anyObject())).thenReturn("{}");
		ResponseEntity<String> response = productsController.getProductData((long) 0);
		
		System.err.println("response = " + response);
		
		assertTrue(response.getStatusCode()==HttpStatus.OK);
	}

	@Test
	public void testGetProductPriceException() throws Exception{
		when(mockProductService.getCombinedProductPrice(anyLong())).thenThrow(new PriceException());
		ResponseEntity<String> response = productsController.getProductData((long) 0);
		assertTrue(response.getStatusCode()==HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetProductProductException() throws Exception{
		when(mockProductService.getCombinedProductPrice(anyLong())).thenThrow(new ProductException());
		ResponseEntity<String> response = productsController.getProductData((long) 0);
		assertTrue(response.getStatusCode()==HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testUpdateProductPrice() throws Exception {
		CurrentPrice curr = getCurrentPrice();
		when(mockPriceService.updateCurrentPrice(anyLong(), anyObject())).thenReturn(true);
		ResponseEntity<String> response = productsController.updateProductPrice(100L, curr);
		assertTrue(response.getStatusCode()==HttpStatus.OK);
	}
	

}

class CombinedProduct {
	String name;
	Long id;

	Map<String, Object> current_price;

}
