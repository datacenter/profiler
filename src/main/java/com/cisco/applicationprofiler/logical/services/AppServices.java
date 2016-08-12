package com.cisco.applicationprofiler.logical.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.ProjectTable;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.ApplicationNameExistsException;
import com.cisco.applicationprofiler.exceptions.ForbiddenOperationException;
import com.cisco.applicationprofiler.exceptions.GenericCouldNotSaveException;
import com.cisco.applicationprofiler.exceptions.GenericInvalidDataException;
import com.cisco.applicationprofiler.helper.BdHelper;
import com.cisco.applicationprofiler.helper.TenantHelper;
import com.cisco.applicationprofiler.models.Application;
import com.cisco.applicationprofiler.models.Contract;
import com.cisco.applicationprofiler.models.Epg;
import com.cisco.applicationprofiler.models.LogicalRequirement;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.repo.ProjectsRepository;
import com.cisco.applicationprofiler.services.applicationTemplates.ApplicationTemplateInterface;
import com.cisco.applicationprofiler.ui.models.ApplicationTemplate;
import com.cisco.applicationprofiler.ui.models.ApplicationUi;
import com.cisco.applicationprofiler.ui.models.ContractUi;
import com.cisco.applicationprofiler.ui.models.EpgUi;
import com.cisco.applicationprofiler.util.ACISizerConstant;
import com.cisco.applicationprofiler.util.AppUtilities;
import com.cisco.applicationprofiler.util.Utility;
import com.cisco.applicationprofiler.view.ViewAppFabricNodes;
import com.cisco.applicationprofiler.view.ViewNodes;
import com.google.gson.Gson;

@Service
public class AppServices {

	public static final String CANNOT_CREATE_APPLICATION_UNDER_TENANT = "Cannot create application under tenant : ";

	@Inject
	private ProjectsRepository m_projrepo;

	@Inject
	private Gson gson;

	@Inject
	private EpgServices epgServices;

	@Inject
	private ContractServicesAcii contractServices;
	
	@Inject
	private ApplicationTemplateInterface applicationTemplateInterface;
	
	@Inject
	private ProjectServicesAci projectServices;
	

	public Application inputMapping(int appId, com.cisco.applicationprofiler.ui.models.ApplicationUi app) {
		Application appDest = new Application(appId, app.getName());
		appDest.setInstances(app.getNoOfInstances());
		appDest.setUiData(app.getUiData());
		return appDest;
	}

	public com.cisco.applicationprofiler.ui.models.ApplicationUi outputMapping(Application app, Tenant tenant) {
		com.cisco.applicationprofiler.ui.models.ApplicationUi appDest = new com.cisco.applicationprofiler.ui.models.ApplicationUi();
		appDest.setFullName(tenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + app.getDisplayName());
		appDest.setId(Integer.parseInt(app.getName()));
		appDest.setName(app.getDisplayName());
		appDest.setNoOfInstances(app.getInstances());
		appDest.setUiData(app.getUiData());
		return appDest;
	}

