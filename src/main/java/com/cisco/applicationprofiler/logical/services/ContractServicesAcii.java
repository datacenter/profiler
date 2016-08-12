package com.cisco.applicationprofiler.logical.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.ProjectTable;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.ContractInvalidDetailException;
import com.cisco.applicationprofiler.exceptions.ContractNameExistsException;
import com.cisco.applicationprofiler.exceptions.GenericCouldNotSaveException;
import com.cisco.applicationprofiler.helper.TenantHelper;
import com.cisco.applicationprofiler.models.Application;
import com.cisco.applicationprofiler.models.Contract;
import com.cisco.applicationprofiler.models.Epg;
import com.cisco.applicationprofiler.models.Filter;
import com.cisco.applicationprofiler.models.L3out;
import com.cisco.applicationprofiler.models.LogicalRequirement;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.SharedResource;
import com.cisco.applicationprofiler.models.Subject;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.repo.ProjectsRepository;
import com.cisco.applicationprofiler.ui.models.ContractUi;
import com.cisco.applicationprofiler.util.ACISizerConstant;
import com.cisco.applicationprofiler.util.AppUtilities;
import com.cisco.applicationprofiler.util.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Service
public class ContractServicesAcii {

	public static final String CONTRACT_NOT_FOUND_WITH_ID = "Contract not found with id ";

	@Inject
	private ProjectsRepository m_projrepo;
	
	@Inject
	private ProjectServicesAci projectServices;

	@Inject
	private Gson gson;

	public void inputMapping(com.cisco.applicationprofiler.ui.models.ContractUi src, Contract dest) {
		dest.setConsumerEnforced(src.isConsumerEnforced());
		dest.setConsumerId(""+src.getConsumerId());
		dest.setConsumerType(src.getConsumerType());
		dest.setProviderEnforced(src.isProviderEnforced());
		dest.setProviderId(""+src.getProviderId());
		dest.setProviderType(src.getProviderType());
		dest.setUiData(src.getUiData());
		dest.setUnique_filters(src.getNoOfFilters());
		dest.setSubjects(src.getSubjects());
		dest.setConfigName(src.getConfigName());
	}

	public com.cisco.applicationprofiler.ui.models.ContractUi outputMapping(Contract src, com.cisco.applicationprofiler.ui.models.ContractUi dest, Tenant tenant,
			Tenant commonTenant) {
		dest.setConsumerEnforced(src.getConsumerEnforced());
		dest.setConsumerId(Integer.parseInt(src.getConsumerId()));
		//dest.setConsumerName(src.getDisplayName());
		dest.setConsumerType(src.getConsumerType());
		dest.setFullName(tenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + src.getDisplayName());
		dest.setId(Integer.parseInt(src.getName()));
		dest.setName(src.getDisplayName());
		dest.setNoOfFilters(src.getUnique_filters());
		dest.setProviderEnforced(src.getProviderEnforced());
		dest.setProviderId(Integer.parseInt(src.getProviderId()));
		setProviderName(src, dest, tenant);
		setConsumerName(src, dest, tenant, commonTenant);
		dest.setProviderType(src.getProviderType());
		dest.setUiData(src.getUiData());
		dest.setSubjects(src.getSubjects());
		dest.setConfigName(src.getConfigName());
		if(src.getConsumerType().equals(ACISizerConstant._l3out)){
			dest.setType(ACISizerConstant._contractWithl3out);
		}
		else if(src.getConsumerType().equals(ACISizerConstant._shared_resource)){
			dest.setType(ACISizerConstant._contractWithSharedResource);
		}
		return dest;
	}

	/**
	 * @param src
	 * @param dest
	 * @param tenant
	 */
	private void setProviderName(Contract src, com.cisco.applicationprofiler.ui.models.ContractUi dest, Tenant tenant) {
		/*tenant.getEpgs().parallelStream().filter(epg -> (epg.getId() == src.getProviderId())).findFirst()
				.ifPresent(epg -> dest.setProviderName(getFullQualifiedNameEpg(tenant, epg)));*/
		for (Epg epg : tenant.getEpgs()) {
			if(epg.getName().equals(src.getProviderId())){
				dest.setProviderName(getFullQualifiedNameEpg(tenant, epg));
				return;
			}
		}
	}

