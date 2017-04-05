(function() {
    'use strict';

    angular.module('fnNumbers')
        .config(['$stateProvider', config])
        .controller('NumbersCtrl', NumbersCtrl);

    NumbersCtrl.$inject = [
        '$window', 
        '$q', 
        '$timeout', 
        '$document', 
        '$mdToast', 
        '$filter', 
        '$mdDialog', 
        'Numbers', 
        'Users'];

    function NumbersCtrl($window, $q, $timeout, $document, $mdToast, $filter, $mdDialog, Numbers, User) {
        var self = this;
        self.formatLocal = formatLocal;
        self.user = User.getUser();

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

        self.setPreferred = function(number) {
            Numbers.getPreferredResource().save(number).$promise
            .then(function(result) {
                toastMe('Your test number changed to ' + formatLocal(self.user.countryCode, number.number), 4000);
                init();
            })
            .catch(function(error){
                toastMe(error.data.message);
            });
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
                self.numbers = result;
            })
            .catch(function(error) {
                console.log(JSON.stringify(error));
            });
        }

        function DialogController(self, $mdDialog) {
            self.hide = function() {
                $mdDialog.hide();
            };
        }

        function toastMe(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('top right')
                .parent($document[0].querySelector('#numbers'))
                .content(msg)
                .hideDelay(hideDelay))
            .then(function() {
                    // Nothing to do
            });
        }

        init();
    }

    function config($stateProvider) {
        $stateProvider.state('numbers', {
            url: '/numbers',
            templateUrl: 'app/components/numbers/numbers.tpl.html',
            controller: 'NumbersCtrl'
        });
    }

})();