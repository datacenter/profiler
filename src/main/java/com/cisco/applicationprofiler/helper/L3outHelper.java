/**
 * 
 */
package com.cisco.applicationprofiler.helper;

import java.util.ArrayList;
import java.util.List;

import com.cisco.applicationprofiler.models.L3out;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.Vrf;
import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public class L3outHelper {

	public static L3out getPreferredL3out(LogicalSummary logicalSummary, Tenant targetTenant, Tenant commonTenant) {

		L3out l3out = null;
		Vrf targetVrf = null;

		if (targetTenant.isLocalVrf()&&targetTenant.isLocalL3out()) {
			l3out = TenantHelper.getDefaultL3out(targetTenant);
			if (l3out == null) {
				targetVrf = TenantHelper.getDefaultVrf(targetTenant);
				if (targetVrf != null ) {
					l3out = TenantHelper.addLocalL3out(logicalSummary, targetTenant, targetVrf);
				}
			}
			if (l3out == null) {
				l3out = TenantHelper.addLocalL3out(logicalSummary, targetTenant);
			}

		} else if(targetTenant.isLocalVrf()&&!targetTenant.isLocalL3out()) {
			l3out = TenantHelper.getDefaultL3outCommon(commonTenant, targetTenant);
		}else if(!targetTenant.isLocalVrf()&&targetTenant.isLocalL3out()){
			l3out=TenantHelper.getDefaultL3outLocalInCommon(commonTenant, targetTenant);
			if(l3out==null){
				l3out=TenantHelper.addCommonL3out(logicalSummary, commonTenant, targetTenant);
			}
		} 
		else if(!targetTenant.isLocalVrf()&&!targetTenant.isLocalL3out())
		{
			l3out = TenantHelper.getDefaultL3outCommon(commonTenant, targetTenant);
		}
		return l3out;
	}
	
	public static List<L3out> getAllL3outsOfTenantAndCommon(Tenant tenant,Tenant commonTenant){
		ArrayList<L3out> temp=new ArrayList<>();
		for (L3out l3out : tenant.getL3outs()) {
			temp.add(l3out);
		}
		if(ACISizerConstant.TENANT_TYPE_USER.equals(tenant.getType())){
			for (L3out l3out : commonTenant.getL3outs()) {
					temp.add(l3out);
			}
		}
		return temp;
	}
}
