package com.cisco.applicationprofiler.logical.services;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.ProjectTable;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.DefaultNodeException;
import com.cisco.applicationprofiler.exceptions.DeletionNotAllowedException;
import com.cisco.applicationprofiler.exceptions.ForbiddenOperationException;
import com.cisco.applicationprofiler.exceptions.GenericCouldNotSaveException;
import com.cisco.applicationprofiler.exceptions.SharedResourceNameExistsException;
import com.cisco.applicationprofiler.exceptions.SharedResourceNotFoundException;
import com.cisco.applicationprofiler.models.LogicalRequirement;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.SharedResource;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.Vrf;
import com.cisco.applicationprofiler.repo.ProjectsRepository;
import com.cisco.applicationprofiler.ui.models.SharedResourceUi;
import com.cisco.applicationprofiler.util.ACISizerConstant;
import com.google.gson.Gson;

@Service
public class SharedResourceServices {
	@Inject
	private com.cisco.applicationprofiler.repo.ProjectsRepository m_projrepo;

	@Inject
	private Gson gson;
	
	public void inputMapping(SharedResourceUi src,SharedResource dest){
		
		dest.setConsumed_contracts(src.getConsumedContracts());
		dest.setUiData(src.getUiData());
		dest.setVrf(src.getVrf());
	}
	
	public SharedResourceUi outputMapping(SharedResource src,SharedResourceUi dest, Tenant currTenant){
		dest.setId(Integer.parseInt(src.getName()));
		dest.setFullName(currTenant.getDisplayName()+ACISizerConstant.TENANT_FIELD_SEPERATOR+src.getDisplayName());
		dest.setName(src.getDisplayName());
		dest.setUiData(src.getUiData());
		dest.setVrf(src.getVrf());
		dest.setConsumedContracts(src.getConsumed_contracts());
		return dest;
		
	}

	public SharedResourceUi addSharedResource(int projectId, int tenantId, int vrfId, SharedResourceUi srcSR)
			throws AciEntityNotFound {

		ProjectTable proj = m_projrepo.getOne(projectId);
		Tenant currTenant = null;
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		List<Tenant> tenantList = new ArrayList<Tenant>();

		boolean bFoundTenant = false;
		boolean bFoundVrf = false;

		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		SharedResource sharedResourceToAdd = new SharedResource(logicalSummary.generateUniqueId(),
				srcSR.getName());
inputMapping(srcSR, sharedResourceToAdd);
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (tenantId == tenant.getId()) {
				bFoundTenant = true;
			currTenant=tenant;

				for (Vrf iter : tenant.getVrfs()) {
					if (vrfId == iter.getId()) {
						bFoundVrf = true;
						sharedResourceToAdd.setVrf(iter.getName());
						if (null == sharedResourceToAdd.getUiData())
							sharedResourceToAdd.setUiDataNull();
						break;
					}
				}
				if (false == bFoundVrf) {
					throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
				}

				if (null == tenant.getSharedResources())
					tenant.setSharedResources(new LinkedHashSet<SharedResource>());
				if (false == tenant.getSharedResources().add(sharedResourceToAdd))
					throw new SharedResourceNameExistsException(ACISizerConstant.SHARED_RESOURCE_NAME_EXISTS);

			}

			tenantList.add(tenant);
		}

