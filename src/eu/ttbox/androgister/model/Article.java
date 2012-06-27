package eu.ttbox.androgister.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Article implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name;

	private BigDecimal priceHT;
	
	public Article setName(String state) {
		this.name = state;
		return this;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() { 
		return priceHT;
	}

	
	
}
