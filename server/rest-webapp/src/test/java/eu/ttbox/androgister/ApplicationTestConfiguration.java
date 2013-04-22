package eu.ttbox.androgister;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.transport.TTransportException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import eu.ttbox.androgister.config.cassandra.CassandraAstyanaxConfiguration;
import eu.ttbox.androgister.config.cassandra.CassandraConfiguration;

@Configuration
@PropertySource("classpath:/androgister.properties")
@ComponentScan(basePackages = { "eu.ttbox.androgister.repository" })
@Import(value = { CassandraAstyanaxConfiguration.class })
public class ApplicationTestConfiguration {

    private final Log log = LogFactory.getLog(ApplicationTestConfiguration.class);

    @PostConstruct
    public void initApp() throws IOException, TTransportException {
        this.log.info("App test context started!");
    }
}
