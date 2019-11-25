package com.datafoundry.upload.model.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeDetailsDto {
	private String eid;
	private String name;
	@Min(value = 14, message = "Employee age should be 14 at least.")
	private String age;
	private String aid;
	private String street;
	private String city;
	@Size(min = 6, max = 6, message = "Pincode should contain 6 digits exactly.")
	@Digits(message = "Pincode sholud be a number", fraction = 0, integer = 6)
	private String pincode;
}
