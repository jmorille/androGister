package eu.ttbox.androgister.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    public long id = -1;
    public long priceSumHT = 0l;
    public long orderNumber = -1;
    public String orderUUID;
    public String orderDeleteUUID;

    public OrderStatusEnum status = OrderStatusEnum.ORDER;

    public OrderPaymentModeEnum paymentMode;
    public long personId = -1;
    public String personMatricule;
    public String personFirstname;
    public String personLastname;

    // Shoub be private
    private long orderDate = -1;

    // instance Data
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
        if (result == null) {
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

    public long getOrderNumber() {
        return orderNumber;
    }

    public Order setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public String getOrderUUID() {
        return orderUUID;
    }

    public Order setOrderUUID(String orderUUID) {
        this.orderUUID = orderUUID;
        return this;
    }

    public OrderPaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public Order setPaymentMode(OrderPaymentModeEnum paymentMode) {
        this.paymentMode = paymentMode;
        return this;
    }

    public long getPersonId() {
        return personId;
    }

    public Order setPersonId(long personId) {
        this.personId = personId;
        return this;
    }

    public String getPersonMatricule() {
        return personMatricule;
    }

    public Order setPersonMatricule(String personMatricule) {
        this.personMatricule = personMatricule;
        return this;
    }

    public String getPersonFirstname() {
        return personFirstname;
    }

    public Order setPersonFirstname(String personFirstname) {
        this.personFirstname = personFirstname;
        return this;
    }

    public String getPersonLastname() {
        return personLastname;
    }

    public Order setPersonLastname(String personLastname) {
        this.personLastname = personLastname;
        return this;
    }

    public String getOrderDeleteUUID() {
        return orderDeleteUUID;
    }

    public Order setOrderDeleteUUID(String orderDeleteUUID) {
        this.orderDeleteUUID = orderDeleteUUID;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
