/**
 * 
 */
package com.cisco.applicationprofiler.apic.models;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Component;

import com.cisco.applicationprofiler.apic.models.FilterEntry.FilterEntryBuilder;
import com.cisco.applicationprofiler.models.Application;
import com.cisco.applicationprofiler.models.Epg;
import com.cisco.applicationprofiler.models.Vrf;

/**
 * @author Mahesh
 *
 */
@Component
public class ProfilerToApicMapper {

	public String mapProfilerToApic(com.cisco.applicationprofiler.models.Tenant profilerTenant) {
		Tenant tenant = mapProfilerTenantToApicTenant(profilerTenant);
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Tenant.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			marshaller.marshal(tenant, arrayOutputStream);
			String tenantXml = arrayOutputStream.toString();
			System.out.println(tenantXml);
			return tenantXml;

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;

	}

	private Tenant mapProfilerTenantToApicTenant(com.cisco.applicationprofiler.models.Tenant profilerTenant) {
		Tenant tenant = new Tenant();
		HashMap<String, String> vrfIdNameMap = new HashMap<String, String>();
		HashMap<String, String> bdIdNameMap = new HashMap<String, String>();
		HashMap<String, App> appApicMap = new HashMap<String, App>();
		HashMap<String, String> contractIdNameMap = new HashMap<String, String>();

		mapContract(profilerTenant, tenant, contractIdNameMap);

		mapApplication(profilerTenant, tenant, appApicMap);

		mapVrf(profilerTenant, tenant, vrfIdNameMap);

		mapBd(profilerTenant, tenant, vrfIdNameMap, bdIdNameMap);

		mapEpg(profilerTenant, bdIdNameMap, appApicMap, contractIdNameMap);

		tenant.setName(profilerTenant.getDisplayName());
		return tenant;
	}

	private void mapEpg(com.cisco.applicationprofiler.models.Tenant profilerTenant, HashMap<String, String> bdIdNameMap,
			HashMap<String, App> appApicMap, HashMap<String, String> contractIdNameMap) {
		for (Epg epgProfiler : profilerTenant.getEpgs()) {
			com.cisco.applicationprofiler.apic.models.Epg epg = new com.cisco.applicationprofiler.apic.models.Epg();
			epg.setName(epgProfiler.getDisplayName());
			BdEpgMap bdEpgMap = new BdEpgMap();
			bdEpgMap.setName(bdIdNameMap.get(epgProfiler.getBd()));
			epg.setBdEpgMap(bdEpgMap);
			mapConsumerToEpg(contractIdNameMap, epgProfiler, epg);

			epg.setProvider(new ArrayList<ProviderConsumer>());
			mapProviderToEpg(contractIdNameMap, epgProfiler, epg);

			App app = appApicMap.get(epgProfiler.getApp());
			app.getEpgs().add(epg);
		}
	}

	private void mapProviderToEpg(HashMap<String, String> contractIdNameMap, Epg epgProfiler,
			com.cisco.applicationprofiler.apic.models.Epg epg) {
		ProviderConsumer provider;
		for (String provideContract : epgProfiler.getProvided_contracts()) {
			provider = new ProviderConsumer();
			provider.setName(contractIdNameMap.get(provideContract));
			epg.getProvider().add(provider);
		}
	}

	private void mapConsumerToEpg(HashMap<String, String> contractIdNameMap, Epg epgProfiler,
			com.cisco.applicationprofiler.apic.models.Epg epg) {
		epg.setConsumer(new ArrayList<ProviderConsumer>());

		ProviderConsumer consumer;
		for (String consumedContract : epgProfiler.getConsumed_contracts()) {
			consumer = new ProviderConsumer();
			consumer.setName(contractIdNameMap.get(consumedContract));
			epg.getConsumer().add(consumer);
		}
	}

	private void mapBd(com.cisco.applicationprofiler.models.Tenant profilerTenant, Tenant tenant,
			HashMap<String, String> vrfIdNameMap, HashMap<String, String> bdIdNameMap) {
		tenant.setBds(new ArrayList<Bd>());
		for (com.cisco.applicationprofiler.models.Bd bdProfiler : profilerTenant.getBds()) {
			Bd bd = new Bd();
			bd.setName(bdProfiler.getDisplayName());
			BdVrfMap bdVrfMap = new BdVrfMap();
			bdVrfMap.setName(vrfIdNameMap.get(bdProfiler.getVrf()));
			bd.setBdVrfMap(bdVrfMap);
			bdIdNameMap.put(bdProfiler.getName(), bdProfiler.getDisplayName());
			tenant.getBds().add(bd);
		}
	}

