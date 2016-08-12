package com.cisco.applicationprofiler.logical.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.logical.services.GenericServices;
import com.cisco.applicationprofiler.models.GenericQueryModel;
import com.cisco.applicationprofiler.models.GenericResponseModel;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
public class GenericController {

	@Autowired
	private GenericServices m_service;

	@ApiOperation(value = "Excecute generic queries", notes = "Execute queries which are not supported by other rest apis", response = GenericResponseModel.class, responseContainer = "List")
	@RequestMapping(value = "/profiler/v1/generic", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public List<GenericResponseModel> executeQuery(@RequestBody GenericQueryModel query) throws AciEntityNotFound {
		return m_service.executeQuery(query);
	}

	@ApiOperation(value = "Get all filter types", notes = "Get filter types for shared resources, e.g; HTTP, FTP, SMTP etc")
	@RequestMapping(value = "/profiler/v1/generic/sharedResourceFilters", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getFilterTypes() {
		return m_service.getFilterTypes();
	}

}
