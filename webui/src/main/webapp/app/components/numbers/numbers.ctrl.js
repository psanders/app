import * as moment from 'moment-timezone';

(function() {
    'use strict';

    angular.module('fnNumbers').config(['$stateProvider', config]);
    angular.module('fnNumbers').controller('NumbersCtrl', NumbersCtrl);
    angular.module('fnNumbers').controller('DialogController', DialogController);

    DialogController.$inject = ['$scope', '$mdDialog'];
    NumbersCtrl.$inject = ['$window',
        '$q', 
        '$timeout', 
        '$document', 
        '$mdToast', 
        '$filter', 
        '$mdDialog', 
        'Numbers', 
        'Users',
        'Apps'];

    function NumbersCtrl($window, $q, $timeout, $document, $mdToast, $filter, $mdDialog, Numbers, Users, Apps) {
        var self = this;
        self.editView = false;
        self.user = Users.getUser();

        function formatLocal(code, number) {
            return number;
        }

        self.formatLocal = formatLocal;

        // Sets proper timezone for 'asCalendar' filter
        moment.tz.setDefault(Users.getUser().timezone)

        self.query = {
            order: '-modified',
            limit: 10,
            page: 1
        };

        self.numberRequest = function(evt) {
            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'app/components/numbers/number_request.tpl.html',
                parent: angular.element(document.body),
                targetEvent: evt,
                clickOutsideToClose: true,
                fullscreen: true
            })
            .then(function(answer) {
                self.status = 'You said the information was "' + answer + '".';
            }, function() {
                self.status = 'You cancelled the dialog.';
            });
        };

        self.save = function(number) {
            Numbers.getResource().save(number).$promise
            .then(function(result) {
                toastMe('Done!');
                init();
            })
            .catch(function(error) {
                toastMe(error.data.message);
            });
        }

        self.openEditView = function(number) {
            self.number = number;
            self.editView = true;
        }

        self.closeEditView = function() {
            self.editView = false;
            init();
        }

        self.onPageChange = function(page, limit) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
        };

        self.onOrderChange = function(order) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
        };

        function init() {
            Numbers.getResource().get().$promise
            .then(function(result){
                self.didNumbers = result;
            })
            .catch(function(error) {
                console.error(JSON.stringify(error));
            });

            Apps.get().$promise
            .then(function(result) {
                self.apps = result;
            })
            .catch(function(error) {
                console.log(JSON.stringify(error));
            });
        }

        function toastMe(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('bottom right')
                .parent($document[0].querySelector('#numbers'))
                .content(msg)
                .hideDelay(hideDelay))
            .then(function() {
                    // Nothing to do
            });
        }

        init();
    }

    function DialogController($scope, $mdDialog) {
        $scope.hide = function() {
            $mdDialog.hide();
        };
    }

    function config($stateProvider) {
        $stateProvider.state('numbers', {
            url: '/numbers',
            templateUrl: 'app/components/numbers/numbers.tpl.html',
            controller: 'NumbersCtrl'
        });
    }
})();