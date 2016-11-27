(function() {

    'use strict';

    angular.module('fnNumbers', ['ngResource']);

    angular.module('fnNumbers', ['ngResource']).service('Numbers', function($resource, $rootScope, CredentialsService) {
        var accountId = CredentialsService.getCredentials().accountId;

        this.getResource = function() {
            return $resource($rootScope.apiUrl + '/accounts/:accountId/numbers/:number?result=json', {accountId: accountId});
        }

        // Preferred number for testing
        this.getPreferredResource = function() {
            return $resource($rootScope.apiUrl + '/accounts/:accountId/numbers/preferred?result=json', {accountId: accountId, number: '@number'});
        }
    });

})();
