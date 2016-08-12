/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.logical.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.apic.ProcessBuilderApic;
import com.cisco.applicationprofiler.apic.models.ApicUser;
import com.cisco.applicationprofiler.apic.models.ProfilerToApicMapper;
import com.cisco.applicationprofiler.domain.Device;
import com.cisco.applicationprofiler.domain.ProjectTable;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.DefaultNodeException;
import com.cisco.applicationprofiler.exceptions.GenericCouldNotSaveException;
import com.cisco.applicationprofiler.exceptions.GenericInvalidDataException;
import com.cisco.applicationprofiler.exceptions.TenantNameExistsException;
import com.cisco.applicationprofiler.helper.BdHelper;
import com.cisco.applicationprofiler.helper.L3outHelper;
import com.cisco.applicationprofiler.helper.TenantHelper;
import com.cisco.applicationprofiler.models.Application;
import com.cisco.applicationprofiler.models.Bd;
import com.cisco.applicationprofiler.models.Contract;
import com.cisco.applicationprofiler.models.Epg;
import com.cisco.applicationprofiler.models.L3out;
import com.cisco.applicationprofiler.models.LogicalRequirement;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.SharedResource;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.Vrf;
import com.cisco.applicationprofiler.repo.DeviceRepository;
import com.cisco.applicationprofiler.repo.ProjectsRepository;
import com.cisco.applicationprofiler.ui.models.AciSizerModelUi;
import com.cisco.applicationprofiler.ui.models.ApplicationUi;
import com.cisco.applicationprofiler.ui.models.BdUi;
import com.cisco.applicationprofiler.ui.models.ContractUi;
import com.cisco.applicationprofiler.ui.models.EpgUi;
import com.cisco.applicationprofiler.ui.models.L3outUi;
import com.cisco.applicationprofiler.ui.models.SharedResourceUi;
import com.cisco.applicationprofiler.ui.models.VrfUi;
import com.cisco.applicationprofiler.util.ACISizerConstant;
import com.cisco.applicationprofiler.util.AutoNameGenerator;
import com.cisco.applicationprofiler.util.Utility;
import com.cisco.applicationprofiler.view.ViewNode;
import com.cisco.applicationprofiler.view.ViewNodes;
import com.google.gson.Gson;

/**
 * @author Deepa
 *
 */
@Service
public class TenantServicesAcii {
	private static final String APIC_TENANT_URL = "/api/mo/uni.xml";

	private static final String APIC_LOGIN_URL = "/api/aaaLogin.xml";

	private static final String HTTPS = "https://";

	public static final String SNAPSHOT_NOT_FOUND_FOR_THE_TENANT_ID = "Snapshot not found for the tenant id ";

	private static final Logger LOGGER = LoggerFactory.getLogger(TenantServicesAcii.class);

	@Inject
	private ProjectsRepository projrepo;

	@Inject
	private Gson gson;

	@Inject
	private ContractServicesAcii contractServices;

	@Inject
	private VrfServices vrfServices;

	@Inject
	private BdServices bdServices;

	@Inject
	private L3OutServices l3OutServices;

	@Inject
	private SharedResourceServices sharedResourceServices;

	@Inject
	private AutoNameGenerator autoNameGenerator;

	@Inject
	private ProjectServicesAci projectServices;

	@Inject
	private ProfilerToApicMapper profilerToApicMapper;

	@Inject
	private DeviceRepository deviceRepository;
	
	@Inject
	private ProcessBuilderApic processBuilderApic;
	
	public Tenant addTenant(int projectId, Tenant newTenant) throws AciEntityNotFound, GenericInvalidDataException {
		LOGGER.debug("adding tenant to" + projectId);
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// update the summary also
		// convert it to java obj

		if (newTenant.getType().equals(ACISizerConstant.TENANT_TYPE_UTILITY)) {
			throw new GenericInvalidDataException("tenant type cannot be utility");
		}
		TenantHelper.modifiedTime(newTenant);

		Tenant tenantAdded = addTenant(newTenant, proj);
		proj.setLastUpdatedTime(new Timestamp(new Date().getTime()));
		// set it back to the project and update db

		ProjectTable returnProj = projrepo.save(proj);

		if (returnProj != null) {
			// projectServices.callSizingSummary(projectId);
			return tenantAdded;
		}

		throw new GenericCouldNotSaveException("Could not save tenant data");
	}

