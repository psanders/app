(function() {
    'use strict';

    var app = angular.module('fnCalls');

    app.config(['$stateProvider', function($stateProvider) {
      $stateProvider.state('calls', {
        url: '/calls',
        templateUrl: 'app/components/calls/calls.tpl.html',
        controller: 'CallsCtrl'
      });
    }]);

    app.controller('CallsCtrl', ['$scope','$window', '$q', '$timeout', 'Calls',
        function($scope, $window, $q, $timeout, Calls) {

        $scope.formatLocal = formatLocal;
        $scope.startDate = new Date();
        $scope.endDate = new Date();

        $scope.query = {
            order: '-modified',
            limit: 10,
            page: 1
        };

        $scope.updateView = function() {
            if(!$scope.startDate || !$scope.endDate) return;

            var cRequest = {start: moment($scope.startDate).format("YYYY-MM-DD"), end: moment($scope.endDate).format("YYYY-MM-DD")};

            Calls.get(cRequest).$promise
            .then(function(result) {
                $scope.calls = result;
            })
            .catch(function(error) {
                console.error(JSON.stringify(error));
            });

            $scope.filter = false;
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

        $scope.updateView();
    }]);

})();