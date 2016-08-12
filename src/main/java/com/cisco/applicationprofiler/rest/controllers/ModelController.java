package com.cisco.applicationprofiler.rest.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.domain.Model;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.services.ModelServices;
import com.cisco.applicationprofiler.ui.models.ModelUi;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(ApplicationProfilerConstants.MODEL)
public class ModelController {

	@Inject
	private ModelServices modelServices;
	

	@ApiOperation(value = "Add a model", notes = "Only single model can be added with the call")
	@RequestMapping(method = RequestMethod.POST, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public Model addModel(@RequestBody ModelUi modelUi) throws AciEntityNotFound {
		return modelServices.addModel(modelUi);
	}

	@ApiOperation(value = "fetch a model", notes = "Only single model can be fetched with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public Model getModel(@PathVariable("id") int Id) throws AciEntityNotFound {
		return modelServices.getModel(Id);
	}

	@ApiOperation(value = "fetch all models", notes = "all models can be fetched with the call")
	@RequestMapping(method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public List<Model> getModels() throws AciEntityNotFound {
		return modelServices.getModels();
	}

}
