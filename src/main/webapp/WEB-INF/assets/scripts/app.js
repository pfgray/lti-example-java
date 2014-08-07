var app = angular.module('ltiApp', ['ui.router']);

app.config(function($stateProvider, $urlRouterProvider) {
    //
    // For any unmatched url, redirect to /state1
    $urlRouterProvider.otherwise("/intro");
    //
    // Now set up the states
    $stateProvider
    .state('intro', {
        url: "/intro",
        templateUrl: "assets/templates/partial-intro.html"
    })
    .state('tools', {
        url: "/tools",
        templateUrl: "assets/templates/partial-tools.html"
    });

}).run(function($rootScope, $state) {
    $rootScope.$state = $state;
});