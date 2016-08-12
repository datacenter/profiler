/**
 * 
 */
package com.cisco.applicationprofiler.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * @author Mahesh
 *
 */
@Component
public class AutoNameGeneratorImpl implements AutoNameGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cisco.acisizer.util.AutoNameGenerator#generateName(java.util.List,
	 * java.lang.String)
	 */
	@Override
	public String generateName(List<String> nameList, String type) {
		int maxInt = 1;
		
		if (nameList != null && !nameList.isEmpty()) {
			List<Integer> list=new ArrayList<>();
			/*maxInt = nameList.parallelStream().mapToInt(name -> getIntPart(name)).max().getAsInt() + 1;*/
			for (String name : nameList) {
				list.add(getIntPart(name));
			}
			Collections.sort(list);
			maxInt=list.get(list.size()-1)+1;
		}
		return type + "-" + maxInt;
	}

	/**
	 * @param name
	 * @return
	 */
	public int getIntPart(String name) {
		try {
			if (name.split("-").length > 1)
				return Integer.parseInt(name.split("-")[1]);
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}

}
