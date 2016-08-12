(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('homeController', function(appService, $scope, $rootScope, $timeout) {
      var vm = this;

      function homeFunction() {
        $scope.mainContainer = window.innerHeight - 60;
        $('#mainScroll').slimScroll({ distance: '1px', opacity: 1, height: $scope.mainContainer, size: '5px', color: '#03a2e3' });
      }

      $rootScope.$on('$viewContentLoaded', function(event, viewConfig) {
        $timeout(homeFunction, 300)
      });
    });

})();
