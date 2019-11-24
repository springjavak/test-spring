package com.datafoundry.upload.service;

import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListValidatorService {

	@Autowired
	private Validator validator;

	public <T extends Collection> void validateListItems(T list) throws ConstraintViolationException {
		Set<ConstraintViolation<Object>> constraintViolations = null;
		for (Object item : list) {
			constraintViolations = validator.validate(item);
		}
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
	}
}