	/**
	 * @param src
	 * @param dest
	 * @param tenant
	 * @param commonTenant
	 */
	private void setConsumerName(Contract src, com.cisco.applicationprofiler.ui.models.ContractUi dest, Tenant tenant,
			Tenant commonTenant) {
		if (src.getConsumerType().equals(ACISizerConstant._epg)) {
			/*tenant.getEpgs().parallelStream().filter(epg -> (epg.getId() == src.getConsumerId())).findFirst()
					.ifPresent(epg -> dest.setConsumerName(getFullQualifiedNameEpg(tenant, epg)));*/
			for (Epg epg : tenant.getEpgs()) {
				if(epg.getName().equals(src.getConsumerId())){
					dest.setConsumerName(getFullQualifiedNameEpg(tenant, epg));
					break;
				}
			}
			
		} else {
		/*	Optional<L3out> l3outOptional = tenant.getL3outs().parallelStream()
					.filter(l3out -> l3out.getId() == src.getConsumerId()).findFirst();
			if (l3outOptional.isPresent()) {
				dest.setConsumerName(getFullyQualifiedNameL3out(tenant, l3outOptional.get()));
			} else {
				l3outOptional = commonTenant.getL3outs().parallelStream()
						.filter(l3out -> l3out.getId() == src.getConsumerId()).findFirst();
				if (l3outOptional.isPresent()) {
					dest.setConsumerName(getFullyQualifiedNameL3out(commonTenant, l3outOptional.get()));
				}
			}*/
			
			for (L3out l3out : tenant.getL3outs()) {
				if (l3out.getName().equals(src.getConsumerId()) ){
					dest.setConsumerName(getFullyQualifiedNameL3out(tenant, l3out));
					break;
				} 
			}

			if(null == dest.getConsumerName())
			{
				for (L3out l3outIter : commonTenant.getL3outs()) {
					if (l3outIter.getName().equals(src.getConsumerId())) {
						dest.setConsumerName(getFullyQualifiedNameL3out(commonTenant, l3outIter));
						break;
					}
				}
				if(null == dest.getConsumerName())
				{
					for (SharedResource sr : commonTenant.getSharedResources()) {
						if (sr.getName()== src.getConsumerId()) {
							dest.setConsumerName(getFullyQualifiedNameSharedResource(commonTenant, sr));
							break;
						}
					}
				}

			}


		}
	}

	private String getFullyQualifiedNameSharedResource(Tenant tenant,
			SharedResource sr) {
		return tenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + sr.getDisplayName();
	}

	/**
	 * @param tenant
	 * @param l3out
	 * @return
	 */
	private String getFullyQualifiedNameL3out(Tenant tenant, L3out l3out) {
		return tenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + l3out.getDisplayName();
	}

	/**
	 * @param tenant
	 * @param epg
	 * @return
	 */
	private String getFullQualifiedNameEpg(Tenant tenant, Epg epg) {
		return tenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + epg.getDisplayName();
	}

