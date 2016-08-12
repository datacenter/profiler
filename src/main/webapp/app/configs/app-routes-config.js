(function(){
  'use strict';

  angular
    .module('profiler')
    .config(routeConfig);
  
  //routes configuration
  function routeConfig($stateProvider, $locationProvider, $urlRouterProvider) {

    $stateProvider
      .state('login', {
        url: '/',
        templateUrl: 'app/views/login/login.html',
        controller: 'loginController as loginCtrl'
      })
      .state('home', {
        url: '/home',
        templateUrl: 'app/views/home/home.html',
        controller: 'homeController as homeCtrl'
      })
      .state('home.projects', {
        url: '/projects',
        parent : 'home',
        templateUrl: 'app/views/home/projects/projects.html',
        controller: 'projectsController as projCtrl'
      })
      .state('home.projectdetails', {
        url: '/projectdetails',
        parent : 'home',
        templateUrl: 'app/views/home/projects/details/project-details.html',
        controller: 'projectDetailsController as projDetailsCtrl'
      })
      .state('home.plugins', {
        url: '/plugins',
        parent : 'home',
        views : {
          '' : {
            templateUrl: 'app/views/home/plugins/plugins.html',
            controller: 'pluginsController as pluginsCtrl'
          },
          'leftpanel@home.plugins' : {
            templateUrl: 'app/views/home/plugins/plugins-left-panel.html',
            controller: 'pluginsLeftPanelController as pluginsLPCtrl'
          },
          'rightpanel@home.plugins' : {
            templateUrl: 'app/views/home/plugins/plugins-right-panel.html',
            controller: 'pluginsRightPanelController as pluginsRPCtrl'
          }
        }        
      })
      .state('home.repositories', {
        url: '/repositories',
        parent : 'home',
        views : {
          '' : {
            templateUrl: 'app/views/home/repositories/repositories.html',
            controller: 'repositoriesController as repoCtrl'
          },
          'leftpanel@home.repositories' : {
            templateUrl: 'app/views/home/repositories/repository-left-panel.html',
            controller: 'repositoryLeftPanelController as repoLPCtrl'
          },
          'rightpanel@home.repositories' : {
            templateUrl: 'app/views/home/repositories/repository-right-panel.html',
            controller: 'repositoryRightPanelController as repoRPCtrl'
          }
        }        
      })
      .state('home.users', {
        url: '/users',
        parent : 'home',
        templateUrl: 'app/views/home/users/users.html',
        controller: 'usersController as usersCtrl'
      });

    $urlRouterProvider.otherwise('/');

    //set pretty url or enabling html5 mode
    // $locationProvider.html5Mode({
    //   enabled: true,
    //   requireBase: false
    // });

  }

})();