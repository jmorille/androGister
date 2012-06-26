package eu.ttbox.androgister.core;

import android.content.Intent;
import eu.ttbox.androgister.model.Article;

public class Intents {

	 public static final String ACTION_STATUS = "eu.ttbox.androgister.intent.ACTION_STATUS";
	 
	 public static final String EXTRA_STATUS = "eu.ttbox.androgister.intent.EXTRA_STATUS";
	 
	    public static Intent status(Article status) {
	        Intent intent = new Intent(ACTION_STATUS);
	        intent.putExtra(EXTRA_STATUS, status);
	        return intent;
	    }
}
