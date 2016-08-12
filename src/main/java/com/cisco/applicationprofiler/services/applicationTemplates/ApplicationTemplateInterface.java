/**
 * 
 */
package com.cisco.applicationprofiler.services.applicationTemplates;

import com.cisco.applicationprofiler.models.Application;
import com.cisco.applicationprofiler.models.LogicalSummary;
import com.cisco.applicationprofiler.models.Tenant;
import com.cisco.applicationprofiler.ui.models.ApplicationTemplate;

/**
 * @author Mahesh
 *
 */
public interface ApplicationTemplateInterface {

	Application addApplication(ApplicationTemplate appTemplate, Tenant targetTenant,
			Tenant commonTenant, LogicalSummary logicalSummary);
}
