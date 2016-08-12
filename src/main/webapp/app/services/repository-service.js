(function() {
  'use strict';
  angular
    .module('profiler')
    .factory('repoService', ['$resource', function($resource) {
      // return $resource('http://jsonplaceholder.typicode.com/users/:id', {id:'@id'}, {
      return $resource('/profiler/repo/:id', { id: '@id' }, {
        update: {
          method: 'put'
        }
      });

    }])

  // If repos service is implemeneted in rest way, following code is not required
  .service('repoTestService', ['appService', 'REQUEST_TYPE', function(appService, REQUEST_TYPE) {
    this.getRepos = function(filterOptions) {
      var config = {
        url: "/profiler/repoObjects",
        method: "GET",
        params: filterOptions || {},
        requireToken: true,
        headers: {
          'contentType': 'application/json'
        },
        progressbar: {id:'repo-records-request'},
        requestId: REQUEST_TYPE.REPO_RECORDS_QUERY,
        cancelDuplicate: true        
      };
      return appService.processRequest(config);
    };

    this.deleteRepoRecords = function(recordId, objectType) {
      var config = {
        url: "/profiler/" + objectType + "/" + recordId,
        method: "DELETE",
        requestId: REQUEST_TYPE.REPO_RECORDS_DELETE
      };
      return appService.processRequest(config);
    };

    this.getObjectDetails = function(recordId, objectType) {
      var config = {
        url: "/profiler/" + objectType + "/" + recordId,
        method: "GET",
        requestId: REQUEST_TYPE.OBJECT_DETAILS
      };
      return appService.processRequest(config);
    };

    this.importRepoRecords = function(deviceId) {
      var config = {
        url: "/profiler/repoObjects/import/device/" + deviceId,
        method: "POST",
        requireToken: true,
        progressbar: {id:'import-request'}
        //this is commented to not cancel the import service on navigating, so that import status will be updated
        //  in UI once the service is completed
        /*,
        requestId: REQUEST_TYPE.IMPORT_DEVICE*/ 
      };
      return appService.processRequest(config);
    };


  }]);




})();
