/**
 * 
 */
package com.cisco.applicationprofiler.services.applicationTemplates;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.cisco.applicationprofiler.exceptions.ApplicationNameExistsException;
import com.cisco.applicationprofiler.helper.BdHelper;
import com.cisco.applicationprofiler.helper.L3outHelper;
import com.cisco.applicationprofiler.logical.services.ContractServicesAcii;
import com.cisco.applicationprofiler.logical.services.EpgServices;
import com.cisco.applicationprofiler.models.ACISizerModel;
import com.cisco.applicationprofiler.models.Application;
import com.cisco.applicationprofiler.models.Bd;
import com.cisco.applicationprofiler.models.Contract;
import com.cisco.applicationprofiler.models.Epg;
import com.cisco.applicationprofiler.models.Eps;
import com.cisco.applicationprofiler.models.L3out;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.SharedResource;
import com.cisco.applicationprofiler.models.Subnets;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.models.UIData;
import com.cisco.applicationprofiler.ui.models.ApplicationTemplate;
import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
@Component
public class ApplicationTemplateImpl extends AbstractApplicationTemplate {

	@Inject
	private ContractServicesAcii contractServices;
	
	@Inject
	private EpgServices epgServices;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cisco.acisizer.services.applicationTemplates.ApplicationTemplate#
	 * addApplication()
	 */
	@Override
	public Application addApplication(ApplicationTemplate appTemplate, Tenant targetTenant, Tenant commonTenant,
			LogicalSummary logicalSummary) {

		Bd newBd = null;
		int subnets = 1;
		Application app = null;

		//the default subnet is 1
		//subnets = appModelToNoOfEpgs.get(appTemplate.getModel());
		app = new Application(logicalSummary.generateUniqueId(),appTemplate.getName());
		app.setInstances(appTemplate.getNoOfInstances());
		app.setConfiguration(appTemplate.getConfiguration());
		app.setModel(appTemplate.getModel());
		if (appTemplate.getModel().equals(ACISizerConstant.TEMPLATE_APP_MODEL_2TIER)) {
			app.setUiData(new UIData(340f, 50f));
		} else {
			app.setUiData(new UIData(310f, 30f));
		}
		if(!targetTenant.getApps().add(app)){
			throw new ApplicationNameExistsException("Application name \"" + app.getDisplayName() + "\" already exists");
		}
		
		if (appTemplate.getModel().equals(ACISizerConstant.TEMPLATE_APP_MODEL_NO_TEMPLATE))
			return app;
		
		int bdSubnets = 0;
		if(0 == appTemplate.getConfiguration().getSubnetPolicy().compareToIgnoreCase(ACISizerConstant.TEMPLATE_CONF_UNIQUE))
		{
			bdSubnets = appModelToNoOfEpgs.get(appTemplate.getModel()) * appTemplate.getNoOfInstances();
		}
		else if(0 == appTemplate.getConfiguration().getSubnetPolicy().compareToIgnoreCase(ACISizerConstant.TEMPLATE_CONF_DEFAULT))
		{
			bdSubnets = 1;
			if(0 == appTemplate.getConfiguration().getBdPolicy().compareToIgnoreCase(ACISizerConstant.TEMPLATE_CONF_DEFAULT))
				bdSubnets = 0;
		}
		newBd = BdHelper.createBd(appTemplate, logicalSummary, targetTenant,commonTenant,bdSubnets);
		
		List<Epg> epgCreatedList = new ArrayList<>();

		for (int i = 0; i < appModelToNoOfEpgs.get(appTemplate.getModel()); i++) {
			Epg epgApp = createEpg(appTemplate, targetTenant, commonTenant, logicalSummary, app,
					appModelToSuffixes.get(appTemplate.getModel()).get(i)
							+ ACISizerConstant.TEMPLATE_EPG_DISPLAY_NAME_TIER,
					subnets, newBd, appModelSuffixesToUidata.get(appTemplate.getModel()+appModelToSuffixes.get(appTemplate.getModel()).get(i)));
			epgCreatedList.add(epgApp);
		}



		if (epgCreatedList.size() > 1) {

			int filterCount = contractComplexity.get(appTemplate.getConfiguration().getContractComplexity());
			for (int i = 0; i < epgCreatedList.size(); i++) {
				for (int j = i + 1; j < epgCreatedList.size(); j++) {
					createContract(targetTenant, commonTenant, logicalSummary, app, epgCreatedList.get(i),
							epgCreatedList.get(j), "ct" + suffixes.get(i) + suffixes.get(j), filterCount,appModelSuffixesToUidata.get(appTemplate.getModel()+suffixes.get(i) + suffixes.get(j)));
				}

			}
		}

		if (epgCreatedList.size() > 0) {
			if (appTemplate.getConfiguration().isL3outEnabled()) {
				L3out l3out = L3outHelper.getPreferredL3out(logicalSummary, targetTenant, commonTenant);
/*
 				Integer subIfCount = TemplateConstants.getL3outcomplexity().get(appTemplate.getConfiguration().getL3outComplexity());
				if(l3out.getSubifs()<subIfCount){
					l3out.setSubifs(subIfCount);
				}
*/				
				createContract(targetTenant, commonTenant, logicalSummary, app,
						epgCreatedList.get(epgCreatedList.size() - 1), l3out,
						ACISizerConstant.TEMPLATE_CONTRACT_NAME_L3OUT, 1, appModelSuffixesToUidata
								.get(appTemplate.getModel() + ACISizerConstant.TEMPLATE_CONTRACT_NAME_L3OUT));
			}
		}
		
		return app;
	}



