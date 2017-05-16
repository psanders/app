(function() {
    'use strict';

    var app = angular.module('fnUsers');
    app.config(['$stateProvider', config]);
    app.controller('AccountCtrl', AccountCtrl);
    app.controller('ProfileCtrl', ProfileCtrl);
    app.controller('PasswordCtrl', PasswordCtrl);

    AccountCtrl.$inject =  ['$mdToast', '$mdDialog', '$document', 'CredentialsService', 'LoginService'];
    ProfileCtrl.$inject =  ['$mdToast', '$document', 'Users'];
    PasswordCtrl.$inject = ['$mdToast', '$document', '$scope', 'Users'];

    function AccountCtrl($mdToast, $mdDialog, $document, CredentialsService, LoginService) {
        var self = this;
        self.account = CredentialsService.getCredentials();

        self.login = function(evt) {
            console.log('DBG0001 ~> ' + $mdDialog);

            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'app/components/users/password_dialog.tpl.html',
                parent: angular.element(document.body),
                targetEvent: evt,
                clickOutsideToClose: true
            })
            .then(function(request) {
                regenerate(request);
            }, function() {
                // Do nothing
            });
        };

        function toastMe(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('bottom left')
                .parent($document[0].querySelector('#settings'))
                .content(msg)
                .hideDelay(hideDelay))
                .then(function() {
                    // Nothing to do
            });
        }

        function regenerate(r) {
            LoginService.getResource().save(r).$promise
            .then(function(data) {
                console.log(JSON.stringify(data));
                self.account = data
                CredentialsService.setCredentials(data);
                toastMe("Regenerated!. Remember to update all your apps with the new token.", 8000);
            }).catch(function(error) {
                console.error(JSON.stringify(error));
                toastMe(error.data.message);
            });
        }
    }

    function ProfileCtrl($mdToast, $document, Users) {
        var self = this;

        // Timezone from timezone.js
        self.gtzs = gtzs;
        // Countries from countries.js
        self.countries = countries;
        self.user = Users.getUser();

        self.save = function(user) {
            Users.setUser(self.user);
            Users.getResource().save(self.user).$promise
            .then(function(data) {
                toastMe("Saved.");
            }).catch(function(error) {
                toastMe("Unable to save profile. Code #0003");
            });
        }

        function toastMe(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('bottom left')
                .parent($document[0].querySelector('#settings'))
                .content(msg)
                .hideDelay(hideDelay))
            .then(function() {
                // Nothing to do
            });
        }
    }

    function PasswordCtrl($mdToast, $document, $scope, Users) {
        var self = this;

        self.request = angular.copy({password: "", confirmPassword: ""});

        self.update = function(request) {
            Users.getPasswordResource().save({email: Users.getUser().email, password: self.request.password}).$promise
            .then(function(data) {
                toastMe("Done.");
            }).catch(function(error) {
                console.error(JSON.stringify(error));
                toastMe("Unable to change password. Code #0005");
            });

            $scope.passwordForm.$setPristine();
            self.request = angular.copy({password: "", confirmPassword: ""});
        }

        // This code is all over the place...
        var toastMe = function(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('bottom left')
                .parent($document[0].querySelector('#settings'))
                .content(msg)
                .hideDelay(hideDelay))
            .then(function() {
                    // Nothing to do
            });
        }
    }

    function DialogController($scope, $mdDialog) {
        $scope.hide = function() {
            console.log('Hide what? :O ')
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            console.log('Cancel what? :O ')
            $mdDialog.cancel();
        };
        $scope.regenerate = function(r) {
            console.log('Regenerate what? :O ')
            $mdDialog.hide(r);
        };
    }
    
    function config($stateProvider) {
    }

})();