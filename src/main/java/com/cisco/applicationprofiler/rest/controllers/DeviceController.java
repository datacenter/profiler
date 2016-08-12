package com.cisco.applicationprofiler.rest.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cisco.applicationprofiler.domain.Device;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.services.DeviceServices;
import com.cisco.applicationprofiler.ui.models.DeviceUi;
import com.cisco.applicationprofiler.util.ApplicationProfilerConstants;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(ApplicationProfilerConstants.DEVICES)
public class DeviceController {
	@Inject
	private DeviceServices deviceServices;

	@ApiOperation(value = "Add a device", notes = "Only single device can be added with the call")
	@RequestMapping(method = RequestMethod.POST, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public Device addDevice(@RequestBody DeviceUi deviceUi) {
		return deviceServices.addDevice(deviceUi);
	}

	@ApiOperation(value = "delete a device", notes = "Only single device can be deleted with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.DELETE, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public Device deleteDevice(@PathVariable("id") int Id) throws AciEntityNotFound {
		return deviceServices.deleteDevice(Id);
	}

	@ApiOperation(value = "update a device", notes = "Only single device can be updated with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.PUT, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public Device updateDevice(@PathVariable("id") int Id, @RequestBody DeviceUi deviceUi) throws AciEntityNotFound {
		return deviceServices.updateDevice(Id, deviceUi);
	}

	@ApiOperation(value = "fetch a device", notes = "Only single device can be fetched with the call")
	@RequestMapping(value = ApplicationProfilerConstants.PATH_PARAM, method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public Device getDevice(@PathVariable("id") int Id) throws AciEntityNotFound {
		return deviceServices.getDevice(Id);
	}

	@ApiOperation(value = "fetch all devices", notes = "all device can be fetched with the call")
	@RequestMapping(method = RequestMethod.GET, produces = ApplicationProfilerConstants.JSON)
	@ResponseBody
	public List<Device> getDevices() throws AciEntityNotFound {
		return deviceServices.getDevices();
	}

}
