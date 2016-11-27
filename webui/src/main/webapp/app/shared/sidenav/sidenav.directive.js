(function() {
    'use strict';

    angular.module('fnSidenav').directive('sidenav', function() {
        return {
            restrict: 'E',
            templateUrl: 'app/shared/sidenav/sidenav.tpl.html'
        };
    });
})();