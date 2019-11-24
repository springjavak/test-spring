package com.datafoundry.upload.model.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "employee")
public class Employee {
	@Id
	private String id;
	private int eid;
	private String name;
	private int age;
	private Address address;
}
