package com.ddatkins.tgt.products.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ddatkins.tgt.products.exception.PriceException;
import com.ddatkins.tgt.products.exception.ProductException;
import com.google.gson.Gson;

import static com.ddatkins.tgt.products.config.ApplicationConstants.*;

@Service
public class ProductService {
	final static Logger logger = LoggerFactory.getLogger(ProductService.class);
	@Autowired PriceService priceService;
	
	/*
	 * {
    "name": "Xbox One S 1TB Halo Wars 2 Bundle",
    "id": 52147760,
    "current_price": {
        "value": "391.99",
        "currency_code": "USD"
    }
}
	 */
	public Map<String, Object> getCombinedProductPrice(Long id) throws PriceException, ProductException {
		Map<String, Object> combinedProductPrice = new HashMap<String,Object>();
		combinedProductPrice.put(PRODUCT_ID, id);
		
		Map<String, Object> priceCurrentValue = priceService.getPriceCurrentValue(id);
		combinedProductPrice.put(CURRENT_PRICE, priceCurrentValue);
		combinedProductPrice.put(PRODUCT_NAME,getProductName(id));
		
		return combinedProductPrice;
	}
	
	@SuppressWarnings("unchecked")
	public String getProductName(Long id) throws ProductException {
		String name = null;
		try {
			Map<String, Object> productDetail = retrieveProductDetail(id);
			Map<String, Object> prodMap = (Map<String, Object>) productDetail.get("product");
			Map<String, Object> itemMap = (Map<String, Object>) prodMap.get("item");
			Map<String, Object> prodDescription = (Map<String, Object>) itemMap.get("product_description");
			name = prodDescription.get("title").toString();
			return name;
		} catch (Exception e) {
			logger.error("error retrieving product " + id + " with error " + e.getMessage());
			throw new ProductException();
		}
	}
	
	public Map<String, Object> retrieveProductDetail(Long id) {

		RestTemplate restTemplate = new RestTemplate();
		@SuppressWarnings("unchecked")
		Map<String, Object> prodDetail = restTemplate.getForObject(PRODUCT_RESOURCE_URL + id, Map.class);
		logger.debug("got prodDetail = " + prodDetail);
		return prodDetail;
	}
	
	public String getProductJson(Map<String,Object> productDetail) {
    	Gson gson = new Gson();
		String gsonRes = gson.toJson(productDetail);
		logger.info("product detail json = {}", gsonRes);
		return gsonRes;
    }

	
	
}
