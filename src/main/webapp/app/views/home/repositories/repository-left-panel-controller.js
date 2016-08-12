(function() {
  'use strict';


  angular.module('profiler').controller('repositoryLeftPanelController', ['$scope', '$timeout', 'IMPORT_STATUS', 'SlimScrollConfig', 'modelService', '$uibModal', function($scope, $timeout, IMPORT_STATUS, SlimScrollConfig, modelService, $uibModal) {
    var _modelsList = [];
    var vm = this;
    vm.repoCtrl = $scope.$parent.repoCtrl;
    vm.IMPORT_STATUS = IMPORT_STATUS;

    $scope.$on('PROFILER_DEVICE_LIST_UPDATED', function(eventInfo, devicesList) {
      vm.repoCtrl.devicesList = devicesList;

    });

    
    vm.selectDevice = function(selectedDevice){ 
      vm.repoCtrl.selectedDevice = selectedDevice;
      $scope.$parent.$broadcast('PROFILER_DEVICE_SELECTED', {device:vm.repoCtrl.selectedDevice}); 
    };
   
    vm.addDevicePopup = function() {
      $uibModal.open({
        templateUrl: 'app/views/home/repositories/modals/adddevice.html',
        size: 'sm',
        scope: $scope,
        controller: 'devicePopupController',
        controllerAs: 'popupCtrl',
        resolve: {
          deviceObject: function() {
            return null;
          },
          modelsList: function(){
            return _modelsList;
          }
        }
      }).result.then(function(deviceObject) {
        // vm.repoCtrl.getDevicesList();

        // // keep the newly added device at the bottom & selected
        vm.repoCtrl.devicesList.push(deviceObject);
        vm.repoCtrl.setSelectedDeviceIndex(vm.repoCtrl.devicesList.length-1);

        // // keep the newly added device at the top & selected
        // vm.repoCtrl.devicesList.unshift(deviceObject);
        // vm.repoCtrl.setSelectedDeviceIndex(0);        

      }, function() {
        console.log('Dismiss');
      });
    };

    /*Edit device popup*/

    vm.editDevicePopup = function(event, deviceToEdit) {
    	event.preventDefault();
    	event.stopPropagation();
      
			deviceToEdit.modelId = deviceToEdit.modelDetails.id;
			
      $uibModal.open({
        templateUrl: 'app/views/home/repositories/modals/editdevice.html',
        size: 'sm',
        scope: $scope,
        controller: 'devicePopupController',
        controllerAs: 'popupCtrl',
        resolve: {
          deviceObject: function() {
            return deviceToEdit;
          },
          modelsList: function(){
            return _modelsList;
          }
        }
      }).result.then(function(deviceObject) { 
      	vm.repoCtrl.getDevicesList();
      }, function() {
        console.log('Dismiss');
      });
    };

    vm.deleteDevicePopup = function(event, deviceToDelete) {
    	event.preventDefault();
    	event.stopPropagation();

      $uibModal.open({
        templateUrl: 'app/views/home/repositories/modals/deletedevice.html',
        size: 'sm',
        scope: $scope,
        controller: 'devicePopupController',
        controllerAs: 'popupCtrl',
        resolve: {
          deviceObject: function() {
            return deviceToDelete;
          },
          modelsList: function(){
            return _modelsList;
          }
        }
      }).result.then(function(deletedDeviceId) {        
        // vm.repoCtrl.getDevicesList();

        for(var i=0; i<vm.repoCtrl.devicesList.length; i++){
          if(vm.repoCtrl.devicesList[i].id === deletedDeviceId){
            vm.repoCtrl.devicesList.splice(i, 1);
            break;
          }  
        }
        vm.repoCtrl.setSelectedDeviceIndex(vm.repoCtrl.selectedDeviceIndex);

      }, function() {
        console.log('Dismiss');
      });
    } //deleteDevicePopup

    $scope.$on('$viewContentLoaded', function(event, viewConfig) {
      init();
    });


    function init() {
      var outerdeviceHeight = window.innerHeight - 130;
      var innerdeviceHeight = window.innerHeight - 190;
      SlimScrollConfig.height = innerdeviceHeight;
      $('#devices-container').height(outerdeviceHeight);
      $('#leftScrollContainer').slimScroll(SlimScrollConfig);
      
      vm.repoCtrl.getDevicesList();
      _modelsList = modelService.query();

      console.log()
    }

  }]);

})();
