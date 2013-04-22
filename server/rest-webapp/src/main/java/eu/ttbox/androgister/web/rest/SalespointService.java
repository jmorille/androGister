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

import eu.ttbox.androgister.model.Salespoint;
import eu.ttbox.androgister.repository.SalespointRepository;

@Controller
@RequestMapping("/salespoint")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, timeout = 1000)
public class SalespointService {

    private static final Logger LOG = LoggerFactory.getLogger(SalespointService.class);

    @Autowired
    public SalespointRepository salespointRepository;

    @RequestMapping(value = "/{salespointId}", method = RequestMethod.GET)
    @ResponseBody
    public Salespoint getById(@PathVariable String salespointId) {
        Salespoint salespoint = salespointRepository.getById(salespointId);
        return salespoint;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public List<Salespoint> findSalespoint(@RequestParam(value = "s", defaultValue = "0") int firstResult, @RequestParam(value = "p", defaultValue = "10") int maxResult) {
        List<Salespoint> entities = null;
        try {
            entities = salespointRepository.getAll(); // firstResult, maxResult
        } catch (Exception e) {
            LOG.error("Error find all salespoint " + e.getMessage(), e);
        }
        return entities;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "/sync", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Salespoint> syncSalespoints(@RequestBody List<Salespoint> salespoints) {

        LOG.info("Sync {} salespoints ", salespoints.size());
        return salespoints;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ResponseBody
    public int initSalespointList() {
        int count = 0;
        for (int i = 1; i < 20; i++) {
            Salespoint salespoint = createMockSalespoint(i);
            salespointRepository.persist(salespoint);
            LOG.debug("persist Salespoint  : {} ", salespoint);
            count++;
        }
        return count;
    }

    private Salespoint createMockSalespoint(int salespointId) {
        Salespoint salespoint = new Salespoint();
         salespoint.id = "s" +  salespointId; 
        salespoint.name = "Salespoint name " + salespointId;
        
        return salespoint;
    }

}
