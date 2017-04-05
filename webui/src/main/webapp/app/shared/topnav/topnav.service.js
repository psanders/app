(function() {

    'use strict';

    var app = angular.module('fnTopnav', []);

    app.service('Activities', ['$resource', '$rootScope', 'Users', 'CredentialsService',
        function($resource, $rootScope, Users, CredentialsService) {
            var accountId = CredentialsService.getCredentials().accountId;

            return $resource($rootScope.apiUrl + '/users/:email/activities?result=json', {
                email: Users.getUser().email, accountId: accountId
            });
    }]);
})();
