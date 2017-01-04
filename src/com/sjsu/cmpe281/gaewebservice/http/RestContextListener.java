package com.sjsu.cmpe281.gaewebservice.http;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.sjsu.cmpe281.gaewebservice.util.DataStoreUtil;
	
public class RestContextListener implements ServletContextListener {
	
	private Logger logger = Logger.getGlobal();

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logger.log(Level.INFO, "Initializing the data store singleton. ");
		DataStoreUtil.init();
	}
	
}
