(function() {

    'use strict';

    var app = angular.module('fnCalls', ['ngResource']);

    app.service('Calls', ['$resource', '$rootScope', 'CredentialsService',
        function($resource, $rootScope, CredentialsService) {
            var accountId = CredentialsService.getCredentials().accountId;
                return $resource($rootScope.apiUrl + '/accounts/:accountId/calls/:callId?result=json', {accountId: accountId});
            }
    ]);}

)();
