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
import com.cisco.applicationprofiler.logical.services.BdServices;
import com.cisco.applicationprofiler.ui.models.BdUi;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @author Deepa
 *
 */

@RestController
public class BdController {

	@Autowired
	private BdServices bdServices;

	@ApiResponses(value = { @ApiResponse(code = 409, message = "Conflict, item already exists"),
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save") })
	@ApiOperation(value = "Add an bd to vrf", notes = "Only one bd can be added with the call, the bd name is expected to be unique for this vrf", response = BdUi.class, responseContainer = "Self")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/bd", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public BdUi addBd(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("vrfId") int vrfId, @RequestBody BdUi bd) throws AciEntityNotFound {
		return bdServices.addBd(projectId, tenantId, vrfId, bd);
	}

	@ApiResponses(value = { @ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Update the bd", notes = "Update the bd by providing project id, tenant id and vrf id", response = BdUi.class, responseContainer = "Self")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/bd/{id}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public BdUi updateBd(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("vrfId") int vrfId, @PathVariable("id") int id, @RequestBody BdUi bd)
					throws AciEntityNotFound, ForbiddenOperationException {
		return bdServices.updateBd(projectId, tenantId, vrfId, id, bd);
	}

	@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized operation"),
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save") })

	@ApiOperation(value = "Delete the bd", notes = "Delete the bd by providing project id, tenant id and vrf id, deleting default bd is unauthorized")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/bd/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public int deleteBd(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("vrfId") int vrfId, @PathVariable("id") int id)
					throws DeletionNotAllowedException, AciEntityNotFound {
		return bdServices.deleteBd(projectId, tenantId, vrfId, id);

	}

	@ApiOperation(value = "Get the bd info", notes = "Get the bd info by providing project id, tenant id, vrf id and bd id", response = BdUi.class)
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/bd/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public BdUi getBd(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("vrfId") int vrfId, @PathVariable("id") int id) throws AciEntityNotFound {
		return bdServices.getBd(projectId, tenantId, vrfId, id);
	}

	@ApiOperation(value = "Get all bds", notes = "Get all bd details for a particular vrf by providing project id, tenant id and vrf id", response = BdUi.class, responseContainer = "List")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/bds", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<BdUi> getBds(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("vrfId") int vrfId) throws AciEntityNotFound {
		return bdServices.getBds(projectId, tenantId, vrfId);
	}

}
