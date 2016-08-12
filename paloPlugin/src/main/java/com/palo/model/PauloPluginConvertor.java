package com.palo.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.palo.domain.AuditInfo;
import com.palo.domain.Contract;
import com.palo.domain.Device;
import com.palo.domain.Filter;
import com.palo.domain.FilterEntry;
import com.palo.domain.Subject;
import com.palo.repo.AuditInfoRepository;
import com.palo.repo.ContractRepository;
import com.palo.repo.DeviceRepository;

@Component
public class PauloPluginConvertor {
	private static final String QUERY_PARAM_SEPERATOR = "&";

	private static final String API_TYPE = "/api/?type=";

	private static final String HTTPS = "https://";

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Value("${paulo.plugin.filepath}")
	private String filePath;

	@Value("${paulo.plugin.url}")
	private String URL;
	
	@Value("${paulo.plugin.device}")
	private String deviceId;

	public void init()
			throws NoSuchAlgorithmException, KeyManagementException, IOException, XMLStreamException, JAXBException {
		Long startTime = System.currentTimeMillis();
		System.out.println("start time :" + startTime);
		Device device=deviceRepository.findOne(Integer.parseInt(deviceId));
		StreamSource xml;
		if (device.getIpAddress().equals("$$")) {
			xml = new StreamSource(filePath);
		} else {
			 disableSSL();
			 String key=callLogin(device);
			xml = new StreamSource(callConfigApi(device, key).getInputStream());
		}

		XMLInputFactory xif = XMLInputFactory.newFactory();

		XMLStreamReader xsr = xif.createXMLStreamReader(xml);
		xsr.nextTag();
		while (!xsr.getLocalName().equals("application")) {
			xsr.nextTag();
		}
		JAXBContext jaxbContext = JAXBContext.newInstance(Application.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<Application> que = jaxbUnmarshaller.unmarshal(xsr, Application.class);
		AuditInfo auditInfo = getAuditInfo(device);
		mapPaloEntryToAciFilterEntry(que.getValue(), auditInfo);
		Long endTime = System.currentTimeMillis();
		System.out.println("end time :" + endTime);
		System.out.println("diff time: " + (endTime - startTime));

	}

	private AuditInfo getAuditInfo(Device device) {
		Date date = new Date();
		AuditInfo auditInfo = new AuditInfo();
		//auditInfo.setAuditedOn(new Timestamp(date.getTime()));
		auditInfo.setImportedOn(new Timestamp(date.getTime()));
		auditInfo.setSourceDevice(device);

		return auditInfoRepository.save(auditInfo);
	}

	private void disableSSL() throws NoSuchAlgorithmException, KeyManagementException
		 {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		/*HttpsURLConnection conn = callLogin();
		return conn;*/
	}

	private String callLogin(Device device) throws MalformedURLException, IOException, XMLStreamException {
		String paulo_url = getLoginUrl(device);
		HttpsURLConnection conn = getHttpConnection(paulo_url);
		
		
		XMLInputFactory xif = XMLInputFactory.newFactory();
		XMLStreamReader xsr;

	
			xsr = xif.createXMLStreamReader(conn.getInputStream());
			xsr.nextTag();
			while (!xsr.getLocalName().equals("key")) {
				xsr.nextTag();
			}
			
		
		return xsr.getElementText();
		
	} 
	
	private HttpsURLConnection callConfigApi(Device device,String key) throws MalformedURLException, IOException{
		return getHttpConnection(getConfigApiUrl(device, key));
	}

	private HttpsURLConnection getHttpConnection(String paulo_url) throws MalformedURLException, IOException {
		URL url = new URL(
				paulo_url);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestProperty("accept", "text/xml");
		return conn;
	}
	
	private String getLoginUrl(Device device){
		String URL=HTTPS+device.getIpAddress()+API_TYPE+"keygen"+QUERY_PARAM_SEPERATOR+"user="+device.getUsername()+QUERY_PARAM_SEPERATOR+"password="+device.getPassword();
		return URL;
	}
	
	private String getConfigApiUrl(Device device,String key){
		return HTTPS+device.getIpAddress()+API_TYPE+"config"+QUERY_PARAM_SEPERATOR+"action="+"get"+QUERY_PARAM_SEPERATOR+"xpath="+"/config/predefined/application"+QUERY_PARAM_SEPERATOR+"key="+key;
	}

	private void mapPaloEntryToAciFilterEntry(Application application, AuditInfo auditInfo) {
		Contract contract = null;
		for (Entry entry : application.getEntry()) {

			contract = new Contract();
			contract.setName(entry.getName() + "_contract_"+auditInfo.getId());
			contract.setAuditInfo(auditInfo);

			System.out.println("id :" + entry.getId());
			if (entry.getDefault1() != null) {
				mapDefault(entry, getFilter(entry, getSubject(entry, contract,auditInfo),auditInfo),auditInfo);
			} else if (entry.getUseApplications() != null) {
				FilterEntry filterEntry = getFilterEntry(getFilter(entry, getSubject(entry, contract,auditInfo), auditInfo), entry,0,auditInfo);
				filterEntry.setEtherType(entry.getUseApplications().getMember());
			}
			contractRepository.save(contract);
		}

	}

	private void mapDefault(Entry entry, Filter filter, AuditInfo auditInfo) {
		if (entry.getDefault1().getPort() != null) {
			System.out.println("value :" + entry.getDefault1().getPort().getMember());
			String members[] = entry.getDefault1().getPort().getMember().split("/");
			String ports[] = members[1].split(",");
			int i=0;
			for (String port : ports) {
				i=i+1;
				FilterEntry filterEntry = getFilterEntry(filter, entry,i,auditInfo);
				filterEntry.setEtherType("ip");
				if(NumberUtils.isNumber(port)){
					filterEntry.setSrcPort(port);
				}
					
				filterEntry.setIpProtocol(members[0]);
				
			}

		} else {
			System.out.println("protocol : " + entry.getDefault1().getIdentByIpProtocol());
			FilterEntry filterEntry = getFilterEntry(filter, entry,0,auditInfo);
			filterEntry.setEtherType("ip");
			filterEntry.setIpProtocol(entry.getDefault1().getIdentByIpProtocol());
		}
	}

	private FilterEntry getFilterEntry(Filter filter, Entry entry, int i, AuditInfo auditInfo) {
		FilterEntry filterEntry = new FilterEntry();
		filterEntry.setName(entry.getName()+"-"+i+"-"+auditInfo.getId());
		filter.getFilterEntry().add(filterEntry);
		filterEntry.setFilter(filter);
		filterEntry.setAuditInfo(auditInfo);
		return filterEntry;
	}

	private Filter getFilter(Entry entry, Subject subject, AuditInfo auditInfo) {
		Filter filter = new Filter();
		filter.setName(entry.getName() + "_Filter_"+auditInfo.getId());
		filter.setDescription(entry.getDescription());
		filter.setSubject(subject);
		filter.setAuditInfo(auditInfo);
		subject.getFilters().add(filter);
		return filter;
	}

	private Subject getSubject(Entry entry, Contract contract,AuditInfo auditInfo) {
		Subject subject = new Subject();
		subject.setName(entry.getName() + "_Subject_"+auditInfo.getId());
		subject.setDescription(entry.getDescription());
		subject.setContract(contract);
		contract.getSubjects().add(subject);
		return subject;
	}

}
