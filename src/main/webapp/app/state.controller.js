(function(){
  "use strict";

  angular
    .module('profiler')
    .run(['$rootScope', 'appService', function($rootScope, appService){
      $rootScope.root = {};

      $rootScope.$on("$stateChangeStart", function(event, toState, toParams, fromState, fromParams) {    
        if(toState.name !== 'login' && !appService.isUserLoggedIn()){
          event.preventDefault();
          appService.gotoView("login");
          return;
        }else if(toState && (toState.name==='home' || (!fromState.name && toState.name==='home.projectdetails') ) ){
          event.preventDefault();
          appService.gotoView("home.projects");
          return;
        }
          
        $rootScope.root.currentState = toState.name;
      });

    }]);




})();