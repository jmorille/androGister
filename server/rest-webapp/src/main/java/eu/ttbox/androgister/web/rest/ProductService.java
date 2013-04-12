package eu.ttbox.androgister.web.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.ttbox.androgister.web.model.Product;

@Controller
@RequestMapping("/product")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, timeout = 1000)
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);


    @RequestMapping(value = "/{productId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Product  getById(@PathVariable Long productId) {
        Product product = null;
        
        return product;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "/sync", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Product> syncProducts(@RequestBody List<Product> products) {
        
        LOG.info("Sync {} products " , products.size());
        return products;
    }
    
    
    
}
