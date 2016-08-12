package com.cisco.applicationprofiler.logical.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.ProjectTable;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.BdNotFoundException;
import com.cisco.applicationprofiler.exceptions.DeletionNotAllowedException;
import com.cisco.applicationprofiler.exceptions.EpgNameExistsException;
import com.cisco.applicationprofiler.exceptions.GenericCouldNotSaveException;
import com.cisco.applicationprofiler.exceptions.GenericInvalidDataException;
import com.cisco.applicationprofiler.helper.TenantHelper;
import com.cisco.applicationprofiler.models.Application;
import com.cisco.applicationprofiler.models.Bd;
import com.cisco.applicationprofiler.models.Contract;
import com.cisco.applicationprofiler.models.Epg;
import com.cisco.applicationprofiler.models.Eps;
import com.cisco.applicationprofiler.models.LogicalRequirement;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.SharedResource;
import com.cisco.applicationprofiler.models.Subnets;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.repo.ProjectsRepository;
import com.cisco.applicationprofiler.ui.models.EpgUi;
import com.cisco.applicationprofiler.util.ACISizerConstant;
import com.cisco.applicationprofiler.util.AppUtilities;
import com.cisco.applicationprofiler.util.Utility;
import com.google.gson.Gson;

@Service
public class EpgServices {
	public static final String COULD_NOT_SAVE_EPG_DATA = "Could not save epg data";
	@Inject
	private ProjectsRepository projectRepo;
	
	
	@Inject
	private ProjectServicesAci projectServices;

	@Inject
	private Gson gson;

	public void inputMapping(com.cisco.applicationprofiler.ui.models.EpgUi src, Epg dest) {
		mapCommonFields(src, dest);
		dest.setBd("" + src.getBdId());
	}

	public com.cisco.applicationprofiler.ui.models.EpgUi outputMapping(Epg src, com.cisco.applicationprofiler.ui.models.EpgUi dest, Tenant currTenant, Tenant commonTenant) {
		
		String[] arr = src.getBd().split(ACISizerConstant.TENANT_FIELD_SEPERATOR);
		if(arr.length > 1)
			dest.setBdId(Integer.parseInt(arr[1]));
		else
			dest.setBdId(Integer.parseInt(src.getBd()));
		
		dest.setBdName(getFullNameForBd(src, currTenant, commonTenant));
		if(src.getSubnets()!=null){
		dest.setEpgSubnets(src.getSubnets().getIpv4());}
		dest.setFullName(getFullNameForEpg(src, currTenant));
		dest.setId(Integer.parseInt(src.getName()));
		dest.setName(src.getDisplayName());
		dest.setNoOfEndPoints(src.getEps().getMac());
		dest.setNoOfsharedServiceFilter(src.getFilterCount());
		dest.setPorts(src.getPorts());
		dest.setSharedServiceId(src.getSharedResourceId());
		dest.setSharedServiceName(getFullNameForSharedResource(src, currTenant, commonTenant));
		dest.setSharedServicesEnabled(src.isEnableSharedReource());
		dest.setSpan(src.getSpan());
		dest.setUiData(src.getUiData());
		dest.setAppId(Integer.parseInt(src.getApp()));
		return dest;
	}

	private String getFullNameForSharedResource(Epg src, Tenant currTenant, Tenant commonTenant) {
	/*	Optional<SharedResource> sharedResourceOptional = currTenant.getSharedResources().stream()
				.filter(sharedResource -> sharedResource.getId() == src.getSharedResourceId()).findFirst();*/
		for (SharedResource sharedResource : currTenant.getSharedResources()) {
			if(sharedResource.getId()==src.getSharedResourceId()){
				return currTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR
						+ sharedResource.getDisplayName();
			}
		}
		
	/*	if (sharedResourceOptional.isPresent()) {
			return currTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR
					+ sharedResourceOptional.get().getDisplayName();
		}*/
		/*sharedResourceOptional = commonTenant.getSharedResources().stream()
				.filter(sharedResource -> sharedResource.getId() == src.getSharedResourceId()).findFirst();
		if (sharedResourceOptional.isPresent()) {
			return commonTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR
					+ sharedResourceOptional.get().getDisplayName();
		}*/
		
		for (SharedResource sharedResource : commonTenant.getSharedResources()) {
			if(sharedResource.getId() == src.getSharedResourceId()){
				return commonTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR
						+ sharedResource.getDisplayName();
			}
		}
		return null;

	}

	private String getFullNameForEpg(Epg epg, Tenant tenant) {
		return tenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + epg.getDisplayName();
	}

