package com.sjsu.cmpe281.gaewebservice.exceptions;

@SuppressWarnings("serial")
public class EntityAlreadyExistsException extends Exception{
	public EntityAlreadyExistsException(String message){
		super(message);
	}
}
