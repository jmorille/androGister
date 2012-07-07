package eu.ttbox.androgister.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id = -1;
    private long priceSumHT = 0l;
    private long orderNumber = -1;
    private String orderUUID;
    private long orderDate = -1;
    private OrderStatusEnum status = OrderStatusEnum.ORDER;

    private int paymentMode;
    private long personId;
    private String personMatricule;
    private String personFirstname;
    private String personLastname;

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

    public int getPaymentMode() {
        return paymentMode;
    }

    public Order setPaymentMode(int paymentMode) {
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

}
