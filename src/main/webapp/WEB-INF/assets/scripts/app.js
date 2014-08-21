var app = angular.module('ltiApp', []);

app.controller('ExampleLtiAppController', ['$scope','$http', function($scope, $http){

    $scope.registration_request = window.tool_proxy_registration_request;
    $scope.tc_profile_token = window.tool_consumer_retrieval_token;

    $scope.loadingProfile = true;
    $http.get('profile_retrieval?token=' + $scope.tc_profile_token)
        .success(function(data){
            console.log('got profile:', data);
        })
        .error(function(data){
            console.error('error getting profile: ', data);
        });

}]);