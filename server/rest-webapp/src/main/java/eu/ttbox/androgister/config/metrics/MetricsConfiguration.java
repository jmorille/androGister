package eu.ttbox.androgister.config.metrics;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import me.prettyprint.hector.api.Keyspace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.yammer.metrics.HealthChecks;
import com.yammer.metrics.reporting.GraphiteReporter;

import eu.ttbox.androgister.config.Constants;

@Configuration
public class MetricsConfiguration {

    private final Log log = LogFactory.getLog(MetricsConfiguration.class);

    @Autowired
    private Environment env;

//    @Autowired
//    private Keyspace keyspaceOperator;

   

    @PostConstruct
    public void initMetrics() {
        if (env.acceptsProfiles(Constants.SPRING_PROFILE_METRICS)) {
            log.debug("Initializing Metrics healthchecks");
//            HealthChecks.register(new CassandraHealthCheck(keyspaceOperator));
//            HealthChecks.register(new JavaMailHealthCheck(mailService));

            String graphiteHost = env.getProperty("app.metrics.graphite.host");
            if (graphiteHost != null) {
                log.debug("Initializing Metrics Graphite reporting");
                Integer graphitePort = env.getProperty("app.metrics.graphite.port", Integer.class);
                GraphiteReporter.enable(1,
                        TimeUnit.MINUTES,
                        graphiteHost,
                        graphitePort);
            } else {
                log.warn("Graphite server is not configured, unable to send any data to Graphite");
            }
        }
    }
}
