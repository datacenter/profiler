package com.cisco.applicationprofiler.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.Device;
import com.cisco.applicationprofiler.domain.Model;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.repo.DeviceRepository;
import com.cisco.applicationprofiler.repo.ModelRepository;
import com.cisco.applicationprofiler.ui.models.DeviceUi;

@Service
public class DeviceServices {

	@Inject
	private DeviceRepository deviceRepository;
	@Inject
	private ModelRepository modelRepository;

	public Device addDevice(DeviceUi deviceUi) {
		Device device = new Device();
		device.setName(deviceUi.getName());
		device.setIpAddress(deviceUi.getIpAddress());
		device.setUsername(deviceUi.getUsername());
		device.setPassword(deviceUi.getPassword());
		device.setImportedStatus(1);
		Model model = modelRepository.findOne(deviceUi.getModelId());
		device.setModelDetails(model);
		if ("aci".equalsIgnoreCase(model.getName())){
			device.setType("ACI");
		} else {
			device.setType("PauloAlto");
		}
		deviceRepository.save(device);
		return device;
	}

	public Device deleteDevice(int id) throws AciEntityNotFound {
		Device device = ValidateAndGetDevice(id);
		deviceRepository.delete(id);
		return device;
	}

	public Device updateDevice(int id, DeviceUi deviceUi) throws AciEntityNotFound {
		Device device = ValidateAndGetDevice(id);
		device.setName(deviceUi.getName());
		device.setIpAddress(deviceUi.getIpAddress());
		device.setUsername(deviceUi.getUsername());
		device.setPassword(deviceUi.getPassword());
		Model model = modelRepository.findOne(deviceUi.getModelId());
		device.setModelDetails(model);
		deviceRepository.save(device);
		return device;
	}

	public Device getDevice(int id) throws AciEntityNotFound {
		Device device = ValidateAndGetDevice(id);
		return device;
	}

	public List<Device> getDevices() {
		List<Device> device = deviceRepository.findAll();
		return device;
	}

	private Device ValidateAndGetDevice(int id) throws AciEntityNotFound {
		Device device = deviceRepository.findOne(id);
		if (null == device) {
			throw new AciEntityNotFound("Device do not exist");
		}
		return device;
	}

}
