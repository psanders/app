(function() {

    'use strict';

    angular.module('fnCalls', ['ngResource']);

    angular.module('fnCalls')
    .service('Calls', function($resource, $rootScope, CredentialsService) {
        var accountId = CredentialsService.getCredentials().accountId;
        return $resource($rootScope.apiUrl + '/accounts/:accountId/calls/:callId?result=json', {accountId: accountId});
    });

})();
