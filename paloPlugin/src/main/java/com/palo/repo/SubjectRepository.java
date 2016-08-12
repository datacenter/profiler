package com.palo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.palo.domain.Subject;


public interface SubjectRepository extends JpaRepository<Subject, Integer>{

}
