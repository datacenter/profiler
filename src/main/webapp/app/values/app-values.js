(function(){
	'use strict';

  angular
  	.module('profiler')
  	//repository records table pagination options
    .value("paginationOptions", {
      totalItems: 100,
      currentPage: 1,
      itemsPerPage: 50,
      maxSize: 5
    })
    //repository records filter service options
    .value("repoFilterOptions", {
      searchString: '',
      startRecord: 1,
      numRecords: 50,
      startDate: null,
      endDate: null,
      sourceDeviceName: '',
      type: 'none',
      deviceId : 0
    })
    
})();