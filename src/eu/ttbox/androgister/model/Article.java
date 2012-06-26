package eu.ttbox.androgister.model;

import java.io.Serializable;

public class Article implements Serializable {
	private static final long serialVersionUID = 1L;

	private String state;

	public Article setState(String state) {
		this.state = state;
		return this;
	}

	public String getState() {
		return state;
	}

	
	
}
