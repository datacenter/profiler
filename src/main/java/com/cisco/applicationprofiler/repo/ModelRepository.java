package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.applicationprofiler.domain.Model;

public interface ModelRepository extends JpaRepository<Model, Integer> {

}
