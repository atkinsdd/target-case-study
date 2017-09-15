package com.ddatkins.tgt.products.config;

import java.net.UnknownHostException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;

@Configuration
class RiakConfig {

	@Bean
	RiakCluster riakCluster() throws UnknownHostException {
        // This example will use only one node listening on localhost:8087
        RiakNode node = new RiakNode.Builder()
                .withRemoteAddress("127.0.0.1")
                .withRemotePort(8087)
                .build();
        // you could also have multiple nodes, see riakCluster
        RiakCluster cluster = new RiakCluster.Builder(node)
                .build();

        cluster.start();

        return cluster;
    }
	
	@Bean(destroyMethod="shutdown")
	public RiakClient riakClient() throws NumberFormatException, UnknownHostException {

		 RiakClient client = new RiakClient(riakCluster());
		 return client;
	}
}

