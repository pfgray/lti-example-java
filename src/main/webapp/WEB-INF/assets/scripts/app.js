var app = angular.module('ltiApp', []);

app.controller('ExampleLtiAppController', ['$scope','$http', 'ConfigService', function($scope, $http, configService){

    $scope.registration_request = window.tool_proxy_registration_request;
    $scope.tc_profile_token = window.tool_consumer_retrieval_token;

    $scope.loadingProfile = true;
    $http.get('profile_retrieval?token=' + $scope.tc_profile_token)
    .success(function(data){
        console.log('got profile:', data);
        $scope.loadingProfile = false;
        $scope.tc_profile = data;
    })
    .error(function(data){
        $scope.loadingProfile = false;
        console.error('error getting profile: ', data);
    });

    $scope.sendTool = function(){
        var toolProxyInfo = {};
        //initiate the proxy registration request
        if($scope.tc_profile.service_offered){
            for(var i=0; i<$scope.tc_profile.service_offered.length; i++){
                for(var j=0; j<$scope.tc_profile.service_offered[i].format.length; j++){
                    if($scope.tc_profile.service_offered[i].format[j] === 'application/vnd.ims.lti.v2.toolproxy+json'){
                        toolProxyInfo = $scope.tc_profile.service_offered[i];
                    }
                }
            }
        }
        $scope.creatingProxy = true;
        $http.post('toolRegistration?token=' + $scope.tc_profile_token, toolProxyInfo)
        .success(function(data){
            $scope.creatingProxy = false;
        })
        .error(function(data){
            $scope.creatingProxy = false;
            console.error('error creating proxy: ', data);
        });

        console.log("endpoint:", toolProxyInfo);
    }
    configService.getConfig()
    .success(function(data){
        console.log('got config:', data);
        $scope.config = data;
    });
    $scope.saveConfig = function(config){
        configService.updateConfig(config)
        .success(function(data){
            console.log('updated config:', data);
            $scope.config = data;
        });
    }
}]);

app.service('ConfigService', ['$http', function($http){
    var ConfigService = {};
    ConfigService.getConfig = function(){
        return $http.get('config');
    }
    ConfigService.updateConfig = function(config){
        return $http.post('config', config);
    }
    return ConfigService;
}]);
