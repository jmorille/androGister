package eu.ttbox.androgister.model;

public class PriceHelper {

	public static String getToStringPrice(long price) {
		double priceInDevice = ((double)price) /100;
		return String.valueOf(priceInDevice);
	}
	
}
