package eu.ttbox.androgister.model.product;

import java.io.Serializable;

import eu.ttbox.androgister.model.PriceHelper;

public class Product implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public long id = -1;
	
	public String name;

	public String description;

	public String ean;

	public String tag;

	public long priceHT = 0l;
 
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

	public String getPriceHTasString() {
		return PriceHelper.getToStringPrice(priceHT);
 	}

	
	public Product setPriceHT(long priceHT) {
		this.priceHT = priceHT;
		return this;
	}

	
}
