/**
 * 
 */
package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.applicationprofiler.domain.Application;

/**
 * @author Mahesh
 *
 */
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

}
