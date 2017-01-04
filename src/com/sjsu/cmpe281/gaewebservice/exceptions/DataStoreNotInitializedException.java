package com.sjsu.cmpe281.gaewebservice.exceptions;

@SuppressWarnings("serial")
public class DataStoreNotInitializedException extends Exception {

	public DataStoreNotInitializedException(String message) {
		super(message);
	}
}
