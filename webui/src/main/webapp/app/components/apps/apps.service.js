(function () {
    'use strict';

    var app = angular.module('fnApps', ['ngResource']);

    app.service('Apps', ['$resource', '$rootScope', 'CredentialsService',
        function ($resource, $rootScope, CredentialsService) {
            return $resource($rootScope.apiUrl.concat('/accounts/:accountId/apps/:appId?result=json'), {
                    accountId: CredentialsService.getCredentials().accountId
            });
        }
    ]);

})();
