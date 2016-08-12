(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('projectPopupController', ['$scope', '$uibModalInstance', 'projectService', 'dashboardService', 'projectObject', function($scope, $uibModalInstance, projectService, dashboardService, projectObject) {
      var vm = this;
      vm.projectObject = angular.extend({}, projectObject);
      
      vm.addProject = function(projectToAdd) {
        if ($scope.addProjectForm.$valid) {
          // projectService.save(projectToAdd).$promise.then(function(projectObjectAdded) {
          //   $uibModalInstance.close(projectObjectAdded);
          // });
          dashboardService.addProject(projectToAdd, function(projectObjectAdded){
            $uibModalInstance.close(projectObjectAdded);
          });
        }
      }; //addProject

      vm.editProject = function(projectToUpdate) {
        if ($scope.editProjectForm.$valid) {
          // projectService.update({id:projectToUpdate.id}, projectToUpdate).$promise.then(function(projectObjectUpdated) {
          //   $uibModalInstance.close(projectObjectUpdated);
          // });
          // dashboardService.editProject(projectToUpdate.id, projectToUpdate, function(projectObjectUpdated) {
          //   $uibModalInstance.close(projectObjectUpdated);
          // });

          var dataToApi = {
            description : projectToUpdate.description,
            name : projectToUpdate.name
          };

          dashboardService.editProject(projectToUpdate.id, dataToApi, function(projectObjectUpdated) {
            console.log("DONE");
            console.log(projectObjectUpdated);
            $uibModalInstance.close(projectObjectUpdated);
          });
        }
      }; //editProject

      vm.deleteProject = function(projectIdToDelete) {
        // projectService.delete({ id: projectIdToDelete }).$promise.then(function() {
        //   $uibModalInstance.close(projectIdToDelete);
        // });        
        dashboardService.deleteProject(projectIdToDelete, function(response){
          $uibModalInstance.close(projectIdToDelete);
        });
      }; //deleteProject

    }]);

})();
