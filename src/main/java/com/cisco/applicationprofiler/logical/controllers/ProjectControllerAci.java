/**
 * Copyright @maplelabs
 */
package com.cisco.applicationprofiler.logical.controllers;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.applicationprofiler.domain.ProjectTable;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.AciFileIoException;
import com.cisco.applicationprofiler.exceptions.AciSizingException;
import com.cisco.applicationprofiler.exceptions.ProjectNameExistsException;
import com.cisco.applicationprofiler.exceptions.ProjectTypeInvalidException;
import com.cisco.applicationprofiler.logical.services.ProjectServicesAci;
import com.cisco.applicationprofiler.models.Project;
import com.cisco.applicationprofiler.models.Tenant;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Deepa
 *
 */

@RestController
public class ProjectControllerAci {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectControllerAci.class);

	@Inject
	private ProjectServicesAci projectServices;

	private String getUserId(HttpServletRequest request) {
		String userId = request.getHeader("auth_user");
		if (null == userId) {
			userId = request.getHeader("http_auth_user");
			if (null == userId)
				userId = "dummy";
		}

		System.out.println("The logged in user : " + userId);

		return userId;
	}

	@ApiOperation(httpMethod = "POST", value = "Add a Project")
	@RequestMapping(value = "/profiler/v1/project", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Project addProject(HttpServletRequest request, @RequestBody ProjectTable project)
			throws ProjectTypeInvalidException, ProjectNameExistsException {
		return projectServices.addProject(getUserId(request), project);
	}

	@ApiOperation(httpMethod = "PUT", value = "Updates a Project")
	@RequestMapping(value = "/profiler/v1/project/{id}", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public Project updateProject(HttpServletRequest request, @PathVariable("id") int projectId,
			@RequestBody ProjectTable project) throws AciEntityNotFound, ProjectNameExistsException {
		return projectServices.updateProject(project, projectId);
	}

	@ApiOperation(httpMethod = "DELETE", value = "deletes a Project")
	@RequestMapping(value = "/profiler/v1/project/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public int deleteProject(HttpServletRequest request, @PathVariable("id") int projectId) throws AciEntityNotFound {
		return projectServices.deleteProject(projectId);
	}

	@ApiOperation(httpMethod = "GET", value = "Gets a Project")
	@RequestMapping(value = "/profiler/v1/project/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Project getProject(HttpServletRequest request, @PathVariable("id") int projectId)
			throws AciEntityNotFound, AciFileIoException, AciSizingException {
		return projectServices.getProject(projectId);
	}

	@ApiOperation(httpMethod = "GET", value = "Gets a Project sizing results")
	@RequestMapping(value = "/profiler/v1/project/{id}/sizingresults", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Project getProjectSizingResults(HttpServletRequest request, @PathVariable("id") int projectId)
			throws AciEntityNotFound, AciFileIoException, AciSizingException {
		return projectServices.getSizingREsults(projectId);
	}

	@ApiOperation(httpMethod = "GET", value = "Gets all Project")
	@RequestMapping(value = "/profiler/v1/projects", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Project> getProjects(HttpServletRequest request) {
		logServerInfo();
		return projectServices.getProjects(getUserId(request));
	}

	private void logServerInfo() {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		LOGGER.info(("Current relative path is: " + s));

		LOGGER.info("user.dir: " + System.getProperty("user.dir"));
		LOGGER.info(new File(System.getProperty("catalina.base")).getAbsoluteFile().toString());
		InetAddress ip;
		String hostname;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			LOGGER.info("Your current IP address : " + ip);
			LOGGER.info("Your current Hostname : " + hostname);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}


	@RequestMapping(value = "/profiler/v1/project/{projectId}/nodeCollection/defaultValues", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Tenant getDefaultValueTenant(HttpServletRequest request, @PathVariable("projectId") int projectId)
			throws AciEntityNotFound {
		return projectServices.getDefaultValueTenant(projectId);
	}

	@ApiOperation(httpMethod = "POST", value = "clone a Project")
	@RequestMapping(value = "/profiler/v1/project/{projectId}/clone", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Project createClone(HttpServletRequest request, @PathVariable("projectId") int projectId) {
		return projectServices.createClone(getUserId(request), projectId);
	}

	@ApiOperation(httpMethod = "GET", value = "Search all projects with respect to the search keys")
	@RequestMapping(value = "/profiler/v1/projectQuery", params = { "query", "pageNumber",
			"pageSize" }, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Project> searchProjects(HttpServletRequest request,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "pageNumber", required = true) int pageNumber,
			@RequestParam(value = "pageSize", required = true) int pageSize) {
		return projectServices.searchProjects(getUserId(request), query, pageNumber, pageSize);
	}


}
