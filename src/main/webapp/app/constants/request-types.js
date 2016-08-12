(function() {
  'use strict';

  var request_array = [
    //REST SERVICES
    "USER_ADD", "USER_EDIT", "USER_GET", "USER_DELETE", "USER_QUERY",
    "DEVICE_ADD", "DEVICE_EDIT", "DEVICE_GET", "DEVICE_DELETE", "DEVICE_QUERY",
    "MODEL_ADD", "MODEL_EDIT", "MODEL_GET", "MODEL_DELETE", "MODEL_QUERY",
    "PLUGIN_ADD", "PLUGIN_EDIT", "PLUGIN_GET", "PLUGIN_DELETE", "PLUGIN_QUERY",
    "PROJECT_ADD", "PROJECT_EDIT", "PROJECT_GET", "PROJECT_DELETE", "PROJECT_QUERY",
    "TENANT_ADD", "TENANT_EDIT", "TENANT_GET", "TENANT_DELETE", "TENANT_QUERY",
    "APP_ADD", "APP_EDIT", "APP_GET", "APP_DELETE", "APP_QUERY",
    "EPG_ADD", "EPG_EDIT", "EPG_GET", "EPG_DELETE", "EPG_QUERY",      
    "CONTRACT_ADD", "CONTRACT_EDIT", "CONTRACT_GET", "CONTRACT_DELETE", "CONTRACT_QUERY",        
    "REPO_RECORDS_ADD", "REPO_RECORDS_EDIT", "REPO_RECORDS_GET", "REPO_RECORDS_DELETE", "REPO_RECORDS_QUERY",
    //OTHERS
    "OBJECT_DETAILS",
    "PUSH_CONFIG",
    "IMPORT_DEVICE"
  ];

  function getRequestObject(){
    var reqTypes = {};
    for(var i=0; i<request_array.length; i++){
      reqTypes[ request_array[i] ] = (i+1);
    }
    return reqTypes;
  }

  angular
    .module('profiler')
    .constant("REQUEST_TYPE", getRequestObject());

})();
