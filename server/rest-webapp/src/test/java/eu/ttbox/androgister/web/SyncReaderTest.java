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

import eu.ttbox.androgister.model.Product;
import eu.ttbox.androgister.web.sync.SyncEntity;
import eu.ttbox.androgister.web.sync.SyncHeader;
import eu.ttbox.androgister.web.sync.SyncRequestProductReader;

public class SyncReaderTest {

	private static final Logger LOG = LoggerFactory.getLogger(SyncReaderTest.class);

	private InputStream openJsonFile(String name) {
		
		InputStream is = getClass().getResourceAsStream(name);
		return is;
	}
 
	
	@Test
	public void testReadSync() throws IOException {
		String name = "/sync/product-sync.json"; 
		  
		StringWriter sb = new StringWriter(1024);

		InputStream is = openJsonFile(name);
		try {
			SyncRequestProductReader requestReader = new SyncRequestProductReader();
			requestReader.syncFile(is, sb);
		} finally {
			System.out.println("Result --> " + sb.getBuffer().toString());
			is.close();
		}

		System.out.println("------> \n" + sb.getBuffer().toString());

	}
 
	

	 
	
}
