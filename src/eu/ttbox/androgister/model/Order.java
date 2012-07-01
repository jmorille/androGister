package eu.ttbox.androgister.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id = -1;
	
	private long priceSumHT = 0l;
	
	private long orderDate = -1;
 	  
	private  OrderStatusEnum status = OrderStatusEnum.ORDER;

	private transient Date cachedOrderDate = null;

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

	public Order setOrderDate(long orderDate) {
		this.orderDate = orderDate;
		this.cachedOrderDate = null;
		return this;
	}
	
	public long getOrderDate() {
		return orderDate;
	}

	public Date getOrderDateAsDate() {
		Date result = cachedOrderDate;
		if (result==null) {
			result = new Date(orderDate);
			this.cachedOrderDate = result;
		}
		return result;
	}

	 
	public OrderStatusEnum getStatus() {
 		return status;
	}

	public Order setStatus(OrderStatusEnum status) {  
		this.status = status;
		return this;
	}

	

	
	
}
