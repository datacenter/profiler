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
import com.cisco.applicationprofiler.exceptions.ForbiddenOperationException;
import com.cisco.applicationprofiler.exceptions.L3OutNameExistsException;
import com.cisco.applicationprofiler.helper.TenantHelper;
import com.cisco.applicationprofiler.models.EpgPrefixes;
import com.cisco.applicationprofiler.models.L3out;
import com.cisco.applicationprofiler.models.LogicalRequirement;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.Subnets;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.Vrf;
import com.cisco.applicationprofiler.repo.ProjectsRepository;
import com.cisco.applicationprofiler.ui.models.L3outUi;
import com.cisco.applicationprofiler.util.ACISizerConstant;
import com.cisco.applicationprofiler.util.Utility;
import com.google.gson.Gson;

/**
 * @author Deepa
 *
 */
@Service
public class L3OutServices {

	@Inject
	private ProjectsRepository projrepo;
	
	@Inject
	private Gson gson;
	
	@Inject
	private ProjectServicesAci projectServices;

	private void inputMapping(com.cisco.applicationprofiler.ui.models.L3outUi src, L3out dest) {
		dest.setCount(src.getCount());

		Subnets subnets = new Subnets();
		subnets.setIpv4(src.getSubnets());
		dest.setSubnets(subnets);

		dest.setUiData(src.getUiData());
		dest.setVrf("" + src.getVrfId());
		commonMapping(src, dest);
	}

	/**
	 * @param src
	 * @param dest
	 */
	private void commonMapping(com.cisco.applicationprofiler.ui.models.L3outUi src, L3out dest) {
		dest.setSvis(src.getSvis());
		dest.setSubifs(src.getSubInterfaces());
		dest.setSpan(src.getSpan());
		EpgPrefixes epgPrefixes = new EpgPrefixes();
		epgPrefixes.setIpv4(src.getEpgPrefixes());
		dest.setEpg_prefixes(epgPrefixes);
		dest.setOwnershipId(src.getOwnershipId());
	}

	public com.cisco.applicationprofiler.ui.models.L3outUi outputMapping(L3out src, com.cisco.applicationprofiler.ui.models.L3outUi dest, Vrf vrf,
			Tenant currTenant) {
		dest.setCount(src.getCount());
		dest.setFullName(currTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + src.getDisplayName());
		dest.setId(Integer.parseInt(src.getName()));
		dest.setName(src.getDisplayName());
		dest.setSpan(src.getSpan());
		dest.setSubInterfaces(src.getSubifs());
		if (src.getSubnets() != null) {
			dest.setSubnets(src.getSubnets().getIpv4());
		}
		dest.setSvis(src.getSvis());
		dest.setUiData(src.getUiData());
		dest.setVrfId(Integer.parseInt(vrf.getName()));
		if (src.getEpg_prefixes() != null) {
			dest.setEpgPrefixes(src.getEpg_prefixes().getIpv4());
		}
		dest.setVrfName(currTenant.getDisplayName() + ACISizerConstant.TENANT_FIELD_SEPERATOR + vrf.getDisplayName());
		dest.setOwnershipId(src.getOwnershipId());
		return dest;

	}

	public com.cisco.applicationprofiler.ui.models.L3outUi addL3Out(int projectId, int tenantId, int vrfId,
			com.cisco.applicationprofiler.ui.models.L3outUi l3out) throws AciEntityNotFound {
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);

		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// update the summary also
		// convert it to java obj
		LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		int count = logicalSummary.getL3OutCount();
		logicalSummary.setL3OutCount(count + 1);
		int uniqueId = logicalSummary.generateUniqueId();

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);

		final Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		TenantHelper.modifiedTime(currTenant);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID);
		}
		Vrf currVrf = Utility.getCurrentVrf(vrfId, currTenant);
		if (currVrf == null) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
		L3out l3outToAdd = new L3out(uniqueId, l3out.getName());
		inputMapping(l3out, l3outToAdd);
		l3outToAdd.setCount(1);
		l3outToAdd.setVrf(currVrf.getName());
		if (!currTenant.getL3outs().add(l3outToAdd)) {
			throw new L3OutNameExistsException(
					"L3Out name " + l3outToAdd.getDisplayName() + " already exists in the system");
		}

/*		String updatedTenants = gson.toJson(logicalRequirement);
		proj.setLogicalRequirement(updatedTenants);

		// convert back to json string
		proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));*/

		// set it back to the project and update db
		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement, logicalSummary);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			return outputMapping(l3outToAdd, new com.cisco.applicationprofiler.ui.models.L3outUi(), currVrf, currTenant);
		} else {
			return null;
		}

	}

	public com.cisco.applicationprofiler.ui.models.L3outUi getL3Out(int projectId, int tenantId, int vrfId, int l3OutId)
			throws AciEntityNotFound {
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
		Vrf currVrf = Utility.getCurrentVrf(vrfId, currTenant);
		if (currVrf == null) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}
