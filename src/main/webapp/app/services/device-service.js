(function() {
  'use strict';
  angular
    .module('profiler')
    .factory('deviceService', ['$resource', 'REQUEST_TYPE', function($resource, REQUEST_TYPE) {

      // return $resource('http://jsonplaceholder.typicode.com/users/:id', {id:'@id'}, {
      return $resource('/profiler/devices/:id', { id: '@id' }, {
        get: {
          method: 'GET',
          progressbar: {id:'get-device-request'},
          requestId: REQUEST_TYPE.DEVICE_GET
        },        
        query: {
          method: 'GET',
          isArray:true,
          progressbar: {id:'get-devices-request'},
          requestId: REQUEST_TYPE.DEVICE_QUERY
        },
        save: {
          method: 'POST',
          progressbar: {id:'add-device-request'},
          requestId: REQUEST_TYPE.DEVICE_ADD
        },
        delete: {
          method: 'DELETE',
          progressbar: {id:'delete-device-request'},
          requestId: REQUEST_TYPE.DEVICE_DELETE
        },
        update: {
          method: 'PUT',
          progressbar: {id:'update-device-request'},
          requestId: REQUEST_TYPE.DEVICE_EDIT
        }
      });

    }])

})();
