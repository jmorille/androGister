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
                ).withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl(cassandraClusterName + "_" + cassandraKeyspace) //
                        .setPort(9160) //
                        .setMaxConnsPerHost(1) //
                        .setSeeds(cassandraHost) //
                )// .withConnectionPoolMonitor(new
                 // CountingConnectionPoolMonitor())//
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
        } catch (ConnectionException e) {
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
                    .put("key_validation_class", "AsciiType") //
                    .put("comparator_type", "AsciiType") //
                    .put("default_validation_class", "UTF8Type") //
                    .build());

            keyspace.createColumnFamily(UserRepository.CF_USER, null);

            keyspace.createColumnFamily(ProductRepository.CF_PRODUCT, ImmutableMap.<String, Object> builder() //
                    .put("key_validation_class", "UUIDType") //
                    .put("comparator_type", "AsciiType") //
                    // .put("default_validation_class", "UTF8Type") //
                    .put("column_metadata", ImmutableMap.<String, Object> builder() //
                            .put("versionDate", ImmutableMap.<String, Object> builder()//
                                    .put("validation_class", "LongType")// DateType
                                    .put("index_name", "product_versionDate")//
                                    .put("index_type", "KEYS")//
                                    .build()) //
                            .put("creationDate", ImmutableMap.<String, Object> builder()//
                                    .put("validation_class", "LongType")// DateType
                                    .build()) //
                            .put("salepointId", ImmutableMap.<String, Object> builder()//
                                    .put("validation_class", "UTF8Type")//
                                    .put("index_name", "product_salepointId")//
                                    .put("index_type", "KEYS")//
                                    .build())//
                            .put("priceHT", ImmutableMap.<String, Object> builder()//
                                    .put("validation_class", "LongType")//
                                    .build())//
                            .put("name", ImmutableMap.<String, Object> builder()//
                                    .put("validation_class", "UTF8Type") //
                                    .build())//
                            .put("description", ImmutableMap.<String, Object> builder()//
                                    .put("validation_class", "UTF8Type") //
                                    .build())//

                            .build())//
                    .build());

            keyspace.createColumnFamily(ProductRepository.CF_SALESPOINT_PRODUCT, ImmutableMap.<String, Object> builder() //
                    .put("key_validation_class", "AsciiType") //
                    .put("comparator_type", "UUIDType") //
                    .put("default_validation_class", "LongType") //
                    .build());

        }
        return keyspace;
    }
    // @Bean
    // public EntityManagerImpl entityManager(Keyspace keyspace) {
    // String[] packagesToScan = { "eu.ttbox.androgister.model" };
    // return new EntityManagerImpl(keyspace, packagesToScan);
    // }

}
