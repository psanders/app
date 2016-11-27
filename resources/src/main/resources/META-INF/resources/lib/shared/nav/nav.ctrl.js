(function() {
    'use strict';

    var navbar = angular.module('fnNav');

    navbar.controller('NavCtrl', ['$interval','$scope', '$location', '$timeout', '$mdSidenav', '$mdComponentRegistry', 'CredentialsService', 'Users',
        function($interval, $scope, $location, $timeout, $mdSidenav, $mdComponentRegistry, CredentialsService, Users) {

        // TODO: May not be the better solution...
        var uObserver = $interval(function(){
            if(Users.getUser()) {
                $scope.user = Users.getUser();
                $interval.cancel(uObserver);
            }
        }, 1000);

        $scope.$on('funds-added', function(event) {
            $scope.user = Users.getUser();
        });

        $scope.setSec = function(sec) {
            $location.url(sec);
        }

        $scope.currentSec = function(sec) {
            if("/" + sec == $location.path()) {
                return true;
            }
            return false;
        }

        $scope.isEditor = function() {
            if("/editor" == $location.path()) {
                return true;
            }
            return false;
        }


        $scope.isAuthenticated = function() {
            return  CredentialsService.isAuthenticated();
        }

        $scope.toggleSidebar = function () {
            $mdSidenav('sidenav').toggle()
            .then(function () {
                //console.log("close LEFT is done");
            });
        };

        //$mdComponentRegistry.when('left').then(function(it){
        //    $scope.sidenavOpen = function() {
        //        return $mdSidenav('sidenav').isLockedOpen();
        //    }
        //});
        $scope.sidenavOpen = function() {
            return $mdSidenav('sidenav').isLockedOpen();
        }

        function OpenInNewTab(url) {
            console.log(url);
            var win = window.open(url, '_blank');
            win.focus();
        }

        $scope.OpenInNewTab = OpenInNewTab;
    }]);

})();