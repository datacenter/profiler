/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.logical.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.ProjectTable;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.BdNameExistsException;
import com.cisco.applicationprofiler.exceptions.BdNotFoundException;
import com.cisco.applicationprofiler.exceptions.DefaultNodeException;
import com.cisco.applicationprofiler.exceptions.DeletionNotAllowedException;
import com.cisco.applicationprofiler.exceptions.ForbiddenOperationException;
import com.cisco.applicationprofiler.helper.TenantHelper;
import com.cisco.applicationprofiler.models.Bd;
import com.cisco.applicationprofiler.models.Epg;
import com.cisco.applicationprofiler.models.LogicalRequirement;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.Subnets;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.Vrf;
import com.cisco.applicationprofiler.repo.ProjectsRepository;
import com.cisco.applicationprofiler.ui.models.BdUi;
import com.cisco.applicationprofiler.util.ACISizerConstant;
import com.cisco.applicationprofiler.util.Utility;
import com.google.gson.Gson;

/**
 * @author Deepa
 *
 */
@Service
public class BdServices {
	public static final String CANNOT_DELETE_DEFAULT_BD = "Cannot delete default bd ";

	private static final Logger LOGGER = LoggerFactory.getLogger(BdServices.class);

	public static final String BD_NOT_FOUND_WITH_ID = "Bd not found with id";

	@Inject
	private ProjectsRepository projrepo;

	@Inject
	private ProjectServicesAci projectServices;

	@Inject
	private Gson gson;

	public Bd inputMapping(int bdId, com.cisco.applicationprofiler.ui.models.BdUi bd) {
		Bd bdDest = new Bd(bdId, bd.getName());
		bdDest.setVrf("" + bd.getVrfId());
		bdDest.setOwnershipId(bd.getOwnershipId());
		commonMapping(bd, bdDest);
		return bdDest;
	}

	/**
	 * @param bd
	 * @param bdDest
	 */
	private void commonMapping(com.cisco.applicationprofiler.ui.models.BdUi bd, Bd bdDest) {
		bdDest.setUiData(bd.getUiData());
		Subnets subnets = new Subnets();
		subnets.setIpv4(bd.getBdSubnets());
		bdDest.setSubnets(subnets);
		bdDest.setNoOfInstances(bd.getNoOfInstances());
	}

	public com.cisco.applicationprofiler.ui.models.BdUi outputMapping(Bd bd, Tenant tenant, Vrf vrf) {
		com.cisco.applicationprofiler.ui.models.BdUi bdDest = new com.cisco.applicationprofiler.ui.models.BdUi();
		if (bd.getSubnets() != null) {
			bdDest.setBdSubnets(bd.getSubnets().getIpv4());
		}
		bdDest.setFullName(tenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + bd.getDisplayName());
		bdDest.setId(Integer.parseInt(bd.getName()));
		bdDest.setName(bd.getDisplayName());
		bdDest.setUiData(bd.getUiData());
		bdDest.setVrfId(Integer.parseInt(bd.getVrf()));
		bdDest.setVrfName(tenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + vrf.getDisplayName());
		bdDest.setOwnershipId(bd.getOwnershipId());
		bdDest.setNoOfInstances(bd.getNoOfInstances());
		return bdDest;

	}

	public com.cisco.applicationprofiler.ui.models.BdUi addBd(int projectId, int tenantId, int vrfId,
			com.cisco.applicationprofiler.ui.models.BdUi newBd) throws AciEntityNotFound {
		Tenant currTenant;
		Vrf currVrf = null;
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// update the summary also
		// convert it to java obj
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		int count = logicalSummary.getBdCount();
		logicalSummary.setBdCount(count + newBd.getNoOfInstances());
		int uniqueId = logicalSummary.generateUniqueId();

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);
		currVrf = getCurrentVrf(vrfId, currTenant);

		if (currVrf == null) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
		Bd bdToAdd = inputMapping(uniqueId, newBd);
		if (null == bdToAdd.getSubnets()) {
			bdToAdd.setSubnets(new Subnets());
		}

		bdToAdd.setVrf(currVrf.getName());
		if (!currTenant.getBds().add(bdToAdd)) {
			throw new BdNameExistsException("");
		}

		// convert back to json string
		/*String updatedTenants = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(updatedTenants);

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));*/

