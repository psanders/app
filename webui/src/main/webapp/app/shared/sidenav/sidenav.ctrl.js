(function() {
    'use strict';

    angular.module('fnSidenav').controller('SidenavCtrl', SidenavCtrl);

    SidenavCtrl.$inject =  ['$mdSidenav', 'SidenavStatus', '$state', '$scope', '$mdMedia'];

    function SidenavCtrl($mdSidenav, SidenavStatus, $state, $scope, $mdMedia) {
        var self = this;
        self.navOpen = true;

        // On-load had small screen
        if ($mdMedia('xs')) {
            self.navOpen = false;
        }

        self.toggleSidebar = function () {
            SidenavStatus.toggleSidebar();
        }

        self.go = function (sec) {
            $state.go(sec)
        }

        self.currentSec = function(sec) {
            return $state.current.name == sec;
        }

        self.isSidenavLockedOpen = function() {
            return SidenavStatus.isLockedOpen();
        }
    }
})();