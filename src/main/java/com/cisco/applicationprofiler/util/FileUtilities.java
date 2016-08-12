/**
 * 
 */
package com.cisco.applicationprofiler.util;

import java.util.UUID;

/**
 * @author Mahesh
 *
 */
public class FileUtilities {
	public static final String FILE_EXT_JSON = ".json";
	public static final String SEPERATOR = "-";
	public static final String INPUT = "input-";

	private FileUtilities() {
	}
	
	public static String generateUniqueFileName(String name){
		return INPUT+name+SEPERATOR+UUID.randomUUID()+FILE_EXT_JSON;
	}

}
