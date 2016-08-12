(function() {
  'use strict';
  angular
    .module('profiler')
    .service('mockService', [ 'appService', function(appService) {
      
      this.getProjects = getProjects;
      this.getDevices = getDevices;
      this.getRepos = getRepos;
      this.getContractDetails = function(){
        var config = {
          method: 'GET',
          url: '/mockdata/contract-details.json'
        }
        return appService.processRequest(config);
      };
      this.getFilterDetails = function(){
        var config = {
          method: 'GET',
          url: '/mockdata/filter-details.json'
        }
        return appService.processRequest(config);
      };
      this.getFilterEntryDetails = function(){
        var config = {
          method: 'GET',
          url: '/mockdata/filter-entry-details.json'
        }
        return appService.processRequest(config);
      };



    }]);

  return;

  function getProjects(projectsCount) {
    var list = [];
    projectsCount = projectsCount || 50;
    for (var i = 0; i < projectsCount; i++) {
      list.push({
        "name": "Maple Labs " + i,
        "createdDate": "17 May 2016",
        "anps": parseInt(Math.random() * 50),
        "vrfCount": parseInt(Math.random() * 50),
        "bdCount": parseInt(Math.random() * 50),
        "epgCount": parseInt(Math.random() * 50),
        "endPoints": parseInt(Math.random() * 50),
        "contractCount": parseInt(Math.random() * 50)
      });
    }
    return list;
  }

  function getRepos(reposCount) {
    var list = [];
    reposCount = reposCount || 50;
    for (var i = 0; i < reposCount; i++) {
      list.push({
        id: i,
        sourceDevice: 'Source Device 0' + i,
        objectType: 'Test Object 0' + i,
        objectName: 'Test Object Internal 0' + i,
        // objectName : 'Test Object User 0'+i,
        objectVersion: 'v.0.1.' + i,
        importedOn: '12th Mar 2016',
        auditedon: '12th May 2016',
        auditstatus: i % 2 ? 'complete' : 'progress'
      });
    }
    return {
      totalRecords : list.length, 
      records : list
    };
  }

  function getDevices(devicesCount) {
    var list = [];
    devicesCount = devicesCount || 50;
    for (var i = 0; i < devicesCount; i++) {
      list.push({
        id: 'DEVICE_ID_'+i,
        name: 'Device 0' + i,
        ipAddress: '120.0.0.'+i,
        username: 'user' + i,
        password: 'user' + i,
        itmesCount: parseInt(Math.random() * 50),
        date: (i+1)+' June 2016'
      });
    }
    return list;
  }


})();
