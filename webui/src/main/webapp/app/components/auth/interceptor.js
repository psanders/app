(function() {

    'use strict';

    var auth = angular.module('fnAuth');

    auth.config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('TokenInterceptor');
    }]);

    auth.factory('TokenInterceptor', function ($rootScope, $window, $q, $base64, CredentialsService) {
        return {
            request: function (config) {
                config.headers = config.headers || {};

                if (CredentialsService.isAuthenticated()) {
                    var c = CredentialsService.getCredentials();
                    var r = c.accountId.concat(":").concat(c.token);
                    config.headers.Authorization = "Basic ".concat($base64.encode(r))
                }

                return config;
            },
            /* Set Authentication.isAuthenticated to true if 200 received */
            response: function (response) {
                if (response != null && response.status == 200
                    && CredentialsService.isAuthenticated()) {
                    // Do nothing
                }
                return response || $q.when(response);
            },
            /* Revoke client authentication if 401 is received */
            responseError: function(rejection) {
                if (rejection != null && rejection.status === 401
                    && ($window.localStorage.token
                    || !CredentialsService.isAuthenticated())) {
                    //$window.location = 'auth';
                }
                return $q.reject(rejection);
            }
        };
    });

})();
