package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.applicationprofiler.domain.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer>{

}
