# target-case-study
Create an end-to- end Proof-of- Concept for a products API,which will aggregate product data from multiple sources and return it as JSON to the caller.
The goal is to create a RESTful service that can retrieve product and price details by ID.

## Configure
1. Checkout the application from github: https://github.com/atkinsdd/target-case-study.git
1. Download, install, and configure [Riak](https://www.tiot.jp/riak-docs/riak/kv/2.2.3/) 
   - By default riak uses ports 8087(Protocol Buffers) and 8098 (HTTP).
    - For this exercise I configured a 'map' bucket type (named targetproduct), with a bucket named 'productById' to store price data using the following commands:
	  - *riak-admin bucket-type create targetproduct '{"props":{"datatype": "map"}}'*
	  - *riak-admin bucket-type activate targetproduct*
1. Add some test data to Riak using the TasteOfRiakTgt java application, found in the *utils* package, to confirm Riak setup.
1. Verify Riak by listing keys in the *productById* bucket, via the url: 	
   - http://localhost:8098/types/targetproduct/buckets/productById/keys?keys=true
## Run
1. Make sure Riak is running:
   - from a command line execute *riak start*
   - execute *riak ping* and the response should be  'pong'
1. From the command line, in the target-case-study directory, execute
   - *mvn spring-boot:run*   which starts the spring-boot application
     - Once running the application listens on port 8080
1. PUT data to the repository using a browser application like Postman.
   - Verify the product to be added lives at **redsky.target.com**. For example, check product id 52147760, via the url **http://redsky.target.com/v2/pdp/tcin/52147760?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics*
   - Next, using a tool like *Postman*, create a PUT operation to: **http://localhost:8080/product/52147760**
     - The body of the request should contain price json like **{"value": "391.99",  "currency_code": "USD" }**
1. From a browser, or tool like Postman, retrieve product data
		http://myretail.com:8080/product/52147760  (assuming you have configured myretail as a localhost alias)
## Test
Unit tests live in *src/test/java*.  
- Execute using maven:  *mvn test*
- Execute from an IDE 
