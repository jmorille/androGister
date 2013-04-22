package eu.ttbox.androgister.config.cassandra;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

import eu.ttbox.androgister.repository.ProductRepository;
import eu.ttbox.androgister.repository.SalespointRepository;
import eu.ttbox.androgister.repository.UserRepository;

/**
 * Cassandra configuration file.
 * 
 */
@Configuration
public class CassandraAstyanaxConfiguration {

    private final Logger log = LoggerFactory.getLogger(CassandraAstyanaxConfiguration.class);

    public static final String CASSANDRA_KEYSPACE = "cassandra.keyspace";
    public static final String CASSANDRA_CLUSTER_NAME = "cassandra.clusterName";
    public static final String CASSANDRA_HOST = "cassandra.host";

    @Autowired
    private Environment env;

    private AstyanaxContext<Keyspace> context;

    @PreDestroy
    public void destroy() { 
        log.info("Closing Astyanax connection pool");
        context.shutdown(); 
    }

    @Bean
    public Keyspace keyspaceOperator() throws ConnectionException {
        log.info("Configuring Cassandra keyspace");
        String cassandraHost = env.getProperty(CASSANDRA_HOST);
        String cassandraClusterName = env.getProperty(CASSANDRA_CLUSTER_NAME);

        String cassandraKeyspace = env.getProperty(CASSANDRA_KEYSPACE);

        AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder() //
                .forCluster(cassandraClusterName) //
                .forKeyspace(cassandraKeyspace) //
                .withAstyanaxConfiguration(new AstyanaxConfigurationImpl() //
                        .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE) //
                        .setCqlVersion("3.0.1") //
                        .setTargetCassandraVersion("1.2") //
                ).withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl(cassandraClusterName+"_"+cassandraKeyspace) //
                        .setPort(9160) //
                        .setMaxConnsPerHost(1) //
                        .setSeeds(cassandraHost) //
                )//.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())//
                .buildKeyspace(ThriftFamilyFactory.getInstance());
        this.context = context; // Keep ref to close it
        
        // Start Context 
        context.start(); 
        // Use Key space 
         Keyspace keyspace = context.getClient(); 
         
         // Keyspace definition
         KeyspaceDefinition ksDef = null;
        try {
        ksDef = keyspace.describeKeyspace();
        } catch (ConnectionException e){
            log.info("-------------- KeyspaceDefinition : ConnectionException " + e.getMessage());
         }
        log.info("-------------- KeyspaceDefinition : " + ksDef);
        if (ksDef == null) { 
            // Model
            keyspace.createKeyspace(ImmutableMap.<String, Object> builder() //
                    .put("strategy_options", ImmutableMap.<String, Object> builder()//
                            .put("replication_factor", "1")//
                            .build())//
                    .put("strategy_class", "SimpleStrategy")//
                    .build()//
            );
            keyspace.createColumnFamily(SalespointRepository.CF_SALESPOINT, ImmutableMap.<String, Object> builder() //
                    .put("default_validation_class", "UTF8Type") //
                    .put("key_validation_class", "UTF8Type") //
                    .put("comparator_type", "UTF8Type") //
                    .build());

            keyspace.createColumnFamily(UserRepository.CF_USER, null);
            keyspace.createColumnFamily(ProductRepository.CF_PRODUCT, null);
        }
        return keyspace;
    }
    // @Bean
    // public EntityManagerImpl entityManager(Keyspace keyspace) {
    // String[] packagesToScan = { "eu.ttbox.androgister.model" };
    // return new EntityManagerImpl(keyspace, packagesToScan);
    // }

}
