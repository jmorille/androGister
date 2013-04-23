package com.netflix.astyanax.entitystore;

public class MyEntityMapperFactoty {

    
    public static   EntityMapper  createEntityManager(Class  clazz) {
        return new EntityMapper  (clazz, null);
    }
    
}
