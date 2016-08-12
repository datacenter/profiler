package com.cisco.applicationprofiler.apic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProcessBuilderApic {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessBuilderApic.class);

	private static final String COOKIE_APIC_COOKIE = "Cookie:APIC-cookie=";

	private static final String D = "-d";

	private static final String CACHE_CONTROL_NO_CACHE = "Cache-Control:no-cache";

	private static final String H = "-H";

	private static final String POST = "POST";

	private static final String X = "-X";

	private static final String INSECURE = "--insecure";

	private static final String CURL = "curl";

	public String apicLogin(String loginUrl, String loginXml) {
		try {
			ProcessBuilder pb = new ProcessBuilder(CURL, INSECURE, X, POST, H, CACHE_CONTROL_NO_CACHE, D, loginXml,
					loginUrl);
			LOGGER.info(loginXml);
			Process p = pb.start();
			InputStream is = p.getInputStream();

			XMLInputFactory xif = XMLInputFactory.newFactory();
			XMLStreamReader xsr;

			xsr = xif.createXMLStreamReader(is);
			xsr.nextTag();
			while (!xsr.getLocalName().equals("aaaLogin")) {
				xsr.nextTag();
			}
			for (int i = 0; i < xsr.getAttributeCount(); i++) {
				if ("token".equals(xsr.getAttributeLocalName(i))) {
					System.out.println("token: " + xsr.getAttributeValue(i));
					return xsr.getAttributeValue(i);
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void pushTenant(String tenantUrl, String tenantXml, String apicToken) throws IOException {
		String cookieHeader = COOKIE_APIC_COOKIE + apicToken;
		ProcessBuilder pb = new ProcessBuilder(CURL, INSECURE, X, POST, H, cookieHeader, H, CACHE_CONTROL_NO_CACHE, D,
				tenantXml, tenantUrl);
		Process p = pb.start();
		InputStream is = p.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder builder = new StringBuilder();
		line = bufferedReader.readLine();
		while (line != null) {
			builder.append(line);
			line = bufferedReader.readLine();
		}
		System.out.println("output" + builder.toString());
	}
}