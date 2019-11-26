package com.datafoundry.upload.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.datafoundry.upload.model.StaticMessages;
import com.datafoundry.upload.model.dao.Employee;
import com.datafoundry.upload.model.dto.EmployeeDetailsDto;
import com.datafoundry.upload.model.dto.UploadResponse;
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

	@ApiOperation(value = "API1: Reading the excel through multipart file upload and inserting the data into database.")
	@RequestMapping(path = "/post-employee-file", method = RequestMethod.POST)
	public ResponseEntity<UploadResponse> postEmployeeFile(@RequestParam MultipartFile multipartFile)
			throws IOException {
		UploadResponse uploadResponse = employeeDBService.postEmployeeFileService(multipartFile);
		String message = uploadResponse.getMessage();
		if (message.equals(StaticMessages.VALID_NO_DATA_TO_PROCESS)) {
			return new ResponseEntity<UploadResponse>(uploadResponse, HttpStatus.CONFLICT);
		} else {
			if (uploadResponse.getAddressMap().isEmpty()) {
				return new ResponseEntity<UploadResponse>(uploadResponse, HttpStatus.CREATED);
			} else {
				uploadResponse.setMessage(StaticMessages.RESPONSE_ADDRESS_NOT_MAPPED);
				return new ResponseEntity<UploadResponse>(uploadResponse, HttpStatus.CREATED);
			}
		}
	}

	@ApiOperation(value = "API2: Reading all the employees available in the database using Spring boot pagination")
	@RequestMapping(path = "/get-all-employees", method = RequestMethod.GET)
	public ResponseEntity<?> getAllEmployees(@RequestParam int pageSize, @RequestParam int pageNumber) {
		Page<Employee> employeePage = employeeDBService.getAllEmployeesPages(pageSize, pageNumber);
		if (employeePage.hasContent()) {
			return new ResponseEntity<Page<Employee>>(employeePage, HttpStatus.OK);
		} else {
			String message = StaticMessages.VALID_NO_DATA_TO_PROCESS;
			return new ResponseEntity<String>(message, HttpStatus.NO_CONTENT);
		}
	}

	@ApiOperation(value = "API3: Updating Employee details or address details based on employeeID")
	@RequestMapping(path = "/update-employee", method = RequestMethod.PUT)
	public ResponseEntity<String> putEmployee(@RequestBody Employee employee) {
		String message = employeeDBService.updateEmployeeService(employee);
		if (message.equals(StaticMessages.RESPONSE_UPDATED)) {
			return new ResponseEntity<String>(message, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation(value = "API4: Deleting employee details. Corresponding Address details are also deleted.")
	@RequestMapping(path = "/delete-employee", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteEmployee(@RequestParam String id) {
		String message = employeeDBService.deleteEmployeeService(id);
		if (message.equals(StaticMessages.RESPONSE_DELETED)) {
			return new ResponseEntity<String>(message, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation(value = "API5: Loading all the employee details and address details available in the database into excel.")
	@RequestMapping(path = "/export-employee-file", method = RequestMethod.GET)
	public void exportEmployeeFile(HttpServletResponse response) throws IOException {
		Workbook workbook = employeeDBService.exportEmployeeData();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss_SSS");
		String date = dateFormat.format(new Date());
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + date + ".xls\"");
		workbook.write(response.getOutputStream());
	}

	@ApiOperation(value = "Other-API1.1: Inserting a list of employees into database")
	@RequestMapping(path = "/save-employees", method = RequestMethod.POST)
	public ResponseEntity<String> postEmployeeList(@RequestBody List<EmployeeDetailsDto> employeeDetailsDto)
			throws ConstraintViolationException {
		listValidatorService.validateListItems(employeeDetailsDto);
		String message = employeeDBService.postEmployeeListService(employeeDetailsDto);
		if (message.equals(StaticMessages.RESPONSE_CREATED)) {
			return new ResponseEntity<String>(message, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}
}
