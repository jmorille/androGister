package me.prettyprint.hom;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Row;

public class HectorObjectMapperHelper {

    public static  <T> T getObject(HectorObjectMapper objMapper, CFMappingDef<T> cfMapDef, Keyspace keyspace, String colFamName, Row<String, String, byte[]> pkObj) {
        ColumnSlice<String, byte[]> slice = pkObj.getColumnSlice();
        T entity = objMapper.createObject(cfMapDef, pkObj, slice);
        return entity;
    }
}
