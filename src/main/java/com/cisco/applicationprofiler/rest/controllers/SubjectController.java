package com.cisco.applicationprofiler.rest.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.domain.Subject;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.models.View;
import com.cisco.applicationprofiler.services.SubjectServices;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(ApplicationProfilerConstants.SUBJECT)
public class SubjectController {
	@Inject
	private SubjectServices subjectServices;
	
	@ApiOperation(value = "fetch a subject", notes = "Only single subject can be fetched with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Subject.class)
	public Subject getSubject(@PathVariable("id") int Id) throws AciEntityNotFound {
		return subjectServices.getSubject(Id);
	}

	@ApiOperation(value = "fetch all subject", notes = "all subject can be fetched with the call")
	@RequestMapping(method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Subject.class)
	public List<Subject> getSubjects() throws AciEntityNotFound {
		return subjectServices.getSubjects();
	}
	
	@ApiOperation(value = "delete a subject", notes = "single subject can be deleted with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.DELETE, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Subject.class)
	public Subject deleteSubjects(@PathVariable("id") int id) throws AciEntityNotFound {
		return subjectServices.deleteSubjects(id);
	}
}
