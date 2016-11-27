(function() {

    'use strict';

    var apps = angular.module('fnUsers');

    apps.directive('profile', function(){
       return {
           restrict: 'E',
           templateUrl: '/app/components/users/profile.tpl.html'
       };
    });

    // Todo: Move this to its own module
    apps.directive('account', function(){
       return {
           restrict: 'E',
           templateUrl: '/app/components/users/account.tpl.html'
       };
    });

    apps.directive('password', function(){
       return {
           restrict: 'E',
           templateUrl: '/app/components/users/password.tpl.html'
       };
    });

})();
