package eu.ttbox.androgister.config.cassandra;

import me.prettyprint.hector.api.ddl.ComparatorType;

public enum ColumnFamilyKeys {

    USER_CF("User",  null, ComparatorType.ASCIITYPE, null),  
    PRODUCT_CF("Product", ComparatorType.TIMEUUIDTYPE.getClassName(), ComparatorType.ASCIITYPE, ComparatorType.UTF8TYPE.getClassName()), //
    PRODUCT_SALESPOINT_CF("ProductSalepoint", ComparatorType.ASCIITYPE.getClassName(), ComparatorType.TIMEUUIDTYPE, null), //

    DOMAIN_CF("Domain", null, null, null);

    ColumnFamilyKeys(String name, String keyValidatorClass, ComparatorType comparatorType, String defaultValidatorClass) {
        this.cfName = name;
        this.keyValidatorClass = keyValidatorClass;
        this.comparatorType = comparatorType;
        this.defaultValidatorClass = defaultValidatorClass;
    }

    public final String cfName;
    public final String keyValidatorClass;
    public final ComparatorType comparatorType;
    public final String defaultValidatorClass;

}
