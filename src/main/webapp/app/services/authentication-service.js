(function() {
  'use strict';

  angular
    .module('profiler')
    .service('authenticationService', ["appService", function(appService) {

      this.login = function(username, password) {
        var config = {
            url: "/profiler/login",
            method: "POST",
            data: { "username": username, "password": password }
        };

        return appService.processRequest(config);
      };

      this.logout = function(username, password) {
        var config = {
            url: "/profiler/logout",
            method: "POST"
        };

        return appService.processRequest(config);
      };


    }]); //controller

})();
