(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('modelPopupController', ['$scope', '$uibModalInstance', 'pluginsList', 'modelService', 'modelObject', function($scope, $uibModalInstance, pluginsList, modelService, modelObject) {
      var vm = this;
      vm.modelObject = angular.extend({}, modelObject);
      vm.modelPlugins = pluginsList;
      
      vm.addModel = function(modelToAdd) {
        if ($scope.addModelForm.$valid) {
          modelService.save(modelToAdd).$promise.then(function(modelObjectAdded) {
            $uibModalInstance.close(modelObjectAdded);
          });
        }
      }; //addModel

      vm.editModel = function(modelToUpdate) {
        if ($scope.editModelForm.$valid) {
          modelService.update({id:modelToUpdate.id}, modelToUpdate).$promise.then(function(modelObjectUpdated) {
            $uibModalInstance.close(modelObjectUpdated);
          });
        }
      }; //editModel

      vm.deleteModel = function(modelIdToDelete) {
        modelService.delete({ id: modelIdToDelete }).$promise.then(function() {
          $uibModalInstance.close(modelIdToDelete);
        });
      }; //deleteModel

    }]);

})();
