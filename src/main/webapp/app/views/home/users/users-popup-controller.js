(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('userPopupController', ['$scope', '$uibModalInstance', 'userService', 'USER_AUTH_TYPES', 'USER_ROLES', 'userObject', function($scope, $uibModalInstance, userService, USER_AUTH_TYPES, USER_ROLES, userObject) {
      var vm = this;
      vm.userObject = angular.extend({}, userObject);
      vm.userRoles = USER_ROLES;
      vm.authenticationTypes = USER_AUTH_TYPES;

      vm.addUser = function() {
        if ($scope.addUserForm.$valid) {
          userService.save(vm.userObject).$promise.then(function(userObjectAdded) {
            $uibModalInstance.close(userObjectAdded);
          });
        }
      }; //addUser

      vm.editUser = function(userToUpdate) {
        if ($scope.editUserForm.$valid) {
          userService.update({id:userToUpdate.id}, userToUpdate).$promise.then(function(userObjectUpdated) {
            $uibModalInstance.close(userObjectUpdated);
          });
        }
      }; //editUser

      vm.deleteUser = function(userIdToDelete) {
        userService.delete({ id: userIdToDelete }).$promise.then(function() {
          $uibModalInstance.close(userIdToDelete);
        });
      }; //deleteUser



    }]);

})();
