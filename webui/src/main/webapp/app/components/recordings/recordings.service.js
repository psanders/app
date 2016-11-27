(function() {

    'use strict';

    angular.module('fnRecordings', ['ngResource']);

    angular.module('fnRecordings').service('Recordings', function($resource, $rootScope, CredentialsService) {
        var accountId = CredentialsService.getCredentials().accountId;
        return $resource($rootScope.apiUrl + '/accounts/:accountId/recordings/:recordingId?result=json', {accountId: accountId});
    });

})();
