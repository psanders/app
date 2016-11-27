(function() {
    'use strict';

    angular.module('fnTopnav').directive('topnav', function(){
        return {
            restrict: 'E',
            templateUrl: 'app/shared/topnav/topnav.tpl.html'
        };
    });
})();