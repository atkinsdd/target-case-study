package com.ddatkins.tgt.products.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import static com.ddatkins.tgt.products.config.ApplicationConstants.*;

@Service
public class ProductService {
	final static Logger logger = LoggerFactory.getLogger(ProductService.class);
	@Autowired PriceService priceService;
	
	
	public Map<String, Object> getCombinedProductPrice(Long id) {
		Map<String, Object> combinedProductPrice = new HashMap<String,Object>();
		combinedProductPrice.put("id", id);
		
		Map<String, Object> priceCurrentValue = priceService.getPriceCurrentValue(id);
		combinedProductPrice.put("current_price", priceCurrentValue);
		combinedProductPrice.put("name",getProductName(id));
		
		return combinedProductPrice;
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
		logger.debug("product detail json = {}", gsonRes);
		return gsonRes;
    }

	@SuppressWarnings("unchecked")
	public String getProductName(Long id) {
		Map<String, Object> productDetail = retrieveProductDetail(id);
		Map<String,Object> prodMap = (Map<String, Object>) productDetail.get("product");
    	Map<String,Object> itemMap = (Map<String, Object>) prodMap.get("item");
    	Map<String,Object> prodDescription = (Map<String, Object>) itemMap.get("product_description");
		String name = prodDescription.get("title").toString();
		return name;
	}
	
}
