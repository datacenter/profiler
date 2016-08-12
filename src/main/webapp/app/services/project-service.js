(function() {
  'use strict';
  angular
    .module('profiler')
    .factory('projectService', ['$resource', 'REST_URI', 'REQUEST_TYPE', function($resource, REST_URI, REQUEST_TYPE) {

      // return $resource('http://jsonplaceholder.typicode.com/users/:id', {id:'@id'}, {
      return $resource(REST_URI.baseUri + '/project/:id', { id: '@id' }, {
        //return $resource('http://10.11.0.171:8088/acisizer/v1/projects/:id', { id: '@id' }, {
        
        // get: {
        //   progressbar: {id:'get-project-request'}
        // },
        get: {
          method: 'GET',
          progressbar: {id:'get-project-request'},
          requestId: REQUEST_TYPE.PROJECT_GET
        },
        query: {
          method: 'GET',
          isArray:true,
          progressbar: {id:'get-projects-request'},
          requestId: REQUEST_TYPE.PROJECT_QUERY
        },
        save: {
          method: 'POST',
          progressbar: {id:'add-project-request'},
          requestId: REQUEST_TYPE.PROJECT_ADD
        },
        delete: {
          method: 'DELETE',
          progressbar: {id:'delete-project-request'},
          requestId: REQUEST_TYPE.PROJECT_DELETE
        },
        update: {
          method: 'PUT',
          progressbar: {id:'update-project-request'},
          requestId: REQUEST_TYPE.PROJECT_EDIT
        }
      });

    }])

})();
