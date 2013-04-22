package eu.ttbox.androgister.config.cassandra;

import javax.annotation.PreDestroy;

import me.prettyprint.cassandra.connection.HOpTimer;
import me.prettyprint.cassandra.connection.MetricsOpTimer;
import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftCfDef;
import me.prettyprint.cassandra.service.ThriftCluster;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hom.EntityManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import eu.ttbox.androgister.config.Constants;

/**
 * Cassandra configuration file.
 * 
 */
@Configuration
public class CassandraConfiguration {

    private final Logger log = LoggerFactory.getLogger(CassandraConfiguration.class);

    public static final String CASSANDRA_KEYSPACE = "cassandra.keyspace";
    public static final String CASSANDRA_CLUSTER_NAME = "cassandra.clusterName";
    public static final String CASSANDRA_HOST = "cassandra.host";

    @Autowired
    private Environment env;

    private Cluster myCluster;

    @PreDestroy
    public void destroy() {
        log.info("Closing Hector connection pool");
        myCluster.getConnectionManager().shutdown();
        HFactory.shutdownCluster(myCluster);
    }
    
    @Bean
    public ThriftCluster cluster() {
        String cassandraHost = env.getProperty(CASSANDRA_HOST);
        String cassandraClusterName = env.getProperty(CASSANDRA_CLUSTER_NAME);
        CassandraHostConfigurator cassandraHostConfigurator = new CassandraHostConfigurator(cassandraHost);
        cassandraHostConfigurator.setMaxActive(100);
        if (env.acceptsProfiles(Constants.SPRING_PROFILE_METRICS)) {
            log.debug("Cassandra Metrics monitoring enabled");
            HOpTimer hOpTimer = new MetricsOpTimer(cassandraClusterName);
            cassandraHostConfigurator.setOpTimer(hOpTimer);
        }
        ThriftCluster cluster = new ThriftCluster(cassandraClusterName, cassandraHostConfigurator);
        this.myCluster = cluster; // Keep a pointer to the cluster, as Hector is buggy and can't find it again...
        return cluster;
    }

    @Bean
    public Keyspace keyspaceOperator(ThriftCluster cluster) {
        log.info("Configuring Cassandra keyspace");
        String cassandraKeyspace = env.getProperty(CASSANDRA_KEYSPACE);

        ConfigurableConsistencyLevel consistencyLevelPolicy = new ConfigurableConsistencyLevel();
        consistencyLevelPolicy.setDefaultReadConsistencyLevel(HConsistencyLevel.ONE);
        // consistencyLevelPolicy.setDefaultWriteConsistencyLevel(HConsistencyLevel.LOCAL_QUORUM);

        KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(cassandraKeyspace);
        if (keyspaceDef == null) {
            log.warn("Keyspace \"{}\" does not exist, creating it!", cassandraKeyspace);
            keyspaceDef = new ThriftKsDef(cassandraKeyspace);
            cluster.addKeyspace(keyspaceDef, true);
            // HFactory.createKeyspace(cassandraKeyspace, cluster );
            // Column Family
            addColumnFamily(cluster, ColumnFamilyKeys.USER_CF, 0);
            addColumnFamily(cluster, ColumnFamilyKeys.PRODUCT_CF, 0); 
            addColumnFamily(cluster, ColumnFamilyKeys.PRODUCT_SALESPOINT_CF, 0);
            addColumnFamily(cluster, ColumnFamilyKeys.DOMAIN_CF, 0);
        }
        return HFactory.createKeyspace(cassandraKeyspace, cluster, consistencyLevelPolicy);
    }

    private void addColumnFamily(ThriftCluster cluster, ColumnFamilyKeys cfName, int rowCacheKeysToSave) {

        String cassandraKeyspace = this.env.getProperty(CASSANDRA_KEYSPACE);

        ColumnFamilyDefinition cfd = HFactory.createColumnFamilyDefinition(cassandraKeyspace, cfName.cfName);

        cfd.setRowCacheKeysToSave(rowCacheKeysToSave);
        if (cfName.keyValidatorClass != null) {
            cfd.setKeyValidationClass(cfName.keyValidatorClass);
        }
        if (cfName.comparatorType != null) {
            cfd.setComparatorType(cfName.comparatorType);
        }
        if (cfName.defaultValidatorClass != null) {
            cfd.setDefaultValidationClass(cfName.defaultValidatorClass);
        }
         cluster.addColumnFamily(cfd);
    }

    private void addColumnFamilySortedbyUUID(ThriftCluster cluster, String cfName, int rowCacheKeysToSave) {

        String cassandraKeyspace = this.env.getProperty(CASSANDRA_KEYSPACE);

        ColumnFamilyDefinition cfd = HFactory.createColumnFamilyDefinition(cassandraKeyspace, cfName);

        cfd.setRowCacheKeysToSave(rowCacheKeysToSave);
        cfd.setComparatorType(ComparatorType.TIMEUUIDTYPE);
        cluster.addColumnFamily(cfd);
    }

    private void addColumnFamilyCounter(ThriftCluster cluster, String cfName, int rowCacheKeysToSave) {
        String cassandraKeyspace = this.env.getProperty(CASSANDRA_KEYSPACE);

        ThriftCfDef cfd = new ThriftCfDef(cassandraKeyspace, cfName, ComparatorType.UTF8TYPE);

        cfd.setRowCacheKeysToSave(rowCacheKeysToSave);
        cfd.setDefaultValidationClass(ComparatorType.COUNTERTYPE.getClassName());
        cluster.addColumnFamily(cfd);
    }

    @Bean
    public EntityManagerImpl entityManager(Keyspace keyspace) {
        String[] packagesToScan = { "eu.ttbox.androgister.model" };
        return new EntityManagerImpl(keyspace, packagesToScan);
    }

    
    
    
}
