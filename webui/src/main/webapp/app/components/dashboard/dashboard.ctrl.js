(function() {
    'use strict';

    angular.module('fnDashboard').config(['$stateProvider', function($stateProvider) {
      $stateProvider.state('dashboard', {
        url: '/dashboard',
        templateUrl: 'app/components/dashboard/dashboard.tpl.html',
        controller: 'DashboardCtrl'
      });
    }]);

    angular.module('fnDashboard').controller('DashboardCtrl', ['$scope', '$window', '$timeout', '$interval', 'Analytics',
        function($scope, $window, $timeout, $interval, Analytics) {

        $scope.data = [];
        $scope.options = {
            color: ["#E01B5D"],
            chart: {
                type: 'sparklinePlus',
                margin : {
                    top: 20,
                    right: 80,
                    bottom: 20,
                    left: 40
                },
                x: function(d, i){return i;},
                xTickFormat: function(d) {
                    return moment(Number($scope.data[d].x)).format("d MMM HH:mm");
                },
                transitionDuration: 300
            }
        };

        $scope.drawAnalytics = function(period) {
            $scope.period = period;
            Analytics.get({period: period}).$promise
            .then(function(result) {
                $scope.data = [];
                var v = result.stats;

                for(var y in v) {
                    var entry  = {};
                    entry.x = y;
                    entry.y = v[y].completedCalls;
                    $scope.data.push(entry);
                }
            }).catch(function(error) {
                console.error(error);
            });
        };

        // This is to ensure svg area is re-draw http://stackoverflow.com/questions/25555257/redrawing-svg-on-resize
        $timeout(function(){
            $scope.drawAnalytics("HOUR");
        }, 1);

        $scope.drawAnalytics("HOUR");
    }]);
})();