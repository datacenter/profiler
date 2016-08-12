(function(){
	'use strict';

  angular
  	.module('profiler')
    .controller('pluginsRightPanelController', ['$scope','$uibModal','modelService', function($scope,$uibModal,modelService){
    	var vm = this;   
      vm.pluginsCtrl = $scope.$parent.pluginsCtrl; 


      vm.addModelPopup = function() {
        $uibModal.open({
          templateUrl: 'app/views/home/plugins/modals/addModels.html',
          size: 'sm',
          scope: $scope,
          controller: 'modelPopupController',
          controllerAs: 'modelPopupCtrl',
          resolve: {
            modelObject: function() {
              return null;
            },
            pluginsList: function(){
              return vm.pluginsCtrl.pluginsList;
            }
          }
        }).result.then(function(deviceObject) {
          vm.updateModelsList();
          vm.pluginsCtrl.getPlugins();
        }, function() {
          console.log('Dismiss');
        });
      };

      vm.updateModelsList = function(){        
        vm.modelsList = modelService.query();
      }

      vm.getSupportedModels = function(supportedModels){
        var i, modelsStr = '';
        if(supportedModels && supportedModels.length){
          modelsStr += supportedModels[0].name;
          for(var i=1; i<supportedModels.length; i++){
            modelsStr += ', ' + supportedModels[i].name;
          }
        }        
        return modelsStr;
      };

      $scope.$on('$viewContentLoaded', function(event, viewConfig) {
        init();
      });

      function init() {
        var outerdeviceHeight = window.innerHeight - 130;
        vm.updateModelsList();
        $('.winHeight').css('height', outerdeviceHeight);
      }
    }]);

})();