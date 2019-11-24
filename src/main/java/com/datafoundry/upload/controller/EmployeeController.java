package com.datafoundry.upload.controller;

import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.datafoundry.upload.model.dao.Employee;
import com.datafoundry.upload.model.dto.EmployeeDetailsDto;
import com.datafoundry.upload.service.EmployeeDBService;
import com.datafoundry.upload.service.ListValidatorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class EmployeeController {

	@Autowired
	private EmployeeDBService employeeDBService;
	@Autowired
	private ListValidatorService listValidatorService; 

	@ApiOperation(value = "API1: Reading the excel through multipart file upload and inserting the data into database")
	@PostMapping(path = "/save-employees")
	public ResponseEntity<String> postEmployeeList(@RequestBody List<EmployeeDetailsDto> employeeDetailsDto)
			throws ConstraintViolationException {
		
		listValidatorService.validateListItems(employeeDetailsDto);

		String message = employeeDBService.postEmployeeListService(employeeDetailsDto);

		return new ResponseEntity<String>(message, HttpStatus.CREATED);
	}

	@ApiOperation(value = "API2: Reading all the employees available in the database using Spring boot pagination")
	@GetMapping(path = "/get-all-employees")
	public ResponseEntity<?> getAllEmployees() {

		Page<Employee> employeePage = employeeDBService.getAllEmployeesPages();
		if (employeePage.hasContent()) {
			return new ResponseEntity<Page<Employee>>(employeePage, HttpStatus.OK);
		} else {
			String message = "There is no data";
			return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "API3: Updating Employee details or address details based on employeeID")
	@PutMapping(path = "/update-employee")
	public ResponseEntity<String> putEmployee(@RequestBody Employee employee) {

		String message = employeeDBService.updateEmployeeService(employee);

		if (message == "Updated") {
			return new ResponseEntity<String>(message, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(message, HttpStatus.CONFLICT);
		}
	}

	@ApiOperation(value = "API4: Deleting employee details. Corresponding Address details are also deleted.")
	@DeleteMapping(path = "/delete-employee")
	public ResponseEntity<String> deleteEmployee(@RequestParam String id) {

		String message = employeeDBService.deleteEmployeeService(id);

		if (message == "Deleted") {
			return new ResponseEntity<String>(message, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(message, HttpStatus.CONFLICT);
		}
	}
}