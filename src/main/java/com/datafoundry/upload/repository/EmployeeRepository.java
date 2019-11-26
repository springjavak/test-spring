package com.datafoundry.upload.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.datafoundry.upload.model.dao.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String>{

}
