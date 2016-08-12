(function() {
  'use strict';
  angular
    .module('profiler')
    .factory('userService', ['$resource', 'REQUEST_TYPE', function($resource, REQUEST_TYPE) {

      // return $resource('http://jsonplaceholder.typicode.com/users/:id', {id:'@id'}, {
      return $resource('/profiler/users/:id', { id: '@id' }, {
        // update: {
        //   method: 'put'
        // }
        get: {
          method: 'GET',
          requestId: REQUEST_TYPE.USER_GET
        },
        query: {
          method: 'GET',
          isArray:true,
          requestId: REQUEST_TYPE.USER_QUERY
        },
        save: {
          method: 'POST',
          requestId: REQUEST_TYPE.USER_ADD
        },
        delete: {
          method: 'DELETE',
          requestId: REQUEST_TYPE.USER_DELETE
        },
        update: {
          method: 'PUT',
          requestId: REQUEST_TYPE.USER_EDIT
        }
      });

    }])

})();
