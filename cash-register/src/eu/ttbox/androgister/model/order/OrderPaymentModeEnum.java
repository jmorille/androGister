package eu.ttbox.androgister.model.order;

public enum OrderPaymentModeEnum {
    CASH, CREDIT;

    public static OrderPaymentModeEnum getEnumFromKey(int key) {
        if (key < 0) {
            return null;
        }
        return OrderPaymentModeEnum.values()[key];
     }

    public Integer getKey() {
         return  Integer.valueOf(  this.ordinal());
    }
}