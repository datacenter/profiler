package com.cisco.applicationprofiler.helper;

import java.util.ArrayList;
import java.util.List;

import com.cisco.applicationprofiler.models.Application;
import com.cisco.applicationprofiler.models.Bd;
import com.cisco.applicationprofiler.models.Epg;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.Subnets;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.UIData;
import com.cisco.applicationprofiler.models.Vrf;
import com.cisco.applicationprofiler.ui.models.ApplicationTemplate;
import com.cisco.applicationprofiler.util.ACISizerConstant;

public class BdHelper {
	
	public static Bd createBd(ApplicationTemplate appTemplate, LogicalSummary logicalSummary, Tenant tenant, Tenant commonTenant, int subnets)
	{
		Bd newBd = null;
		Tenant tenantTarget=null;
		if (0 == appTemplate.getConfiguration().getBdPolicy()
				.compareToIgnoreCase(ACISizerConstant.TEMPLATE_CONF_UNIQUE)) {
			/*Optional<Vrf> targetVrf = targetTenant.getVrfs().stream()
					.filter(vrfIter -> (true == vrfIter.getDisplayName().equals(ACISizerConstant.TEMPLATE_VRF_NAME)))
					.findFirst();
			if (!targetVrf.isPresent()) {
				return newBd;
			}*/
			Vrf targetVrf = null;
			if (tenant.isLocalL3out() && tenant.isLocalVrf()) {
				tenantTarget=tenant;
				targetVrf=TenantHelper.getDefaultVrf(tenant);
				if (targetVrf == null) {
					targetVrf=TenantHelper.addLocalVrfBd(logicalSummary, tenant);
				}
			}else{
				tenantTarget=commonTenant;
				targetVrf=TenantHelper.getDefaultVrf(commonTenant);
			}

			int bdId = logicalSummary.generateUniqueId();
			String bdDisplayName = "BD" + appTemplate.getName();
			newBd = generateNewBd(bdId,bdDisplayName, appTemplate, tenantTarget,subnets, targetVrf);

			int count = 0;
			while(false == tenantTarget.getBds().add(newBd))
			{
				String displayName = bdDisplayName+"-"+count++;
				newBd = generateNewBd(bdId,displayName, appTemplate, tenantTarget,subnets, targetVrf);
			}
		} else {
			if (tenant.isLocalL3out() &&  tenant.isLocalVrf()) {
				for (Bd bd : tenant.getBds()) {
					if (isDefault(bd)) {
						newBd = bd;
					}
				}
				if(newBd==null){
					newBd=TenantHelper.addLocalBd(logicalSummary, tenant);
				}
			} else {
				for (Bd bd : commonTenant.getBds()) {
					if (isDefault(bd)) {
						newBd = bd;
					}
				}
			}
		}

		return newBd;
	}

	/**
	 * @param bd
	 * @return
	 */
	private static boolean isDefault(Bd bd) {
		return ACISizerConstant.DEFAULT.equals(bd.getType());
	}

	private static Bd generateNewBd(int bdId, String bdDisplayName,ApplicationTemplate appTemplate,
			Tenant targetTenant, int subnets,
			Vrf targetVrf) {
		Bd newBd;
		newBd = new Bd(bdId, bdDisplayName);
		newBd.setOwnershipId(targetTenant.getId());
		newBd.setSubnets(new Subnets(subnets));
		// newBd.setSuffix(null);
		newBd.setUiData(new UIData(10, 10));
		newBd.setVrf(targetVrf.getName());
		newBd.setNoOfInstances(appTemplate.getNoOfInstances());
		return newBd;
	}
	
	public static Bd getBdbyName(String name,Tenant tenant){
		for(Bd bd:tenant.getBds()){
			if(name.equals(bd.getName())){
				return bd;
			}
		}
		return null;
		
	}

	public static void updateUniqueBdInstance(Application oldApp, Application newApp,
			Tenant currTenant) {
		
		Bd targetBd = findTargetBdPerApplication(newApp, currTenant);
		if(null != targetBd)
		{
			int effectiveInstance = targetBd.getNoOfInstances()-oldApp.getInstances()+newApp.getInstances();
			targetBd.setNoOfInstances(effectiveInstance);
		}
		
		
	}

	public static Bd findTargetBdPerApplication(Application targetApp, Tenant targetTenant)
	{
		Bd foundBd = null;
		if(null == targetApp.getModel() || 0 == targetApp.getModel().compareToIgnoreCase(ACISizerConstant.TEMPLATE_APP_MODEL_NO_TEMPLATE))
		{
			return foundBd;
		}
		
		Epg targetEpg = null;
		
		if(null != targetApp.getConfiguration() && 0 == targetApp.getConfiguration().getBdPolicy().compareToIgnoreCase(ACISizerConstant.TEMPLATE_CONF_UNIQUE))
		{
			for(Epg iter : targetTenant.getEpgs())
			{
				if(0 == iter.getApp().compareToIgnoreCase(targetApp.getName()))
				{
					targetEpg = iter;
					break;
				}
			}
			
			if(null != targetEpg)
			{
				for(Bd iter : targetTenant.getBds())
				{
					if(0 == iter.getName().compareToIgnoreCase(targetEpg.getBd()))
					{
						foundBd = iter;
						break;
					}
				}
			}
		}
		
		return foundBd;
	}
	
	public static List<Bd> getAllBdsOfTenantAndCommon(Tenant tenant,Tenant commonTenant){
		ArrayList<Bd> temp=new ArrayList<>();
		for (Bd bd : tenant.getBds()) {
			temp.add(bd);
		}
		if(ACISizerConstant.TENANT_TYPE_USER.equals(tenant.getType())){
			for (Bd bd : commonTenant.getBds()) {
					temp.add(bd);
			}
		}
		
		return temp;
		
	}
	
}
