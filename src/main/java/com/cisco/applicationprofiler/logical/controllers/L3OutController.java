/**
 * Copyright @maplelabs
 */
package com.cisco.applicationprofiler.logical.controllers;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.ForbiddenOperationException;
import com.cisco.applicationprofiler.logical.services.L3OutServices;
import com.cisco.applicationprofiler.ui.models.L3outUi;



/**
 * @author Deepa
 *
 */

@RestController
public class L3OutController {

	@Autowired
	private L3OutServices l3OutServices;

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/l3Out", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public L3outUi addL3Out(@PathVariable("projectId") int projectId,@PathVariable("tenantId") int tenantId,@PathVariable("vrfId") int vrfId,@RequestBody L3outUi l3Out) throws AciEntityNotFound {
		return l3OutServices.addL3Out(projectId,tenantId,vrfId, l3Out);
	}
	
	/* to be used to update only the vrf information , not to update the tenants structure */
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/l3Out/{id}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public L3outUi  updateL3Out(@PathVariable("projectId")int projectId,@PathVariable("tenantId") int tenantId,@PathVariable("vrfId") int vrfId,@PathVariable("id") int id,@RequestBody L3outUi vrf) throws AciEntityNotFound, ForbiddenOperationException {
		return l3OutServices.updateL3Out(projectId,tenantId,vrfId, id,vrf);
	}
	
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/l3Out/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public int  deleteL3Out(@PathVariable("projectId")int projectId,@PathVariable("tenantId") int tenantId,@PathVariable("vrfId") int vrfId,@PathVariable("id") int id) throws AciEntityNotFound {
		return l3OutServices.deleteL3Out(projectId,tenantId,vrfId, id);

	}
	
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/l3Out/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public L3outUi getL3Out(@PathVariable("projectId")int projectId,@PathVariable("tenantId") int tenantId,@PathVariable("vrfId") int vrfId,@PathVariable("id") int id) throws AciEntityNotFound {
			return l3OutServices.getL3Out(projectId,tenantId,vrfId, id);
	}
	
	
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/l3Outs", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<L3outUi> getL3Outs(@PathVariable("projectId") int projectId,@PathVariable("tenantId") int tenantId,@PathVariable("vrfId") int vrfId) throws AciEntityNotFound {
		return  l3OutServices.getL3Outs( projectId,tenantId,vrfId);
	}

	
}
