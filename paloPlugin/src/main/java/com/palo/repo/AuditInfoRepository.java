/**
 * 
 */
package com.palo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.palo.domain.AuditInfo;


/**
 * @author Mahesh
 *
 */
public interface AuditInfoRepository extends JpaRepository<AuditInfo, Integer> {

}
