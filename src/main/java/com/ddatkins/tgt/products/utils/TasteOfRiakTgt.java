package com.ddatkins.tgt.products.utils;

/**
 // In the Java client, a bucket/bucket type combination is specified
 // using a Namespace object. To specify bucket, bucket type, and key,
 // use a Location object that incorporates the Namespace object, as is
 // done below.

 Location map =
 new Location(new Namespace("<bucket_type>", "<bucket>"), "<key>");
 */
import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.datatypes.FetchMap;
import com.basho.riak.client.api.commands.datatypes.MapUpdate;
import com.basho.riak.client.api.commands.datatypes.RegisterUpdate;
import com.basho.riak.client.api.commands.datatypes.UpdateMap;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.crdt.types.RiakMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;


public class TasteOfRiakTgt {
    final static Logger logger = LoggerFactory.getLogger(TasteOfRiakTgt.class);

    public static RiakClient client;

    // This will create a client object that we can use to interact with Riak

    public static void mainOk (String[] args) throws UnknownHostException, ExecutionException, InterruptedException{

        logger.info("main");
        // In the Java client, a bucket/bucket type combination is specified
        // using a Namespace object. To specify bucket, bucket type, and key,
        // use a Location object that incorporates the Namespace object, as is
        // done below.
        Namespace ns = new Namespace("targetproduct", "productById");
        String id = "16696652";
		Location mapLocation = new Location(ns, id);

//        RegisterUpdate ru1 = new RegisterUpdate(id);
//        RegisterUpdate ru2 = new RegisterUpdate("{value:13.49,currency_code:USD}");
        RegisterUpdate ru2 = new RegisterUpdate("219.99");
        RegisterUpdate ru3 = new RegisterUpdate("USD");
        MapUpdate mu = new MapUpdate()
                .update("value",ru2)
                .update("currency_code", ru3);
        UpdateMap update = new UpdateMap.Builder(mapLocation, mu)
                .build();
        TasteOfRiakTgt.client = RiakClient.newClient(8087, "127.0.0.1");  //8087 is Protocol Buffers port,  8098 is HTTP
        TasteOfRiakTgt.client.execute(update);

        //now fetch to see that you retrieve it

        String key = id;

        String priceJson = null;
        //http://localhost:8098/types/targetproduct/buckets/productById/keys?keys=true
        RiakMap itemMap = fetchMap(ns, key);
        if (itemMap != null) {
            try {
                if (itemMap
                        .getRegister("value") != null) {

                    priceJson = itemMap
                            .getRegister("value")
                            .getValue().toStringUtf8();

                    logger.info("HEY! got price = {}",priceJson);

                }
                if (itemMap
                        .getRegister("currency_code") != null) {

                    priceJson = itemMap
                            .getRegister("currency_code")
                            .getValue().toStringUtf8();

                    logger.info("HEY! got price = {}",priceJson);

                }


            } catch (NullPointerException npe) {
                System.err.println("No Riak map for product_name with key = " + key);
            }
        } else {
            logger.error("itemMap null for ns {}, key {}", ns, key);
        }

        client.shutdown();
    }

    public static RiakMap fetchMap(Namespace ns,String key) {
        return getMap(ns, key);
    }

    public static RiakMap getMap(Namespace ns, String key)
    {
        Location loc = new Location(ns, key);

        FetchMap fetch = new FetchMap.Builder(loc).build();
        FetchMap.Response response = null;
        try {
            response = TasteOfRiakTgt.client.execute(fetch);
            System.out.println("response context for key " + key + " = " + response.hasContext());
            if (response.hasContext())
                return (response.getDatatype());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}