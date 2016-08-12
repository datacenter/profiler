(function() {
  'use strict';
  angular
    .module('profiler')
    .service('popupService', ['$uibModal', '$q', function($uibModal, $q) {
      var fn = this;

      fn.showObjectDetails = function(scope, detailsObject, detailsObjectType) {
        $uibModal.open({
          templateUrl: 'app/views/home/repositories/modals/object-details.html',
          scope: scope,
          controller: function($scope, inputObject, objectType) {
            var vm = this;
            vm.objectType = objectType;
            vm.setContract = function(event, contract) {
              vm.selectedItem = contract;
              vm.contract = contract;
              vm.view = 'contract';
            };
            vm.setFilter = function(event, filter) {
              vm.selectedItem = filter;
              vm.filter = filter;
              vm.view = 'filter';
            };
            vm.setFilterEntry = function(event, filterEntry) {
              vm.selectedItem = filterEntry;
              vm.filterEntry = filterEntry;
              vm.view = 'filterEntry';
            };
            switch (objectType) {
              //@Manohar :: here the case match strings are "value of objectType" of repo objects
              case 'contract':
                vm.setContract(null, inputObject);
                break;
              case 'filter':
                vm.setFilter(null, inputObject);
                break;
              case 'filter entry':
                vm.setFilterEntry(null, inputObject);
                break;
            }
          },
          controllerAs: 'contractCtrl',
          resolve: {
            inputObject: function() {
              return detailsObject;
            },
            objectType: function() {
              return detailsObjectType;
            }
          }
        })//
      };//showObjectDetails


      fn.showContractSelection = function(scope){
        var deferred = $q.defer();
        var modalInstance = $uibModal.open({
          templateUrl: 'app/views/home/projects/details/modals/contractConfig.html',
          size: 'lg',
          scope: scope,
          controller: ['$scope', 'util', 'repoFilterOptions', 'paginationOptions', 'repoTestService', function($scope, util, repoFilterOptions, paginationOptions, repoTestService) {
            var vm = this;
            vm.isRepoRequestInProgress = false;
            vm.repoFilter = angular.extend({}, repoFilterOptions);
            vm.pagination = angular.extend({}, paginationOptions);
            vm.canShowstartDateCalPopup = false;
            vm.canShowEndDateCalPopup = false;
            vm.reposList = [];
            vm.startDate = null,
            vm.endDate = null,
            vm.dateFormat = 'dd/MM/yyyy'; //'dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'
            vm.pageNumToGo = 1;            
            vm.selectedContract = null;

            modalInstance.rendered.then(function(){
              vm.getRepos(true);
              vm.outerdeviceHeight = window.innerHeight - 130; 
              $('.repos-list').slimScroll({distance: '1px',opacity: 1,height: vm.outerdeviceHeight-145,size: '5px',color: '#03a2e3'});
              $(".fixedTable").tableHeadFixer();
            });

            modalInstance.result.then(function(userObject) {
              deferred.resolve(vm.selectedContract);
            }, function(error) {
              console.log('dismissed')
              deferred.reject('dismissed');
            });

            vm.selectRow = function(rowIndex){
              vm.selectedRowIndex = rowIndex; 
              vm.selectedContract = vm.reposList[rowIndex];
            };

            vm.getRepos = function(resetStartRecord) {
              if (vm.isRepoRequestInProgress) return;
              if(resetStartRecord){
                vm.pagination.currentPage = 1;
                vm.repoFilter.startRecord = 1;
                vm.pageNumToGo = 1;
              }
              vm.repoFilter.startDate = util.getFormmatedDate(vm.startDate);
              vm.repoFilter.endDate = util.getFormmatedDate(vm.endDate);
              vm.repoFilter.type = 'contract';
              vm.isRepoRequestInProgress = true;
              repoTestService.getRepos(vm.repoFilter).then(function(response) {
                vm.reposList = response.records || [];
                vm.pagination.totalItems = response.totalRecords;
                vm.isRepoRequestInProgress = false;
              }, function() {
                // console.log("Error");
                vm.isRepoRequestInProgress = false;
              });

            };// getRepos

            vm.setPage = function(pageNum) {
              vm.pagination.currentPage = pageNum;
              vm.onPageChanged();
            };// setPage

            vm.onPageChanged = function() {
              vm.repoFilter.startRecord = vm.pagination.currentPage * vm.pagination.itemsPerPage;
              vm.pageNumToGo = vm.pagination.currentPage;
              vm.getRepos(false);
            };// onPageChanged

          }],//controller
          controllerAs: 'contrCtrl'
        })//modal

        return deferred.promise;

      };//showContractSelection

      fn.showPushToApicPopup = function(tenants, apicDevices){
        var deferred = $q.defer();
        
        var modalInstance = $uibModal.open({
          templateUrl: 'app/views/home/projects/details/modals/pushapic_popup.html',
          size: 'sm',
          controller : ['$scope', 'projectDetailsService', function($scope, projectDetailsService){
            var vm = this;
            vm.tenants = tenants;
            vm.selectedTenant = null;
            vm.selectedTenantIndxs = {};
            vm.selectedApicDevice = null;
            vm.apicDevices = apicDevices;
            
            vm.toggleTenantSelection = function(event, index, tenant){
              event.preventDefault();
              if(event.target.tagName === 'INPUT') return;

              var _isAtleatOneTenantSelected = false;
              vm.selectedTenant = tenant; 
              vm.selectedTenantIndxs = {};  //remove this when implementing multi-select
              vm.selectedTenantIndxs[index] = !vm.selectedTenantIndxs[index];
              for(var key in vm.selectedTenantIndxs){
                if(vm.selectedTenantIndxs[key]){
                  _isAtleatOneTenantSelected = true;
                  break;
                }
              }
              vm.hiddenValue = _isAtleatOneTenantSelected ? 'yes' : '';
            };
            vm.pushToApic = function(){
              console.log(vm.selectedTenant , vm.selectedApicDevice);
              if(vm.selectedTenant && vm.selectedApicDevice){
                projectDetailsService.pushToApic(vm.selectedTenant.id, vm.selectedApicDevice.id).then(function(){
                  console.log('pushed to APIC');
                  modalInstance.close();
                  deferred.resolve(vm.selectedApicDevice);
                }, function(){
                  console.log('error in pushing');
                  deferred.reject('error');
                }); 
              }
            };
          }],
          controllerAs : 'apicCtrl'
        });

        return deferred.promise;
      };//showPushToApicPopup

    }])

})();
