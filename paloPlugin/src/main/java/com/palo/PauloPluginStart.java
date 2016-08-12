/**
 * Copyright @maplelabs
 */
package com.palo;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.palo.model.PauloPluginConvertor;

/**
 * @author Mahesh
 *
 */
@EnableAutoConfiguration
@Component
@ComponentScan
public class PauloPluginStart {

	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, IOException, XMLStreamException, JAXBException {
		ConfigurableApplicationContext ctx=SpringApplication.run(PauloPluginStart.class, args);
		PauloPluginConvertor test=ctx.getBean(PauloPluginConvertor.class);
		test.init();
	}
}
