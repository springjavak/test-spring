package com.datafoundry.upload.model.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "employee")
public class Employee {
	@Id
	private String id;
	private String eid;
	private String name;
	private String age;
	private Address address;
}
