package eu.ttbox.androgister.web.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class SyncRequestWriter<ENTITY extends EntitySyncable> {

	private static final Logger LOG = LoggerFactory.getLogger(SyncRequestWriter.class);


	// Instance
	private final Class<ENTITY> entityClass;

	public SyncRequestWriter(Class<ENTITY> entityClass) {
		super();
		this.entityClass = entityClass;
	}

	
	public void syncFile(InputStream is, Writer out) throws IOException {
		// Init Jackson
		final ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		MappingJsonFactory jsonFactory = new MappingJsonFactory(mapper);
		// Init Writer
		final JsonGenerator jgen = jsonFactory.createGenerator(out);
		jgen.writeStartObject();
		
		
		
		// Close Json Writer
		jgen.writeEndObject();
		jgen.close();
		
	}
	
	
}
