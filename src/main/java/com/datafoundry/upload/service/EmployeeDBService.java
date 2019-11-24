package com.datafoundry.upload.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.datafoundry.upload.config.WebPageConfig;
import com.datafoundry.upload.model.dao.Address;
import com.datafoundry.upload.model.dao.Employee;
import com.datafoundry.upload.model.dao.repo.EmployeePagingRepository;
import com.datafoundry.upload.model.dao.repo.EmployeeRepository;
import com.datafoundry.upload.model.dto.EmployeeDetailsDto;

@Service
public class EmployeeDBService {

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private EmployeePagingRepository employeePagingRepository;

	public String postEmployeeListService(List<EmployeeDetailsDto> employeeDetailsDto) {

		String message;

		List<Employee> listEmployee = new ArrayList<Employee>();

		for (EmployeeDetailsDto listDto : employeeDetailsDto) {

			Employee employee = new Employee();
			Address address = new Address();

			address.setAid(listDto.getAid());
			address.setStreet(listDto.getStreet());
			address.setCity(listDto.getCity());
			address.setPincode(listDto.getPincode());

			employee.setEid(listDto.getEid());
			employee.setName(listDto.getName());
			employee.setAge(listDto.getAge());

			employee.setAddress(address);

			listEmployee.add(employee);
		}

		employeeRepository.saveAll(listEmployee);

		message = "Posted";
//		int i=1;
//		if(i==1)
//			throw new AddressWithInvalidEmployeeIdException();
		return message;
	}

	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	public Page<Employee> getAllEmployeesPages() {

		PageRequest pageable = PageRequest.of(5, WebPageConfig.PAGE_SIZE);
		Page<Employee> pages = employeePagingRepository.findAll(pageable);
	
			return pages;
		
		
	}

	public String updateEmployeeService(Employee employee) {

		String message;

		if (employeeRepository.existsById(employee.getId())) {
			employeeRepository.save(employee);
			message = "Updated";
		} else {
			message = "The employee is not registered.";
		}
		return message;
	}

	public String deleteEmployeeService(String id) {

		String message;

		if (employeeRepository.existsById(id)) {
			employeeRepository.deleteById(id);
			message = "Deleted";
		} else {
			message = "The employee is not registered.";
		}
		return message;
	}
}
