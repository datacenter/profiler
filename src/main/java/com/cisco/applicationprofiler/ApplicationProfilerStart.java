/**
 * Copyright @maplelabs
 */
package com.cisco.applicationprofiler;

import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

/**
 * @author Mahesh
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { LiquibaseAutoConfiguration.class })
public class ApplicationProfilerStart {

	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ApplicationProfilerStart.class);
        //SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
		// Check if the selected profile has been set as argument.
        // if not the development profile will be added
        //addDefaultProfile(app, source);
		app.run(args);
	}

    /**
     * Set a default profile if it has not been set.
     * This function is not used now since the command line arguments have to be in format --<prop>=value
     *  which is not the case.
     */
   /* private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("-Dspring.profiles.active")) {
            app.setAdditionalProfiles("dev");
        }
    }*/

	@Bean
	EmbeddedServletContainerCustomizer containerCustomizer(@Value("${keystorefile}") Resource keystoreFile,
			@Value("${keystorepass}") String keystorePass, @Value("${sslport}") String sslPort) throws Exception {

		final String absoluteKeystoreFile = keystoreFile.getFile().getAbsolutePath();
		final String keypass = keystorePass;
		final String sslPortString = sslPort;
		if (sslPort == null) {
			sslPort = "8443";
		}

		final Integer sslPortVal = Integer.valueOf(sslPort);
		
		return new EmbeddedServletContainerCustomizer() {

			private Connector getSslConnector() {
				Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
				connector.setPort(sslPortVal);
				connector.setSecure(true);
				connector.setScheme("https");
				connector.setProperty("compression", "on");
				connector.setProperty("compressableMimeType",
						"text/html,text/xml,text/plain,application/json,application/xml,application/javascript");
				Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
				proto.setSSLEnabled(true);
				proto.setKeystoreFile(absoluteKeystoreFile);
				proto.setSessionTimeout("3600");
				proto.setKeystorePass(keypass);
				proto.setKeystoreType("PKCS12");
				proto.setKeyAlias("tomcat");
				return connector;
			}

			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
				Connector connector = getSslConnector();
				tomcat.addAdditionalTomcatConnectors(connector);				
				tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {
					@Override
					public void customize(Connector connector) {
						if (!connector.getProtocol().equals(sslPortString) ){
							// For http connector, set redirect port
							//
						    AbstractHttp11Protocol httpProtocol = (AbstractHttp11Protocol) connector.getProtocolHandler();
						    httpProtocol.setCompression("on");
						    httpProtocol.setCompressionMinSize(64);
						    connector.setRedirectPort(sslPortVal);
						    connector.setProperty("compression", "on");
						    connector.setProperty("compressableMimeType",
								"text/html,text/xml,text/plain,application/json,application/xml,application/javascript");
						}
					}
				});

				tomcat.setSessionTimeout(10, TimeUnit.MINUTES);
				// tomcat.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,
				// "/"));
				    tomcat.addContextCustomizers( new AppTomcatContextCustomizer());
			}
		};

	}

}