	public com.cisco.applicationprofiler.ui.models.ApplicationUi addApp(int projectId, int tenantId,
			com.cisco.applicationprofiler.ui.models.ApplicationUi app) throws AciEntityNotFound, ForbiddenOperationException {
		if(tenantId == ACISizerConstant.DEFAULT_TENANT_ID)
		{
			throw new ForbiddenOperationException(CANNOT_CREATE_APPLICATION_UNDER_TENANT+ACISizerConstant.TENANT_NAME_COMMON);
		}
		
		ProjectTable proj = m_projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		Application appToAdd = inputMapping(logicalSummary.generateUniqueId(), app);
		if (appToAdd.getInstances() <= 0)
			appToAdd.setInstances(1);

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID );
		}

		if (!currTenant.getApps().add(appToAdd)) {
			throw new ApplicationNameExistsException("Application name " + appToAdd.getName() + " already exists");
		}
		/*String addedApp = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(addedApp);*/

		// update the summary also
		// convert it to java obj
		int count = logicalSummary.getAppCount();
		logicalSummary.setAppCount(count + 1);

		// convert back to json string
		/*proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));
		
		proj.setLastUpdatedTime(new Timestamp(new Date().getTime()));
*/
		// set it back to the project and update db
		
		
		ProjectTable returnProj =projectServices.saveProject(proj, logicalRequirement, logicalSummary);

		if (returnProj != null) {
			return outputMapping(appToAdd, currTenant);
		} else {
			throw new GenericCouldNotSaveException("Could not save application data");
		}

	}

	public com.cisco.applicationprofiler.ui.models.ApplicationUi updateApp(int projectId, int tenantId, int appId,
			com.cisco.applicationprofiler.ui.models.ApplicationUi app) throws AciEntityNotFound {
		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		/*Optional<Tenant> currTenants = logicalRequirement.getTenants().stream()
				.filter(tenant -> (tenant.getId() == tenantId)).findFirst();*/
		
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);
		if (currTenant==null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}

		/*if (currTenant.getApps().stream()
				.filter(appIter -> (appIter.getId() != appId && appIter.getDisplayName().equals(app.getName())))
				.findFirst().isPresent()) {
			throw new ApplicationNameExistsException("Application name " + app.getName() + " already exists");
		}*/
		
		if(isDuplicate(currTenant, appId, app)){
			throw new ApplicationNameExistsException("Application name " + app.getName() + " already exists");
		}
		
		/*Optional<Application> currApps = currTenant.getApps().stream().filter(appIter -> (appIter.getId() == appId))
				.findFirst();*/
		Application currApp = null;
		currApp = getCurrentApp(appId, currTenant);
		
		if (currApp==null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID);
		}
		currTenant.getApps().remove(currApp);
		Application appToUpdate = new Application(appId, app.getName());
		appToUpdate.copyApplication(currApp);
		updateApplication(app, appToUpdate);

		currTenant.getApps().add(appToUpdate);
		
		BdHelper.updateUniqueBdInstance(currApp,appToUpdate,currTenant);
		
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);

		if (returnProj != null) {
			//projectServices.callSizingSummary(projectId);
			return outputMapping(appToUpdate, currTenant);
		} else {
			throw new GenericCouldNotSaveException("Could not save application data");
		}

	}

	

	/**
	 * @param appId
	 * @param currTenant
	 * @return
	 */
	private Application getCurrentApp(int appId, Tenant currTenant) {
		for (Application appIter : currTenant.getApps()) {
			if(appIter.getId()==appId){
				return appIter;
			}
			
		}
		return null;
	}

	private boolean isDuplicate(Tenant tenant,int appId,ApplicationUi app){
		for (Application appIter : tenant.getApps()) {
			if(appIter.getId()!=appId && appIter.getDisplayName().equals(app.getName())){
				return true;
			}
		}
		return false;
	}
	
	public int deleteApp(int projectId, int tenantId, int appId) throws AciEntityNotFound {
		Tenant currentTenant;
		Tenant commonTenant;

		ProjectTable proj = m_projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		currentTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currentTenant);
		Application appToDelete=getCurrentApp(appId, currentTenant);

		if (appToDelete == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID);
		}
		
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		
		commonTenant = Utility.getCommonTenant(logicalRequirement);
		deleteContractsFromApp(appToDelete, currentTenant, commonTenant, logicalSummary);
		deleteEpgsFromApp(currentTenant, appToDelete,logicalSummary);
		currentTenant.getApps().remove(appToDelete);
		// convert back to json string
		/*String deletedApp = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(deletedApp);*/

		// update the summary also
		// convert it to java obj
		
		int count = logicalSummary.getAppCount();
		logicalSummary.setAppCount(count - 1);

		// convert back to json string
		/*proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));*/

		// set it back to the project and update db
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);

		if (returnProj != null) {
			//projectServices.callSizingSummary(projectId);
			return 1;
		} else {
			throw new GenericCouldNotSaveException("Could not save application data");
		}
	}

	private void deleteEpgsFromApp(Tenant currentTenant, Application appToDelete, LogicalSummary logicalSummary) {
		ArrayList<Epg> epgs = new ArrayList<>();
		if (currentTenant.getEpgs() != null) {
			for (Epg epg : currentTenant.getEpgs()) {
				if (appToDelete.getName().equals(epg.getApp())) {
					epgs.add(epg);
				}

			}
			for (Epg epg : epgs) {
				epgServices.deleteEpg(currentTenant, epg, appToDelete, logicalSummary);
				//currentTenant.getEpgs().remove(epg);
			}
		}
	}

	private void deleteContractsFromApp(Application appToDelete, Tenant currentTenant, Tenant commonTenant,LogicalSummary logicalSummary)
			throws AciEntityNotFound {
		ArrayList<Integer> contractsToDelete = new ArrayList<>();
		if (currentTenant.getContracts() != null) {
			for (Contract contract : currentTenant.getContracts()) {
				if (appToDelete.getName().equals(contract.getAppName())) {
					contractsToDelete.add(contract.getId());
				}
			}

			for (Integer integer : contractsToDelete) {
				contractServices.deleteContract(integer, currentTenant, commonTenant, appToDelete, logicalSummary);
			}
		}

	}

	public com.cisco.applicationprofiler.ui.models.ApplicationUi getApp(int projectId, int tenantId, int appId)
			throws AciEntityNotFound {
		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}

		/*Optional<Application> appOptional = currTenant.getApps().stream().filter(app -> app.getId() == appId)
				.findFirst();*/
		Application currApp=getCurrentApp(appId, currTenant);
		if (currApp==null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID);
		}
		return outputMapping(currApp, currTenant);

	}

	public List<com.cisco.applicationprofiler.ui.models.ApplicationUi> getApps(int projectId, int tenantId)
			throws AciEntityNotFound {

		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant tenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		if (tenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		/*List<com.cisco.acisizer.ui.models.ApplicationUi> retApps = tenant.getApps().parallelStream().map(app -> {
			return outputMapping(app, tenant);
		}).collect(Collectors.toList());*/
		
		List<ApplicationUi> retApps = new ArrayList<>();
		for (Application application : tenant.getApps()) {
			retApps.add(outputMapping(application, tenant));
		}

		return retApps;
	}

	private boolean updateApplication(com.cisco.applicationprofiler.ui.models.ApplicationUi app, Application dest) {
		boolean bSuccess = false;
		dest.setInstances(app.getNoOfInstances());
		dest.setUiData(app.getUiData());
		bSuccess = true;

		return bSuccess;
	}

	public ViewNodes getNodeCollection(int projectId, int tenantId, int appId) throws AciEntityNotFound {

		ViewNodes nodes = new ViewNodes();
	
		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID );
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID );
		}
		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);

		Application currApp = AppUtilities.getCurrentApp(appId, currTenant);
		if (currApp == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_APP_FOR_ID );
		}
		int appInstance = currApp.getInstances();
		nodes.getNodes().add(outputMapping(currApp, currTenant));
		/*currTenant.getEpgs().stream().filter(epg -> epg.getApp().equals(currApp.getName()))
				.forEach(epg -> addEpgToNodes(nodes, currTenant, commonTenant, epg));*/
		for (Epg epg : currTenant.getEpgs()) {
			if(epg.getApp().equals(currApp.getName())){
				addEpgToNodes(nodes, currTenant, commonTenant, epg, appInstance);
			}
		}
		/*currTenant.getContracts().stream().filter(contract -> contract.getAppName().equals(currApp.getName()))
				.forEach(contract -> addContractToNodes(nodes, currTenant, commonTenant, contract));*/
		
		for (Contract contract : currTenant.getContracts()) {
			if(contract.getAppName().equals(currApp.getName())){
				addContractToNodes(nodes, currTenant, commonTenant, contract, appInstance);
			}
		}
		

		return nodes;
	}

	/**
	 * @param nodes
	 * @param currTenant
	 * @param commonTenant
	 * @param contract
	 * @param appInstance 
	 * @return
	 */
	private boolean addContractToNodes(ViewNodes nodes, Tenant currTenant, Tenant commonTenant, Contract contract, int appInstance) {
		if(true == contract.getConsumerType().equals(ACISizerConstant._shared_resource))//do not show the shared service contract in the application pane
			return false;
		
		ContractUi contractToAddInConnection = contractServices.outputMapping(contract,
				new ContractUi(appInstance), currTenant, commonTenant);
		if(contract.getConsumerType().equals(ACISizerConstant._epg))
			Utility.convertToViewConnectionType(Integer.parseInt(contract.getConsumerId()),contract.getId(),  nodes);

		Utility.convertToViewConnectionType( Integer.parseInt(contract.getProviderId()),contract.getId(), nodes);
		return nodes.getNodes().add(contractToAddInConnection);
	}

	/**
	 * @param nodes
	 * @param currTenant
	 * @param commonTenant
	 * @param epg
	 * @param appInstance 
	 * @param currApp
	 * @return
	 */
	private boolean addEpgToNodes(ViewNodes nodes, Tenant currTenant, Tenant commonTenant, Epg epg, int appInstance) {
		EpgUi epgToAddInConnection = epgServices.outputMapping(epg,
				new EpgUi(appInstance), currTenant, commonTenant);
		Utility.convertToViewConnectionType(Integer.parseInt(epg.getApp()),epgToAddInConnection.getId(), nodes);
		return nodes.getNodes().add(epgToAddInConnection);
	}

	/**
	 * @param nodes
	 * @param currTenant
	 * @param app
	 * @return
	 */
	public boolean addAppToNodes(ViewNodes nodes, Tenant currTenant, Application app) {
		return nodes.getNodes().add(outputMapping(app, currTenant));
	}

	
	public ViewAppFabricNodes getAppFabricNodeCollection(int projectId, int tenantId, int appId)
			throws AciEntityNotFound {
		ViewAppFabricNodes nodes = new ViewAppFabricNodes();
		nodes.setApp(getNodeCollection(projectId, tenantId, appId));
		
		return nodes;
	}

	public com.cisco.applicationprofiler.ui.models.ApplicationUi addAppTemplate(
			int projectId, int tenantId, ApplicationTemplate appTemplate) throws AciEntityNotFound,GenericInvalidDataException, ForbiddenOperationException {

		if(tenantId == ACISizerConstant.DEFAULT_TENANT_ID)
		{
			throw new ForbiddenOperationException(CANNOT_CREATE_APPLICATION_UNDER_TENANT+ACISizerConstant.TENANT_NAME_COMMON);
		}
		
		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		
		Tenant targetTenant = Utility.getCurrentTenant(tenantId,logicalRequirement);
		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);

		if (targetTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		
		Application app=applicationTemplateInterface.addApplication(appTemplate, targetTenant, commonTenant, logicalSummary);
		
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));
		proj.setLogicalRequirement(gson.toJson(logicalRequirement));

		if (m_projrepo.save(proj) != null) {
			//projectServices.callSizingSummary(projectId);
			return outputMapping(app, targetTenant);
		} 

		throw new GenericCouldNotSaveException("Could not save data for tenant");

	}
}
