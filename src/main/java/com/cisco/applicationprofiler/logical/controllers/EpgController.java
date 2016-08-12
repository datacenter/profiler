package com.cisco.applicationprofiler.logical.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.DeletionNotAllowedException;
import com.cisco.applicationprofiler.exceptions.GenericInvalidDataException;
import com.cisco.applicationprofiler.logical.services.EpgServices;
import com.cisco.applicationprofiler.ui.models.EpgUi;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@RestController
public class EpgController extends AciController{

	@Autowired
	private EpgServices epgServices;

	@ApiResponses(value = { @ApiResponse(code = 409, message = "Conflict, item already exists"),
			@ApiResponse(code = 507, message = "Insufficient Storage, could not save") })
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/epg", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public EpgUi addEpg(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId, @Valid @RequestBody EpgUi epg)
					throws AciEntityNotFound, GenericInvalidDataException {

		EpgUi respEpg = epgServices.addEpg(projectId, tenantId, appId, epg);
		return respEpg;
	}

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/uuid", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, String> getUuid(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId) {

		Map<String, String> uidMap = new HashMap<String, String>(1);

		String uuid = UUID.randomUUID().toString();
		uidMap.put("uuid", uuid);
		return uidMap;
	}

	@ApiResponses(value = { @ApiResponse(code = 507, message = "Insufficient Storage, could not save") })
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/epg/{epgId}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public EpgUi updateEpg(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId, @PathVariable("epgId") int epgId, @Valid @RequestBody EpgUi epg)
					throws AciEntityNotFound {

		EpgUi respEpg = epgServices.updateEpg(projectId, tenantId, appId, epgId, epg);
		return respEpg;

	}

	@ApiResponses(value = { @ApiResponse(code = 507, message = "Insufficient Storage, could not save") })
	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/epg/{epgId}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public int deleteEpg(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId, @PathVariable("epgId") int epgId)
					throws DeletionNotAllowedException, AciEntityNotFound {
		return (epgServices.deleteEpg(projectId, tenantId, appId, epgId));
	}

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/epg/{epgId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public EpgUi getEpg(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId, @PathVariable("epgId") int epgId) throws AciEntityNotFound {

		EpgUi respEpg = epgServices.getEpg(projectId, tenantId, appId, epgId);
		return respEpg;

	}

	@RequestMapping(value = "/profiler/v1/project/{projectId}/tenant/{tenantId}/app/{appId}/epgs", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<EpgUi> getEpgs(@PathVariable("projectId") int projectId, @PathVariable("tenantId") int tenantId,
			@PathVariable("appId") int appId) throws AciEntityNotFound {
		return epgServices.getEpgs(projectId, tenantId, appId);
	}

	

}	
