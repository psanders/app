(function() {

    'use strict';

    angular.module('fnSidenav', []);

    angular.module('fnSidenav').service('SidenavStatus', function($mdSidenav) {
        this.toggleSidebar = function() {
            $mdSidenav('sidenav').toggle()
            .then(function () {
                //
            });
        };
    });
})();
