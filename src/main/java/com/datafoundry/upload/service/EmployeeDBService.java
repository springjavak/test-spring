package com.datafoundry.upload.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

	public Page<Employee> getAllEmployeesPages(int pageSize, int pageNumber) {

		if (pageSize <= 0 || pageSize > WebPageConfig.PAGE_SIZE_DEFAULT_MAX) {
			pageSize = WebPageConfig.PAGE_SIZE_DEFAULT_MAX;
		}
		if (pageNumber <= 0) {
			pageNumber = 1;
		}

		PageRequest pageable = PageRequest.of(pageNumber, pageSize);
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

	public String postEmployeeFileService(MultipartFile multipartFile) throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
		XSSFSheet worksheetEmployee = workbook.getSheet("Employee");
		XSSFSheet worksheetAddress = workbook.getSheet("Address");
		Map<String, Address> addressMap = new HashMap();
		DataFormatter dataFormatter = new DataFormatter();
		for (int i = 1; i < worksheetAddress.getPhysicalNumberOfRows(); i++) {
			Address address = new Address();
			XSSFRow row = worksheetAddress.getRow(i);
			address.setAid(dataFormatter.formatCellValue(row.getCell(0)));
			address.setStreet(dataFormatter.formatCellValue(row.getCell(1)));
			address.setCity(dataFormatter.formatCellValue(row.getCell(2)));
			address.setPincode(dataFormatter.formatCellValue(row.getCell(3)));
			String key = dataFormatter.formatCellValue(row.getCell(4));
			addressMap.put(key, address);
		}
		List<Employee> listEmployee = new ArrayList<>();
		for (int i = 1; i < worksheetEmployee.getPhysicalNumberOfRows(); i++) {
			Employee employee = new Employee();
			XSSFRow row = worksheetEmployee.getRow(i);
			employee.setEid(dataFormatter.formatCellValue(row.getCell(0)));
			employee.setName(dataFormatter.formatCellValue(row.getCell(1)));
			employee.setAge(dataFormatter.formatCellValue(row.getCell(2)));
			listEmployee.add(employee);
		}
		listEmployee.forEach(emp -> emp.setAddress(addressMap.get(emp.getEid())));
		employeeRepository.saveAll(listEmployee);
		return "Posted";
	}

	public String ecportEmployeeData(String location) throws IOException {

		String message;

		List<Employee> listEmployee = getAllEmployees();

		if (!(listEmployee.size() == 0)) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheetEmployee = workbook.createSheet("Employee");
			XSSFSheet sheetAddress = workbook.createSheet("Address");
			int rownumEmployee = 0;
			int rownumAddress = 0;
			Row rowEmployee = sheetEmployee.createRow(rownumEmployee++);
			Row rowAddress = sheetAddress.createRow(rownumAddress++);

			Cell cellEid = rowEmployee.createCell(0);
			Cell cellName = rowEmployee.createCell(1);
			Cell cellAge = rowEmployee.createCell(2);
			cellEid.setCellValue("EID");
			cellName.setCellValue("NAME");
			cellAge.setCellValue("AGE");

			Cell cellAid = rowAddress.createCell(0);
			Cell cellStreet = rowAddress.createCell(1);
			Cell cellCity = rowAddress.createCell(2);
			Cell cellPincode = rowAddress.createCell(3);
			Cell cellEidForeign = rowAddress.createCell(4);

			cellAid.setCellValue("AID");
			cellStreet.setCellValue("Street");
			cellCity.setCellValue("City");
			cellPincode.setCellValue("Pincode");
			cellEidForeign.setCellValue("EID");

			for (Employee list : listEmployee) {

				rowEmployee = sheetEmployee.createRow(rownumEmployee++);

				cellEid = rowEmployee.createCell(0);
				cellName = rowEmployee.createCell(1);
				cellAge = rowEmployee.createCell(2);
				cellEid.setCellValue(list.getEid());
				cellName.setCellValue(list.getName());
				cellAge.setCellValue(list.getAge());

				if (!(list.getAddress() == null)) {

					rowAddress = sheetAddress.createRow(rownumAddress++);
					cellAid = rowAddress.createCell(0);
					cellStreet = rowAddress.createCell(1);
					cellCity = rowAddress.createCell(2);
					cellPincode = rowAddress.createCell(3);
					cellEidForeign = rowAddress.createCell(4);

					cellAid.setCellValue(list.getAddress().getAid());
					cellStreet.setCellValue(list.getAddress().getStreet());
					cellCity.setCellValue(list.getAddress().getCity());
					cellPincode.setCellValue(list.getAddress().getCity());
					cellEidForeign.setCellValue(list.getEid());

				}
			}
			
			FileOutputStream out = new FileOutputStream(
					new File("employees-with-addresses-" + UUID.randomUUID().toString() + ".xlsx")); // location

			workbook.write(out);
			out.close();

			message = "File is exported.";

		} else {
			message = "There is no data to export";
		}
		return message;
	}
}
