(function() {
    'use strict';

    var navbar = angular.module('fnNav', []);

    navbar.directive('navbar', function(){
        return {
            restrict: 'E',
            templateUrl: 'app/shared/nav/navbar.tpl.html'
        };
    });

    navbar.directive('sidenav', function(){
        return {
            restrict: 'E',
            templateUrl: 'app/shared/nav/sidenav.tpl.html'
        };
    });
})();