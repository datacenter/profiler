(function() {
  'use strict';

  angular
    .module('profiler')
    .constant("APP_CONSTANTS", {
      VERSION: "0.0.1",
      NAME: "Profiler"
    })
    // user roles
    .constant('USER_ROLES', [
      'ROLE_USER',
      'ROLE_ADMIN'
    ])
    // user authentication types
    .constant('USER_AUTH_TYPES', [
      'Local Authentication'
    ])
    // device plugin
    .constant('DEVICE_PLUGINS', [
      'Paul'
    ])
    // device models
    .constant('DEVICE_MODELS', [
      "PAM_200",
      "ACI"
    ])
    // device object types
    .constant("OBJECT_TYPES", {
      //@Manohar :: here the key names are "value of objectType" of repo objects
      "filter": {
        "lable" : "filters",
        "repoTypeRestReq" : "filters",
        "repoTypeFilterQueryString" : "filter"
      },
      "filter entry": {
        "lable" : "filter entry",
        "repoTypeRestReq" : "filterEntry",
        "repoTypeFilterQueryString" : "filter_entry"
      },
      "contract": {
        "lable" : "contracts",
        "repoTypeRestReq" : "contracts",
        "repoTypeFilterQueryString" : "contract"
      },
      '': {
        "lable" : "none",
        "repoTypeRestReq" : "none",
        "repoTypeFilterQueryString" : ""
      }
    })
    // device import statuse
    .constant("IMPORT_STATUS", {
      NOT_IMPORTED: 1,
      PROGRESS: 2,
      SUCCESS: 3,
      FAIL: 4
    });


})();
