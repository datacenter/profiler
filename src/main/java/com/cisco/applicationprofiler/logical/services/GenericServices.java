package com.cisco.applicationprofiler.logical.services;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.applicationprofiler.exceptions.AciEntityNotFound;
import com.cisco.applicationprofiler.generic.GenericRequestBody;
import com.cisco.applicationprofiler.generic.GenericResponseBd;
import com.cisco.applicationprofiler.models.Bd;
import com.cisco.applicationprofiler.models.GenericQueryModel;
import com.cisco.applicationprofiler.models.GenericResponseModel;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.util.ACISizerConstant;
@Service
public class GenericServices {

	int m_projectId = -1;
	int m_tenantId = -1;
	int m_vrfId = -1;
	int m_bdId = -1;
	int m_appId = -1;
	int m_epgId = -1;
	int m_contractId = -1;
	int m_l3outId = -1;

	@Autowired
	private TenantServicesAcii m_serviceTenant;	
	
	public ArrayList<GenericResponseModel> executeQuery(GenericQueryModel query) throws AciEntityNotFound {

		initIds(query);

		if(0 == query.getResponseType().compareToIgnoreCase(ACISizerConstant._bd))
		{
			ArrayList<GenericResponseModel> list = new ArrayList<GenericResponseModel>();
			
			List<Tenant> tenants = m_serviceTenant.getTenants(m_projectId);
			
			for(Tenant item : tenants)
			{
				if(m_tenantId == item.getId() || 0 == item.getName().compareToIgnoreCase(ACISizerConstant.TENANT_NAME_COMMON))
				{
					String tenantName = item.getName();
					for(Bd bd : item.getBds())
					{
						list.add(new GenericResponseBd(bd.getId(),tenantName+"\\"+ bd.getName()));
					}
				}
				
			}
			
			
			return list;
			
		}
		return null;
	}

	private void initIds(GenericQueryModel query) {

		for(GenericRequestBody item : query.getQueryList())
		{
			switch(item.getType())
			{
			case ACISizerConstant._project:
				m_projectId = item.getId();
				break;
			case ACISizerConstant._tenant:
				m_tenantId = item.getId();
				break;
			case ACISizerConstant._vrf:
				m_vrfId = item.getId();
				break;
			case ACISizerConstant._bd:
				m_bdId = item.getId();
				break;
			case ACISizerConstant._app:
				m_appId = item.getId();
				break;
			case ACISizerConstant._epg:
				m_epgId = item.getId();
				break;
			case ACISizerConstant._contract:
				m_contractId = item.getId();
				break;
			case ACISizerConstant._l3out:
				m_l3outId = item.getId();
				break;
			}
			
		}
		
	}

	public List<String> getFilterTypes() {

		List<String> filters = new ArrayList<String>();
		filters.addAll(getAllFilters());
		return filters;
	}
	
	private Set<String> getAllFilters()
	{
		Set<String> filterTypes = new LinkedHashSet<String>();
		filterTypes.add(ACISizerConstant.FILTER_FTP);
		filterTypes.add(ACISizerConstant.FILTER_HTTP);
		filterTypes.add(ACISizerConstant.FILTER_SMTP);
		filterTypes.add(ACISizerConstant.FILTER_TELNET);
	
		return filterTypes;
	}
}
