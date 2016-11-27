(function() {

    'use strict';

    angular.module('fnAuth').config(['$stateProvider', function($stateProvider) {
      $stateProvider.state('login', {
        url: '/login',
        templateUrl: 'app/components/auth/auth.tpl.html',
        controller: 'AuthCtrl'
      });
    }]);

    angular.module('fnAuth').controller('AuthCtrl', ['$http', '$location', '$state', '$base64', '$window', '$mdToast', '$document',
        'LoginService', 'CredentialsService', 'Users',
        function($http, $location, $state, $base64, $window, $mdToast, $document, LoginService, CredentialsService, Users) {

        var self = this;
        var view = 'login';
        var code = $location.search().code;
        self.originalUser = angular.copy({email: '', password: '', firstName: '', lastName: ''});
        self.user = {};

        self.login = function() {
            $http.defaults.headers.common.Authorization = 'Basic ' +
            $base64.encode(self.user.email + ':' + self.user.password);

            LoginService.getResource().get(self.user).$promise
            .then(function(result){
                // So put it on the session
                CredentialsService.setCredentials(result);

                Users.getResource().get({email: self.user.email}).$promise
                .then(function(user){
                    Users.setUser(user);

                    $location.url('/dashboard');
                    self.reset();
                })
                .catch(function(error){
                    toastMe(error.data.message);
                });

                self.reset();
            }).catch(function(error) {
                if (error === undefined || error.data === undefined || error.data.message === undefined) {
                    toastMe("Unable to login. Code #0008");
                } else {
                    toastMe(error.data.message);
                }
            });
        }

        self.hasCode = function() {
            if(code === undefined) return false;
            return true;
        }

        self.logout = function() {
            CredentialsService.destroyCredentials();
            Users.destroyUser();
            $state.go('login');
        }

        self.isAuthenticated = function() {
            return CredentialsService.isAuthenticated();
        }

        self.reset = function() {
            self.loginForm === undefined || self.loginForm.$setUntouched();
            self.signupForm === undefined || self.signupForm.$setUntouched();
            self.forgotForm === undefined || self.forgotForm.$setUntouched();
            self.user = angular.copy(self.originalUser);
        };

        self.recover = function() {
            Users.getPasswordResource().save({email: self.user.email, password: ''}).$promise
            .then(function(result){
                console.debug(JSON.stringify(result));
            }).catch(function(error) {
               console.error(JSON.stringify(error));
            }).finally(function() {
                toastMe("Check your email to complete recovery.");
                self.chgView('login');
            })
        }

        self.signup = function() {
            var c = $base64.encode(self.user.email);

            if (self.hasCode() && c !== code) {
                toastMe("Ups! Something bad happen :(", 15000);
                return;
            }

            Users.getResource().save(self.user).$promise
            .then(function(result) {
                loginNow(self.user.email, self.user.password);
            }).catch(function(error) {
                if (error.data === undefined) {
                    toastMe("Unable to create account. Code #0009");
                } else {
                    toastMe(error.data.message);
                }
            });
        }

        self.chgView = function(v) {
            self.reset();
            view = v;
        }

        self.isView = function(v) {
            if (view == v) return true;
            return false;
        }

        init();

        function init() {
            if ($location.search().playground !== undefined) {
                loginNow("john@doe.com", "osamayor");
            }

            if (CredentialsService.isAuthenticated() && $location.path() == "/login") {
               $location.url('apps');
            }

            if(self.hasCode()) {
                self.user.email = $base64.decode(code);
                view = 'signup';
            }
        }

        function parseLocation(location) {
            var pairs = location.substring(1).split("&");
            var obj = {};
            var pair;
            var i;

            for ( i in pairs ) {
              if ( pairs[i] === "" ) continue;

              pair = pairs[i].split("=");
              obj[ decodeURIComponent( pair[0] ) ] = decodeURIComponent( pair[1] );
            }

            return obj;
        };

        function loginNow(email, password) {
            if (CredentialsService.isAuthenticated()) CredentialsService.destroyCredentials();
            self.user.email = email;
            self.user.password = password;
            self.login();
        }

        function toastMe(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                    .position('top right')
                    .content(msg)
                    .hideDelay(hideDelay))
                .then(function() {
                    // Nothing to do
            });
        }

    }]);

})();
