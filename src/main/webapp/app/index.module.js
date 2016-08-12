(function() {
  'use strict';

  var ngModules = [
    // 'ngAnimate',
    // 'ngSanitize',
    'ngResource',
    'ngStorage',
    'ui.router',
    'ui.bootstrap'
  ];
  
  var externalModules = [ 
    'aci.directives',
    'xeditable',
    'widget.scrollbar',
    'chart.js' 
  ];
  
  var appModules = [    
    'maple.formvalidation',
    'maple.progress'
  ];

  var modulesToInject = [];
  modulesToInject = modulesToInject.concat(ngModules);
  modulesToInject = modulesToInject.concat(externalModules);
  modulesToInject = modulesToInject.concat(appModules);

	angular.module('profiler', modulesToInject);
	
  // angular
  //   .module('profiler', ['ngAnimate', 'ngResource', 'ui.router', 'ui.bootstrap', 'toastr']);

})();
