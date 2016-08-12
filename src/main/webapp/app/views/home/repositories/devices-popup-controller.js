(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('devicePopupController', ['$scope', '$uibModalInstance', 'modelsList', 'deviceService', 'deviceObject', function($scope, $uibModalInstance, modelsList, deviceService, deviceObject) {
      var vm = this;
      vm.deviceObject = angular.extend({}, deviceObject);
      vm.deviceModels = modelsList;
      
      vm.addDevice = function(deviceToAdd) {
        if ($scope.addDeviceForm.$valid) {
          deviceService.save(deviceToAdd).$promise.then(function(deviceObjectAdded) {
            $uibModalInstance.close(deviceObjectAdded);
          });
        }
      }; //addDevice

      vm.editDevice = function(deviceToUpdate) {
        if ($scope.editDeviceForm.$valid) {
          deviceService.update({id:deviceToUpdate.id}, deviceToUpdate).$promise.then(function(deviceObjectUpdated) {
            $uibModalInstance.close(deviceObjectUpdated);
          });
        }
      }; //editDevice

      vm.deleteDevice = function(deviceIdToDelete) {
        deviceService.delete({ id: deviceIdToDelete }).$promise.then(function() {
          $uibModalInstance.close(deviceIdToDelete);
        });
      }; //deleteDevice

    }]);

})();
