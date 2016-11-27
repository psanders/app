(function() {
    'use strict';

    angular.module('fnNumbers');

    angular.module('fnNumbers').config(['$stateProvider', function($stateProvider) {
      $stateProvider.state('numbers', {
        url: '/numbers',
        templateUrl: 'app/components/numbers/numbers.tpl.html',
        controller: 'NumbersCtrl'
      });
    }]);

    angular.module('fnNumbers').controller('NumbersCtrl', ['$scope','$window', '$q', '$timeout', '$document', '$mdToast', '$filter', '$mdDialog', 'Numbers', 'Users',
        function($scope, $window, $q, $timeout, $document, $mdToast, $filter, $mdDialog, Numbers, User) {

        $scope.formatLocal = formatLocal;
        $scope.user = User.getUser();

        $scope.query = {
            order: '-modified',
            limit: 10,
            page: 1
        };

        $scope.numberRequest = function(evt) {
            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'app/components/numbers/number_request.tpl.html',
                parent: angular.element(document.body),
                targetEvent: evt,
                clickOutsideToClose: true,
                fullscreen: true
            })
            .then(function(answer) {
                $scope.status = 'You said the information was "' + answer + '".';
            }, function() {
                $scope.status = 'You cancelled the dialog.';
            });
        };

        function DialogController($scope, $mdDialog) {
          $scope.hide = function() {
            $mdDialog.hide();
          };
        }

        $scope.init = function() {
            Numbers.getResource().get().$promise
            .then(function(result){
                $scope.numbers = result;
            })
            .catch(function(error) {
                console.log(JSON.stringify(error));
            });
        }

        $scope.setPreferred = function(number) {
            Numbers.getPreferredResource().save(number).$promise
            .then(function(result) {
                toastMe('Your test number changed to ' + formatLocal($scope.user.countryCode, number.number), 4000);
                $scope.init();
            })
            .catch(function(error){
                toastMe(error.data.message);
            });
        }

        $scope.onPageChange = function(page, limit) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
        };

        $scope.onOrderChange = function(order) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
        };

        var toastMe = function(msg, hideDelay) {
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

        $scope.init();
    }]);



})();