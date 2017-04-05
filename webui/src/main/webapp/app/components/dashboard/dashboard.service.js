(function() {

    'use strict';

    var app = angular.module('fnDashboard', ['ngSanitize']);

    app .service('Analytics', ['$resource', '$rootScope', 'CredentialsService', function($resource, $rootScope, CredentialsService) {
        var accountId = CredentialsService.getCredentials().accountId;
        return $resource($rootScope.apiUrl + '/accounts/:accountId/analytics/calls/:period?result=json', {
            accountId: accountId
        });
    }]);

})();
