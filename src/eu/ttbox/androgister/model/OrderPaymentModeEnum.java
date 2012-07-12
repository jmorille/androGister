package eu.ttbox.androgister.model;

public enum OrderPaymentModeEnum {
    CASH, CREDIT;

    public static OrderPaymentModeEnum getEnumFromKey(int key) {
        if (key < 0) {
            return null;
        }
        return OrderPaymentModeEnum.values()[key];
     }

    public int getKey() {
         return this.ordinal();
    }
}
