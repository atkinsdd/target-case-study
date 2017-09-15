# target-case-study
Create an end-to- end Proof-of- Concept for a products API,which will aggregate product data from multiple sources and return it as JSON to the caller.
The goal is to create a RESTful service that can retrieve product and price details by ID.

To run the application,
	1. Checkout the application from github: 
		https://github.com/atkinsdd/target-case-study.git
	2. Riak (https://www.tiot.jp/riak-docs/riak/kv/2.2.3/) is used as the price repository
	3. Download, install, and configure Riak 
		By default riak uses ports 8087(Protocol Buffers) and 8098 (HTTP)
		For this exercise I configured a 'map' bucket type (named targetproduct), with a bucket named 'productById' to store price data:
		riak-admin bucket-type create targetproduct '{"props":{"datatype": "map"}}'
		riak-admin bucket-type activate targetproduct
		Add some test data to Riak using the TasteOfRiakTgt java application to confirm Riak setup.
	3a. Verify Riak: 	
		http://localhost:8098/types/targetproduct/buckets/productById/keys?keys=true
	4. Running the application
		Make sure Riak is running: 1. riak start  2. 'riak ping' should receive the response 'pong'
		From the command line, in the target-case-study directory, execute
			mvn spring-boot:run
		Once running the application listens on port 8080
	6. PUT data to the repository using a browser application like Postman:
	     First, verify the product id at redsky.target.com, for example 52147760, by checking the url:
	     	http://redsky.target.com/v2/pdp/tcin/52147760?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics
		 Next create a PUT operation to: http://localhost:8080/product/52147760
		 	the body of the request should contain price json like
				{"value": "391.99",  "currency_code": "USD" }
	7. From a browser, or tool like Postman, retrieve product data
		http://myretail.com:8080/product/52147760  (assuming you have configured myretail as a localhost alias)