(function() {

    'use strict';

    var app = angular.module('fnNumbers', ['ngResource']);

    app.service('Numbers', ['$resource', '$rootScope', 'CredentialsService',
        function ($resource, $rootScope, CredentialsService) {
            return $resource($rootScope.apiUrl + '/accounts/:accountId/dids/:did?result=json', {
                accountId: CredentialsService.getCredentials().accountId
            });
        }
    ]);

})();
