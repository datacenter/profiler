package com.cisco.applicationprofiler.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.applicationprofiler.domain.Contract;

public interface ContractRepository extends JpaRepository<Contract, Integer> {

}
