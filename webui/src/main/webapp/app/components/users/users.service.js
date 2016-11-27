(function() {

    'use strict';

    var apps = angular.module('fnUsers', ['ngResource']);

    apps.service('Users', function($resource, $window, $rootScope, CredentialsService) {

        this.setUser = function(user) {
            $window.localStorage.user = JSON.stringify(user);
            moment.tz.setDefault(user.timezone);
        };

        this.getUser = function() {
            if (!$window.localStorage.user) return;
            return JSON.parse($window.localStorage.user);
        };

        this.destroyUser = function() {
            delete $window.localStorage.user;
        };

        this.getResource = function() {
            return $resource($rootScope.apiUrl + '/users/:email?result=json');
        }

        this.getPasswordResource = function() {
            return $resource($rootScope.apiUrl + '/users/:email/password?result=json', {
                email: "@email"
            });
        }
    });

})();
