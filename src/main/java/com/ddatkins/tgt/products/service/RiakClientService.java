package com.ddatkins.tgt.products.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.datatypes.FetchMap;
import com.basho.riak.client.api.commands.datatypes.MapUpdate;
import com.basho.riak.client.api.commands.datatypes.UpdateMap;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.crdt.types.RiakMap;

@Service
public class RiakClientService {
	
	final static Logger logger = LoggerFactory.getLogger(RiakClientService.class);

	@Autowired
	RiakClient riakClient;
	
	Map<String,Object> fetchFromBucket(Namespace bucket,String key) {
		return getValue(bucket, key);
	}
	
	void save(Namespace bucket, String key, String value) {
		 store(bucket, key, value);
	}
	
	Map<String,Object> getValue(Namespace ns, String key) {
		try{
			Location loc = new Location(ns, key);
			FetchValue fv = new FetchValue.Builder(loc).build();
			FetchValue.Response response = riakClient.execute(fv);
			RiakObject obj = response.getValue(RiakObject.class);
			logger.info("Riak object type = " + obj.getContentType() + ", binary value = " + obj.getValue().toString());
		}catch(Exception e) {
			System.out.println("Failed");
			e.printStackTrace();
			throw new RuntimeException("Failed calling ", e);
		}
		return null;
	}
	
	
	String fetchPriceDetailFromMapBucket(Namespace ns, String key) {
		String priceJson = null;
		RiakMap itemMap = fetchMap(ns, key);
		if (itemMap != null) {
			try {
			if (itemMap.getRegister("PriceDetails") != null)
				priceJson = itemMap
						.getRegister("PriceDetails")
						.getValue().toStringUtf8();
			} catch (NullPointerException npe) {
				logger.error("No Riak map for ns {} key {}",ns, key);
			}
		}
		return priceJson;
	}

	RiakMap fetchMap(Namespace ns,String key) {
		return getMap(ns, key);
	}
	
	RiakMap getMap(Namespace ns, String key)
	{
		Location loc = new Location(ns, key);
		
		FetchMap fetch = new FetchMap.Builder(loc).build();
		FetchMap.Response response = null;
		try {
			response = riakClient.execute(fetch);
			logger.debug("response context for key {} = {}", key, response.hasContext());
			if (response.hasContext())
				return (response.getDatatype());
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
    boolean mapUpdate(Location mapLocation, MapUpdate mu) {

        UpdateMap update = new UpdateMap.Builder(mapLocation, mu)
                .build();

        UpdateMap.Response response = null;
        try {
			response = riakClient.execute(update);
			logger.debug("updateMap response context for location {} = {}", mapLocation, response.hasContext());
		} catch (ExecutionException | InterruptedException e) {
			logger.error("error updating maplocation {} with value {}, with error = {}",mapLocation,mu, e.getMessage());
			return false;
		}
        return true;
    }
	
	StoreValue.Response store(Namespace ns, String key, String value) {
		try{
			Location loc = new Location(ns, key);
			StoreValue storeOp = new StoreValue.Builder(value)
										.withLocation(loc)
										.build();
		 
			return riakClient.execute(storeOp);
		}catch(Exception e) {
			logger.error("Store Failed with error {}",e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed storing ", e);
		}
	}
	
}