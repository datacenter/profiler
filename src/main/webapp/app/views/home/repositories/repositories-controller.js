(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('repositoriesController', ['$scope', '$rootScope', 'deviceService', function($scope, $rootScope, deviceService) {
      var vm = this;
      vm.devicesList = [];

      vm.selectedDevice = {};
      vm.selectedDeviceIndex = -1;

      vm.getDevicesList = function(shouldNotifyDeviceSelection){
        console.log( "shouldNotifyDeviceSelection :: " + shouldNotifyDeviceSelection );
        deviceService.query().$promise.then(function(data) {
          vm.devicesList = data;
          $rootScope.$broadcast('PROFILER_DEVICE_LIST_UPDATED', data);
          if(shouldNotifyDeviceSelection){
            notifyDeviceSelection();
          }
        });
      };

      vm.setSelectedDeviceIndex = function(index){
        if(vm.selectedDeviceIndex != index){
          vm.selectedDeviceIndex = index;
          notifyDeviceSelection();  
        }        
      };

      function notifyDeviceSelection(){
        vm.selectedDevice = vm.devicesList[vm.selectedDeviceIndex] || {};          
        console.log( "notifyDeviceSelection :: " , vm.selectedDevice );
        $rootScope.$broadcast('PROFILER_DEVICE_SELECTED', {device:vm.selectedDevice});
      };

    }]);

})();
