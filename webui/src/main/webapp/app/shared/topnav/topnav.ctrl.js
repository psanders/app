(function() {
    'use strict';

    angular.module('fnTopnav').controller('TopnavCtrl', TopnavCtrl);

    TopnavCtrl.$inject =
        ['$interval',
        '$scope',
        '$state',
        '$location',
        '$timeout',
        '$mdSidenav',
        '$mdMedia',
        '$mdComponentRegistry',
        'CredentialsService',
        'Users',
        'Activities',
        'SidenavStatus'];

    function TopnavCtrl($interval, $scope, $state, $location, $timeout, $mdSidenav, $mdMedia, $mdComponentRegistry,
        CredentialsService, Users, Activities, SidenavStatus) {

        var self = this;

        self.navOpen = true;

        // On-load had small screen
        if ($mdMedia('xs')) {
            self.navOpen = false;
        }

        var uObserver = $interval(function(){
            if(Users.getUser()) {
                $scope.user = Users.getUser();
                $interval.cancel(uObserver);
            }
        }, 1);

        $scope.$watch(function() { return $mdMedia('xs'); }, function(small) {
            $scope.small = small;
        });

        $scope.$on('funds-added', function(event) {
            $scope.user = Users.getUser();
        });

        self.showActivities = function() {
            self.activities = [];

            Activities.query({maxResults: 6}).$promise
            .then(function(result) {
                self.activities = result;
            }).catch(function(error) {
                console.error(error);
            });
        }

        self.toggleSidebar = function () {
            SidenavStatus.toggleSidebar();
        }
    }

})();