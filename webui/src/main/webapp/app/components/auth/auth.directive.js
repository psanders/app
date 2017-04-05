(function() {

    'use strict';

    var app = angular.module('fnAuth');

    app.directive('auth', function() {
        return {
            restrict: 'E',
            templateUrl: 'app/components/auth/auth.tpl.html'
        };
    });
})();