		// set it back to the project and update db
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);

		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			return outputMapping(bdToAdd, currTenant, currVrf);
		} else {
			return null;
		}

	}

	/**
	 * @param vrfId
	 * @param currTenant
	 * @return Vrf
	 */
	private Vrf getCurrentVrf(int vrfId, Tenant currTenant) {
		Vrf currVrf = null;
		for (Vrf vrf : currTenant.getVrfs()) {
			if (vrf.getId() == vrfId) {
				currVrf = vrf;
				break;
			}
		}

		return currVrf;
	}

	boolean isDeletePossible(List<Tenant> tenants, String name) {
		for (Tenant tenant : tenants) {
			if (tenant.getEpgs() != null && !tenant.getEpgs().isEmpty()) {
				for (Epg epg : tenant.getEpgs()) {
					if (name.equals(epg.getBd())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public com.cisco.applicationprofiler.ui.models.BdUi getBd(int projectId, int tenantId, int vrfId, int bdId)
			throws AciEntityNotFound {
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID );
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		Vrf currVrf = getCurrentVrf(vrfId, currTenant);
		if (currVrf == null) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
		for (Bd bd : currTenant.getBds()) {
			if (bdId == bd.getId()) {
				Bd bdTemp = new Bd(bd.getId(), bd.getDisplayName());
				bdTemp.copyBd(bd);
				/*
				 * bdTemp.setFullyQualifiedName(currTenant.getDisplayName() +
				 * ACISizerConstant.TENANT_FIELD_SEPERATOR +
				 * bdTemp.getDisplayName());
				 */
				return outputMapping(bdTemp, currTenant, currVrf);
			}
		}
		throw new BdNotFoundException("Bd id " + bdId + " not found");
	}

	public List<com.cisco.applicationprofiler.ui.models.BdUi> getBds(int projectId, int tenantId, int vrfId)
			throws AciEntityNotFound {

		//List<Bd> newBdList = new ArrayList<Bd>();

		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		Vrf currVrf = getCurrentVrf(vrfId, currTenant);
		if (currVrf == null) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
		/*for (Bd bd : currTenant.getBds()) {
			if (currVrf.getName().equals(bd.getVrf())) {
				Bd bdTemp = new Bd(bd.getId(), bd.getDisplayName());
				bdTemp.copyBd(bd);
				newBdList.add(bdTemp);
			}
		}*/
		/*
		return newBdList.parallelStream().map(bd -> {
			return outputMapping(bd, currTenant, currVrf);
		}).collect(Collectors.toList());
		
		
		*/
		
		List<BdUi> names=new ArrayList<>();
		 for (Bd bd : currTenant.getBds() ){
			 if (currVrf.getName().equals(bd.getVrf())) {
				names.add(outputMapping(bd, currTenant, currVrf));	
			 }
		 }

		return names; 
	}

	public int deleteBd(int projectId, int tenantId, int vrfId, int bdId)
			throws DeletionNotAllowedException, AciEntityNotFound {

		if (bdId == ACISizerConstant.DEFAULT_BD_ID) {
			throw new DefaultNodeException(CANNOT_DELETE_DEFAULT_BD +"'"+ACISizerConstant.BD_DEFAULT+"'");
		}

		// get the bds by project id
		ProjectTable proj = projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		/*
		Optional<Epg> dependentEpg = currTenant.getEpgs().stream().filter(epg ->(true == epg.getBd().equals(Integer.toString(bdId)))).findFirst();
		if(dependentEpg.isPresent())
		{
			throw new DeletionNotAllowedException(ACISizerConstant.PLEASE_DELETE_THE_EPGS_ASSOCIATED_FIRST);
			
		}
		*/
		 for(Epg epg : currTenant.getEpgs())
		 {
			 if(epg.getBd().equals(Integer.toString(bdId)))
			    {
				 throw new DeletionNotAllowedException(ACISizerConstant.PLEASE_DELETE_THE_EPGS_ASSOCIATED_FIRST);
			    }
			 
		 }
		
		
		
		 Bd bdToRemove = getBdToRemove(currTenant, bdId, vrfId);
		
		deleteBd(tenantId, vrfId, bdToRemove, currTenant, logicalRequirement.getTenants());
	/*	// convert back to json string
		String updatedTenants = gson.toJson(logicalRequirement);

		// set it back to the project and update db
		proj.setLogicalRequirement(updatedTenants);*/

		// update the summary also
		// convert it to java obj
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		int count = logicalSummary.getBdCount();
		logicalSummary.setBdCount(count - bdToRemove.getNoOfInstances());

		// convert back to json string
		//proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));

		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			return 1;
		} else {
			return 0;
		}

	}

	/**
	 * @param tenantId
	 * @param vrfId
	 * @param bdToRemove
	 * @param tenants
	 * @param logicalRequirement
	 * @return
	 * @throws DeletionNotAllowedException
	 * @throws AciEntityNotFound
	 */
	private Bd deleteBd(int tenantId, int vrfId, Bd bdToRemove, Tenant currTenant, List<Tenant> tenants)
			throws DeletionNotAllowedException, AciEntityNotFound {

		
		String bdToRemoveName = currTenant.getName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + bdToRemove.getName();
		if (!isDeletePossible(tenants, bdToRemoveName)) {
			throw new DeletionNotAllowedException("Please delete the associated epgs first");
		}
		currTenant.getBds().remove(bdToRemove);
		return bdToRemove;
	}

	private Bd getBdToRemove(Tenant currTenant, int bdId, int vrfId) throws AciEntityNotFound {
		Vrf currVrf = getCurrentVrf(vrfId, currTenant);
		if (currVrf == null) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
		Bd bdToRemove = null;
		for (Bd bd : currTenant.getBds()) {
			if (bd.getId() == bdId && bd.getVrf().equals(currVrf.getName())) {
				bdToRemove = bd;
				break;
			}
		}
		if (bdToRemove == null) {
			throw new BdNotFoundException(BD_NOT_FOUND_WITH_ID);
		}

		return bdToRemove;
	}

	public com.cisco.applicationprofiler.ui.models.BdUi updateBd(int projectId, int tenantId, int vrfId, int bdId,
			com.cisco.applicationprofiler.ui.models.BdUi bd) throws AciEntityNotFound, ForbiddenOperationException {

		if (bdId == ACISizerConstant.DEFAULT_BD_ID) {
			throw new ForbiddenOperationException("default Bd cannot be updated");
		}
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		Vrf currVrf = Utility.getCurrentVrf(vrfId, currTenant);
		if (currVrf == null) {
			throw new EntityNotFoundException(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
		Bd removedBd = getBdToRemove(currTenant, bdId, vrfId);
		currTenant.getBds().remove(removedBd);
		Bd bdToUpdate = new Bd(bdId, bd.getName());
		bdToUpdate.copyBd(removedBd);
		commonMapping(bd, bdToUpdate);
		// bdToUpdate.setSubnets(removedBd.getSubnets());
		if (!currTenant.getBds().add(bdToUpdate)) {
			throw new BdNameExistsException("");
		}
		// convert back to json string
		//String updatedTenants = gson.toJson(logicalRequirement);

		// set it back to the project and update db
		//proj.setLogicalRequirement(updatedTenants);

		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			return outputMapping(bdToUpdate, currTenant, currVrf);
		} else {
			return null;
		}
	}

	/*public Bd addBd(int tenantId, int vrfId, Bd src, ProjectTable proj) throws AciEntityNotFound {
		boolean tenantFound = false;
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		logicalSummary.setBdCount(logicalSummary.getBdCount() + 1);
		int uniqueId = logicalSummary.generateUniqueId();

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		ArrayList<Tenant> tenantList = new ArrayList<>();
		Bd bdAdded = null;
		for (Tenant tenant : logicalRequirement.getTenants()) {

			if (tenant.getId() == tenantId) {
				tenantFound = true;
				bdAdded = pushBd(src, uniqueId, tenant);
			}
			tenantList.add(tenant);

		}

		if (!tenantFound) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}

		// convert back to json string
		logicalRequirement.setTenants(tenantList);
		String updatedTenants = gson.toJson(logicalRequirement);

		proj.setLogicalRequirement(updatedTenants);

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));
		return bdAdded;
	}*/

	/*private Bd pushBd(Bd src, int bdId, Tenant tenant) {
		if (!tenant.getBds().add(src)) {
			throw new BdNameExistsException("Bd name " + src.getDisplayName() + " already exists in the system");
		}
		return src;
	}*/
}
