package com.cisco.applicationprofiler.rest.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cisco.applicationprofiler.domain.Project;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.models.View;
import com.cisco.applicationprofiler.services.ProjectServices;
import com.cisco.applicationprofiler.ui.models.ProjectUi;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(ApplicationProfilerConstants.PROJECT_URL)
public class ProjectControllers {

	@Inject
	private ProjectServices projectServices;

	@ApiOperation(value = "Add a project", notes = "Only single project can be added with the call")
	@RequestMapping(method = RequestMethod.POST, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Project.class)
	public Project addProject(@RequestBody ProjectUi projectUi) {
		return projectServices.addProject(projectUi);
	}

	@ApiOperation(value = "update a project", notes = "Only single project can be updated with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.PUT, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Project.class)
	public Project updateProject(@PathVariable("id") int id, @RequestBody ProjectUi projectUi)
			throws AciEntityNotFound {
		return projectServices.updateProject(id, projectUi);
	}

	@ApiOperation(value = "delete a project", notes = "Only single project can be deleted with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.DELETE, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Project.class)
	public Project deleteProject(@PathVariable("id") int id) throws AciEntityNotFound {
		return projectServices.deleteProject(id);
	}

	@ApiOperation(value = "get a project", notes = "Only single project can be fetched with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Project.class)
	public Project getProject(@PathVariable("id") int id) throws AciEntityNotFound {
		return projectServices.getProject(id);
	}

	@ApiOperation(value = "get all project", notes = "all projects can be fetched with the call")
	@RequestMapping(method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Project.class)
	public List<Project> getProjects() throws AciEntityNotFound {
		return projectServices.getProjects();
	}
}
