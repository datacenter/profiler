'use strict';

angular.module('profiler')
    .directive('staticInclude', function($http, $templateCache, $compile) {
        return function(scope, element, attrs) {
            var templatePath = attrs.staticInclude;
            $http.get(templatePath, {cache: $templateCache}).success(function(response) {
                var contents = $('<div/>').html(response).contents();
                element.html(contents);
                $compile(contents)(scope);
            });
        };
	})
	
	.directive('modal', function () {
    return {
      templateUrl: 'views/workFlowTemplate.html',
      restrict: 'E',
      transclude: true,
      replace:true,
      scope:true,
      link: function postLink(scope, element, attrs) {
        scope.title = attrs.title;

        console.log(element);

        scope.$watch(attrs.visible, function(value){
          if(value == true){
            $(element).modal('show');           
          }
          else
            $(element).modal('hide');
        });
      }
    };
    })
	
		
	.directive('customPopover', function ($compile,$templateCache) {
    return {
        restrict: 'A',
        template: '<span>{{label}}</span>',
        link: function (scope, el, attrs) {
            var popOverContent;

        popOverContent = $templateCache.get("templateId.html");  
		popOverContent = $compile("<div>" + popOverContent+"</div>")(scope);
        scope.label = attrs.popoverLabel;

            $(el).popover({
                trigger:'manual',
                html: true,
				container:'body',
                content: popOverContent,
                placement: attrs.popoverPlacement,
				title: attrs.popoverTitle + '<button type="button" class="close" aria-hidden="true">&times;</button>'	
            }).on('shown.bs.popover', function () {
				var popover = $(el).data('bs.popover');
				if (typeof popover !== "undefined") {
					var $tip = popover.tip();
					 $tip.find('.close').bind('click', function () {
							popover.hide();
					});
				}
			});
			$(el).on('click', function () {
				$(el).popover('show');
				
			});
			$('body').on('click', function (e) {
				$(el).each(function () {
					if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
						$(this).popover('hide');
					}
				});
			});
        }
    };
		
    })


.directive('tabs', function() {
    return {
      restrict: 'E',
      transclude: true,
      scope: {},
      controller: [ "$scope", function($scope) {
        var panes = $scope.panes = [];
 
        $scope.select = function(pane) {
          angular.forEach(panes, function(pane) {
            pane.selected = false;
          });
          pane.selected = true;
        }
 
        this.addPane = function(pane) {
          if (panes.length == 0) $scope.select(pane);
          panes.push(pane);
        }
      }],
      template:
        '<div class="tabbable">' +
		'<div class="tab-buttonbox">'+
          '<ul class="nav tabs-button">' +
            '<li class="tabs-button" ng-repeat="pane in panes" ng-class="{active:pane.selected}">'+
              '<a href="" ng-click="select(pane)">{{pane.title}}</a>' +
            '</li>' +
          '</ul>' +'</div>'+
		  '<div class="content-container">' +
          '<div class="tab-content"ng-transclude></div>' +
          '</div>' +
	     '</div>',
      replace: true
    };
  }).
  directive('pane', function() {
    return {
      require: '^tabs',
      restrict: 'E',
      transclude: true,
      scope: { title: '@' },
      link: function(scope, element, attrs, tabsCtrl) {
        tabsCtrl.addPane(scope);
      },
      template:
        '<div class="tab-pane" ng-class="{active: selected}" ng-transclude>' +
        '</div>',
      replace: true
    };
  });
 
