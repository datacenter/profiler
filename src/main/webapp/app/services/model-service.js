(function() {
  'use strict';
  angular
    .module('profiler')
    .factory('modelService', ['$resource', 'REQUEST_TYPE', function($resource, REQUEST_TYPE) {

      return $resource('/profiler/models/:id', { id: '@id' }, {
        get: {
          method: 'GET',
          requestId: REQUEST_TYPE.MODEL_GET
        },
        query: {
          method: 'GET',
          isArray:true,
          requestId: REQUEST_TYPE.MODEL_QUERY
        },
        save: {
          method: 'POST',
          requestId: REQUEST_TYPE.MODEL_ADD
        },
        delete: {
          method: 'DELETE',
          requestId: REQUEST_TYPE.MODEL_DELETE
        },
        update: {
          method: 'PUT',
          requestId: REQUEST_TYPE.MODEL_EDIT
        }
      });

    }])

})();
