(function() {

    'use strict';

    var auth = angular.module('fnAuth');

    auth.directive('forgot', function(){
        return {
            restrict: 'E',
            templateUrl: 'app/components/auth/forgot/forgot.tpl.html'
        };
    });
})();
