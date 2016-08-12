package com.cisco.applicationprofiler.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.Device;
import com.cisco.applicationprofiler.domain.Plugin;
import com.cisco.applicationprofiler.models.Repos;
import com.cisco.applicationprofiler.repo.DeviceRepository;
import com.cisco.applicationprofiler.repo.RepoDao;

@Service
public class RepoServices {

	private static final String PLUGIN_NAME_VERSION_SEPERATOR = "-";
	private static final String JAR = ".jar";
	private static final int IMPORT_STATUS_SUCCESS = 3;
	private static final int IMPORT_STATUS_FAILURE = 4;
	private static final int IMPORT_IN_PROGRESS = 2;

	@Inject
	private RepoDao repoDao;

	@Inject
	private DeviceRepository deviceRepository;

	@Value("${profiler.plugin.file}")
	private String fileName;

	private static final Logger LOGGER = LoggerFactory.getLogger(RepoServices.class);

	public void importRepoObjects(int id) {
		Device device = deviceRepository.findOne(id);
		device.setImportedStatus(IMPORT_IN_PROGRESS);
		deviceRepository.save(device);
		callPlugin(device);
		deviceRepository.save(device);
	}

	private void callPlugin(Device device) {
		LOGGER.info("import started");

		String line = "";
		try {
			Plugin plugin = device.getModelDetails().getPlugin();
			String pluginJarName = plugin.getName() + PLUGIN_NAME_VERSION_SEPERATOR + plugin.getVersion() + JAR;

			LOGGER.info("executing command : " + "java -Dpaulo.plugin.device=" + device.getId()
					+ " -Dpaulo.plugin.filepath=" + plugin.getPath() + fileName + " -jar " + plugin.getPath()
					+ pluginJarName);
			Process proc = Runtime.getRuntime()
					.exec("java -Dpaulo.plugin.device=" + device.getId() + " -Dpaulo.plugin.filepath="
							+ plugin.getPath() + fileName + " -jar " + plugin.getPath() + pluginJarName);
			// Then retreive the process output

			InputStream in = proc.getInputStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			line = reader.readLine();

			while (line != null) {
				LOGGER.info(line);
				line = reader.readLine();
			}
			proc.waitFor();
			if (proc.exitValue() != 0) {
				device.setImportedStatus(IMPORT_STATUS_FAILURE);
				LOGGER.info("Exit value :" + proc.exitValue());
			} else {
				device.setImportedStatus(IMPORT_STATUS_SUCCESS);
				LOGGER.info("Status of device :  SUCCESS");
			}

		} catch (IOException e) {
			LOGGER.info(e.toString());
			device.setImportedStatus(IMPORT_STATUS_FAILURE);
			LOGGER.info("Exception :  FAILURE :" + e);
		} catch (Exception e) {
			device.setImportedStatus(IMPORT_STATUS_FAILURE);
			LOGGER.info("Exception :  FAILURE :" + e);

		}
		LOGGER.info("import completed");

	}

	public Repos getRepoObjects(String searchString, int startRecord, int numRecords, String sourceDeviceName,
			String type, String startDate, String endDate, int deviceId) {
		return repoDao.RepoDaoService(searchString, startRecord, numRecords, sourceDeviceName, type, startDate,
				endDate,deviceId);
	}
}
