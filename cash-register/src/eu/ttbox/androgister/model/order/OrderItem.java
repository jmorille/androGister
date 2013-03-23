package eu.ttbox.androgister.model.order;

import java.io.Serializable;

import eu.ttbox.androgister.model.PriceHelper;

public class OrderItem implements Serializable {

	private static final long serialVersionUID = 1L;

	public long id = -1;
	public long orderId = -1;
	public long productId = -1;

	public String name;
	public String ean;

	// SHoub be private
	int quantity = 1;
	long priceUnitHT = 0l;
	long priceSumHT = 0l;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrderId() {
		return orderId;
	}

	public OrderItem setOrderId(long orderId) {
		this.orderId = orderId;
		return this;
	}

	public long getProductId() {
		return productId;
	}

	public OrderItem setProductId(long productId) {
		this.productId = productId;
		return this;
	}

	public OrderItem setName(String state) {
		this.name = state;
		return this;
	}

	public String getName() {
		return name;
	}

	public String getEan() {
		return ean;
	}

	public OrderItem setEan(String ean) {
		this.ean = ean;
		return this;
	}

	// ### Price
	// ##########

	public long getPriceUnitHT() {
		return priceUnitHT;
	}

	public String getPriceHTUnitAsString() {
		return PriceHelper.getToStringPrice(priceUnitHT);
	}

	public int getQuantity() {
		return quantity;
	}

	public OrderItem setPriceUnitHT(long priceHT) {
		this.priceUnitHT = priceHT;
		this.quantity = 1;
		computePriceSum();
		return this;
	}

	public OrderItem setPriceUnitHT(long priceHT, int quantity) {
		this.priceUnitHT = priceHT;
		this.quantity = quantity;
		computePriceSum();
		return this;
	}


    public OrderItem addQuantity(int quantity2) {
        this.quantity += quantity2;
        computePriceSum();
        return this;
     }
	// ### Price Sum
	// ##############

	public long getPriceSumHT() {
		return priceSumHT;
	}

	public String getPriceHTSumAsString() {
		return PriceHelper.getToStringPrice(getPriceSumHT());
	}

	// ### Business
	// ##############

	private void computePriceSum() {
		this.priceSumHT = priceUnitHT * quantity;
	}


}