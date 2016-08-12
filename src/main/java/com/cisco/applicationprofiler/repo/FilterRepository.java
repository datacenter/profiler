package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.applicationprofiler.domain.Filter;

public interface FilterRepository extends JpaRepository<Filter, Integer>{

}
