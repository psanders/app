(function() {

    'use strict';

    var app = angular.module('fnAuth', ['base64', 'ngResource']);

    app.service('CredentialsService', ['$window', function($window) {

        this.setCredentials = function(credentials) {
            $window.localStorage.credentials = JSON.stringify(credentials);
        };

        this.getCredentials = function() {
            if (!this.isAuthenticated()) return;
            return JSON.parse($window.localStorage.credentials);
        };

        this.destroyCredentials = function() {
            delete $window.localStorage.credentials;
        };

        this.isAuthenticated = function() {
            if ($window.localStorage.credentials && $window.localStorage.credentials != "{}") {
                return true;
            } else {
                return false;
            }
        };
    }]);

    app.service('LoginService', ['$resource', '$rootScope', function($resource, $rootScope) {
        var credentials = $resource($rootScope.apiUrl + '/users/credentials?result=json');

        this.getResource = function() {
          return credentials;
        }
    }]);

})();
