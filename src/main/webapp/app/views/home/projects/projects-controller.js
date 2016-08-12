(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('projectsController', ['$scope', '$timeout', '$uibModal', 'mockService', 'projectService', 'dashboardService', 'appService', '$rootScope', function($scope, $timeout, $uibModal, mockService, projectService, dashboardService, appService, $rootScope) {

      var vm = this;

      vm.viewProjectDetails = function(project) {
        console.log(project);
        $rootScope.projectId = project.id;
        $rootScope.isProjectVisible = true;
        $rootScope.currProject = project;
        appService.gotoView('home.projectdetails');
      }

      vm.addProjectPopup = function() {
        $uibModal.open({
          templateUrl: 'app/views/home/projects/modals/addproject.html',
          size: 'sm',
          scope: $scope,
          controller: 'projectPopupController',
          controllerAs: 'popupCtrl',
          resolve: {
            projectObject: function() {
              return null;
            }
          }
        }).result.then(function(projectObject) {
          vm.projectsList.push(projectObject);
        }, function() {
          // console.log('CANCEL');
        });
      };

      vm.editProjectPopup = function(projectToEdit, index) {
        $uibModal.open({
          templateUrl: 'app/views/home/projects/modals/editproject.html',
          size: 'sm',
          scope: $scope,
          controller: 'projectPopupController',
          controllerAs: 'popupCtrl',
          resolve: {
            projectObject: function() {
              return projectToEdit;
            }
          }
        }).result.then(function(projectObject) {
          vm.projectsList[index] = projectObject;
        }, function() {
          // console.log('CANCEL');
        });
      };

      vm.deleteProjectPopup = function(projectToDelete) {
        $uibModal.open({
          templateUrl: 'app/views/home/projects/modals/deleteproject.html',
          size: 'sm',
          scope: $scope,
          controller: 'projectPopupController',
          controllerAs: 'popupCtrl',
          resolve: {
            projectObject: function() {
              return projectToDelete;
            }
          }
        }).result.then(function(deletedProjectId) {
          for (var i = 0; i < vm.projectsList.length; i++) {
            if (vm.projectsList[i].id == deletedProjectId) {
              vm.projectsList.splice(i, 1);
              break;
            }
          }
        }, function() {
          console.log('Dismiss');
        });

      }; //deleteProjectPopup

      $scope.$on('$viewContentLoaded', function(event, viewConfig) {
        $timeout(function() {
          // vm.projectsList = projectService.query();
          dashboardService.getProjects(function(response){
            vm.projectsList = response;
          });
        }, 300)
      });

    }]);

})();
