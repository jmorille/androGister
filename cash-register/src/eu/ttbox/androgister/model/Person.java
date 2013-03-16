package eu.ttbox.androgister.model;

import java.io.Serializable;

public class Person implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public long id = -1;
	
	public String lastname;
 	public String firstname;
 	public String matricule;
 	
 	public String tag;
 	public long priceHT = 0l;
 
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	
	public Person setLastname(String state) {
		this.lastname = state;
		return this;
	}

	public String getLastname() {
		return lastname;
	}
 
	public String getFirstname() {
		return firstname;
	}

	public Person setFirstname(String description) {
		this.firstname = description;
		return this;
	}

	public String getMatricule() {
		return matricule;
	}

	public Person setMatricule(String ean) {
		this.matricule = ean;
		return this;
	}

	public String getTag() {
		return tag;
	}

	public Person setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public long getPriceHT() {
		return priceHT;
	}

	public String getPriceHTasString() {
		return PriceHelper.getToStringPrice(priceHT);
 	}

	
	public Person setPriceHT(long priceHT) {
		this.priceHT = priceHT;
		return this;
	}

	 
	
}
