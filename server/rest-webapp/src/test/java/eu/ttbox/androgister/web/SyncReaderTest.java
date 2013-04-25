package eu.ttbox.androgister.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.ExpectedException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Function;

import eu.ttbox.androgister.model.Product;
import eu.ttbox.androgister.web.sync.SyncEntity;
import eu.ttbox.androgister.web.sync.SyncHeader;

public class SyncReaderTest {

	private static final Logger LOG = LoggerFactory.getLogger(SyncReaderTest.class);

	private InputStream openJsonFile(String name) {
		
		InputStream is = getClass().getResourceAsStream(name);
		return is;
	}

	@Test(expected=JsonParseException.class) 
	public void testReadSyncBroken() throws IOException { 
		String name = "/sync/product-sync-broken.json"; 
		StringWriter sb = new StringWriter(1024); 
		InputStream is = openJsonFile(name);
		try {
			syncFile(is, sb);
		} finally { 
			is.close();
		} 

	}

	
	@Test
	public void testReadSync() throws IOException {
		String name = "/sync/product-sync.json"; 
		  
		StringWriter sb = new StringWriter(1024);

		InputStream is = openJsonFile(name);
		try {
			syncFile(is, sb);
		} finally {
			System.out.println("Error --> " + sb.getBuffer().toString());
			is.close();
		}

		System.out.println("--> " + sb.getBuffer().toString());

	}

	public void syncFile(InputStream is, Writer out) throws IOException {
		// Init Jackson
		final ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		MappingJsonFactory jsonFactory = new MappingJsonFactory(mapper);
		// Init Writer
		final JsonGenerator jgen = jsonFactory.createGenerator(out);
		jgen.writeStartArray();

		// Read The file
		JsonParser jp = jsonFactory.createJsonParser(is);

		// advance stream to START_ARRAY first:
		JsonToken firstToken = jp.nextToken();
		// if (firstToken != JsonToken.START_ARRAY) {
		// throw new RuntimeException("Invalid Format");
		// }
		SyncHeader entityHeader = null;
		final HashMap<UUID, Long> entityIgnore = new HashMap<UUID, Long>();
		JsonToken currentToken = null;
		// Type Ref
		JavaType typeEntityRef = TypeFactory.defaultInstance().constructType(new TypeReference<SyncEntity<UUID>>() {
		});
		CollectionType typeRef = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, typeEntityRef);
		// Read
		while ((currentToken = jp.nextToken()) != JsonToken.END_OBJECT) {
			String fieldname = jp.getCurrentName();
			if ("header".equals(fieldname)) {
				currentToken = jp.nextToken(); // move to value
				entityHeader = mapper.readValue(jp, SyncHeader.class);
				LOG.info("Header : {}", entityHeader);
			} else if ("synced".equals(fieldname)) {
				currentToken = jp.nextToken(); // move to value
				List<SyncEntity<UUID>> resut = mapper.readValue(jp, typeRef);
				for (SyncEntity<UUID> entity : resut) {
					entityIgnore.put(entity.serverId, entity.versionDate);
					LOG.info("synced : {}", entity);
				}
			} else if ("updated".equals(fieldname)) {
				syncFileUpdated(mapper, jp, jgen, entityIgnore);

			} else if ("deleted".equals(fieldname)) {
				currentToken = jp.nextToken(); // move to value
				List<SyncEntity<UUID>> resut = mapper.readValue(jp, typeRef);
				for (SyncEntity<UUID> entity : resut) {
					LOG.info("deleted : {}", entity);
				}
			} 
		}
		jp.close();
		
		// Get other update form Db
		syncDatabaseUpdated(mapper, jgen, entityIgnore, entityHeader);
		
		
		// Close Json Writer 
        jgen.writeEndArray();
        jgen.close();
        
	}

	private void syncFileUpdated(final ObjectMapper mapper, JsonParser jp, final JsonGenerator jgen, final HashMap<UUID,  Long> entityIgnore) throws JsonParseException, IOException {
		JsonToken firstToken = jp.nextToken();
		if (firstToken != JsonToken.START_ARRAY) {
			throw new RuntimeException("Invalid Format : Token [" + firstToken + "]");
		}
		while (jp.nextToken() != JsonToken.END_ARRAY) {
			// Read Entity
			Product entity = mapper.readValue(jp, Product.class);
			LOG.info("updated Entity : {}", entity);
			// TODO Save Server
			entity.uuid = UUID.randomUUID();
			entity.versionDate = Long.valueOf(System.currentTimeMillis());
			// Keep Update data
			entityIgnore.put(entity.uuid, entity.versionDate);
			// Write the status
			mapper.writeValue(jgen, entity);
			jgen.flush();
		}
	}
	
	private void syncDatabaseUpdated(final ObjectMapper mapper, final JsonGenerator jgen, final HashMap<UUID,  Long> entityIgnore, SyncHeader entityHeader) {
		// Read Other modify
        Function<Product, Boolean> callback = new Function<Product, Boolean>() {

            @Override
            public Boolean apply(Product entity) {
                try {
                	Long clientVersion = entityIgnore.get(entity.uuid);
                	if (clientVersion==null || !clientVersion.equals(entity.versionDate)) {
                		// Send server version
                		mapper.writeValue(jgen, entity);
                	}
                } catch (Exception e) {
                	LOG.error("Callback read error : " + e.getMessage(), e);
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }

        };
        // Read All Datas
        String salespointId = "ttbox"; // TODO 
//        productRepository.findEntityUpdatedFrom(salespointId,entityHeader.syncDate, callback);
        
	}
	

}
