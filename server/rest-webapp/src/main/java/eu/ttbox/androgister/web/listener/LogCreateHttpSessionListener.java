package eu.ttbox.androgister.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCreateHttpSessionListener implements HttpSessionListener {

	  private static final Logger LOG = LoggerFactory.getLogger(LogCreateHttpSessionListener.class);

	  
	  
	@Override
	public void sessionCreated(HttpSessionEvent se) { 
		LOG.warn("*** *********************************** ***");
		LOG.warn("*** ********* Session Created ********* ***");
		LOG.warn("*** *********************************** ***");
		// Yoyo Le mécréant
		System.out.println("*** *********************************** ***");
		System.out.println("*** ********* Session Created ********* ***");
		System.out.println("*** *********************************** ***");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) { 
	}

 

}
