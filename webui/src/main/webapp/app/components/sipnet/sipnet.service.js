(function() {

    'use strict';

    var app = angular.module('fnSIPNet', ['ngResource']);

    app.service('Agents', ['$resource', '$rootScope', 'CredentialsService',
        function($resource, $rootScope, CredentialsService) {
            var accountId = CredentialsService.getCredentials().accountId;
            return $resource($rootScope.apiUrl + '/accounts/:accountId/agents/:agentId?result=json', {accountId: accountId});
    }]);

    app.service('Domains', ['$resource', '$rootScope', 'CredentialsService',
        function($resource, $rootScope, CredentialsService) {
            var accountId = CredentialsService.getCredentials().accountId;
            return $resource($rootScope.apiUrl + '/accounts/:accountId/domains/:domainUri?result=json', {accountId: accountId});
    }]);

})();
