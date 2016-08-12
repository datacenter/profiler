(function() {
  'use strict';
  angular
    .module('profiler')
    .service('projectDetailsService', ['$rootScope', 'REST_URI', 'REQUEST_TYPE', 'appService', function($rootScope, REST_URI, REQUEST_TYPE, appService) {

      this.pushToApic = function(tenantId, apicDeiceId) {
        var config = {
          url: (REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + tenantId + '/apic/' + apicDeiceId + '/applychanges'),
          method: "POST",
          progressbar: {id:'push-to-apic-request'},
          requestId: REQUEST_TYPE.PUSH_CONFIG        
        };
        return appService.processRequest(config);
      };
      
    }])

})();
