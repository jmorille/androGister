package eu.ttbox.androgister.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;

public abstract class AbstractEntityRepository<K, N> {

    public final StringSerializer ss = StringSerializer.get();
    public final BytesArraySerializer bs = BytesArraySerializer.get();

    public final String columnFamilyName;
    public final Keyspace keyspace;

    public final Serializer<K> keySerializer;
    public final Serializer<N> topSerializer;

    public final ThriftColumnFamilyTemplate<K, N> template;

    public AbstractEntityRepository(Keyspace keyspace ) {
        super();
        this.keyspace = keyspace;
        // Init
        this.template = createTemplate(  keyspace);
        // Define Shortcut
        this.columnFamilyName = template.getColumnFamily();
        this.keySerializer = template.getKeySerializer();
        this.topSerializer = template.getTopSerializer();
    }

    public abstract ThriftColumnFamilyTemplate<K, N> createTemplate(Keyspace keyspace);

    
    public Map<N, Object> getById(K key) {
        SliceQuery<K, N, byte[]> q = HFactory.createSliceQuery(keyspace, keySerializer, template.getTopSerializer(), bs) //
                .setColumnFamily(columnFamilyName) //
                .setKey(key); 
        QueryResult<ColumnSlice<N, byte[]>> result = q.execute();
        if (null == result || null == result.get()) {
            return null;
        } 
        Map<N, Object> obj = createMapEntity(result.get());
        return obj;
    }
    
    
    public Map<N, Object> createMapEntity(ColumnSlice<N, byte[]> columnSlice) {
        Map<N, Object> entity = new HashMap<N, Object>();
        List<HColumn<N, byte[]>> columns = columnSlice.getColumns();
        for (HColumn<N, byte[]> hcol : columns) {
            N columnName = hcol.getName();
            Serializer<?> valSer = template.getValueSerializer(columnName);
            valSer = valSer == null ? ss : valSer;
            Object value = valSer.fromBytes(hcol.getValue());
            entity.put(columnName, value);
        } 
        return entity;
    }
     

}
