(function() {

    'use strict';

    var dashboard = angular.module('fnDashboard', ['ngSanitize']);

    dashboard.service('Analytics', function($resource, $rootScope, CredentialsService) {
        var accountId = CredentialsService.getCredentials().accountId;
        return $resource($rootScope.apiUrl + '/accounts/:accountId/analytics/calls/:period?result=json', {
            accountId: accountId
        });
    });

})();
