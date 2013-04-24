package eu.ttbox.androgister.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import eu.ttbox.androgister.model.Product;
import eu.ttbox.androgister.web.sync.SyncEntity;
import eu.ttbox.androgister.web.sync.SyncHeader;

public class SyncReaderTest {

	private static final Logger LOG = LoggerFactory.getLogger(SyncReaderTest.class);

	private InputStream openJsonFile() {
		String name = "/sync/product-sync.json";
		InputStream is = getClass().getResourceAsStream(name);
		return is;
	}

	@Test
	public void testReadSync() throws IOException {
		StringWriter sb = new StringWriter(1024);

		InputStream is = openJsonFile();
		try {
			syncFile(is, sb);
		} finally {
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

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String fieldname = jp.getCurrentName();

			if ("header".equals(fieldname)) {
				JsonToken currentToken = jp.nextToken(); // move to value
				SyncHeader entityHeader = mapper.readValue(jp, SyncHeader.class);
				LOG.info("Header : {}", entityHeader);
			} else if ("synced".equals(fieldname)) {
				JsonToken currentToken = jp.nextToken(); // move to value
				CollectionType typeRef = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, SyncEntity.class);
				List<SyncEntity<String>> resut = mapper.readValue(jp, typeRef);
				for (SyncEntity<String> entity : resut) {
					LOG.info("synced : {}", entity);
				}
			} else if ("updated".equals(fieldname)) {
				syncFileUpdated(mapper, jp, jgen);

			} else if ("deleted".equals(fieldname)) {
				JsonToken currentToken = jp.nextToken(); // move to value
				CollectionType typeRef = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, SyncEntity.class);
				List<SyncEntity<String>> resut = mapper.readValue(jp, typeRef);
				for (SyncEntity<String> entity : resut) {
					LOG.info("deleted : {}", entity);
				}
			}

		}

	}

	private void syncFileUpdated(final ObjectMapper mapper, JsonParser jp, final JsonGenerator jgen) throws JsonParseException, IOException {
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
			 // Write the status
            mapper.writeValue(jgen, entity);
            jgen.flush();
            
		}
	}

}
