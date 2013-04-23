package com.netflix.astyanax.entitystore;

import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;

public class MyEntityMapper<T, K> {

    private EntityMapper<T, K> entityMapper;

    public MyEntityMapper(Class<T> clazz) {
        super();
        entityMapper = new EntityMapper<T, K>(clazz, null);
    }

    public void fillMutationBatch(MutationBatch mb, ColumnFamily<K, String> columnFamily, T entity) {
        entityMapper.fillMutationBatch(mb, columnFamily, entity);
    }

    public T constructEntity(K id, ColumnList<String> cl) {
        return entityMapper.constructEntity(id, cl);
        // TODO lifecycleHandler.onPostLoad(entity);
    }

    public K getEntityId(T entity) throws Exception {
        return entityMapper.getEntityId(entity);
    }
}
