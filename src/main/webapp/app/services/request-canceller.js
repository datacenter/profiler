(function(){
  'use strict';

  angular
    .module('profiler')
    .service("requestCanceller", ['$q', '$rootScope', function($q, $rootScope){
      var fn = this,
          _activeRequests = {};  

      fn.add = function(config){
        if (config.requestId && config.timeout === undefined) {
          if(config.cancelDuplicate){
            fn.cancel(config.requestId);        
          }

          _activeRequests[config.requestId] = $q.defer();
          config.timeout = _activeRequests[config.requestId].promise;
        }
      };

      fn.cancel = function(requestId){
        var canceller = _activeRequests[requestId];
        if(canceller){
          canceller.promise.isUserCancelled = true;
          canceller.resolve('auto cancelled previous request of same type / id');
          console.log("REQEST CANCELLED :: " + requestId);
          fn.remove(requestId)
          return true;
        }
        return false;
      };

      fn.remove = function(requestId){
        delete _activeRequests[requestId];
      };

      fn.cancelAll = function(){
        for(var requestId in _activeRequests){
          fn.cancel(requestId);
        }
        console.log("CANCELLED ALL ACTIVE REQUESTS");
      };

      $rootScope.$on("$stateChangeSuccess", function(event, toState, toParams, fromState, fromParams) {
        fn.cancelAll();
      });

    }]);//requestCanceller


    //interceptor definition
  function httpRequestCancllerConfig($provide, $httpProvider) {
    // Intercept http calls.
    $provide.factory('HttpRequestCancllerInterceptor', ["$q", 'requestCanceller', function($q, requestCanceller) {
      return {
        // On request success
        request: function(config) {
          requestCanceller.add(config || {});
          // Return the config or wrap it in a promise if blank.
          return config || $q.when(config);
        },

        // On response success
        response: function(response) {
          var config = response.config; 

          // Return the response or promise.
          return response || $q.when(response);
        },

        // On response error
        responseError : function(response) {

          //capture user / auto cancelled REQUESTS
          if(response.config.timeout && response.config.timeout.isUserCancelled) {
            return $q.defer().promise;
          }

          return $q.reject(response);
        }
      };
    }]);

    // Add interceptor to the $httpProvider.
    $httpProvider.interceptors.push('HttpRequestCancllerInterceptor');

  }//httpRequestCancllerConfig

  angular
    .module('profiler')
    .config(httpRequestCancllerConfig)

})();