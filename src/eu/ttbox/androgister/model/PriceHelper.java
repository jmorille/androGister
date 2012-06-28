package eu.ttbox.androgister.model;

public class PriceHelper {

	public static String getToStringPrice(long price) {
		double priceInDevice = price /100d;
		return String.format("%.,2f", priceInDevice);
	}
	
}
