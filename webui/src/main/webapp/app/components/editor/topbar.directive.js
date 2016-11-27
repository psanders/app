(function() {

    'use strict';

    angular.module('fnEditor').directive('topbar', function() {
        return {
            restrict: 'E',
            templateUrl: 'app/components/editor/topbar.tpl.html'
        };
    });
})();