	private Contract createContract(Tenant targetTenant, Tenant commonTenant, LogicalSummary logicalSummary,
			Application app, Epg srcEpg, ACISizerModel consumer, String contractName, int filterCount, UIData uiData) {
		Contract newContract = new Contract(logicalSummary.generateUniqueId(), contractName, app.getName());
		newContract.setProviderType(ACISizerConstant._epg);
		newContract.setProviderId(srcEpg.getName());
		newContract.setUnique_filters(filterCount);
		newContract.setUiData(uiData);
		newContract.setProviderEnforced(true);
		
		if (true == consumer.getClass().equals(L3out.class)) {
			newContract.setConsumerType(ACISizerConstant._l3out);
		} else if (true == consumer.getClass().equals(SharedResource.class)) {
			newContract.setConsumerType(ACISizerConstant._shared_resource);
		} else if (true == consumer.getClass().equals(Epg.class)) {
			newContract.setConsumerType(ACISizerConstant._epg);
		}
		newContract.setConsumerId(consumer.getName());

		contractServices.addContract(newContract, commonTenant, targetTenant, app, logicalSummary, app);

		return newContract;
	}

	

	private Epg createEpg(ApplicationTemplate appTemplate, Tenant targetTenant, Tenant commonTenant,
			LogicalSummary logicalSummary, Application app, String epgDisplayName, int srcSubnets, Bd targetBd, UIData uiData) {
		Epg epg = new Epg(logicalSummary.generateUniqueId(), epgDisplayName, app.getName());
		epg.setUiData(uiData);
		epg.setEps(new Eps(epgComplexity.get(appTemplate.getConfiguration().getEpgComplexity())));
		epg.setSpan(2);
		epg.setSubnets(new Subnets(srcSubnets));

		/*if (null == targetBd) {
			int subnets = 0;
			if (0 == appTemplate.getConfiguration().getSubnetPolicy()
					.compareToIgnoreCase(ACISizerConstant.TEMPLATE_CONF_UNIQUE)) {
				subnets = srcSubnets;
			} else {
				subnets = 1;// for default subnet
			}
			Optional<Bd> currBd = targetTenant.getBds().stream()
					.filter(bdIter -> (true == bdIter.getDisplayName().equals(ACISizerConstant.TEMPLATE_BD_NAME)))
					.findFirst();
			Bd currBd=null;
			for (Bd bd : targetTenant.getBds()) {
				if(bd.getDisplayName().equals(ACISizerConstant.TEMPLATE_BD_NAME)){
					currBd=bd;
					break;
				}
			}
			
			if (currBd!=null) {
				targetBd = currBd;
				targetBd.setSubnets(new Subnets(subnets));
				epg.setBd(targetBd.getName());
			}

			// fallback if the template bd name has been changed
			if (null == targetBd) {
				Optional<Bd> defaultBd = targetTenant.getBds().stream()
						.filter(bdIter -> (true == bdIter.getDisplayName().equals(ACISizerConstant.BD_DEFAULT)))
						.findFirst();
				Bd defaultBd=null;
				for (Bd bd : targetTenant.getBds()) {
					if(bd.getDisplayName().equals(ACISizerConstant.BD_DEFAULT)){
						defaultBd=bd;
						break;
					}
				}
				
				if (defaultBd!=null) {
					targetBd = defaultBd;
					targetBd.setSubnets(new Subnets(srcSubnets));
					epg.setBd(commonTenant.getId() + ACISizerConstant.TENANT_FIELD_SEPERATOR + targetBd.getId());
				}

			}
		} else {*/

			if(0 == targetBd.getVrf().compareToIgnoreCase(ACISizerConstant.DEFAULT_VRF_NAME))//this means the bd resides in common tenant
				epg.setBd(ACISizerConstant.DEFAULT_TENANT_ID + ACISizerConstant.TENANT_FIELD_SEPERATOR + targetBd.getId());
			else
				epg.setBd("" + targetBd.getId());
		//}
		
		if (appTemplate.getConfiguration().isSharedServiceEnabled()) 
		{
			int filterCount = contractComplexity.get(appTemplate.getConfiguration().getContractComplexity());
			for (SharedResource iter : commonTenant.getSharedResources()) 
			{
				// assuming that there is only one shared resource in the
				// common tenant
				epg.setEnableSharedReource(true);
				epg.setSharedResourceId(iter.getId());
				epg.setFilterCount(filterCount);
				break;
			}
		}
		
		
		epgServices.addEpg(epg, targetTenant,commonTenant, logicalSummary, app);

		return epg;
	}


}
