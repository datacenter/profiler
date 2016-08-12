package com.cisco.applicationprofiler.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.Contract;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.repo.ContractRepository;

@Service
public class ContractServices {
	
	@Inject
	ContractRepository contractRepository;
	
	public Contract getContract(int id) throws AciEntityNotFound {
		Contract contract = ValidateAndGetContract(id);
		return contract;
	}

	private Contract ValidateAndGetContract(int id) throws AciEntityNotFound {
		Contract contract = contractRepository.findOne(id);
		if(null == contract){
			throw new AciEntityNotFound("Contract do not exist");
		}
		return contract;
	}

	public List<Contract> getContracts() {
		List<Contract> contract = contractRepository.findAll();
		return contract;
	}

	public Contract deleteContract(int id) throws AciEntityNotFound {
		Contract contract = ValidateAndGetContract(id);
		contractRepository.delete(id);
		return contract;
	}

}
