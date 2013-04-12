package eu.ttbox.androgister.config.metrics;

import static me.prettyprint.hector.api.factory.HFactory.createRangeSlicesQuery;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.exceptions.HectorException;

import com.yammer.metrics.core.HealthCheck;

import eu.ttbox.androgister.config.cassandra.ColumnFamilyKeys;

/**
 * Metrics HealthCheck for Cassandra.
 */
public class CassandraHealthCheck extends HealthCheck {

    private final Keyspace keyspaceOperator;

    public CassandraHealthCheck(Keyspace keyspaceOperator) {
        super("Cassandra");
        this.keyspaceOperator = keyspaceOperator;
    }

    @Override
    public Result check() throws Exception {
        try {
            createRangeSlicesQuery(keyspaceOperator,
                    StringSerializer.get(), StringSerializer.get(), StringSerializer.get())
                    .setColumnFamily(ColumnFamilyKeys.DOMAIN_CF.cfName)
                    .setRange(null, null, false, 1)
                    .execute()
                    .get();
            return Result.healthy();
        } catch (HectorException he) {
            return Result.unhealthy("Cannot connect to Cassandra Cluster : " + keyspaceOperator.getKeyspaceName());
        }
    }
}
