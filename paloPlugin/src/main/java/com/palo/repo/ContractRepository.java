package com.palo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.palo.domain.Contract;


public interface ContractRepository extends JpaRepository<Contract, Integer> {

}
