package eu.ttbox.androgister.web.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import eu.ttbox.androgister.model.Product;
import eu.ttbox.androgister.repository.ProductRepository;

@Controller
@RequestMapping("/product")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, timeout = 1000)
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    public ProductRepository productRepository;

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public Product getById(@PathVariable UUID productId) {
        Product product = productRepository.findById(productId);
        return product;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public List<Product> findProduct(@RequestParam(value = "s", defaultValue = "0") int firstResult, @RequestParam(value = "p", defaultValue = "10") int maxResult) {
        List<Product> entities = null;
        try {
            entities = productRepository.findAll(); // firstResult, maxResult
        } catch (Exception e) {
            LOG.error("Error find all product " + e.getMessage(), e);
        }
        return entities;
    }

    // @Transactional(propagation = Propagation.REQUIRED)
    // @RequestMapping(value = "/sync", method = RequestMethod.POST, consumes =
    // MediaType.APPLICATION_JSON_VALUE)
    // @ResponseBody
    // public List<Product> syncProducts(@RequestBody List<Product> products) {
    //
    // LOG.info("Sync {} products ", products.size());
    // return products;
    // }

    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "/sync", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void syncProducts(@RequestParam(value = "syncstate", defaultValue = "-1") long syncstate, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("Sync Begin products : Last Sync State {}", syncstate);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        MappingJsonFactory jsonFactory = new MappingJsonFactory(mapper);

        // Prepare Writer
        response.setContentType("application/json;charset=UTF-8");
        // wrapper.setHeader("Content-length", "" +
        // jsonContent.getBytes().length);

        OutputStream os = response.getOutputStream();
        JsonGenerator jgen = jsonFactory.createGenerator(os, JsonEncoding.UTF8);
        jgen.writeStartArray();

        // ServletServerHttpResponse responseHeader = new
        // ServletServerHttpResponse(response);
        // MappingJackson2HttpMessageConverter jsonConverter = new
        // MappingJackson2HttpMessageConverter();
        // MediaType jsonMimeType = MediaType.APPLICATION_JSON;
        // jsonConverter.write(entity, jsonMimeType,responseHeader);

        // Read The file
        BufferedReader is = request.getReader();
        JsonParser jp = jsonFactory.createJsonParser(is);
        // advance stream to START_ARRAY first:
        JsonToken firstToken = jp.nextToken();
        if (firstToken != JsonToken.START_ARRAY) {
            throw new RuntimeException("Invalid Format");
        }
        while (jp.nextToken() == JsonToken.START_OBJECT) {
            // Read Entity
            Product entity = mapper.readValue(jp, Product.class);
            // Save Entity
            LOG.debug("Read Product entity : {}", entity);
            productRepository.persist(entity);
            // Write the status
            mapper.writeValue(jgen, entity);
            jgen.flush();
        }
        jp.close();
        is.close();

        // Close Writer
        jgen.writeEndArray();
        jgen.close();
        LOG.info("Sync {} products ", "End");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ResponseBody
    public int initProductList() {
        int count = 0;
        for (int i = 1; i < 20; i++) {
            Product product = createMockProduct(i);
            productRepository.persist(product);
            LOG.debug("persist Product  : {} ", product);
            count++;
        }
        return count;
    }

    private Product createMockProduct(int productId) {
        Product product = new Product();
        // product.id = Long.valueOf(productId);

        product.name = "Product name " + productId;
        product.description = "Product name " + productId;
        product.priceHT = Long.valueOf(productId);
        return product;
    }

}