	private String getFullNameForBd(Epg src, Tenant currTenant, Tenant commonTenant) {
		String[] bdNames = src.getBd().split(ACISizerConstant.TENANT_FIELD_SEPERATOR);
		//Optional<Bd> bdOptional;
		if (bdNames.length == 2) {
			/*bdOptional = commonTenant.getBds().parallelStream().filter(bd -> bd.getName().equals(bdNames[1]))
					.findFirst();
			if (bdOptional.isPresent()) {
				return getFullNameForBd(commonTenant, bdOptional.get());
			}*/
			
			for (Bd bd : commonTenant.getBds()) {
				if(bd.getName().equals(bdNames[1])){
					return getFullNameForBd(commonTenant, bd);
				}
			}
			
		}
		/*bdOptional = currTenant.getBds().parallelStream().filter(bd -> bd.getName().equals(bdNames[0])).findFirst();
		if (bdOptional.isPresent()) {
			return getFullNameForBd(currTenant, bdOptional.get());
		}*/
		
		for (Bd bd : currTenant.getBds()) {
			if(bd.getName().equals(bdNames[0])){
				return getFullNameForBd(currTenant, bd);
			}
		}
		
		return null;
	}

	/**
	 * @param commonTenant
	 * @param bdOptional
	 * @return
	 */
	private String getFullNameForBd(Tenant commonTenant, Bd bdOptional) {
		return commonTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + bdOptional.getDisplayName();
	}

	public com.cisco.applicationprofiler.ui.models.EpgUi addEpg(int projectId, int tenantId, int appId,
			com.cisco.applicationprofiler.ui.models.EpgUi epg) throws AciEntityNotFound, GenericInvalidDataException {

		ProjectTable proj = projectRepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);
		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);

		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		Application currApp = AppUtilities.getCurrentApp(appId, currTenant);
		if (currApp == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID);
		}

		Epg epgToAdd = new Epg(logicalSummary.generateUniqueId(), epg.getName(), currApp.getName());
		inputMapping(epg, epgToAdd);

		

		if (true == epgToAdd.isEnableSharedReource() && epgToAdd.getFilterCount() <= 0) {
			throw new GenericInvalidDataException("For shared resource filter must be > 0");
		}

		// epgToAdd.setBd(bdName);
		Epg addedEpg = addEpg(epgToAdd, currTenant,commonTenant, logicalSummary, currApp);
		//createContract(currTenant, commonTenant, epgToAdd, logicalSummary);
		/*	proj.setLogicalRequirement(gson.toJson(logicalRequirement));

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));
*/
		// set it back to the project and update db
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			com.cisco.applicationprofiler.ui.models.EpgUi epgToReturn = new com.cisco.applicationprofiler.ui.models.EpgUi();
			outputMapping(addedEpg, epgToReturn, currTenant, commonTenant);
			return epgToReturn;
		} else {
			throw new GenericCouldNotSaveException(COULD_NOT_SAVE_EPG_DATA);
		}

	}

	private boolean isBdExistsInCommonTenant(Tenant tenant, String bdName) {
		if (null != tenant.getBds()) {
			/*return commonTenant.getBds().stream().filter(bd -> bd.getName().equals(bdName)).findFirst().isPresent();*/
			for (Bd bd : tenant.getBds()) {
				if(bd.getName().equals(bdName)){
					return true;
				}
			}
		}
		return false;
	}

