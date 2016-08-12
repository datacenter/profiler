package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.applicationprofiler.domain.Plugin;

public interface PluginRepository extends JpaRepository<Plugin, Integer>{

}
