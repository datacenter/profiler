package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cisco.applicationprofiler.domain.RepoObjects;


public interface RepoRepository extends JpaRepository<RepoObjects, Integer> {
	
}
