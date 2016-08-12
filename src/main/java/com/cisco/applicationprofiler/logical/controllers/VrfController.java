/**
 * Copyright @maplelabs
 */
package com.cisco.applicationprofiler.logical.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.DeletionNotAllowedException;
import com.cisco.applicationprofiler.exceptions.ForbiddenOperationException;
import com.cisco.applicationprofiler.logical.services.VrfServices;
import com.cisco.applicationprofiler.ui.models.VrfUi;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @author Deepa
 *
 */

@RestController
public class VrfController {

	@Autowired
	private VrfServices vrfServices;

	@ApiResponses(value = { @ApiResponse(code = 409, message = "Conflict, item already exists"),
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Add an vrf to tenant", notes = "Only single vrf can be added with the call, the vrf name is expected to be unique for this tenant", response = VrfUi.class, responseContainer = "Self")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public VrfUi addVrf(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@RequestBody VrfUi vrf) throws AciEntityNotFound {
		return vrfServices.addVrf(projectId, tenantId, vrf);
	}

	/*
	 * to be used to update only the vrf information , not to update the tenants
	 * structure
	 */
	@ApiResponses(value = { @ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Update the vrf", notes = "Update the vrf by providing project id, tenant id and vrf id", response = VrfUi.class, responseContainer = "Self")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{id}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public VrfUi updateVrf(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("id") int id, @RequestBody VrfUi vrf) throws AciEntityNotFound, ForbiddenOperationException {
		return vrfServices.updateVrf(projectId, tenantId, id, vrf);
	}

	@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized operation"),
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Delete the vrf", notes = "Delete the vrf by providing project id, tenant id and vrf id, deleting the default vrf is unauthorized")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public int deleteVrf(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("id") int id) throws DeletionNotAllowedException, AciEntityNotFound {
		return vrfServices.deleteVrf(projectId, tenantId, id);

	}

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public VrfUi getVrf(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("id") int id) throws AciEntityNotFound {
		return vrfServices.getVrf(projectId, tenantId, id);
	}

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrfs", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<VrfUi> getVrfs(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId)
			throws AciEntityNotFound {
		return vrfServices.getVrfs(projectId, tenantId);
	}

}
