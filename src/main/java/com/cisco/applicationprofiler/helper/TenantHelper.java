/**
 * 
 */
package com.cisco.applicationprofiler.helper;

import java.sql.Timestamp;
import java.util.Date;

import com.cisco.applicationprofiler.models.Bd;
import com.cisco.applicationprofiler.models.EpgPrefixes;
import com.cisco.applicationprofiler.models.L3out;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.Subnets;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.Vrf;
import com.cisco.applicationprofiler.services.applicationTemplates.TemplateConstants;
import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public class TenantHelper {

	public static final String L3 = "L3-";

	/**
	 * @param logicalSummary
	 * @param tenant
	 * @return
	 */
	public static Vrf addLocalVrfBdL3out(LogicalSummary logicalSummary, Tenant tenant) {
		Vrf newVrf = addLocalVrfBd(logicalSummary, tenant);
		addLocalL3out(logicalSummary, tenant, newVrf);
		return newVrf;
	}

	/**
	 * @param logicalSummary
	 * @param tenant
	 * @param newVrf
	 */
	public static L3out addLocalL3out(LogicalSummary logicalSummary, Tenant tenant, Vrf newVrf) {
		L3out newL3out = new L3out(logicalSummary.generateUniqueId(), ACISizerConstant.TEMPLATE_L3OUT_NAME);
		newL3out.setSubifs(TemplateConstants.getL3outcomplexity().get(tenant.getL3outComplexity()));
		return addL3outToTenant(tenant, newVrf, newL3out);
	}
	
	/**
	 * @param logicalSummary
	 * @param tenant
	 * @param newVrf
	 */
	public static L3out addCommonL3out(LogicalSummary logicalSummary, Tenant commonTenant,Tenant targetTenant) {
		Vrf newVrf=getDefaultVrf(commonTenant);
		L3out newL3out = new L3out(logicalSummary.generateUniqueId(), L3+targetTenant.getDisplayName());
		newL3out.setSubifs(TemplateConstants.getL3outcomplexity().get(targetTenant.getL3outComplexity()));
		newL3out.setOwnershipId(targetTenant.getId());
		return addL3outToTenant(commonTenant, newVrf, newL3out);
	}

	/**
	 * @param tenant
	 * @param newVrf
	 * @param newL3out
	 * @return
	 */
	private static L3out addL3outToTenant(Tenant tenant, Vrf newVrf, L3out newL3out) {
		newL3out.setVrf(newVrf.getName());
		newL3out.setEpg_prefixes(new EpgPrefixes(1));
		newL3out.setSvis(1);
		newL3out.setType(ACISizerConstant.DEFAULT);
		tenant.getL3outs().add(newL3out);
		return newL3out;
	}

	/**
	 * @param logicalSummary
	 * @param tenant
	 * @return
	 */
	public static Vrf addLocalVrfBd(LogicalSummary logicalSummary, Tenant tenant) {
		Vrf newVrf = addLocalVrf(logicalSummary, tenant);

		addLocalBd(logicalSummary, tenant, newVrf);
		return newVrf;
	}

	/**
	 * @param logicalSummary
	 * @param tenant
	 * @return
	 */
	public static Vrf addLocalVrf(LogicalSummary logicalSummary, Tenant tenant) {
		Vrf newVrf = new Vrf(logicalSummary.generateUniqueId(), ACISizerConstant.TEMPLATE_VRF_NAME);
		newVrf.setType(ACISizerConstant.DEFAULT);
		tenant.getVrfs().add(newVrf);
		return newVrf;
	}

	/**
	 * @param logicalSummary
	 * @param tenant
	 * @param newVrf
	 */
	public static Bd addLocalBd(LogicalSummary logicalSummary, Tenant tenant, Vrf newVrf) {
		Bd newBd = new Bd(logicalSummary.generateUniqueId(), ACISizerConstant.TEMPLATE_BD_NAME);
		newBd.setSubnets(new Subnets(1));
		newBd.setVrf(newVrf.getName());
		newBd.setOwnershipId(tenant.getId());
		newBd.setType(ACISizerConstant.DEFAULT);
		tenant.getBds().add(newBd);
		return newBd;
	}

	public static Bd addLocalBd(LogicalSummary logicalSummary, Tenant tenant) {
		Vrf targetVrf = null;
		Bd targetBd = null;
		targetVrf = getDefaultVrf(tenant);
		if (targetVrf == null) {
			targetVrf = addLocalVrf(logicalSummary, tenant);
		}
		targetBd = addLocalBd(logicalSummary, tenant, targetVrf);

		return targetBd;

	}

	public static L3out addLocalL3out(LogicalSummary logicalSummary, Tenant tenant) {
		Vrf targetVrf = null;
		L3out targetL3out = null;
		targetVrf = getDefaultVrf(tenant);
		if (targetVrf == null) {
			targetVrf = addLocalVrf(logicalSummary, tenant);
		}
		targetL3out = addLocalL3out(logicalSummary, tenant, targetVrf);

		return targetL3out;

	}

	/**
	 * @param tenant
	 * @param targetVrf
	 * @return
	 */
	public static Vrf getDefaultVrf(Tenant tenant) {
		for (Vrf vrf : tenant.getVrfs()) {
			if (ACISizerConstant.DEFAULT.equals(vrf.getType())) {
				return vrf;
			}
		}
		return null;
	}

	public static L3out getDefaultL3out(Tenant tenant) {
		for (L3out l3out : tenant.getL3outs()) {
			if (ACISizerConstant.DEFAULT.equals(l3out.getType())) {
				return l3out;
			}
		}
		return null;
	}
	
	public static L3out getDefaultL3outLocalInCommon(Tenant common,Tenant target) {
		for (L3out l3out : common.getL3outs()) {
			if (ACISizerConstant.DEFAULT.equals(l3out.getType()) && target.getId()==l3out.getOwnershipId()) {
				return l3out;
			}
		}
		return null;
	}
	
	public static L3out getDefaultL3outCommon(Tenant common,Tenant target) {
		for (L3out l3out : common.getL3outs()) {
			if (ACISizerConstant.DEFAULT.equals(l3out.getType())) {
				return l3out;
			}
		}
		return null;
	}
	
	public static void modifiedTime(Tenant tenantAdded){
		tenantAdded.setModifiedTime(new Timestamp(new Date().getTime()));		
	}
	
	
}
