/**
 * CopyRight @MapleLabs
 */
package com.cisco.applicationprofiler.logical.services;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.domain.ProjectTable;
import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.exceptions.DefaultNodeException;
import com.cisco.applicationprofiler.exceptions.DeletionNotAllowedException;
import com.cisco.applicationprofiler.exceptions.ForbiddenOperationException;
import com.cisco.applicationprofiler.exceptions.VrfNameExistsException;
import com.cisco.applicationprofiler.helper.TenantHelper;
import com.cisco.applicationprofiler.models.Bd;
import com.cisco.applicationprofiler.models.L3out;
import com.cisco.applicationprofiler.models.LogicalRequirement;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.Vrf;
import com.cisco.applicationprofiler.repo.ProjectsRepository;
import com.cisco.applicationprofiler.ui.models.VrfUi;
import com.cisco.applicationprofiler.util.ACISizerConstant;
import com.cisco.applicationprofiler.util.Utility;
import com.google.gson.Gson;

/**
 * @author Deepa
 *
 */
@Service
public class VrfServices {

	public static final String ÜNENFORCED = "ünenforced";
	public static final String ENFORCED = "enforced";
	public static final String PLEASE_DELETE_THE_ASSOCIATED_BDS_AND_L3OUT_FIRST = "please delete the associated bds and l3out first";
	@Inject
	private ProjectsRepository projrepo;
	
	@Inject
	private Gson gson;
	
	@Inject
	private ProjectServicesAci projectServices;

	private void inputMapping(com.cisco.applicationprofiler.ui.models.VrfUi src, Vrf dest) {
		dest.setUiData(src.getUiData());
		if (src.isEnforced()) {
			dest.setStatus(ENFORCED);
		} else {
			dest.setStatus(ÜNENFORCED);
		}

	}

	public com.cisco.applicationprofiler.ui.models.VrfUi outputmapping(Vrf src, com.cisco.applicationprofiler.ui.models.VrfUi dest,
			Tenant currTenant) {
		dest.setFullName(currTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + src.getDisplayName());
		dest.setId(Integer.parseInt(src.getName()));
		dest.setName(src.getDisplayName());
		dest.setUiData(src.getUiData());
		if(src.getStatus().equals(ENFORCED)){
			dest.setEnforced(true);
		}else{
			dest.setEnforced(false);
		}
		return dest;

	}

	public com.cisco.applicationprofiler.ui.models.VrfUi addVrf(int projectId, int tenantId, com.cisco.applicationprofiler.ui.models.VrfUi vrf)
			throws AciEntityNotFound {

		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		int count = logicalSummary.getVrfCount();
		logicalSummary.setVrfCount(count + 1);
		int uniqueId = logicalSummary.generateUniqueId();

		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);
		// update the summary also
		// convert it to java obj
		Vrf vrfToAdd = new Vrf(uniqueId, vrf.getName());
		inputMapping(vrf, vrfToAdd);
		if (!currTenant.getVrfs().add(vrfToAdd)) {
			throw new VrfNameExistsException("Vrf name " + vrfToAdd.getDisplayName() + " already exists in the system");
		}
		/*String updatedTenants = gson.toJson(logicalRequirement);

		proj.setLogicalRequirement(updatedTenants);

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));*/
		// set it back to the project and update db
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			return outputmapping(vrfToAdd, new com.cisco.applicationprofiler.ui.models.VrfUi(), currTenant);
		} else {
			return null;
		}

	}

	public com.cisco.applicationprofiler.ui.models.VrfUi getVrf(int projectId, int tenantId, int vrfId) throws AciEntityNotFound {
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		Vrf requiredVrf = getTargetVrf(vrfId, currTenant);

		return outputmapping(requiredVrf, new com.cisco.applicationprofiler.ui.models.VrfUi(), currTenant);
	}

	private Vrf getTargetVrf(int vrfId, Tenant currTenant) throws AciEntityNotFound {
/*		Optional<Vrf> vrfOptional = currTenant.getVrfs().stream().filter(vrfIter -> vrfIter.getId() == vrfId)
				.findFirst();
		if (!vrfOptional.isPresent()) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
		Vrf requiredVrf = vrfOptional.get();
		return requiredVrf;
*/	
		Vrf target = null;
		for(Vrf iter : currTenant.getVrfs())
		{
			if(iter.getId() == vrfId);
			{
				target = iter;
				break;
			}
		}
		
		if(null == target)
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		
		return target;
	}

	public List<VrfUi> getVrfs(int projectId, int tenantId) throws AciEntityNotFound {
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}

