(function() {

    'use strict';

    var apps = angular.module('fnBilling');

    apps.directive('billing', function(){
       return {
           restrict: 'E',
           templateUrl: '/app/components/billing/billing.tpl.html'
       };
    });

})();
