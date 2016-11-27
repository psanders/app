(function() {
    'use strict';

    angular.module('fnUsers');

    angular.module('fnUsers').controller('AccountCtrl', ['$scope', '$mdToast', '$mdDialog', '$document', 'CredentialsService', 'LoginService',
        function($scope, $mdToast, $mdDialog, $document, CredentialsService, LoginService) {

        var self = this;
        $scope.account = CredentialsService.getCredentials();

        // TODO: Compare user with Users.getUser() to avoid saving the object with no changes
        var regenerate = function(r) {
            LoginService.getResource().save(r).$promise
            .then(function(data) {
                console.log(JSON.stringify(data));
                $scope.account = data
                CredentialsService.setCredentials(data);
                toastMe("Regenerated!. Remember to update all your apps with the new token.", 8000);
            }).catch(function(error) {
                console.error(JSON.stringify(error));
                toastMe(error.data.message);
            });
        }

        $scope.login = function(evt) {
            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'app/components/users/password_dialog.tpl.html',
                parent: angular.element(document.body),
                targetEvent: evt,
                clickOutsideToClose:true
            })
            .then(function(request) {
                regenerate(request);
            }, function() {
                // Do nothing
            });
        };

        // This code is all over the place...
        var toastMe = function(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('top right')
                .parent($document[0].querySelector('#settings'))
                .content(msg)
                .hideDelay(hideDelay))
                .then(function() {
                    // Nothing to do
            });
        }
    }]);

    angular.module('fnUsers').controller('ProfileCtrl', ['$scope', '$mdToast', '$document', 'Users',
        function($scope, $mdToast, $document, Users) {
        var self = this;

        // Timezone from timezone.js
        self.gtzs = gtzs;
        // Countries from countries.js
        self.countries = countries;
        self.user = Users.getUser();

        // TODO: Compare user with Users.getUser() to avoid saving the object with no changes
        self.save = function(user) {
            Users.setUser(self.user);
            Users.getResource().save(self.user).$promise
            .then(function(data) {
                toastMe("Saved.");
            }).catch(function(error) {
                toastMe("Unable to save profile. Code #0003");
            });
        }

        // This code is all over the place...
        var toastMe = function(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('top right')
                .parent($document[0].querySelector('#settings'))
                .content(msg)
                .hideDelay(hideDelay))
            .then(function() {
                // Nothing to do
            });
        }
    }]);

    angular.module('fnUsers').controller('PasswordCtrl', ['$scope', '$mdToast', '$document', 'Users',
        function($scope, $mdToast, $document, Users) {

        $scope.request = angular.copy({password: "", confirmPassword: ""});

        $scope.update = function(request) {
            Users.getPasswordResource().save({email: Users.getUser().email, password: request.password}).$promise
            .then(function(data) {
                toastMe("Done.");
            }).catch(function(error) {
                console.error(JSON.stringify(error));
                toastMe("Unable to change password. Code #0005");
            });

            $scope.passwordForm.$setPristine();
            $scope.request = angular.copy({password: "", confirmPassword: ""});
        }

        // This code is all over the place...
        var toastMe = function(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('top right')
                .parent($document[0].querySelector('#settings'))
                .content(msg)
                .hideDelay(hideDelay))
            .then(function() {
                    // Nothing to do
            });
        }
    }]);

    function DialogController($scope, $mdDialog) {
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.regenerate = function(r) {
            $mdDialog.hide(r);
        };
    }
})();