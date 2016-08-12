package com.cisco.applicationprofiler.rest.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.domain.Contract;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.models.View;
import com.cisco.applicationprofiler.services.ContractServices;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(ApplicationProfilerConstants.CONTRACT)
public class ContractController {

	
	@Inject
	private ContractServices contractServices;
	
	@ApiOperation(value = "fetch a contract", notes = "Only single device can be fetched with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.FilterEntry.class)
	public Contract getPlugin(@PathVariable("id") int Id) throws AciEntityNotFound {
		return contractServices.getContract(Id);
	}

	@ApiOperation(value = "fetch all contract", notes = "all device can be fetched with the call")
	@RequestMapping(method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Contract.class)
	public List<Contract> getPlugins() throws AciEntityNotFound {
		return contractServices.getContracts();
	}

	
	@ApiOperation(value = "delete a contract", notes = "Only single contract can be RequestMethod with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.DELETE, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Contract.class)
	public Contract deleteContract(@PathVariable("id") int id) throws AciEntityNotFound {
		return contractServices.deleteContract(id);
	}
}
