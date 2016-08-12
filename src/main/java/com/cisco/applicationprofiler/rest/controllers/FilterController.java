package com.cisco.applicationprofiler.rest.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.domain.Filter;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.models.View;
import com.cisco.applicationprofiler.services.FilterServices;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(ApplicationProfilerConstants.FILTER)
public class FilterController {

	
	@Inject
	private FilterServices filterServices;
	
	@ApiOperation(value = "fetch a filter", notes = "Only single device can be fetched with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.FilterEntry.class)

	public Filter getFilter(@PathVariable("id") int Id) throws AciEntityNotFound {
		return filterServices.getFilter(Id);
	}

	@ApiOperation(value = "fetch all filters", notes = "all device can be fetched with the call")
	@RequestMapping(method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Filter.class)
	public List<Filter> getFilters() throws AciEntityNotFound {
		return filterServices.getFilters();
	}
	
	@ApiOperation(value = "delete a filter", notes = "Only single filter can be RequestMethod with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.DELETE, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.Filter.class)
	public Filter deleteFilter(@PathVariable("id") int id) throws AciEntityNotFound {
		return filterServices.deleteFilter(id);
	}
}
