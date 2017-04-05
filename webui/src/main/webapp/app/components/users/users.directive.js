(function() {

    'use strict';

    var app = angular.module('fnUsers');

    app.directive('profile', function() {
       return {
           restrict: 'E',
           templateUrl: '/app/components/users/profile.tpl.html'
       };
    });

    app.directive('account', function() {
       return {
           restrict: 'E',
           templateUrl: '/app/components/users/account.tpl.html'
       };
    });

    app.directive('password', function() {
       return {
           restrict: 'E',
           templateUrl: '/app/components/users/password.tpl.html'
       };
    });

})();
