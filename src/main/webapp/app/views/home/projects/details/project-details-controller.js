(function() {
  'use strict';

  angular
    .module('profiler')
    .controller('projectDetailsController', function($compile, $scope, $timeout, $rootScope,$uibModal, $location, deviceService, repoTestService, popupService, logicService, DIAGRAM_CONSTANTS, DIALOG_CONSTANTS, ERROR_STRING_CONSTANTS, CONSTANT_FACTORY, PHY_SIZER_CONSTANTS, OBJECT_TYPES ) {

      //Manohar ::
      var _selectedContractConfigDetails = null;

      // $rootScope.currProject = {"id":2,"name":"demo","description":"demo","user":{"id":1,"username":"admin","role":"ROLE_ADMIN","email":"admin@maplelabs.com","authentication":"Local Authentication"},"createdTime":1467094416000,"lastUpdatedTime":1467094416000,"logicalSummary":{"tenantCount":0,"vrfCount":0,"bdCount":0,"l3OutCount":0,"appCount":0,"epgCount":0,"endPoints":0,"contractCount":0,"eppCount":0,"serviceChainCount":0,"l2OutCount":0,"connectionDefCount":0,"uniqueId":0,"sharedResourceCount":0,"leafCountBorder":0,"leafCountRegular":0,"filterCount":0}};
      $rootScope.authenticated = true;
      $scope.menuOrientation = "horizontal";
      $scope.onSelect = function(ev) {
        alert($(ev.item.firstChild).text());
      };
      $scope.isDisplayed = false;
      $scope.currentEditorType = "epg1";

      $scope.fabricShapes = ["vrf", "bd", "l3out"];
      $scope.appShapes = ["app", "epg", "contract", "contractl3", "contractshared"];
      $scope.vrfsLength = 1;
      $rootScope.project = {};

      $scope.sumToggle = function() {
        console.log("in sumToggle");
        $('.slideTogglebox').slideToggle();
        $rootScope.hideProjectSummaryPane = false;
      }

      //MANOHAR 
      // reloading / reinitializing the drigram
      function reloadAppWithDelay(dealy){
        $timeout(function(){
          $scope.loadApplication();
        }, dealy || 100);
      }


      $rootScope.showTabs = function() {

        /*$('.ct').codetabs({
            preload: 4,
            speed: 800,
            fxOne: 'glueHor',
            pag: {align: 'begin',width: 80}
        });*/
        /*var code = $('.ct').data('codetabs');
        if (code != null){
            code.ev.on('start', function () {

        });
        code.ev.on('loadEnd', function (e, $slide, ID) {
        });
        }*/
      }

      $scope.detailedResults = function() {
        console.log("Detailed Results called");
        $location.path("/results");
        $("#sizer-result-tab").addClass("active");
        $("#sizer-logical-tab").removeClass("active");
      }

      $rootScope.projectName = "";
      $rootScope.tenantList = {};

      // Load the Tabs if the user is already authenticated
      $scope.$on('$viewContentLoaded', function() {

        // Manohar   
        // To clear/remove the existing ACI windows, otherwise duplicate aci windows will exist on every load
        // and ACI windows will not be closed as expected
        $('.a-widget.a-window').remove();

        setTimeout(function() {



          // GET the devices list & filter to APIC devices with key name as 'aci'
          deviceService.query().$promise.then(function(devicesList) {
            $rootScope.apicDevices = [];
            for(var i=0; i<devicesList.length; i++){
              if(devicesList[i].type === 'ACI' ){
                $rootScope.apicDevices.push( devicesList[i] );                
              }              
            }
          });

          // deviceService.get({'id':$rootScope.currProject.deviceId}).$promise.then(function(deviceObject) {
          //   $rootScope.currentApicDevice = deviceObject;
          // });

          $rootScope.currentApicDevice = $rootScope.currProject.device;

          $rootScope.showTabs();
          // $scope.loadApplication();
          // $scope.loadFabric();

          $("#projectstab").addClass("active");
          $("#physicaltab").removeClass("active");
          if ($scope.isDisplayed == false) {
            $scope.isDisplayed = true;
            $rootScope.showLogicalView();
            if ($rootScope.currProject) {
              $rootScope.projectName = ($rootScope.currProject.name.length < 32) ? $rootScope.currProject.name : $rootScope.currProject.name.slice(0, 29) + "...";
              $scope.getTenants();
            } else {
              logicService.getProjectById(function(resp) {
                $rootScope.currProject = resp;
                $rootScope.projectName = ($rootScope.currProject.name.length < 32) ? $rootScope.currProject.name : $rootScope.currProject.name.slice(0, 29) + "...";
                $scope.getTenants();
              });
            }
            $rootScope.projectDropDown = true;
            $rootScope.project['selectedProject'] = "Logical";
            $rootScope.isProjectVisible = true;
          }

        }, 500);

         
      });
      $scope.closeProjectSummary = function() {
        $rootScope.hideProjectSummaryPane = true;
      }

      $scope.findShapeByUid = function(item) {
        var shapes = $scope.appDiagram.shapes;
        for (var idx = 0; idx < shapes.length; idx++) {
          if (shapes[idx].dataItem.uid == item.uid) {
            return shapes[idx];
          }
        }
      }

      $scope.fabricX = -1;
      $scope.fabricY = -1;

      $scope.getFabricPosition = function(element) {
        var xPosition = 0;
        var yPosition = 0;

        while (element) {
          xPosition += (element.offsetLeft - element.scrollLeft + element.clientLeft);
          yPosition += (element.offsetTop - element.scrollTop + element.clientTop);
          element = element.offsetParent;
        }
        return { x: xPosition, y: yPosition };
      }


      $scope.loadFabric = function(resp) {
        if ($scope.fabricDiagram) {
          $scope.fabricDiagram.dataSource.data([]);
          $scope.fabricDiagram.clear();
        }
        // find absolute position of fabric pane
        if ($scope.fabricX == -1) {
          var myElement = document.getElementById("fabricDiagram");
          var position = $scope.getFabricPosition(myElement);
          $scope.fabricX = position.x;
          $scope.fabricY = position.y;
        }
        console.log($rootScope.currProject + 'test')
        logicService.getFabricNodeConnection(function(resp) {
          var log = [];
          var data = resp;
          console.log(data)
            //  var data = fabricData.fabric;
          angular.forEach(data.nodes, function(value, key) {
            //value.id = value.id;
            value.pKey = value.id;
            $scope.fabricDiagram.dataSource.pushCreate(value);
            $scope.fabricDiagram.dataSource.sync();
          }, log);

          angular.forEach(data.connections, function(value, key) {
            $scope.fabricDiagram.connectionsDataSource.add({ from: Number(value.dataItem.from), to: Number(value.dataItem.to) });
            $scope.fabricDiagram.connectionsDataSource.sync();
          }, log);

          /***Adding the rootScope.notification variable in loadFabric instead of load application**/
          $rootScope.notification = $("#notification").aciNotification({
            position: {
              pinned: true,
              top: "35%",
              /* right: "40%",*/
              left: "35%",
              bottom: "40%"
            },
            autoHideAfter: 0,
            stacking: "down",
            hideOnClick: 0,
            templates: [{
              type: "error",
              template: $("#errorTemplate").html()
            }]

          }).data("aciNotification");
          $scope.doFabircLayout();
          //  $scope.fabricDiagram.bringIntoView($scope.fabricDiagram.shapes, {align: "center"});
        });

      }

      $rootScope.fadeOutBackground = function() {
        $("body").prepend("<div id='PopupMask' style='position:fixed;width:100%;height:100%;z-index:10;background-color:gray;'></div>");
        $("#PopupMask").css('opacity', 0.2);
      }

      $scope.applicationAdded = false;
      $scope.reAlignTree = false;

      $scope.appX = -1;
      $scope.loadApplication = function() {
        debugger;
        if ($scope.appDiagram) {
          $scope.appDiagram.dataSource.data([]);
          $scope.appDiagram.clear();
        }

        if ($scope.appX == -1) {
          var myElement = document.getElementById("appDiagram");
          var position = $scope.getFabricPosition(myElement);
          $scope.appX = position.x;
          $scope.appY = position.y;
        }

        logicService.getNodeConnection(function(resp) {
          var log = [];
          var data = resp.app;
          angular.forEach(data.nodes, function(value, key) {
            value.x = value.uiData.x;
            value.y = value.uiData.y;
            if (value.type == "app") {
              if ($scope.applicationAdded == false) {
                $scope.treeMidPoint = value.uiData.x;
              } else {
                $scope.treeMidPoint = 310;
              }
            }

            $scope.appDiagram.dataSource.pushCreate(value);
            $scope.appDiagram.dataSource.sync();
          }, log);
          angular.forEach(data.connections, function(value, key) {
            $scope.appDiagram.connectionsDataSource.add({ from: value.dataItem.from, to: value.dataItem.to });
            $scope.appDiagram.connectionsDataSource.sync();
          }, log);
          // Automatic rendering only if app is added using No Template model
          if ($scope.applicationAdded == true) {
            if ($scope.noTemplateApp == true) {
              $scope.updateNodePositions();
            }
            $scope.noTemplateApp = false;
            $scope.applicationAdded = false;
          }
          if ($scope.reAlignTree == true) {
            var noOfEpgNodes = $scope.getNumberOfNodes("epg");
            $scope.realignNodes("epg", noOfEpgNodes);
            var noOfContractNodes = $scope.getNumberOfNodes("contract");
            $scope.realignNodes("contract", noOfContractNodes);
            $scope.updateNodePositions();
            $scope.reAlignTree = false;
          }

        });

        $scope.updateNodePositions = function() {
          var shapes = $scope.appDiagram.shapes;
          var data = [];
          var dataItem = [];
          for (var idx = 0; idx < shapes.length; idx++) {
            dataItem = { "type": shapes[idx].options.type, "x": shapes[idx].options.x, "y": shapes[idx].options.y };
            data.push({ "id": shapes[idx].dataItem.pKey, "dataItem": dataItem });
          }
          logicService.updateNodePositions(data, function(resp) {});
        }

        $scope.doLayout();
      }

      $scope.autoRendering = false;

      $scope.doLayout = function() {
        $scope.appDiagram.bringIntoView($scope.appDiagram.shapes, { align: "top" });
        $scope.appDiagram.zoom(0.7);
        // $scope.appDiagram.layout({ horizontalSeparation: 50,verticalSeparation: 90});
      }

      $scope.treeMidPoint = 310;
      $scope.verticalSeperation = 190;
      $scope.horizontalSeperation = 120;
      $scope.minLeftOffset = 50;

      $scope.getNumberOfNodes = function(type) {
        var count = 0;
        var shapes = $scope.appDiagram.shapes;
        shapes.forEach(function(shape) {

          if (((type == "contract") && ((shape.type == "contract") || (shape.type == "contractl3"))) || ((type == "contractl3") && ((shape.type == "contract") || (shape.type == "contractl3"))) || ((type == "epg") && (shape.type == "epg"))) {
            count++;
          }
        });
        return count;
      }

      $scope.getNewItemPosition = function(item) {
        var shapes = $scope.appDiagram.shapes;
        var position;
        if ((item.type == "epg") || (item.type == "contract") || (item.type == "contractl3")) {

          var noOfnodes = $scope.getNumberOfNodes(item.type) + 1;
          var initialPoint = 0;
          if (noOfnodes == 1) {
            position = $scope.treeMidPoint;
          } else {
            initialPoint = $scope.realignNodes(item.type, noOfnodes);
            if (item.type == "epg") {
              var noOfContractnodes = $scope.getNumberOfNodes("contract");
              $scope.realignNodes("contract", noOfContractnodes);
            }
            if ((noOfnodes == 2) && (item.type == "epg")) {
              position = initialPoint + (noOfnodes) * $scope.horizontalSeperation;
            } else {
              position = initialPoint + (noOfnodes - 1) * $scope.horizontalSeperation;
            }
          }
        }
        var offset = 0;
        if ((initialPoint < $scope.minLeftOffset) && (noOfnodes > 1)) {
          offset = Math.abs(initialPoint);
          $scope.adjustOffsets(offset);
          $scope.treeMidPoint += offset;
        }
        return position + offset;
      }


      $scope.realignNodes = function(type, Num) {
        var shapes = $scope.appDiagram.shapes;
        var count = 0;
        var initialPoint = 0;
        if ((type == "contract") || type == ("contractl3")) {
          if (Num % 2 == 0) {
            initialPoint = $scope.treeMidPoint - (Math.floor(Num / 2)) * $scope.horizontalSeperation + $scope.horizontalSeperation / 2;
          } else {
            initialPoint = $scope.treeMidPoint - (Math.floor(Num / 2)) * $scope.horizontalSeperation;
          }

        } else {
          //epg
          if ((Num % 2 == 0) && (Num != 2)) {
            initialPoint = $scope.treeMidPoint - (Math.floor(Num / 2)) * $scope.horizontalSeperation + $scope.horizontalSeperation / 2;
          } else {
            initialPoint = $scope.treeMidPoint - (Math.floor(Num / 2)) * $scope.horizontalSeperation;
          }
        }

        var lastPos = 0;
        var offset = 0;

        shapes.forEach(function(shape) {
          if (shape.type == "app") {
            if (shape.options.x != $scope.treeMidPoint) {
              shape.options.x = $scope.treeMidPoint;
              shape.redrawVisual();
            }
          } else
          if (((type == "contract") && ((shape.type == "contract") || (shape.type == "contractl3"))) || ((type == "contractl3") && ((shape.type == "contract") || (shape.type == "contractl3"))) || ((type == "epg") && (shape.type == "epg"))) {
            if (shape.type == "epg") {
              shape.options.y = $scope.verticalSeperation;
            } else if ((shape.type == "contract") || (shape.type == "contractl3")) {
              // Spacing between epg and contract node
              shape.options.y = 330;
            }
            shape.options.x = initialPoint + (count * $scope.horizontalSeperation) + offset;
            shape.redrawVisual();
            count++;
            lastPos = shape.options.x;
            if (type == "epg") {
              if ((Num == 2) && (count == Num / 2)) {
                offset = $scope.horizontalSeperation;
              }
            }
          }

        });
        return initialPoint;
      }

      $scope.adjustOffsets = function(delta) {
        var shapes = $scope.appDiagram.shapes;
        shapes.forEach(function(shape) {
          shape.options.x += delta;
          shape.redrawVisual();
        });
        $scope.updateNodePositions();
      }


      $scope.doFabircLayout = function(paneSize) {
        if ($scope.fabricDiagram) {
          if (paneSize) {
            $scope.fabricDiagram.options.layout.grid.width = paneSize - 10;
          }
          $scope.fabricDiagram.zoom(0.7);
          $scope.fabricDiagram.layout({});
          $scope.fabricDiagram.bringIntoView($scope.fabricDiagram.shapes, { align: "top", animate: true });
          $scope.fabricDiagram.resize();
        }

      }

      $scope.loadMask = function(target) {
        var element = $(target);
        aci.ui.progress(element, true);
        setTimeout(function() {
          aci.ui.progress(element, false);
        }, 1000);
      }


      // START : Logical Diagram here
      $rootScope.showLogicalView = function() {
        var Shape = aci.dataviz.diagram.Shape,
          Connection = aci.dataviz.diagram.Connection,
          Rect = aci.dataviz.diagram.Rect,
          Point = aci.dataviz.diagram.Point,
          selected;

        $("#canvasProperties").on("change", canvasPropertiesChange);

        var layoutMapping = {
          "TreeDown": {
            type: "tree",
            subtype: "down"
          },
          "TreeUp": {
            type: "tree",
            subtype: "up"
          },
          "TreeLeft": {
            type: "tree",
            subtype: "left"
          },
          "TreeRight": {
            type: "tree",
            subtype: "right"
          },
          "RadialTree": {
            type: "tree",
            subtype: "radial"
          },
          "TipOverTree": {
            type: "tree",
            subtype: "typeover"
          },
          "LayeredHorizontal": {
            type: "layered",
            subtype: "horizontal"
          },
          "LayeredVertical": {
            type: "layered",
            subtype: "vertial"
          },
          "ForceDirected": {
            type: "force",
            subtype: "directed"
          },
          "MindmapVertical": {
            type: "tree",
            subtype: "mindmapvertical"
          },
          "MindmapHorizontal": {
            type: "tree",
            subtype: "mindmaphorizontal"
          }

        };

        function canvasPropertiesChange() {
          appDiagram.element.css(
            "background-color",
            $("#canvasBackgroundColorPicker").getAciColorPicker().value());

          var layout = layoutMapping[$("#canvasLayout").getAciDropDownList().value()];

          appDiagram.layout({
            type: layout.type,
            subtype: layout.subtype,
            animation: true
          });
        }

        $("#shapeProperties").on("change", shapePropertiesChange);

        function shapePropertiesChange() {
          var elements = selected || [],
            options = {
              fill: $("#shapeBackgroundColorPicker").getAciColorPicker().value(),
              stroke: {
                color: $("#shapeStrokeColorPicker").getAciColorPicker().value(),
                width: $("#shapeStrokeWidth").getAciNumericTextBox().value()
              }
            },
            bounds = new Rect(
              $("#shapePositionX").getAciNumericTextBox().value(),
              $("#shapePositionY").getAciNumericTextBox().value(),
              $("#shapeWidth").getAciNumericTextBox().value(),
              $("#shapeHeight").getAciNumericTextBox().value()
            ),
            element, i;

          for (i = 0; i < elements.length; i++) {
            element = elements[i];
            if (element instanceof Shape) {
              element.redraw(options);

              element.bounds(bounds);
            }
          }
        }

        function connectionPropertiesChange() {
          var elements = selected || [],
            options = {
              startCap: $("#connectionStartCap").getAciDropDownList().value(),
              endCap: $("#connectionEndCap").getAciDropDownList().value()
            },
            element;

          for (i = 0; i < elements.length; i++) {
            element = elements[i];
            if (element instanceof Connection) {
              element.redraw(options);
            }
          }
        }

        $("#connectionProperties").on("change", connectionPropertiesChange);

        $("#alignConfiguration .configurationButtons").aciButton({
          click: function(e) {
            var value = this.element.data("position");
            appDiagram.alignShapes(value);
          }
        });

        $("#arrangeConfiguration .configurationButtons").aciButton({
          click: function(e) {
            var methodName = this.element.find("span").attr("class");
            diagram[methodName]();
          }
        });

        $("#diagramZoomIndicator").change(function() {
          var value = $(this).val();
          $("#diagramZoom").getAciSlider().value(value);
          appDiagram.zoom(value);
        });

        function reset() {

          appDiagram.clear();
        }

        function undo() {
          appDiagram.dataSource.fetch();
          //appDiagram.undo();
        }

        function redo() {
          appDiagram.redo();
        }

        function copyItem() {
          appDiagram.copy();
        }

        function pasteItem() {
          appDiagram.paste();
        }

        var actions = {
          blank: reset,
          undo: undo,
          redo: redo,
          copy: copyItem,
          paste: pasteItem
        };

        $("#export").on("click", function() {
          var json = JSON.stringify(appDiagram.save()),
            blob = new Blob([json], { type: "application\/json" });;

          if (navigator.msSaveBlob) {
            navigator.msSaveBlob(blob, this.getAttribute("download"));
          } else {
            this.href = window.URL.createObjectURL(blob);
          }
        });

        $("#upload").aciUpload({
          async: {
            saveUrl: "save",
            removeUrl: "remove",
            autoUpload: true
          },
          showFileList: false,
          localization: {
            select: ""
          },
          select: function(e) {
            if (typeof(FileReader) !== "undefined") {
              var f = e.files[0].rawFile,
                reader = new FileReader;

              reader.onload = (function(file) {
                return function(e) {
                  appDiagram.load(JSON.parse(e.target.result));
                };
              })(f);

              reader.readAsBinaryString(f);
            }
          }
        });

        $("#splitter").aciSplitter({
          panes: [
            { collapsible: true, size: "110px" },
            { collapsible: false, scrollable: false },
          ]
        });

        $("#center-splitter").aciSplitter({
          resize: function(e) {
            if (e.sender.options.panes[1].size.indexOf("%") > -1) {
              $scope.doFabircLayout(e.width / 2);
            } else if (e.sender.options.panes[1].size.indexOf("px") > -1) {
              $scope.doFabircLayout(e.sender.options.panes[1].size.slice(0, -2));
            }

          },
          panes: [
            { collapsible: true, size: "50%", scrollable: false },
            { collapsible: true, size: "50%", scrollable: false }
          ]
        });

        $("#tenant-splitter").aciSplitter({
          orientation: "vertical",
          panes: [
            { collapsible: true, size: "135px", scrollable: false },
            { collapsible: false, size: "80%", scrollable: false }
          ]
        });

        var serviceRoot = "http://localhost/aci-ui/service";
        var appDataSource = new aci.data.DataSource({
          batch: false,
          transport: {
            read: function(options) {
              options.success([]);
            },
            update: function(options) {
              $scope.loadMask("#appDiagram");
              if (options.data.isSaved == false) {
                if (options.data.type == "epg") {

                  logicService.addEpg(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    // if there is an error, response will be a single number
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });
                    if (isNaN(resp)) {
                      options.data.isSaved = true;
                      // Store the primary Key
                      options.data.pKey = resp.id;
                      options.data.id = resp.id;
                      options.data.appId = resp.appId;
                      options.data.fullName = resp.fullName;
                      options.success(options.data);
                      //$scope.doLayout();
                      $scope.updateTenantList();
                      $scope.updateNodePositions();
                      //appDiagram.dataSource.sync();
                      //MANOHAR (to make next add item to be as add item:: coming as edit last item)
                      reloadAppWithDelay(100);
                    } else {
                      $scope.loadApplication();
                    }

                  });
                } else if (options.data.type == "contract") {
                  if (options.data.providerId.value == options.data.consumerId.value) {
                    $rootScope.notification.show({
                      title: "Invalid input",
                      message: "Please provide different values for provider and consumer"
                    }, "error");

                    appDiagram.dataSource.pushDestroy(appDiagram.dataSource.get(options.data.Id));
                    appDiagram.dataSource.sync();
                  } else {
                    options.data.providerId = {
                      value: $scope.nearestEpg.dataItem.pKey,
                      type: $scope.nearestEpg.dataItem.type
                    };

                    //Manoahr :: injecting subject property to contract configuration options
                    if(_selectedContractConfigDetails){
                      options.data.subjects = _selectedContractConfigDetails.subjects;
                      options.data.configName = _selectedContractConfigDetails.name;
                      _selectedContractConfigDetails = null;
                    }

                    logicService.addContract(options.data, function(resp) {
                      $rootScope.canceler.resolve();
                      $scope.showLoadingImage();
                      logicService.getSizingResults(function(resp) {
                        $rootScope.currProject = resp;
                        $scope.removeLoadingImage();
                      });

                      if (isNaN(resp)) {

                        options.data.isSaved = true;
                        // Store the primary Key
                        options.data.pKey = resp.id;
                        options.data.name = resp.name;

                        var contractConnection = findShapeById(appDiagram, options.data.Id);

                        var fConnection = findShapeById(appDiagram, options.data.providerId.value);
                        if (!fConnection) {
                          fConnection = findShapeByPKey(appDiagram, options.data.providerId.value);
                        }
                        var tConnection = findShapeById(appDiagram, options.data.consumerId.value);
                        if (!tConnection) {
                          tConnection = findShapeByPKey(appDiagram, options.data.consumerId.value);
                        }
                        appDiagram.connect(contractConnection, tConnection);
                        appDiagram.connect(contractConnection, fConnection);
                        options.success(options.data);
                        //$scope.doLayout();
                        $scope.updateTenantList();
                        $scope.updateNodePositions();
                        // MANOHAR ::(to make next add item to be as add item:: coming as edit last item)
                        reloadAppWithDelay(100);
                      } else {
                        $scope.loadApplication();
                        //  $scope.doLayout();
                      }
                      $scope.nearestEpgId = null;
                      $scope.nearestEpg.redrawVisual();
                    });

                  }
                } else if ((options.data.type == "contractl3") ||
                  (options.data.type == "contractshared")) {
                  options.data.providerId = {
                    value: $scope.nearestEpg.dataItem.pKey,
                    type: $scope.nearestEpg.dataItem.type
                  };
                  var checked = $("#provider-checkbox").is(":checked");
                  if (checked == true) {
                    options.data.providerEnforced = true;
                  } else {
                    options.data.providerEnforced = false;
                  }

                  var checked2 = $("#consumer-checkbox").is(":checked");
                  if (checked2 == true) {
                    options.data.consumerEnforced = true;
                  } else {
                    options.data.consumerEnforced = false;
                  }
                  // Bug Fix 253

                  if (options.data.type == "contractl3") {
                    var contractL3ConsumerId = document.getElementById("contractl3consumerId");
                    var editedConsumerId = contractL3ConsumerId.selectedIndex; // Actual Dropdown Index
                    var selectedL3OutConsumerId = contractL3ConsumerId[editedConsumerId].value; //Using dropdown index get the value of the option
                    var shape;
                    if (options.data.consumerId.value == 4) {
                      shape = findShapeById(fabricDiagram, options.data.consumerId.value);
                    } else {
                      shape = findShapeById(fabricDiagram, selectedL3OutConsumerId);
                    }

                    options.data.consumerId = {
                      value: shape.dataItem.pKey,
                      type: shape.dataItem.type
                    };
                  }
                  // Fix End

                  logicService.addContract(options.data, function(resp) {
                    options.data.isSaved = true;
                    // Store the primary Key
                    options.data.pKey = resp.id;
                    options.data.name = resp.name;

                    var contractConnection = findShapeById(appDiagram, options.data.Id);

                    var fConnection = findShapeById(appDiagram, options.data.providerId.value);
                    if (!fConnection) {
                      fConnection = findShapeByPKey(appDiagram, options.data.providerId.value);
                    }
                    var tConnection = findShapeById(appDiagram, options.data.consumerId.value);
                    if (!tConnection) {
                      tConnection = findShapeByPKey(appDiagram, options.data.consumerId.value);
                    }
                    //appDiagram.connect(contractConnection, tConnection);
                    appDiagram.connect(contractConnection, fConnection);
                    options.success(options.data);
                    // $scope.loadApplication();
                    contractConnection.redrawVisual();
                    $scope.updateTenantList();
                    $scope.updateNodePositions();
                    //MANOHAR (to make next add item to be as add item:: coming as edit last item)
                    reloadAppWithDelay(100);
                  });
                  $scope.nearestEpgId = null;
                  $scope.nearestEpg.redrawVisual();
                }

                //options.success();
              } else {
                if (options.data.type == "epg") {
                  logicService.updateEpg(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });
                    options.data.isSaved = true;
                    options.data.eps = resp.eps;
                    $scope.updateTenantList();
                    options.success(options.data);
                  });
                } else if (options.data.type == "contract") {

                  //Manohar :: injecting subject property to contract configuration options
                  if(_selectedContractConfigDetails){
                    options.data.subjects = _selectedContractConfigDetails.subjects;
                    options.data.configName = _selectedContractConfigDetails.name;
                    _selectedContractConfigDetails = null;
                  }

                  logicService.updateContract(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });
                    options.data.isSaved = true;
                    options.data.pKey = resp.id;
                    options.success(options.data);
                    // $scope.loadApplication();
                  });

                } else if ((options.data.type == "contractl3") ||
                  (options.data.type == "contractshared")) {
                  var checkedProvider = $("#provider-checkbox").is(":checked");
                  if (checkedProvider == true) {
                    options.data.providerEnforced = true;
                  } else {
                    options.data.providerEnforced = false;
                  }

                  var checkedConsumer = $("#consumer-checkbox").is(":checked");
                  if (checkedConsumer == true) {
                    options.data.consumerEnforced = true;
                  } else {
                    options.data.consumerEnforced = false;
                  }
                  
                  //Manohar :: injecting subject property to contract configuration options
                  if(_selectedContractConfigDetails){
                    options.data.subjects = _selectedContractConfigDetails.subjects;
                    options.data.configName = _selectedContractConfigDetails.name;
                    _selectedContractConfigDetails = null;
                  }

                  logicService.updateContract(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });
                    options.data.isSaved = true;
                    options.data.pKey = resp.id;
                    /*  var contractConnection = findShapeById(appDiagram, options.data.Id);
                      removeEdges(contractConnection.dataItem.id);
                      var fConnection = findShapeById(appDiagram, options.data.providerId);
                      if (!fConnection) {
                          fConnection = findShapeByPKey(appDiagram, options.data.providerId);
                      }
                      var tConnection = findShapeById(appDiagram, options.data.consumerId);
                      if (!tConnection) {
                          tConnection = findShapeByPKey(appDiagram, options.data.consumerId);
                      }
                     
                      appDiagram.connect(contractConnection, fConnection);*/
                    options.success();
                    //$scope.loadApplication();
                    // $scope.doLayout();

                    // Manohar :: updating the filter under tenant details after updating the contract
                    $scope.updateTenantList();                    
                  });

                } else if (options.data.type == "app") {
                  logicService.updateApplication(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });
                    options.data.isSaved = true;
                    options.success(options.data);
                    $scope.applicationList[$scope.selectedApplicationIndex].noOfInstances = resp.noOfInstances;
                    $scope.applicationList[$scope.selectedApplicationIndex].name = resp.name;
                    $scope.updateTenantList();
                    $scope.createApplicationListDropDown();
                  });
                }
              }
            },
            create: function(options) {

              logicService.getUuid(options.data, function(resp) {
                options.data.Id = resp.uuid;
                options.data.isSaved = false;
                options.success(options.data);
                if (appDiagram.dataSource.total() > 1 && options.data.type == "epg") {
                  appDiagram.connectionsDataSource.add({ from: $rootScope.appId, to: resp.uuid });
                }
                if ((options.data.type == "contract") || (options.data.type == "contractl3") || ((options.data.type == "contractshared"))) {
                  appDiagram.connectionsDataSource.add({ from: $scope.selectedEpg.dataItem.id, to: resp.uuid });
                  $scope.nearestEpg = $scope.selectedEpg;
                }
                var node = findShapeById(appDiagram, resp.uuid);
                if (node) {
                  onSelect(appDiagram, node);
                  appDiagram.edit(node);
                  //  $scope.doLayout();
                }
              });
            },
            destroy: function(options) {
              if (options.data.type == "app") {
                options.success();
                //  $scope.getApplicationsForTenant();
              } else if (options.data.type == "epg") {
                options.success();
                $scope.updateTenantList();
              } else if ((options.data.type == "contract") ||
                (options.data.type == "contractl3")) {
                options.success();
              }
            },
            parameterMap: function(options, operation) {
              if (operation !== "read") {
                return { models: aci.stringify(options.models || [options]) };
              }
            }
          },
          schema: {
            model: {
              id: "id",
              fields: {
                id: { from: 'Id', type: "string", editable: false }
              }
            }
          }
        });

        function removeShapeFromDiagram(shapeId, shape) {
          removeEdges(shapeId);
          appDiagram.dataSource.remove(shape);
        }

        function onRemove(e) {
          // prevent remove first
          e.preventDefault(true);
          var dItem = {};
          var src = appDiagram.dataSource.get(e.shape.dataItem.id);
          angular.copy(src, dItem);
          if (dItem.type == "app") {
            $scope.openDelAppWindow($rootScope.selectedApplicationIndex);
          } else {
            $scope.openDelNodeWindow(dItem);
          }
        }

        function onEdit(e) {
          _selectedContractConfigDetails = null;
          // e.shape.dirty=true;
          /* <!-- If EPG then render a different template --> */
          if (e.shape.type === "epg") {
            //$rootScope.tenantList
            var log = [];
            var bds = getAllBds();
            if (e.shape.pKey) {
              document.getElementById("epgBd").disabled = true;
            }
            $("#epgBd").aciComboBox({
              dataTextField: "text",
              dataValueField: "value",
              dataSource: bds,
              filter: "contains",
              suggest: true
            });

            $("#epgSubnets").aciNumericTextBox({ format: 'n0' });
            $("#epgSpan").aciNumericTextBox({ format: 'n0' });
            $("#epgEndPoints").aciNumericTextBox({ format: 'n0' });
            $("#sharedfilter").aciNumericTextBox({ format: 'n0' });

            if (!e.shape.pKey) {
              logicService.getNodeDefaultValues("epg", function(resp) {
                // @Manohar::start
                // this is commented to not increase the contracts and/or filters count on creating an EPG
                resp.sharedServicesEnabled = false;
                resp.noOfsharedServiceFilter = 0;
                // @Manohar::end
                
                $("#epgSubnets").getAciNumericTextBox().value(resp.epgSubnets);
                $("#epgSpan").getAciNumericTextBox().value(resp.span);
                $("#epgEndPoints").getAciNumericTextBox().value(resp.noOfEndPoints);
                $("#sharedfilter").getAciNumericTextBox().value(resp.noOfsharedServiceFilter);
                document.getElementById("epgName").value = resp.name;

                e.shape.span = resp.span;
                e.shape.epgSubnets = resp.epgSubnets;
                e.shape.noOfEndPoints = resp.noOfEndPoints;
                e.shape.name = resp.name;
                e.shape.noOfsharedServiceFilter = resp.noOfsharedServiceFilter;
                e.shape.sharedServiceId = resp.sharedServiceId;
                document.getElementById("shared-checkbox").checked = resp.sharedServicesEnabled;
                e.shape.sharedServicesEnabled = resp.sharedServicesEnabled;
              });
            } else {
              logicService.getEpgDetails(e.shape.pKey, function(resp) {
                $("#epgSubnets").getAciNumericTextBox().value(resp.epgSubnets);
                $("#epgSpan").getAciNumericTextBox().value(resp.span);
                $("#epgEndPoints").getAciNumericTextBox().value(resp.noOfEndPoints);
                $("#sharedfilter").getAciNumericTextBox().value(resp.noOfsharedServiceFilter);
                e.shape.span = resp.span;
                e.shape.epgSubnets = resp.epgSubnets;
                e.shape.noOfEndPoints = resp.noOfEndPoints;
                e.shape.bdName = resp.bdName;

                e.shape.noOfsharedServiceFilter = resp.noOfsharedServiceFilter;
                document.getElementById("shared-checkbox").checked = resp.sharedServicesEnabled;
                e.shape.sharedServicesEnabled = resp.sharedServicesEnabled;
              });
            }


          } else if (e.shape.type === "contract") {
            // Manohar ::
            $('#contractText').val(e.shape.configName || 'None');

            $('#contractConfig').click(function(){
              //showing the list of contracts in popup
              popupService.showContractSelection($scope.$new()).then(function(data){
                //updating the selected contract name in UI
                $('#contractText').val(data.objectName);
                $('#contractText').trigger('change');
                repoTestService.getObjectDetails(data.id, OBJECT_TYPES[data.objectType].repoTypeRestReq)
                  .then(function(objectDetails){
                    _selectedContractConfigDetails = objectDetails;
                  });
              });
            })

            $('#contractText').click(function(){
              //showing the config object details in popup
              if(_selectedContractConfigDetails){
                popupService.showObjectDetails($scope.$new(), _selectedContractConfigDetails, e.shape.type);
              }else if(e.shape.subjects && e.shape.subjects.length){
                repoTestService.getObjectDetails(e.shape.subjects[0].id, OBJECT_TYPES[e.shape.type].repoTypeRestReq)
                  .then(function(detailsObject){
                    popupService.showObjectDetails($scope.$new(), detailsObject, e.shape.type);
                  });
              }
            })


            var log = [];
            var data = appDiagram.dataSource.data();
            for (var i = 0; i < data.length; i++) {
              // Show epg's for the selected application
              if ((data[i].type == "epg") &&
                (data[i].appId == $rootScope.applicationList[$rootScope.selectedApplicationIndex].id)) {
                if ($scope.nearestEpgId != data[i].pKey) {
                  log.push({ text: data[i].fullName, value: data[i].pKey, type: data[i].type, uid: data[i].uid });
                }
              }
            }
            // If contract is edited, grey out the fields for provider and consumer
            if (e.shape.pKey) {
              document.getElementById("contractproviderId").disabled = true;
              document.getElementById("contractconsumerId").disabled = true;
            } else {
              e.shape.providerId = $scope.nearestEpg.dataItem.fullName;
              document.getElementById("contractproviderId").disabled = true;
            }
            $("#contractproviderId").aciComboBox({
              dataTextField: "text",
              dataValueField: "value",
              dataSource: log,
              filter: "contains",
              suggest: true
            });
            $("#contractconsumerId").aciComboBox({
              dataTextField: "text",
              dataValueField: "value",
              dataSource: log,
              filter: "contains",
              suggest: true
            });

            // $("#contractCount").aciNumericTextBox({ format: 'n0' });

            if (!e.shape.pKey) {
              logicService.getNodeDefaultValues("contract", function(resp) {
                // $("#contractCount").getAciNumericTextBox().value(resp.noOfFilters);
                e.shape.count = resp.noOfFilters;
                document.getElementById("contractName").value = resp.name;
                e.shape.name = resp.name;
                document.getElementById("provider-checkbox").checked = resp.providerEnforced;
                e.shape.providerEnforced = resp.providerEnforced;
                document.getElementById("consumer-checkbox").checked = resp.consumerEnforced;
                e.shape.consumerEnforced = resp.consumerEnforced;
              });
            } else {
              // logicService.getContractDetails(e.shape.pKey, function(resp) {
              //   $("#contractCount").getAciNumericTextBox().value(resp.noOfFilters);
              // });
            }
            //  document.getElementById('contractproviderId').disabled = true;
          } else if (e.shape.type === "contractl3") {
            // Manohar ::
            $('#contractL3Text').val(e.shape.configName || 'None');

            $('#contractL3Config').click(function(){
              //showing the list of contracts in popup
              popupService.showContractSelection($scope.$new()).then(function(data){
                //updating the selected contract name in UI
                $('#contractL3Text').val(data.objectName);
                $('#contractL3Text').trigger('change');
                repoTestService.getObjectDetails(data.id, 'contracts')
                  .then(function(objectDetails){
                    _selectedContractConfigDetails = objectDetails;
                  });
              });
            })

            $('#contractL3Text').click(function(){
              //showing the config object details in popup
              if(_selectedContractConfigDetails){
                popupService.showObjectDetails($scope.$new(), _selectedContractConfigDetails, 'contract');
              }else if(e.shape.subjects && e.shape.subjects.length){
                repoTestService.getObjectDetails(e.shape.subjects[0].id, 'contracts')
                  .then(function(detailsObject){
                    popupService.showObjectDetails($scope.$new(), detailsObject, 'contract');
                  });
              }
            })


            var log = [];
            var consumer = [];
            var data = appDiagram.dataSource.data();
            for (var i = 0; i < data.length; i++) {
              // Show epg's for the selected application
              if ((data[i].type == "epg") &&
                (data[i].appId == $rootScope.applicationList[$rootScope.selectedApplicationIndex].id)) {
                if ($scope.nearestEpgId != data[i].pKey) {
                  log.push({ text: data[i].fullName, value: data[i].pKey, type: data[i].type, uid: data[i].uid });
                }
              }
            }

            var fabricData = fabricDiagram.dataSource.data();
            for (var i = 0; i < fabricData.length; i++) {
              // Show epg's for the selected application
              if ((fabricData[i].type == "l3out")) {
                consumer.push({ text: fabricData[i].fullName, value: fabricData[i].pKey, type: fabricData[i].type, uid: fabricData[i].uid });
              }
            }
            // If contract is edited, grey out the fields for provider and consumer
            if (e.shape.pKey) {
              document.getElementById("contractl3providerId").disabled = true;
              document.getElementById("contractl3consumerId").disabled = true;
            } else {
              e.shape.providerId = $scope.nearestEpg.dataItem.fullName;
              document.getElementById("contractl3providerId").disabled = true;
            }
            $("#contractl3providerId").aciComboBox({
              dataTextField: "text",
              dataValueField: "value",
              dataSource: log,
              filter: "contains",
              suggest: true
            });

            $("#contractl3consumerId").aciComboBox({
              dataTextField: "text",
              dataValueField: "value",
              dataSource: consumer,
              filter: "contains",
              suggest: true
            });


            $("#contractl3count").aciNumericTextBox({ format: 'n0' });

            if (!e.shape.pKey) {
              logicService.getNodeDefaultValues("contractl3", function(resp) {
                $("#contractl3count").getAciNumericTextBox().value(resp.noOfFilters);
                e.shape.count = resp.noOfFilters;
                document.getElementById("contractl3Name").value = resp.name;
                e.shape.name = resp.name;
                document.getElementById("provider-checkbox").checked = resp.providerEnforced;
                e.shape.providerEnforced = resp.providerEnforced;
                document.getElementById("consumer-checkbox").checked = resp.consumerEnforced;
                e.shape.consumerEnforced = resp.consumerEnforced;

                //Bug Fix 253
                e.shape.consumerId = resp.consumerName;
                $("#contractl3consumerId").aciComboBox({
                  dataTextField: "text",
                  dataValueField: "value",
                  dataSource: consumer,
                  filter: "contains",
                  suggest: true
                });

                e.shape.consumerId = {
                  type: resp.consumerType,
                  value: resp.consumerId
                };

                //var selectedConsumer = document.getElementById("contractl3consumerId");
                e.shape.selectedL3outId = resp.consumerId;
                e.shape.dirty = true;
                // Fix end
              });
            } else {
              logicService.getContractDetails(e.shape.pKey, function(resp) {
                // $("#contractl3count").getAciNumericTextBox().value(resp.noOfFilters);
                //read the value from e.shape & set the checkbox on Edit

                if (resp.providerEnforced === true) {
                  document.getElementById("provider-checkbox").checked = true;
                } else {
                  document.getElementById("provider-checkbox").checked = false;
                }

                if (resp.consumerEnforced === true) {
                  document.getElementById("consumer-checkbox").checked = true;
                } else {
                  document.getElementById("consumer-checkbox").checked = false;
                }

                e.shape.dirty = true;
              });
            }
          } else if (e.shape.type === "contractshared") {
            var log = [];
            var consumer = [];
            var data = appDiagram.dataSource.data();
            for (var i = 0; i < data.length; i++) {
              // Show epg's for the selected application
              if ((data[i].type == "epg") &&
                (data[i].appId == $rootScope.applicationList[$rootScope.selectedApplicationIndex].id)) {
                if ($scope.nearestEpgId != data[i].pKey) {
                  log.push({ text: data[i].fullName, value: data[i].pKey, type: data[i].type, uid: data[i].uid });
                }
              }
            }

            var fabricData = fabricDiagram.dataSource.data();
            for (var i = 0; i < fabricData.length; i++) {
              // Show epg's for the selected application
              if ((fabricData[i].type == "shared")) {
                consumer.push({ text: fabricData[i].fullName, value: fabricData[i].pKey, type: fabricData[i].type, uid: fabricData[i].uid });
              }
            }
            // If contract is edited, grey out the fields for provider and consumer
            if (e.shape.pKey) {
              document.getElementById("contractsharedproviderId").disabled = true;
              document.getElementById("contractsharedconsumerId").disabled = true;
            } else {
              e.shape.providerId = $scope.nearestEpg.dataItem.fullName;
              document.getElementById("contractsharedproviderId").disabled = true;

            }
            $("#contractsharedproviderId").aciComboBox({
              dataTextField: "text",
              dataValueField: "value",
              dataSource: log,
              filter: "contains",
              suggest: true
            });


            $("#contractsharedcount").aciNumericTextBox({ format: 'n0' });

            if (!e.shape.pKey) {
              logicService.getNodeDefaultValues("contractshared", function(resp) {
                $("#contractsharedcount").getAciNumericTextBox().value(resp.noOfFilters);
                e.shape.count = resp.noOfFilters;
                document.getElementById("contractsharedName").value = resp.name;
                e.shape.name = resp.name;
                document.getElementById("provider-checkbox").checked = resp.providerEnforced;
                e.shape.providerEnforced = resp.providerEnforced;
                document.getElementById("consumer-checkbox").checked = resp.consumerEnforced;
                e.shape.consumerEnforced = resp.consumerEnforced;
                e.shape.consumerId = resp.consumerName;
                document.getElementById("contractsharedconsumerId").disabled = true;
                $("#contractsharedconsumerId").aciComboBox({
                  dataTextField: "text",
                  dataValueField: "value",
                  dataSource: log,
                  filter: "contains",
                  suggest: true
                });

                e.shape.consumerId = {
                  type: resp.consumerType,
                  value: resp.consumerId
                };
                e.shape.dirty = true;

              });

            } else {
              logicService.getContractDetails(e.shape.pKey, function(resp) {
                $("#contractsharedcount").getAciNumericTextBox().value(resp.noOfFilters);
                //read the value from e.shape & set the checkbox on Edit
                if (resp.providerEnforced === true) {
                  document.getElementById("provider-checkbox").checked = true;
                } else {
                  document.getElementById("provider-checkbox").checked = false;
                }

                if (resp.consumerEnforced === true) {
                  document.getElementById("consumer-checkbox").checked = true;
                } else {
                  document.getElementById("consumer-checkbox").checked = false;
                }
                e.shape.consumerId = resp.consumerName;
                document.getElementById("contractsharedconsumerId").disabled = true;
                $("#contractsharedconsumerId").aciComboBox({
                  dataTextField: "text",
                  dataValueField: "value",

                  filter: "contains",
                  suggest: true
                });
                e.shape.dirty = true;
              });
            }
          } else if (e.shape.type == "app") {
            $("#appInstances").aciNumericTextBox({ format: 'n0' });

          }

        }

        function onSelect(diagram, node) {
          if (node) {
            // diagrameditor must contain templates for each and every node we add else the default will be displayed
            var templateName = "#" + node.type + "Template";
            var tmpltname = $(templateName).html();
            if (tmpltname) {
              var javascriptTemplate = aci.template(tmpltname);
              var javascriptData = {};

              /* <!-- If EPG then render a different template --> */
              if (node.type === "epg") {
                javascriptData["addEpgMaxSpan"] = DIALOG_CONSTANTS.epg.maxspan;
                javascriptData["addEpgMinSpan"] = DIALOG_CONSTANTS.epg.minspan;
                javascriptData["addEpgMinSubnets"] = DIALOG_CONSTANTS.epg.minsubnets;
                javascriptData["addEpgMaxSubnets"] = DIALOG_CONSTANTS.epg.maxsubnets;
                javascriptData["addEpgMaxEndPoints"] = DIALOG_CONSTANTS.epg.maxendpoints;
                javascriptData["addEpgMinEndPoints"] = DIALOG_CONSTANTS.epg.minendpoints;
              } else if (node.type === "vrf" || node.type === "bd" || node.type === "contract") {
                javascriptData["contractNodes"] = [];
              }
              diagram.options.editable.shapeTemplate = javascriptTemplate(javascriptData);
            }
          }

        }


        function onChange(e) {
          /*<!-- Check if an node was removed then remove its connections as well --> */
          if (e.added.length > 0) {
            var addedItem = e.added[0];
          }
          if (e.removed.length > 0) {
            var removedObjs = e.removed;
            for (var count = 0; count < removedObjs.length; count++) {
              var shp = removedObjs[count];
              if (shp instanceof Shape) {
                //  removeEdges(shp.dataItem.id);
              }
            }
          }
        }

        function removeEdges(shapeId) {
          var toConnection = findToConnectionsforUid(shapeId);
          var fromConnection = findFromConnectionsforUid(shapeId);
          if (toConnection) {
            appDiagram.remove(toConnection);
          }
          if (fromConnection) {
            appDiagram.remove(fromConnection);
          }
        }

        function onCancel(e) {
          /*  if (e.shape.isSaved == false) {
                var uid = e.shape.uid;
                var id = e.shape.id;
                removeEdges(id);
                appDiagram.dataSource.pushDestroy(appDiagram.dataSource.getByUid(uid));
                appDiagram.dataSource.sync();
            }*/

          _selectedContractConfigDetails = null;

          $scope.loadApplication();
          $scope.reAlignTree = true;
          if ($scope.nearestEpg != null) {
            $scope.nearestEpgId = null;
            $scope.nearestEpg.redrawVisual();
          }
          return false;
        }

        (function($) {
          var CustomEditorDiagram = aci.dataviz.ui.Diagram.extend({
            options: {
              name: "CustomEditorDiagram"
            },
            editModel: function(dataItem, editorType) {
              aci.dataviz.ui.Diagram.fn.editModel.call(this, dataItem, editorType);
              var title = null;

              if (dataItem.type == "app") {
                title = "Edit Application";
              } else {
                // For Add windows
                if (!dataItem.pKey) {
                  if (dataItem.type == "epg") {
                    title = "Add Epg";

                  } else if ((dataItem.type == "contract") ||
                    (dataItem.type == "contractl3") ||
                    (dataItem.type == "contractshared")) {
                    title = "Add Contract";
                  } else if (dataItem.type == "vrf") {
                    title = "Add VRF";
                  } else if (dataItem.type == "bd") {
                    title = "Add BD";
                  } else if (dataItem.type == "l3out") {
                    title = "Add L3OUT";
                  } else {
                    title = "EDIT";
                  }
                } else {
                  if (dataItem.type == "epg") {
                    title = "EDIT EPG";
                  } else if ((dataItem.type == "contract") ||
                    (dataItem.type == "contractl3") ||
                    ((dataItem.type == "contractshared"))) {
                    title = "EDIT CONTRACT";
                  } else if (dataItem.type == "vrf") {
                    title = "EDIT VRF";
                  } else if (dataItem.type == "bd") {
                    title = "EDIT BD";
                  } else if (dataItem.type == "l3out") {
                    title = "EDIT L3OUT";
                  } else if (dataItem.type == "shared") {
                    title = "EDIT SHARED RESOURCE";
                  } else {
                    title = "EDIT";
                  }
                }
              }
              this.editor.window.title(title);

            }
          });
          aci.ui.plugin(CustomEditorDiagram);
        })(jQuery);

        $scope.offsetY = 30;
        /*Below is the function to get visible height of any pane that does not include the scroll height*/
        $scope.getVisibleHeight = function() {
          var elBottom, elTop, scrollBot, scrollTop, visibleBottom, visibleTop;
          scrollTop = $("#appDiagram").scrollTop();
          scrollBot = scrollTop + $("#appDiagram").height();
          elTop = $("#appDiagram").offset().top;
          elBottom = elTop + $("#appDiagram").outerHeight();
          visibleTop = elTop < scrollTop ? scrollTop : elTop;
          visibleBottom = elBottom > scrollBot ? scrollBot : elBottom;
          return visibleBottom - visibleTop;
        }
        $scope.displayMouseClickPopUp = function(e, template) {
          console.log("displayMouseClickPopUp");
          console.log(e);
          var myElement = document.getElementById("fabricDiagram");
          var position = $scope.getFabricPosition(myElement);

          var cMenu = $("#context-menu").data("aciContextMenu");
          var nodePreviewHtml = template(e.item.dataItem);
          $("#node-preview").html(nodePreviewHtml);
          var visHt = $scope.getVisibleHeight();
          var yHeight = $rootScope.Y;
          var xHeight = $rootScope.X;

          if (e.item.dataItem.type != "app") {
            if ($rootScope.Y < 512) {
              yHeight = $rootScope.Y - visHt / 2;
            } else if ($rootScope.Y > 512) {
              yHeight = $rootScope.Y - visHt / 1.5;
            }
          } else {
            yHeight = $rootScope.Y;
          }

          var xPositionDiff = position.x - $rootScope.X;
          if (e.item.dataItem.type != "app") {
            if (xPositionDiff > 325) {
              cMenu.open($rootScope.X + (e.item.bounds().width / 2), yHeight);
            } else {
              cMenu.open(($rootScope.X - (e.item.bounds().width) - 275), yHeight);
            }
          } else {
            cMenu.open($rootScope.X + (e.item.bounds().width / 2), yHeight);
          }

          $("#iconEditButton").aciButton({
            spriteCssClass: "aci-edit-icon-mouseclick",
            click: function() {
              e.item.diagram.edit(e.item);
            }
          });
          $("#iconDeleteButton").aciButton({
            spriteCssClass: "aci-del-icon-mouseclick",
            click: function() {
              // e.item.diagram.remove(e.item);
              var dItem = {};
              var src = appDiagram.dataSource.get(e.item.dataItem.id);
              angular.copy(src, dItem);
              if (dItem.type == "app") {
                $scope.openDelAppWindow($rootScope.selectedApplicationIndex);
              } else {
                $scope.openDelNodeWindow(dItem);
              }
            }
          });
        }

        console.log("#appDiagram");
        console.log($('#appDiagram'));
        var appDiagram = $("#appDiagram").aciCustomEditorDiagram({
          theme: "default",
          eventType: [],
          isConnectCalled: false,
          autoBind: true,
          remove: onRemove,
          edit: onEdit,
          click: function(e) {

            if (e.item.dataItem.type == "epg") {
              var template = aci.template($("#epg-node-template").html());
              logicService.getEpgDetails(e.item.dataItem.pKey, function(resp) {
                e.item.dataItem.bdName = resp.bdName;
                e.item.dataItem.noOfsharedServiceFilter = resp.noOfsharedServiceFilter;
                $scope.displayMouseClickPopUp(e, template);

              });
            } else if (e.item.dataItem.type == "app") {
              var template = aci.template($("#app-node-template").html());
              $scope.displayMouseClickPopUp(e, template);
            } else if (e.item.dataItem.type == "contract") {
              var template = aci.template($("#contract-node-template").html());
              logicService.getContractDetails(e.item.dataItem.pKey, function(resp) {
                e.item.dataItem.providerName = resp.providerName;
                e.item.dataItem.consumerName = resp.consumerName;
                e.item.dataItem.noOfFilters = resp.noOfFilters;
                e.item.dataItem.providerEnforced = resp.providerEnforced;
                e.item.dataItem.consumerEnforced = resp.consumerEnforced;
                $scope.displayMouseClickPopUp(e, template);
              });


            } else if (e.item.dataItem.type == "contractl3") {
              var template = aci.template($("#contractl3-node-template").html());
              logicService.getContractDetails(e.item.dataItem.pKey, function(resp) {
                e.item.dataItem.providerName = resp.providerName;
                e.item.dataItem.consumerName = resp.consumerName;
                e.item.dataItem.noOfFilters = resp.noOfFilters;
                e.item.dataItem.providerEnforced = resp.providerEnforced;
                e.item.dataItem.consumerEnforced = resp.consumerEnforced;
                $scope.displayMouseClickPopUp(e, template);
              });
            } else if (e.item.dataItem.type == "contractshared") {
              var template = aci.template($("#contractl3-node-template").html());
              logicService.getContractDetails(e.item.dataItem.pKey, function(resp) {
                e.item.dataItem.providerName = resp.providerName;
                e.item.dataItem.consumerName = resp.consumerName;
                e.item.dataItem.noOfFilters = resp.noOfFilters;
                e.item.dataItem.providerEnforced = resp.providerEnforced;
                e.item.dataItem.consumerEnforced = resp.consumerEnforced;
                $scope.displayMouseClickPopUp(e, template);
              });
            }
          },
          change: onChange,
          cancel: onCancel,
          dataSource: appDataSource,
          connectionsDataSource: {},
          editable: {
            tools: false
          },
          shapeDefaults: {
            connectors: [{
              "name": "auto",
              position: function(shape) {
                var p = shape.bounds().top();
                return shape._transformPoint(new aci.dataviz.diagram.Point(p.x, p.y));
              }
            }, {
              name: "Upstream",
              position: function(shape) {
                var p = shape.bounds().top();
                return shape._transformPoint(new aci.dataviz.diagram.Point(p.x, p.y));

              }
            }, {
              name: "Downstream",
              position: function(shape) {
                var p = shape.bounds().bottom();
                if ((shape.type == "app") && (shape.dataItem.noOfInstances > 1)) {
                  return shape._transformPoint(new aci.dataviz.diagram.Point(p.x - 6, p.y - 8));
                }
                return shape._transformPoint(new aci.dataviz.diagram.Point(p.x, p.y));


              }
            }],
            editable: {
              tools: false
            },
            hover: {
              fill: { color: "red" }
            },

            visual: function(options) {
              var diagram = aci.dataviz.diagram;
              var g = new diagram.Group();
              var path = null;
              options = angular.extend(options, DIAGRAM_CONSTANTS[options.type].options);
              var temp = options.Id;
              options.Id = options.uid;
              options.id = options.uid;
              var shapeWidth = 55;
              var len = 5;

              if ($scope.nearestEpg != null && options.type == "epg") {
                if (options.dataItem.pKey == $scope.nearestEpgId) {
                  var path = new diagram.Path({
                    data: options.path,
                    width: shapeWidth,
                    height: options.height,
                    content: {
                      align: "middle"
                    },
                    x: 5,
                    fill: {
                      color: "#33c4d7",
                      width: 15,
                    },
                    stroke: {
                      width: 15,
                      color: "#33c4d7"
                    }
                  });
                } else {
                  var path = new diagram.Path({
                    data: options.path,
                    width: shapeWidth,
                    height: options.height,
                    content: {
                      align: "middle"
                    },
                    x: 5,
                    fill: "#23Bfd4",
                  });
                  $scope.nearestEpg = null;
                }

              } else {
                /*if (options.type == "contractl3")
            {
                            shapeWidth = options.width;
                            path = new diagram.Path({
                data: options.path,
                width: shapeWidth,
                                content: {
                                    align: "middle"
                                },
                height: options.height,
                fill: "#128FFF",
                y:-8,
              });
            }*/
                //else
                {
                  shapeWidth = options.width;
                  path = new diagram.Path({
                    data: options.path,
                    width: shapeWidth,
                    height: options.height,
                    content: {
                      align: "middle"
                    },
                    x: 5,
                    fill: "#23Bfd4"
                  });
                }
              }
              var bbox = path.drawingElement.bbox();
              if (options.dataItem.name) {
                len = ((bbox.width() + bbox.origin.x) - options.dataItem.name.visualLength()) / 2;
              }
              // position the grey part of icon
              if (options.type == 'app') {
                shapeWidth = options.width - 28;
                var path2 = new diagram.Path({
                  data: options.path2,
                  width: shapeWidth,
                  content: {
                    align: "middle"
                  },
                  height: options.height - 47,
                  x: -1,
                  y: 2,
                  fill: "#070808",
                });
                shapeWidth = options.width - 35;
                var path3 = new diagram.Path({
                  data: options.path3,
                  width: shapeWidth,
                  content: {
                    align: "middle"
                  },
                  height: options.height - 35,
                  x: 8,
                  y: 9,
                  fill: "#49494a",
                });
                if (options.dataItem.noOfInstances > 1) {
                  var bubble = new diagram.Path({
                    data: options.bubble,
                    width: 34,
                    content: {
                      align: "middle"
                    },
                    height: 34,
                    x: 45,
                    y: -8,
                    fill: {
                      color: "#0C93C5",
                    }
                  });
                }
              } else if (options.type == 'epg') {
                shapeWidth = options.width - 32;
                var path2 = new diagram.Path({
                  data: options.path2,
                  width: shapeWidth,
                  content: {
                    align: "middle"
                  },
                  height: options.height - 45,
                  x: 5,
                  y: 0,
                  fill: "#070808",
                });
                shapeWidth = options.width - 30;
                var path3 = new diagram.Path({
                  data: options.path3,
                  width: shapeWidth,
                  content: {
                    align: "middle"
                  },
                  height: options.height - 40,
                  y: 1,
                  x: 3,
                  fill: "#49494a",
                });
              } else if (options.type == 'contract') {
                shapeWidth = options.width - 30;
                var path2 = new diagram.Path({
                  data: options.path2,
                  width: shapeWidth,
                  content: {
                    align: "middle"
                  },
                  height: options.height - 35,
                  fill: "#49494a",
                  x: 0,
                  y: 3,
                });
                shapeWidth = options.width - 20;
                var path3 = new diagram.Path({
                  data: options.path3,
                  width: shapeWidth,
                  content: {
                    align: "middle"
                  },
                  height: options.height - 46,
                  fill: "#070808",
                  x: 5,
                  y: 0,
                });
              } else if (options.type == 'contractl3') {
                shapeWidth = options.width - 35;
                var path2 = new diagram.Path({
                  data: options.path2,
                  width: shapeWidth,
                  height: options.height - 18,
                  content: {
                    align: "middle"
                  },
                  fill: "#070808",
                  x: 0,
                  y: -2,
                });
                shapeWidth = options.width - 45;
                var path3 = new diagram.Path({
                  data: options.path3,
                  width: shapeWidth,
                  height: options.height - 48,
                  content: {
                    align: "middle"
                  },
                  fill: "#49494a",
                  x: 3,
                  y: 6,
                });
              } else if (options.type == 'contractshared') {
                shapeWidth = options.width - 25;
                var path2 = new diagram.Path({
                  data: options.path2,
                  width: shapeWidth,
                  height: options.height - 20,
                  content: {
                    align: "middle"
                  },
                  fill: "#070808",
                  x: 10,
                  y: -3,
                });
                shapeWidth = options.width - 35;
                var path3 = new diagram.Path({
                  data: options.path3,
                  width: shapeWidth,
                  height: options.height - 48,
                  content: {
                    align: "middle"
                  },
                  fill: "#49494a",
                  x: 8,
                  y: 3,
                });
              }

              function getTextOffset() {
                var offset = 5;
                if (options.dataItem.name) {
                  if (options.dataItem.name.length < 6) {
                    offset = 10;
                  } else if (options.dataItem.name.length < 8) {
                    offset = 3;
                  } else {
                    offset = 0;
                  }
                }
                return offset;
              }

              function getOffset() {
                var offset = 56;
                if (options.dataItem.noOfInstances) {
                  if (options.dataItem.noOfInstances.toString().length == 1) {
                    offset = 57;
                  } else if (options.dataItem.noOfInstances.toString().length == 2) {
                    offset = 53;
                  } else {
                    offset = 48;
                  }
                }
                return offset;
              }

              var tb = new diagram.TextBlock({
                autoSize: false,
                width: bbox.width() + bbox.origin.x,
                fontFamily: "Helvetica",
                color: '#070808',
                height: 10,
                text: options.dataItem.name,
                fontSize: 15,
                x: getTextOffset(),
                y: 55
              });
              if (options.type != 'app') {
                g.append(tb);
              } else {
                if (options.dataItem.noOfInstances > 1) {
                  var instances = new diagram.TextBlock({
                    autoSize: false,
                    width: 15,
                    fontFamily: "Helvetica",
                    height: 22,
                    text: options.dataItem.noOfInstances,
                    fontSize: 17,
                    fill: "#FFFFFF",
                    stroke: {
                      color: "white"
                    },
                    x: getOffset(),
                    y: 0,
                    //dx:-10,
                  });
                }

              }

              g.append(new diagram.Rectangle({
                width: bbox.width() + bbox.origin.x,
                height: bbox.height() + bbox.origin.y,
                stroke: {
                  width: 0
                }
              }));
              g.append(path);
              if (path2) {
                g.append(path2);
              }
              if (path3) {
                g.append(path3);
              }
              if (bubble) {
                g.append(bubble);
              }
              if (instances) {
                g.append(instances);
              }
              return g;
            }
          },
          layout: {
            type: "tree",
            subtype: "down",
            horizontalSeparation: 50,
            verticalSeparation: 90,
            grid: {
              offsetY: $scope.offsetY,
            },
            _mouseUp: function(e) {},
          },
          connectionDefaults: {
            type: "polyline",
            editable: false,
            startCap: 'none',
            endCap: 'none',
            stroke: {
              //  dashType: "longDash",
              color: "#979797",
              width: 2
            }
          },
          select: function(e) {
            if (e.selected.length) {
              selected = e.selected;
              var element = e.selected[0];
              if (element instanceof Shape) {
                if ((element.dataItem.type == "epg") || (element.dataItem.type == "contractl3")) {
                  highlightNodes(element);
                }

                onSelect(appDiagram, element);
                // TODO : Fix this later
                //updateShapeProperties(element.options);
              } else {
                // TODO : Fix this later
                //updateConnectionProperties(element.options);
              }
            } else {
              // If selection is removed, remove the highlight for bd, l3out
              if ($scope.epgSelected == true) {
                $scope.epgSelected = false;
                if ($scope.selectedBd) {
                  $scope.selectedBd.redrawVisual();
                }
                if ($scope.selectedVrf) {
                  $scope.selectedVrf.redrawVisual();
                }
                if ($scope.selectedl3out) {
                  $scope.selectedl3out.forEach(function(shape) {
                    shape.redrawVisual();
                  });
                }
              } else if ($scope.contractl3Selected == true) {
                $scope.contractl3Selected = false;
                if ($scope.selectedl3out) {
                  $scope.selectedl3out.redrawVisual();
                }
              }
            }
          }
        }).getAciCustomEditorDiagram();


        var highlightNodes = function(element) {
          var shapes = fabricDiagram.shapes;
          // If epg is selected, highlight the bd and l3out
          if (element.dataItem.type == "epg") {
            // Highlight bd
            $scope.epgSelected = true;
            shapes.forEach(function(shape) {
              if (shape.dataItem.type == "bd") {
                if (element.dataItem.bdId == shape.dataItem.id) {
                  shape.redrawVisual();
                  $scope.selectedBd = shape;

                }
              }
            });
            //highlight vrf
            shapes.forEach(function(shape) {
              if (shape.dataItem.type == "vrf") {
                if ($scope.selectedBd.dataItem.vrfId == shape.dataItem.id) {
                  shape.redrawVisual();
                  $scope.selectedVrf = shape;

                }
              }
            });
            // highlight l3out
            $scope.selectedl3out = [];
            logicService.getEpgContracts(element.dataItem.pKey, function(resp) {
              var contractList = resp;
              contractList.forEach(function(contract) {
                if (contract.type == "contractl3") {
                  shapes.forEach(function(shape) {
                    if (shape.dataItem.type == "l3out") {
                      if (contract.consumerId == shape.dataItem.id) {
                        shape.redrawVisual();
                        $scope.selectedl3out.push(shape);
                      }
                    }
                  });
                }
              });
            });
          } else if (element.dataItem.type == "contractl3") {
            $scope.contractl3Selected = true;
            logicService.getContractDetails(element.dataItem.pKey, function(resp) {
              shapes.forEach(function(shape) {
                if (shape.dataItem.type == "l3out") {
                  if (resp.consumerId == shape.dataItem.id) {
                    shape.redrawVisual();
                    $scope.selectedl3out = shape;
                  }
                }
              });
            });
          }
        }

        $scope.appDiagram = appDiagram;
        var fabricDataSource = new aci.data.DataSource({
          batch: false,
          transport: {
            read: function(options) {
              options.success([]);
            },
            update: function(options) {
              $scope.loadMask("#fabricDiagram");
              if (options.data.isSaved == false) {
                if (options.data.type == "vrf") {
                  // options.data.status = options.data.status.value;
                  var checked = $("#status").is(":checked");
                  if (checked == true) {
                    options.data.enforced = "true";
                  } else {
                    options.data.enforced = "false";
                  }
                  logicService.addVrf(options.data, function(resp) {
                    $scope.showLoadingImage();
                    $rootScope.canceler.resolve();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });
                    options.data.isSaved = true;
                    options.data.pKey = resp.id;
                    options.data.id = resp.id;
                    options.data.name = resp.name;
                    options.data.enforced = resp.enforced;
                    options.success(options.data);
                    $scope.updateTenantList();
                    //$scope.fabricDiagram.dataSource.sync();
                    $scope.loadFabric();
                  });

                } else if (options.data.type == "bd") {
                  options.data.vrf = {
                    value: $scope.nearestvrf.dataItem.id,
                    text: $scope.nearestvrf.dataItem.fullName
                  };
                  logicService.addBd(options.data, function(resp) {
                    // Fix for adding bd to default vrf from different tenant
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });

                    if (!isNaN(resp)) {
                      fabricDiagram.dataSource.pushDestroy(fabricDiagram.dataSource.get(options.data.Id));
                      fabricDiagram.dataSource.sync();
                    } else {
                      options.data.isSaved = true;
                      options.data.pKey = resp.id;
                      options.data.id = resp.id;
                      options.data.name = resp.name;
                      options.data.bdSubnets = resp.bdSubnets;
                      options.data.vrfId = options.data.vrf;
                      options.data.fullName = resp.fullName;
                      options.success(options.data);
                      $scope.updateTenantList();
                      $scope.loadFabric();
                    }
                    $scope.nearestVrfId = null;
                    $scope.nearestvrf.redrawVisual();
                  });
                } else if (options.data.type == "l3out") {
                  logicService.addL3out(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });

                    options.data.isSaved = true;
                    options.data.pKey = resp.id;
                    options.data.id = resp.id;
                    options.data.name = resp.name;
                    options.data.fullName = resp.fullName;
                    options.data.vrfId = options.data.vrf;
                    options.data.vrfName = resp.vrfName;
                    options.data.svis = resp.svis;
                    options.data.epgPrefixes = resp.epgPrefixes;
                    options.success(options.data);
                    $scope.loadFabric();
                    //   fabricDiagram.bringIntoView(fabricDiagram.shapes, {align: "layout"});
                  });
                  $scope.nearestVrfId = null;
                  $scope.nearestvrf.redrawVisual();
                }
              } else {
                // this is in case of update
                // this is in case of update
                if (options.data.type == "vrf") {
                  var checked = $("#status").is(":checked");
                  if (checked == true) {
                    options.data.enforced = "true";
                  } else {
                    options.data.enforced = "false";
                  }
                  logicService.updateVrf(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });

                    options.data.isSaved = true;
                    options.data.fullName = resp.fullName;
                    options.data.enforced = resp.enforced;
                    options.success(options.data);
                  });
                } else if (options.data.type == "bd") {
                  logicService.updateBd(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });

                    if (!isNaN(resp)) {
                      $scope.loadFabric();
                    } else {
                      options.data.isSaved = true;
                      options.data.fullName = resp.fullName;
                      options.success(options.data);
                    }
                  });
                } else if (options.data.type == "l3out") {
                  logicService.updateL3out(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });

                    options.data.isSaved = true;
                    options.data.fullName = resp.fullName;
                    options.success(options.data);
                  });
                } else if (options.data.type == "shared") {
                  logicService.updateSharedResource(options.data, function(resp) {
                    $rootScope.canceler.resolve();
                    $scope.showLoadingImage();
                    logicService.getSizingResults(function(resp) {
                      $rootScope.currProject = resp;
                      $scope.removeLoadingImage();
                    });

                    options.data.isSaved = true;
                    options.data.fullName = resp.fullName;
                    options.success(options.data);
                  });
                }
              }


            },
            create: function(options) {
              logicService.getUuid(options.data, function(resp) {
                options.data.Id = resp.uuid;
                options.data.isSaved = false;
                options.success(options.data);
                var node = findFabricShapeById(fabricDiagram, resp.uuid);
                if ((options.data.type == "bd") || (options.data.type == "l3out")) {
                  fabricDiagram.connectionsDataSource.add({ from: $scope.nearestvrf.dataItem.id, to: resp.uuid });
                }
                if (node) {
                  onSelect(fabricDiagram, node);
                  fabricDiagram.edit(node);
                  $scope.doFabircLayout();
                }
              });

            },
            destroy: function(options) {
              if (options.data.type == "bd") {
                options.success();
              } else if (options.data.type == "vrf") {
                options.success();
              } else if (options.data.type == "l3out") {
                options.success();
              }
            },
            parameterMap: function(options, operation) {
              if (operation !== "read") {
                return { models: aci.stringify(options.models || [options]) };
              }
            }
          },
          /* schema: {
           model: { id: "pKey" }
           }*/
          schema: {
            model: {
              id: "id",
              fields: {
                id: { from: 'Id', type: "string", editable: false }
              }
            }
          }

        });

        function removeShapeFromFabricDiagram(shapeId, shape) {
          removeEdges(shapeId);
          fabricDiagram.dataSource.remove(shape);
        }

        function onRemoveFabric(e) {
          e.preventDefault(true);
          var dItem = {};
          var src = fabricDiagram.dataSource.get(e.shape.dataItem.id);
          angular.copy(src, dItem);
          $scope.openDelNodeWindow(dItem);
        };

        function getAllBds() {
          var bds = [];
          var shapes = fabricDiagram.shapes;
          shapes.forEach(function(shape) {
            if (shape.dataItem.type == "bd") {
              bds.push({ "text": shape.dataItem.fullName, "value": shape.dataItem.id });
            }
          });
          return bds;
        }

        function getAllVrfs() {
          var vrfs = [];
          var shapes = fabricDiagram.shapes;

          // WA for vrf drop down issue.need to discuss implement the correct fix
          shapes.forEach(function(shape) {
            if (shape.dataItem.type == "vrf") {
              vrfs.push({ "text": shape.dataItem.fullName, "value": shape.dataItem.id });
            }
          });
          /* for (var idx = 0; idx < shapes.length; idx++) {
               if (shapes[idx].dataItem.type == "vrf") {
                   vrfs.push({"text": shapes[idx].dataItem.fullyQualifiedName, "value": shapes[idx].dataItem.name});
               }
           }
          */
          return vrfs;
        }

        function onEditFabric(e) {

          if (e.shape.type === "vrf") {
            if (e.shape.pKey) {
              if (e.shape.enforced === true) {
                document.getElementById("status").checked = true;
              } else {
                document.getElementById("status").checked = false;
              }
            } else {
              logicService.getNodeDefaultValues("vrf", function(resp) {
                document.getElementById("vrfName").value = resp.name;
                e.shape.name = resp.name;
                document.getElementById("status").checked = true;
                e.shape.enforced = true;
                e.shape.dirty = true;

              });
            }

          } else if (e.shape.type === "bd") {

            var vrfs = getAllVrfs();
            document.getElementById("vrf").disabled = true;
            if (e.shape.pKey) {

              e.shape.vrf = e.shape.vrfName;
              logicService.getbdDetails(e.shape.pKey, e.shape.vrfId, e.shape.vrfName, function(resp) {
                e.shape.vrf = resp.vrfName;
                $("#vrf").aciComboBox({
                  dataTextField: "text",
                  dataValueField: "value",
                  dataSource: vrfs,
                  filter: "contains",
                  suggest: true
                });
                $("#subnet").aciNumericTextBox({ format: 'n0' });
                $("#subnet").getAciNumericTextBox().value(e.shape.bdSubnets);
              });
            } else {
              e.shape.vrf = $scope.nearestvrf.dataItem.fullName;
              e.shape.vrfId = $scope.nearestvrf.dataItem.id;
              $("#vrf").aciComboBox({
                dataTextField: "text",
                dataValueField: "value",
                dataSource: vrfs,
                filter: "contains",
                suggest: true
              });
              $("#subnet").aciNumericTextBox({ format: 'n0' });

              logicService.getNodeDefaultValues("bd", function(resp) {
                $("#subnet").getAciNumericTextBox().value(resp.bdSubnets);
                e.shape.bdSubnets = resp.bdSubnets;
                document.getElementById("bdName").value = resp.name;
                e.shape.name = resp.name;
                e.shape.dirty = true;
              });
            }
          } else if (e.shape.type === "l3out") {

            var vrfs = getAllVrfs();
            if (e.shape.pKey) {
              document.getElementById("vrf").disabled = true;
              e.shape.vrf = e.shape.vrfName;
              logicService.getl3Details(e.shape.pKey, e.shape.vrfId, e.shape.vrfName, function(resp) {
                e.shape.vrf = resp.vrfName;
                $("#vrf").aciComboBox({
                  dataTextField: "text",
                  dataValueField: "value",
                  dataSource: vrfs,
                  filter: "contains",
                  suggest: true
                });
                $("#epgprefixes").aciNumericTextBox({ format: 'n0' });
                $("#l3Span").aciNumericTextBox({ format: 'n0' });
                $("#subif").aciNumericTextBox({ format: 'n0' });
                $("#svi").aciNumericTextBox({ format: 'n0' });
              });
            } else {
              document.getElementById("vrf").disabled = true;
              e.shape.vrf = $scope.nearestvrf.dataItem.fullName;
              e.shape.vrfId = $scope.nearestvrf.dataItem.id;
              $("#vrf").aciComboBox({
                dataTextField: "text",
                dataValueField: "value",
                dataSource: vrfs,
                filter: "contains",
                suggest: true
              });
              $("#epgprefixes").aciNumericTextBox({ format: 'n0' });
              $("#l3Span").aciNumericTextBox({ format: 'n0' });
              $("#subif").aciNumericTextBox({ format: 'n0' });
              $("#svi").aciNumericTextBox({ format: 'n0' });
              logicService.getNodeDefaultValues("l3out", function(resp) {
                $("#epgprefixes").getAciNumericTextBox().value(resp.epgPrefixes);
                $("#l3Span").getAciNumericTextBox().value(resp.span);
                $("#subif").getAciNumericTextBox().value(resp.subInterfaces);
                $("#svi").getAciNumericTextBox().value(resp.svis);
                e.shape.epgPrefixes = resp.epgPrefixes;
                e.shape.span = resp.span;
                e.shape.subInterfaces = resp.subInterfaces;
                e.shape.svis = resp.svis;
                document.getElementById("l3outname").value = resp.name;
                e.shape.name = resp.name;
                e.shape.dirty = true;
              });
            }
          } else if (e.shape.type === "shared") {

            if (e.shape.pKey) {
              var vrfs = getAllVrfs();
              document.getElementById("vrf").disabled = true;
              logicService.getVrfDetails(e.shape.vrf, e.shape.fullName, function(resp) {
                e.shape.vrf = resp.name;
                e.shape.vrfId = resp.id;
                $("#vrf").aciComboBox({
                  dataTextField: "text",
                  dataValueField: "value",
                  dataSource: vrfs,
                  filter: "contains",
                  suggest: true
                });
              });

            }

          }

          window.setTimeout(function() {
            $(":input[name='name']").focus();
          }, 1000);


        };

        function onChangeFabric(e) {
          /* <!-- Check if an node was removed then remove its connections as well --> */
          if (e.removed.length > 0) {
            var removedObjs = e.removed;
            for (var count = 0; count < removedObjs.length; count++) {
              var shp = removedObjs[count];
              if (shp instanceof Shape) {
                removeEdges(shp.dataItem.id);
              }
            }
          }
        };

        function onCancelFabric(e) {
          /*  if (e.shape.isSaved == false) {
                    var uid = e.shape.uid;
                    var id = e.shape.id;
                    removeEdges(id);
                    fabricDiagram.dataSource.pushDestroy(fabricDiagram.dataSource.getByUid(uid));
                    fabricDiagram.dataSource.sync();
          removeEdges(e.shape.id);
                } else {    
        }*/
          $scope.loadFabric();
          if ($scope.nearestvrf != null) {
            $scope.nearestVrfId = null;
            $scope.nearestvrf.redrawVisual();
          }
          return false;
        };


        $("#context-menu").aciContextMenu({
          target: '#ruler'
        });

        document.onmousemove = function(e) {
          $rootScope.X = e.pageX;
          $rootScope.Y = e.pageY;
        }

        $("#fabricDiagram").aciCustomEditorDiagram({
          theme: 'default',
          eventType: [],
          zoomMin: 0.6,
          dataSource: fabricDataSource,
          connectionsDataSource: {},
          remove: onRemoveFabric,
          edit: onEditFabric,
          change: onChangeFabric,
          cancel: onCancelFabric,
          layout: {
            type: "tree",
            subtype: "down",
            horizontalSeparation: 5,
            verticalSeparation: 30,
            grid: {
              width: 500
            }
          },
          editable: {
            tools: false
          },
          click: function(e) {
            var template;
            if (e.item.dataItem.type == "bd") {
              template = aci.template($("#bd-node-template").html());

            } else if (e.item.dataItem.type == "vrf") {
              template = aci.template($("#vrf-node-template").html());
            } else if (e.item.dataItem.type == "l3out") {
              template = aci.template($("#l3out-node-template").html());
            } else if (e.item.dataItem.type == "shared") {
              template = aci.template($("#shared-node-template").html());
            }
            var nodePreviewHtml = template(e.item.dataItem);
            $("#node-preview").html(nodePreviewHtml);
            $("#iconEditButton").aciButton({
              spriteCssClass: "aci-edit-icon-mouseclick",
              click: function() {
                e.item.diagram.edit(e.item);
              }
            });
            $("#iconDeleteButton").aciButton({
              spriteCssClass: "aci-del-icon-mouseclick",
              click: function() {
                //e.item.diagram.remove(e.item);
                var dItem = {};
                var src = fabricDiagram.dataSource.get(e.item.dataItem.id);
                angular.copy(src, dItem);
                $scope.openDelNodeWindow(dItem);
              }
            });
            var cMenu = $("#context-menu").data("aciContextMenu");
            cMenu.open($rootScope.X + (e.item.bounds().width / 2), $rootScope.Y);
          },
          shapeDefaults: {
            connectors: [{
              "name": "auto",
              position: function(shape) {
                var p = shape.bounds().top();
                return shape._transformPoint(new aci.dataviz.diagram.Point(p.x - (22 * $scope.vrfsLength), p.y));
              }
            }, {
              name: "Upstream",
              position: function(shape) {

                var p = shape.bounds().top();
                return shape._transformPoint(new aci.dataviz.diagram.Point(p.x - (22 * $scope.vrfsLength), p.y));

              }
            }, {
              name: "Downstream",
              position: function(shape) {

                var p = shape.bounds().bottom();
                return shape._transformPoint(new aci.dataviz.diagram.Point(p.x - (22 * $scope.vrfsLength), p.y));

              }
            }],
            editable: {
              tools: false
            },
            hover: {
              fill: { color: "red" }
            },
            visual: function(options) {
              var diagram = aci.dataviz.diagram;
              var g = new diagram.Group();
              options.dataItem = angular.extend(options.dataItem, DIAGRAM_CONSTANTS[options.type].options);
              var vrfs = getAllVrfs();
              $scope.vrfsLength = (vrfs.length > 3 ? 3 : vrfs.length);
              $scope.vrfsLength = ($scope.vrfsLength == 0 ? 1 : $scope.vrfsLength);
              $scope.vrfsLength = 1;
              options.Id = options.uid;
              options.id = options.uid;

              $scope.setColor = function() {
                return "#23Bfd4";
              }

              if ($scope.nearestvrf != null && options.type == "vrf") {
                // Changing color for vrf when dragging a bd/l3out
                if (options.dataItem.id == $scope.nearestVrfId) {
                  // highlighted colors for Common VRF
                  if ((options.type == "vrf") && ($rootScope.tenantId !== 1) &&
                    (options.dataItem.fullName && options.dataItem.fullName.indexOf("Common") == 0)) {
                    var path = new diagram.Path({
                      data: options.dataItem.common1,
                      width: $scope.vrfsLength * 53,
                      height: $scope.vrfsLength * 53,
                      fill: "#33c4d7",
                    });

                    var path3 = new diagram.Path({
                      data: options.dataItem.path,
                      width: $scope.vrfsLength * 55,
                      height: $scope.vrfsLength * 55,
                      fill: {
                        color: "#00abc1",
                        width: 15,
                      },
                      stroke: {
                        width: 15,
                        color: "#00abc1",
                      }
                    });
                  } else {
                    var path = new diagram.Path({
                      data: options.dataItem.path,
                      width: $scope.vrfsLength * 55,
                      height: $scope.vrfsLength * 55,
                      fill: {
                        color: "#33c4d7",
                        width: 15,
                      },
                      stroke: {
                        width: 15,
                        color: "#33c4d7",
                      }
                    });
                  }
                } else {
                  if ((options.type == "vrf") && ($rootScope.tenantId !== 1) &&
                    (options.dataItem.fullName && options.dataItem.fullName.indexOf("Common") == 0)) {
                    var path = new diagram.Path({
                      data: options.dataItem.common1,
                      width: $scope.vrfsLength * 55,
                      height: $scope.vrfsLength * 55,
                      fill: "#33c4d7",
                    });
                  } else {
                    var path = new diagram.Path({
                      data: options.dataItem.path,
                      width: $scope.vrfsLength * 55,
                      height: $scope.vrfsLength * 55,
                      fill: $scope.setColor(),
                    });
                  }
                  $scope.colorChanged = false;
                  $scope.nearestvrf = null;
                }

                // If epg node is selected, highlight bd, l3out and vrf 
              } else if ($scope.epgSelected == true) {
                if ((options.type == "bd") || (options.type == "l3out") || ((options.type == "vrf"))) {
                  // highlighted colors for Common VRF
                  if ((options.type == "vrf") && ($rootScope.tenantId !== 1) &&
                    (options.dataItem.fullName && options.dataItem.fullName.indexOf("Common") == 0)) {
                    var path = new diagram.Path({
                      data: options.dataItem.common1,
                      width: $scope.vrfsLength * 53,
                      height: $scope.vrfsLength * 53,
                      fill: "#33c4d7",
                    });

                    var path3 = new diagram.Path({
                      data: options.dataItem.path,
                      width: $scope.vrfsLength * 55,
                      height: $scope.vrfsLength * 55,
                      fill: {
                        color: "#00abc1",
                        width: 15,
                      },
                      stroke: {
                        width: 15,
                        color: "#00abc1",
                      }
                    });
                  } else {
                    var path = new diagram.Path({
                      data: options.dataItem.path,
                      width: $scope.vrfsLength * 55,
                      height: $scope.vrfsLength * 55,
                      fill: {
                        color: "#33c4d7",
                        width: 15,
                      },
                      stroke: {
                        width: 15,
                        color: "#33c4d7",
                      }
                    });
                  }
                }
              } else if ($scope.contractl3Selected == true) {
                if (options.type == "l3out") {
                  var path = new diagram.Path({
                    data: options.dataItem.path,
                    width: $scope.vrfsLength * 55,
                    height: $scope.vrfsLength * 55,
                    fill: {
                      color: "#33c4d7",
                      width: 15,
                    },
                    stroke: {
                      width: 15,
                      color: "#33c4d7",
                    }
                  });
                }
              } else {
                if ((options.type == "vrf") && ($rootScope.tenantId !== 1) &&
                  (options.dataItem.fullName && options.dataItem.fullName.indexOf("Common") == 0)) {
                  var path = new diagram.Path({
                    data: options.dataItem.common1,
                    width: $scope.vrfsLength * 55,
                    height: $scope.vrfsLength * 55,
                    fill: "#33c4d7",
                  });
                } else {
                  var path = new diagram.Path({
                    data: options.dataItem.path,
                    width: $scope.vrfsLength * 55,
                    height: $scope.vrfsLength * 55,
                    fill: $scope.setColor(),
                  });
                }
              }

              // position the grey portion of icon
              if (options.type == 'vrf') {
                if (($rootScope.tenantId !== 1) &&
                  (options.dataItem.fullName && options.dataItem.fullName.indexOf("Common") == 0)) {
                  var path2 = new diagram.Path({
                    data: options.dataItem.common2,
                    width: $scope.vrfsLength * 28,
                    height: $scope.vrfsLength * 12,
                    fill: "#FFFFFF",
                    stroke: {
                      color: "#FFFFFF"
                    },
                    x: 8,
                    y: 3
                  });
                } else {
                  var path2 = new diagram.Path({
                    data: options.dataItem.path2,
                    width: $scope.vrfsLength * 26,
                    height: $scope.vrfsLength * 8,
                    fill: "#070808",
                    x: 0,
                    y: 0
                  });
                  var path3 = new diagram.Path({
                    data: options.dataItem.path3,
                    width: $scope.vrfsLength * 28,
                    height: $scope.vrfsLength * 18,
                    fill: "#49494a",
                    x: 0,
                    y: 3
                  });
                }
              } else if (options.type == 'bd') {
                var path2 = new diagram.Path({
                  data: options.dataItem.path2,
                  width: $scope.vrfsLength * 20,
                  height: $scope.vrfsLength * 10,
                  fill: "#070808",

                  x: -3,
                  y: -4
                });
                if (options.dataItem.noOfInstances > 1) {

                  var bubble = new diagram.Path({
                    data: options.dataItem.bubble,
                    width: 34,
                    content: {
                      align: "middle"
                    },
                    height: 34,
                    x: 40,
                    y: -8,
                    fill: {
                      color: "#0C93C5",
                    }
                  });
                }
                if (($rootScope.tenantId !== 1) &&
                  (options.dataItem.fullName && options.dataItem.fullName.indexOf("Common") == 0)) {
                  var path3 = new diagram.Path({
                    data: options.dataItem.common,
                    width: $scope.vrfsLength * 25,
                    height: $scope.vrfsLength * 14,
                    fill: "#4b4a4b",
                    x: 2,
                    y: 1
                  });
                } else {
                  var path3 = new diagram.Path({
                    data: options.dataItem.path3,
                    width: $scope.vrfsLength * 25,
                    height: $scope.vrfsLength * 10,
                    fill: "#49494a",
                    x: 5,
                    y: -7
                  });
                }

              } else if (options.type == 'l3out') {
                var path2 = new diagram.Path({
                  data: options.dataItem.path2,
                  width: $scope.vrfsLength * 18,
                  height: $scope.vrfsLength * 10,
                  fill: "#070808",
                  x: -8,
                  y: -4
                });
                if (($rootScope.tenantId !== 1) &&
                  (options.dataItem.fullName && options.dataItem.fullName.indexOf("Common") == 0)) {
                  var path3 = new diagram.Path({
                    data: options.dataItem.common,
                    width: $scope.vrfsLength * 14,
                    height: $scope.vrfsLength * 14,
                    fill: "#4b4a4b",
                    x: 3,
                    y: 3
                  });
                } else {}
                var path3 = new diagram.Path({
                  data: options.dataItem.path3,
                  width: $scope.vrfsLength * 14,
                  height: $scope.vrfsLength * 14,
                  fill: "#49494a",
                  x: 3,
                  y: 3
                });
              } else if (options.type == 'shared') {
                var path2 = new diagram.Path({
                  data: options.dataItem.path2,
                  width: $scope.vrfsLength * 32,
                  height: $scope.vrfsLength * 34,
                  fill: "#070808",
                  x: 3,
                  y: 0
                });
                var path3 = new diagram.Path({
                  data: options.dataItem.path3,
                  width: $scope.vrfsLength * 12,
                  height: $scope.vrfsLength * 8,
                  fill: "#49494a",
                  x: 2,
                  y: -4
                });
              }

              function getOffset() {
                var offset;
                if (options.dataItem.noOfInstances) {
                  if (options.dataItem.noOfInstances.toString().length == 1) {
                    offset = 53;
                  } else if (options.dataItem.noOfInstances.toString().length == 2) {
                    offset = 48;
                  } else {
                    offset = 44;
                  }
                }
                return offset;
              }

              var bbox = path.drawingElement.bbox();
              /*if ((options.type == "vrf")&&($rootScope.tenantId !== 1) &&
                (options.dataItem.fullName && options.dataItem.fullName.startsWith("Common"))){
  
            } else */
              {
                var tb = new diagram.TextBlock({
                  autoSize: false,
                  width: 100,
                  fontFamily: "Helvetica",
                  color: "#070808",
                  height: 10,
                  text: options.dataItem.name,
                  fontSize: 15,
                  x: 13,
                  y: $scope.vrfsLength * 55
                });
                g.append(tb);
              }

              if ((options.type == "bd") && (options.dataItem.noOfInstances > 1)) {
                var instances = new diagram.TextBlock({
                  autoSize: false,
                  width: 15,
                  fontFamily: "Helvetica",
                  height: 20,
                  text: options.dataItem.noOfInstances,
                  fontSize: 17,
                  fill: "#FFFFFF",
                  stroke: {
                    color: "white"
                  },
                  x: getOffset(),
                  y: 0,
                  //dx:-10,
                });
              }
              g.append(new diagram.Rectangle({
                width: $scope.vrfsLength * 100,
                height: 33 * $scope.vrfsLength,
                stroke: {
                  width: 0
                }
              }));
              g.append(path);
              g.append(path2);
              if (path3) {
                g.append(path3);
              }
              if (bubble) {
                g.append(bubble);
              }
              if (instances) {
                g.append(instances);
              }

              return g;
            }
          },
          select: function(e) {
            if (e.selected.length) {
              selected = e.selected;
              var element = e.selected[0];
              if (element instanceof Shape) {
                onSelect(fabricDiagram, element);
                // TODO : Fix this later
                //updateShapeProperties(element.options);
              } else {
                // TODO : Fix this later
                //updateConnectionProperties(element.options);
              }
            }
          },
          connectionDefaults: {
            type: "polyline",
            editable: false,
            stroke: {
              color: "#979797",
              editable: false,
              width: 2
            }
          }
        });

        function hasVrf() {
          var status = false;
          var shapes = fabricDiagram.shapes;
          for (var idx = 0; idx < shapes.length; idx++) {
            if (shapes[idx].dataItem.type == "vrf") {
              status = true;
              break;
            }
          }
          return status;
        }

        var fabricDiagram = $("#fabricDiagram").getAciCustomEditorDiagram();
        $scope.fabricDiagram = fabricDiagram;
        //  fabricDiagram.bringIntoView(fabricDiagram.shapes, {align: "middle"});
        $("#fabricDiagram").aciDropTarget({
          filter: '#fabricDiagram',
          drop: function(e) {
            var item, pos, transformed;
            if (e.draggable.hint) {
              item = e.draggable.hint.data("shape");
              pos = e.draggable.hintOffset;
              pos = new Point(pos.left, pos.top);
              if (jQuery.inArray(item.type, $scope.fabricShapes) < 0) {
                return false;
              }
              if (!hasVrf() && item.type != "vrf") {
                return false;
              }
              var transformed = fabricDiagram.documentToModel(pos);
              item.x = transformed.x;
              item.y = transformed.y;
              var newItem = fabricDiagram.dataSource.add(item);
              fabricDiagram.layout({});
              //  fabricDiagram.bringIntoView(fabricDiagram.shapes,{align : "top"});
            }
          }
        });


        function updateShapeProperties(shape) {
          $("#shapeBackgroundColorPicker").getAciColorPicker().value(aci.parseColor(shape.background));
          $("#shapeStrokeColorPicker").getAciColorPicker().value(aci.parseColor(shape.stroke.color));
          $("#shapeStrokeWidth").getAciNumericTextBox().value(shape.stroke.width);
          $("#shapeWidth").getAciNumericTextBox().value(shape.width);
          $("#shapeHeight").getAciNumericTextBox().value(shape.height);
          $("#shapePositionX").getAciNumericTextBox().value(shape.x);
          $("#shapePositionY").getAciNumericTextBox().value(shape.y);
        }

        function updateConnectionProperties(shape) {
          $("#connectionStartCap").getAciDropDownList().value(shape.startCap);
          $("#connectionEndCap").getAciDropDownList().value(shape.endCap);
        }

        /*
            $("#shapesPanelBar").aciPanelBar({
                expandMode: "multiple", 
            }).getAciPanelBar().expand(">li", true);
*/


        // $("#shapesPanelBarApp").aciPanelBar({
        //     expandMode: "multiple",
        // }).getAciPanelBar().expand(">li", true);

        // $("#shapesPanelBarFabric").aciPanelBar({
        //     expandMode: "multiple",
        // }).getAciPanelBar().expand(">li", true);

        function updateSliderIndicator(e) {
          $("#diagramZoomIndicator").attr("value", e.value);

          appDiagram.zoom(e.value / 100);
        }

        $("#diagramZoom").aciSlider({
          min: 10,
          max: 200,
          value: 100,
          smallStep: 10,
          largeStep: 50,
          tickPlacement: "none",
          showButtons: false,
          change: updateSliderIndicator,
          slide: updateSliderIndicator
        });

        $(".numeric").aciNumericTextBox();


        $("#shapesPanelBar .shapeItem").aciDraggable({
          hint: function() {
            return this.element.clone();
          },
          drag: function(e) {
            if ((this.element[0].dataset.type == "bd") || (this.element[0].dataset.type == "l3out")) {
              if (e.screenX > $scope.fabricX) {
                setInterval($scope.getnearestvrf(e), 1000);
              } else {
                if ($scope.nearestvrf != null) {
                  $scope.nearestVrfId = null;
                  $scope.nearestvrf.redrawVisual();
                }
              }
            } else if ((this.element[0].dataset.type == "contract") ||
              (this.element[0].dataset.type == "contractl3") ||
              (this.element[0].dataset.type == "contractshared")) {
              if ((e.screenX < $scope.fabricX) || (e.screenX > $scope.appX)) {
                setInterval($scope.getNearestEpg(e), 1000);
              } else {
                if ($scope.nearestEpg != null) {
                  $scope.nearestEpgId = null;
                  $scope.nearestEpg.redrawVisual();
                }
              }
            }
          },
          dragend: function(e) {
            if (e.screenX < $scope.appX) {
              if ($scope.nearestEpg != null) {
                $scope.nearestEpgId = null;
                $scope.nearestEpg.redrawVisual();
              }
            }
          }
        });

        $("#shapesPanelBar .toItem").aciDraggable({
          hint: function() {
            return this.element.clone();
          }

        });

        $("#shapesPanelBarApp .shapeItem").aciDraggable({
          hint: function() {
            return this.element.clone();
          },
          drag: function(e) {
            if ((this.element[0].dataset.type == "bd") || (this.element[0].dataset.type == "l3out")) {
              if (e.screenX > $scope.fabricX) {
                setInterval($scope.getnearestvrf(e), 3000);
              } else {
                if ($scope.nearestvrf != null) {
                  $scope.nearestVrfId = null;
                  $scope.nearestvrf.redrawVisual();
                }
              }
            } else if ((this.element[0].dataset.type == "contract") ||
              (this.element[0].dataset.type == "contractl3") ||
              (this.element[0].dataset.type == "contractshared")) {
              if (((e.screenX < $scope.fabricX) || (e.screenX > $scope.appX)) && (e.screenY > ($scope.appY + 60))) {
                setInterval($scope.getNearestEpg(e), 1000);
              } else {
                if ($scope.nearestEpg != null) {
                  $scope.nearestEpgId = null;
                  $scope.nearestEpg.redrawVisual();
                }
              }
            }
          },
          dragend: function(e) {
            if (e.screenX < $scope.appX) {
              if ($scope.nearestEpg != null) {
                $scope.nearestEpgId = null;
                $scope.nearestEpg.redrawVisual();
              }
            }
          }
        });

        $("#shapesPanelBarApp .toItem").aciDraggable({
          hint: function() {
            return this.element.clone();
          }

        });


        $("#shapesPanelBarFabric .shapeItem").aciDraggable({
          hint: function() {
            return this.element.clone();
          },
          drag: function(e) {
            if ((this.element[0].dataset.type == "bd") || (this.element[0].dataset.type == "l3out")) {
              if ((e.screenX > $scope.fabricX) && (e.screenY > ($scope.fabricY + 60))) {
                setInterval($scope.getnearestvrf(e), 3000);
              } else {
                if ($scope.nearestvrf != null) {
                  $scope.nearestVrfId = null;
                  $scope.nearestvrf.redrawVisual();
                }
              }
            } else if ((this.element[0].dataset.type == "contract") ||
              (this.element[0].dataset.type == "contractl3")) {
              if ((e.screenX < $scope.fabricX) || (e.screenX > $scope.appX)) {
                setInterval($scope.getNearestEpg(e), 1000);
              } else {
                if ($scope.nearestEpg != null) {
                  $scope.nearestEpgId = null;
                  $scope.nearestEpg.redrawVisual();
                }
              }
            }
          },
          dragend: function(e) {
            if (e.screenX < $scope.appX) {
              if ($scope.nearestEpg != null) {
                $scope.nearestEpgId = null;
                $scope.nearestEpg.redrawVisual();
              }
            }
          }
        });

        $("#shapesPanelBarFabric .toItem").aciDraggable({
          hint: function() {
            return this.element.clone();
          }

        });

        function findShapeByUid(item) {
          var shapes = appDiagram.shapes;
          for (var idx = 0; idx < shapes.length; idx++) {
            if (shapes[idx].dataItem.uid == item.uid) {
              return shapes[idx];
            }
          }
        }

        function findShapeByPKey(diagram, pKey) {
          var shapes = diagram.shapes;
          for (var idx = 0; idx < shapes.length; idx++) {
            if (shapes[idx].dataItem.pKey == pKey) {
              return shapes[idx];
            }
          }
        }

        function findShapeById(diagram, id) {
          var shapes = diagram.shapes;
          for (var idx = 0; idx < shapes.length; idx++) {
            if (shapes[idx].dataItem.id == id) {
              return shapes[idx];
            }
          }
        }

        function findFabricShapeById(diagram, id) {
          var shapes = diagram.shapes;
          for (var idx = 0; idx < shapes.length; idx++) {
            if (shapes[idx].dataItem.id == id) {
              return shapes[idx];
            }
          }
        }

        function findFromConnectionsforUid(uid) {
          var connections = appDiagram.connections;
          for (var idx = 0; idx < connections.length; idx++) {
            if (connections[idx].dataItem.from == uid) {
              return connections[idx];
            }
          }
        }

        function findToConnectionsforUid(uid) {
          var connections = appDiagram.connections;
          for (var idx = 0; idx < connections.length; idx++) {
            if (connections[idx].dataItem.to == uid) {
              return connections[idx];
            }
          }
        }

        $scope.nearestvrf = null;
        $scope.getnearestvrf = function(e) {
          var shapes = fabricDiagram.shapes;
          var min_dist = 999999;
          var nearest = null;
          $scope.nearestVrfId = -1;

          var zoomlevel = $scope.fabricDiagram.zoom();

          shapes.forEach(function(shape) {
            if (shape.dataItem.type == "vrf") {
              var a = ((shape.options.x * zoomlevel) + $scope.fabricX) - e.pageX;
              var b = ((shape.options.y * zoomlevel) + $scope.fabricY) - e.pageY;
              var c = a * a + b * b;
              if (c < min_dist) {
                min_dist = c;
                nearest = shape;
              }
            }

          });
          if (nearest != null) {
            $scope.nearestVrfId = nearest.dataItem.id;
          }
          if ($scope.nearestvrf != null && (nearest.dataItem.id != $scope.nearestvrf.dataItem.id)) {
            $scope.nearestvrf.redrawVisual();

          }
          $scope.nearestvrf = nearest;
          $scope.nearestvrf.redrawVisual();
        }

        $scope.nearestEpg = null;
        $scope.getNearestEpg = function(e) {
          var shapes = appDiagram.shapes;
          var min_dist = 999999;
          var nearestEpg = null;
          $scope.nearestEpgId = -1;

          shapes.forEach(function(shape) {
            if (shape.dataItem.type == "epg") {
              var a = (shape.options.x + $scope.appX) - e.screenX;
              var b = (shape.options.y + $scope.appY) - e.screenY;
              var c = a * a + b * b;
              if (c < min_dist) {
                min_dist = c;
                nearestEpg = shape;
              }
            }

          });
          if (nearestEpg != null) {
            $scope.nearestEpgId = nearestEpg.dataItem.pKey;
          }
          if ($scope.nearestEpg != null && (nearestEpg.dataItem.pKey != $scope.nearestEpg.dataItem.pKey)) {
            $scope.nearestEpg.redrawVisual();
          }
          $scope.nearestEpg = nearestEpg;
          $scope.nearestEpg.redrawVisual();
        }

        $("#appDiagram").aciDropTarget({
          drop: function(e) {
            var item, pos, transformed;
            if (e.draggable.hint) {
              item = e.draggable.hint.data("shape");
              $scope.epgCountContract = 0;
              if (item.type == "contract") {
                for (var i = 0; i < appDiagram.shapes.length; i++) {
                  if (appDiagram.shapes[i].type == "epg") {
                    $scope.epgCountContract++;
                  }
                }
              }
              // console.log($scope.epgCountContract);
              if ($scope.epgCountContract == 1) {
                if ($scope.nearestEpg != null) {
                  $scope.nearestEpgId = null;
                  $scope.nearestEpg.redrawVisual();
                }
                return false;
              }
              if (appDiagram.dataSource.total() < 1) {
                return false;
              }
              if (jQuery.inArray(item.type, $scope.appShapes) < 0) {
                return false;
              }

              pos = e.draggable.hintOffset;
              pos = new Point(pos.left, pos.top);
              var transformed = appDiagram.documentToModel(pos);
              $scope.selectedEpg = $scope.nearestEpg;
              var pos = $scope.getNewItemPosition(item);
              item.x = pos;

              if (item.type == "epg") {
                item.y = $scope.verticalSeperation;
              } else {
                item.y = 330;
              }
              var newItem = appDiagram.dataSource.add(item);
              appDiagram.dataSource.sync();
            }
          }
        });

      }

      $rootScope.diagramSelect = function(e) {
        console.log(arguments);
      }

      $rootScope.selectedTenantIndex = 0;

      $scope.updateTenantList = function() {
        logicService.getTenants(function(resp) {
          $rootScope.tenantList = resp;
          $scope.makeTenantCarousel();
          $scope.createTenantListDropDown();
          if ($rootScope.selectedTenantIndex != 0) {
            $scope.tenantSlider.goToSlide($rootScope.selectedTenantIndex - 1);
          } else {
            $scope.tenantSlider.goToSlide($scope.prevTenantIndex);
          }
        })
      };

      $scope.tenantSlider = {};
      $scope.numSlidesToDisplay = 6;
      $scope.tenantsControls = false;
      $rootScope.tenantId = 0;
      $rootScope.appId = 0;
      $rootScope.selectedTenantIndex = 0;

      $scope.getTenants = function(isNewTenantAdded) {
        logicService.getTenants(function(resp) {
          $rootScope.tenantList = resp;
          if(isNewTenantAdded){
            $rootScope.selectedTenantIndex = $rootScope.tenantList.length - 1;
          }
          $scope.makeTenantCarousel();
          console.log("===============");
          console.log("getTenants");
          console.log(resp);
          if ($rootScope.selectedTenantIndex != 0) {
            if ($scope.firstTenantDeleted == true) {
              $scope.tenantSlider.goToSlide($rootScope.selectedTenantIndex - 1);
              $scope.displayedTenant = $rootScope.selectedTenantIndex - 1;
              $scope.firstTenantDeleted = false;
            } else {
              $scope.tenantSlider.goToSlide($rootScope.selectedTenantIndex);
              $scope.displayedTenant = $rootScope.selectedTenantIndex;
            }
          }
          if (($rootScope.selectedTenantIndex == 0) && ($rootScope.tenantList.length > 1)) {
            $rootScope.selectedTenantIndex = 1;
            $scope.displayedTenant = $rootScope.selectedTenantIndex;
          }
          $scope.createTenantListDropDown();
          $scope.getApplicationsForTenant();
          $scope.loadFabric();
        })
      };


      $scope.getTenantIndex = function(name) {
        var index = -1;
        for (var i = 0; i < $rootScope.tenantList.length; ++i) {
          if (($rootScope.tenantList[i].displayName) == name) {
            index = i;
          }
        }
        return index;
      }

      $scope.createTenantListDropDown = function() {
        // adding the tenant list in the template with angular modal 'project-details.html'
        return;
        var list = document.getElementsByClassName("tenantDropDown");
        var len = list.length;
        for (var i = 0; i < len; ++i) {
          list[0].remove();
        }
        var list = document.getElementsByClassName("divider");
        var len = list.length;
        for (var i = 0; i < len; ++i) {
          list[0].remove();
        }
        //  $("#tenantDivider").remove();
        var appDrop = document.getElementById("tenantDropDownList");
        for (var i = 1; i < $rootScope.tenantList.length; ++i)
        //Create the new dropdown menu        
        {

          //  if ((i != $rootScope.selectedTenantIndex) && (i != $scope.displayedTenant)){
          var appListDropDown = document.createElement("li");
          appListDropDown.className = "tenantDropDown";
          var label = "<a>" + $rootScope.tenantList[i].displayName + "</a>";
          appListDropDown.innerHTML = label;
          appDrop.appendChild(appListDropDown);
          if (i < ($rootScope.tenantList.length - 1)) {
            var divider = document.createElement("li");
            divider.className = "divider";
            divider.id = "tenantDivider"
            appDrop.appendChild(divider);
          }
          //}           
        }
      }

      $scope.makeTenantCarousel = function() {

     
        console.log($("#content-slider"))
        $scope.tenantSlider = $("#content-slider").lightSlider({
          item: 1,
          loop: false,
          slideMargin: 15,
          speed: 100,
          enableDrag: false,
          controls: false,
          adaptiveHeight: false,
          gallery: true,
          thumbItem: 0,

        });

      }


      $scope.arrowSelected = false;
      $scope.showPrevTenant = function() {
        $scope.arrowSelected = true;
        if ($rootScope.tenantList.length > 2) {
          if (($rootScope.selectedTenantIndex != 0) && ($rootScope.selectedTenantIndex - 1 != 0)) {
            var index = $rootScope.selectedTenantIndex - 1;
            $scope.changeSelectedTenant($rootScope.selectedTenantIndex - 1);
            $scope.tenantSlider.goToSlide($rootScope.selectedTenantIndex - 1);
          }
        }

      }


      $scope.showNextTenant = function() {
        $scope.arrowSelected = true;
        if ($rootScope.selectedTenantIndex < $rootScope.tenantList.length - 1) {
          $scope.changeSelectedTenant($rootScope.selectedTenantIndex + 1);
        }
        if ($rootScope.selectedTenantIndex != 0) {
          $scope.tenantSlider.goToNextSlide();
        }
        //$scope.arrowSelected = false;
      }

      $scope.changeTenantInstancesField = function() {
        var oTby1 = document.getElementsByName('tenantInstances');
        for (var i = 0; i < oTby1.length; ++i) {
          if (i == $rootScope.selectedTenantIndex) {
            oTby1[i].className = 'instancesSelected';
          } else {
            oTby1[i].className = 'tenantInstances';
          }
        }
      }

      $scope.editButtonsVisbible = false;
      $scope.changeSelectedTenant = function(index) { 
        $scope.currentZoomVal = 0.7;
        if ((!$scope.editInstancesClicked) && (!$scope.delTenantWindowVisible)) {
          if (($rootScope.selectedTenantIndex == 0) && (index != 0)) {
            //index = index-1;
            $scope.prevTenantIndex = -1;
          }
          if ((index == 0) && ($rootScope.selectedTenantIndex > 0)) {
            $scope.prevTenantIndex = $rootScope.selectedTenantIndex;
          }
          if (index != $scope.editTenantIndex) {
            $scope.editTenantIndex = -1;
            if ($scope.editButtonsVisbible == true) {
              $scope.updateTenantList();
            }
            $scope.editButtonsVisbible = false;
          }
          if (index != $rootScope.selectedTenantIndex) { 
            $rootScope.selectedTenantIndex = index;
            $rootScope.tenantId = $rootScope.tenantList[$rootScope.selectedTenantIndex].id;
            $rootScope.selectedApplicationIndex = 0;
            $scope.getApplicationsForTenant();
            $scope.loadFabric();
            $scope.tenantSlider.goToSlide($rootScope.selectedTenantIndex-1);
          }

          $scope.createTenantListDropDown();
        } else {
          $scope.editInstancesClicked = false;
          $scope.delTenantWindowVisible = false;
        }

      }

      $scope.editTenantIndex = -1;

      $scope.editTenant = function(index, count) {
        $scope.editTenantIndex = index;
        $scope.editButtonsVisbible = true;
        $scope.updatedInstanceCount = count;
      }

      $scope.editInstancesClicked = false;
      $scope.tenantInstancesClicked = function() {
        $scope.editInstancesClicked = true;
      }

      $scope.moreButtonDisabled = false;

      $scope.showLoadingImage = function() {
        $("#summaryView").addClass("loading");
        $scope.moreButtonDisabled = true;
      }

      $scope.removeLoadingImage = function() {
        $("#summaryView").removeClass("loading");
        $scope.moreButtonDisabled = false;
      }

      $scope.updateTenant = function(newNameOfTenant) {
        // if user did not change the tenant name then simply close edit window without call to service;
        if($rootScope.tenantList[$rootScope.selectedTenantIndex].displayName === newNameOfTenant){
          $("#tenanteditwindow").data("aciWindow").close(); $('#lightBox').hide();
          return;
        }

        var jsonObj = {
          "displayName" : newNameOfTenant
        };

        logicService.updateTenant(jsonObj, function(resp) {
          $rootScope.canceler.resolve();
          $rootScope.tenantList[$rootScope.selectedTenantIndex].displayName = resp.displayName;
          $("#tenanteditwindow").data("aciWindow").close();
          $('#lightBox').hide();
          $scope.showLoadingImage();
          logicService.getSizingResults(function(resp) {
            $rootScope.currProject = resp;
            $scope.removeLoadingImage();
          });
        })
        $scope.editTenantIndex = -1;
        $scope.editButtonsVisbible = false;
        
      }

      $scope.cancelUpdateTenant = function() {
        $scope.editTenantIndex = -1;
        $scope.updateTenantList();
        $scope.editButtonsVisbible = false;
        $("#tenanteditwindow").data("aciWindow").close();
        $('#lightBox').hide();
      }

      $scope.appSlider = {};
      $scope.numAppSlidesToDisplay = 5;
      $scope.appControls = false;
      $rootScope.applicationList = {};

      $scope.getApplicationsForTenant = function() {
        $rootScope.tenantId = $rootScope.tenantList[$rootScope.selectedTenantIndex].id;

        logicService.getApplications(function(resp) {
          $rootScope.applicationList = resp;
          //  $scope.makeApplicationCarousel();
          $scope.changeSelectedApplication($rootScope.selectedApplicationIndex);

        })
      };

      $scope.makeApplicationCarousel = function() {

        $scope.appSlider = $("#application-slider").lightSlider({
          item: $scope.numAppSlidesToDisplay,
          adaptiveHeight: false,
          loop: false,
          enableDrag: false,
          controls: false,
          gallery: false,
          pager: false,
          autoWidth: false,
        });

        if ($rootScope.applicationList.length > $scope.numAppSlidesToDisplay) {
          $scope.appControls = true;
        } else {
          $scope.appControls = false;
        }
      }

      $scope.showPrevApplication = function() {
        $scope.appSlider.goToPrevSlide();
      }

      $scope.showNextApplication = function() {
        $scope.appSlider.goToNextSlide();
      }

      $rootScope.selectedApplicationIndex = 0;
      $scope.deleteIndex = -1;

      $scope.changeSelectedApplication = function(index) {
        $scope.currentZoomVal = 0.7;
        $rootScope.selectedApplicationIndex = index;
        if ($rootScope.applicationList.length > 0) {
          $rootScope.appId = $rootScope.applicationList[$rootScope.selectedApplicationIndex].id;
        } else {
          $rootScope.appId = 0;
        }
        // If App is deleted, pressing on 'X' mark also invokes this func. In that case dont call loadApplication 
        if (($rootScope.applicationList.length > 0) && (index != $scope.deleteIndex)) {
          if ($scope.applicationAdded == true) {
            $scope.loadFabric();
          }
          $scope.loadApplication();
        } else {
          // Should not call nodeCollection api when no apps
          $scope.appDiagram.dataSource.data([]);
          $scope.appDiagram.clear();

        }
        if ($rootScope.applicationList.length > 0) {
          $scope.createApplicationListDropDown();
        }

      }

      $scope.appWindowVisible = false;
      $scope.addAppMaxNumInstances = DIALOG_CONSTANTS.application.maxNumInstances;
      $scope.addAppMinNumInstances = DIALOG_CONSTANTS.application.minNumInstances;

      $scope.openApplicationWindow = function() {

        var window = $("#appwindow");
        $("#addAppInstances").aciNumericTextBox({ format: 'n0' });
        $("#addAppInstances").getAciNumericTextBox().value(DIALOG_CONSTANTS.application.numInstances);
        $scope.addAppForm = {};
        $scope.addAppForm['numInstances'] = DIALOG_CONSTANTS.application.numInstances;

        var log = [
          { "text": "NoTemplate", "value": "NoTemplate" },
          { "text": "Flat", "value": "Flat" },
          { "text": "2Tier", "value": "2Tier" },
          { "text": "3Tier", "value": "3Tier" }
        ];

        $("#appmodel").aciComboBox({
          dataTextField: "text",
          dataValueField: "value",
          dataSource: log,
          filter: "contains",
          suggest: true
        });


        var onClose = function() {}

        if (!window.data("aciWindow")) {
          window.aciWindow({
            width: "300px",
            title: "Add Application",
            actions: [
              "Close"
            ],
            close: onClose
          });
        }

        window.data("aciWindow").open();
        window.data("aciWindow").center();
      }

      $scope.validateAddApplication = function(event) {
        event.preventDefault();
        // Needs to be changed
        var project_id = 1;
        var tenant_id = 1;

        var model = $("#appmodel").aciComboBox();
        var addAppJson = {
          "name": $scope.addAppForm['name'],
          "instances": $scope.addAppForm['numInstances'],
          "model": model[0].value,
          "templateType": "app"
        };
        // Pass the initial x and y coordinates for application node
        addAppJson.uiData = {
          "x": 50,
          "y": 0
        };
        addAppJson.configuration = {
          "l3outComplexity": "High",
          "sharedServiceEnabled": true,
          "contractComplexity": "Medium",
          "subnetPolicy": "Default",
          "epgComplexity": "Medium",
          "enableL3out": true,
          "bdPolicy": "Default"
        };

        $scope.addApplication(JSON.stringify(addAppJson));
        $("#appwindow").data("aciWindow").close();
      }

      $scope.onCanceladdApp = function() {
        $("#appwindow").data("aciWindow").close();
      }

      $scope.addApplication = function(jsonObj) {
        logicService.addApplication(jsonObj, function(resp) {
          $rootScope.canceler.resolve();
          $scope.showLoadingImage();
          logicService.getSizingResults(function(resp) {
            $rootScope.currProject = resp;
            $scope.removeLoadingImage();
          });
          $rootScope.applicationList = resp;
          $rootScope.tenantId = $rootScope.tenantList[$rootScope.selectedTenantIndex].id;
          $scope.applicationAdded = true;
          $scope.updateTenantList();
          //retrieve the app list and make newly created app as selected
          logicService.getApplications(function(resp) {
            $rootScope.applicationList = resp;
            $scope.makeApplicationCarousel();
            $rootScope.selectedApplicationIndex = $rootScope.applicationList.length - 1;
            $scope.changeSelectedApplication($rootScope.applicationList.length - 1);

          })
        })
      };

      $scope.getApplicationIndex = function(name) {
        var index = -1;
        for (var i = 0; i < $rootScope.applicationList.length; ++i) {
          if (($rootScope.applicationList[i].name) == name) {
            index = i;
          }
        }
        return index;
      }

      // Manohar ::  Not using below function as we implemented it in angular way
      $scope.createApplicationListDropDown = function() {return;

        $scope.applicationDisplayName = $scope.applicationList[$scope.selectedApplicationIndex].name + " (" + $scope.applicationList[$scope.selectedApplicationIndex].noOfInstances + ")";
        var list = document.getElementsByClassName("dropDrownClass");
        var len = list.length;
        for (var i = 0; i < len; ++i) {
          list[0].remove();
        }

        $("#appDivider").remove();

        var appDrop = document.getElementById("appDropDownList");
        for (var i = 0; i < $rootScope.applicationList.length; ++i)
        //Create the new dropdown menu        
        {
          if (i != $rootScope.selectedApplicationIndex) {
            var appListDropDown = document.createElement("li");
            appListDropDown.className = "dropDrownClass";
            appListDropDown.value = i;
            var label = "<div class=\"appName\">" + $rootScope.applicationList[i].name + " (" + $rootScope.applicationList[i].noOfInstances + ")" + "</div>";
            var line = "<div class=\"vertical-line\" style=\"margin-top:-26px;margin-left:58px;\"></div>";
            var imageTag = "<div id=\"appclose\" style=\"float:right;\" onclick=\"$scope.clickList();\";>";
            var content = "&#10006";
            var closingdiv = "</div>";
            appListDropDown.innerHTML = label + line + imageTag + content + closingdiv;
            appDrop.appendChild(appListDropDown);
            if (i < ($rootScope.applicationList.length - 1)) {
              var divider = document.createElement("li");
              divider.className = "divider";
              divider.id = "appDivider";
              appDrop.appendChild(divider);
            }
          }
        }
      }


      $scope.tenantWindowVisible = false;
      $scope.addTenantMaxNumInstances = DIALOG_CONSTANTS.tenant.maxNumInstances;
      $scope.addTenantMinNumInstances = DIALOG_CONSTANTS.tenant.minNumInstances;

      $scope.openTenantWindow = function() {

        var window = $("#tenantwindow");
        $("#addTenantInstances").aciNumericTextBox({ format: 'n0' });
        $scope.tenant = {};
        $('#lightBox').show();
        var onClose = function() {}

        if (!window.data("aciWindow")) {
          window.aciWindow({
            width: "375px",
            title: "Add Tenant",
            actions: [
              "Close"
            ],
            close: onClose
          });
        }
        $('.a-window-action').click(function(){$('#lightBox').hide();});
        window.data("aciWindow").open();
        window.data("aciWindow").center();

        // Default values
        logicService.getTenantDefaultValues(function(resp) {
          $("#addTenantInstances").getAciNumericTextBox().value(resp.count);
          $scope.tenant['type'] = resp.type;
          $scope.tenant['displayName'] = resp.displayName;
        })
        $scope.tenant['l3OutSlider'] = "2";
        $scope.tenant['localVrf'] = "1";
        $scope.tenant['localL3Out'] = "1";
      }

  
      $scope.openEditTenantWindow = function(tenant, index) {


        var _indx = getIndexByProp($rootScope.tenantList, 'id', tenant.id);

        if(_indx !== -1){
          $rootScope.selectedTenantIndex = _indx;  
        }else{
          alert('Something went wrong!')
        }

        var window = $("#tenanteditwindow");
        $rootScope.tenantId = $rootScope.tenantList[$rootScope.selectedTenantIndex].id;
        $scope.tenantDisplayNameToEdit = $rootScope.tenantList[$rootScope.selectedTenantIndex].displayName;
        //this is to bind/update the UI with scope data
        $compile(window.contents())($scope);
        

        var onClose = function() {}
        $('#lightBox').show();
        if (!window.data("aciWindow")) {
          window.aciWindow({
            width: "375px",
            title: "Edit Tenant",
            actions: [
              "Close"
            ],
            close: onClose
          });
        }
        $('.a-window-action').click(function(){$('#lightBox').hide();});
        window.data("aciWindow").open();
        window.data("aciWindow").center();

      }
    

      $scope.disableL3Slider = function() {
        if ($scope.tenant['localL3Out'] == "1") {
          document.getElementById("l3-tenant-slider").disabled = false;
        } else {
          document.getElementById("l3-tenant-slider").disabled = true;
        }
      }

      $scope.validateAddTenant = function(event) {
        event.preventDefault();

        //$scope.templateType = "tenant";
        var count = $("#addTenantInstances").getAciNumericTextBox().value();
        var addTenantJSON = null;
        //var local=$("#tenantModel").is(":checked");
        var l3OutValue = $scope.tenant['l3OutSlider'];
        var l3OutSliderVal = (Math.round(parseFloat(l3OutValue))).toString();
        if (l3OutSliderVal == "1") {
          $scope.tenant['l3OutSlider'] = "Low";
        } else if (l3OutSliderVal == "2") {
          $scope.tenant['l3OutSlider'] = "Medium";
        } else {
          $scope.tenant['l3OutSlider'] = "High";
        }

        if ($scope.tenant['localVrf'] == "1") {
          $scope.tenant['localVrf'] = true;
        } else {
          $scope.tenant['localVrf'] = false;
        }

        if ($scope.tenant['localL3Out'] == "1") {
          $scope.tenant['localL3Out'] = true;
        } else {
          $scope.tenant['localL3Out'] = false;
        }



        addTenantJSON = {
          "displayName": $scope.tenant['displayName'],
          "type": $scope.tenant['type'],
          "count": count,
          "localVrf": $scope.tenant['localVrf'],
          "localL3out": $scope.tenant['localL3Out'],
          "l3outComplexity": $scope.tenant['l3OutSlider']
        };

        $scope.postTenant(JSON.stringify(addTenantJSON));

        $("#tenantwindow").data("aciWindow").close();
        $('#lightBox').hide();
      }

      $scope.onCanceladdTenant = function(isEditTenantWindow) {
        if(isEditTenantWindow){
          $("#tenanteditwindow").data("aciWindow").close();  
        }else{
          $("#tenantwindow").data("aciWindow").close();  
        }    
        $('#lightBox').hide();    
      }

      $scope.postTenant = function(jsonObj) {
        logicService.addTenant(jsonObj, function(resp) {
          $scope.tenants = resp;
          // $rootScope.selectedTenantIndex = $rootScope.tenantList.length;
          $scope.getTenants(true);
          // $scope.changeSelectedTenant($rootScope.tenantList.length-1);
          $scope.createTenantListDropDown();
          $rootScope.canceler.resolve();
          $scope.showLoadingImage();
          logicService.getSizingResults(function(resp) {
            $rootScope.currProject = resp;
            $scope.removeLoadingImage();
          });
        })
      };

      $scope.delTenantWindowVisible = false;

      $scope.openDelTenantWindow = function(index) {
        $rootScope.selectedTenantIndex = index - 1;
        var window = $("#delTenantWindow");
        $scope.delTenantWindowVisible = true;

        var onClose = function() {}
        $('#lightBox').show();
        if (!window.data("aciWindow")) {
          window.aciWindow({
            width: "300px",
            title: "Delete Tenant",
            actions: [
              "Close"
            ],
            close: onClose
          });
        }
        $('.a-window-action').click(function(){$('#lightBox').hide();});
        $scope.delTenantLabel = DIALOG_CONSTANTS.deleteTenant.label + $rootScope.tenantList[$rootScope.selectedTenantIndex].displayName + "?";

        window.data("aciWindow").open();
        window.data("aciWindow").center();
      }

      $scope.onCanceldelTenant = function() {
        $("#delTenantWindow").data("aciWindow").close();
        $('#lightBox').hide();
      }

      $scope.firstTenantDeleted = false;
      $scope.deleteTenant = function() {
        $rootScope.tenantId = $rootScope.tenantList[$rootScope.selectedTenantIndex].id;
        logicService.deleteTenant(function(resp) {
          $scope.showLoadingImage();
          $rootScope.canceler.resolve();
          logicService.getSizingResults(function(resp) {
            $rootScope.currProject = resp;
            $scope.removeLoadingImage();
          });
          if ($rootScope.selectedTenantIndex < $rootScope.tenantList.length - 1) {
            $scope.firstTenantDeleted = true;
          }
          if (($rootScope.selectedTenantIndex == 1) && ($rootScope.tenantList.length > 2)) {

          } else {
            $rootScope.selectedTenantIndex = $rootScope.selectedTenantIndex - 1;
          }
          /*  if (($rootScope.selectedTenantIndex == 1) && ($rootScope.tenantList.length == 2)) {
          $rootScope.selectedTenantIndex = $rootScope.selectedTenantIndex - 1;
        }*/
          $rootScope.selectedApplicationIndex = 0;
          $scope.getTenants();
        })

        $("#delTenantWindow").data("aciWindow").close();
        $('#lightBox').hide();
      }

      $scope.delAppWindowVisible = false;

      $scope.openDelAppWindow = function(index) {
        $rootScope.selectedApplicationIndex = index;
        var window = $("#delAppWindow");

        var onClose = function() {}

        if (!window.data("aciWindow")) {
          window.aciWindow({
            width: "300px",
            title: "Delete Application",
            actions: [
              "Close"
            ],
            close: onClose
          });
        }

        var label = $('#delAppLabel');
        label.text(DIALOG_CONSTANTS.deleteApplication.label + $rootScope.applicationList[$rootScope.selectedApplicationIndex].name + "?");

        window.data("aciWindow").open();
        window.data("aciWindow").center();
      }

      $scope.onCanceldelApp = function() {
        $("#delAppWindow").data("aciWindow").close();
      }

      $scope.deleteApp = function() {
        $rootScope.appId = $rootScope.applicationList[$rootScope.selectedApplicationIndex].id;
        logicService.deleteApplication(function(resp) {
          $rootScope.canceler.resolve();
          $scope.showLoadingImage();
          logicService.getSizingResults(function(resp) {
            $rootScope.currProject = resp;
            $scope.removeLoadingImage();
          });
          if (($rootScope.selectedApplicationIndex == 0) && ($rootScope.applicationList.length > 1)) {
            $rootScope.selectedApplicationIndex = $rootScope.selectedApplicationIndex + 1;
          }
          $rootScope.selectedApplicationIndex = $rootScope.selectedApplicationIndex - 1;
          $scope.updateTenantList();
          $scope.getApplicationsForTenant();
        })

        $("#delAppWindow").data("aciWindow").close();
      }

      $scope.setTitle = function(type) {
        var title;
        if (type == "vrf") {
          title = "Delete VRF";
        } else if (type == "bd") {
          title = "Delete BD";
        } else if (type == "l3out") {
          title = "Delete L3OUT";
        } else if (type == "shared") {
          title = "Delete Shared Resource";
        } else if (type == "epg") {
          title = "Delete EPG";
        } else if ((type == "contract") || (type == "contractl3") || (type == "contractshared")) {
          title = "Delete Contract";
        }
        return title;
      }

      $scope.deleteNode = null;

      $scope.openDelNodeWindow = function(shape) {
        var window = $("#delNodeWindow");

        var onClose = function() {}

        var title = $scope.setTitle(shape.type);
        if (!window.data("aciWindow")) {
          window.aciWindow({
            width: "300px",
            title: title,
            actions: [
              "Close"
            ],
            close: onClose
          });
        }

        var label = $('#delNodeLabel');
        label.text(DIALOG_CONSTANTS.deleteApplication.label + shape.name + "?");
        $scope.deleteNode = shape;
        window.data("aciWindow").title(title);
        window.data("aciWindow").open();
        window.data("aciWindow").center();
      }

      $scope.onCanceldelNode = function() {
        $("#delNodeWindow").data("aciWindow").close();
      }

      $scope.onDeleteNode = function() {
        if ($scope.deleteNode.type == "vrf") {
          logicService.deleteVrf($scope.deleteNode, function(resp) {
            $rootScope.canceler.resolve();
            $scope.showLoadingImage();
            logicService.getSizingResults(function(resp) {
              $rootScope.currProject = resp;
              $scope.removeLoadingImage();
            });

            $scope.fabricDiagram.dataSource.remove($scope.deleteNode);
            $scope.updateTenantList();
            // TODO: START this is a hack and has to be removed
            $scope.loadFabric();
            // TODO: END this is a hack and has to be removed
          });
        } else if ($scope.deleteNode.type == "bd") {
          logicService.deleteBd($scope.deleteNode, function(resp) {
            $rootScope.canceler.resolve();
            $scope.showLoadingImage();
            logicService.getSizingResults(function(resp) {
              $rootScope.currProject = resp;
              $scope.removeLoadingImage();
            });

            $scope.fabricDiagram.dataSource.remove($scope.deleteNode);
            $scope.updateTenantList();
            // TODO: START this is a hack and has to be removed
            $scope.loadFabric();
            // TODO: END this is a hack and has to be removed
          });
        } else if ($scope.deleteNode.type == "l3out") {
          logicService.deleteL3out($scope.deleteNode, function(resp) {
            $rootScope.canceler.resolve();
            $scope.showLoadingImage();
            logicService.getSizingResults(function(resp) {
              $rootScope.currProject = resp;
              $scope.removeLoadingImage();
            });

            $scope.fabricDiagram.dataSource.remove($scope.deleteNode);
            $scope.updateTenantList();
            // TODO: START this is a hack and has to be removed
            $scope.loadFabric();
            // TODO: END this is a hack and has to be removed
          });
        } else if ($scope.deleteNode.type == "shared") {
          logicService.deleteSharedResource($scope.deleteNode, function(resp) {
            $rootScope.canceler.resolve();
            $scope.showLoadingImage();
            logicService.getSizingResults(function(resp) {
              $rootScope.currProject = resp;
              $scope.removeLoadingImage();
            });

            $scope.fabricDiagram.dataSource.remove($scope.deleteNode);
            $scope.updateTenantList();
            // TODO: START this is a hack and has to be removed
            $scope.loadFabric();
            // TODO: END this is a hack and has to be removed
          });
        } else if ($scope.deleteNode.type == "epg") {
          logicService.deleteEpg($scope.deleteNode, function(resp) {
            $rootScope.canceler.resolve();
            $scope.showLoadingImage();
            logicService.getSizingResults(function(resp) {
              $rootScope.currProject = resp;
              $scope.removeLoadingImage();
            });
            $scope.appDiagram.dataSource.remove($scope.deleteNode);
            $scope.updateTenantList();
            $scope.loadApplication();
            $scope.reAlignTree = true;
          });
        } else if (($scope.deleteNode.type == "contract") ||
          ($scope.deleteNode.type == "contractl3") ||
          ($scope.deleteNode.type == "contractshared")) {
          logicService.deleteContract($scope.deleteNode, function(resp) {
            $rootScope.canceler.resolve();
            $scope.showLoadingImage();
            logicService.getSizingResults(function(resp) {
              $rootScope.currProject = resp;
              $scope.removeLoadingImage();

            });
            //$scope.appDiagram.dataSource.remove($scope.deleteNode);
            $scope.updateTenantList();
            $scope.loadApplication();
            $scope.reAlignTree = true;

          });
        }
        $scope.nodeType = null;
        $("#delNodeWindow").data("aciWindow").close();
      }

      $scope.showSizingResults = false;

      $scope.showExpandedView = function() {
        $scope.showSizingResults = true;
        $scope.moreButtonDisabled = false;
        $scope.getUtilDetails();
      }

      $scope.hideExpandedView = function() {
        $scope.showSizingResults = false;
      }






      /*  Sizer Results screen */

      $scope.sizingSummary = {};
      $scope.resourceGridOptions = {};
      $scope.resourceGridData = {};
      var rowIndex = 0;
      $scope.utilizationDetails = [];
      $scope.popoverTitle = "TOR1 Utilization Details";
      $scope.grid = {};




      $scope.getUtilDetails = function() {

        // Remove the instance count bubble after redirection to this page
        var bubbleNode = document.getElementsByClassName("numberCircle");
        var len = bubbleNode.length;
        for (var i = 0; i < len; ++i) {
          bubbleNode[0].remove();
        }
        // Fix for destroying all the aci-windows each time redirection happens
        var aciWindow = document.getElementsByClassName("ng-scope a-window-content a-content");
        var len = aciWindow.length;
        for (var i = 0; i < len; ++i) {
          aciWindow[0].remove();
        }

        //clear the sizer utilization pop-up
        var popup = document.getElementsByClassName("popover");
        var len = popup.length;
        for (var i = 0; i < len; ++i) {
          popup[0].remove();
        }

        //  if($location.path() == "/results") {
        // $scope.getInputJSON- Added for testing
        //  $scope.getInputJSON();
        $scope.resourceGridDataSource.data([]);
        logicService.getSizingSummary(function(resp) {
            $scope.resourceGridData = resp.resourceGridData;
            $scope.resourceGridDataSource.data(resp.resourceGridData);
          })
          //  }
      };

      $scope.resourceGridDataSource = new aci.data.DataSource({
        data: [],
        pageSize: 8,
        serverPaging: false,
        serverSorting: false,
        serverFiltering: false
      });

      $scope.resourceGridOptions = {
        sortable: true,
        pageable: true,
        selectable: "multiple",
        change: function() {
          var selectedRows = this.select();
          $scope.rowIndex = selectedRows[0].rowIndex;
        },
        columns: [{
            field: "switch",
            title: "Switch",
            width: 70
          }, {
            field: "vrfs",
            title: "VRFs",
            width: 65
          }, {
            field: "bds",
            title: "BDs",
            width: 65
          }, {
            field: "epgs",
            title: "EPGs",
            width: 65
          }, {
            field: "l3Out",
            title: "L3Out",
            width: 65
          },
          /*{
          title: "Utilization",
                    template: function (dataItem) {
                        return '<div style="width: 16px;height:16px;background-color:green;display: inline-block;margin-right: 10px;margin-bottom:2px;"></div><div class="view-icon" ng-click="getPopoverDetails(dataItem);" custom-popover id="popover" popover-title={{popoverTitle}} \
                      popover-html="Some Popover Text" popover-placement="right" \
                      popover-label=""/></div>';
                    }
                }*/
          {
            field: "utilizationDetails[0].displayCount",
            title: "Policy TCAM",
            width: 100
          }, {
            field: "utilizationDetails[1].displayCount",
            title: "VLAN Table",
            width: 100
          }, {
            field: "utilizationDetails[2].displayCount",
            title: "Source Prefix TCAM",
            width: 120
          }, {
            field: "utilizationDetails[3].displayCount",
            title: "Dest Prefix TCAM",
            width: 100
          }, {
            field: "utilizationDetails[4].displayCount",
            title: "Router IP Table",
            width: 100
          }

        ]
      };


      $scope.getPopoverDetails = function(data) {
        $scope.utilizationDetails = data.utilizationDetails;
      };

      $scope.getInputJSON = function() {
        logicService.getInputJSON(function(resp) {
          var data = "text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(resp));
          var a = document.createElement('a');
          a.href = 'data:' + data;
          a.download = 'data.json';
          a.innerHTML = 'Download Input JSON';
          var container = document.getElementById('inputJSon');
          if (container.childNodes.length != 0) {
            container.removeChild(container.childNodes[0]);
          }
          container.appendChild(a);
        })
      };


      $rootScope.showModal = false;
      $scope.appType = "";
      $scope.l3OutComplexity = "Low";

      //$scope.epgType ="Small";
      $scope.uniqueBD = "Unique";
      $scope.toggleFabricShow = "1";
      $scope.uniqueSubnet = "Unique";

      $scope.toggleModal = function() {
        if ($rootScope.tenantList[$rootScope.selectedTenantIndex].type == CONSTANT_FACTORY.getUtility()) {
          $rootScope.fadeOutBackground();
          $rootScope.notification.show({
            title: ERROR_STRING_CONSTANTS.FORBIDDEN_APPLICATION_TITLE,
            message: ERROR_STRING_CONSTANTS.FORBIDDEN_APPLICATION_MESSAGE
          }, "error");
        } else {

          //    $rootScope.fadeOutBackground();
          $("modal.in").prepend("<div id='PopupMaskProject' style='position:fixed;width:100%;height:100%;z-index:11;background-color:gray;'></div>");
          $("#PopupMaskProject").css('opacity', 0.1);
          $("#addAppModal").draggable({
            handle: ".modal-header"
          });
          //document.getElementById("l3-slider").disabled = false;
          $rootScope.showModal = !$rootScope.showModal;
          $rootScope.applicationWindowTitle = "ADD APPLICATION - Type";
          $scope.secPanelScrollHt = 0;
          $rootScope.prevButtonShow = false;
          $rootScope.nextButtonShow = true;
          $rootScope.saveButtonShow = false;
          $rootScope.firstCircleValue = 1;
          $rootScope.secCircleValue = 2;
          $rootScope.thirdCircleValue = 3;
          $('#progressBar').css("width", "0%");
          $('#progressBar').css("background-color", "#e1e1e1");
          $('#secondRound').css("background-color", "#e1e1e1");
          $('#thirdRound').css("background-color", "#e1e1e1");

          $("#addAppInst").aciNumericTextBox({ format: 'n0' });
          $("#addAppInst").getAciNumericTextBox().value(1);

          var log = [];
          log.push({ text: "Blank Application", value: "Blank Application", type: "appType" });
          log.push({ text: "Flat", value: "Flat", type: "appType" });
          log.push({ text: "2-Tier", value: "2-Tier", type: "appType" });
          log.push({ text: "3-Tier", value: "3-Tier", type: "appType" });

          $("#appType").empty();
          $("#appType").aciComboBox({
            dataTextField: "text",
            dataValueField: "value",
            dataSource: log,
            filter: "contains",
            suggest: true
          });
          $scope.epgType = "2";
          $scope.l3OutComplexity = "2";
          $scope.addApp = {};
          //Get default values for application node
          logicService.getNodeDefaultValues("app", function(resp) {
            $scope.addApp = {
              name: resp.name,
              noOfInstances: resp.noOfInstances,
              model: $scope.appType
            }
            $scope.addApp.configuration = {
              sharedServiceEnabled: true,
              l3outEnabled: true,
              l3outComplexity: $scope.l3OutComplexity,
              epgComplexity: $scope.epgType,
              bdPolicy: $scope.uniqueBD,
              subnetPolicy: $scope.uniqueSubnet,
              contractComplexity: "2",
              //noOfServiceInstances: "2"
            };
          });
        }
      };

      $scope.onAppTypeChange = function() {
        var appTypeSelect = $("#appType")[0];
        var appTypeValue = appTypeSelect.options[appTypeSelect.selectedIndex].text;

        if (appTypeValue == "Blank Application") {
          $rootScope.saveButtonShow = true;
          $rootScope.nextButtonShow = false;
          $rootScope.prevButtonShow = false;
        } else {
          $rootScope.saveButtonShow = false;
          $rootScope.nextButtonShow = true;
          $rootScope.prevButtonShow = false;
        }
      }

      $scope.scrollToNext = function() {

        var modalApp = $("#modalAppForm");

        var data = {};
        data.appTypeVal = jQuery('#appType').val();

        if ((data.appTypeVal == "? string: ?") || (data.appTypeVal == null)) {
          $scope.appTypeValid = true;
        } else {
          $scope.secPanelScrollHt = $scope.secPanelScrollHt + 310;
          $scope.appTypeValid = false;
          if ($scope.secPanelScrollHt == 310) {
            $rootScope.applicationWindowTitle = "ADD APPLICATION - Network";
            $rootScope.prevButtonShow = true;
            $rootScope.nextButtonShow = true;
            $rootScope.saveButtonShow = false;
            $rootScope.firstCircleValue = "\u2714";
            $rootScope.secCircleValue = "2";
            $rootScope.thirdCircleValue = "3";
            $('#progressBar').css("width", "50%");
            $('#progressBar').css("background-color", "#7bbdf6");
            $('#secondRound').css("background-color", "#7bbdf6");
          } else if ($scope.secPanelScrollHt == 620) {
            $rootScope.applicationWindowTitle = "ADD APPLICATION - Configure";
            $rootScope.prevButtonShow = true;
            $rootScope.nextButtonShow = false;
            $rootScope.saveButtonShow = true;
            $rootScope.firstCircleValue = "\u2714";
            $rootScope.secCircleValue = "\u2714";
            $rootScope.thirdCircleValue = "3";
            $('#progressBar').css("width", "100%");
            $('#progressBar').css("background-color", "#7bbdf6");
            $('#secondRound').css("background-color", "#7bbdf6");
            $('#thirdRound').css("background-color", "#7bbdf6");
          } else {
            $scope.secPanelScrollHt == 620;
          }

          modalApp.animate({ scrollTop: $scope.secPanelScrollHt }, 800);
        }
      }
      $scope.scrollToPrev = function() {
        var modalApp = $("#modalAppForm");
        $scope.secPanelScrollHt = $scope.secPanelScrollHt - 310;
        if ($scope.secPanelScrollHt == 310) {
          $rootScope.applicationWindowTitle = "ADD APPLICATION - Network";
          $rootScope.prevButtonShow = true;
          $rootScope.nextButtonShow = true;
          $rootScope.saveButtonShow = false;
          $rootScope.firstCircleValue = "\u2714";
          $rootScope.secCircleValue = "2";
          $rootScope.thirdCircleValue = "3";
          $('#progressBar').css("width", "50%");
          $('#progressBar').css("background-color", "#7bbdf6");
          $('#secondRound').css("background-color", "#7bbdf6");
          $('#thirdRound').css("background-color", "#e1e1e1");

        } else if ($scope.secPanelScrollHt == 0) {
          $rootScope.applicationWindowTitle = "ADD APPLICATION - Type";
          $rootScope.prevButtonShow = false;
          $rootScope.nextButtonShow = true;
          $rootScope.saveButtonShow = false;
          $rootScope.firstCircleValue = "1";
          $rootScope.secCircleValue = "2";
          $rootScope.thirdCircleValue = "3";
          $('#progressBar').css("width", "0%");
          $('#progressBar').css("background-color", "#e1e1e1");
          $('#secondRound').css("background-color", "#e1e1e1");
          $('#thirdRound').css("background-color", "#e1e1e1");
        } else {
          $scope.secPanelScrollHt == 0;
        }
        modalApp.animate({ scrollTop: $scope.secPanelScrollHt }, 800);
      }

      $rootScope.scrollPanels = function(panelName, scrollHt) {
        var modalApp = $("#modalAppForm");
        modalApp.animate({ scrollTop: scrollHt }, 800);
      }

      /* $scope.l3SliderEnable = function(){            
                var checked=$("#extConnectivity").is(":checked");
                if(checked == false){                
                     document.getElementById("l3-slider").disabled = true;                
                }
                else{
                     document.getElementById("l3-slider").disabled = false;
                }
        }*/
      $scope.saveAppDialog = function() {
        $scope.addApp.uiData = {
          "x": 50,
          "y": 0
        };

        /*Need to change this following implementation*/
        var epgComplexityVal = (Math.round(parseFloat($scope.addApp.configuration.epgComplexity))).toString();
        var contractComplexityVal = (Math.round(parseFloat($scope.addApp.configuration.contractComplexity))).toString();
        var l3ComplexityVal = (Math.round(parseFloat($scope.addApp.configuration.l3outComplexity))).toString();

        /*if L3Out Checkbox is disabled, send L3OutComplexity as Medium- default*/
        var checked = $("#extConnectivity").is(":checked");
        if (checked == false) {
          l3ComplexityVal = "2";
        }

        if (epgComplexityVal == "1")
          $scope.addApp.configuration.epgComplexity = "Small";
        else if (epgComplexityVal == "2")
          $scope.addApp.configuration.epgComplexity = "Medium";
        else
          $scope.addApp.configuration.epgComplexity = "Large";


        if (l3ComplexityVal == "1")
          $scope.addApp.configuration.l3outComplexity = "Low";
        else if (l3ComplexityVal == "2")
          $scope.addApp.configuration.l3outComplexity = "Medium";
        else
          $scope.addApp.configuration.l3outComplexity = "High";

        if (contractComplexityVal == "1")
          $scope.addApp.configuration.contractComplexity = "Low";
        else if (contractComplexityVal == "2")
          $scope.addApp.configuration.contractComplexity = "Medium";
        else
          $scope.addApp.configuration.contractComplexity = "High";

        if ($scope.addApp.model == "Blank Application") {
          $scope.noTemplateApp = true;
        }

        /* Mapping the new App-Type Dropdown values- 2-Tier, 3-Tier, Blank Application to the old values - 2Tier,3Tier,NoTemplate
         * before sending the request to the backend*/

        var newAppTypeModel = $scope.addApp.model;
        if (newAppTypeModel == "2-Tier") {
          $scope.addApp.model = "2Tier";
        } else if (newAppTypeModel == "3-Tier") {
          $scope.addApp.model = "3Tier";
        } else if (newAppTypeModel == "Blank Application") {
          $scope.addApp.model = "NoTemplate";
        }

        $scope.addApplication(JSON.stringify($scope.addApp));
        $scope.repositionAddAppDialog();
        $rootScope.showModal = false;
        $('#modalAppForm').scrollTop(0);
        $scope.appTypeValid = false;
      }

      $scope.closeAppDialog = function() {
        $scope.repositionAddAppDialog();
        $rootScope.showModal = false;
        $('#modalAppForm').scrollTop(0);
        $scope.appTypeValid = false;
      }

      $scope.repositionAddAppDialog = function() {
        $("#modalAppForm").modal('hide');
        $("#addAppModal").css("top", "0");
        $("#addAppModal").css("bottom", "0");
        $("#addAppModal").css("left", "0");
        $("#addAppModal").css("right", "0");
        $("#PopupMask").remove();
      }

      $scope.changeSliderPos = function(divValue) {
        var divName = "#" + divValue;
        var sliderVal = $(divName)[0];
        var intValue = Math.round(sliderVal.value).toString();
        sliderVal.value = intValue;
      }

      $scope.currentZoomVal = 0.7;
      $scope.zoomInAppDiagram = function() {
        $scope.currentZoomVal += 0.1;
        $scope.appDiagram.zoom($scope.currentZoomVal);
      }

      $scope.zoomOutAppDiagram = function() {
        $scope.currentZoomVal -= 0.1;
        $scope.appDiagram.zoom($scope.currentZoomVal);
      }

      $scope.chooseSwitch = [];

      $scope.chooseSwitchesFromPhySizer = function() {
        $("#switchDropDown li").removeClass("activeRoom");
        logicService.getRooms(function(resp) {
          $scope.chooseSwitch['roomList'] = resp;
          var roomId = $rootScope.currProject.roomId;
          if (roomId == 0) {
            $scope.selectedRoomIndex = 0;
          } else {
            for (var i = 0; i < resp.length; ++i) {
              if (roomId == resp[i].id) {
                $scope.selectedRoomIndex = i + 1;
                break;
              }
            }
          }
        });
      }

      $scope.activeRoomItem = function(index, id) {
        $scope.selectedRoomIndex = index;
        if (id == null) {
          var jsonObj = {
            "usePhysical": false,
            "roomId": id
          }
        } else {
          var jsonObj = {
            "usePhysical": true,
            "roomId": id
          }
        }
        $rootScope.currProject.roomId = id;
        logicService.preferredSwitch(jsonObj, function(resp) {
          $rootScope.canceler.resolve();
          $scope.showLoadingImage();
          $('#switchDropDown').prop('disabled', true);
          logicService.getSizingResults(function(resp) {
            $rootScope.currProject = resp;
            $scope.removeLoadingImage();
            $('#switchDropDown').prop('disabled', false);
          });
        })

      
      }

      // Manohar ::  
      $rootScope.pushToApicStatus = 0;
      $scope.pushApicPopup = function() {
        if(!$rootScope.pushToApicStatus)
          $rootScope.pushToApicStatus = 0;
        popupService.showPushToApicPopup($rootScope.tenantList, $rootScope.apicDevices)
          .then(function(apicDevice){
            $rootScope.pushToApicStatus = 1;
            $rootScope.currentApicDevice = apicDevice;
            $scope.updateTenantList();
          }, function(){
            // $rootScope.pushToApicStatus = 2;
          });
      };


    })

  function getIndexByProp(objList, prop, value){
    for(var i=0; i<objList.length; i++){
      if( objList[i][prop] == value ){
        return i;
      }
    }
    return -1;
  }  

})();
