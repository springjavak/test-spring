package com.datafoundry.upload.model.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.datafoundry.upload.model.StaticMessages;

import lombok.Data;

@Data
public class EmployeeDetailsDto {
	private String eid;
	private String name;
	@Min(value = 14, message = StaticMessages.VALID_MIN + "14")
	private String age;
	private String aid;
	private String street;
	private String city;
	@Size(min = 6, max = 6, message = "Pincode should contain 6 digits exactly.")
	@Digits(message = "Pincode should contain digits only", fraction = 0, integer = 6)
	private String pincode;
}
