(function() {

    'use strict';

    var app = angular.module('fnSidenav', []);

    app.service('SidenavStatus', ['$mdSidenav', function($mdSidenav) {
        this.toggleSidebar = function() {
            $mdSidenav('sidenav').toggle()
            .then(function () {
                //
            });
        };
    }]);
})();
