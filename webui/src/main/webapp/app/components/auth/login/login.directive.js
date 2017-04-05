(function() {

    'use strict';

    var app = angular.module('fnAuth');

    app.directive('login', function() {
        return {
            restrict: 'E',
            templateUrl: 'app/components/auth/login/login.tpl.html'
        };
    });
})();
