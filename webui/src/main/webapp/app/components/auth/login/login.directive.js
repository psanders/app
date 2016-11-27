(function() {

    'use strict';

    var auth = angular.module('fnAuth');

    auth.directive('login', function(){
        return {
            restrict: 'E',
            templateUrl: 'app/components/auth/login/login.tpl.html'
        };
    });
})();
