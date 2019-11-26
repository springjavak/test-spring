package com.datafoundry.upload.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.datafoundry.upload.config.WebPageConfig;
import com.datafoundry.upload.model.NoDataException;
import com.datafoundry.upload.model.StaticMessages;
import com.datafoundry.upload.model.dao.Address;
import com.datafoundry.upload.model.dao.Employee;
import com.datafoundry.upload.model.dto.EmployeeDetailsDto;
import com.datafoundry.upload.model.dto.UploadResponse;
import com.datafoundry.upload.repository.EmployeePagingRepository;
import com.datafoundry.upload.repository.EmployeeRepository;

@Service
public class EmployeeDBService {

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private EmployeePagingRepository employeePagingRepository;

	// API-1
	public UploadResponse postEmployeeFileService(MultipartFile multipartFile) throws IOException {
		UploadResponse uploadResponse = new UploadResponse();
		Map<String, Address> addressMap = new HashMap<String, Address>();
		if (!multipartFile.isEmpty()) {
			XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
			XSSFSheet worksheetEmployee = workbook.getSheet("Employee");
			XSSFSheet worksheetAddress = workbook.getSheet("Address");
			DataFormatter dataFormatter = new DataFormatter();
			for (int i = 1; i < worksheetAddress.getPhysicalNumberOfRows(); i++) {
				Address address = new Address();
				XSSFRow row = worksheetAddress.getRow(i);
				address.setAid(dataFormatter.formatCellValue(row.getCell(0)));
				address.setStreet(dataFormatter.formatCellValue(row.getCell(1)));
				address.setCity(dataFormatter.formatCellValue(row.getCell(2)));
				address.setPincode(dataFormatter.formatCellValue(row.getCell(3)));
				String key = dataFormatter.formatCellValue(row.getCell(4)).trim();
				if (key.equals("")) {
					key = "This address had no EID " + UUID.randomUUID().toString();
				}
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
			for (Employee emp : listEmployee) {
				emp.setAddress(addressMap.get(emp.getEid()));
				addressMap.remove(emp.getEid());
			}
			employeeRepository.saveAll(listEmployee);
			workbook.close();
			uploadResponse.setMessage(StaticMessages.RESPONSE_CREATED);
		} else {
			uploadResponse.setMessage(StaticMessages.VALID_NO_DATA_TO_PROCESS);
		}
		uploadResponse.setAddressMap(addressMap);
		;
		return uploadResponse;
	}

	// API-2
	public Page<Employee> getAllEmployeesPages(int pageSize, int pageNumber) {
		if (pageSize <= 0 || pageSize > WebPageConfig.PAGE_SIZE_DEFAULT_MAX) {
			pageSize = WebPageConfig.PAGE_SIZE_DEFAULT_MAX;
		}
		if (pageNumber <= 0) {
			pageNumber = WebPageConfig.PAGE_NUMBER_DEFAULT;
		}
		PageRequest pageable = PageRequest.of(pageNumber, pageSize);
		Page<Employee> pages = employeePagingRepository.findAll(pageable);
		return pages;
	}

	// API-3
	public String updateEmployeeService(Employee employee) {
		String message;
		if (employeeRepository.existsById(employee.getId())) {
			employeeRepository.save(employee);
			message = StaticMessages.RESPONSE_UPDATED;
		} else {
			message = StaticMessages.RESPONSE_BAD_REQUEST;
		}
		return message;
	}

	// API-4
	public String deleteEmployeeService(String id) {
		String message;
		if (employeeRepository.existsById(id)) {
			employeeRepository.deleteById(id);
			message = StaticMessages.RESPONSE_DELETED;
		} else {
			message = StaticMessages.RESPONSE_BAD_REQUEST;
		}
		return message;
	}

	// API-5
	public Workbook exportEmployeeData() throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		List<Employee> listEmployee = employeeRepository.findAll();
		if (!isEmpty(listEmployee)) {
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
					cellPincode.setCellValue(list.getAddress().getPincode());
					cellEidForeign.setCellValue(list.getEid());
				}
			}
		} else {
			throw new NoDataException();
		}
		return workbook;
	}

	// Other API-1.1
	public String postEmployeeListService(List<EmployeeDetailsDto> employeeDetailsDto) {
		String message;
		if (isEmpty(employeeDetailsDto)) {
			message = StaticMessages.VALID_NO_DATA_TO_PROCESS;
		} else {
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
			message = StaticMessages.RESPONSE_CREATED;
		}
		return message;
	}

	private static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}
}
