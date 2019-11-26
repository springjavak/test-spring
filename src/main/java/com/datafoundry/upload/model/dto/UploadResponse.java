package com.datafoundry.upload.model.dto;

import java.util.HashMap;
import java.util.Map;
import com.datafoundry.upload.model.dao.Address;
import lombok.Data;

@Data
public class UploadResponse {
	String message;
	Map<String, Address> addressMap = new HashMap<String, Address>();
}
