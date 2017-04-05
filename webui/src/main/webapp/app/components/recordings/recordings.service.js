(function() {

    'use strict';

    var app = angular.module('fnRecordings', ['ngResource']);

    app.service('Recordings', ['$resource', '$rootScope', 'CredentialsService',
        function($resource, $rootScope, CredentialsService) {
            var accountId = CredentialsService.getCredentials().accountId;
            return $resource($rootScope.apiUrl + '/accounts/:accountId/recordings/:recordingId?result=json', {accountId: accountId});
    }]);

})();
