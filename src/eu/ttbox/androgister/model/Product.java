package eu.ttbox.androgister.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name;

	private String description;

	private String ean;

	private String tag;

	private BigDecimal priceHT;
	
	public Product setName(String state) {
		this.name = state;
		return this;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() { 
		return priceHT;
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

	public BigDecimal getPriceHT() {
		return priceHT;
	}

	public Product setPriceHT(BigDecimal priceHT) {
		this.priceHT = priceHT;
		return this;
	}

	
	
}
