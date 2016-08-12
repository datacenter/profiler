package com.cisco.applicationprofiler.repo;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cisco.applicationprofiler.domain.ProjectTable;

public interface ProjectsRepository extends JpaRepository<ProjectTable, Integer> {

	@Modifying
	@Transactional
	@Query("update ProjectTable p set p.name = ?2 , p.type = ?3 , p.customerName = ?4, p.salesContact = ?5 , p.opportunity =?6 , p.account = ?7 , p.lastUpdatedTime =?8,p.description=?9 where p.id = ?1")
	void updateProject(int id, String name, String type, String customerName, String salesContact, String opportunity,
			String account, Timestamp lastUpdatedTime, String description);

	@Modifying
	@Transactional
	@Query("update ProjectTable p set p.device.id=?2 where p.id = ?1")
	void updateProjectWithDeviceId(int id, int deviceId);

	/**
	 * query to get all project names
	 * 
	 * @return names
	 */
	@Query("select name from ProjectTable")
	List<String> getProjectNames();

	@Query("select p from ProjectTable p where userid = ?1")
	List<ProjectTable> findProjectByUserId(String id);

	@Query("SELECT p from ProjectTable p WHERE " + "p.userId = :id and " + "(p.name LIKE CONCAT('%',:search, '%') OR "
			+ " p.account LIKE CONCAT('%',:search, '%') OR " + " p.description LIKE CONCAT('%',:search, '%'))"
			+ " order by p.name asc")
	List<ProjectTable> searchProjects(@Param("id") String id, @Param("search") String query, Pageable pageRequest);

}
