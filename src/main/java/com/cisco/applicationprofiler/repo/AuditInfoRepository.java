/**
 * 
 */
package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.applicationprofiler.domain.AuditInfo;

/**
 * @author Mahesh
 *
 */
public interface AuditInfoRepository extends JpaRepository<AuditInfo, Integer> {

}
