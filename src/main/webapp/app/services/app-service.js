(function(){
  'use strict';

  angular
    .module('profiler')
    .service("appService", ["$http", "$q", "$state", "$sessionStorage", function($http, $q, $state, $sessionStorage){
      
      this.setUserDetails = function(userData){
        var userDetails = {};
        userDetails.name = userData.username;
        userDetails.role = userData.role;
        userDetails.id = userData.userId;

        $sessionStorage._userInfo = userDetails;
        $sessionStorage._isUserLoggedIn = true;
        $sessionStorage._token = userData.jwtKey; 
      };

      this.getUserInfo = function(){
        return $sessionStorage._userInfo || {};
      };

      this.getToken = function(){
        return $sessionStorage._token;
      };

      this.isUserLoggedIn = function(){
        return $sessionStorage._isUserLoggedIn;
      };

      this.logout = function(){
        $sessionStorage._isUserLoggedIn = false;
        $sessionStorage._userInfo = {};
        $sessionStorage._token = '';
        window.sessionStorage.clear();
        this.gotoView("login");
      };

      this.gotoView = function(viewName){
        $state.go(viewName);
      };

      this.processRequest = function(config){
        var deferred = $q.defer();
        $http(config).success(function (response) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
      };


    }]);//appService

})();