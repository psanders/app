(function() {

    'use strict';

    var auth = angular.module('fnAuth');

    auth.directive('signup', function(){
        return {
            restrict: 'E',
            templateUrl: 'app/components/auth/signup/signup.tpl.html'
        };
    });
})();
