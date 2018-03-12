package com.percy.interview.othello.config;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Properties {
	static PropertiesConfiguration config;
	static final String CONFIG_FILE = "othello.config";
	static Logger logger = LoggerFactory.getLogger(Properties.class);
	
	static {
		 try {
			 config = (PropertiesConfiguration) new PropertiesConfiguration(CONFIG_FILE).interpolatedConfiguration();
		 } catch (ConfigurationException e) {
			logger.error("Error Loading Config {}", CONFIG_FILE);
			throw new RuntimeException(e);
		}
	}
	
	public static int getIntegerProperty(String name, int defaultVal) {
		logger.debug("Getting {} with default {}", name, defaultVal);
		int value = config.getInt(name, defaultVal);
		logger.debug("Received {} with value {}", name, value);
		return value;
		
	}
	
	public static String getStringProperty(String name) {
		logger.debug("Getting {}", name);
		String value = config.getString(name);
		logger.debug("Received {} with value {}", name, value);
		return config.getString(name);
	}
	
	public static List<String> getStringListProptery(String name, List<String> defaultList){
		logger.debug("Getting {} with default {}", name, Arrays.toString(defaultList.toArray()));
		List<String> list;
		String[] strArray = config.getStringArray(name);
		if (strArray.length == 0) {
			list = defaultList;
		}else {
			list = Arrays.asList(strArray);
		}
		logger.debug("Received {} with value {}", name, Arrays.toString(list.toArray()));
		return list;
	}
}
