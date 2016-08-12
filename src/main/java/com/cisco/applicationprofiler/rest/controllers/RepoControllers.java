package com.cisco.applicationprofiler.rest.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.models.Repos;
import com.cisco.applicationprofiler.services.RepoServices;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;

@Controller
public class RepoControllers {
	@Inject
	private RepoServices repoServices;

	@RequestMapping(value = ApplicationProfilerConstants.REPO, method = RequestMethod.GET, params = { "searchString",
			"startRecord", "numRecords", "sourceDeviceName", "type","startDate","endDate","deviceId" }, produces = "application/json")
	@ResponseBody
	public Repos getRepoObjects(@RequestParam(value = "searchString", required = false) String searchString,
			@RequestParam(value = "startRecord", required = false) int startRecord,
			@RequestParam(value = "numRecords", required = false) int numRecords,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "sourceDeviceName", required = false) String sourceDeviceName,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value= "deviceId", required=true) int deviceId) {
		return repoServices.getRepoObjects(searchString, startRecord, numRecords, sourceDeviceName, type, startDate,
				endDate, deviceId);
	}

	@RequestMapping(value = ApplicationProfilerConstants.REPO_IMPORT_URL, method = RequestMethod.POST, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	private void importRepoObjects(@PathVariable("id") int id) throws AciEntityNotFound {
		repoServices.importRepoObjects(id);
	}

}