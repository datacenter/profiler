angular.module('profiler').factory("logicService",
    ['$http', '$q', 'SERVICE_STATIC_JSON', "REST_URI", "$rootScope", function ($http, $q, SERVICE_STATIC_JSON, REST_URI, $rootScope,ngDialog) {
        var service = {};

        //$rootScope.projectId = 387;
        var UTILITY="utility";
        
        $rootScope.canceler = $q.defer();
        
        service.getUtility=function(){
            return UTILITY;
        }
       
        service.getApps = function (callback) {
            $http({
                url: SERVICE_STATIC_JSON.getAppsUri,
                method: "GET",
            }).success(function (response) {
                callback(response);
            }).error(function (response) {
                console.log("error in api call");
                //$rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: response.error,
                    message: response.message
                }, "error");
            });
        }

        service.getProjectById = function (callback) {
               $http({
                url: REST_URI.baseUri + '/projects/' + $rootScope.projectId,
                method: "GET"
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
               // $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
     
        service.postJson = function (jsonObj, callback) {
            // url needs to be changed
            var url = '/acisizer/test';
            var res = $http.post(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });

        }


        service.addContract = function (jsonObj, callback) {
            //http://localhost:8080/acisizer/v1/project/1/tenant/1/app/1/epg
            //http://localhost:8080/acisizer/v1/project/1/tenant/2/contract
            var reqJson = {
                "name": jsonObj.name,
                "id": 0,
                "noOfFilters": jsonObj.count,
                "providerId": jsonObj.providerId.value,
                "consumerId": jsonObj.consumerId.value,
                "providerType": jsonObj.providerId.type,
                "consumerType": jsonObj.consumerId.type,
                "providerEnforced": jsonObj.providerEnforced,
                "consumerEnforced": jsonObj.consumerEnforced,
                "type":jsonObj.type,
                "subjects":jsonObj.subjects,
                "configName":jsonObj.configName
                
            };
            reqJson.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            };
            // url needs to be changed
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/app/' + $rootScope.appId + '/contract';
            var res = $http.post(url, reqJson);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
                callback(status);
            });
        }

        service.getContractDetails = function (contractid, callback) {
            // url needs to be changed
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/app/' + $rootScope.appId + '/contract/' + contractid;
            var res = $http.get(url);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.updateContract = function (jsonObj, callback) {
            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            };
            if(jsonObj.count){
                jsonObj.noOfFilters = jsonObj.count;
            }
            if (typeof jsonObj.providerId == "object") {
                jsonObj.providerType = jsonObj.providerId.type,
                jsonObj.providerId = jsonObj.providerId.value     
            }
            if (typeof jsonObj.consumerId == "object") {
               jsonObj.consumerType = jsonObj.consumerId.type,
                jsonObj.consumerId = jsonObj.consumerId.value  
            }
            // url needs to be changed
            //PUT /acisizer/v1/project/{projectId}/tenant/{tenantId}/contract/{contractId}
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/app/' + $rootScope.appId + '/contract/' + jsonObj.pKey;
            var res = $http.put(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }


        service.deleteContract = function (jsonObj, callback) {

            // url needs to be changed
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/app/' + $rootScope.appId + '/contract/' + jsonObj.pKey;
            var res = $http.delete(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }


        service.addEpg = function (jsonObj, callback) {
            //http://localhost:8080/acisizer/v1/project/1/tenant/1/app/1/epg

            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            };
            jsonObj.bdId=jsonObj.bdName;
            $http({
                 url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/app/' + $rootScope.appId + '/epg',
               // url: 'scripts/mock/addEpg.json',
                 method: "POST",
               // method: "GET",
                data: jsonObj,
                dataType: "json",
                headers:{
                    'Accept':'application/json, text/plain, */*'
                }
            }).success(function (response,status) {
                //response.eps = response.eps.mac;
                callback(response);
            }).error(function (data,response,status) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
                callback(response);
            });
        }

        service.getUuid = function (jsonObj, callback) {
            console.log(jsonObj)
            // url needs to be changed
             //var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/app/' + $rootScope.appId + '/uuid';
            var url = 'scripts/mock/uuid.json';
           // var res = $http.post(url, jsonObj);
             var res = $http.get(url, jsonObj);

            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }


        service.updateEpg = function (jsonObj, callback) {
            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            }
            jsonObj.Id = jsonObj.pKey;
            // url needs to be changed
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/app/' + $rootScope.appId + '/epg/' + jsonObj.pKey;
            var res = $http.put(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.getEpgDetails = function (epgid, callback) {
            // url needs to be changed
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/app/' + $rootScope.appId + '/epg/' + epgid;
            var res = $http.get(url);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }


        service.deleteEpg = function (jsonObj, callback) {

            jsonObj.eps = {
                "mac": jsonObj.eps
            }

            // url needs to be changed
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/app/' + $rootScope.appId + '/epg/' + jsonObj.pKey;
            var res = $http.delete(url, jsonObj);
            res.success(function (data, status, headers, config) {
                //  data.eps = data.eps.mac;
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }

        service.addTenant = function (jsonObj, callback) {
            var url=null;           
            // url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenantTemplate"; // Keep this remove the next call
            url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant"; // Keep this remove the next call
            var res = $http.post(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });   
        }
        
         service.updateTenant = function (jsonObj, callback) {

                var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId;
                 var res = $http.put(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        service.addApplication = function (jsonObj, callback) {

            $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/appTemplate",
                method: "POST",
                data: jsonObj,
                dataType: "json",
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call"); 
                $rootScope.fadeOutBackground();                          
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.updateApplication = function (jsonObj, callback) {
            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            }
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/app/" + $rootScope.appId;
            var res = $http.put(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });   
        }
        
        service.updateNodePositions = function (jsonObj, callback) {
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/view";
            jsonObj.forEach(function(obj){
                obj.dataItem.uiData={};
                obj.dataItem.uiData.x=obj.dataItem.x;
                obj.dataItem.uiData.y=obj.dataItem.y;
            })
            var res = $http.post(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });   
        }

        service.getTenants = function (callback) {

            $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenants",
                method: "GET"
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }

        service.getApplications = function (callback) {

            $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/apps",
                method: "GET"
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }

        service.deleteApplication = function (callback) {

            $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/app/" + $rootScope.appId,
                method: "DELETE"
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }

        service.deleteTenant = function (callback) {

            $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId,
                method: "DELETE"
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }

        service.getNodeConnection = function (callback) {
            console.log(REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/app/" + $rootScope.appId + "/nodeCollection");
            $http({
                // url: "app/views/home/projects/details/mock/app1.json",
                // method: "GET"
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/app/" + $rootScope.appId + "/nodeCollection",
                method: "GET"
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.getFabricNodeConnection = function (callback) {
            $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/nodeCollection",
                //url: 'scripts/mock/fabricCollection.json',
                method: "GET"
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }


        service.addVrf = function (jsonObj, callback) {
            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            };
            $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/vrf",
                method: "POST",
                data: jsonObj,
                dataType: "json",
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        service.updateVrf = function (jsonObj, callback) {
            // url needs to be changed
            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            };
            var tenantId = 0;
            if (jsonObj.fullName && jsonObj.fullName.startsWith("Common")) {
                tenantId = 1;
            }
            else {
                tenantId = $rootScope.tenantId;
            }
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + tenantId + '/vrf/' + jsonObj.pKey;
            var res = $http.put(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        service.deleteVrf = function (jsonObj, callback) {
            // url needs to be changed
            var tenantId = 0;
            if (jsonObj.fullyQualifiedName && jsonObj.fullyQualifiedName.startsWith("Common")) {
                tenantId = 1;
            }
            else {
                tenantId = $rootScope.tenantId;
            }
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + tenantId + '/vrf/' + jsonObj.pKey;
            var res = $http.delete(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
               console.log("error in api call");               
               $rootScope.fadeOutBackground();                                                      
               $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        var setAndGetVrfId=function(jsonObj){
            if (jsonObj.vrf && jsonObj.vrf.value) {
               vrfId = jsonObj.vrf.value;
               jsonObj.vrf = jsonObj.vrf.value;
           } else {
               vrfId = jsonObj.vrfId;
            }
            return vrfId;
       }

       var isTenantCommon=function(jsonObj){
           return jsonObj.vrf && jsonObj.vrf.text && jsonObj.vrf.text.startsWith("Common");
       }

        service.addBd = function (jsonObj, callback) {
            var tenantId = $rootScope.tenantId;
            if(isTenantCommon(jsonObj)){
                tenantId=1;
            }
            var vrfId = setAndGetVrfId(jsonObj);;
            
            /*if (typeof jsonObj.vrf == "object") {
                vrfId = jsonObj.vrf.value;
                if (jsonObj.vrf.text.startsWith("Common")) {
                    tenantId = 1;
                }
                jsonObj.vrf = jsonObj.vrf.value;
            } else {
                vrfId = jsonObj.vrfId;
            }*/
            jsonObj.ownershipId=$rootScope.tenantId;
            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            };
            //POST /acisizer/v1/project/{projectId}/tenant/{tenantId}/vrf/{vrfId}/bd
            $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + tenantId + "/vrf/" + vrfId + "/bd",
                method: "POST",
                data: jsonObj,
                dataType: "json",
            }).success(function (response) {
                callback(response);
            }).error(function (data,response) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
                callback(response);
            });
        }
        service.updateBd = function (jsonObj, callback) {
            // url needs to be changed
            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            };
            var vrfId = null;
            if (typeof jsonObj.vrf == "object") {
                vrfId = jsonObj.vrf.value;
                jsonObj.vrf = jsonObj.vrf.value;
            } else {
                vrfId = jsonObj.vrfId;
            }
            var tenantId = 0;
            if (jsonObj.fullName && jsonObj.fullName.startsWith("Common")) {
                tenantId = 1;
            }
            else {
                tenantId = $rootScope.tenantId;
            }
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + tenantId + "/vrf/" + vrfId + "/bd/" + jsonObj.pKey;
            var res = $http.put(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                callback(status);
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        service.deleteBd = function (jsonObj, callback) {
            var tenantId = 0;
            if (jsonObj.fullName && jsonObj.fullName.startsWith("Common")) {
                tenantId = 1;
            }
            else {
                tenantId = $rootScope.tenantId;
            }
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + tenantId + "/vrf/" + jsonObj.vrfId + "/bd/" + jsonObj.pKey;
            var res = $http.delete(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }


        service.addL3out = function (jsonObj, callback) {
            var vrfId = null;
            var tenantId = $rootScope.tenantId;
         //  if (typeof jsonObj.vrf == "object") {
                vrfId = jsonObj.vrfId;
                if (jsonObj.vrf && jsonObj.vrf.startsWith("Common")) {
                    tenantId = 1;
                    jsonObj.ownershipId=$rootScope.tenantId;
                }
                //jsonObj.vrf = jsonObj.vrf.value;
                
          /*  } else {
                vrfId = jsonObj.vrfId;
            } */
            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            };
            //jsonObj.epg_prefixes={"ipv4":jsonObj.epg_prefixes};
            $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + tenantId + "/vrf/" + vrfId + "/l3Out",
                method: "POST",
                data: jsonObj,
                dataType: "json",
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }

        service.getbdDetails = function (bdid, vrfid, vrfName, callback) {
            var tenantId = $rootScope.tenantId;
            if (vrfName.startsWith("Common")) {
                    tenantId = 1;
                }
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + tenantId + '/vrf/' + vrfid + '/bd/' + bdid;
            var res = $http.get(url);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.getl3Details = function (l3outid, vrfid, vrfName, callback) {
            var tenantId = $rootScope.tenantId;
            if (vrfName.startsWith("Common")) {
                    tenantId = 1;
                }
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + tenantId + '/vrf/' + vrfid + '/l3Out/' + l3outid;
            var res = $http.get(url);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.updateL3out = function (jsonObj, callback) {
            // url needs to be changed
            jsonObj.Id = jsonObj.pKey;
            jsonObj.uiData = {
                "x": jsonObj.x,
                "y": jsonObj.y
            };
            var tenantId = 0;
            if (jsonObj.fullName && jsonObj.fullName.startsWith("Common")) {
                tenantId = 1;
            }
            else {
                tenantId = $rootScope.tenantId;
            }
            jsonObj.epg_prefixes={"ipv4":jsonObj.epg_prefixes}
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + tenantId + "/vrf/" + jsonObj.vrfId + "/l3Out/" + jsonObj.pKey;
            var res = $http.put(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        service.deleteL3out = function (jsonObj, callback) {
            var tenantId = 0;
            if (jsonObj.fullName && jsonObj.fullName.startsWith("Common")) {
                tenantId = 1;
            }
            else {
                tenantId = $rootScope.tenantId;
            }
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + tenantId + "/vrf/" + jsonObj.vrfId + "/l3Out/" + jsonObj.pKey;
            var res = $http.delete(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.getVrfDetails = function (vrfid, fullname, callback) {
            // url needs to be changed
            var tenantId = 0;
            if (fullname.startsWith("Common")) {
                tenantId = 1;
            }
            else {
                tenantId = $rootScope.tenantId;
            }
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + tenantId + '/vrf/' + vrfid;
            var res = $http.get(url);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }

        service.updateSharedResource = function (jsonObj, callback) {
            // url needs to be changed
            jsonObj.Id = jsonObj.pKey;
            var tenantId = 0;
            if (jsonObj.fullName && jsonObj.fullName.startsWith("Common")) {
                tenantId = 1;
            }
            else {
                tenantId = $rootScope.tenantId;
            }
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + tenantId + "/vrf/" + jsonObj.vrfId + "/sharedResource/" + jsonObj.pKey;
            var res = $http.put(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.deleteSharedResource = function (jsonObj, callback) {
            // url needs to be changed
            jsonObj.Id = jsonObj.pKey;
            var tenantId = 0;
            if (jsonObj.fullName && jsonObj.fullName.startsWith("Common")) {
                tenantId = 1;
            }
            else {
                tenantId = $rootScope.tenantId;
            }
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + tenantId + "/vrf/" + jsonObj.vrf + "/sharedResource/" + jsonObj.pKey;
            var res = $http.delete(url, jsonObj);
            res.success(function (data, status, headers, config) {
                callback(data);
            });
            res.error(function (data, status, headers, config) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.getFabric = function (jsonObj, callback) {
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/tenant/' + $rootScope.tenantId + '/vrfs';
            $http({
                url: url,
                method: "GET"
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }

        service.getSizingSummary = function (callback) {
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/sizingsummary/';
            $http({
                url: url,
                method: "GET"
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.getSizingResults = function (callback) {
            $rootScope.canceler = $q.defer();
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/sizingresults/';
            //canceler.resolve();
            var res = $http.get(url, {timeout: $rootScope.canceler.promise});
    
            res.success(function (data, status, headers, config) {
                //  canceler.resolve();
                callback(data);
            
            });
            res.error(function (data, status, headers, config) {
                if (status != 0 && data && data.error) {
                    console.log("error in api call");
                    $rootScope.fadeOutBackground();
                    $rootScope.notification.show({
                        title: data.error,
                        message: data.message
                    }, "error");
                }
            });
            
            
        }
        
        service.getInputJSON = function (callback) {
            var url = REST_URI.baseUri + '/project/' + $rootScope.projectId + '/sizingsummaryinput/';
            $http({
                url: url,
                method: "GET",
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.fadeOutBackground();
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.getEpgContracts = function (epgId, callback) {
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/app/" + $rootScope.appId + "/epg/" + epgId + "/contracts";
            $http({
                url: url,
                method: "GET",
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.getTenantDefaultValues = function (callback) {
            var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/nodeCollection/defaultValues"
            $http({
                url: url,
                method: "GET",
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }
        
        service.getNodeDefaultValues = function (type, callback) {
             var url = REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/tenant/" + $rootScope.tenantId + "/nodeCollection/defaultValues?type=" + type;
            //var url = 'scripts/mock/epgdefaultValues.json';
            $http({
                url: url,
                method: "GET",
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call");
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
        }

        service.getRooms= function(callback){
                 $http({
                    url: REST_URI.baseUri + '/project/'  + $rootScope.projectId +"/physical/rooms",
                    method: "GET"
                }).success(function (response) {
                    callback(response);
                }).error(function (data) {
                    console.log("error in api call");
                    // $rootScope.fadeOutBackground();
                    $rootScope.notification.show({
                        title: data.error,
                        message: data.message
                    }, "error");
                });
            }

            service.preferredSwitch = function(jsonObj, callback){
                $http({
                url: REST_URI.baseUri + '/project/'  + $rootScope.projectId + "/preferredswitch",
                method: "POST",
                data: jsonObj,
                dataType: "json",
            }).success(function (response) {
                callback(response);
            }).error(function (data) {
                console.log("error in api call"); 
                $rootScope.fadeOutBackground();                          
                $rootScope.notification.show({
                    title: data.error,
                    message: data.message
                }, "error");
            });
            }

        return service;

    }]);
String.prototype.visualLength = function()
{
    var ruler = document.getElementById("ruler");
   //ruler.innerHTML = this;
    return 200;
}