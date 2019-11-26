package com.datafoundry.upload.model.dto;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import lombok.Data;

@Data
public class ExportResponse {
	String message;
	InputStreamResource inputStreamResource;
}
