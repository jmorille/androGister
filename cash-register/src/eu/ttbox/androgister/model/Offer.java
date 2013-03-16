package eu.ttbox.androgister.model;

import java.io.Serializable;

public class Offer implements Serializable {
	
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

	
	
	public Offer setName(String state) {
		this.name = state;
		return this;
	}

	public String getName() {
		return name;
	}
 
	public String getDescription() {
		return description;
	}

	public Offer setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getEan() {
		return ean;
	}

	public Offer setEan(String ean) {
		this.ean = ean;
		return this;
	}

	public String getTag() {
		return tag;
	}

	public Offer setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public long getPriceHT() {
		return priceHT;
	}

	public String getPriceHTasString() {
		return PriceHelper.getToStringPrice(priceHT);
 	}

	
	public Offer setPriceHT(long priceHT) {
		this.priceHT = priceHT;
		return this;
	}

	
}