	/**
	 * @param newTenant
	 * @param proj
	 */
	public Tenant addTenant(Tenant newTenant, ProjectTable proj) {
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		/*
		 * int count = logicalSummary.getTenantCount();
		 * logicalSummary.setTenantCount(count + 1);
		 */

		Tenant tenantToAdd;

		if (newTenant.getDisplayName().equals(ACISizerConstant.TENANT_NAME_COMMON)) {
			tenantToAdd = new Tenant(ACISizerConstant.DEFAULT_TENANT_ID, newTenant.getDisplayName());
		} else {
			tenantToAdd = new Tenant(logicalSummary.generateUniqueId(proj.getId()), newTenant.getDisplayName());
		}
		tenantToAdd.copyTenant(newTenant);
		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		if (logicalRequirement.getTenants() == null) {
			List<Tenant> tennants = new ArrayList<>();
			tennants.add(tenantToAdd);
			logicalRequirement.setTenants(tennants);

		} else {
			addTenant(tenantToAdd, logicalRequirement);
		}

		proj.setLogicalRequirement(gson.toJson(logicalRequirement));

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));
		return tenantToAdd;
	}

	/**
	 * @param tenantToAdd
	 * @param logicalRequirement
	 * @return
	 */
	public Tenant addTenant(Tenant tenantToAdd, LogicalRequirement logicalRequirement) {
		if (isDuplicate(tenantToAdd, logicalRequirement.getTenants())) {
			throw new TenantNameExistsException(
					"Tenant name " + tenantToAdd.getDisplayName() + " already exists in the system");
		}

		if (null == tenantToAdd.getUiData()) {
			tenantToAdd.setUiDataNull();
		}

		logicalRequirement.getTenants().add(tenantToAdd);
		return tenantToAdd;
	}

	public Tenant getTenant(int projectId, int tenantId) throws AciEntityNotFound {

		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		return getTenant(tenantId, proj);

	}

	private Tenant getTenant(int tenantId, ProjectTable proj) throws AciEntityNotFound {
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		for (Tenant tenant : logicalRequirement.getTenants()) {

			// tenant = gson.fromJson(tenants.get(i), Tenant.class);
			if (tenant.getId() == tenantId) {
				setEntityCounts(tenant);
				return tenant;

			}

		}
		throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
	}

	public void setEntityCounts(Tenant tenant) {
		int epgCount = 0;
		int contractCount = 0;
		int filterCount = 0;
		int bdCount = 0;
		int endPointCount = 0;
		for (Application app : tenant.getApps()) {
			epgCount = epgCount + app.getTotalEpgCount() * app.getInstances();
			contractCount = contractCount + app.getTotalContractCount() * app.getInstances();
			filterCount = filterCount + app.getTotalFilterCount() * app.getInstances();
			endPointCount = endPointCount + app.getTotalEpsCount() * app.getInstances();
		}
		for (Bd bd : tenant.getBds()) {
			bdCount = bdCount + bd.getNoOfInstances();
		}
		tenant.setTotalEpgCount(epgCount);
		tenant.setTotalFilterCount(filterCount);
		tenant.setTotalContractCount(contractCount);
		tenant.setTotalBdCount(bdCount);
		tenant.setTotalEndPoints(endPointCount);
	}

	public List<Tenant> getTenants(int projectId) throws AciEntityNotFound {
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);

		for (Tenant tenant : logicalRequirement.getTenants()) {
			setEntityCounts(tenant);
		}
		// logicalRequirement.getTenants().parallelStream().forEach(TenantServices::generateFullyQualifiedNames);
		return logicalRequirement.getTenants();
	}

	public int deleteTenant(int projectId, int tenantId) throws AciEntityNotFound {

		if (tenantId == ACISizerConstant.DEFAULT_TENANT_ID) {
			throw new DefaultNodeException("Cannot delete default tenant, id=" + tenantId);
		}

		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}
		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);

		Tenant tenantToTemove = getTargetTenant(tenantId, logicalRequirement);
		logicalRequirement.getTenants().remove(tenantToTemove);

		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);
		removeBdsOwnedByThisTenant(commonTenant, tenantToTemove.getId());
		removeL3outsOwnedByThisTenant(commonTenant, tenantToTemove.getId());

		// convert back to json string
		/*
		 * String updatedTenants = gson.toJson(logicalRequirement);
		 * 
		 * // set it back to the project and update db
		 * proj.setLogicalRequirement(updatedTenants);
		 * 
		 * // update the summary also // convert it to java obj LogicalSummary
		 * logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(),
		 * LogicalSummary.class); int count = logicalSummary.getTenantCount();
		 * logicalSummary.setTenantCount(count - 1);
		 * 
		 * // convert back to json string
		 * proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));
		 */

		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement);

		if (returnProj != null) {
			// projectServices.callSizingSummary(projectId);
			return 1;
		}

		throw new GenericCouldNotSaveException("Could not delete tenant data");
	}

	private void removeL3outsOwnedByThisTenant(Tenant commonTenant, int ownerId) {

		ArrayList<L3out> removeList = new ArrayList<L3out>();
		for (L3out iter : commonTenant.getL3outs()) {
			if (ownerId == iter.getOwnershipId()) {
				removeList.add(iter);
			}
		}

		commonTenant.getL3outs().removeAll(removeList);
	}

	private void removeBdsOwnedByThisTenant(Tenant commonTenant, int ownerId) {

		ArrayList<Bd> removeList = new ArrayList<Bd>();
		for (Bd iter : commonTenant.getBds()) {
			if (ownerId == iter.getOwnershipId()) {
				removeList.add(iter);
			}
		}

		commonTenant.getBds().removeAll(removeList);
	}

	private Tenant getTargetTenant(int tenantId, LogicalRequirement logicalRequirement) throws AciEntityNotFound {
		/*
		 * Optional<Tenant> tenantsToTemove =
		 * logicalRequirement.getTenants().stream() .filter(tenantToRemove ->
		 * tenantId == tenantToRemove.getId()).findFirst(); if
		 * (!tenantsToTemove.isPresent()) { throw new
		 * AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID); } final
		 * Tenant tenantToDelete = tenantsToTemove.get(); if
		 * (!tenantToDelete.getContracts().isEmpty()) {
		 * tenantToDelete.getContracts().stream().forEach((contract) -> {
		 * contractServices.updateProviderConsumers(tenantToDelete,
		 * Utility.getCommonTenant(logicalRequirement), contract); }); } return
		 * tenantsToTemove;
		 */
		Tenant tenantToTemove = null;
		for (Tenant iter : logicalRequirement.getTenants()) {
			if (tenantId == iter.getId()) {
				tenantToTemove = iter;
				break;
			}
		}

		if (null == tenantToTemove) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}

		if (null != tenantToTemove.getContracts()) {
			for (Contract iter : tenantToTemove.getContracts()) {
				contractServices.updateProviderConsumers(tenantToTemove, Utility.getCommonTenant(logicalRequirement),
						iter);
			}
		}

		return tenantToTemove;
	}

	public Tenant updateTenant(int projectId, int tenantId, Tenant updatedTenant) throws AciEntityNotFound {
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant tenantToRemove = null;

		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (tenant.getId() == tenantId) {
				tenantToRemove = tenant;
				break;
			}
		}

		if (tenantToRemove == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		// LogicalSummary logicalSummary =
		// gson.fromJson(proj.getLogicalRequirementSummary(),
		// LogicalSummary.class);
		TenantHelper.modifiedTime(updatedTenant);
		tenantToRemove.setCount(updatedTenant.getCount());
		tenantToRemove.setDisplayName(updatedTenant.getDisplayName());
		
		
		// proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));

		/*
		 * String updatedTenants = gson.toJson(logicalRequirement);
		 * 
		 * // set it back to the project and update db
		 * proj.setLogicalRequirement(updatedTenants);
		 */
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement);

		if (returnProj != null) {
			// projectServices.callSizingSummary(projectId);
			TenantHelper.modifiedTime(tenantToRemove);
			return tenantToRemove;
		}

		throw new GenericCouldNotSaveException("Could not save tenant data");
	}

	private boolean isDuplicate(Tenant tenant, List<Tenant> tenants) {
		/*
		 * return tenants.stream() .filter(tenantIter -> (true ==
		 * tenantIter.getDisplayName().equals(tenant.getDisplayName())))
		 * .findFirst().isPresent();
		 */

		for (Tenant tenantTemp : tenants) {
			if (tenantTemp.getDisplayName().equals(tenant.getDisplayName())) {
				return true;
			}
		}
		return false;
		// return foundTenant.isPresent();

	}

	public ViewNodes getNodeCollectionTenant(int projectId, int tenantId) throws AciEntityNotFound {
		ViewNodes nodes = new ViewNodes();

		ProjectTable proj = projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);
		if (!commonTenant.getName().equals(currTenant.getName())) {
			/*
			 * commonTenant.getVrfs().parallelStream() .forEach(vrf ->
			 * addCommonTenantVrfToNodes(nodes, commonTenant, currTenant, vrf));
			 */
			for (Vrf vrf : commonTenant.getVrfs()) {
				addCommonTenantVrfToNodes(nodes, commonTenant, currTenant, vrf);
			}
		}
		/*
		 * currTenant.getVrfs().parallelStream().forEach(vrf ->
		 * addCurrentTenantVrfToNodes(nodes, currTenant, vrf));
		 */
		for (Vrf vrf : currTenant.getVrfs()) {
			addCurrentTenantVrfToNodes(nodes, currTenant, vrf);
		}

		return nodes;
	}

	public void addCommonTenantVrfToNodes(ViewNodes nodes, Tenant commonTenant, Tenant currTenant, Vrf vrf) {
		/*
		 * commonTenant.getBds().parallelStream() .filter(bd ->
		 * bd.getVrf().equals(vrf.getName()) && (bd.getOwnershipId() ==
		 * currTenant.getId() || bd.getOwnershipId() == commonTenant.getId()))
		 * .forEach(bd -> addBdToNodes(nodes, commonTenant, vrf, bd));
		 */
		for (Bd bd : commonTenant.getBds()) {
			if (bd.getVrf().equals(vrf.getName())
					&& (bd.getOwnershipId() == currTenant.getId() || bd.getOwnershipId() == commonTenant.getId())) {
				addBdToNodes(nodes, commonTenant, vrf, bd);
			}
		}

		for (L3out iter : commonTenant.getL3outs()) {
			if (iter.getVrf().equals(vrf.getName())
					&& (iter.getOwnershipId() == currTenant.getId() || iter.getOwnershipId() == commonTenant.getId())) {
				addL3outToNodes(nodes, commonTenant, vrf, iter);
			}
		}

		addVrfToNodes(nodes, commonTenant, vrf);
	}

	public void addCurrentTenantVrfToNodes(ViewNodes nodes, Tenant currTenant, Vrf vrf) {
		/*
		 * currTenant.getBds().parallelStream().filter(bd ->
		 * bd.getVrf().equals(vrf.getName())) .forEach(bd -> addBdToNodes(nodes,
		 * currTenant, vrf, bd));
		 */
		for (Bd bd : currTenant.getBds()) {
			if (bd.getVrf().equals(vrf.getName())) {
				addBdToNodes(nodes, currTenant, vrf, bd);
			}
		}

		for (L3out l3out : currTenant.getL3outs()) {
			if (l3out.getVrf().equals(vrf.getName())) {
				addL3outToNodes(nodes, currTenant, vrf, l3out);
			}
		}

		addVrfToNodes(nodes, currTenant, vrf);
	}

	/**
	 * @param nodes
	 * @param currTenant
	 * @param vrf
	 * @return
	 */
	public boolean addVrfToNodes(ViewNodes nodes, Tenant currTenant, Vrf vrf) {
		/*
		 * currTenant.getL3outs().parallelStream().filter(l3out ->
		 * l3out.getVrf().equals(vrf.getName())) .forEach(l3out ->
		 * addL3outToNodes(nodes, currTenant, vrf, l3out));
		 * currTenant.getSharedResources().parallelStream()
		 * .filter(sharedResources ->
		 * sharedResources.getVrf().equals(vrf.getName()))
		 * .forEach(sharedResources -> addSharedResourceToNodes(nodes,
		 * sharedResources, currTenant));
		 */

		for (SharedResource sharedResources : currTenant.getSharedResources()) {
			if (sharedResources.getVrf().equals(vrf.getName())) {
				addSharedResourceToNodes(nodes, sharedResources, currTenant);
			}
		}

		return nodes.getNodes()
				.add(vrfServices.outputmapping(vrf, new com.cisco.applicationprofiler.ui.models.VrfUi(), currTenant));
	}

	/**
	 * @param nodes
	 * @param sharedResources
	 * @param currTenant
	 * @return
	 */
	private boolean addSharedResourceToNodes(ViewNodes nodes, SharedResource sharedResources, Tenant currTenant) {
		SharedResourceUi sharedResourceToAddInUi = sharedResourceServices.outputMapping(sharedResources,
				new SharedResourceUi(), currTenant);
		Utility.convertToViewConnectionType(Integer.parseInt(sharedResources.getVrf()),
				Integer.parseInt(sharedResources.getName()), nodes);
		return nodes.getNodes().add(sharedResourceToAddInUi);
	}

	/**
	 * @param nodes
	 * @param currTenant
	 * @param vrf
	 * @param l3out
	 * @return
	 */
	public boolean addL3outToNodes(ViewNodes nodes, Tenant currTenant, Vrf vrf, L3out l3out) {
		com.cisco.applicationprofiler.ui.models.L3outUi l3outToAddInConnection = l3OutServices.outputMapping(l3out,
				new com.cisco.applicationprofiler.ui.models.L3outUi(), vrf, currTenant);
		Utility.convertToViewConnectionType(vrf.getId(), l3outToAddInConnection.getId(), nodes);
		return nodes.getNodes().add(l3outToAddInConnection);
	}

	/**
	 * @param nodes
	 * @param currTenant
	 * @param vrf
	 * @param bd
	 * @return
	 */
	public boolean addBdToNodes(ViewNodes nodes, Tenant currTenant, Vrf vrf, Bd bd) {
		com.cisco.applicationprofiler.ui.models.BdUi bdToAddInConnection = bdServices.outputMapping(bd, currTenant,
				vrf);
		Utility.convertToViewConnectionType(vrf.getId(), bdToAddInConnection.getId(), nodes);
		return nodes.getNodes().add(bdToAddInConnection);
	}

	public List<ViewNode> updateView(int projectId, Integer tenantId, List<ViewNode> nodes) throws AciEntityNotFound {
		if (null == nodes || true == nodes.isEmpty())
			return null;

		ProjectTable proj = projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}
		boolean bFoundTenant = false;
		List<ViewNode> updatedNodes = null;
		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (0 == tenant.getName().compareToIgnoreCase(tenantId.toString())) {
				bFoundTenant = true;
				updatedNodes = updateView(tenant, nodes);
				break;
			}
		}

		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (ACISizerConstant.DEFAULT_TENANT_ID == tenant.getId()) {
				bFoundTenant = true;
				List<ViewNode> updatedDefaultNodes = updateView(tenant, nodes);
				updatedNodes.addAll(updatedDefaultNodes);
				break;
			}
		}

		if (false == bFoundTenant)
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);

		String updatedTenant = gson.toJson(logicalRequirement);

		// set it back to the project and update db
		proj.setLogicalRequirement(updatedTenant);

		if (projrepo.save(proj) != null) {
			return updatedNodes;
		}

		throw new GenericCouldNotSaveException("Could not save node view");
	}

	private List<ViewNode> updateView(Tenant tenant, List<ViewNode> nodes) {
		List<ViewNode> updatedNodes = new ArrayList<ViewNode>();

		for (ViewNode iter : nodes) {

			switch (iter.getDataItem().getType()) {
			case ACISizerConstant._app:
				for (Application app : tenant.getApps()) {
					if (app.getId() == iter.getId()) {
						updatedNodes.add(iter);
						app.setUiData(iter.getDataItem().getUiData());
						break;
					}
				}
				break;
			case ACISizerConstant._epg:
				for (Epg epg : tenant.getEpgs()) {
					if (epg.getId() == iter.getId()) {
						updatedNodes.add(iter);
						epg.setUiData(iter.getDataItem().getUiData());
						break;
					}
				}

				break;
			case ACISizerConstant._contract:
			case ACISizerConstant._contractWithl3out:
			case ACISizerConstant._contractWithSharedResource:
				for (Contract contract : tenant.getContracts()) {
					if (contract.getId() == iter.getId()) {
						updatedNodes.add(iter);
						contract.setUiData(iter.getDataItem().getUiData());
						break;
					}
				}
				break;
			case ACISizerConstant._vrf:
				for (Vrf vrf : tenant.getVrfs()) {
					if (vrf.getId() == iter.getId()) {
						updatedNodes.add(iter);
						vrf.setUiData(iter.getDataItem().getUiData());
						break;
					}
				}
				break;
			case ACISizerConstant._bd:
				for (Bd bd : tenant.getBds()) {
					if (bd.getId() == iter.getId()) {
						updatedNodes.add(iter);
						bd.setUiData(iter.getDataItem().getUiData());
						break;
					}
				}
				break;
			case ACISizerConstant._l3out:
				for (L3out l3out : tenant.getL3outs()) {
					if (l3out.getId() == iter.getId()) {
						updatedNodes.add(iter);
						l3out.setUiData(iter.getDataItem().getUiData());
						break;
					}
				}
				break;
			case ACISizerConstant._shared_resource:
				for (SharedResource sr : tenant.getSharedResources()) {
					if (sr.getId() == iter.getId()) {
						updatedNodes.add(iter);
						sr.setUiData(iter.getDataItem().getUiData());
						break;
					}
				}
				break;

			}
		}

		return updatedNodes;
	}

	public Tenant addTenantTemplate(int projectId, Tenant tenantUi)
			throws AciEntityNotFound, GenericInvalidDataException {

		ProjectTable proj = projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);

		Tenant tenant = new Tenant(logicalSummary.generateUniqueId(), tenantUi.getDisplayName());
		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);
		tenant.copyTenant(tenantUi);
		// tenant.setCount(template.getInstance());
		addTenantTemplate(logicalSummary, tenant, commonTenant);

		Tenant tenantAdded = addTenant(tenant, logicalRequirement);
		// TenantHelper.modifiedTime(tenantAdded);
		// Utility.setNodeCountForFabricPane(tenantAdded, logicalSummary);
		logicalSummary.setTenantCount(logicalSummary.getTenantCount() + 1);
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));
		proj.setLogicalRequirement(gson.toJson(logicalRequirement));
		ProjectTable returnProj = projrepo.save(proj);

		if (returnProj != null) {
			// projectServices.callSizingSummary(projectId);
			return tenantAdded;
		}

		throw new GenericCouldNotSaveException("Could not save data for tenant");
	}

	/**
	 * @param logicalSummary
	 * @param tenant
	 * @param commonTenant
	 */
	public void addTenantTemplate(LogicalSummary logicalSummary, Tenant tenant, Tenant commonTenant) {
		if (tenant.isLocalVrf() && tenant.isLocalL3out()) {
			TenantHelper.addLocalVrfBdL3out(logicalSummary, tenant);
		} else if (tenant.isLocalVrf() && !tenant.isLocalL3out()) {
			TenantHelper.addLocalVrf(logicalSummary, tenant);
		} else if (!tenant.isLocalVrf() && tenant.isLocalL3out()) {
			TenantHelper.addCommonL3out(logicalSummary, commonTenant, tenant);
		}
	}

	public AciSizerModelUi getDefaultValues(int projId, int tenantId, String type) throws AciEntityNotFound {
		ProjectTable proj = projrepo.findOne(projId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		Tenant commonTenant = Utility.getCommonTenant(logicalRequirement);
		return getDefaultValues(currTenant, commonTenant, type);

	}

	public AciSizerModelUi getDefaultValues(Tenant tenant, Tenant commonTenant, String type) throws AciEntityNotFound {
		List<String> names;
		switch (type) {
		case ACISizerConstant._app:
			ApplicationUi applicationUi = ApplicationUi.getDefaultApplication();
			/*
			 * List<String> tenantNames =
			 * tenant.getApps().parallelStream().map(app ->
			 * app.getDisplayName()) .collect(Collectors.toList());
			 */
			names = new ArrayList<>();
			for (Application app : tenant.getApps()) {
				names.add(app.getDisplayName());
			}
			applicationUi.setName(autoNameGenerator.generateName(names, type));
			return applicationUi;
		case ACISizerConstant._bd:
			BdUi bdUi = BdUi.getDefaultBd();
			/*
			 * List<String> bdNames = tenant.getBds().parallelStream().map(bd ->
			 * bd.getDisplayName()) .collect(Collectors.toList());
			 */
			names = new ArrayList<>();
			for (Bd bd : BdHelper.getAllBdsOfTenantAndCommon(tenant, commonTenant)) {
				names.add(bd.getDisplayName());
			}
			bdUi.setName(autoNameGenerator.generateName(names, type));
			return bdUi;
		case ACISizerConstant._contract:
			ContractUi contractUi = ContractUi.getContractDefault();
			/*
			 * List<String> contractNames =
			 * tenant.getContracts().parallelStream() .map(contract ->
			 * contract.getDisplayName()).collect(Collectors.toList());
			 */
			names = new ArrayList<>();
			for (Contract contract : tenant.getContracts()) {
				names.add(contract.getDisplayName());
			}
			contractUi.setName(autoNameGenerator.generateName(names, type));
			return contractUi;

		case ACISizerConstant._contractWithl3out:
			ContractUi contractL3outUi = ContractUi.getContractDefault();
			/*
			 * List<String> contractNames =
			 * tenant.getContracts().parallelStream() .map(contract ->
			 * contract.getDisplayName()).collect(Collectors.toList());
			 */
			names = new ArrayList<>();
			for (Contract contract : tenant.getContracts()) {
				names.add(contract.getDisplayName());
			}
			contractL3outUi.setName(autoNameGenerator.generateName(names, type));
			setDefaultL3outConsumer(commonTenant, contractL3outUi);
			return contractL3outUi;

		case ACISizerConstant._contractWithSharedResource:
			ContractUi contractSRUi = ContractUi.getContractDefault();
			names = new ArrayList<>();
			for (Contract contract : tenant.getContracts()) {
				names.add(contract.getDisplayName());
			}
			contractSRUi.setName(autoNameGenerator.generateName(names, "ctSha"));
			setDefaultSR(commonTenant, contractSRUi);
			return contractSRUi;

		case ACISizerConstant._epg:
			EpgUi epgUi = EpgUi.getEpgDefaults();
			/*
			 * List<String> epgNames = tenant.getEpgs().parallelStream().map(epg
			 * -> epg.getDisplayName()) .collect(Collectors.toList());
			 */
			names = new ArrayList<>();
			for (Epg epg : tenant.getEpgs()) {
				names.add(epg.getDisplayName());
			}
			epgUi.setName(autoNameGenerator.generateName(names, type));
			epgUi.setEpgSubnets(0);
			setDefaultSharedResource(epgUi, commonTenant);
			return epgUi;
		case ACISizerConstant._l3out:
			L3outUi l3outUi = L3outUi.getL3outDefault();
			/*
			 * List<String> l3outNames =
			 * tenant.getL3outs().parallelStream().map(l3out ->
			 * l3out.getDisplayName()) .collect(Collectors.toList());
			 */
			names = new ArrayList<>();
			for (L3out l3out : L3outHelper.getAllL3outsOfTenantAndCommon(tenant, commonTenant)) {
				names.add(l3out.getDisplayName());
			}
			l3outUi.setName(autoNameGenerator.generateName(names, type));
			return l3outUi;
		case ACISizerConstant._vrf:
			VrfUi vrfUi = new VrfUi();
			/*
			 * List<String> vrfNames = tenant.getVrfs().parallelStream().map(vrf
			 * -> vrf.getDisplayName()) .collect(Collectors.toList());
			 */
			names = new ArrayList<>();
			for (Vrf vrf : tenant.getVrfs()) {
				names.add(vrf.getDisplayName());
			}
			vrfUi.setName(autoNameGenerator.generateName(names, type));
			return vrfUi;
		}
		throw new AciEntityNotFound("type is not valid");
	}

	private void setDefaultSR(Tenant commonTenant, ContractUi contractSRUi) {

		for (SharedResource sr : commonTenant.getSharedResources()) {
			if (sr.getId() == ACISizerConstant.DEFAULT_SHARED_RESOURCE_ID) {
				contractSRUi.setConsumerId(sr.getId());
				contractSRUi.setConsumerName(sr.getDisplayName());
				contractSRUi.setConsumerType(ACISizerConstant._shared_resource);
				;
				break;
			}
		}
	}

	private void setDefaultL3outConsumer(Tenant commonTenant, ContractUi contractL3outUi) {
		// TODO Auto-generated method stub
		for (L3out l3out : commonTenant.getL3outs()) {
			if (l3out.getId() == ACISizerConstant.DEFAULT_L3OUT_ID) {
				contractL3outUi.setConsumerId(l3out.getId());
				contractL3outUi.setConsumerName(commonTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR
						+ l3out.getDisplayName());
				contractL3outUi.setConsumerType(ACISizerConstant._l3out);
				break;
			}
		}
	}

	private void setDefaultSharedResource(EpgUi epgUi, Tenant commonTenant) {
		// TODO Auto-generated method stub
		for (SharedResource sr : commonTenant.getSharedResources()) {
			if (sr.getId() == ACISizerConstant.DEFAULT_SHARED_RESOURCE_ID) {
				epgUi.setSharedServicesEnabled(true);
				epgUi.setSharedServiceId(sr.getId());
				epgUi.setSharedServiceName(
						commonTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + sr.getDisplayName());
				epgUi.setNoOfsharedServiceFilter(1);
				break;
			}
		}
	}

	public void pushConfig(int projectId, int tenantId, int deviceId) throws AciEntityNotFound {

		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant tenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		
		Device device = deviceRepository.findOne(deviceId);
		String loginURL = getApicLoginUrl(device);
		String loginXml=getApicLoginXml(device);
		String tenantXml=profilerToApicMapper.mapProfilerToApic(tenant);
		String apicToken=processBuilderApic.apicLogin(loginURL, loginXml);
		LOGGER.info(apicToken);
		
		try {
			processBuilderApic.pushTenant(getApicTenantUrl(device), tenantXml, apicToken);
		} catch (IOException e) {
			e.printStackTrace();
		}
		projrepo.updateProjectWithDeviceId(projectId, deviceId);
		tenant.setLastPushedTime(new Timestamp(new Date().getTime()));
		proj.setDevice(device);
		projectServices.saveProject(proj, logicalRequirement);
		
	}

	private String getApicLoginUrl(Device device) {
		return HTTPS + device.getIpAddress() + APIC_LOGIN_URL;
	}
	
	private String getApicTenantUrl(Device device){
		return HTTPS+device.getIpAddress()+APIC_TENANT_URL;
	}

	private String getApicLoginXml(Device device) {
		ApicUser user = new ApicUser();
		String tenantXml = null;
		JAXBContext jaxbContext;
		try {
			user.setName(device.getUsername());
			user.setPwd(device.getPassword());
			jaxbContext = JAXBContext.newInstance(ApicUser.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			marshaller.marshal(user, arrayOutputStream);
			tenantXml = arrayOutputStream.toString();

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return tenantXml;

	}

}
