(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('pluginsLeftPanelController', ['$scope', 'util',   function($scope, util) {
      var vm = this;
      vm.pluginsCtrl = $scope.$parent.pluginsCtrl;
      
      vm.selectVendor = function(index, vendorDesc){
        vm.selectedVendorIndex = index;
        vm.pluginsCtrl.vendorPluginsList = util.getItemsByPropVal(vm.pluginsCtrl.pluginsList, 'description', vendorDesc);
      };

      $scope.$on('$viewContentLoaded', function(event, viewConfig) {
        init();
      });

      function init() {
        var outerdeviceHeight = window.innerHeight - 130;
        $('.winHeight').css('height', outerdeviceHeight);
        vm.pluginsCtrl.getPlugins();
      }
    }]);

})();
