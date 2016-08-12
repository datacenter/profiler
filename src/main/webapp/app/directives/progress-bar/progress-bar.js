(function() {
  'use strict';
  var module = angular.module('maple.progress', []);

  var REQUEST_STATES = {
    'PROGRESS' : 'mpl-progress',
    'ERROR' : 'mpl-error',
    'SUCCESS' : 'mpl-success',
    'CLOSE' : 'mpl-clear'
  };

  var CLSSES_LIST = REQUEST_STATES.PROGRESS + ' ' + REQUEST_STATES.ERROR + ' ' +
                    REQUEST_STATES.SUCCESS + ' ' + REQUEST_STATES.CLOSE;

  module.service('mplprogService', ['$rootScope', function($rootScope) {

    this.progress = function(elementId) {
      broadcast(elementId, REQUEST_STATES.PROGRESS);      
    };
    this.error = function(elementId) {
      broadcast(elementId, REQUEST_STATES.ERROR);
    };
    this.success = function(elementId) {
      broadcast(elementId, REQUEST_STATES.SUCCESS);
    };
    this.close = function(elementId) {
      broadcast(elementId, REQUEST_STATES.CLOSE);
    };

    function broadcast(progressElemId, requestState) {
      $rootScope.$broadcast('MPL_PROGRESS_EVENT', { 
        progressElemId: progressElemId, 
        requestState: requestState
      });
    }

  }]);//service



  // Interceptor configuration
  module.config(["$provide", "$httpProvider", function httpInterceptorsConfig($provide, $httpProvider) {
    // Intercept http calls.
    $provide.factory('mplprogressHttpInterceptor', ["$q", "mplprogService", function($q, mplprogService) {
      return {
        // On request success
        request: function(config) {
          if(config.progressbar && config.progressbar.id){
            mplprogService.progress(config.progressbar.id);
          }
          // Return the config or wrap it in a promise if blank.
          return config || $q.when(config);
        } ,

        // On request failure
        requestError: function(rejection) {
          var config = rejection.config;
          if(config.progressbar && config.progressbar.id){
            mplprogService.error(config.progressbar.id);
          }
          // Return the promise rejection.
          return $q.reject(rejection);
        },

        // On response success
        response: function(response) {
          var config = response.config;
          if(config.progressbar && config.progressbar.id){
            mplprogService.success(config.progressbar.id);
          }

          // Return the response or promise.
          return response || $q.when(response);
        },

        // On response error
        responseError : function(response) {
          var config = response.config;
          if(config.progressbar && config.progressbar.id){
            mplprogService.error(config.progressbar.id);
          }

          return $q.reject(response);
        }
      };//return

    }]);//mplprogressHttpInterceptor

    // Add interceptor to the $httpProvider.
    $httpProvider.interceptors.push('mplprogressHttpInterceptor');
  }]);//httpInterceptorsConfig  



  module.directive('mplProgress', ['$timeout', function($timeout) {
    var config = {
      progress: 'Your request is in progress',
      error: 'Oops!, got an error while processing your request',
      success: 'Your request has been processed successfully',
      autoClose: false,
      autoCloseOnError: false,
      autoCloseOnSuccess: true,
      timeoutOnError: 1000,
      timeoutOnSuccess: 0,
      closeBtn: true,
      backDrop: true
    }

    function getTruthyVal(givenVal, defaultValue){
      return angular.isUndefined(givenVal) ? defaultValue : 
              (givenVal === 'false' || givenVal === '0') ? false : Boolean(givenVal);
    }

    function getTimeoutVal(givenVal, defaultValue){
      var givenVal = parseInt(givenVal);
      return isNaN(givenVal) ? defaultValue : givenVal;         
    }

    return {
      restrict: 'A',
      scope:true,
      templateUrl: function(element, attrs) {
        return attrs.templateUrl || 'app/directives/progress-bar/progress-bar.html';
      },
      link: function(scope, el, attrs) {
        var elementId = el.attr('id');
        el.addClass('mpl-progress-bar-container');

        scope.progressMessage = attrs.progressMessage || config.progress;
        scope.errorMessage = attrs.errorMessage || config.error;
        scope.successMessage = attrs.successMessage || config.success;
        scope.autoClose = getTruthyVal(attrs.autoClose, config.autoClose);
        scope.autoCloseOnError = getTruthyVal(attrs.autoCloseOnError, config.autoCloseOnError);
        scope.autoCloseOnSuccess = getTruthyVal(attrs.autoCloseOnSuccess, config.autoCloseOnSuccess);
        scope.closeBtn = getTruthyVal(attrs.closeBtn, config.closeBtn);
        scope.backDrop = getTruthyVal(attrs.backDrop, config.backDrop);
        scope.timeoutOnError = getTimeoutVal(attrs.timeoutOnError, config.timeoutOnError);
        scope.timeoutOnSuccess = getTimeoutVal(attrs.timeoutOnSuccess, config.timeoutOnSuccess);

        scope.close = function(){
          scope.canShow=false;
        }

        //listening to progress event
        scope.$on('MPL_PROGRESS_EVENT', function(event, data) {
          if(elementId!==data.progressElemId) return;
          
          el.removeClass(CLSSES_LIST);
          el.addClass(data.requestState);

          switch(data.requestState){
            case REQUEST_STATES.PROGRESS:
              scope.canShow = true;  
              break;
            case REQUEST_STATES.ERROR:            
              scope.canShow = true;
              if(scope.autoClose || scope.autoCloseOnError){
                clear(scope.timeoutOnError)                
              }
              break;
            case REQUEST_STATES.SUCCESS:
              scope.canShow = true;
              if(scope.autoClose || scope.autoCloseOnSuccess){
                clear(scope.timeoutOnSuccess);                
              }
              break;
            case REQUEST_STATES.CLOSE:
              scope.canShow = false;
              break;    
          }

          function clear(timeout){
            if(timeout){
              $timeout(function(){
                scope.canShow = false;
              }, timeout);  
            }else{
              scope.canShow = false;                  
            }
          }

        });//event listener

      }//link function
    }//directive definition object

  }]);

})();
