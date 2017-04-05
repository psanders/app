(function() {
    'use strict';

    angular.module('fnDashboard')
        .config(['$stateProvider', config])
        .controller('DashboardCtrl', DashboardCtrl);

    DashboardCtrl.$inject = ['$window', '$timeout', '$interval', 'Analytics'];

    function DashboardCtrl($window, $timeout, $interval, Analytics) {
        var self = this;
        self.data = [];
        self.options = {
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
                    return moment(Number(self.data[d].x)).format("d MMM HH:mm");
                },
                transitionDuration: 300
            }
        };

        self.drawAnalytics = function(period) {
            self.period = period;
            Analytics.get({period: period}).$promise
            .then(function(result) {
                self.data = [];
                var v = result.stats;

                for(var y in v) {
                    var entry  = {};
                    entry.x = y;
                    entry.y = v[y].completedCalls;
                    self.data.push(entry);
                }
            }).catch(function(error) {
                console.error(error);
            });
        };

        // This is to ensure svg area is re-draw http://stackoverflow.com/questions/25555257/redrawing-svg-on-resize
        $timeout(function(){
            self.drawAnalytics("HOUR");
        }, 1);

        self.drawAnalytics("HOUR");
    }
    function config($stateProvider) {
        $stateProvider.state('dashboard', {
            url: '/dashboard',
            templateUrl: 'app/components/dashboard/dashboard.tpl.html',
            controller: 'DashboardCtrl'
        });
    }

})();