package com.cisco.applicationprofiler.util;

public final class ACISizerConstant {

	public static final String _project = "project";
	public static final String _tenant = "tenant";
	public static final String _app = "app";
	public static final String _epg = "epg";
	public static final String _l3out = "l3out";
	public static final String _contract = "contract";
	public static final String _contractWithl3out = "contractl3";
	public static final String _contractWithSharedResource = "contractshared";
	public static final String _bd = "bd";
	public static final String _vrf = "vrf";
	public static final String _shared_resource = "shared";
	public static final String TENANT_TYPE_UTILITY = "utility";
	public static final String TENANT_NAME_COMMON = "Common";
	public static final String DEFAULT_VRF_NAME = "VRF1";
	public static final String DEFAULT_L3OUT_NAME = "L3OUT1";
	public static final String DEFAULT_SHARED_RESOURCE_NAME = "SHARED1";
	public static final String TENANT_FIELD_SEPERATOR = "/";
	public static final String COULD_NOT_FIND_THE_PROJECT_FOR_ID = "Could not find the Project";
	public static final String COULD_NOT_FIND_THE_ROOM_FOR_ID = "Could not find the Room";
	public static final String PLEASE_DELETE_THE_CONTRACTS_ASSOCIATED_FIRST = "please delete the contracts associated first";
	public static final String PLEASE_DELETE_THE_EPGS_ASSOCIATED_FIRST = "please delete the epgs associated first";
	public static final String COULD_NOT_FIND_APP_FOR_ID = "Could not find app";
	public static final String EPG_NAME_EXISTS = "Please choose different Name, Epg exists with name ";
	public static final String TENANT_NOT_FOUND_WITH_ID = "Could not found Tenant" ;
	public static final String COULD_NOT_FIND_EPG_FOR_ID = "Could not find Epg";
	public static final String VRF_NOT_FOUND_WITH_ID = "Could not found Vrf";
	public static final String SHARED_RESOURCE_NOT_FOUND_WITH_ID = "Could not found shared resource";
	public static final String SHARED_RESOURCE_NOT_CREATED = "Shared resource not created";
	public static final String SHARED_RESOURCE_NAME_EXISTS = "Shared resource already exists";
	public static final String BD_DEFAULT = "BD1";

	public static final String SUMMARY_UI_Tenant = "Tenant";
	public static final String SUMMARY_UI_Vrf = "VRFs";
	public static final String SUMMARY_UI_Bd = "BD";
	public static final String SUMMARY_UI_Epg = "EPG";
	public static final String SUMMARY_UI_Endpoint = "Endpoint";
	public static final String SUMMARY_UI_Contract = "Contract";
	public static final String SUMMARY_UI_L3out = "L3Out";
	public static final String SUMMARY_UI_Source_Prefix_TCAM = "Source Prefix TCAM";
	public static final String SUMMARY_UI_Dest_Prefix_TCAM = "Destination Prefix TCAM";
	public static final String SUMMARY_UI_Policy_TCAM = "Policy TCAM";
	public static final String SUMMARY_UI_Router_IP_Table = "Router IP Table";
	public static final String SUMMARY_UI_VLAN_Table = "VLAN Table";

	public static final int RANDOM_NUMBER_GENERATION_MAX=10000;
	
	public static final int DEFAULT_TENANT_ID = 1;
	public static final int DEFAULT_VRF_ID = 2;
	public static final int DEFAULT_BD_ID = 3;
	public static final int DEFAULT_L3OUT_ID = 4;
	public static final int DEFAULT_SHARED_RESOURCE_ID = 5;
	
	public static final String FILTER_HTTP = "HTTP";
	public static final String FILTER_FTP = "FTP";
	public static final String FILTER_SMTP = "SMTP";
	public static final String FILTER_TELNET = "TELNET";
	public static final String L3_OUT_NOT_FOUND_WITH_ID = "L3Out not found with id";
	
	public static final String TEMPLATE_TENANT_MODEL_LOCAL = "Local";
	public static final String TEMPLATE_TENANT_MODEL_COMMON = "Common";
	public static final String TEMPLATE_VRF_NAME = "LocalVRF";
	public static final String TEMPLATE_BD_NAME = "LocalBD";
	public static final String TEMPLATE_L3OUT_NAME = "LocalL3OUT";
	
	public static final String TEMPLATE_EPG_COMP_SMALL = "Small";
	public static final String TEMPLATE_EPG_COMP_MEDIUM = "Medium";
	public static final String TEMPLATE_EPG_COMP_LARGE = "Large";

	public static final String TEMPLATE_CONTRACT_COMP_LOW = "Low";
	public static final String TEMPLATE_CONTRACT_COMP_MEDIUM = "Medium";
	public static final String TEMPLATE_CONTRACT_COMP_HIGH = "High";

	public static final String TEMPLATE_L3OUT_COMP_LOW = "Low";
	public static final String TEMPLATE_L3OUT_COMP_MEDIUM = "Medium";
	public static final String TEMPLATE_L3OUT_COMP_HIGH = "High";
	
	public static final String TEMPLATE_APP_MODEL_NO_TEMPLATE = "NoTemplate";
	public static final String TEMPLATE_APP_MODEL_FLAT = "Flat";
	public static final String TEMPLATE_APP_MODEL_2TIER = "2Tier";
	public static final String TEMPLATE_APP_MODEL_3TIER = "3Tier";
	
	public static final String TEMPLATE_CONF_DEFAULT = "Default";
	public static final String TEMPLATE_CONF_UNIQUE = "Unique";
	
	public static final String TEMPLATE_APP_DISPLAY_NAME_3TIER = "";//decide on the name
	public static final String TEMPLATE_APP_DISPLAY_NAME_2TIER = "";
	public static final String TEMPLATE_APP_DISPLAY_NAME_FLAT = "";

	public static final String TEMPLATE_EPG_SUFFIX_APP="APP";
	public static final String TEMPLATE_EPG_SUFFIX_WEB="WEB";
	public static final String TEMPLATE_EPG_SUFFIX_DB="DB";
	
	public static final String TEMPLATE_CONTRACT_NAME_L3OUT = "ctL3OUT";

	public static final String TEMPLATE_EPG_DISPLAY_NAME_TIER="TIER";
	public static final String NAME = "Name :";
	public static final String COPY = " -copy";
	public static final int EPG_SPAN_DEFAULT = 2;
	public static final int EPG_NO_OF_ENDPOINTS_DEFAULT = 20;
	public static final int EPG_SUBNET_DEFAULT = 1;
	public static final int APPLICATION_NO_OF_INSTANCES_DEFAULT = 1;
	public static final int BD_SUBNETS_DEFAULT = 1;
	public static final String TENANT_TYPE_USER="user";
	public static final String DEFAULT = "default";
	public static final String ERROR_IN_SIZING = "Error in sizing ";

	public static final String SERVER = "Server";
	public static final String LEAF = "Leaf";
	public static final String SPINE = "Spine";
	public static final String FIREWALL = "Firewall";
	public static final String FABRIC_INTERCONNECT = "Fabric Interconnect";
	public static final String CISCO_SIZING_TOOL_SCALE_CALC = "scale-calc";
}
