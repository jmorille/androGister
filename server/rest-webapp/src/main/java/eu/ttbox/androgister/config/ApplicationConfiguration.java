package eu.ttbox.androgister.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import eu.ttbox.androgister.config.cassandra.CassandraAstyanaxConfiguration;
import eu.ttbox.androgister.config.cassandra.CassandraConfiguration;
import eu.ttbox.androgister.config.metrics.MetricsConfiguration;

@Configuration
@ComponentScan(basePackages = {//
"eu.ttbox.androgister.repository", "eu.ttbox.androgister.security"
// , "eu.ttbox.androgister.service"  //
})
@Import(value = { 
CassandraAstyanaxConfiguration.class, MetricsConfiguration.class })
@PropertySource({ "classpath:/androgister.properties" })
@ImportResource("classpath*:/META-INF/androgister/applicationContext*.xml")
public class ApplicationConfiguration {

    private final Logger log = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Autowired
    private Environment env;

    @PostConstruct
    public void initTatami() throws IOException, TTransportException {
        if (env.getActiveProfiles().length == 0) {
            log.debug("No Spring profile configured, running with default configuration");
        } else {
            for (String profile : env.getActiveProfiles()) {
                log.debug("Detected Spring profile : " + profile);
            }
        }
        Constants.VERSION = env.getRequiredProperty("app.version");

        log.info("AndroGister version " + Constants.VERSION + " started!");
    }
}
