package eu.ttbox.androgister.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id = -1;
	
	private long priceSumHT = 0l;
	
	private ArrayList<OrderItem> items;

	public long getId() {
		return id;
	}

	public Order setId(long id) {
		this.id = id;
		return this;
	}

	public long getPriceSumHT() {
		return priceSumHT;
	}

	public Order setPriceSumHT(long priceSumHT) {
		this.priceSumHT = priceSumHT;
		return this;
	}

	public ArrayList<OrderItem> getItems() {
		return items;
	}

	public Order setItems(ArrayList<OrderItem> items) {
		this.items = items;
		return this;
	}

	
	
}