	private void mapVrf(com.cisco.applicationprofiler.models.Tenant profilerTenant, Tenant tenant,
			HashMap<String, String> vrfIdNameMap) {
		tenant.setVrfs(new ArrayList<com.cisco.applicationprofiler.apic.models.Vrf>());
		for (Vrf vrfProfiler : profilerTenant.getVrfs()) {
			com.cisco.applicationprofiler.apic.models.Vrf vrf = new com.cisco.applicationprofiler.apic.models.Vrf();
			vrf.setName(vrfProfiler.getDisplayName());
			vrfIdNameMap.put(vrfProfiler.getName(), vrfProfiler.getDisplayName());
			tenant.getVrfs().add(vrf);

		}
	}

	private void mapApplication(com.cisco.applicationprofiler.models.Tenant profilerTenant, Tenant tenant,
			HashMap<String, App> appApicMap) {
		tenant.setApps(new ArrayList<App>());
		for (Application appProfiler : profilerTenant.getApps()) {
			App app = new App();
			app.setEpgs(new ArrayList<com.cisco.applicationprofiler.apic.models.Epg>());
			app.setName(appProfiler.getDisplayName());
			tenant.getApps().add(app);
			appApicMap.put(appProfiler.getName(), app);
		}
	}

	private void mapContract(com.cisco.applicationprofiler.models.Tenant profilerTenant, Tenant tenant,
			HashMap<String, String> contractIdNameMap) {
		tenant.setContracts(new ArrayList<Contract>());
		tenant.setFilters(new ArrayList<Filter>());
		for (com.cisco.applicationprofiler.models.Contract contractProfiler : profilerTenant.getContracts()) {
			Contract contract = new Contract();
			mapProfilerContractToApicContract(contractProfiler, contract, tenant);
			tenant.getContracts().add(contract);
			contractIdNameMap.put(contractProfiler.getName(), contractProfiler.getDisplayName());
		}
	}

	private void mapProfilerContractToApicContract(com.cisco.applicationprofiler.models.Contract contractProfiler,
			Contract contract, Tenant tenant) {
		
		contract.setName(contractProfiler.getDisplayName());
		contract.setSubjects(new ArrayList<Subject>());
		for (com.cisco.applicationprofiler.models.Subject subjectProfiler : contractProfiler.getSubjects()) {
			Subject subject = new Subject();
			subject.setName(subjectProfiler.getName());
			subject.setSubjectAttributes(new ArrayList<SubjectAttributes>());
			for (com.cisco.applicationprofiler.models.Filter filterProfiler : subjectProfiler.getFilters()) {
				SubjectAttributes subjectAttributes = new SubjectAttributes();
				subjectAttributes.setFilterName(filterProfiler.getName());
				subject.getSubjectAttributes().add(subjectAttributes);
				Filter filter = new Filter();
				mapProfilerFilterToApicFilter(filterProfiler, filter);
				tenant.getFilters().add(filter);
			}
			contract.getSubjects().add(subject);
		}

	}

	private void mapProfilerFilterToApicFilter(com.cisco.applicationprofiler.models.Filter filterProfiler,
			Filter filter) {
		filter.setName(filterProfiler.getName());
		filter.setFilterEntry(new ArrayList<FilterEntry>());
		for (com.cisco.applicationprofiler.domain.FilterEntry filterEntryProfiler : filterProfiler.getFilterEntry()) {
			FilterEntry filterEntry = new FilterEntryBuilder(filterEntryProfiler.getName())
					.etherType(filterEntryProfiler.getEtherType()).protocol(filterEntryProfiler.getIpProtocol())
					.srcFromPort(filterEntryProfiler.getSrcPort()).destFromPort(filterEntryProfiler.getDestPort())
					.createFilterEntry();
			filter.getFilterEntry().add(filterEntry);
		}

	}

}
