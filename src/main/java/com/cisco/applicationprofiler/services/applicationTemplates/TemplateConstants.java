/**
 * 
 */
package com.cisco.applicationprofiler.services.applicationTemplates;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public class TemplateConstants {

	private static final Map<String, Integer> l3outComplexity;

	static {
		l3outComplexity = new HashMap<>();
		l3outComplexity.put(ACISizerConstant.TEMPLATE_L3OUT_COMP_LOW, 2);
		l3outComplexity.put(ACISizerConstant.TEMPLATE_L3OUT_COMP_MEDIUM, 4);
		l3outComplexity.put(ACISizerConstant.TEMPLATE_L3OUT_COMP_HIGH, 6);
	}
	
	/**
	 * @return the l3outcomplexity
	 */
	public static Map<String, Integer> getL3outcomplexity() {
		return Collections.unmodifiableMap(TemplateConstants.l3outComplexity);
	}

}
