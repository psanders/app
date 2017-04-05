(function() {

    'use strict';

    var app = angular.module('fnNumbers', ['ngResource']);

    app.service('Numbers', ['$resource', '$rootScope', 'CredentialsService', function($resource, $rootScope, CredentialsService) {
        var accountId = CredentialsService.getCredentials().accountId;
        var self = this;

        self.getResource = function() {
            return $resource($rootScope.apiUrl + '/accounts/:accountId/numbers/:number?result=json', {accountId: accountId});
        }

        // Preferred number for testing
        self.getPreferredResource = function() {
            return $resource($rootScope.apiUrl + '/accounts/:accountId/numbers/preferred?result=json', {accountId: accountId, number: '@number'});
        }
    }]);

})();
