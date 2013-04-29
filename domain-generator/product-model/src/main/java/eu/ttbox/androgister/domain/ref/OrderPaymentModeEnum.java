package eu.ttbox.androgister.domain.ref;

public enum OrderPaymentModeEnum {
    
    CASH(1), CREDIT(2);

    private final int key;

    OrderPaymentModeEnum(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public static OrderPaymentModeEnum getEnumFromKey(int key) {
        if (key == CASH.key) {
            return CASH;
        } else if (key == CREDIT.key) {
            return CREDIT;
        } else {
            for (OrderPaymentModeEnum e : OrderPaymentModeEnum.values()) {
                if (key == e.key) {
                    return e;
                }
            }
            return null;
        }
    }

}
