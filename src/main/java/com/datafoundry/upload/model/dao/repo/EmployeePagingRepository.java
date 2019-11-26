package com.datafoundry.upload.model.dao.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.datafoundry.upload.model.dao.Employee;

public interface EmployeePagingRepository extends PagingAndSortingRepository<Employee, String> {



}