/*		return currTenant.getVrfs().parallelStream()
				.map(vrf -> outputmapping(vrf, new com.cisco.acisizer.ui.models.VrfUi(), currTenant))
				.collect(Collectors.toList());
*/
		List<VrfUi> vrfs = new ArrayList<VrfUi>();
		for(Vrf vrf : currTenant.getVrfs())
		{
			vrfs.add(outputmapping(vrf, new VrfUi(), currTenant));
		}
		
		return vrfs;
	}

	public int deleteVrf(int projectId, int tenantId, int vrfId) throws DeletionNotAllowedException, AciEntityNotFound {

		if (vrfId == ACISizerConstant.DEFAULT_VRF_ID) {
			throw new DefaultNodeException("Cannot delete default vrf '" + ACISizerConstant.DEFAULT_VRF_NAME+"'");
		}

		// get the vrfs by project id
		ProjectTable proj = projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);
		Tenant currTenant = null;
		for (Tenant tenant : logicalRequirement.getTenants()) {
			// Tenant tenant = gson.fromJson(tenants.get(i), Tenant.class);
			if (tenant.getId() == tenantId) {
				currTenant = tenant;
			}
		}
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		TenantHelper.modifiedTime(currTenant);
		Vrf currVrf = null;
		for (Vrf vrf : currTenant.getVrfs()) {
			if (vrfId == vrf.getId()) {
				currVrf = vrf;
			}
		}
		if (currVrf == null) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
		if (!isDeletePossible(currVrf, currTenant)) {
			throw new DeletionNotAllowedException(PLEASE_DELETE_THE_ASSOCIATED_BDS_AND_L3OUT_FIRST);
		}
		currTenant.getVrfs().remove(currVrf);
		// convert back to json string
		/*String updatedTenants = gson.toJson(logicalRequirement);

		// set it back to the project and update db
		proj.setLogicalRequirement(updatedTenants);*/

		// update the summary also
		// convert it to java obj
		/*LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));*/

		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement);
		//projectServices.callSizingSummary(projectId);
		if (returnProj != null) {
			return 1;
		} else {
			return 0;
		}
	}

	public boolean isDeletePossible(Vrf currVrf, Tenant currTenant) {

		for (Bd bd : currTenant.getBds()) {
			if (bd.getVrf().equals(currVrf.getName())) {
				return false;
			}
		}
		for (L3out l3out : currTenant.getL3outs()) {
			if (l3out.getVrf().equals(currVrf.getName())) {
				return false;
			}
		}

		return true;

	}

	public com.cisco.applicationprofiler.ui.models.VrfUi updateVrf(int projectId, int tenantId, int vrfId,
			com.cisco.applicationprofiler.ui.models.VrfUi vrf) throws AciEntityNotFound, ForbiddenOperationException {
		if(vrfId==ACISizerConstant.DEFAULT_VRF_ID){
			throw new ForbiddenOperationException("default vrf cannot be updated");
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
		Vrf vrfToUpdate = getTargetVrf(vrfId, vrf, currTenant);
		inputMapping(vrf, vrfToUpdate); 
		if (!currTenant.getVrfs().add(vrfToUpdate)) {
			throw new VrfNameExistsException("Vrf name " + vrf.getName() + " already exists in the system");
		}

		/*String updatedTenants = gson.toJson(logicalRequirement);
		//projectServices.callSizingSummary(projectId);

		// set it back to the project and update db
		proj.setLogicalRequirement(updatedTenants);*/

		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement);

		if (returnProj != null) {
			return outputmapping(vrfToUpdate, new com.cisco.applicationprofiler.ui.models.VrfUi(), currTenant);
		} else {
			return null;
		}
	}

	private Vrf getTargetVrf(int vrfId, com.cisco.applicationprofiler.ui.models.VrfUi vrf,
			Tenant currTenant) throws AciEntityNotFound {
/*		Optional<Vrf> vrfOptional = currTenant.getVrfs().stream().filter(vrfiter -> vrfiter.getId() == vrfId)
				.findFirst();
		if (!vrfOptional.isPresent()) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
		currTenant.getVrfs().remove(vrfOptional.get());
		Vrf vrfToUpdate = new Vrf(vrfId, vrf.getName());
		vrfToUpdate.copyVrf(vrfOptional.get());
		return vrfToUpdate;
*/	
		Vrf target = null;
		for(Vrf iter : currTenant.getVrfs())
		{
			if(vrfId == iter.getId())
			{
				target = iter;
				break;
			}
		}
		
		if(null == target)
		{
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}

		currTenant.getVrfs().remove(target);
		Vrf vrfToUpdate = new Vrf(vrfId, vrf.getName());
		vrfToUpdate.copyVrf(target);
		return vrfToUpdate;
		
	}
}
