package eu.ttbox.androgister.web.sync;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Function;

import eu.ttbox.androgister.model.Product;

public class SyncRequestProductReader extends SyncRequestReader<Product>{

	public SyncRequestProductReader( ) {
		super(Product.class);
	}

	 @Override
	public void mockSync( final JsonGenerator jgen,  Function<Product, Boolean> callback   ) {
	        String salespointId = "ttbox"; // TODO 
//	        productRepository.findEntityUpdatedFrom(salespointId,entityHeader.syncDate, callback);
	        
		  for (int i = 0 ; i<3; i++) {
			  Product product = new Product();
			  product.serverId = UUID.randomUUID();
			  product.creationDate = System.currentTimeMillis()- (long) Math.random()*100;
			  product.name = "From other sync " + product.serverId;
			  product.name = "Description other sync " + product.serverId;
			  product.salepointId = "ttbox";
			  // Apply 
			  callback.apply(product);
		  } 
	}
}
