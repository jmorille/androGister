package eu.ttbox.androgister.config.cassandra;

import me.prettyprint.hector.api.ddl.ComparatorType;

public enum ColumnFamilyKeys {

    USER_CF("User", null), //ComparatorType.TIMEUUIDTYPE
    DOMAIN_CF("Domain", null);

    ColumnFamilyKeys(String name, ComparatorType comparatorType) {
        this.cfName = name;
        this.comparatorType = comparatorType;
    }

    public final String cfName;
    public final ComparatorType comparatorType;

}
