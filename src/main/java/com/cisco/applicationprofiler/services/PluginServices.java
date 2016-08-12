package com.cisco.applicationprofiler.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.Plugin;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.repo.PluginRepository;
@Service
public class PluginServices {
	@Inject
	PluginRepository pluginRepository;

	public Plugin getPlugin(int id) throws AciEntityNotFound {
		Plugin plugin =pluginRepository.findOne(id);
		if (null == plugin) {
			throw new AciEntityNotFound("plugin do not exist");
		}
		return plugin;
	}

	public List<Plugin> getPlugins() {
		List<Plugin> plugin =pluginRepository.findAll();
		return plugin;
	}

}
