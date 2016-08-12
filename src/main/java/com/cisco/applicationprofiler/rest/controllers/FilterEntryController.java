package com.cisco.applicationprofiler.rest.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.domain.FilterEntry;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.models.View;
import com.cisco.applicationprofiler.services.FilterEntryServices;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(ApplicationProfilerConstants.FILTER_ENTRY)
public class FilterEntryController {
	@Inject
	private FilterEntryServices filterEntryServices;

	@ApiOperation(value = "fetch a filterEntry", notes = "Only single filterEntry can be fetched with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.FilterEntry.class)
	public FilterEntry getFilterEntry(@PathVariable("id") int Id) throws AciEntityNotFound {
		return filterEntryServices.getFilterEntry(Id);
	}

	@ApiOperation(value = "fetch all filterEntry", notes = "all filterEntry can be fetched with the call")
	@RequestMapping(method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.FilterEntry.class)

	public List<FilterEntry> getFilterEntry() throws AciEntityNotFound {
		return filterEntryServices.getFilterEntries();
	}
	
	@ApiOperation(value = "delete a filterEntry", notes = "Only single filterEntry can be RequestMethod with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.DELETE, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	@JsonView(View.FilterEntry.class)
	public FilterEntry deleteFilterEntry(@PathVariable("id") int id) throws AciEntityNotFound {
		return filterEntryServices.deleteFilterEntry(id);
	}
}
