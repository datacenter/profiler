package com.cisco.applicationprofiler.logical.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.logical.services.ContractServicesAcii;
import com.cisco.applicationprofiler.models.View;
import com.cisco.applicationprofiler.ui.models.ContractUi;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@RestController
public class ContractControllerAcii extends AciController {

	@Autowired
	private ContractServicesAcii m_service;

	@ApiResponses(value = { @ApiResponse(code = 409, message = "Conflict, item already exists"),
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Add a contract to tenant", notes = "Only single contract can be added with the call, the contract name is expected to be unique for this tenant", response = ContractUi.class, responseContainer = "Self")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/contract", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ContractUi addContract(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId, @Valid @RequestBody ContractUi srcContract) throws AciEntityNotFound {
		ContractUi respCtrt = m_service.addContract(projectId, tenantId, appId, srcContract);
		return respCtrt;
	}

	@ApiResponses(value = { @ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Update the contract", notes = "Update the contract by providing project id, tenant id and contract id", response = ContractUi.class, responseContainer = "Self")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/contract/{contractId}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public ContractUi updateContract(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId, @PathVariable("contractId") int contractId,
			@RequestBody ContractUi srcContract) throws AciEntityNotFound {

		ContractUi respCtrt = m_service.updateContract(projectId, tenantId, appId, contractId, srcContract);
		return respCtrt;

	}

	@ApiResponses(value = { @ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Delete the Contract", notes = "Delete the Contract by providing project id, tenant id and contract id")

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/contract/{contractId}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public int deleteContract(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId, @PathVariable("contractId") int contractId) throws AciEntityNotFound {
		return (m_service.deleteContract(projectId, tenantId, appId, contractId));
	}

	@ApiOperation(value = "Get the Contract info", notes = "Get the Contract info by providing project id, tenant id and contract id", response = ContractUi.class)
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/contract/{contractId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ContractUi getContract(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId, @PathVariable("contractId") int contractId) throws AciEntityNotFound {

		ContractUi respCtrt = m_service.getContract(projectId, tenantId, appId, contractId);
		return respCtrt;

	}

	@ApiOperation(value = "Get all Contracts", notes = "Get all Contract details for a particular tenant by providing project id ,tenant id and app id", response = ContractUi.class, responseContainer = "List")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/contracts", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	@JsonView(View.AuditInfo.class)
	public List<ContractUi> getContracts(@PathVariable("projectId") int projectId,
			@PathVariable("tenantId") int tenantId, @PathVariable("appId") int appId) throws AciEntityNotFound {
		List<ContractUi> respCtrts = m_service.getContracts(projectId, tenantId, appId);
		return respCtrts;

	}
	
	@ApiOperation(value = "Get all Contracts per epg", notes = "Get all Contract details for a particular tenant by providing project id ,tenant id, app id and epg id", response = ContractUi.class, responseContainer = "List")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/epg/{epgId}/contracts", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	@JsonView(View.AuditInfo.class)
	public List<ContractUi> getContractsEx(@PathVariable("projectId") int projectId,
			@PathVariable("tenantId") int tenantId, @PathVariable("appId") int appId, @PathVariable("epgId") int epgId) throws AciEntityNotFound {
		List<ContractUi> respCtrts = m_service.getContractsEx(projectId, tenantId, appId, epgId);
		return respCtrts;

	}

}
