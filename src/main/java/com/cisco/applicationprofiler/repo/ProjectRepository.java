package com.cisco.applicationprofiler.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.cisco.applicationprofiler.domain.Project;


public interface ProjectRepository extends JpaRepository<Project, Integer>  {

	@Query("SELECT p FROM Project p WHERE p.user.id=?1")
	List<Project> findProjectsByUserId(int userId);
}
