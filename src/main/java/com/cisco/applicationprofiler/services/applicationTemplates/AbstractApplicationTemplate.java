/**
 * 
 */
package com.cisco.applicationprofiler.services.applicationTemplates;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.applicationprofiler.models.UIData;
import com.cisco.applicationprofiler.util.ACISizerConstant;

/**
 * @author Mahesh
 *
 */
public abstract class AbstractApplicationTemplate implements ApplicationTemplateInterface {
	protected static final Map<String, Integer> epgComplexity;
	protected static final Map<String, Integer> contractComplexity;
	protected static final Map<String, Integer> appModelToNoOfEpgs;
	protected static final Map<String, List<String>> appModelToSuffixes;
	protected static final Map<String,UIData> appModelSuffixesToUidata;
 
	protected static final List<String> appModel;
	protected static final List<String> confType;
	protected static final List<String> suffixes;

	static {
		appModelSuffixesToUidata=new HashMap<>();
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_FLAT+ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP, new UIData(310.2073f, 211.42276f));
		
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_FLAT+ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP, new UIData(310.2073f, 171.42276f));
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_FLAT+ACISizerConstant.TEMPLATE_CONTRACT_NAME_L3OUT, new UIData(310.03253f, 340.42276f));


		
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_2TIER+ACISizerConstant.TEMPLATE_EPG_SUFFIX_DB, new UIData(250f, 180f));
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_2TIER+ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP, new UIData(440f, 180f));
		
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_3TIER+ACISizerConstant.TEMPLATE_EPG_SUFFIX_DB, new UIData(160f, 190f));
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_3TIER+ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP, new UIData(300f, 190f));
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_3TIER+ACISizerConstant.TEMPLATE_EPG_SUFFIX_WEB, new UIData(450f, 190f));
		
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_2TIER+ACISizerConstant.TEMPLATE_EPG_SUFFIX_DB+ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP, new UIData(340f, 300f));
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_2TIER+ACISizerConstant.TEMPLATE_CONTRACT_NAME_L3OUT, new UIData(530f, 300f));
		

		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_3TIER+ACISizerConstant.TEMPLATE_EPG_SUFFIX_DB+ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP, new UIData(160f, 330f));
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_3TIER+ACISizerConstant.TEMPLATE_EPG_SUFFIX_DB+ACISizerConstant.TEMPLATE_EPG_SUFFIX_WEB, new UIData(300f, 330f));
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_3TIER+ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP+ACISizerConstant.TEMPLATE_EPG_SUFFIX_WEB, new UIData(450f, 330f));
		appModelSuffixesToUidata.put(ACISizerConstant.TEMPLATE_APP_MODEL_3TIER+ACISizerConstant.TEMPLATE_CONTRACT_NAME_L3OUT, new UIData(624.03253f, 338.42276f));


		appModel = Arrays.asList(ACISizerConstant.TEMPLATE_APP_MODEL_NO_TEMPLATE,
				ACISizerConstant.TEMPLATE_APP_MODEL_FLAT, ACISizerConstant.TEMPLATE_APP_MODEL_2TIER,
				ACISizerConstant.TEMPLATE_APP_MODEL_3TIER);
		confType = Arrays.asList(ACISizerConstant.TEMPLATE_CONF_DEFAULT, ACISizerConstant.TEMPLATE_CONF_UNIQUE);
		suffixes = Arrays.asList( ACISizerConstant.TEMPLATE_EPG_SUFFIX_DB,ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP,
				ACISizerConstant.TEMPLATE_EPG_SUFFIX_WEB);

		appModelToSuffixes = new HashMap<>();

		appModelToSuffixes.put(ACISizerConstant.TEMPLATE_APP_MODEL_FLAT,
				Arrays.asList(ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP));
		appModelToSuffixes.put(ACISizerConstant.TEMPLATE_APP_MODEL_2TIER,
				Arrays.asList(ACISizerConstant.TEMPLATE_EPG_SUFFIX_DB, ACISizerConstant.TEMPLATE_EPG_SUFFIX_APP));
		appModelToSuffixes.put(ACISizerConstant.TEMPLATE_APP_MODEL_3TIER, suffixes);

		epgComplexity = new HashMap<>();
		epgComplexity.put(ACISizerConstant.TEMPLATE_EPG_COMP_SMALL, 5);
		epgComplexity.put(ACISizerConstant.TEMPLATE_EPG_COMP_MEDIUM, 25);
		epgComplexity.put(ACISizerConstant.TEMPLATE_EPG_COMP_LARGE, 50);

		contractComplexity = new HashMap<>();
		contractComplexity.put(ACISizerConstant.TEMPLATE_CONTRACT_COMP_LOW, 2);
		contractComplexity.put(ACISizerConstant.TEMPLATE_CONTRACT_COMP_MEDIUM, 4);
		contractComplexity.put(ACISizerConstant.TEMPLATE_CONTRACT_COMP_HIGH, 6);

		

		appModelToNoOfEpgs = new HashMap<>();
		appModelToNoOfEpgs.put(ACISizerConstant.TEMPLATE_APP_MODEL_NO_TEMPLATE, 0);
		appModelToNoOfEpgs.put(ACISizerConstant.TEMPLATE_APP_MODEL_FLAT, 1);
		appModelToNoOfEpgs.put(ACISizerConstant.TEMPLATE_APP_MODEL_2TIER, 2);
		appModelToNoOfEpgs.put(ACISizerConstant.TEMPLATE_APP_MODEL_3TIER, 3);

	}

	/**
	 * @return the epgcomplexity
	 */
	public static Map<String, Integer> getEpgcomplexity() {
		return Collections.unmodifiableMap(epgComplexity);
	}

	/**
	 * @return the contractcomplexity
	 */
	public static Map<String, Integer> getContractcomplexity() {
		return Collections.unmodifiableMap(contractComplexity);
	}

	

	/**
	 * @return the appmodel
	 */
	public static List<String> getAppmodel() {
		return appModel;
	}

	/**
	 * @return the conftype
	 */
	public static List<String> getConftype() {
		return confType;
	}

}
