(function() {
  'use strict';

  angular
    .module('profiler')
    .run(runBlock);

  /** @ngInject */
  function runBlock($log, $rootScope) {

  	$rootScope.logicalURL = '/profiler/v1/projects/';

    // $log.debug('runBlock end');
  }

})();