		if (false == bFoundTenant) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}

		// convert back to json string
		logicalRequirement.setTenants(tenantList);
		String addedSR = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(addedSR);

		// update the summary also
		// convert it to java obj
		logicalSummary.incrementSharedResourceCount();

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));

		// set it back to the project and update db
		ProjectTable returnProj = m_projrepo.save(proj);

		if (returnProj != null) {
			return outputMapping(sharedResourceToAdd, new SharedResourceUi(),currTenant);
		}

		throw new GenericCouldNotSaveException("Could not save shared resource data");
	}

	public SharedResourceUi updatesharedResource(int projectId, int tenantId, int vrfId, int sharedResourceId,
			SharedResourceUi srcSR) throws AciEntityNotFound, ForbiddenOperationException {
		SharedResource updateSR = null ;
		if(sharedResourceId==ACISizerConstant.DEFAULT_SHARED_RESOURCE_ID){
			throw new ForbiddenOperationException("default shared resource cannot be edited");
		}
		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		List<Tenant> tenantList = new ArrayList<Tenant>();

		boolean bFoundTenant = false;
		Tenant currTenant = null;
		boolean bFoundVrf = false;
		boolean bSharedResourceFound = false;

		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);

		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (tenantId == tenant.getId()) {
				bFoundTenant = true;
				 currTenant = tenant;

				for (Vrf iter : tenant.getVrfs()) {
					if (vrfId == iter.getId()) {
						bFoundVrf = true;
						break;
					}

				}
				if (false == bFoundVrf) {
					throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
				}

				if (null == tenant.getSharedResources())
					throw new SharedResourceNotFoundException(ACISizerConstant.SHARED_RESOURCE_NOT_CREATED);

				 updateSR = new SharedResource(sharedResourceId, srcSR.getName());
				inputMapping(srcSR, updateSR);
				
				for (SharedResource sr : tenant.getSharedResources()) {
					if (sr.getId() == sharedResourceId && Integer.parseInt(sr.getName()) == vrfId) {
						bSharedResourceFound = true;
						tenant.getSharedResources().remove(sr);
						updateSR.setVrf(sr.getVrf());
						break;
					}
				}
				if (true == bSharedResourceFound) {
					tenant.getSharedResources().add(updateSR);
				}

			}

			tenantList.add(tenant);
		}

		if (false == bFoundTenant) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID );
		}

		if (false == bSharedResourceFound) {
			throw new SharedResourceNotFoundException(
					ACISizerConstant.SHARED_RESOURCE_NOT_FOUND_WITH_ID);
		}

		// convert back to json string
		logicalRequirement.setTenants(tenantList);
		String updatedSR = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(updatedSR);

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));

		// set it back to the project and update db
		ProjectTable returnProj = m_projrepo.save(proj);

		if (returnProj != null) {
			return outputMapping(updateSR, new SharedResourceUi(),currTenant);
		}

		throw new GenericCouldNotSaveException("Could not save shared resource data");
	}

	public int deleteSharedResource(int projectId, int tenantId, int vrfId, int sharedResourceId)
			throws AciEntityNotFound, DeletionNotAllowedException {
		
		if (sharedResourceId == ACISizerConstant.DEFAULT_SHARED_RESOURCE_ID) {
			throw new DefaultNodeException("Cannot delete default shared service '" + ACISizerConstant.DEFAULT_SHARED_RESOURCE_NAME+"'");
		}
		
		
		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		List<Tenant> tenantList = new ArrayList<Tenant>();

		boolean bFoundTenant = false;
		boolean bFoundVrf = false;
		boolean bSharedResourceFound = false;

		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);

		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (tenantId == tenant.getId()) {
				bFoundTenant = true;

				for (Vrf iter : tenant.getVrfs()) {
					if (vrfId == iter.getId()) {
						bFoundVrf = true;
						break;
					}

				}
				if (false == bFoundVrf) {
					throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
				}

				if (null == tenant.getSharedResources())
					throw new SharedResourceNotFoundException(ACISizerConstant.SHARED_RESOURCE_NOT_CREATED);

				SharedResource deleteElementSR = null;
				for (SharedResource sr : tenant.getSharedResources()) {
					if (sr.getId() == sharedResourceId/* && Integer.parseInt(sr.getName()) == vrfId*/) {
						bSharedResourceFound = true;
						deleteElementSR = sr;
						break;
					}
				}

				if (true == bSharedResourceFound) {
					if (null != deleteElementSR.getConsumed_contracts()) {
						if (!deleteElementSR.getConsumed_contracts().isEmpty()) {
							throw new DeletionNotAllowedException(
									ACISizerConstant.PLEASE_DELETE_THE_CONTRACTS_ASSOCIATED_FIRST);
						}
					}

					tenant.getSharedResources().remove(deleteElementSR);
				}

			}

			tenantList.add(tenant);
		}

		if (false == bFoundTenant) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID );
		}

		if (false == bSharedResourceFound) {
			throw new SharedResourceNotFoundException(
					ACISizerConstant.SHARED_RESOURCE_NOT_FOUND_WITH_ID);
		}

		// convert back to json string
		logicalRequirement.setTenants(tenantList);
		String deleteSR = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(deleteSR);
		logicalSummary.decrementSharedResourceCount();

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));

		// set it back to the project and update db
		ProjectTable returnProj = m_projrepo.save(proj);

		if (returnProj != null) {
			return 1;
		}

		throw new GenericCouldNotSaveException("Could not save shared resource data");
	}

	public SharedResourceUi getSharedResource(int projectId, int tenantId, int vrfId, int sharedResourceId)
			throws AciEntityNotFound {

		SharedResource ret = null;
		Tenant currTenant = null;

		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		boolean bFoundTenant = false;
		boolean bFoundVrf = false;
		boolean bSharedResourceFound = false;

		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (tenantId == tenant.getId()) {
				bFoundTenant = true;
currTenant=tenant;
				for (Vrf iter : tenant.getVrfs()) {
					if (vrfId == iter.getId()) {
						bFoundVrf = true;
						break;
					}
				}

				if (false == bFoundVrf) {
					throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
				}

				if (null == tenant.getSharedResources())
					throw new SharedResourceNotFoundException(ACISizerConstant.SHARED_RESOURCE_NOT_CREATED);

				for (SharedResource sr : tenant.getSharedResources()) {
					if (sr.getId() == sharedResourceId) {
						bSharedResourceFound = true;
						ret = sr;
						break;
					}
				}

				if (true == bSharedResourceFound)
					break;
			}
		}

		if (false == bFoundTenant) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}

		if (false == bSharedResourceFound) {
			throw new SharedResourceNotFoundException(
					ACISizerConstant.SHARED_RESOURCE_NOT_FOUND_WITH_ID);
		}

		
		return outputMapping(ret, new SharedResourceUi(),currTenant);
	}

	public List<SharedResourceUi> getSharedResources(int projectId, int tenantId, int vrfId) throws AciEntityNotFound {
		List<SharedResourceUi> ret = new ArrayList<SharedResourceUi>();

		ProjectTable proj = m_projrepo.getOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		boolean bFoundTenant = false;
		boolean bFoundVrf = false;
		Tenant currTenant=null;

		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		for (Tenant tenant : logicalRequirement.getTenants()) {
			if (tenantId == tenant.getId()) {
				bFoundTenant = true;
currTenant=tenant;
				// validate vrf id is available
				for (Vrf iter : tenant.getVrfs()) {
					if (vrfId == iter.getId()) {
						bFoundVrf = true;
						break;
					}
				}

				if (false == bFoundVrf) {
					throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
				}

				if (null == tenant.getSharedResources()) {
					throw new SharedResourceNotFoundException(ACISizerConstant.SHARED_RESOURCE_NOT_CREATED);
				}

				for (SharedResource sr : tenant.getSharedResources()) {
					if (Integer.parseInt(sr.getName()) == vrfId)
						ret.add(outputMapping(sr, new SharedResourceUi(),currTenant));
				}

			}
		}

		if (false == bFoundTenant) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}

		return ret;
	}

}
