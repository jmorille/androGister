package eu.ttbox.androgister.web.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
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

public abstract class SyncRequestReader<ENTITY extends EntitySyncable> {

	private static final Logger LOG = LoggerFactory.getLogger(SyncRequestReader.class);

	// Request
	private static final String TAG_HEADER = "header";
	private static final String TAG_SYNCED = "synced";
	private static final String TAG_UPDATED = "updated";
	private static final String TAG_DELETED = "deleted";

	// Response
	private static final String TAG_UPDATE_ACK = "updateAck";

	// Instance
	private final Class<ENTITY> entityClass;

	public SyncRequestReader(Class<ENTITY> entityClass) {
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
			if (TAG_HEADER.equals(fieldname)) {
				currentToken = jp.nextToken(); // move to value
				entityHeader = mapper.readValue(jp, SyncHeader.class);
				LOG.info("Header : {}", entityHeader);
			} else if (TAG_SYNCED.equals(fieldname)) {
				currentToken = jp.nextToken(); // move to value
				List<SyncEntity<UUID>> resut = mapper.readValue(jp, typeRef);
				for (SyncEntity<UUID> entity : resut) {
					entityIgnore.put(entity.serverId, entity.versionDate);
					LOG.info("synced : {}", entity);
				}
			} else if (TAG_UPDATED.equals(fieldname)) {
				syncFileUpdated(mapper, jp, jgen, entityIgnore);

			} else if (TAG_DELETED.equals(fieldname)) {
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
		jgen.writeEndObject();
		jgen.close();

	}

	private void syncFileUpdated(final ObjectMapper mapper, JsonParser jp, final JsonGenerator jgen, final HashMap<UUID, Long> entityIgnore) throws JsonParseException, IOException {
		JsonToken firstToken = jp.nextToken();
		if (firstToken != JsonToken.START_ARRAY) {
			throw new RuntimeException("Invalid Format : Token [" + firstToken + "]");
		}
		// Prepare Generator
		jgen.writeArrayFieldStart(TAG_UPDATE_ACK);
		while (jp.nextToken() != JsonToken.END_ARRAY) {
			// Read Entity
			ENTITY entity = mapper.readValue(jp, entityClass);
			LOG.info("updated Entity : {}", entity);
			// TODO Save Server

			entity.setServerId(UUID.randomUUID());
			entity.setVersionDate(Long.valueOf(System.currentTimeMillis()));
			// Keep Update data
			entityIgnore.put(entity.getServerId(), entity.getVersionDate());
			// Write the status
			mapper.writeValue(jgen, entity);
			jgen.flush();
		}
		// Close field generator
		jgen.writeEndArray();

	}

	protected Function<ENTITY, Boolean> createCallbackWriter(final ObjectMapper mapper, final JsonGenerator jgen, final HashMap<UUID, Long> entityIgnore) {
		// Read Other modify
		Function<ENTITY, Boolean> callback = new Function<ENTITY, Boolean>() {

			@Override
			public Boolean apply(ENTITY entity) {
				try {
					Long clientVersion = entityIgnore.get(entity.getServerId());
					if (clientVersion == null || !clientVersion.equals(entity.getVersionDate())) {
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
		return callback;
	}

	private void syncDatabaseUpdated(final ObjectMapper mapper, final JsonGenerator jgen, final HashMap<UUID, Long> entityIgnore, SyncHeader entityHeader) throws JsonGenerationException, IOException {
		Function<ENTITY, Boolean> callback = createCallbackWriter(mapper, jgen, entityIgnore);
		// Prepare Generator
		jgen.writeArrayFieldStart(TAG_UPDATED);

		// Read All Datas
		mockSync(jgen, callback);
		// Close field generator
		jgen.writeEndArray();

	}

	public abstract void mockSync(final JsonGenerator jgen, Function<ENTITY, Boolean> callback);

}
