package com.cisco.applicationprofiler.rest.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.domain.Plugin;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.services.PluginServices;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(ApplicationProfilerConstants.PLUGINS)
public class PluginController {
	
	@Inject
	private PluginServices pluginServices;
	
	@ApiOperation(value = "fetch a plugin", notes = "Only single device can be fetched with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public Plugin getPlugin(@PathVariable("id") int Id) throws AciEntityNotFound {
		return pluginServices.getPlugin(Id);
	}

	@ApiOperation(value = "fetch all plugins", notes = "all device can be fetched with the call")
	@RequestMapping(method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public List<Plugin> getPlugins() throws AciEntityNotFound {
		return pluginServices.getPlugins();
	}

}
