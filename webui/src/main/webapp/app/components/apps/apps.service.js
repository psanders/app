(function () {
    'use strict';

    angular.module('fnApps', ['ngResource']);

    angular.module('fnApps').service('Apps', AppsService);

    function AppsService($resource, $rootScope, CredentialsService) {
        return $resource($rootScope.apiUrl
            .concat('/accounts/:accountId/apps/:appId?result=json'),
                {accountId: CredentialsService.getCredentials().accountId});
    }
})();
