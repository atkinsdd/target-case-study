package com.ddatkins.tgt.products.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ddatkins.tgt.products.domain.CurrentPrice;
import com.ddatkins.tgt.products.service.PriceService;
import com.ddatkins.tgt.products.service.ProductService;

@RestController
public class ProductsController {

	final static Logger logger = LoggerFactory.getLogger(ProductsController.class);

	@Autowired
	PriceService priceService;
	@Autowired
	ProductService productService;

	@RequestMapping(method = RequestMethod.GET, value = "/product/{id}", produces = "application/json")
	public ResponseEntity<String> getProductData(@PathVariable("id") Long id) {
		try {
			Map<String, Object> combinedProductInfo = productService.getCombinedProductPrice(id);
			String result = productService.getProductJson(combinedProductInfo);
			logger.info("retrieved result {}", result);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/product/{id}", consumes = "application/json")
	public ResponseEntity<String> updateProductPrice(@PathVariable("id") Long id, @RequestBody CurrentPrice currentPrice) {
		logger.info("on PUT, the incoming price is {}", currentPrice);
		if (priceService.updateCurrentPrice(id, currentPrice)) {
			return ResponseEntity.ok("success!");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Price update failed");
		}

	}

}
