package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.applicationprofiler.domain.Device;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

}
