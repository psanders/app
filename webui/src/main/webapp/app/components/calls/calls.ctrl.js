(function() {
    'use strict';

    angular.module('fnCalls')
        .config(['$stateProvider', config])
        .controller('CallsCtrl', CallsCtrl);

    CallsCtrl.$inject = ['$window', '$q', '$timeout', 'Calls'];

    function CallsCtrl($window, $q, $timeout, Calls) {
        var self = this;
        self.formatLocal = formatLocal;
        self.startDate = new Date();
        self.endDate = new Date();

        self.query = {
            order: '-modified',
            limit: 10,
            page: 1
        };

        self.updateView = function() {
            if(!self.startDate || !self.endDate) return;

            var cRequest = {start: moment(self.startDate).format("YYYY-MM-DD"), end: moment(self.endDate).format("YYYY-MM-DD")};

            Calls.get(cRequest).$promise
            .then(function(result) {
                self.calls = result;
            })
            .catch(function(error) {
                console.error(JSON.stringify(error));
            });

            self.filter = false;
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

        self.updateView();
    }

    function config($stateProvider) {
        $stateProvider.state('calls', {
            url: '/calls',
            templateUrl: 'app/components/calls/calls.tpl.html',
            controller: 'CallsCtrl'
        });
    }

})();