/*	private void createContract(Tenant currTenant, Tenant commonTenant, Epg addedEpg, LogicalSummary logicalSummary) {
		if (addedEpg.isEnableSharedReource()) {
			Contract contract = new Contract(logicalSummary.generateUniqueId(), "CSR" + addedEpg.getName(),
					addedEpg.getApp());
			// contract.setId(logicalSummary.generateUniqueId());
			contract.setProviderId(addedEpg.getName());
			contract.setConsumerId(addedEpg.getSharedResourceId());
			contract.setProviderType(ACISizerConstant._epg);
			contract.setConsumerType(ACISizerConstant._shared_resource);
			contract.setUnique_filters(addedEpg.getFilterCount());
			contractServices.addContract(contract, commonTenant, currTenant);
		}
	}*/

	/**
	 * @param epg
	 * @param currTenant
	 * @param commonTenant
	 * @param currApp
	 * @return
	 */
	public Epg addEpg(Epg epgToAdd, Tenant currTenant, Tenant commonTenant, LogicalSummary logicalSummary,
			Application currApp) {

		if (!isBdExistsInCommonTenant(currTenant, epgToAdd.getBd())) {
			if (!isBdExistsInCommonTenant(commonTenant, epgToAdd.getBd())) {
				throw new BdNotFoundException("Bd name not found " + epgToAdd.getBd());
			}
			// ******** identify if this is required, if so then set fully
			// qualified name bd for both, in common and user tenant
			epgToAdd.setBd(commonTenant.getName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + epgToAdd.getBd());
		}

		addEpg(epgToAdd, currTenant);

		plusCount(currApp, epgToAdd);

		return epgToAdd;
	}

	/**
	 * @param currApp
	 * @param epgToAdd 
	 */
	public void plusCount(Application currApp, Epg epgToAdd) {
		currApp.setTotalEpgCount(currApp.getTotalEpgCount()+1);
		currApp.setTotalEpsCount(currApp.getTotalEpsCount()+epgToAdd.getEps().getMac());
		if(epgToAdd.isEnableSharedReource())
		{
			currApp.setTotalContractCount(currApp.getTotalContractCount()+1);
			currApp.setTotalFilterCount(currApp.getTotalFilterCount()+epgToAdd.getFilterCount());
		}
	}

	public void addEpg(Epg epgToAdd, Tenant currTenant) {

		if (null == epgToAdd.getUiData()) {
			epgToAdd.setUiDataNull();
		}
		//if (currTenant.getEpgs() == null) {
		//	currTenant.setEpgs(new LinkedHashSet<Epg>());
		//	currTenant.getEpgs().add(epgToAdd);
		//}

		else if (!currTenant.getEpgs().add(epgToAdd)) {
			throw new EpgNameExistsException(ACISizerConstant.EPG_NAME_EXISTS + epgToAdd.getDisplayName());
		}
		//return epgToAdd;
	}

	public com.cisco.applicationprofiler.ui.models.EpgUi updateEpg(int projectId, int tenantId, int appId, int epgId,
			com.cisco.applicationprofiler.ui.models.EpgUi epg) throws AciEntityNotFound {
		ProjectTable proj = projectRepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID );
		}
		Application currApp = AppUtilities.getCurrentApp(appId, currTenant);
		if (currApp == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID);
		}

		Epg currEpg = null;
		for (Epg epgIter : currTenant.getEpgs()) {
			if (epgId == epgIter.getId()) {
				currEpg = epgIter;
				break;
			}
		}
		if (currEpg == null || !(currEpg.getApp().equals(currApp.getName()))) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_EPG_FOR_ID);
		}
		currTenant.getEpgs().remove(currEpg);
		Epg epgToUpdate = new Epg(epgId, epg.getName(), currApp.getName());
		epgToUpdate.copyEpg(currEpg);
		//updateEpg(epg, epgToUpdate);
		mapCommonFields(epg, epgToUpdate);
		if (!currTenant.getEpgs().add(epgToUpdate)) {
			throw new EpgNameExistsException(ACISizerConstant.EPG_NAME_EXISTS + epg.getName());
		}
		
		plusCount(currApp, epgToUpdate);
		minusCount(currEpg, currApp);

		// update the summary also
		// convert it to java obj
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);

		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);
		// the contract between epg and shared resource
		//createContract(currTenant, commonTenant, epgToUpdate, logicalSummary);

		/*String updatedEpg = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(updatedEpg);

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));*/

		// set it back to the project and update db
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			com.cisco.applicationprofiler.ui.models.EpgUi epgUpdated = new com.cisco.applicationprofiler.ui.models.EpgUi();
			outputMapping(epgToUpdate, epgUpdated, currTenant, commonTenant);
			return epgUpdated;
		} else {
			throw new GenericCouldNotSaveException(COULD_NOT_SAVE_EPG_DATA);
		}

	}

	public int deleteEpg(int projectId, int tenantId, int appId, int epgId)
			throws DeletionNotAllowedException, AciEntityNotFound {
		Tenant currentTenant = null;
		Application currentApp = null;
		Epg epgToDelete = null;

		ProjectTable proj = projectRepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID );
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (tenantId == tenant.getId()) {
				currentTenant = tenant;
				break;
			}
		}
		
		if (null == currentTenant) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
			TenantHelper.modifiedTime(currentTenant);
		for (Application app : currentTenant.getApps()) {
			if (appId == app.getId()) {
				currentApp = app;
				break;
			}
		}

		if (null == currentApp) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID);
		}

		for (Epg epg : currentTenant.getEpgs()) {
			if (epgId == epg.getId()) {
				epgToDelete = epg;
				break;
			}
		}

		if (null == epgToDelete || !(epgToDelete.getApp().equals(currentApp.getName()))) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_EPG_FOR_ID );
		}

		if (null != epgToDelete.getConsumed_contracts()) {
			if (!epgToDelete.getConsumed_contracts().isEmpty()) {
				throw new DeletionNotAllowedException(ACISizerConstant.PLEASE_DELETE_THE_CONTRACTS_ASSOCIATED_FIRST);
			}
		}

		if (null != epgToDelete.getProvided_contracts()) {
			if (!epgToDelete.getProvided_contracts().isEmpty()) {
				throw new DeletionNotAllowedException(ACISizerConstant.PLEASE_DELETE_THE_CONTRACTS_ASSOCIATED_FIRST);
			}
		}
		List<Contract> removeContract = new ArrayList<Contract>();
		/*if (epgToDelete.isEnableSharedReource()) {
			if (null != currentTenant.getContracts()) {
				for (Contract iter : currentTenant.getContracts()) {
					if (iter.getConsumerId() == epgToDelete.getSharedResourceId()) {
						removeContract.add(iter);
					}
				}
			}
		}*/
		if (null != currentTenant.getContracts()) {
			currentTenant.getContracts().remove(removeContract);
		}
		
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		
		if (null != currentTenant.getEpgs()) {
			deleteEpg(currentTenant, epgToDelete, currentApp, logicalSummary);
		}

		// convert back to json string
		/*String deletedEpg = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(deletedEpg);

		// update the summary also
		// convert it to java obj
		
		
		
		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));*/

		// set it back to the project and update db
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			return 1;
		} else { 
			throw new GenericCouldNotSaveException(COULD_NOT_SAVE_EPG_DATA);
		}
	}

	public void deleteEpg(Tenant currentTenant, Epg epgToDelete,Application currentApp,LogicalSummary logicalSummary) {
		currentTenant.getEpgs().remove(epgToDelete);
		minusCount(epgToDelete, currentApp);
	}

	/**
	 * @param epgToDelete
	 * @param currentApp
	 */
	public void minusCount(Epg epgToDelete, Application currentApp) {
		currentApp.setTotalEpgCount(currentApp.getTotalEpgCount()-1);
		currentApp.setTotalEpsCount(currentApp.getTotalEpsCount()-epgToDelete.getEps().getMac());
		if(epgToDelete.isEnableSharedReource())
		{
			currentApp.setTotalContractCount(currentApp.getTotalContractCount()-1);
			currentApp.setTotalFilterCount(currentApp.getTotalFilterCount() - epgToDelete.getFilterCount());
		}
	}

	public com.cisco.applicationprofiler.ui.models.EpgUi getEpg(int projectId, int tenantId, int appId, int epgId)
			throws AciEntityNotFound {
		ProjectTable proj = projectRepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);

		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID );
		}
		Application currApp = AppUtilities.getCurrentApp(appId, currTenant);
		if (currApp == null) {
			throw new EntityNotFoundException(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID );
		}
		
		/*Optional<Epg> epgOptional = currTenant.getEpgs().stream().filter(epgIter -> epgIter.getId() == epgId)
				.findFirst();
		*/
		Epg currEpg=null;
		for (Epg epg : currTenant.getEpgs()) {
			if(epg.getId() == epgId){
				currEpg=epg;
				break;
			}
		}

		if (currEpg==null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_EPG_FOR_ID);
		}
		com.cisco.applicationprofiler.ui.models.EpgUi epgToReturn = new com.cisco.applicationprofiler.ui.models.EpgUi();
		outputMapping(currEpg, epgToReturn, currTenant, commonTenant);

		return epgToReturn;

	}

	public List<com.cisco.applicationprofiler.ui.models.EpgUi> getEpgs(int projectId, int tenantId, int appId) throws AciEntityNotFound {
		java.util.List<EpgUi> epgList ;

		ProjectTable proj = projectRepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);

		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		Application currApp = AppUtilities.getCurrentApp(appId, currTenant);
		if (currApp == null) {
			throw new EntityNotFoundException(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID);
		}
		/*epgList=currTenant.getEpgs().parallelStream().filter(epg->epg.getApp().equals(currApp.getName())).collect(Collectors.toList());*/
		
		epgList=new ArrayList<>();
		for (Epg epg : currTenant.getEpgs()) {
			if(epg.getApp().equals(currApp.getName())){
				epgList.add(outputMapping(epg, new EpgUi(), currTenant, commonTenant));
			}
		}
		
		//return epgList.parallelStream().map(epg -> outputMapping(epg, new com.cisco.acisizer.ui.models.EpgUi(), currTenant, commonTenant)).collect(Collectors.toList());
		return epgList;
	}
	
	/**
	 * @param src
	 * @param dest
	 */
	private void mapCommonFields(com.cisco.applicationprofiler.ui.models.EpgUi src, Epg dest) {
		dest.setUiData(src.getUiData());
		dest.setPorts(src.getPorts());
		
		Subnets subnets = new Subnets();
		subnets.setIpv4(src.getEpgSubnets());
		dest.setSubnets(subnets);
		
		Eps eps = new Eps();
		eps.setMac(src.getNoOfEndPoints());
		dest.setEps(eps);
		
		dest.setSpan(src.getSpan());
		
		dest.setEnableSharedReource(src.isSharedServicesEnabled());
		dest.setFilterCount(src.getNoOfsharedServiceFilter());
		dest.setSharedResourceId(src.getSharedServiceId());

	}

}
