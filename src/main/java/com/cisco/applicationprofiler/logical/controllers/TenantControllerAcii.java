/**
 * Copyright @maplelabs
 */
package com.cisco.applicationprofiler.logical.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.GenericInvalidDataException;
import com.cisco.applicationprofiler.logical.services.TenantServicesAcii;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.ui.models.AciSizerModelUi;
import com.cisco.applicationprofiler.view.ViewNode;
import com.cisco.applicationprofiler.view.ViewNodes;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @author Deepa
 *
 */

@RestController
public class TenantControllerAcii {

	@Autowired
	private TenantServicesAcii tenantServices;

	@ApiResponses(value = { @ApiResponse(code = 409, message = "Conflict, item already exists"),
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Add a tenant to project", notes = "Only one tenant can be added with the call, the tenant name is expected to be unique for this project", response = Tenant.class)

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Tenant addTenant(@PathVariable("projectId") int projectId, @Valid @RequestBody Tenant tenant)
			throws AciEntityNotFound, GenericInvalidDataException {
		return tenantServices.addTenant(projectId, tenant);
	}

	/*
	 * to be used to update only the vrf information , not to update the tenants
	 * structure
	 */
	@ApiResponses(value = { @ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Update a tenant inside a project", notes = "Update the tenant by providing project id, tenant id", response = Tenant.class)
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{id}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public Tenant updateTenant(@PathVariable("projectId") int projectId, @PathVariable("id") int id,
			@RequestBody Tenant tenant) throws AciEntityNotFound {
		return tenantServices.updateTenant(projectId, id, tenant);
	}

	@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized operation"),
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save") })
	@ApiOperation(value = "Delete the tenant", notes = "Delete the tenant by providing project id, tenant id, but the common tenant cannot be deleted")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public int deleteTenant(@PathVariable("projectId") int projectId, @PathVariable("id") int id)
			throws AciEntityNotFound {
		return tenantServices.deleteTenant(projectId, id);

	}

	@ApiOperation(value = "Get the tenant info", notes = "Get the tenant info by providing project id, tenant id", response = Tenant.class)
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Tenant getTenant(@PathVariable("projectId") int projectId, @PathVariable("id") int id)
			throws AciEntityNotFound {
		return tenantServices.getTenant(projectId, id);
	}

	@ApiOperation(value = "Get all tenants", notes = "Get all tenant details for a particular project by providing project id", response = Tenant.class, responseContainer = "List")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenants", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Tenant> getTenants(@PathVariable("projectId") int projectId) throws AciEntityNotFound {
		return tenantServices.getTenants(projectId);
	}

	@ApiOperation(value = "Get the tenant nodes", notes = "Get the nodes with respect to the tenant by providing project id, tenant id", response = ViewNode.class)
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/nodeCollection", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ViewNodes getNodeCollectionTenant(@PathVariable("projectId") int projectId,
			@PathVariable("tenantId") int tenantId) throws AciEntityNotFound {
		return tenantServices.getNodeCollectionTenant(projectId, tenantId);
	}

	@ApiOperation(value = "Update the view", notes = "Update the view/coordinates by providing the project id and tenant id, it returns the list of nodes which has been successfully updated")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/view", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public List<ViewNode> updateView(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@RequestBody List<ViewNode> nodes) throws AciEntityNotFound {
		return tenantServices.updateView(projectId, tenantId, nodes);
	}

	@ApiOperation(value = "Add a tenant to project with a templated tenant", notes = "Only one tenant can be added with the call, the tenant name is expected to be unique for this project, it will create a default VRF and a BD"
			+ ", name = unique tenant name" + ", templateType = 'tenant'" + ", model = Local/Common"
			+ ", instance = number of tenant instances", response = Tenant.class)

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenantTemplate", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Tenant addTenantTemplate(@PathVariable("projectId") int projectId,
			@Valid @RequestBody Tenant template) throws AciEntityNotFound, GenericInvalidDataException {
		return tenantServices.addTenantTemplate(projectId, template);
	}

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/nodeCollection/defaultValues", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public AciSizerModelUi getDefaultValues(@PathVariable("projectId") int projectId,
			@PathVariable("tenantId") int tenantId, @QueryParam("type") String type)
					throws AciEntityNotFound, GenericInvalidDataException {
		return tenantServices.getDefaultValues(projectId, tenantId, type);
	}
	
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/apic/{apicId}/applychanges", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public void pushConfig(@PathVariable("projectId") int projectId,
			@PathVariable("tenantId") int tenantId,@PathVariable("apicId") int apicId)
					throws AciEntityNotFound, GenericInvalidDataException {
		tenantServices.pushConfig(projectId, tenantId, apicId);
	}

}
