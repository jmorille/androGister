package eu.ttbox.androgister.domain.ref;

public enum OrderStatusEnum {
	ORDER(1), ORDER_CANCEL(-1);

	private final int key;

	OrderStatusEnum(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}

	public static OrderStatusEnum getEnumFromKey(int key) {
		if (key == 1) {
			return ORDER;
		} else if (key == -1) {
			return ORDER_CANCEL;
		} else {
			for (OrderStatusEnum e : OrderStatusEnum.values()) {
				if (key == e.key) {
					return e;
				}
			}
			return null;
		}
	}

}
