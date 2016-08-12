(function() {
  'use strict';
  angular
    .module('profiler')
    .factory('pluginService', ['$resource', 'REQUEST_TYPE', function($resource, REQUEST_TYPE) {

      return $resource('/profiler/plugins/:id', { id: '@id' }, {
        get: {
          method: 'GET',
          progressbar: {id:'get-plugin-request'},
          requestId: REQUEST_TYPE.PLUGIN_GET
        },        
        query: {
          method: 'GET',
          isArray:true,
          progressbar: {id:'get-plugins-request'},
          requestId: REQUEST_TYPE.PLUGIN_QUERY
        },
        save: {
          method: 'POST',
          progressbar: {id:'add-plugin-request'},
          requestId: REQUEST_TYPE.PLUGIN_ADD
        },
        delete: {
          method: 'DELETE',
          progressbar: {id:'delete-plugin-request'},
          requestId: REQUEST_TYPE.PLUGIN_DELETE
        },
        update: {
          method: 'PUT',
          progressbar: {id:'update-plugin-request'},
          requestId: REQUEST_TYPE.PLUGIN_EDIT
        }
      });

    }])

})();