	public com.cisco.applicationprofiler.ui.models.ContractUi addContract(int projectId, int tenantId, int appId,
			com.cisco.applicationprofiler.ui.models.ContractUi srcContract) throws AciEntityNotFound {
		ProjectTable proj = m_projrepo.getOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);;
		Application currApp = AppUtilities.getCurrentApp(appId, currTenant);
		if (null == currApp) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID );
		}
		Contract contractToAdd = new Contract(logicalSummary.generateUniqueId(), srcContract.getName(),
				currApp.getName());
		
		//contractToAdd.setSubjects(srcContract.getSubjects());
		inputMapping(srcContract, contractToAdd);
		if (!addContract(contractToAdd, commonTenant, currTenant,currApp,logicalSummary, currApp)) {
			throw new ContractNameExistsException("Contract name already exists " + contractToAdd.getDisplayName());
		}
		

		// convert back to json string

		/*String addedContract = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(addedContract);

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));*/

		// set it back to the project and update db
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			com.cisco.applicationprofiler.ui.models.ContractUi contractAdded = new com.cisco.applicationprofiler.ui.models.ContractUi();
			outputMapping(contractToAdd, contractAdded, currTenant, commonTenant);
			return contractAdded;
		} else {
			throw new GenericCouldNotSaveException("Could not save contract data");
		}

	}

	/**
	 * @param tenantId
	 * @param appId
	 * @param contract
	 * @param commonTenant
	 * @param currTenant
	 * @param logicalSummary 
	 * @param currApp 
	 * @param currApp
	 * @param currentApp 
	 * @return
	 */
	public boolean addContract(Contract contract, Tenant commonTenant, Tenant currTenant, Application currApp, LogicalSummary logicalSummary, Application currentApp) {
		int effectiveContractCount = currApp.getTotalContractCount();
		currApp.setTotalContractCount(effectiveContractCount+1);
		setUniqueFilterCount(contract);
		currApp.setTotalFilterCount(currApp.getTotalFilterCount()+contract.getUnique_filters());
		
		return addContract(contract, commonTenant, currTenant);
	}

	private void setUniqueFilterCount(Contract contract) {
		int filterCount =0;
		
			for(Subject subject : contract.getSubjects()){
				for(Filter filter : subject.getFilters()){
					filterCount = filterCount + filter.getFilterEntry().size();
					
				}
			}
			
		
		
		contract.setUnique_filters(filterCount);
	}

	public boolean addContract(Contract contract, Tenant commonTenant, Tenant currTenant) {
		String msg;
		msg = validateAndUpdateProvidersConsumers(currTenant, contract, commonTenant);
		if (null != msg) {
			throw new ContractInvalidDetailException(msg);
		}
		
		
		
		return currTenant.getContracts().add(contract);
	}

	public com.cisco.applicationprofiler.ui.models.ContractUi updateContract(int projectId, int tenantId, int appId, int contractId,
			com.cisco.applicationprofiler.ui.models.ContractUi srcContract) throws AciEntityNotFound {
		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = null;
		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (tenantId == tenant.getId()) {
				currTenant = tenant;
				break;
			}

		}
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		TenantHelper.modifiedTime(currTenant);
		Contract currContract = null;

		for (Contract contract : currTenant.getContracts()) {
			if (contract.getId() == contractId) {
				currContract = contract;
				break;
			}
		}
		if (currContract == null) {
			throw new AciEntityNotFound(CONTRACT_NOT_FOUND_WITH_ID);
		}

		Application currApp = AppUtilities.getCurrentApp(appId, currTenant);
		if (null == currApp) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID);
		}

		Contract contractToUpdate = new Contract(contractId, srcContract.getName(), currApp.getName());
		inputMapping(srcContract, contractToUpdate);
		currTenant.getContracts().remove(currContract);
		if (!currTenant.getContracts().add(contractToUpdate)) {
			throw new ContractInvalidDetailException(null);
		}
		
		int count = currApp.getTotalFilterCount() - currContract.getUnique_filters() + contractToUpdate.getUnique_filters();
		currApp.setTotalFilterCount(count);

		// convert back to json string
		/*String addedContract = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(addedContract);*/

		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			com.cisco.applicationprofiler.ui.models.ContractUi contractUpdated = new com.cisco.applicationprofiler.ui.models.ContractUi();
			outputMapping(contractToUpdate, contractUpdated, currTenant, Utility.getCommonTenant(logicalRequirement));
			return contractUpdated;
		} else {
			throw new GenericCouldNotSaveException("Could not save contract data");
		}
	}	
	
	
	
	public int deleteContract(int projectId, int tenantId, int appId, int contractId) throws AciEntityNotFound {
		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		return deleteContract(tenantId, appId, contractId, proj);

		// set it back to the project and update db

	}

	
	

	/**
	 * @param tenantId
	 * @param contractId
	 * @param tenantList
	 * @param foundContractIndex
	 * @param proj
	 * @return 
	 * @throws AciEntityNotFound
	 * @throws JsonSyntaxException
	 * @throws TenantNotFoundException
	 * @throws ContractNotFoundException
	 */
	public int deleteContract(int tenantId, int appId, int contractId, ProjectTable proj) throws AciEntityNotFound {
		Tenant currentTenant = null;
		Tenant commonTenant = null;

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		currentTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currentTenant);
		commonTenant = Utility.getCommonTenant(logicalRequirement);
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		if (null == currentTenant) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		Application currApp = AppUtilities.getCurrentApp(appId, currentTenant);
		if (null == currApp) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID);
		}
		deleteContract(contractId, currentTenant, commonTenant, currApp, logicalSummary);
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);
		//projectServices.callSizingSummary(projectId);
		if (returnProj != null) {
			return 1;
		} else {
			throw new GenericCouldNotSaveException("Could not save contract data");
		}
	/*	// convert back to json string
		String deletedContract = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(deletedContract);

		// update the summary also
		// convert it to java obj

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));*/
	}


	/**
	 * @param contractId
	 * @param currentTenant
	 * @param commonTenant
	 * @return contract
	 * @throws AciEntityNotFound
	 */
	public Contract deleteContract(int contractId, Tenant currentTenant, Tenant commonTenant,Application currentApp,LogicalSummary logicalSummary) throws AciEntityNotFound {
		Contract contractToBeRemoved = null;
		for (Contract contract : currentTenant.getContracts()) {
			if (contractId == contract.getId()) {
				contractToBeRemoved = contract;
				break;
			}
		}
		if (contractToBeRemoved == null) {
			throw new AciEntityNotFound(CONTRACT_NOT_FOUND_WITH_ID);
		}
		int count = logicalSummary.getContractCount();
		logicalSummary.setContractCount(count - currentApp.getInstances());	
		
		int effectiveFIlterCount = logicalSummary.getFilterCount()  - contractToBeRemoved.getUnique_filters() * currentApp.getInstances();
		logicalSummary.setFilterCount(effectiveFIlterCount);
		
		int effectiveContractCount = currentApp.getTotalContractCount();
		
		currentApp.setTotalContractCount(effectiveContractCount-1);
		currentApp.setTotalFilterCount(currentApp.getTotalFilterCount()-contractToBeRemoved.getUnique_filters());
		
		return deleteContract(currentTenant, commonTenant, contractToBeRemoved);
	}
	
	/**
	 * @param currentTenant
	 * @param commonTenant
	 * @param contractToBeRemoved
	 * @return
	 */
	public Contract deleteContract(Tenant currentTenant, Tenant commonTenant, Contract contractToBeRemoved) {
		updateProviderConsumers(currentTenant, commonTenant, contractToBeRemoved);
		currentTenant.getContracts().remove(contractToBeRemoved);
		return contractToBeRemoved;
	}

	public void updateProviderConsumers(Tenant currentTenant, Tenant commonTenant, Contract contractToBeRemoved) {
		Epg epgToUpdate = null;
		L3out l3outToUpdate = null;
		for (Epg epg : currentTenant.getEpgs()) {
			if (epg.getName().equals(contractToBeRemoved.getProviderId()) ){
				epgToUpdate = epg;
				break;
			}
		}
		epgToUpdate.getProvided_contracts().remove(contractToBeRemoved.getName());
		if (contractToBeRemoved.getConsumerType().equals(ACISizerConstant._epg)) {
			for (Epg epg : currentTenant.getEpgs()) {
				if (epg.getName().equals(contractToBeRemoved.getConsumerId()) ){
					epgToUpdate = epg;
					break;
				}
			}
			epgToUpdate.getConsumed_contracts().remove(contractToBeRemoved.getName());
		} else {
			for (L3out l3out : currentTenant.getL3outs()) {
				if (l3out.getName().equals(contractToBeRemoved.getConsumerId()) ){
					l3outToUpdate = l3out;
					break;
				}

			}
			if (l3outToUpdate != null) {
				l3outToUpdate.getConsumed_contracts().remove(contractToBeRemoved.getName());
			} else {
				for (L3out l3out : commonTenant.getL3outs()) {
					if (l3out.getName().equals(contractToBeRemoved.getConsumerId())) {
						l3outToUpdate = l3out;
						break;
					}

				}
				if (null != l3outToUpdate) {
					l3outToUpdate.getConsumed_contracts().remove(currentTenant.getName()
							+ ACISizerConstant.TENANT_FIELD_SEPERATOR + contractToBeRemoved.getName());
				}
			}
		}

		if (contractToBeRemoved.getConsumerType().equals(ACISizerConstant._shared_resource)) {
			
				removeContractFromSharedResource(commonTenant, currentTenant,contractToBeRemoved);
			
		}
	}

	private boolean removeContractFromSharedResource(Tenant commonTenant, Tenant currentTenant, Contract contractToBeRemoved) {
		boolean bSuccess = false;
		if (null != commonTenant.getSharedResources()) {
			for (SharedResource iter : commonTenant.getSharedResources()) {
				if (iter.getName().equals(contractToBeRemoved.getConsumerId()) ){
					bSuccess = true;
					if(null != iter.getConsumed_contracts())
					{
						iter.getConsumed_contracts()					
							.remove(Utility.getConsumedContractName(currentTenant.getName(), contractToBeRemoved.getName()));
					}
					break;
				}
			}
		}
		return bSuccess;
	}

	public com.cisco.applicationprofiler.ui.models.ContractUi getContract(int projectId, int tenantId, int appId, int contractId)
			throws AciEntityNotFound {
		Contract retContract = null;

		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID );
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		for (Contract contract : currTenant.getContracts()) {

			if (contract.getId() == contractId) {
				retContract = contract;
				break;
			}

		}
		if (null == retContract) {
			throw new AciEntityNotFound(CONTRACT_NOT_FOUND_WITH_ID);
		}
		com.cisco.applicationprofiler.ui.models.ContractUi contractReqd = new com.cisco.applicationprofiler.ui.models.ContractUi();
		outputMapping(retContract, contractReqd, currTenant, Utility.getCommonTenant(logicalRequirement));
		return contractReqd;
	}

	
	public List<com.cisco.applicationprofiler.ui.models.ContractUi> getContracts(int projectId, int tenantId, int appId)
			throws AciEntityNotFound {
		return getContractsEx(projectId,tenantId,appId,-1);
	}

	private String validateAndUpdateProvidersConsumers(Tenant tenant, Contract srcContract, Tenant commonTenant) {

		if (false == srcContract.getProviderType().equals(ACISizerConstant._epg)) {
			return "Incorrect provider " + srcContract.getProviderType();
		}

		if (false == srcContract.getConsumerType().equals(ACISizerConstant._epg)) {
			if (false == srcContract.getConsumerType().equals(ACISizerConstant._l3out))
				if (false == srcContract.getConsumerType().equals(ACISizerConstant._shared_resource))
					return "Incorrect consumer " + srcContract.getConsumerType();
		}

		boolean bFoundProvider = false;
		if (srcContract.getProviderType().equals(ACISizerConstant._epg)) {
			for (Epg iter : tenant.getEpgs()) {
				if (iter.getName().equals(srcContract.getProviderId()) ){
					if (iter.getProvided_contracts() == null) {
						iter.setProvided_contracts(new HashSet<String>());
					}
					iter.getProvided_contracts().add(srcContract.getName());
					bFoundProvider = true;
					break;
				}
			}
			if (false == bFoundProvider) {
				return "Provider id '" + srcContract.getProviderId() + "' not found, for provider type '"
						+ srcContract.getProviderType() + "'";
			}

		}

		if (srcContract.getConsumerType().equals(ACISizerConstant._epg)) {
			return validateAndUpdateEpg(srcContract, tenant, commonTenant);
		}

		if (srcContract.getConsumerType().equals(ACISizerConstant._l3out)) {
			return validateAndUpdateL3out(srcContract, tenant, commonTenant);

		}
		if (srcContract.getConsumerType().equals(ACISizerConstant._shared_resource)) {
			return validateAndUpdateSharedResource(srcContract, tenant, commonTenant);

		}

		return null;
	}

	private String validateAndUpdateEpg(Contract srcContract, Tenant tenant, Tenant commonTenant) {

		boolean bFound = false;
		for (Epg iter : tenant.getEpgs()) {
			if (iter.getName().equals( srcContract.getConsumerId()) ){
				if (iter.getConsumed_contracts() == null) {
					iter.setConsumed_contracts(new HashSet<String>());
				}
				iter.getConsumed_contracts().add(srcContract.getName());
				bFound = true;
				break;
			}
		}

		if (false == bFound) {
			return "Consumer id '" + srcContract.getConsumerId() + "' not found, for consumer type '"
					+ srcContract.getConsumerType() + "'";
		}

		return null;
	}

	private String validateAndUpdateSharedResource(Contract srcContract, Tenant tenant, Tenant commonTenant) {
		boolean bFound = false;
		for (SharedResource iter : tenant.getSharedResources()) {
			if (iter.getName().equals(srcContract.getConsumerId()) ){
				if (iter.getConsumed_contracts() == null) {
					iter.setConsumed_contracts(new HashSet<String>());
				}
				iter.getConsumed_contracts()
						.add(Utility.getConsumedContractName(tenant.getName(), srcContract.getName()));
				bFound = true;
				break;
			}
		}

		if (false == bFound) {
			for (SharedResource iter : commonTenant.getSharedResources()) {
				if (iter.getName().equals( srcContract.getConsumerId()) ){
					if (null == iter.getConsumed_contracts()) {
						iter.setConsumed_contracts(new HashSet<String>());
					}
					iter.getConsumed_contracts()
							.add(Utility.getConsumedContractName(tenant.getName(), srcContract.getName()));
				}
				bFound = true;
				break;
			}
		}
		if (false == bFound) {
			return "Consumer id '" + srcContract.getConsumerId() + "' not found, for consumer type '"
					+ srcContract.getConsumerType() + "'";
		}
		return null;

	}

	private String validateAndUpdateL3out(Contract srcContract, Tenant tenant, Tenant commonTenant) {
		boolean bFound = false;
		for (L3out iter : tenant.getL3outs()) {
			if (iter.getName().equals(srcContract.getConsumerId()) ){
				if (iter.getConsumed_contracts() == null) {
					HashSet<String> consumedContracts = new HashSet<String>();
					consumedContracts.add(srcContract.getName());
					iter.setConsumed_contracts(consumedContracts);
				} else {
					iter.getConsumed_contracts()
							.add(srcContract.getName());
				}
				bFound = true;
				break;
			}
		}

		if (false == bFound) {
			for (L3out l3out : commonTenant.getL3outs()) {
				if (l3out.getName().equals(srcContract.getConsumerId()) ){
					if (null == l3out.getConsumed_contracts()) {
						HashSet<String> consumedContracts = new HashSet<String>();
						consumedContracts.add(Utility.getConsumedContractName(tenant.getName(), srcContract.getName()));
						l3out.setConsumed_contracts(consumedContracts);
					} else {
						l3out.getConsumed_contracts()
								.add(Utility.getConsumedContractName(tenant.getName(), srcContract.getName()));
					}
					bFound = true;
					break;
				}
			}
		}
		if (false == bFound) {
			return "Consumer id '" + srcContract.getConsumerId() + "' not found, for consumer type '"
					+ srcContract.getConsumerType() + "'";
		}
		return null;
	}

	public List<ContractUi> getContractsEx(int projectId, int tenantId,
			int appId, int epgId) throws AciEntityNotFound{
		List<ContractUi> retContracts = new ArrayList<ContractUi>();

		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}
		boolean bFoundTenant = false;

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (tenantId == tenant.getId()) {
				bFoundTenant = true;
				String appName = null;
				for (Application iter : tenant.getApps()) {
					if (appId == iter.getId()) {
						appName = iter.getName();
						break;
					}
				}

				if (null == appName)
					throw new AciEntityNotFound("Not found app ");

				for (Contract iter : tenant.getContracts()) {
					if (null != iter.getAppName()) {
						if(0 == appName.compareToIgnoreCase(iter.getAppName()))
						{
							ContractUi reqdContract = new ContractUi();
							if ( epgId > 0)//get contracts for the associated epg 
							{
								if(epgId == Integer.parseInt(iter.getProviderId()))
								{
									outputMapping(iter, reqdContract, tenant, Utility.getCommonTenant(logicalRequirement));
									retContracts.add(reqdContract);
								}
							}
							else//get contracts for the application
							{
								outputMapping(iter, reqdContract, tenant, Utility.getCommonTenant(logicalRequirement));
								retContracts.add(reqdContract);
							}
						}
					}
				}
				break;
			}

		}

		if (false == bFoundTenant) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}

		return retContracts;
	}
}
