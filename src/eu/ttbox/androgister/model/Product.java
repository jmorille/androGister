package eu.ttbox.androgister.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private long id = -1;
	
	private String name;

	private String description;

	private String ean;

	private String tag;

	private long priceHT = 1230l;
 
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	
	public Product setName(String state) {
		this.name = state;
		return this;
	}

	public String getName() {
		return name;
	}
 
	public String getDescription() {
		return description;
	}

	public Product setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getEan() {
		return ean;
	}

	public Product setEan(String ean) {
		this.ean = ean;
		return this;
	}

	public String getTag() {
		return tag;
	}

	public Product setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public long getPriceHT() {
		return priceHT;
	}

	public Product setPriceHT(long priceHT) {
		this.priceHT = priceHT;
		return this;
	}

	
}
