
(function(){
  'use strict';

  angular
    .module("profiler")
    .config(["$provide", "$httpProvider", httpInterceptorsConfig]);  

  //interceptor definition
  function httpInterceptorsConfig($provide, $httpProvider) {
    // Intercept http calls.
    $provide.factory('ProfilerHttpInterceptor', ["$q", "$sessionStorage", '$injector', function($q, $sessionStorage, $injector) {
      return {
        // On request success
        request: function(config) {
          config.headers = config.headers || {};
          var isTemplateRequest = (config.url.indexOf('html')!==-1);
          var isLoginRequest = (config.url.indexOf('/login')!==-1);
          var appService = $injector.get('appService');

          if (!isLoginRequest && !isTemplateRequest) {
          // if(config.requireToken){
            config.headers.token = appService.getToken();
          }

          // Return the config or wrap it in a promise if blank.
          return config || $q.when(config);
        } ,

        // On request failure
        requestError: function(rejection) {
          var config = rejection.config;
          // Return the promise rejection.
          return $q.reject(rejection);
        },

        // On response success
        response: function(response) {
          var config = response.config;

          // Return the response or promise.
          return response || $q.when(response);
        },

        // On response error
        responseError : function(response) {
          var config = response.config;

          return $q.reject(response);
        }
      };
    }]);

    // Add interceptor to the $httpProvider.
    $httpProvider.interceptors.push('ProfilerHttpInterceptor');

  }//httpInterceptorsConfig


})()