/*		Optional<L3out> l3outOptional = currTenant.getL3outs().stream().filter(l3out -> l3out.getId() == l3OutId)
				.findFirst();
		if (!l3outOptional.isPresent()) {
			throw new AciEntityNotFound(ACISizerConstant.L3_OUT_NOT_FOUND_WITH_ID);
		}
		L3out l3outequired = l3outOptional.get();
*/		
		L3out l3outequired = null;
		for(L3out iter : currTenant.getL3outs())
		{
			if(iter.getId() == l3OutId)
			{
				l3outequired = iter;
				break;
			}
		}
		if(null == l3outequired)
			throw new AciEntityNotFound(ACISizerConstant.L3_OUT_NOT_FOUND_WITH_ID);
		

		return outputMapping(l3outequired, new com.cisco.applicationprofiler.ui.models.L3outUi(), currVrf, currTenant);

	}

	public List<L3outUi> getL3Outs(int projectId, int tenantId, int vrfId)
			throws AciEntityNotFound {
		// get the tenants by project id
		ProjectTable proj = projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID);
		}

		// convert it to java obj
		LogicalRequirement logicalRequirement = gson.fromJson(proj.getLogicalRequirement(), LogicalRequirement.class);

		Tenant currTenant = Utility.getCurrentTenant(tenantId, logicalRequirement);
		if (currTenant == null) {
			throw new AciEntityNotFound(ACISizerConstant.TENANT_NOT_FOUND_WITH_ID );
		}

		Vrf currVrf = Utility.getCurrentVrf(vrfId, currTenant);
		if (currVrf == null) {
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}

/*		return currTenant.getL3outs().parallelStream().map(
				l3outIter -> outputMapping(l3outIter, new com.cisco.acisizer.ui.models.L3outUi(), currVrf, currTenant))
				.collect(Collectors.toList());
*/	
		List<L3outUi> l3outList = new ArrayList<L3outUi>();
		for(L3out iter : currTenant.getL3outs())
		{
			if(currVrf.getName().equals(iter.getVrf()))
				l3outList.add(outputMapping(iter, new L3outUi(), currVrf, currTenant));
		}
		return l3outList;
	}

	public int deleteL3Out(int projectId, int tenantId, int vrfId, int l3OutId) throws AciEntityNotFound {
		// get the l3Outs by project id

		if (l3OutId == ACISizerConstant.DEFAULT_L3OUT_ID) {
			throw new DefaultNodeException("Cannot delete default L3out '" + ACISizerConstant.DEFAULT_L3OUT_NAME+"'");
		}

		ProjectTable proj = projrepo.findOne(projectId);
		if (proj == null) {
			throw new AciEntityNotFound(ACISizerConstant.COULD_NOT_FIND_THE_PROJECT_FOR_ID + projectId);
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
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}

/*		Optional<L3out> l3Optional = currTenant.getL3outs().stream().filter(l3out -> l3out.getId() == l3OutId)
				.findFirst();
		if (!l3Optional.isPresent()) {
			throw new AciEntityNotFound(ACISizerConstant.L3_OUT_NOT_FOUND_WITH_ID);
		}
*/
		L3out target = null;
		for(L3out iter : currTenant.getL3outs())
		{
			if(l3OutId == iter.getId())
			{
				target = iter;
				break;
			}
		}
		
		if(null == target)
			throw new AciEntityNotFound(ACISizerConstant.L3_OUT_NOT_FOUND_WITH_ID);
		
		currTenant.getL3outs().remove(target);

		/*String updatedTenants = gson.toJson(logicalRequirement);

		// set it back to the project and update db
		proj.setLogicalRequirement(updatedTenants);*/

		// update the summary also
		// convert it to java obj
		/*LogicalSummary logicalSummary = gson.fromJson(proj.getLogicalRequirementSummary(), LogicalSummary.class);
		int count = logicalSummary.getL3OutCount();
		logicalSummary.setL3OutCount(count - 1);*/

		// convert back to json string
		//proj.setLogicalRequirementSummary(gson.toJson(logicalSummary));

		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			return 1;
		} else {
			return 0;
		}

	}

	public com.cisco.applicationprofiler.ui.models.L3outUi updateL3Out(int projectId, int tenantId, int vrfId, int l3OutId,
			com.cisco.applicationprofiler.ui.models.L3outUi l3out) throws AciEntityNotFound, ForbiddenOperationException {
 
		if(l3OutId==ACISizerConstant.DEFAULT_L3OUT_ID){
			throw new ForbiddenOperationException("default l3out cannot be deleted");
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
			throw new AciEntityNotFound(ACISizerConstant.VRF_NOT_FOUND_WITH_ID);
		}

/*		Optional<L3out> l3Optional = currTenant.getL3outs().stream().filter(l3outIter -> l3outIter.getId() == l3OutId)
				.findFirst();
		if (!l3Optional.isPresent()) {
			throw new AciEntityNotFound(ACISizerConstant.L3_OUT_NOT_FOUND_WITH_ID);
		}
		L3out l3outToRemove = l3Optional.get();
*/
		L3out l3outToRemove = null;
		for(L3out iter : currTenant.getL3outs())
		{
			if(l3OutId == iter.getId())
			{
				l3outToRemove = iter;
				break;
			}
		}
		
		if(null == l3outToRemove)
			throw new AciEntityNotFound(ACISizerConstant.L3_OUT_NOT_FOUND_WITH_ID);
		
		currTenant.getL3outs().remove(l3outToRemove);

		L3out l3outToAdd = new L3out(l3outToRemove.getId(), l3out.getName());
		l3outToAdd.copyL3out(l3outToRemove);

		commonMapping(l3out, l3outToAdd);

		if (!currTenant.getL3outs().add(l3outToAdd)) {
			throw new L3OutNameExistsException("L3Out name " + l3out.getName() + " already exists in the system");
		}

		/*String updatedTenants = gson.toJson(logicalRequirement);

		// set it back to the project and update db
		proj.setLogicalRequirement(updatedTenants);*/

		ProjectTable returnProj = projectServices.saveProject(proj, logicalRequirement);
		//projectServices.callSizingSummary(projectId);

		if (returnProj != null) {
			return outputMapping(l3outToAdd, new com.cisco.applicationprofiler.ui.models.L3outUi(), currVrf, currTenant);
		} else {
			return null;
		}
	}
}
