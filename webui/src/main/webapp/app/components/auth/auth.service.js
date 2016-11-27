(function() {

    'use strict';

    var auth = angular.module('fnAuth', ['base64', 'ngResource']);

    auth.service('CredentialsService', function($window) {

        this.setCredentials = function(credentials) {
            $window.localStorage.credentials = JSON.stringify(credentials);
        };

        this.getCredentials = function() {
            if (!$window.localStorage.credentials) return;
            return JSON.parse($window.localStorage.credentials);
        };

        this.destroyCredentials = function() {
            delete $window.localStorage.credentials;
        };

        this.isAuthenticated = function() {
            if ($window.localStorage.credentials) return true;
            return false;
        };
    });

    auth.service('LoginService', function($resource, $rootScope) {
        var credentials = $resource($rootScope.apiUrl + '/users/credentials?result=json');

        this.getResource = function() {
          return credentials;
        }
    });

})();
