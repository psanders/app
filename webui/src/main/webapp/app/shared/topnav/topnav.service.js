(function() {

    'use strict';

    angular.module('fnTopnav', []);

    angular.module('fnTopnav').service('Activities', function($resource, $rootScope, Users, CredentialsService) {
        var accountId = CredentialsService.getCredentials().accountId;
        return $resource($rootScope.apiUrl + '/users/:email/activities?result=json', {
            email: Users.getUser().email, accountId: accountId
        });
    });
})();
