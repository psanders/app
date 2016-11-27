(function() {

    'use strict';

    var auth = angular.module('fnAuth');

    auth.directive('auth', function(){
        return {
            restrict: 'E',
            templateUrl: 'app/components/auth/auth.tpl.html'
        };
    });
})();
