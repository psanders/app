(function() {

    'use strict';

    var app = angular.module('fnAuth');

    app.directive('forgot', function() {
        return {
            restrict: 'E',
            templateUrl: 'app/components/auth/forgot/forgot.tpl.html'
        };
    });
})();
