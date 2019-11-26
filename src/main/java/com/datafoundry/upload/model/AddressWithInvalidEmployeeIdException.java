package com.datafoundry.upload.model;

public class AddressWithInvalidEmployeeIdException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public AddressWithInvalidEmployeeIdException() {
		super("There is an address which contains invalid EID");
	}
}
