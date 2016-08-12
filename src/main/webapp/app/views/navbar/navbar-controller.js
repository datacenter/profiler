(function(){
	'use strict';

  angular
  	.module('profiler')
    .controller('navbarController', ['$scope', 'appService', function($scope, appService){
      var vm = this;
      var ngBody = angular.element('body');

      //hide the logout menu option on click of any item in the view
      ngBody.bind('click', function(){
        if(vm.canShowLogoutMenu){
          vm.canShowLogoutMenu = false;
          $scope.$apply();  
        }
      });

      vm.canShowLogoutMenu = false;
      vm.userInfo = appService.getUserInfo();

      vm.toggleLogoutDropdown = function(event){
        event.stopPropagation();
        vm.canShowLogoutMenu = !vm.canShowLogoutMenu;
      };

      vm.logout = function(event){
        event.stopPropagation();
        appService.logout();
      };

    }]);

})();