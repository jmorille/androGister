package eu.ttbox.androgister.web.rest;

import java.util.List;
import java.util.UUID;

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

import eu.ttbox.androgister.model.Product;
import eu.ttbox.androgister.model.Product;
import eu.ttbox.androgister.model.UserLight;
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

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public List<Product> findUser(@RequestParam(value = "s", defaultValue = "0") int firstResult, @RequestParam(value = "p", defaultValue = "10") int maxResult) {
        List<Product> entities = null;
        try {
            entities = productRepository.finddAll(); // firstResult, maxResult
        } catch (Exception e) {
            LOG.error("Error find all user " + e.getMessage(), e);
        }
        return entities;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "/sync", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Product> syncProducts(@RequestBody List<Product> products) {

        LOG.info("Sync {} products ", products.size());
        return products;
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
        return product;
    }

}
