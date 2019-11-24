package com.datafoundry.upload.model.dao;

import lombok.Data;

@Data
public class Address {
	private int aid;
	private String street;
	private String city;
	private String pincode;
}
