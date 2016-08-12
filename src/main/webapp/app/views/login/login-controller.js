(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('loginController', ["$scope", "authenticationService", "appService", function($scope, authenticationService, appService) {
      var vm = this;

      function init() {
        vm.username = '';
        vm.password = '';
        vm.isCredentialsIncorrect = false;
      }

      vm.onFocuFormInput = function(){
        vm.isCredentialsIncorrect = false;
      }

      vm.login = function() {
        if ($scope.loginForm.$valid) {
          authenticationService.login(vm.username, vm.password).then(function(response) {
            appService.setUserDetails(response);
            appService.gotoView("home");
          }, function() {
            //error in ajax call
            vm.isCredentialsIncorrect = true;
          });
        }
      };


      $scope.$on('$viewContentLoaded', function() {
        init();
        console.log("Loaded");
      })


    }]); //controller

})();
