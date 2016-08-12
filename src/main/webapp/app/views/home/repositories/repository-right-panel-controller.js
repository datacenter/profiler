(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('repositoryRightPanelController', ['$scope', '$timeout', '$uibModal', 'OBJECT_TYPES', 'IMPORT_STATUS', 'REQUEST_TYPE', 'SlimScrollConfig', 'requestCanceller', 'util', 'paginationOptions', 'repoFilterOptions', 'mockService', 'repoService', 'repoTestService', 'popupService', function($scope, $timeout, $uibModal, OBJECT_TYPES, IMPORT_STATUS, REQUEST_TYPE, SlimScrollConfig, requestCanceller, util, paginationOptions, repoFilterOptions, mockService, repoService, repoTestService, popupService) {
      var vm = this;
      vm.repoCtrl = $scope.$parent.repoCtrl;
      
      vm.isViewLoaded = false;
      vm.OBJECT_TYPES = OBJECT_TYPES;
      vm.IMPORT_STATUS = IMPORT_STATUS;      
      vm.repoFilter = angular.extend({}, repoFilterOptions);
      vm.pagination = angular.extend({}, paginationOptions);
      vm.reposList = [];
      vm.canShowstartDateCalPopup = false;
      vm.canShowEndDateCalPopup = false;
      vm.startDate = null,
      vm.endDate = null,
      vm.dateFormat = 'dd/MM/yyyy'; //'dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'
      vm.pageNumToGo = 1;
      vm.isRepoRequestInProgress = false;
      var _repoRecordsSelected = [];
      var isPageNumberchangeByFilter = false;
      var prevRepoFilterType = "";//vm.repoFilter.type;

      function onDeviceSelectedChanged(selectedDevice){      
        //resetting the fileter options to defaults
        vm.repoFilter = angular.extend({}, repoFilterOptions);
        vm.startDate = null;
        vm.endDate = null;
        vm.reposList.length = 0;
        if(vm.isRepoRequestInProgress){
          vm.isRepoRequestInProgress = false;
          requestCanceller.cancel(REQUEST_TYPE.REPO_RECORDS_QUERY);
        }
        vm.repoCtrl.selectedDevice = selectedDevice;
        console.log( "vm.repoCtrl.selectedDevice :: " , vm.repoCtrl.selectedDevice )

        //if the device is already imported then only show the repo records 
        if(vm.repoCtrl.selectedDevice.importedStatus === vm.IMPORT_STATUS.SUCCESS || 
           vm.repoCtrl.selectedDevice.importedStatus === vm.IMPORT_STATUS.PROGRESS ) {
          vm.getRepos(true);
        }else{
          //if the device is not imported/failed then show the import button 
          //if the device is importing, show the progress 
        }
      }

      function init(){
        var outerdeviceHeight = window.innerHeight - 130;
        var innerdeviceHeight = window.innerHeight - 190;
        SlimScrollConfig.height = innerdeviceHeight;
        $('#rightScrollContainer').css('height',outerdeviceHeight);
         $('.repos-list').slimScroll({distance: '1px',opacity: 1,height: outerdeviceHeight-145,size: '5px',color: '#03a2e3',alwaysVisible: true});
        $(".fixedTable").tableHeadFixer();

        vm.isViewLoaded = true;
        vm.pagination.currentPage = 1;
        vm.repoFilter.startRecord = 1;
        vm.pageNumToGo = 1;

        $timeout(function(){
          vm.getRepos();
        }, 10);
        
      }

      /* ============ EVENT LISTENERS ============ */
      $scope.$on('PROFILER_DEVICE_SELECTED', function(eventInfo, evtData) {
        console.log( " GOT notification :: " , evtData )
        onDeviceSelectedChanged(evtData.device);
      });

      $scope.$on('$viewContentLoaded', function(event, viewConfig) {
        init();
      });

      vm.getRepos = function(resetStartRecord, isFilterGoClicked) {
        if(resetStartRecord){
          vm.pagination.currentPage = 1;
          vm.repoFilter.startRecord = 1;
          vm.pageNumToGo = 1;
          isPageNumberchangeByFilter = true;
        }
        if(isFilterGoClicked){
          prevRepoFilterType = vm.repoFilter.type;
        }else{
          vm.repoFilter.type = prevRepoFilterType;
        }
        vm.repoFilter.startDate = util.getFormmatedDate(vm.startDate);
        vm.repoFilter.endDate = util.getFormmatedDate(vm.endDate);
        vm.repoFilter.sourceDeviceName = vm.repoCtrl.selectedDevice.name || '';
        vm.repoFilter.deviceId = vm.repoCtrl.selectedDevice.id || 0 ;
        
        vm.isRepoRequestInProgress = true;
        repoTestService.getRepos(vm.repoFilter)
          .then(function(response) {
            vm.reposList = response.records || [];
            vm.pagination.totalItems = response.totalRecords;
            vm.isRepoRequestInProgress = false;
            isPageNumberchangeByFilter = false;
          }, function() {
            console.log("Error");
            vm.isRepoRequestInProgress = false;
            isPageNumberchangeByFilter = false;
          });
        vm.reposList.length = 0;        
      };// getRepos

      vm.setPage = function(pageNum) {
        vm.pagination.currentPage = pageNum;
        vm.onPageChanged();
      };// setPage

      vm.onPageChanged = function() {
        vm.pagination.currentPage = (vm.pagination.currentPage < 1) ? 1 : vm.pagination.currentPage;
        vm.repoFilter.startRecord = (vm.pagination.currentPage - 1) * vm.pagination.itemsPerPage + 1;
        vm.pageNumToGo = vm.pagination.currentPage;        
        _repoRecordsSelected.length = 0;
        vm.selectedRow = -1;
        if(!isPageNumberchangeByFilter){
          vm.getRepos();  
        }        

        isPageNumberchangeByFilter = false;
      };// onPageChanged

      vm.toggleRepoRecordSelection = function(event, recordIndex) {
        var index = _repoRecordsSelected.indexOf(recordIndex);
        
        event.stopPropagation();
        event.stopImmediatePropagation();        
        if(index===-1){
          _repoRecordsSelected.length = 0;//this is to be removed in case of multi selection required
          _repoRecordsSelected.push(recordIndex);
        }else{
          _repoRecordsSelected.splice(index, 1);
        }
      };// toggleRepoRecordSelection

      vm.isRecordSelected = function(index) {
        return (_repoRecordsSelected.indexOf(index) !== -1);
      };// isRecordSelected

      vm.deleteRepoRecords = function() {
        if(_repoRecordsSelected.length===0) return;
        // _repoRecordsSelected.sort(function(a, b){return b-a;});
        // for(var i=0; i<_repoRecordsSelected.length; i++){
        //   vm.reposList.splice(_repoRecordsSelected[i], 1);
        // }
        var repoRecordToDelete = vm.reposList[_repoRecordsSelected[0]];
        
        var deleteRepoModal = $uibModal.open({
          templateUrl: 'app/views/home/repositories/modals/deleteRepoObject.html',
          size: 'sm',
          scope: $scope.$new(),
          controller: function($scope, repoTestService, repoObject){
            var vm1 = this;
            vm1.repoObject = repoObject;
            vm1.deleteRepoObject = function(repoObject){
              var id = repoObject.id;
              var type = OBJECT_TYPES[repoObject.objectType].repoTypeRestReq;
              repoTestService.deleteRepoRecords(id, type).then(function() {
                for (var i = 0; i < _repoRecordsSelected.length; i++) {
                  vm.reposList.splice(_repoRecordsSelected[i], 1);
                }
                _repoRecordsSelected.length = 0;

                //close popup window
                deleteRepoModal.close();
              });    
            }
          },
          controllerAs: 'deleteRepoObjCtrl',
          resolve: {
            repoObject: function() {
              return repoRecordToDelete;
            }
          }
        });//uibmodal

      };// deleteRepoRecords

      vm.importRepoRecords = function(deviceToImportFrom) {
        var importingDeviceId = deviceToImportFrom.id;
        if(!importingDeviceId){
          return;
        }

        function updateDeviceStatus(){
          //if the user selected device is same as that is in progress of importing, get repo records & dispaly them in UI
          // if(vm.reposList.length===0 && vm.repoCtrl.selectedDevice.id === importingDeviceId){
          if(vm.repoCtrl.selectedDevice.id === importingDeviceId){
            //this is to update the device status in left panel as well updating the inforamtion in right panel
            vm.repoCtrl.getDevicesList(true);
          }else{
            //this is to update the device status in left panel only
            vm.repoCtrl.getDevicesList(false);
          }
        }
        
        repoTestService.importRepoRecords(importingDeviceId).then(function() {
          // deviceToImportFrom.importedStatus = vm.IMPORT_STATUS.SUCCESS;
          updateDeviceStatus();
        }, function() {
          // deviceToImportFrom.importedStatus = vm.IMPORT_STATUS.FAIL;
          updateDeviceStatus();
        });

        deviceToImportFrom.importedStatus = vm.IMPORT_STATUS.PROGRESS;
        // vm.repoCtrl.getDevicesList();
        
      };// importRepoRecords

      /*========= OBJECT DETAILS POPUP */
      vm.showObjectDetails = function(repoObject, rowIndex){
        // if(vm.selectedRow === rowIndex){
        //   vm.selectedRow = -1;
        // }else{
        //   vm.selectedRow = rowIndex;
        // }
        vm.selectedRow = rowIndex;
              
        var objType = OBJECT_TYPES[repoObject.objectType].repoTypeRestReq;
        repoTestService.getObjectDetails(repoObject.id, objType).then(function(detailsObject){
          popupService.showObjectDetails($scope.$new(), detailsObject, repoObject.objectType);
        });
      };//showObjectDetails

      vm.editRepoPopup = function() {
        if(_repoRecordsSelected.length===0) return;
        
        $uibModal.open({
          templateUrl: 'app/views/home/repositories/modals/editRepo.html',
          size: 'sm',
          scope: $scope,
          resolve: {

          }
        }).result.then(function() {}, function() {
          console.log('Dismiss');
        });
       };


    }]);

})();
