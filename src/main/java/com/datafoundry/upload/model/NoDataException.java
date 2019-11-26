package com.datafoundry.upload.model;

public class NoDataException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public NoDataException() {
		super(StaticMessages.VALID_NO_DATA_TO_PROCESS);
	}
}
