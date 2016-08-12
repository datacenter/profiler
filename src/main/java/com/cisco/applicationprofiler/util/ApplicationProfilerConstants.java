package com.cisco.applicationprofiler.util;

public class ApplicationProfilerConstants {

	public static final String PROFILER_PATH = "profiler/";
	public static final String ADMIN_URL = PROFILER_PATH + "users";
	public static final String PROJECT_URL = PROFILER_PATH + "projects";
	public static final String PATH_PARAM = "/{id}";
	public static final String PATH_PARAM_ID = "id";
	public static final String PATH_PARAM_ID_URL = "/{" + PATH_PARAM_ID + "}";
	public static final String JSON = "application/json";
	public static final String LOGIN = PROFILER_PATH + "login";
	public static final String DEVICES = PROFILER_PATH + "devices";
	public static final String PLUGINS = PROFILER_PATH + "plugins";
	public static final String CONTRACT = PROFILER_PATH + "contracts";
	public static final String SUBJECT = PROFILER_PATH + "subjects";
	public static final String FILTER = PROFILER_PATH + "filters";
	public static final String FILTER_ENTRY = PROFILER_PATH + "filterEntry";
	public static final String REPO = PROFILER_PATH + "repoObjects";
	public static final String REPO_IMPORT_URL = REPO + "/import/device" + PATH_PARAM_ID_URL;
	public static final String OBJECT_NAME = "objectName";
	public static final String TENANT_URL=PROJECT_URL+PATH_PARAM_ID_URL+"/tenants";
	public static final String CONTRACT_URL=PROJECT_URL+"/{projectId}/tenant/{tenantId}/app/{appId}/contract";
	public static final String FILTER_URL=PROJECT_URL+"/{projectId}/tenant/{tenantId}/filter";
	public static final String SOURCE_BASED = "sourceDevice";
	public static final String DEVICE_ID = "deviceId";
	public static final String IMPORTED_ON = "importedOn";
	public static final String TYPE = "ObjectType";
	public static final String MODEL = PROFILER_PATH + "models";
}
