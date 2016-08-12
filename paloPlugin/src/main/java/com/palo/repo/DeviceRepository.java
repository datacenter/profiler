package com.palo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.palo.domain.Device;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

}
