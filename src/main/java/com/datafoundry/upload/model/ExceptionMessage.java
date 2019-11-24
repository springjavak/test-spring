package com.datafoundry.upload.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

@Data
public class ExceptionMessage {
	private String timpstamp;
	private String message;

	public ExceptionMessage(String message) {
		this.message = message;
		this.timpstamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
}
