package com.cisco.applicationprofiler.logical.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.applicationprofiler.exceptions.DeletionNotAllowedException;
import com.cisco.applicationprofiler.exceptions.ForbiddenOperationException;
import com.cisco.applicationprofiler.logical.services.SharedResourceServices;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.ui.models.SharedResourceUi;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@RestController
public class SharedResourceController {
	@Autowired
	private SharedResourceServices m_service;
	@ApiResponses(value = { 
		      				@ApiResponse(code = 409, message = "Conflict, item already exists"), 
							@ApiResponse(code = 507, message = "Insufficient Storage, could not save")
							})
	
	@ApiOperation(value = "Add a shared resource to vrf",
    notes = "Only one shared resource can be added with the call, the shared resource name is expected to be unique for this vrf",
    response = SharedResourceUi.class,
    responseContainer = "Self")
	@RequestMapping(value = "/profler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/sharedResource", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public SharedResourceUi addSharedResource(@PathVariable("projectId") int projectId,@PathVariable("tenantId") int tenantId, @PathVariable("vrfId") int vrfId,@RequestBody SharedResourceUi srcSR) throws AciEntityNotFound {
		return m_service.addSharedResource(projectId,tenantId,vrfId,srcSR);
	}
	
	@ApiResponses(value = { 
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save")
			})

	@ApiOperation(value = "Update the shared resource",
	notes = "Update the shared resource by providing project id, tenant id, vrf id and shared resource id",
	response = SharedResourceUi.class,
	responseContainer = "Self")
	@RequestMapping(value = "/profler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/sharedResource/{sharedResourceId}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public SharedResourceUi updateSharedResource(@PathVariable("projectId") int projectId,@PathVariable("tenantId") int tenantId,@PathVariable("vrfId") int vrfId,@PathVariable("sharedResourceId") int sharedResourceId,@RequestBody SharedResourceUi srcSR) throws AciEntityNotFound, ForbiddenOperationException {

		return m_service.updatesharedResource(projectId,tenantId,vrfId,sharedResourceId,srcSR);
	}
	
	@ApiResponses(value = { 
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save")
		})

	@ApiOperation(value = "Delete the shared resource",
	notes = "Delete the shared resource by providing project id, tenant id, vrf id and shared resource id")
	
	@RequestMapping(value = "/profler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/sharedResource/{sharedResourceId}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public int deleteSharedResource(@PathVariable("projectId") int projectId,@PathVariable("tenantId") int tenantId,@PathVariable("vrfId") int vrfId,@PathVariable("sharedResourceId") int sharedResourceId) throws AciEntityNotFound,DeletionNotAllowedException {
		return( m_service.deleteSharedResource(projectId,tenantId,vrfId,sharedResourceId));
	}
	
	@ApiOperation(value = "Get the shared resource info",
	notes = "Get the shared resource info by providing project id, tenant id, vrf id, and shared resource id",
	response = SharedResourceUi.class)
	@RequestMapping(value = "/profler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/sharedResource/{sharedResourceId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public SharedResourceUi getSharedResource(@PathVariable("projectId") int projectId,@PathVariable("tenantId") int tenantId,@PathVariable("vrfId") int vrfId,@PathVariable("sharedResourceId") int sharedResourceId) throws AciEntityNotFound {

		return m_service.getSharedResource(projectId,tenantId,vrfId,sharedResourceId);
	}

	@ApiOperation(value = "Get all shared resources",
	notes = "Get all shared resource details for a particular vrf by providing project id , tenant id and vrf id",
	response = SharedResourceUi.class,
	responseContainer = "List")
	@RequestMapping(value = "/acisizer/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/sharedResources", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<SharedResourceUi> getSharedResources(@PathVariable("projectId") int projectId,@PathVariable("tenantId") int tenantId,@PathVariable("vrfId") int vrfId) throws AciEntityNotFound {
		return m_service.getSharedResources(projectId,tenantId,vrfId);
	}



}
