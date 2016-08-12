package com.cisco.applicationprofiler;

import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.Context;
import org.apache.catalina.deploy.SecurityCollection;

//Custom context to force http to https redirection
//
public class AppTomcatContextCustomizer implements TomcatContextCustomizer {

    @Override
    public void customize(Context context) {
    	 {
/*
			        final SecurityConstraint securityConstraint = new SecurityConstraint();
			        securityConstraint.setUserConstraint("CONFIDENTIAL");
			        SecurityCollection collection = new SecurityCollection();
			        collection.addPattern("/*");
			        securityConstraint.addCollection(collection);
		    		context.addConstraint(securityConstraint);
*/		    }
    }

}